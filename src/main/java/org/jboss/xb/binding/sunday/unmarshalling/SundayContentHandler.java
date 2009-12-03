/*
  * JBoss, Home of Professional Open Source
  * Copyright 2005, JBoss Inc., and individual contributors as indicated
  * by the @authors tag. See the copyright.txt in the distribution for a
  * full listing of individual contributors.
  *
  * This is free software; you can redistribute it and/or modify it
  * under the terms of the GNU Lesser General Public License as
  * published by the Free Software Foundation; either version 2.1 of
  * the License, or (at your option) any later version.
  *
  * This software is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  * Lesser General Public License for more details.
  *
  * You should have received a copy of the GNU Lesser General Public
  * License along with this software; if not, write to the Free
  * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */
package org.jboss.xb.binding.sunday.unmarshalling;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;
import org.apache.xerces.xs.XSTypeDefinition;
import org.jboss.logging.Logger;
import org.jboss.util.StringPropertyReplacer;
import org.jboss.xb.binding.AttributesImpl;
import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.NamespaceRegistry;
import org.jboss.xb.binding.Util;
import org.jboss.xb.binding.introspection.FieldInfo;
import org.jboss.xb.binding.metadata.CharactersMetaData;
import org.jboss.xb.binding.metadata.PropertyMetaData;
import org.jboss.xb.binding.metadata.ValueMetaData;
import org.jboss.xb.binding.parser.JBossXBParser;
import org.jboss.xb.binding.resolver.MutableSchemaResolver;
import org.jboss.xb.binding.sunday.unmarshalling.position.ElementPosition;
import org.jboss.xb.binding.sunday.unmarshalling.position.Position;
import org.jboss.xb.binding.sunday.xop.XOPIncludeHandler;
import org.xml.sax.Attributes;

