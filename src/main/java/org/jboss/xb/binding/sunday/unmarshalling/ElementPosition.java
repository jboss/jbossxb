/*
* JBoss, Home of Professional Open Source
* Copyright 2009, JBoss Inc., and individual contributors as indicated
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

import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.jboss.util.StringPropertyReplacer;
import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.NamespaceRegistry;
import org.jboss.xb.binding.metadata.CharactersMetaData;
import org.jboss.xb.binding.metadata.ValueMetaData;
import org.jboss.xb.binding.sunday.unmarshalling.SundayContentHandler.UnmarshallingContextImpl;
import org.jboss.xb.binding.sunday.xop.XOPIncludeHandler;
import org.xml.sax.Attributes;

/**
 * A ElementPosition.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class ElementPosition extends AbstractPosition
{
   private ParticleBinding nonXsiParticle;
   private boolean ignoreCharacters;
   private StringBuffer textContent;
   private Boolean indentation;
   private boolean ignorableCharacters = true;

   private Object[] interceptorObjects;

   public ElementPosition(QName qName, ParticleBinding particle)
   {
      super(qName, particle);
   }

   public boolean isElement()
   {
      return true;
   }

   public void reset()
   {
      if(!ended)
         throw new JBossXBRuntimeException("Attempt to reset a particle that has already been reset: " + particle.getTerm());
      ended = false;
      o = null;
      
      if(textContent != null)
         textContent.setLength(0);
      
      indentation = null;
      ignorableCharacters = true;
      
      if(nonXsiParticle != null)
         particle = nonXsiParticle;
   }
   
   public ParticleBinding getNonXsiParticle()
   {
      return nonXsiParticle;
   }
   
   public void setNonXsiParticle(ParticleBinding nonXsiParticle)
   {
      this.nonXsiParticle = nonXsiParticle;
   }
   
   public boolean isIgnoreCharacters()
   {
      return ignoreCharacters;
   }
   
   public void setIgnoreCharacters(boolean ignoreCharacters)
   {
      this.ignoreCharacters = ignoreCharacters;
   }
   
   public StringBuffer getTextContent()
   {
      return this.textContent;
   }

   public AbstractPosition nextPosition(QName startName, Attributes atts)
   {
      if (ended) // this is about repeating itself
      {
         if (!qName.equals(startName))
         {
            if (repeatableParticleValue != null)
               endRepeatableParticle();
            return null;
         }

         if (particle.isRepeatable())
         {
            if (particle.isOccurrenceAllowed(occurrence + 1))
            {
               reset();
               ++occurrence;
               return this;
            }
            else if (repeatableParticleValue != null)
               endRepeatableParticle();
            return null;
         }

         // it's not repeatable but it re-appeared
         // it probably has a repeatable parent
         reset();
         previous.repeatForChild(atts);
         occurrence = 1;
         if(next != null)
         {
            next.previous = null;
            next = null;
         }
         return this;
      }
      
      // this is locating the next child
      ElementBinding element = (ElementBinding) particle.getTerm();
      TypeBinding parentType = element.getType();
      ParticleBinding typeParticle = parentType.getParticle();
      ModelGroupBinding modelGroup = typeParticle == null ? null : (ModelGroupBinding) typeParticle.getTerm();
      if (modelGroup == null)
      {
         if (startName.equals(Constants.QNAME_XOP_INCLUDE))
         {
            SchemaBinding schema = element.getSchema();
            TypeBinding anyUriType = schema.getType(Constants.QNAME_ANYURI);
            if (anyUriType == null)
               log.warn("Type " + Constants.QNAME_ANYURI + " not bound.");

            ElementBinding parentElement = (ElementBinding) particle.getTerm();
            parentElement.setXopUnmarshaller(schema.getXopUnmarshaller());

            flushIgnorableCharacters();
            handler = DefaultHandlers.XOP_HANDLER;
            ignoreCharacters = true;
            initValue(null);

            TypeBinding xopIncludeType = new TypeBinding(new QName(Constants.NS_XOP_INCLUDE, "Include"));
            xopIncludeType.setSchemaBinding(schema);
            xopIncludeType.addAttribute(new QName("href"), anyUriType, DefaultHandlers.ATTRIBUTE_HANDLER);
            xopIncludeType.setHandler(new XOPIncludeHandler(parentType, schema.getXopUnmarshaller()));

            ElementBinding xopInclude = new ElementBinding(schema, Constants.QNAME_XOP_INCLUDE, xopIncludeType);
            next = new ElementPosition(startName, new ParticleBinding(xopInclude));
            next.stack = stack;
            next.previous = this;
            next.notSkippedParent = getLastNotSkipped();
            return next;
         }

         QName typeName = parentType.getQName();
         throw new JBossXBRuntimeException((typeName == null ? "Anonymous" : typeName.toString()) + " type of element "
               + qName + " should be complex and contain " + startName + " as a child element.");
      }

      if (next != null)
      {
         if (particle.isOccurrenceAllowed(occurrence + 1))
         {
            // this increase is actually ahead of its time, it may fail to locate the element
            // but in the current impl it doesn't matter
            ++occurrence;
         }
         else
         {
            throw new JBossXBRuntimeException(startName + " cannot appear in this position. Expected content of "
                  + qName + " is " + modelGroup);
         }
      }

      next = modelGroup.newPosition(startName, atts, typeParticle);
      if (next == null)
         throw new JBossXBRuntimeException(startName + " not found as a child of " + qName + " in " + modelGroup);

      next.previous = this;      
      
      flushIgnorableCharacters();

      AbstractPosition nextPosition = next;
      while (nextPosition.next != null)
      {
         nextPosition.notSkippedParent = nextPosition.previous.getLastNotSkipped();
         if (nextPosition.particle.isRepeatable())
            nextPosition.startRepeatableParticle();
         nextPosition.stack = stack;
         nextPosition.initValue(atts);
         nextPosition.parentType = parentType;
         nextPosition = nextPosition.next;
      }

      nextPosition.stack = stack;
      nextPosition.parentType = parentType;
      nextPosition.notSkippedParent = nextPosition.previous.getLastNotSkipped();
      return (ElementPosition) nextPosition;
   }

   public void characters(char[] ch, int start, int length)
   {
      ElementBinding e = (ElementBinding) particle.getTerm();

      // collect characters only if they are allowed content
      if(e.getType().isTextContentAllowed())
      {
         if(indentation != Boolean.FALSE)
         {
            if(e.getType().isSimple())
            {
               // simple content is not analyzed
               indentation = Boolean.FALSE;
               ignorableCharacters = false;
            }
            else if(e.getSchema() != null && !e.getSchema().isIgnoreWhitespacesInMixedContent())
            {
               indentation = Boolean.FALSE;
               ignorableCharacters = false;
            }
            else
            {
               // the indentation is currently defined as whitespaces with next line characters
               // this should probably be externalized in the form of a filter or something
               for (int i = start; i < start + length; ++i)
               {
                  if(ch[i] == 0x0a)
                  {
                     indentation = Boolean.TRUE;
                  }
                  else if (!Character.isWhitespace(ch[i]))
                  {
                     indentation = Boolean.FALSE;
                     ignorableCharacters = false;
                     break;
                  }
               }
            }
         }
         
         if (textContent == null)
            textContent = new StringBuffer();
         textContent.append(ch, start, length);
      }
   }
   
   public void endParticle()
   {
      ended = true;
      if(skip)
         return;
      
      ElementBinding element = (ElementBinding) particle.getTerm();
      TypeBinding type = element.getType();
      
      List<ElementInterceptor> interceptors = null;
      List<ElementInterceptor> localInterceptors = null;
      if(interceptorObjects != null)
      {
         interceptors = element.getInterceptors();
         localInterceptors = parentType == null ? Collections.<ElementInterceptor>emptyList() : parentType.getInterceptors(qName);
      }

      if(o != SundayContentHandler.NIL)
      {
         //
         // characters
         //

         flushIgnorableCharacters();

         TypeBinding charType = type.getSimpleType();
         if(charType == null)
            charType = type;

         CharactersHandler charHandler = ignoreCharacters ? null : charType.getCharactersHandler();

         /**
          * If there is text content then unmarshal it and set.
          * If there is no text content and the type is simple and
          * its characters handler is not null then unmarshal and set.
          * If the type is complex and there is no text data then the unmarshalled value
          * of the empty text content is assumed to be null
          * (in case of simple types that's not always true and depends on nillable attribute).
          */
         String textContent = this.textContent == null ? "" : this.textContent.toString();
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
               dataContent = textContent;
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
                  && !element.isSkip())
               {
                  throw new JBossXBRuntimeException("Element " +
                     qName +
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
                  charHandler.unmarshalEmpty(qName, charType, stack.getNamespaceRegistry(), valueMetaData) :
                  charHandler.unmarshal(qName, charType, stack.getNamespaceRegistry(), valueMetaData, dataContent);
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
                     UnmarshallingContextImpl ctx = stack.getContext();
                     ctx.parent = o;
                     ctx.particle = particle;
                     ctx.parentParticle = notSkippedParent.particle;
                     unmarshalled = beforeSetParent.beforeSetParent(unmarshalled, ctx);
                     ctx.clear();
                  }
                  
                  charHandler.setValue(qName, element, o, unmarshalled);
               }
            }

            if(interceptorObjects != null)
            {
               NamespaceRegistry nsRegistry = stack.getNamespaceRegistry();
               int ioIndex = 0;
               for (int i = interceptors.size() - 1; i >= 0; --i)
               {
                  ElementInterceptor interceptor = interceptors.get(i);
                  interceptor.characters(interceptorObjects[ioIndex++], qName, type, nsRegistry, dataContent);
               }

               for (int i = localInterceptors.size() - 1; i >= 0; --i)
               {
                  ElementInterceptor interceptor = localInterceptors.get(i);
                  interceptor.characters(interceptorObjects[ioIndex++], qName, type, nsRegistry, dataContent);
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

      o = handler.endParticle(o, qName, particle);

      if(interceptorObjects != null && !interceptors.isEmpty())
      {
         int ioIndex = 0;
         for (int i = interceptors.size() - 1; i >= 0; --i)
         {
            ElementInterceptor interceptor = interceptors.get(i);
            interceptorObjects[ioIndex] = interceptor.endElement(interceptorObjects[ioIndex++], qName, type);
         }
      }
      
      //
      // setParent
      //

      if(interceptorObjects != null)
      {
         int ioIndex = 0;
         for(int i = interceptors.size() - 1; i >= 0; --i)
         {
            ElementInterceptor interceptor = interceptors.get(i);
            Object parent = interceptorObjects[ioIndex++];
            interceptor.add(parent, o, qName);
            o = parent;
         }

         for(int i = localInterceptors.size() - 1; i >= 0; --i)
         {
            ElementInterceptor interceptor = localInterceptors.get(i);
            Object parent = interceptorObjects[ioIndex++];
            interceptor.add(parent, o, qName);
            o = parent;
         }
      }
      else if (notSkippedParent != null)
      {
         ParticleBinding parentParticle = notSkippedParent.particle;
         TermBinding parentTerm = parentParticle.getTerm();

         if (notSkippedParent.o != null)
         {
            ParticleHandler handler = this.handler;
            if (parentTerm.isWildcard())
            {
               ParticleHandler wh = ((WildcardBinding) parentTerm).getWildcardHandler();
               if (wh != null)
                  handler = wh;
            }
            setParent(notSkippedParent, handler);
         }
         else if (parentTerm.isWildcard())
         {
            // the parent has anyType, so it gets the value of its child
            AbstractPosition parentPos = previous;
            parentPos.o = o;
            while(!parentPos.isElement())
            {
               parentPos = parentPos.getPrevious();
               parentPos.o = o;
            }

            if (trace)
               log.trace("Value of " + qName + " " + o + " is promoted as the value of its parent element.");
         }
      }
   }
   
   public ElementPosition startParticle(QName startName, Attributes atts)
   {
      return (ElementPosition) nextPosition(startName, atts);
   }

   public void repeatForChild(Attributes atts)
   {
      throw new JBossXBRuntimeException("Failed to repeat parent for non-repeatable element: "
            + "repeatable parent expected to be a model group but got element "
            + qName);
   }

   public void push(Attributes atts)
   {
      ElementBinding element = (ElementBinding) particle.getTerm();

      // TODO xsi:type support should be implemented in a better way
      String xsiType = atts.getValue(Constants.NS_XML_SCHEMA_INSTANCE, "type");
      if (xsiType != null)
      {
         if (trace)
            log.trace(element.getQName() + " uses xsi:type " + xsiType);

         if (nonXsiParticle == null)
            nonXsiParticle = particle;

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

         String xsiTypeNs = stack.getNamespaceRegistry().getNamespaceURI(xsiTypePrefix);
         QName xsiTypeQName = new QName(xsiTypeNs, xsiTypeLocal);

         SchemaBinding schemaBinding = element.getSchema();
         TypeBinding xsiTypeBinding = schemaBinding.getType(xsiTypeQName);
         if (xsiTypeBinding == null)
         {
            throw new JBossXBRuntimeException("Type binding not found for type " + xsiTypeQName
                  + " specified with xsi:type for element " + qName);
         }

         ElementBinding xsiElement = new ElementBinding(schemaBinding, qName, xsiTypeBinding);
         xsiElement.setRepeatableHandler(element.getRepeatableHandler());

         particle = new ParticleBinding(xsiElement, particle.getMinOccurs(), particle.getMaxOccurs(), particle.getMaxOccursUnbounded());
      }

      if (occurrence == 1 && particle.isRepeatable())
         startRepeatableParticle();

      TypeBinding type = element.getType();
      if (type == null)
         throw new JBossXBRuntimeException("No type for element " + element);

      handler = element.getHandler();
      if (handler == null)
         handler = DefaultHandlers.ELEMENT_HANDLER;

      Object parent = previous == null ? null : previous.o;

      if(parentType != null)
      {
         List<ElementInterceptor> interceptors = parentType.getInterceptors(qName);
         if(!interceptors.isEmpty())
         {
            NamespaceRegistry nsRegistry = stack.getNamespaceRegistry();
            interceptorObjects = new Object[interceptors.size() + element.getInterceptors().size()];
            // objects are written to the array in the reversed order to optimize endParticle iterations
            int ioIndex = interceptorObjects.length - 1;
            for(ElementInterceptor i : interceptors)
            {
               parent = i.startElement(parent, qName, type);
               i.attributes(parent, qName, type, atts, nsRegistry);
               interceptorObjects[ioIndex--] = parent;
            }
         }
      }
      
      if(!element.getInterceptors().isEmpty())
      {
         NamespaceRegistry nsRegistry = stack.getNamespaceRegistry();
         int ioIndex;
         if(interceptorObjects == null)
         {
            interceptorObjects = new Object[element.getInterceptors().size()];
            ioIndex = interceptorObjects.length - 1;
         }
         else
            ioIndex = element.getInterceptors().size() - 1;
         for(ElementInterceptor i : element.getInterceptors())
         {
            parent = i.startElement(parent, qName, type);
            i.attributes(parent, qName, type, atts, nsRegistry);
            interceptorObjects[ioIndex--] = parent;
         }
      }

      String nil = atts.getValue(Constants.NS_XML_SCHEMA_INSTANCE, "nil");
      if (nil == null || !("1".equals(nil) || "true".equals(nil)))
         initValue(atts);
      else
         o = SundayContentHandler.NIL;
   }
   
   private void flushIgnorableCharacters()
   {
      if(textContent == null)
         return;

      if(indentation == Boolean.TRUE || ignorableCharacters)
      {
         if(trace)
            log.trace("ignored characters: " + ((ElementBinding) particle.getTerm()).getQName() + " '" + textContent + "'");
         textContent = null;
         indentation = null;
      }
   }
   
   @Override
   protected ParticleHandler getHandler()
   {
      return DefaultHandlers.ELEMENT_HANDLER;
   }
}