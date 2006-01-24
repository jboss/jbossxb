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
import org.jboss.xb.binding.Util;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.xml.sax.Attributes;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class WildcardBinding
   extends TermBinding
{
   private static final short PC_LAX = 3;
   private static final short PC_SKIP = 2;
   private static final short PC_STRICT = 1;

   private QName qName;
   private SchemaBindingResolver schemaResolver;
   private short pc;

   public WildcardBinding(SchemaBinding schema)
   {
      super(schema);
   }

   public QName getQName()
   {
      return qName;
   }

   public void setQName(QName qName)
   {
      this.qName = qName;
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

   public ElementBinding getElement(QName qName, Attributes attrs)
   {
      // todo: this is quick & dirty. and this method is currently called TWICE for each element...
      if(pc == PC_SKIP)
      {
         WildcardBinding wc = new WildcardBinding(schema);
         wc.setProcessContents(pc);
         ParticleBinding particle = new ParticleBinding(wc);
         TypeBinding type = new TypeBinding();
         SequenceBinding sequence = new SequenceBinding(schema);
         sequence.addParticle(particle);
         type.setParticle(new ParticleBinding(sequence));
         return new ElementBinding(schema, qName, type);
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
         WildcardBinding wc = new WildcardBinding(schema);
         wc.setProcessContents(pc);
         ParticleBinding particle = new ParticleBinding(wc);
         TypeBinding type = new TypeBinding();
         SequenceBinding sequence = new SequenceBinding(schema);
         sequence.addParticle(particle);
         type.setParticle(new ParticleBinding(sequence));
         element = new ElementBinding(schema, qName, type);         
      }

      return element;
   }

   public boolean isSkip()
   {
      return skip == null ? false : skip.booleanValue();
   }

   public boolean isModelGroup()
   {
      return false;
   }

   public boolean isWildcard()
   {
      return true;
   }
}
