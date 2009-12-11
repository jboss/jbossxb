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
import org.jboss.xb.binding.sunday.unmarshalling.position.AbstractPosition;
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
   private final SchemaBinding schema;

   private final StackImpl stack = new StackImpl();

   private Object root;

   // DTD information frm startDTD
   private String dtdRootName;
   private String dtdPublicId;
   private String dtdSystemId;
   private boolean sawDTD;

   private boolean trace = log.isTraceEnabled();

   private UnmarshallingContextImpl ctx = new UnmarshallingContextImpl();
   private NamespaceRegistry nsRegistry = new NamespaceRegistry();

   public SundayContentHandler(SchemaBinding schema)
   {
      this.schema = schema;
      this.schemaResolver = null;
      AbstractPosition.resetTrace();
   }

   public SundayContentHandler(SchemaBindingResolver schemaResolver)
   {
      this.schemaResolver = schemaResolver;
      this.schema = null;
      AbstractPosition.resetTrace();
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
      Position position = stack.current();
      if(!position.isElement())
         return;
      
      // if current is ended the characters belong to its parent
      if(position.isEnded())
      {
         position = stack.parent();
         if(!position.isElement())
         {
            for(int i = stack.size() - 3; i >= 0; --i)
            {
               position = stack.parent(i);
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
         position = stack.current();
         if(position.isElement())
         {
            if(position.isEnded())
            {
               if(position.getRepeatableParticleValue() != null)
                  position.endRepeatableParticle(stack.parent());
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
            if(position.getRepeatableParticleValue() != null)
               position.endRepeatableParticle(stack.parent());
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
         
         new ElementPosition(startName, particle).push(stack, atts, false);
         return;
      }

      while (!stack.isEmpty())
      {
         Position current = stack.current();
         ElementPosition next = current.startParticle(startName, atts);
         if (next != null)
         {
            next.push(stack, atts, current == next);
            break;
         }
         stack.pop();
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

   // Inner

   public class StackImpl implements PositionStack
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
         o.setStack(this);
         if(trace)
            log.trace("pushed " + o.getParticle().getTerm());
      }

      public void push(QName qName, ParticleBinding particle, Object o, ParticleHandler handler, TypeBinding parentType)
      {
         ElementPosition position = new ElementPosition(qName, particle, o, handler, parentType);
         push(position);
      }

      public Position pop()
      {
         head = peek1;
         int index = list.size() - 1;
         peek1 = index > 1 ? list.get(index - 2) : null;
         
         Position popped = list.remove(index);
         if(trace)
            log.trace("popped " + popped.getParticle().getTerm());
         return popped;
      }

      public Position current()
      {
         return head;
      }

      public Position parent()
      {
         return peek1;
      }

      public Position parent(int i)
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
      
      public Position notSkippedParent()
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
      
      public Position notSkippedParent(int i)
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

      public UnmarshallingContextImpl getContext()
      {
         return ctx;
      }

      public NamespaceRegistry getNamespaceRegistry()
      {
         return nsRegistry;
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
