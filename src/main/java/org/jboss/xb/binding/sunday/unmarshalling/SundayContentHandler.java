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
import org.jboss.xb.binding.AttributesImpl;
import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.NamespaceRegistry;
import org.jboss.xb.binding.Util;
import org.jboss.xb.binding.introspection.FieldInfo;
import org.jboss.xb.binding.metadata.PropertyMetaData;
import org.jboss.xb.binding.parser.JBossXBParser;
import org.jboss.xb.binding.resolver.MutableSchemaResolver;
import org.jboss.xb.binding.sunday.unmarshalling.position.ElementPosition;
import org.jboss.xb.binding.sunday.unmarshalling.position.Position;
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

   public final static Object NIL = new Object();

   private final SchemaBindingResolver schemaResolver;

   private final StackImpl stack = new StackImpl();

   private Object root;

   // DTD information frm startDTD
   private String dtdRootName;
   private String dtdPublicId;
   private String dtdSystemId;
   private boolean sawDTD;

   private final boolean trace = log.isTraceEnabled();

   public SundayContentHandler(SchemaBinding schema)
   {
      this.stack.schema = schema;
      this.schemaResolver = null;
   }

   public SundayContentHandler(SchemaBindingResolver schemaResolver)
   {
      this.schemaResolver = schemaResolver;
      this.stack.schema = null;
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
      Position position = null;
      while(elementBinding == null && !stack.isEmpty())
      {
         position = stack.peek();
         if(position.isElement())
         {
            if(position.isEnded())
            {
               if(position.getParticle().isRepeatable())
               {
                  Position parentPosition = stack.peek1();
                  if(parentPosition.getRepeatableParticleValue() != null)
                     stack.endRepeatableParticle(parentPosition, position.getQName(), position.getParticle(), parentPosition.getParticle());
               }
               stack.pop();
            }
            else
            {
               elementBinding = (ElementBinding)position.getParticle().getTerm();

               QName endName = localName.length() == 0 ? new QName(qName) : new QName(namespaceURI, localName);
               if(!elementBinding.getQName().equals(endName))
               {
                  throw new JBossXBRuntimeException("Failed to end element " +
                     new QName(namespaceURI, localName) +
                     ": element on the stack is " + elementBinding.getQName()
                  );
               }

               position.endParticle();
            }
         }
         else
         {
            position.endParticle();

            if(position.getParticle().isRepeatable())
            {
               Position parentPosition = stack.peek1();
               if(parentPosition.getRepeatableParticleValue() != null)
                  stack.endRepeatableParticle(parentPosition, position.getQName(), position.getParticle(), parentPosition.getParticle());
            }
            stack.pop();
         }
      }

      if(elementBinding == null)
         throw new JBossXBRuntimeException("Failed to endElement " + qName + ": binding not found");

      if(stack.size() == 1)
      {
         root = elementBinding.getType().getValueAdapter().cast(position.getValue(), Object.class);
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

   public void startElement(String namespaceURI,
                            String localName,
                            String qName,
                            Attributes atts,
                            XSTypeDefinition xercesType)
   {
      QName startName = localName.length() == 0 ? new QName(qName) : new QName(namespaceURI, localName);
      boolean repeated = false;
      Position position = null;
      SchemaBinding schemaBinding = stack.schema;

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
            Position peek = stack.peek();
            position = peek.startParticle(startName, atts);            
            if(position.isEnded())
            {
               stack.pop();
            }
            else
            {
               repeated = position == peek;
               break;
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

         String xsiTypeNs = stack.nsRegistry.getNamespaceURI(xsiTypePrefix);
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
         stack.startRepeatableParticle(stack.peek(), parent, startName, position.getParticle());

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
            interceptor.attributes(parent, startName, type, atts, stack.nsRegistry);
         }

         for (int i = 0; i < interceptors.size(); ++i)
         {
            ElementInterceptor interceptor = interceptors.get(i);
            parent = interceptor.startElement(parent, startName, type);
            push(startName, position.getParticle(), parent, position.getHandler(), position.getParentType());
            interceptor.attributes(parent, startName, type, atts, stack.nsRegistry);
         }

         if (repeated)
         {
            // to have correct endRepeatableParticle calls
            stack.push(position);
         }
      }

      if (!repeated)
         stack.push(position);

      String nil = atts.getValue(Constants.NS_XML_SCHEMA_INSTANCE, "nil");
      if (nil == null || !("1".equals(nil) || "true".equals(nil)))
         position.initValue(parent, atts);
      else
         position.setValue(NIL);
   }

   public void startPrefixMapping(String prefix, String uri)
   {
      stack.nsRegistry.addPrefixMapping(prefix, uri);
   }

   public void endPrefixMapping(String prefix)
   {
      stack.nsRegistry.removePrefixMapping(prefix);
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
      SchemaBindingResolver resolver = schemaResolver == null ? stack.schema.getSchemaResolver() : schemaResolver;
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

   public static class StackImpl
   {
      protected boolean trace = log.isTraceEnabled();
      
      private List<Position> list = new ArrayList<Position>();
      private Position head;
      private Position peek1;

      public SchemaBinding schema;
      public UnmarshallingContextImpl ctx = new UnmarshallingContextImpl();
      public NamespaceRegistry nsRegistry = new NamespaceRegistry();

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
         o.setStack(this);
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
      
      public Position getNotSkippedParent()
      {
         Position position = peek1;
         if(position == null)
            return null;
         
         ParticleBinding particle = position.getParticle();
         if(!particle.getTerm().isSkip() || position.getRepeatableParticleValue() != null)
            return position;
         
         Position wildcardPosition = null;
         if(particle.getTerm().isWildcard())
            wildcardPosition = position;

         for(int i = list.size() - 3; i >= 0; --i)
         {
            position = list.get(i);
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
      
      public Position getNotSkippedParent(int i)
      {
         Position position = null;
         while(i >= 0)
         {
            position = list.get(i--);
            ParticleBinding particle = position.getParticle();
            if(!particle.getTerm().isSkip() || position.getRepeatableParticleValue() != null)
               return position;
         }
         return null;
      }

      public void startRepeatableParticle(Position parentPosition, Object parent, QName startName, ParticleBinding particle)
      {
         if(trace)
            log.trace(" start repeatable (" + size() + "): " + particle.getTerm());

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

      public void endRepeatableParticle(Position parentPosition, QName elementName, ParticleBinding particle, ParticleBinding parentParticle)
      {
         if (trace)
            log.trace(" end repeatable (" + size() + "): " + particle.getTerm());
         RepeatableParticleHandler repeatableHandler = parentPosition.getRepeatableHandler();
         // the way it is now it's never null
         repeatableHandler.endRepeatableParticle(parentPosition.getValue(), parentPosition.getRepeatableParticleValue(), elementName, particle, parentParticle);
         parentPosition.setRepeatableParticleValue(null);
         parentPosition.setRepeatableHandler(null);
      }
   }
   
   public static class UnmarshallingContextImpl implements UnmarshallingContext
   {
      public Object parent;
      public ParticleBinding particle;
      public ParticleBinding parentParticle;
      
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
      
      public void clear()
      {
         parent = null;
         particle = null;
         parentParticle = null;
      }
   }
}
