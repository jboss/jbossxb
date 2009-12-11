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
package org.jboss.xb.binding.sunday.unmarshalling.position;

import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.jboss.util.StringPropertyReplacer;
import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.NamespaceRegistry;
import org.jboss.xb.binding.metadata.CharactersMetaData;
import org.jboss.xb.binding.metadata.ValueMetaData;
import org.jboss.xb.binding.sunday.unmarshalling.CharactersHandler;
import org.jboss.xb.binding.sunday.unmarshalling.DefaultHandlers;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ElementInterceptor;
import org.jboss.xb.binding.sunday.unmarshalling.ModelGroupBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleHandler;
import org.jboss.xb.binding.sunday.unmarshalling.PositionStack;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SundayContentHandler;
import org.jboss.xb.binding.sunday.unmarshalling.TermBeforeSetParentCallback;
import org.jboss.xb.binding.sunday.unmarshalling.TermBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.WildcardBinding;
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

   public ElementPosition(QName qName, ParticleBinding particle)
   {
      super(qName, particle);
   }

   public ElementPosition(QName qName, ParticleBinding particle, Object o, ParticleHandler handler, TypeBinding parentType)
   {
      super(qName, particle);
      this.particle = particle;
      this.o = o;
      this.handler = handler;
      this.parentType = parentType;
   }
   
   public boolean isElement()
   {
      return true;
   }

   public void reset()
   {
      super.reset();
      
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
      ElementBinding element = (ElementBinding) particle.getTerm();
      TypeBinding type = element.getType();
      List<ElementInterceptor> interceptors = element.getInterceptors();
      List<ElementInterceptor> localInterceptors = parentType == null ? Collections.<ElementInterceptor>emptyList() : parentType.getInterceptors(qName);
      int allInterceptors = interceptors.size() + localInterceptors.size();

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
                     ctx.parentParticle = stack.notSkippedParent().getParticle();
                     unmarshalled = beforeSetParent.beforeSetParent(unmarshalled, ctx);
                     ctx.clear();
                  }
                  
                  charHandler.setValue(qName, element, o, unmarshalled);
               }
            }

            if(allInterceptors > 0)
            {
               NamespaceRegistry nsRegistry = stack.getNamespaceRegistry();
               int interceptorIndex = stack.size() - 1 - allInterceptors;
               for (int i = interceptors.size() - 1; i >= 0; --i)
               {
                  ElementInterceptor interceptor = interceptors.get(i);
                  interceptor.characters(stack.parent(interceptorIndex++).getValue(), qName, type, nsRegistry, dataContent);
               }

               for (int i = localInterceptors.size() - 1; i >= 0; --i)
               {
                  ElementInterceptor interceptor = localInterceptors.get(i);
                  interceptor.characters(stack.parent(interceptorIndex++).getValue(), qName, type, nsRegistry, dataContent);
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

      if(!interceptors.isEmpty())
      {
         int interceptorIndex = stack.size() - 1 - interceptors.size();
         for (int i = interceptors.size() - 1; i >= 0; --i)
         {
            ElementInterceptor interceptor = interceptors.get(i);
            interceptor.endElement(stack.parent(interceptorIndex++).getValue(), qName, type);
         }
      }
      
      //
      // setParent
      //

      if(allInterceptors == 0)
      {
         Position notSkippedParent = stack.notSkippedParent();
         if (notSkippedParent != null)
         {
            ParticleBinding parentParticle = notSkippedParent.getParticle();
            TermBinding parentTerm = parentParticle.getTerm();

            if (notSkippedParent.getValue() != null)
            {
               ParticleHandler handler = this.handler;
               if (parentTerm.isWildcard())
               {
                  ParticleHandler wh = ((WildcardBinding) parentTerm).getWildcardHandler();
                  if (wh != null)
                     handler = wh;
               }
               
               if(repeatableParticleValue != null)
               {
                  repeatableHandler.addTermValue(repeatableParticleValue, o, qName, particle, notSkippedParent.getParticle(), handler);
               }
               else if(notSkippedParent.getRepeatableParticleValue() == null || !notSkippedParent.getParticle().getTerm().isSkip())
               {
                  TermBeforeSetParentCallback beforeSetParent = particle.getTerm().getBeforeSetParentCallback();
                  if(beforeSetParent != null)
                  {
                     UnmarshallingContextImpl ctx = stack.getContext();
                     ctx.parent = notSkippedParent.getValue();
                     ctx.particle = particle;
                     ctx.parentParticle = stack.notSkippedParent().getParticle();
                     o = beforeSetParent.beforeSetParent(o, ctx);
                     ctx.clear();
                  }

                  handler.setParent(notSkippedParent.getValue(), o, qName, particle, notSkippedParent.getParticle());
               }
               else
               {
                  notSkippedParent.getRepeatableHandler().addTermValue(
                        notSkippedParent.getRepeatableParticleValue(),
                        o, qName, particle,
                        notSkippedParent.getParticle(), handler);
               }
            }
            else if (parentTerm.isWildcard() && stack.size() > 1)
            {
               // the parent has anyType, so it gets the value of its child
               for (int i = stack.size() - 2; i >= 0; --i)
               {
                  Position peeked = stack.parent(i);
                  peeked.setValue(o);
                  if (peeked.isElement())
                     break;
               }

               if (trace)
                  log.trace("Value of " + qName + " " + o + " is promoted as the value of its parent element.");
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
            interceptor.add(parent, o, qName);
            o = parent;
         }

         for(int i = localInterceptors.size() - 1; i >= 0; --i)
         {
            ElementInterceptor interceptor = localInterceptors.get(i);
            Object parent = stack.pop().getValue();
            interceptor.add(parent, o, qName);
            o = parent;
         }

         // need to push it back to have correct endRepeatableParticle events
         stack.push(popped);
      }
      
      ended = true;
   }
   
   public Position startParticle(QName startName, Attributes atts)
   {
      if(ended)
      {
         if(qName.equals(startName))
         {
            if(particle.isRepeatable())
            {
               Position parentPosition = stack.parent();
               if(parentPosition.repeatTerm(startName, atts))
                  reset();
               else if(repeatableParticleValue != null)
                  endRepeatableParticle(parentPosition);
            }
            else
            {
               reset();
               occurrence = 0;
               endRepeatableParent();
            }
         }
         else if(particle.isRepeatable())
         {
            if(repeatableParticleValue != null)
               endRepeatableParticle(stack.parent());
         }
         return this;
      }
      
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
            initValue(o, null);

            TypeBinding xopIncludeType = new TypeBinding(new QName(Constants.NS_XOP_INCLUDE, "Include"));
            xopIncludeType.setSchemaBinding(schema);
            xopIncludeType.addAttribute(new QName("href"), anyUriType, DefaultHandlers.ATTRIBUTE_HANDLER);
            xopIncludeType.setHandler(new XOPIncludeHandler(parentType, schema.getXopUnmarshaller()));

            ElementBinding xopInclude = new ElementBinding(schema, Constants.QNAME_XOP_INCLUDE, xopIncludeType);
            next = new ElementPosition(startName, new ParticleBinding(xopInclude));
            return next;
         }

         QName typeName = parentType.getQName();
         throw new JBossXBRuntimeException((typeName == null ? "Anonymous" : typeName.toString()) + " type of element "
               + qName + " should be complex and contain " + startName + " as a child element.");
      }

      if (next != null)
      {
         if (particle.getMaxOccursUnbounded() || occurrence < particle.getMinOccurs()
               || occurrence < particle.getMaxOccurs())
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
      {
         throw new JBossXBRuntimeException(startName + " not found as a child of " + qName + " in " + modelGroup);
      }
      else
      {
         flushIgnorableCharacters();

         Object value = o;
         while (next.getNext() != null)
         {
            if (next.getParticle().isRepeatable())
               next.startRepeatableParticle(value);

            stack.push(next);
            value = next.initValue(o, atts);
            next.setParentType(parentType);
            next = next.getNext();
         }

         next.setParentType(parentType);
         if (!next.isElement())
            throw new IllegalStateException();
      }
      return next;
   }
   
   private void endRepeatableParent()
   {
      int stackIndex = stack.size() - 2;
      Position position;
      Position parentPosition = stack.parent();
      while(true)
      {
         if(parentPosition.isElement())
         {
            throw new JBossXBRuntimeException(
               "Failed to start " + qName +
               ": the element is not repeatable, repeatable parent expected to be a model group but got element " +
               ((ElementBinding)parentPosition.getParticle().getTerm()).getQName()
            );
         }

         position = parentPosition;
         if(position.getParticle().isRepeatable())
         {
            ((NonElementPosition)position).endParticle(stackIndex - 1);
            parentPosition = stack.parent(stackIndex - 1);
            position.reset();
            position.initValue(parentPosition.getValue(), null);
            break;
         }

         parentPosition = stack.parent(--stackIndex);
         ((NonElementPosition)position).endParticle(stackIndex);
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
         position = stack.parent(stackIndex);
         position.reset();
         position.initValue(parentPosition.getValue(), null);
      }
   }
   
   public void push(PositionStack stack, Attributes atts, boolean repeated)
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

      Object parent = stack.isEmpty() ? null : (repeated ? stack.parent().getValue() : stack.current().getValue());
      if (!repeated && particle.isRepeatable())
         startRepeatableParticle(parent);

      TypeBinding type = element.getType();
      if (type == null)
         throw new JBossXBRuntimeException("No type for element " + element);

      handler = type.getHandler();
      if (handler == null)
         handler = DefaultHandlers.ELEMENT_HANDLER;

      List<ElementInterceptor> localInterceptors = parentType == null
            ? Collections.<ElementInterceptor>emptyList()
            : parentType.getInterceptors(qName);
      List<ElementInterceptor> interceptors = element.getInterceptors();
      if (interceptors.size() + localInterceptors.size() > 0)
      {
         if (repeated)
            stack.pop();

         NamespaceRegistry nsRegistry = stack.getNamespaceRegistry();
         for (int i = 0; i < localInterceptors.size(); ++i)
         {
            ElementInterceptor interceptor = localInterceptors.get(i);
            parent = interceptor.startElement(parent, qName, type);
            stack.push(qName, particle, parent, handler, parentType);
            interceptor.attributes(parent, qName, type, atts, nsRegistry);
         }

         for (int i = 0; i < interceptors.size(); ++i)
         {
            ElementInterceptor interceptor = interceptors.get(i);
            parent = interceptor.startElement(parent, qName, type);
            stack.push(qName, particle, parent, handler, parentType);
            interceptor.attributes(parent, qName, type, atts, nsRegistry);
         }

         if (repeated)
         {
            // to have correct endRepeatableParticle calls
            stack.push(this);
         }
      }

      if (!repeated)
         stack.push(this);

      String nil = atts.getValue(Constants.NS_XML_SCHEMA_INSTANCE, "nil");
      if (nil == null || !("1".equals(nil) || "true".equals(nil)))
         initValue(parent, atts);
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
}