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

import javax.xml.namespace.QName;
import org.jboss.logging.Logger;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.ObjectLocalMarshaller;
import org.jboss.xb.binding.Util;
import org.xml.sax.Attributes;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class WildcardBinding
   extends TermBinding implements NonElementTermBinding
{
   private static final Logger log = Logger.getLogger(WildcardBinding.class);

   private static final short PC_LAX = 3;
   private static final short PC_SKIP = 2;
   private static final short PC_STRICT = 1;

   private SchemaBindingResolver schemaResolver;
   private short pc = PC_STRICT;

   private ParticleHandler unresolvedElementHandler;
   private CharactersHandler unresolvedCharactersHandler;
   private ObjectLocalMarshaller unresolvedMarshaller;
   private ParticleHandler wildcardHandler;


   public WildcardBinding(SchemaBinding schema)
   {
      super(schema);
   }

   public SchemaBindingResolver getSchemaResolver()
   {
      return schemaResolver;
   }

   public void setSchemaResolver(SchemaBindingResolver schemaResolver)
   {
      this.schemaResolver = schemaResolver;
   }

   public short getProcessContents()
   {
      return pc;
   }

   public void setProcessContents(short pc)
   {
      this.pc = pc;
      if(pc != PC_LAX && pc != PC_SKIP && pc != PC_STRICT)
      {
         throw new JBossXBRuntimeException("Unexpected value for process contents: " + pc);
      }
   }

   public boolean isProcessContentsLax()
   {
      return pc == PC_LAX;
   }

   public boolean isProcessContentsSkip()
   {
      return pc == PC_SKIP;
   }

   public boolean isProcessContentsStrict()
   {
      return pc == PC_STRICT;
   }

   public void setWildcardHandler(ParticleHandler wildcardHandler)
   {
      this.wildcardHandler = wildcardHandler;
   }

   public ParticleHandler getWildcardHandler()
   {
      return wildcardHandler;
   }

   public ParticleHandler getUnresolvedElementHandler()
   {
      return unresolvedElementHandler;
   }

   public void setUnresolvedElementHandler(ParticleHandler unresolvedElementHandler)
   {
      this.unresolvedElementHandler = unresolvedElementHandler;
   }

   public CharactersHandler getUnresolvedCharactersHandler()
   {
      return unresolvedCharactersHandler;
   }

   public void setUnresolvedCharactersHandler(CharactersHandler unresolvedCharactersHandler)
   {
      this.unresolvedCharactersHandler = unresolvedCharactersHandler;
   }

   public ObjectLocalMarshaller getUnresolvedMarshaller()
   {
      return unresolvedMarshaller;
   }
   
   public void setUnresolvedMarshaller(ObjectLocalMarshaller marshaller)
   {
      this.unresolvedMarshaller = marshaller;
   }
   
   public ElementBinding getElement(QName qName, Attributes attrs)
   {
      if(pc == PC_SKIP)
      {
         return getUnresolvedElement(qName, false);
      }

      ElementBinding element = null;
      // first, look into the own schema
      if(schema != null)
      {
         element = schema.getElement(qName);
      }

      if(element == null)
      {
         SchemaBindingResolver resolver = schemaResolver;
         if(resolver == null && schema != null)
         {
            resolver = schema.getSchemaResolver();
         }

         if(resolver != null)
         {
            // this is wildcard handling
            String schemaLocation = attrs == null ? null : Util.getSchemaLocation(attrs, qName.getNamespaceURI());
            SchemaBinding schema = resolver.resolve(qName.getNamespaceURI(), null, schemaLocation);
            if(schema != null)
            {
               element = schema.getElement(qName);
            }
         }
      }

      if(element == null && pc == PC_LAX)
      {
         element = getUnresolvedElement(qName, false);
      }

      return element;
   }

   /**
    * todo: this method is called for each unresolved element TWICE currently
    * because getElement() is called TWICE. Look into fixing this!
    *
    * @param qName
    * @return
    */
   private ElementBinding getUnresolvedElement(QName qName, boolean required)
   {
      if(log.isTraceEnabled())
      {
         log.trace(
            "getUnresolvedElement for " + qName + ", required=" + required
            + ", unresolvedElementHandler=" + unresolvedElementHandler
         );
      }

      if(unresolvedElementHandler == null)
      {
         if(required)
         {
            throw new JBossXBRuntimeException("Schema could not be resolved for wildcard content element " +
               qName +
               " and particle handler for unresolved wildcard content elements is not initialized."
            );
         }
         else
         {
            // todo this stuff could be cached
            ParticleBinding particle = new ParticleBinding(this);
            SequenceBinding sequence = new SequenceBinding(schema);
            sequence.addParticle(particle);

            TypeBinding type = new TypeBinding();
            type.setParticle(new ParticleBinding(sequence));

            ElementBinding element = new ElementBinding(schema, qName, type);
            // this is unresolved element we don't care about
            element.setSkip(Boolean.TRUE);
            return element;
         }
      }

      // TODO this stuff could be cached
      // the 'this' wildcard could be reused
      // the reason it is overridden is to eliminate its wildcardHandler
      // which is not initialized in the new one
      WildcardBinding unresolvedWildcard = new WildcardBinding(schema);
      unresolvedWildcard.pc = PC_LAX;
      unresolvedWildcard.schemaResolver = schemaResolver;
      unresolvedWildcard.unresolvedCharactersHandler = unresolvedCharactersHandler;
      unresolvedWildcard.unresolvedElementHandler = unresolvedElementHandler;
      
      ParticleBinding particle = new ParticleBinding(unresolvedWildcard);
      SequenceBinding sequence = new SequenceBinding(schema);
      sequence.addParticle(particle);

      TypeBinding type = new TypeBinding();
      type.setHandler(unresolvedElementHandler);
      type.setSimpleType(unresolvedCharactersHandler);
      type.setParticle(new ParticleBinding(sequence, 1, 0, true));

      ElementBinding element = new ElementBinding(schema, qName, type);
      element.setRepeatableHandler(repeatableHandler);
      return element;
   }

   public NonElementPosition newPosition(QName qName, Attributes attrs, ParticleBinding particle)
   {
      ElementBinding wildcardContent = getElement(qName, attrs);
      if(wildcardContent != null)
         return new WildcardPosition(qName, particle, new ParticleBinding(wildcardContent));
      return null;
   }
   
   public boolean isSkip()
   {
      return skip == null ? true : skip.booleanValue();
   }

   public boolean isModelGroup()
   {
      return false;
   }

   public boolean isWildcard()
   {
      return true;
   }

   public boolean isElement()
   {
      return false;
   }
   
   public String toString()
   {
      String processContent;
      switch(pc)
      {
         case PC_LAX:
            processContent = "lax";
            break;
         case PC_SKIP:
            processContent = "skip";
            break;
         case PC_STRICT:
            processContent = "strict";
            break;
         default:
            throw new IllegalStateException("Unexpected processContents value: " + pc);
      }
      return "wildcard processContents=" + processContent;
   }
   
   private final class WildcardPosition extends NonElementPosition
   {
      protected WildcardPosition(QName name, ParticleBinding particle, ParticleBinding currentParticle)
      {
         super(name, particle, currentParticle);
      }

      @Override
      protected NonElementPosition startElement(QName name, Attributes atts, boolean required)
      {
         // if positioned try repeating
         if(currentParticle != null && repeatTerm(qName, atts))
            return this;

         ElementBinding wildcardContent = getElement(name, atts);
         if(wildcardContent != null)
         {
            currentParticle = new ParticleBinding(wildcardContent);
            occurrence = 1;
            return this;
         }

         return null;
      }
      
   }
}
