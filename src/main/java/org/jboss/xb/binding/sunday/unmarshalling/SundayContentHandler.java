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
   private final static Logger log = Logger.getLogger(SundayContentHandler.class);

   private final static Object NIL = new Object();

   private final SchemaBinding schema;
   private final SchemaBindingResolver schemaResolver;

   private final StackImpl stack = new StackImpl();

   private Object root;
   private NamespaceRegistry nsRegistry = new NamespaceRegistry();

   private ParticleHandler defParticleHandler = DefaultHandlers.ELEMENT_HANDLER;

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
      StackItem stackItem = stack.peek();
      if(stackItem.cursor != null)
      {
         return;
      }
      
      ElementBinding e = (ElementBinding) stackItem.particle.getTerm();
      // if current is ended the characters belong to its parent
      if(stackItem.ended)
      {
         stackItem = stack.peek1();
         if(stackItem.cursor != null)
         {
            for(int i = stack.size() - 3; i >= 0; --i)
            {
               stackItem = stack.peek(i);
               if(stackItem.cursor == null)
                  break;
            }
         }
         e = (ElementBinding) stackItem.particle.getTerm();
      }

      // collect characters only if they are allowed content
      if(e.getType().isTextContentAllowed())
      {
         if(stackItem.indentation != Boolean.FALSE)
         {
            if(e.getType().isSimple())
            {
               // simple content is not analyzed
               stackItem.indentation = Boolean.FALSE;
               stackItem.ignorableCharacters = false;
            }
            else if(e.getSchema() != null && !e.getSchema().isIgnoreWhitespacesInMixedContent())
            {
               stackItem.indentation = Boolean.FALSE;
               stackItem.ignorableCharacters = false;
            }
            else
            {
               // the indentation is currently defined as whitespaces with next line characters
               // this should probably be externalized in the form of a filter or something
               for (int i = start; i < start + length; ++i)
               {
                  if(ch[i] == 0x0a)
                  {
                     stackItem.indentation = Boolean.TRUE;
                  }
                  else if (!Character.isWhitespace(ch[i]))
                  {
                     stackItem.indentation = Boolean.FALSE;
                     stackItem.ignorableCharacters = false;
                     break;
                  }
               }
            }
         }
         
         if (stackItem.textContent == null)
         {
            stackItem.textContent = new StringBuffer();
         }
         stackItem.textContent.append(ch, start, length);
      }
   }

   public void endElement(String namespaceURI, String localName, String qName)
   {
      ElementBinding elementBinding = null;
      QName endName = localName.length() == 0 ? new QName(qName) : new QName(namespaceURI, localName);
      StackItem item;
      while(true)
      {
         item = stack.peek();
         if(item.cursor == null)
         {
            if(item.ended)
            {
               pop();
               if(item.particle.isRepeatable())
               {
                  StackItem parentItem = stack.peek();
                  if(parentItem.repeatableParticleValue != null)
                  {
                     endRepeatableParticle(parentItem.o, parentItem.repeatableParticleValue, item.qName, item.particle, parentItem.particle);
                     parentItem.repeatableParticleValue = null;
                  }
               }
            }
            else
            {
               elementBinding = (ElementBinding)item.particle.getTerm();
               item.ended = true;
               break;
            }
         }
         else
         {
            if(!item.ended) // could be ended if it's a choice
            {
               endParticle(item, stack.peek1());
            }

            ParticleBinding currentParticle = item.cursor.getCurrentParticle();
            TermBinding term = currentParticle.getTerm();
            if(item.repeatableParticleValue != null /* && currentParticle.isRepeatable()*/ && term.isWildcard())
            {
               StackItem parentItem = stack.peek1();
               endRepeatableParticle(parentItem.o, item.repeatableParticleValue, item.qName, currentParticle, parentItem.particle);
               item.repeatableParticleValue = null;
            }

            pop();
            if(item.particle.isRepeatable())
            {
               StackItem parentItem = stack.peek();
               if(parentItem.repeatableParticleValue != null)
               {
                  endRepeatableParticle(parentItem.o, parentItem.repeatableParticleValue, item.qName, item.particle, parentItem.particle);
                  parentItem.repeatableParticleValue = null;
               }
            }
         }
      }

      if(elementBinding == null)
      {
         throw new JBossXBRuntimeException("Failed to endElement " + qName + ": binding not found");
      }

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
      ParticleBinding particle = null;
      ParticleHandler handler = null;
      TypeBinding parentType = null;
      boolean repeated = false;
      boolean repeatedParticle = false;
      StackItem item = null;
      ModelGroupBinding.Cursor cursor = null; // used only when particle is a wildcard
      SchemaBinding schemaBinding = schema;

      atts = preprocessAttributes(atts);
      
      if(stack.isEmpty())
      {
         if(schemaBinding != null)
         {
            particle = schemaBinding.getElementParticle(startName);
         }
         else if(schemaResolver != null)
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
            {
               particle = schemaBinding.getElementParticle(startName);
            }
            else
            {
               throw new JBossXBRuntimeException("Failed to resolve schema nsURI=" + namespaceURI + " location=" + schemaLocation);
            }
         }
         else
         {
            throw new JBossXBRuntimeException("Neither schema binding nor schema binding resolver is available!");
         }
      }
      else
      {
         while(!stack.isEmpty())
         {
            item = stack.peek();
            if(item.cursor == null)
            {
               TermBinding term = item.particle.getTerm();
               ElementBinding element = (ElementBinding)term;
               if(item.ended)
               {
                  if(element.getQName().equals(startName))
                  {
                     if(item.particle.isRepeatable())
                     {
                        StackItem parentItem = stack.peek1();
                        if(parentItem.cursor.repeatElement(startName))
                        {
                           item.reset();
                           particle = item.particle;
                           parentType = item.parentType;
                           repeated = true;
                        }
                        else
                        {
                           pop();
                           if(parentItem.repeatableParticleValue != null)
                           {
                              endRepeatableParticle(parentItem.o, parentItem.repeatableParticleValue, item.qName, item.particle, parentItem.particle);
                              parentItem.repeatableParticleValue = null;
                           }
                           continue;
                        }
                     }
                     else
                     {
                        item.reset();
                        particle = item.particle;
                        parentType = item.parentType;
                        repeated = true;

                        endRepeatableParent(startName);
                     }
                  }
                  else
                  {
                     pop();                     
                     if(item.particle.isRepeatable())
                     {
                        StackItem parentItem = stack.peek();
                        if(parentItem.repeatableParticleValue != null)
                        {
                           endRepeatableParticle(parentItem.o, parentItem.repeatableParticleValue, item.qName, item.particle, parentItem.particle);
                           parentItem.repeatableParticleValue = null;
                        }
                     }
                     continue;
                  }
               }
               else
               {
                  parentType = element.getType();
                  ParticleBinding typeParticle = parentType.getParticle();
                  ModelGroupBinding modelGroup = typeParticle == null ? null : (ModelGroupBinding)typeParticle.getTerm();
                  if(modelGroup == null)
                  {
                     if(startName.equals(Constants.QNAME_XOP_INCLUDE))
                     {
                        TypeBinding anyUriType = schema.getType(Constants.QNAME_ANYURI);
                        if(anyUriType == null)
                        {
                           log.warn("Type " + Constants.QNAME_ANYURI + " not bound.");
                        }

                        TypeBinding xopIncludeType = new TypeBinding(new QName(Constants.NS_XOP_INCLUDE, "Include"));
                        xopIncludeType.setSchemaBinding(schema);
                        xopIncludeType.addAttribute(new QName("href"), anyUriType, DefaultHandlers.ATTRIBUTE_HANDLER);
                        xopIncludeType.setHandler(new XOPIncludeHandler(parentType, schema.getXopUnmarshaller()));

                        ElementBinding xopInclude = new ElementBinding(schema, Constants.QNAME_XOP_INCLUDE, xopIncludeType);

                        particle = new ParticleBinding(xopInclude);
                        
                        ElementBinding parentElement = (ElementBinding) item.particle.getTerm();
                        parentElement.setXopUnmarshaller(schema.getXopUnmarshaller());

                        flushIgnorableCharacters();
                        item.handler = DefaultHandlers.XOP_HANDLER;
                        item.ignoreCharacters = true;
                        item.o = item.handler.startParticle(stack.peek().o, startName, stack.peek().particle, null, nsRegistry);
                        break;
                     }

                     QName typeName = parentType.getQName();
                     throw new JBossXBRuntimeException((typeName == null ? "Anonymous" : typeName.toString()) +
                        " type of element " +
                        element.getQName() +
                        " should be complex and contain " + startName + " as a child element."
                     );
                  }

                  cursor = modelGroup.newCursor(typeParticle);
                  List<ModelGroupBinding.Cursor> newCursors = cursor.startElement(startName, atts);
                  if(newCursors.isEmpty())
                  {
                     throw new JBossXBRuntimeException(startName +
                        " not found as a child of " +
                        ((ElementBinding)term).getQName() + " in " + modelGroup
                     );
                  }
                  else
                  {
                     flushIgnorableCharacters();

                     Object o = item.o;
                     // push all except the last one
                     for(int i = newCursors.size() - 1; i >= 0; --i)
                     {
                        cursor = newCursors.get(i);

                        ParticleBinding modelGroupParticle = cursor.getParticle();
                        if(modelGroupParticle.isRepeatable())
                        {
                           stack.peek().repeatableParticleValue = startRepeatableParticle(o, startName, modelGroupParticle);
                        }

                        handler = getHandler(modelGroupParticle);
                        o = handler.startParticle(o, startName, modelGroupParticle, atts, nsRegistry);
                        push(startName, cursor, o, handler, parentType);
                     }
                     particle = cursor.getCurrentParticle();
                  }
               }
               break;
            }
            else
            {
               cursor = item.cursor;
               if(cursor == null)
               {
                  throw new JBossXBRuntimeException("No cursor for " + startName);
               }

               //int prevOccurence = cursor.getOccurence();
               ParticleBinding prevParticle = cursor.isPositioned() ? cursor.getCurrentParticle() : null;
               List<ModelGroupBinding.Cursor> newCursors = cursor.startElement(startName, atts);
               if(newCursors.isEmpty())
               {
                  if(!item.ended)
                     endParticle(item, stack.peek1());
                                    
                  StackItem poped = pop();
                  if(!poped.particle.isRepeatable() && stack.peek().cursor == null)
                  {
                     // normally it should be an error
                     // but there is an issue with wildcard binding: it is never bound as repeatable (but should be sometimes)
                     // so, this hack will give another iteration over the whole parent type and make it seem like it's repeatable
                     TermBinding popedTerm = poped.particle.getTerm();
                     if(popedTerm instanceof SequenceBinding &&
                           ((ModelGroupBinding)popedTerm).getParticles().size() == 1 &&
                           ((ModelGroupBinding)popedTerm).getParticles().iterator().next().getTerm().isWildcard())
                     {
                        // hack
                     }
                     else
                     {
                        TermBinding t = cursor.getParticle().getTerm();
                        StringBuffer sb = new StringBuffer(250);
                        sb.append(startName).append(" cannot appear in this position. Expected content of ")
                        .append(((ElementBinding)stack.peek().particle.getTerm()).getQName())
                        .append(" is ").append(t);
                        throw new JBossXBRuntimeException(sb.toString());
                     }
                  }
               }
               else
               {
                  if(item.ended) // for repeatable choices
                  {
                     if(!item.particle.isRepeatable())
                     {
                        throw new JBossXBRuntimeException("The particle expected to be repeatable but it's not: " + item.particle.getTerm());
                     }
                     item.reset();
                     
                     handler = getHandler(item.particle);
                     item.o = handler.startParticle(stack.peek1().o, startName, item.particle, atts, nsRegistry);
                  }
                  
                  ParticleBinding curParticle = cursor.getCurrentParticle();
                  if(curParticle != prevParticle)
                  {
                     if(item.repeatableParticleValue != null &&
                           prevParticle != null && prevParticle.isRepeatable() && prevParticle.getTerm().isModelGroup())
                     {
                        StackItem parentItem = stack.peek1();
                        endRepeatableParticle(parentItem.o, item.repeatableParticleValue, item.qName, prevParticle, parentItem.particle);
                        item.repeatableParticleValue = null;
                     }

                     if(newCursors.size() > 1 && curParticle.isRepeatable())
                     {
                        stack.peek().repeatableParticleValue = startRepeatableParticle(stack.peek1().o, startName, curParticle);
                     }
                  }
                  else
                  {
                     repeatedParticle = true;
                  }

                  // push all except the last one
                  parentType = item.parentType;
                  Object o = item.o;
                  for(int i = newCursors.size() - 2; i >= 0; --i)
                  {
                     cursor = newCursors.get(i);

                     ParticleBinding modelGroupParticle = cursor.getParticle();
                     handler = getHandler(modelGroupParticle);
                     o = handler.startParticle(o, startName, modelGroupParticle, atts, nsRegistry);
                     push(startName, cursor, o, handler, parentType);
                  }
                  cursor = newCursors.get(0);
                  particle = cursor.getCurrentParticle();
                  break;
               }
            }
         }
      }

      Object o = null;
      if(particle != null)
      {
         Object parent = stack.isEmpty() ? null :
            (repeated ? stack.peek1().o : stack.peek().o);
         if(particle.getTerm().isWildcard())
         {
            /*
            WildcardBinding wildcard = (WildcardBinding)particle.getTerm();
            ElementBinding element = wildcard.getElement(startName, atts);
            */
            ElementBinding element = cursor.getElement();
            if(element == null)
            {
               throw new JBossXBRuntimeException("Failed to resolve element " +
                  startName + " for wildcard."
               );
            }

            if(!repeatedParticle && particle.isRepeatable())
            {
               stack.peek().repeatableParticleValue = startRepeatableParticle(parent, startName, particle);
            }
            particle = new ParticleBinding(element/*, particle.getMinOccurs(), particle.getMaxOccurs(), particle.getMaxOccursUnbounded()*/);
         }

         ElementBinding element = (ElementBinding)particle.getTerm();

         // todo xsi:type support should be implemented in a better way
         String xsiType = atts.getValue("xsi:type");
         if(xsiType != null)
         {
            if(trace)
               log.trace(element.getQName() + " uses xsi:type " + xsiType);

            if(item != null && item.nonXsiParticle == null)
               item.nonXsiParticle = particle;
            
            String xsiTypePrefix;
            String xsiTypeLocal;
            int colon = xsiType.indexOf(':');
            if(colon == -1)
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
            if(xsiTypeBinding == null)
            {
               throw new JBossXBRuntimeException("Type binding not found for type " +
                  xsiTypeQName +
                  " specified with xsi:type for element " + startName
               );
            }

            ElementBinding xsiElement = new ElementBinding(schemaBinding, startName, xsiTypeBinding);
            xsiElement.setRepeatableHandler(element.getRepeatableHandler());
            particle =
               new ParticleBinding(xsiElement,
                  particle.getMinOccurs(),
                  particle.getMaxOccurs(),
                  particle.getMaxOccursUnbounded()
               );
         }

         if(!repeated && particle.isRepeatable())
         {
            stack.peek().repeatableParticleValue = startRepeatableParticle(parent, startName, particle);
         }

         TypeBinding type = element.getType();
         if(type == null)
         {
            throw new JBossXBRuntimeException("No type for element " + element);
         }

         handler = type.getHandler();         
         if(handler == null)
         {
            handler = defParticleHandler;
         }

         List<ElementInterceptor> localInterceptors = parentType == null ? Collections.EMPTY_LIST : parentType.getInterceptors(startName);         
         List<ElementInterceptor> interceptors = element.getInterceptors();
         if(interceptors.size() + localInterceptors.size() > 0)
         {
            if (repeated)
            {
               pop();
            }

            for (int i = 0; i < localInterceptors.size(); ++i)
            {
               ElementInterceptor interceptor = localInterceptors.get(i);
               parent = interceptor.startElement(parent, startName, type);
               push(startName, particle, parent, handler, parentType);
               interceptor.attributes(parent, startName, type, atts, nsRegistry);
            }

            for (int i = 0; i < interceptors.size(); ++i)
            {
               ElementInterceptor interceptor = interceptors.get(i);
               parent = interceptor.startElement(parent, startName, type);
               push(startName, particle, parent, handler, parentType);
               interceptor.attributes(parent, startName, type, atts, nsRegistry);
            }

            if (repeated)
            {
               // to have correct endRepeatableParticle calls
               stack.push(item);
            }
         }

         String nil = atts.getValue("xsi:nil");
         if(nil == null || !("1".equals(nil) || "true".equals(nil)))
         {
            o = handler.startParticle(parent, startName, particle, atts, nsRegistry);
         }
         else
         {
            o = NIL;
         }
      }
      else
      {
         ElementBinding parentBinding = null;
         if(!stack.isEmpty())
         {
            ParticleBinding stackParticle = repeated ? stack.peek1().particle : stack.peek().particle;
            if(stackParticle != null)
            {
               parentBinding = (ElementBinding)stackParticle.getTerm();
            }
         }

         if(parentBinding != null && parentBinding.getSchema() != null)
         {
            schemaBinding = parentBinding.getSchema();
         }

         String msg = "Element " +
            startName +
            " is not bound " +
            (parentBinding == null ? "as a global element." : "in type " + parentBinding.getType().getQName());
         if(schemaBinding != null && schemaBinding.isStrictSchema())
         {
            throw new JBossXBRuntimeException(msg);
         }
         else if(trace)
         {
            log.trace(msg);
         }
      }

      if(repeated)
      {
         item.o = o;
         // in case of collection of abstract types
         item.particle = particle;
      }
      else
      {
         push(startName, particle, o, handler, parentType);
      }
   }

   private ParticleHandler getHandler(ParticleBinding modelGroupParticle)
   {
      ParticleHandler handler = ((ModelGroupBinding)modelGroupParticle.getTerm()).getHandler();
      return handler == null ? defParticleHandler : handler;
   }

   private void endRepeatableParent(QName startName)
   {
      int stackIndex = stack.size() - 2;
      StackItem parentItem = stack.peek1();
      ParticleBinding parentParticle = null;
      while(true)
      {
         if(parentItem.cursor == null)
         {
            throw new JBossXBRuntimeException(
               "Failed to start " + startName +
               ": the element is not repeatable, repeatable parent expected to be a model group but got element " +
               ((ElementBinding)parentItem.particle.getTerm()).getQName()
            );
         }

         parentParticle = parentItem.particle;
         if(parentParticle.isRepeatable())
            break;

         StackItem item = parentItem;
         parentItem = stack.peek(--stackIndex);
         endParticle(item, parentItem);
      }

      if(!parentParticle.isRepeatable())
      {
         StringBuffer msg = new StringBuffer();

         StackItem item = stack.peek();
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

      // todo startName is wrong here
      StackItem item = parentItem;
      parentItem = stack.peek(stackIndex - 1);
      endParticle(item, parentItem);

      ParticleHandler handler = getHandler(item.particle);
      item.reset();
      item.o = handler.startParticle(parentItem.o, startName, item.particle, null, nsRegistry);

      while(++stackIndex < stack.size() - 1)
      {
         parentItem = item;
         item = stack.peek(stackIndex);
         handler = getHandler(item.particle);
         item.reset();
         item.o = handler.startParticle(parentItem.o, startName, item.particle, null, nsRegistry);
      }
   }

   private Object startRepeatableParticle(Object parent, QName startName, ParticleBinding particle)
   {
      if(trace)
         log.trace(" start repeatable (" + stack.size() + "): " + particle.getTerm());
      RepeatableParticleHandler repeatableHandler = particle.getTerm().getRepeatableHandler();
      // the way it is now it's never null
      return repeatableHandler.startRepeatableParticle(parent, startName, particle);
   }

   private void endRepeatableParticle(Object parent, Object o, QName elementName, ParticleBinding particle, ParticleBinding parentParticle)
   {
      if (trace)
         log.trace(" end repeatable (" + stack.size() + "): " + particle.getTerm());

      RepeatableParticleHandler repeatableHandler = particle.getTerm().getRepeatableHandler();
      // the way it is now it's never null
      repeatableHandler.endRepeatableParticle(parent, o, elementName, particle, parentParticle);
   }

   private void endParticle(StackItem item, StackItem parentItem)
   {
      if(item.ended)
      {
         throw new JBossXBRuntimeException(item.particle.getTerm() + " has already been ended.");
      }

      ParticleBinding modelGroupParticle = item.particle;
      ParticleHandler handler = item.handler;//getHandler(modelGroupParticle);

      Object o = handler.endParticle(item.o, item.qName, modelGroupParticle);

      item.ended = true;

      // model group should always have parent particle
      //item = (StackItem)stack.peek(parentStackPos);
      if(parentItem.o != null)
      {
         ParticleBinding parentParticle = getParentParticle();//item.particle;
         if(parentParticle == null)
            parentParticle = parentItem.particle;
         
         if(parentItem.repeatableParticleValue == null)
            setParent(handler, parentItem.o, o, item.qName, modelGroupParticle, parentParticle);
         else
         {
            RepeatableParticleHandler repeatableHandler = modelGroupParticle.getTerm().getRepeatableHandler();
            repeatableHandler.addTermValue(parentItem.repeatableParticleValue, o, item.qName, modelGroupParticle, parentParticle, handler);
         }
      }
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
   
   private void flushIgnorableCharacters()
   {
      StackItem stackItem = stack.peek();
      if(stackItem.cursor != null || stackItem.textContent == null)
      {
         return;
      }

      if(stackItem.indentation == Boolean.TRUE || stackItem.ignorableCharacters)
      {
         if(log.isTraceEnabled())
         {
            log.trace("ignored characters: " + ((ElementBinding) stackItem.particle.getTerm()).getQName() + " '"
               + stackItem.textContent + "'");
         }
         stackItem.textContent = null;
         stackItem.indentation = null;
      }
   }
   
   private ParticleBinding getParentParticle()
   {
      StackItem item = stack.peek1();
      if(item == null)
         return null;
      
      ParticleBinding particle = item.particle;
      if(!particle.getTerm().isSkip())
         return particle;
      
      for(int i = stack.size() - 3; i >= 0; --i)
      {
         item = stack.peek(i);
         particle = item.particle;
         if(!particle.getTerm().isSkip())
            return particle;
      }
      return null;
   }
   
   private void endElement()
   {
      StackItem item = stack.peek();
      Object o = item.o;
      ParticleBinding particle = item.particle;
      
      ElementBinding element = (ElementBinding)particle.getTerm();
      QName endName = element.getQName();
      TypeBinding type = element.getType();
      List<ElementInterceptor> interceptors = element.getInterceptors();
      List<ElementInterceptor> localInterceptors = item.parentType == null ? Collections.EMPTY_LIST : item.parentType.getInterceptors(endName);
      int allInterceptors = interceptors.size() + localInterceptors.size();

      if(o != NIL)
      {
         //
         // characters
         //

         flushIgnorableCharacters();

         TypeBinding charType = type.getSimpleType();
         if(charType == null)
         {
            charType = type;
         }

         CharactersHandler charHandler = item.ignoreCharacters ? null : charType.getCharactersHandler();

         /**
          * If there is text content then unmarshal it and set.
          * If there is no text content and the type is simple and
          * its characters handler is not null then unmarshal and set.
          * If the type is complex and there is no text data then the unmarshalled value
          * of the empty text content is assumed to be null
          * (in case of simple types that's not always true and depends on nillable attribute).
          */
         String textContent = item.textContent == null ? "" : item.textContent.toString();
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
               {
                  dataContent = StringPropertyReplacer.replaceProperties(dataContent);
               }
               
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
               if(o == null)
               {
                  o = unmarshalled;
               }
               else if(charHandler != null)
               {
                  TermBeforeSetParentCallback beforeSetParent = charType.getBeforeSetParentCallback();
                  if(beforeSetParent != null)
                  {
                     ctx.parent = o;
                     ctx.particle = particle;
                     ctx.parentParticle = getParentParticle();
                     unmarshalled = beforeSetParent.beforeSetParent(unmarshalled, ctx);
                     ctx.clear();
                  }
                  
                  charHandler.setValue(endName, element, o, unmarshalled);
               }
            }

            if(allInterceptors > 0)
            {
               int interceptorIndex = stack.size() - 1 - allInterceptors;
               for (int i = interceptors.size() - 1; i >= 0; --i)
               {
                  ElementInterceptor interceptor = interceptors.get(i);
                  interceptor.characters(stack.peek(interceptorIndex++).o, endName, type, nsRegistry, dataContent);
               }

               for (int i = localInterceptors.size() - 1; i >= 0; --i)
               {
                  ElementInterceptor interceptor = localInterceptors.get(i);
                  interceptor.characters(stack.peek(interceptorIndex++).o, endName, type, nsRegistry, dataContent);
               }
            }
         }
      }
      else
      {
         o = null;
      }

      //
      // endElement
      //

      StackItem parentItem = stack.size() == 1 ? null : stack.peek1();
      Object parent = parentItem == null ? null : parentItem.o;
      ParticleHandler handler = stack.peek().handler;
      
      o = handler.endParticle(o, endName, particle);

      if(!interceptors.isEmpty())
      {
         int interceptorIndex = stack.size() - 1 - interceptors.size();
         for (int i = interceptors.size() - 1; i >= 0; --i)
         {
            ElementInterceptor interceptor = interceptors.get(i);
            interceptor.endElement(stack.peek(interceptorIndex++).o, endName, type);
         }
      }
      
      //
      // setParent
      //

      if(allInterceptors == 0)
      {
         ParticleBinding parentParticle = getParentParticle();
         boolean hasWildcard = false;
         if (parentParticle != null && parentParticle.getTerm().isElement())
         {
            WildcardBinding wildcard = ((ElementBinding) parentParticle.getTerm()).getType().getWildcard();
            if (wildcard != null)
            {
               hasWildcard = true;
               if(parentItem.cursor.isWildcardContent())
               {
                  ParticleHandler wh = wildcard.getWildcardHandler();
                  if(wh != null)
                     handler = wh;
               }
            }
         }

         if(parent != null)
         {
            if(parentItem.repeatableParticleValue == null)
               setParent(handler, parent, o, endName, particle, parentParticle);
            else
            {
               RepeatableParticleHandler repeatableHandler = particle.getTerm().getRepeatableHandler();
               repeatableHandler.addTermValue(parentItem.repeatableParticleValue, o, endName, particle, parentParticle, handler);
            }
         }
         else if(parentParticle != null && hasWildcard && stack.size() > 1)
         {
            // the parent has anyType, so it gets the value of its child
            for(int i = stack.size() - 2; i >= 0; --i)
            {
               StackItem peeked = stack.peek(i);
               peeked.o = o;
               if(peeked.cursor == null)
                  break;
            }

            if(trace)
               log.trace("Value of " + endName + " " + o + " is promoted as the value of its parent element.");
         }
      }
      else
      {
         StackItem popped = pop();

         for(int i = interceptors.size() - 1; i >= 0; --i)
         {
            ElementInterceptor interceptor = interceptors.get(i);
            parent = pop().o;
            interceptor.add(parent, o, endName);
            o = parent;
         }

         for(int i = localInterceptors.size() - 1; i >= 0; --i)
         {
            ElementInterceptor interceptor = localInterceptors.get(i);
            parent = pop().o;
            interceptor.add(parent, o, endName);
            o = parent;
         }

         // need to have correst endRepeatableParticle events
         stack.push(popped);
      }

      if(stack.size() == 1)
      {
         o = type.getValueAdapter().cast(o, Object.class);
         root = o;
         stack.clear();
         
         if(sawDTD)
         {
            // Probably should be integrated into schema binding?
            try
            {
               // setDTD(String root, String publicId, String systemId)
               Class[] sig = {String.class, String.class, String.class};
               Method setDTD = o.getClass().getMethod("setDTD", sig);
               Object[] args = {dtdRootName, dtdPublicId, dtdSystemId};
               setDTD.invoke(o, args);
            }
            catch(Exception e)
            {
               log.debug("No setDTD found on root: " + o);
            }
         }
      }
   }

   private void setParent(ParticleHandler handler,
                          Object parent,
                          Object o,
                          QName endName,
                          ParticleBinding particle,
                          ParticleBinding parentParticle)
   {
      TermBeforeSetParentCallback beforeSetParent = particle.getTerm().getBeforeSetParentCallback();
      if(beforeSetParent != null)
      {
         ctx.parent = parent;
         ctx.particle = particle;
         ctx.parentParticle = parentParticle;
         o = beforeSetParent.beforeSetParent(o, ctx);
         ctx.clear();
      }
      
      handler.setParent(parent, o, endName, particle, parentParticle);
   }

   private void push(QName qName, ParticleBinding particle, Object o, ParticleHandler handler, TypeBinding parentType)
   {
      StackItem item = new StackItem(qName, particle);
      item.o = o;
      item.handler = handler;
      item.parentType = parentType;      
      stack.push(item);
      if(trace)
      {
         if(particle != null)
            log.trace("pushed " + ((ElementBinding)particle.getTerm()).getQName() + "=" + o);
         else
            log.trace("pushed null particle, o=" + o);
      }
   }

   private void push(QName qName, ModelGroupBinding.Cursor cursor, Object o, ParticleHandler handler, TypeBinding parentType)
   {
      StackItem item = new StackItem(qName, cursor);
      item.o = o;
      item.handler = handler;
      item.parentType = parentType;
      stack.push(item);
      if(trace)
         log.trace("pushed cursor " + cursor + ", o=" + o);
   }

   private StackItem pop()
   {
      StackItem item = stack.pop();
      if(trace)
      {
         if(item.cursor == null)
            log.trace("poped " + ((ElementBinding)item.particle.getTerm()).getQName() + "=" + item.particle);
         else
            log.trace("poped " + item.cursor.getParticle().getTerm());
      }
      return item;
   }

   // Inner

   private static class StackItem
   {
      final QName qName;
      final ModelGroupBinding.Cursor cursor;
      ParticleBinding particle;
      ParticleBinding nonXsiParticle;
      ParticleHandler handler;
      TypeBinding parentType;
      boolean ignoreCharacters;
      Object o;
      Object repeatableParticleValue;
      StringBuffer textContent;
      Boolean indentation;
      boolean ignorableCharacters = true;
      boolean ended;

      public StackItem(QName qName, ModelGroupBinding.Cursor cursor)
      {
         if (cursor == null)
            throw new IllegalArgumentException("Null cursor");
         // this is modelgroup particle
         this.cursor = cursor;
         
         if(qName == null)
            throw new IllegalArgumentException("Null qName");
         this.qName = qName;
         
         this.particle = cursor.getParticle();
      }

      public StackItem(QName qName, ParticleBinding particle)
      {
         if (particle == null)
            throw new IllegalArgumentException("Null particle");
         // this is element particle
         this.cursor = null;
         
         if(qName == null)
            throw new IllegalArgumentException("Null qName");
         this.qName = qName;

         this.particle = particle;
      }

      void reset()
      {
         if(!ended)
         {
            throw new JBossXBRuntimeException(
               "Attempt to reset a particle that has already been reset: " + particle.getTerm()
            );
         }

         ended = false;
         o = null;
         if(textContent != null)
            textContent.setLength(0);
         
         indentation = null;
         ignorableCharacters = true;
         
         if(nonXsiParticle != null)
            particle = nonXsiParticle;
      }
   }

   static class StackImpl
   {
      private List<StackItem> list = new ArrayList<StackItem>();
      private StackItem head;
      private StackItem peek1;

      public void clear()
      {
         list.clear();
         head = null;
         peek1 = null;
      }

      public void push(StackItem o)
      {
         list.add(o);
         peek1 = head;
         head = o;
      }

      public StackItem pop()
      {
         head = peek1;
         int index = list.size() - 1;
         peek1 = index > 1 ? list.get(index - 2) : null;
         return list.remove(index);
      }

      public StackItem peek()
      {
         return head;
      }

      public StackItem peek1()
      {
         return peek1;
      }

      public StackItem peek(int i)
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