/**
 * Default ContentHandler
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class SundayContentHandler
   implements JBossXBParser.DtdAwareContentHandler
{
   final static Logger log = Logger.getLogger(SundayContentHandler.class);

   private final static Object NIL = new Object();

   private final SchemaBinding schema;
   private final SchemaBindingResolver schemaResolver;

   private final StackImpl stack = new StackImpl();

   private Object root;
   private NamespaceRegistry nsRegistry = new NamespaceRegistry();

   private UnmarshallingContextImpl ctx = new UnmarshallingContextImpl();
   // DTD information frm startDTD
   private String dtdRootName;
   private String dtdPublicId;
   private String dtdSystemId;
   private boolean sawDTD;

   private final boolean trace = log.isTraceEnabled();

   public SundayContentHandler(SchemaBinding schema)
   {
      this.schema = schema;
      this.schemaResolver = null;
   }

   public SundayContentHandler(SchemaBindingResolver schemaResolver)
   {
      this.schemaResolver = schemaResolver;
      this.schema = null;
   }

   
   public void startDTD(String dtdRootName, String dtdPublicId, String dtdSystemId)
   {
      this.dtdRootName = dtdRootName;
      this.dtdPublicId = dtdPublicId;
      this.dtdSystemId = dtdSystemId;
   }
   public void endDTD()
   {
      this.sawDTD = true;
   }

   public void characters(char[] ch, int start, int length)
   {
      Position position = stack.peek();
      if(!position.isElement())
         return;
      
      // if current is ended the characters belong to its parent
      if(position.isEnded())
      {
         position = stack.peek1();
         if(!position.isElement())
         {
            for(int i = stack.size() - 3; i >= 0; --i)
            {
               position = stack.peek(i);
               if(position.isElement())
                  break;
            }
         }
      }

      position.characters(ch, start, length);
   }

   public void endElement(String namespaceURI, String localName, String qName)
   {
      ElementBinding elementBinding = null;
      while(elementBinding == null && !stack.isEmpty())
      {
         Position position = stack.peek();
         if(position.isElement())
         {
            if(position.isEnded())
            {
               if(position.getParticle().isRepeatable())
               {
                  Position parentPosition = stack.peek1();
                  if(parentPosition.getRepeatableParticleValue() != null)
                     endRepeatableParticle(parentPosition, position.getQName(), position.getParticle(), parentPosition.getParticle());
               }
               stack.pop();
            }
            else
            {
               elementBinding = (ElementBinding)position.getParticle().getTerm();
               position.setEnded(true);
            }
         }
         else
         {
            if(!position.isEnded()) // could be ended if it's a choice
               endParticle(position);

            if(position.getParticle().isRepeatable())
            {
               Position parentPosition = stack.peek1();
               if(parentPosition.getRepeatableParticleValue() != null)
                  endRepeatableParticle(parentPosition, position.getQName(), position.getParticle(), parentPosition.getParticle());
            }
            stack.pop();
         }
      }

      if(elementBinding == null)
         throw new JBossXBRuntimeException("Failed to endElement " + qName + ": binding not found");

      QName endName = localName.length() == 0 ? new QName(qName) : new QName(namespaceURI, localName);
      if(!elementBinding.getQName().equals(endName))
      {
         throw new JBossXBRuntimeException("Failed to end element " +
            new QName(namespaceURI, localName) +
            ": element on the stack is " + elementBinding.getQName()
         );
      }

      endElement();
   }

   public void startElement(String namespaceURI,
                            String localName,
                            String qName,
                            Attributes atts,
                            XSTypeDefinition xercesType)
   {
      QName startName = localName.length() == 0 ? new QName(qName) : new QName(namespaceURI, localName);
      boolean repeated = false;
      Position position = null;
      SchemaBinding schemaBinding = schema;

      atts = preprocessAttributes(atts);
      
      if(stack.isEmpty())
      {
         ParticleBinding particle = null;
         if(schemaBinding != null)
            particle = schemaBinding.getElementParticle(startName);
         
         if(particle == null && schemaResolver != null)
         {
            String schemaLocation = atts == null ? null : Util.getSchemaLocation(atts, namespaceURI);
            // Use the dtd info if it exists and there is no schemaLocation
            if(schemaLocation == null || schemaLocation.length() == 0)
            {
               if(sawDTD)
                  schemaLocation = dtdSystemId;
               // If there is still no schemaLocation and no namespaceURI, pass in the root local name
               // if the namespace is not null then schemaLocation should be left null and resolved by EntityResolver
               if(schemaLocation == null && (namespaceURI == null || namespaceURI.length() == 0))
                  schemaLocation = localName;
            }
            
            schemaBinding = schemaResolver.resolve(namespaceURI, null, schemaLocation);
            if(schemaBinding != null)
               particle = schemaBinding.getElementParticle(startName);
            else
               throw new JBossXBRuntimeException("Failed to resolve schema nsURI=" + namespaceURI + " location=" + schemaLocation);
         }

         if(particle == null)
         {
            StringBuffer sb = new StringBuffer();
            sb.append("Failed to resolve global element ");
            sb.append(startName);
            if(schemaBinding == null && schemaResolver == null)
               sb.append(". Neither SchemaBinding nor SchemaBindingResolver are available.");
            throw new JBossXBRuntimeException(sb.toString());
         }
         
         position = new ElementPosition(startName, particle);
      }
      else
      {
         while(!stack.isEmpty())
         {
            position = stack.peek();
            if(position.isElement())
            {
               TermBinding term = position.getParticle().getTerm();
               ElementBinding element = (ElementBinding)term;
               if(position.isEnded())
               {
                  if(element.getQName().equals(startName))
                  {
                     if(position.getParticle().isRepeatable())
                     {
                        Position parentPosition = stack.peek1();
                        if(parentPosition.repeatTerm(startName, atts))
                        {
                           position.reset();
                           repeated = true;
                        }
                        else
                        {
                           stack.pop();
                           if(parentPosition.getRepeatableParticleValue() != null)
                              endRepeatableParticle(parentPosition, position.getQName(), position.getParticle(), parentPosition.getParticle());
                           continue;
                        }
                     }
                     else
                     {
                        position.reset();
                        repeated = true;
                        endRepeatableParent(startName);
                     }
                  }
                  else
                  {
                     if(position.getParticle().isRepeatable())
                     {
                        Position parentPosition = stack.peek1();
                        if(parentPosition.getRepeatableParticleValue() != null)
                           endRepeatableParticle(parentPosition, position.getQName(), position.getParticle(), parentPosition.getParticle());
                     }
                     stack.pop();
                     continue;
                  }
               }
               else
               {
                  TypeBinding parentType = element.getType();
                  ParticleBinding typeParticle = parentType.getParticle();
                  ModelGroupBinding modelGroup = typeParticle == null ? null : (ModelGroupBinding)typeParticle.getTerm();
                  if(modelGroup == null)
                  {
                     if(startName.equals(Constants.QNAME_XOP_INCLUDE))
                     {
                        TypeBinding anyUriType = schema.getType(Constants.QNAME_ANYURI);
                        if(anyUriType == null)
                           log.warn("Type " + Constants.QNAME_ANYURI + " not bound.");
                        
                        ElementBinding parentElement = (ElementBinding) position.getParticle().getTerm();
                        parentElement.setXopUnmarshaller(schema.getXopUnmarshaller());

                        ElementPosition ep = (ElementPosition) position;
                        ep.flushIgnorableCharacters();
                        ep.setHandler(DefaultHandlers.XOP_HANDLER);
                        ep.setIgnoreCharacters(true);
                        position.startParticle(position.getValue(), null, nsRegistry);
                        
                        TypeBinding xopIncludeType = new TypeBinding(new QName(Constants.NS_XOP_INCLUDE, "Include"));
                        xopIncludeType.setSchemaBinding(schema);
                        xopIncludeType.addAttribute(new QName("href"), anyUriType, DefaultHandlers.ATTRIBUTE_HANDLER);
                        xopIncludeType.setHandler(new XOPIncludeHandler(parentType, schema.getXopUnmarshaller()));

                        ElementBinding xopInclude = new ElementBinding(schema, Constants.QNAME_XOP_INCLUDE, xopIncludeType);
                        position = new ElementPosition(startName, new ParticleBinding(xopInclude));
                        break;
                     }

                     QName typeName = parentType.getQName();
                     throw new JBossXBRuntimeException((typeName == null ? "Anonymous" : typeName.toString()) +
                        " type of element " +
                        element.getQName() +
                        " should be complex and contain " + startName + " as a child element."
                     );
                  }

                  Position newPosition = modelGroup.newPosition(startName, atts, typeParticle);
                  if(newPosition == null)
                  {
                     throw new JBossXBRuntimeException(startName +
                        " not found as a child of " +
                        ((ElementBinding)term).getQName() + " in " + modelGroup
                     );
                  }
                  else
                  {
                     position.flushIgnorableCharacters();

                     Object o = position.getValue();
                     while(newPosition.getNext() != null)
                     {
                        if(newPosition.getParticle().isRepeatable())
                           startRepeatableParticle(stack.peek(), o, startName, newPosition.getParticle());

                        o = newPosition.startParticle(o, atts, nsRegistry);
                        newPosition.setParentType(parentType);
                        stack.push(newPosition);                        
                        newPosition = newPosition.getNext();
                     }

                     position = newPosition;
                     position.setParentType(parentType);
                     if(!position.isElement())
                        throw new IllegalStateException();
                  }                  
               }
               break;
            }
            else
            {
               ParticleBinding prevParticle = position.getCurrentParticle();
               Position newPosition = position.startElement(startName, atts);               
               if(newPosition == null)
               {
                  if(!position.isEnded())
                     endParticle(position);
                                    
                  stack.pop();
                  if(!position.getParticle().isRepeatable() && stack.peek().isElement())
                  {
                     TermBinding t = position.getParticle().getTerm();
                     StringBuffer sb = new StringBuffer(250);
                     sb.append(startName).append(" cannot appear in this position. Expected content of ")
                     .append(((ElementBinding)stack.peek().getParticle().getTerm()).getQName())
                     .append(" is ").append(t);
                     throw new JBossXBRuntimeException(sb.toString());
                  }
               }
               else
               {
                  ParticleBinding curParticle = position.getCurrentParticle();
                  if(curParticle != prevParticle)
                  {
                     if(position.getRepeatableParticleValue() != null &&
                           prevParticle != null && prevParticle.isRepeatable() && prevParticle.getTerm().isModelGroup())
                     {
                        endRepeatableParticle(position, position.getQName(), prevParticle, position.getParticle());
                     }

                     if(newPosition.getNext() != null && curParticle.isRepeatable() && !curParticle.getTerm().isElement())
                     {
                        startRepeatableParticle(position, position.getValue(), startName, curParticle);
                     }
                  }

                  // push all except the last one
                  Object o = position.getValue();
                  newPosition = newPosition.getNext();
                  while (newPosition.getNext() != null)
                  {
                     o = newPosition.startParticle(o, atts, nsRegistry);
                     newPosition.setParentType(position.getParentType());
                     stack.push(newPosition);
                     newPosition = newPosition.getNext();
                  }

                  newPosition.setParentType(position.getParentType());
                  position = newPosition;
                  if(!position.isElement())
                     throw new IllegalStateException();

                  break;
               }
            }
         }
      }

      ElementBinding element = (ElementBinding) position.getParticle().getTerm();

      // TODO xsi:type support should be implemented in a better way
      String xsiType = atts.getValue(Constants.NS_XML_SCHEMA_INSTANCE, "type");
      if (xsiType != null)
      {
         if (trace)
            log.trace(element.getQName() + " uses xsi:type " + xsiType);

         ElementPosition ep = (ElementPosition) position;
         if (ep != null && ep.getNonXsiParticle() == null)
            ep.setNonXsiParticle(position.getParticle());

         String xsiTypePrefix;
         String xsiTypeLocal;
         int colon = xsiType.indexOf(':');
         if (colon == -1)
         {
            xsiTypePrefix = "";
            xsiTypeLocal = xsiType;
         }
         else
         {
            xsiTypePrefix = xsiType.substring(0, colon);
            xsiTypeLocal = xsiType.substring(colon + 1);
         }

         String xsiTypeNs = nsRegistry.getNamespaceURI(xsiTypePrefix);
         QName xsiTypeQName = new QName(xsiTypeNs, xsiTypeLocal);

         TypeBinding xsiTypeBinding = schemaBinding.getType(xsiTypeQName);
         if (xsiTypeBinding == null)
         {
            throw new JBossXBRuntimeException("Type binding not found for type " + xsiTypeQName
                  + " specified with xsi:type for element " + startName);
         }

         ElementBinding xsiElement = new ElementBinding(schemaBinding, startName, xsiTypeBinding);
         xsiElement.setRepeatableHandler(element.getRepeatableHandler());

         position.setParticle(
               new ParticleBinding(xsiElement, position.getParticle().getMinOccurs(),
                     position.getParticle().getMaxOccurs(), position.getParticle().getMaxOccursUnbounded()));
      }

      Object parent = stack.isEmpty() ? null : (repeated ? stack.peek1().getValue() : stack.peek().getValue());
      if (!repeated && position.getParticle().isRepeatable())
         startRepeatableParticle(stack.peek(), parent, startName, position.getParticle());

      TypeBinding type = element.getType();
      if (type == null)
         throw new JBossXBRuntimeException("No type for element " + element);

      ParticleHandler handler = type.getHandler();
      if (handler == null)
         handler = DefaultHandlers.ELEMENT_HANDLER;
      position.setHandler(handler);

      List<ElementInterceptor> localInterceptors = position.getParentType() == null
            ? Collections.EMPTY_LIST
            : position.getParentType().getInterceptors(startName);
      List<ElementInterceptor> interceptors = element.getInterceptors();
      if (interceptors.size() + localInterceptors.size() > 0)
      {
         if (repeated)
            stack.pop();

         for (int i = 0; i < localInterceptors.size(); ++i)
         {
            ElementInterceptor interceptor = localInterceptors.get(i);
            parent = interceptor.startElement(parent, startName, type);
            push(startName, position.getParticle(), parent, position.getHandler(), position.getParentType());
            interceptor.attributes(parent, startName, type, atts, nsRegistry);
         }

         for (int i = 0; i < interceptors.size(); ++i)
         {
            ElementInterceptor interceptor = interceptors.get(i);
            parent = interceptor.startElement(parent, startName, type);
            push(startName, position.getParticle(), parent, position.getHandler(), position.getParentType());
            interceptor.attributes(parent, startName, type, atts, nsRegistry);
         }

         if (repeated)
         {
            // to have correct endRepeatableParticle calls
            stack.push(position);
         }
      }

      String nil = atts.getValue(Constants.NS_XML_SCHEMA_INSTANCE, "nil");
      if (nil == null || !("1".equals(nil) || "true".equals(nil)))
         position.startParticle(parent, atts, nsRegistry);
      else
         position.setValue(NIL);

      if (!repeated)
         stack.push(position);
   }

   private void endRepeatableParent(QName startName)
   {
      int stackIndex = stack.size() - 2;
      Position position;
      Position parentPosition = stack.peek1();
      while(true)
      {
         if(parentPosition.isElement())
         {
            throw new JBossXBRuntimeException(
               "Failed to start " + startName +
               ": the element is not repeatable, repeatable parent expected to be a model group but got element " +
               ((ElementBinding)parentPosition.getParticle().getTerm()).getQName()
            );
         }

         position = parentPosition;
         if(position.getParticle().isRepeatable())
         {
            endParticle(position, stackIndex - 1);
            parentPosition = stack.peek(stackIndex - 1);
            position.reset();
            position.startParticle(parentPosition.getValue(), null, nsRegistry);
            break;
         }

         parentPosition = stack.peek(--stackIndex);
         endParticle(position, stackIndex);
      }

/*      if(!parentParticle.isRepeatable())
      {
         StringBuffer msg = new StringBuffer();

         item = stack.peek();
         ParticleBinding currentParticle = item.particle;
         msg.append("Failed to start ").append(startName).append(": ")
            .append(currentParticle.getTerm())
            .append(" is not repeatable.")
            .append(" Its parent ")
            .append(parentParticle.getTerm())
            .append(" expected to be repeatable!")
            .append("\ncurrent stack: ");

         for(int i = 0; i < stack.size() - 1; ++i)
         {
            item = stack.peek(i);
            ParticleBinding particle = item.particle;
            TermBinding term = particle.getTerm();
            if(term.isModelGroup())
            {
               if(term instanceof SequenceBinding)
               {
                  msg.append("sequence");
               }
               else if(term instanceof ChoiceBinding)
               {
                  msg.append("choice");
               }
               else
               {
                  msg.append("all");
               }
            }
            else if(term.isWildcard())
            {
               msg.append("wildcard");
            }
            else
            {
               msg.append(((ElementBinding)term).getQName());
            }
            msg.append("\\");
         }

         throw new JBossXBRuntimeException(msg.toString());
      }
*/
      while(++stackIndex < stack.size() - 1)
      {
         parentPosition = position;
         position = stack.peek(stackIndex);
         position.reset();
         position.startParticle(parentPosition.getValue(), null, nsRegistry);
      }
   }

   private void startRepeatableParticle(Position parentPosition, Object parent, QName startName, ParticleBinding particle)
   {
      if(trace)
         log.trace(" start repeatable (" + stack.size() + "): " + particle.getTerm());

      RepeatableParticleHandler repeatableHandler = particle.getTerm().getRepeatableHandler();
      // the way it is now it's never null
      Object repeatableContainer = repeatableHandler.startRepeatableParticle(parent, startName, particle);
      if(repeatableContainer != null)
      {
         if(parentPosition.getRepeatableParticleValue() != null)
            throw new IllegalStateException("Previous repeatable particle hasn't been ended yet!");
         parentPosition.setRepeatableParticleValue(repeatableContainer);
         parentPosition.setRepeatableHandler(repeatableHandler);
      }
   }

   private void endRepeatableParticle(Position parentPosition, QName elementName, ParticleBinding particle, ParticleBinding parentParticle)
   {
      if (trace)
         log.trace(" end repeatable (" + stack.size() + "): " + particle.getTerm());
      RepeatableParticleHandler repeatableHandler = parentPosition.getRepeatableHandler();
      // the way it is now it's never null
      repeatableHandler.endRepeatableParticle(parentPosition.getValue(), parentPosition.getRepeatableParticleValue(), elementName, particle, parentParticle);
      parentPosition.setRepeatableParticleValue(null);
      parentPosition.setRepeatableHandler(null);
   }

   private void endParticle(Position position)
   {
      if(position.isEnded())
         throw new JBossXBRuntimeException(position.getParticle().getTerm() + " has already been ended.");

      position.endParticle();

      // model group should always have parent particle
      Position parentPosition = stack.peek1();
      if(parentPosition.getValue() != null)
         setParent(position.getHandler(), parentPosition, position);
   }

   private void endParticle(Position position, int parentIdex)
   {
      if(position.isEnded())
         throw new JBossXBRuntimeException(position.getParticle().getTerm() + " has already been ended.");

      position.endParticle();

      // model group should always have parent particle
      Position parentPosition = getNotSkippedParent(parentIdex);
      if(parentPosition.getValue() != null)
         setParent(position.getHandler(), parentPosition, position);
   }

   public void startPrefixMapping(String prefix, String uri)
   {
      nsRegistry.addPrefixMapping(prefix, uri);
   }

   public void endPrefixMapping(String prefix)
   {
      nsRegistry.removePrefixMapping(prefix);
   }

   public void processingInstruction(String target, String data)
   {
   }

   public Object getRoot()
   {
      return root;
   }

   // Private

   private Attributes preprocessAttributes(Attributes attrs)
   {
      SchemaBindingResolver resolver = schemaResolver == null ? schema.getSchemaResolver() : schemaResolver;
      if(resolver == null || !(resolver instanceof MutableSchemaResolver))
         return attrs;
      
      int ind = attrs.getIndex(Constants.NS_JBXB, "schemabinding");
      if (ind != -1)
      {
         MutableSchemaResolver defaultResolver = (MutableSchemaResolver)resolver;
         String value = attrs.getValue(ind);
         java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(value);
         while(tokenizer.hasMoreTokens())
         {
            String uri = tokenizer.nextToken();
            if(!tokenizer.hasMoreTokens())
               throw new JBossXBRuntimeException("jbxb:schemabinding attribute value is invalid: ns uri '" + uri + "' is missing value in '" + value + "'");
            String cls = tokenizer.nextToken();
            try
            {
               defaultResolver.mapURIToClass(uri, cls);
            }
            catch (Exception e)
            {
               throw new JBossXBRuntimeException("Failed to addClassBinding: uri='" + uri + "', class='" + cls + "'", e);
            }
         }
         
         AttributesImpl attrsImpl = new AttributesImpl(attrs.getLength() - 1);
         for(int i = 0; i < attrs.getLength(); ++i)
         {
            if(i != ind)
               attrsImpl.add(attrs.getURI(i), attrs.getLocalName(i), attrs.getQName(i), attrs.getType(i), attrs.getValue(i));
         }
         attrs = attrsImpl;
      }
      return attrs;
   }

   private Position getNotSkippedParent()
   {
      Position position = stack.peek1();
      if(position == null)
         return null;
      
      ParticleBinding particle = position.getParticle();
      if(!particle.getTerm().isSkip() || position.getRepeatableParticleValue() != null)
         return position;
      
      Position wildcardPosition = null;
      if(particle.getTerm().isWildcard())
         wildcardPosition = position;

      for(int i = stack.size() - 3; i >= 0; --i)
      {
         position = stack.peek(i);
         particle = position.getParticle();
         if(!particle.getTerm().isSkip() || position.getRepeatableParticleValue() != null)
            return position;
         else if(wildcardPosition != null)
            return wildcardPosition;

         if(particle.getTerm().isWildcard())
            wildcardPosition = position;
      }
      return wildcardPosition;
   }

   private Position getNotSkippedParent(int i)
   {
      Position position = null;
      while(i >= 0)
      {
         position = stack.peek(i--);
         ParticleBinding particle = position.getParticle();
         if(!particle.getTerm().isSkip() || position.getRepeatableParticleValue() != null)
            return position;
      }
      return null;
   }

   private void endElement()
   {
      ElementPosition position = (ElementPosition) stack.peek();
      
      ElementBinding element = (ElementBinding)position.getParticle().getTerm();
      QName endName = element.getQName();
      TypeBinding type = element.getType();
      List<ElementInterceptor> interceptors = element.getInterceptors();
      List<ElementInterceptor> localInterceptors = position.getParentType() == null ? Collections.EMPTY_LIST : position.getParentType().getInterceptors(endName);
      int allInterceptors = interceptors.size() + localInterceptors.size();

      if(position.getValue() != NIL)
      {
         //
         // characters
         //

         position.flushIgnorableCharacters();

         TypeBinding charType = type.getSimpleType();
         if(charType == null)
            charType = type;

         CharactersHandler charHandler = position.isIgnoreCharacters() ? null : charType.getCharactersHandler();

         /**
          * If there is text content then unmarshal it and set.
          * If there is no text content and the type is simple and
          * its characters handler is not null then unmarshal and set.
          * If the type is complex and there is no text data then the unmarshalled value
          * of the empty text content is assumed to be null
          * (in case of simple types that's not always true and depends on nillable attribute).
          */
         String textContent = position.getTextContent() == null ? "" : position.getTextContent().toString();
         if(textContent.length() > 0 || charHandler != null && !type.isIgnoreEmptyString())
         {
            String dataContent;
            SchemaBinding schema = element.getSchema();
            if(textContent.length() == 0)
            {
               dataContent = null;
            }
            else
            {
               dataContent = textContent.toString();
               if(schema != null && schema.isReplacePropertyRefs())
                  dataContent = StringPropertyReplacer.replaceProperties(dataContent);
               
               if(element.isNormalizeSpace())
                  dataContent = dataContent.trim();
            }

            Object unmarshalled;

            if(charHandler == null)
            {
               if(!type.isSimple() &&
                  schema != null &&
                  schema.isStrictSchema()
                  // todo this isSkip() doesn't look nice here
                  && !element.isSkip())
               {
                  throw new JBossXBRuntimeException("Element " +
                     endName +
                     " with type binding " +
                     type.getQName() +
                     " does not include text content binding: " + dataContent
                  );
               }
               unmarshalled = dataContent;
            }
            else
            {
               ValueMetaData valueMetaData = element.getValueMetaData();
               if(valueMetaData == null)
               {
                  CharactersMetaData charactersMetaData = type.getCharactersMetaData();
                  if(charactersMetaData != null)
                  {
                     valueMetaData = charactersMetaData.getValue();
                  }
               }

               // todo valueMetaData is available from type
               unmarshalled = dataContent == null ?
                  charHandler.unmarshalEmpty(endName, charType, nsRegistry, valueMetaData) :
                  charHandler.unmarshal(endName, charType, nsRegistry, valueMetaData, dataContent);
            }

            if(unmarshalled != null)
            {
               // if startElement returned null, we use characters as the object for this element
               if(position.getValue() == null)
               {
                  position.setValue(unmarshalled);
               }
               else if(charHandler != null)
               {
                  TermBeforeSetParentCallback beforeSetParent = charType.getBeforeSetParentCallback();
                  if(beforeSetParent != null)
                  {
                     ctx.parent = position.getValue();
                     ctx.particle = position.getParticle();
                     ctx.parentParticle = getNotSkippedParent().getParticle();
                     unmarshalled = beforeSetParent.beforeSetParent(unmarshalled, ctx);
                     ctx.clear();
                  }
                  
                  charHandler.setValue(endName, element, position.getValue(), unmarshalled);
               }
            }

            if(allInterceptors > 0)
            {
               int interceptorIndex = stack.size() - 1 - allInterceptors;
               for (int i = interceptors.size() - 1; i >= 0; --i)
               {
                  ElementInterceptor interceptor = interceptors.get(i);
                  interceptor.characters(stack.peek(interceptorIndex++).getValue(), endName, type, nsRegistry, dataContent);
               }

               for (int i = localInterceptors.size() - 1; i >= 0; --i)
               {
                  ElementInterceptor interceptor = localInterceptors.get(i);
                  interceptor.characters(stack.peek(interceptorIndex++).getValue(), endName, type, nsRegistry, dataContent);
               }
            }
         }
      }
      else
      {
         position.setValue(null);
      }

      //
      // endElement
      //

      position.endParticle();

      if(!interceptors.isEmpty())
      {
         int interceptorIndex = stack.size() - 1 - interceptors.size();
         for (int i = interceptors.size() - 1; i >= 0; --i)
         {
            ElementInterceptor interceptor = interceptors.get(i);
            interceptor.endElement(stack.peek(interceptorIndex++).getValue(), endName, type);
         }
      }
      
      //
      // setParent
      //

      if(allInterceptors == 0)
      {
         Position notSkippedParent = getNotSkippedParent();
         if (notSkippedParent != null)
         {
            ParticleBinding parentParticle = notSkippedParent.getParticle();
            TermBinding parentTerm = parentParticle.getTerm();

            if (notSkippedParent.getValue() != null)
            {
               ParticleHandler handler = position.getHandler();
               if (parentTerm.isWildcard())
               {
                  ParticleHandler wh = ((WildcardBinding) parentTerm).getWildcardHandler();
                  if (wh != null)
                     handler = wh;
               }
               setParent(handler, notSkippedParent, position);
            }
            else if (parentTerm.isWildcard() && stack.size() > 1)
            {
               // the parent has anyType, so it gets the value of its child
               for (int i = stack.size() - 2; i >= 0; --i)
               {
                  Position peeked = stack.peek(i);
                  peeked.setValue(position.getValue());
                  if (peeked.isElement())
                     break;
               }

               if (trace)
                  log.trace("Value of " + endName + " " + position.getValue() + " is promoted as the value of its parent element.");
            }
         }
      }
      else
      {
         Position popped = stack.pop();

         for(int i = interceptors.size() - 1; i >= 0; --i)
         {
            ElementInterceptor interceptor = interceptors.get(i);
            Object parent = stack.pop().getValue();
            interceptor.add(parent, position.getValue(), endName);
            position.setValue(parent);
         }

         for(int i = localInterceptors.size() - 1; i >= 0; --i)
         {
            ElementInterceptor interceptor = localInterceptors.get(i);
            Object parent = stack.pop().getValue();
            interceptor.add(parent, position.getValue(), endName);
            position.setValue(parent);
         }

         // need to push it back to have correct endRepeatableParticle events
         stack.push(popped);
      }

      if(stack.size() == 1)
      {
         root = type.getValueAdapter().cast(position.getValue(), Object.class);
         stack.clear();
         
         if(sawDTD)
         {
            // Probably should be integrated into schema binding?
            try
            {
               // setDTD(String root, String publicId, String systemId)
               Class[] sig = {String.class, String.class, String.class};
               Method setDTD = root.getClass().getMethod("setDTD", sig);
               Object[] args = {dtdRootName, dtdPublicId, dtdSystemId};
               setDTD.invoke(root, args);
            }
            catch(Exception e)
            {
               log.debug("No setDTD found on root: " + root);
            }
         }
      }
   }

   private void setParent(ParticleHandler handler, Position parentPosition, Position position)
   {
      if(parentPosition.getRepeatableParticleValue() == null)
      {
         TermBeforeSetParentCallback beforeSetParent = position.getParticle().getTerm().getBeforeSetParentCallback();
         if(beforeSetParent != null)
         {
            ctx.parent = parentPosition.getValue();
            ctx.particle = position.getParticle();
            ctx.parentParticle = getNotSkippedParent().getParticle();
            position.setValue(beforeSetParent.beforeSetParent(position.getValue(), ctx));
            ctx.clear();
         }
         
         handler.setParent(parentPosition.getValue(), position.getValue(), position.getQName(), position.getParticle(), parentPosition.getParticle());
      }
      else
         parentPosition.getRepeatableHandler().addTermValue(
               parentPosition.getRepeatableParticleValue(),
               position.getValue(), position.getQName(), position.getParticle(),
               parentPosition.getParticle(), handler);
   }

   private void push(QName qName, ParticleBinding particle, Object o, ParticleHandler handler, TypeBinding parentType)
   {
      ElementPosition position = new ElementPosition(qName, particle);
      position.setValue(o);
      position.setHandler(handler);
      position.setParentType(parentType);      
      stack.push(position);
      if(trace)
         log.trace("pushed[" + (stack.size() - 1) + "] " + particle.getTerm().getQName() + "=" + o);
   }

   // Inner

   static class StackImpl
   {
      private List<Position> list = new ArrayList<Position>();
      private Position head;
      private Position peek1;

      public void clear()
      {
         list.clear();
         head = null;
         peek1 = null;
      }

      public void push(Position o)
      {
         list.add(o);
         peek1 = head;
         head = o;
      }

      public Position pop()
      {
         head = peek1;
         int index = list.size() - 1;
         peek1 = index > 1 ? list.get(index - 2) : null;
         return list.remove(index);
      }

      public Position peek()
      {
         return head;
      }

      public Position peek1()
      {
         return peek1;
      }

      public Position peek(int i)
      {
         return list.get(i);
      }

      public boolean isEmpty()
      {
         return head == null;//list.isEmpty();
      }

      public int size()
      {
         return list.size();
      }
   }
   
   private class UnmarshallingContextImpl implements UnmarshallingContext
   {
      Object parent;
      ParticleBinding particle;
      ParticleBinding parentParticle;
      
      public Object getParentValue()
      {
         return parent;
      }
      
      public ParticleBinding getParticle()
      {
         return particle;
      }
      
      public ParticleBinding getParentParticle()
      {
         return parentParticle;
      }
      
      public String resolvePropertyName()
      {
         TermBinding term = particle.getTerm();
         PropertyMetaData propertyMetaData = term.getPropertyMetaData();
         String prop = propertyMetaData == null ? null : propertyMetaData.getName();
         
         if(prop != null)
         {
            return prop;
         }
         
         if(term.isElement())
         {
            QName name = ((ElementBinding)term).getQName();
            prop = Util.xmlNameToFieldName(name.getLocalPart(), term.getSchema().isIgnoreLowLine());
         }
         
         return prop;
      }

      public Class<?> resolvePropertyType()
      {
         if(parent == null)
         {
            return null;
         }
         
         String prop = resolvePropertyName();
         if(prop != null)
         {      
            FieldInfo fieldInfo = FieldInfo.getFieldInfo(parent.getClass(), prop, false);
            if (fieldInfo != null)
            {
               return fieldInfo.getType();
            }
         }
         return null;
      }
      
      // private
      
      void clear()
      {
         ctx.parent = null;
         ctx.particle = null;
         ctx.parentParticle = null;
      }
   }
}
