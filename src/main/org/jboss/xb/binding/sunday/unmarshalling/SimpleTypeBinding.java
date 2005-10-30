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

import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.sunday.unmarshalling.impl.runtime.RtElementHandler;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class SimpleTypeBinding
   extends TypeBinding
{
   private static final RtElementHandler DEFAULT_HANDLER = new RtElementHandler()
   {
      public Object startParticle(Object parent, QName qName, ParticleBinding particle)
      {
         return null;
      }
   };

   public SimpleTypeBinding()
   {
      setDefaultHandler();
   }

   public SimpleTypeBinding(QName qName)
   {
      super(qName);
      setDefaultHandler();
   }

   public SimpleTypeBinding(QName qName, ValueAdapter valueAdapter)
   {
      super(qName);
      setDefaultHandler();
      setValueAdapter(valueAdapter);
   }

   public SimpleTypeBinding(QName qName, CharactersHandler simple)
   {
      super(qName, simple);
      setDefaultHandler();
   }

   private void setDefaultHandler()
   {
      setHandler(DEFAULT_HANDLER);
   }

   public AttributeBinding addAttribute(QName name, TypeBinding type, AttributeHandler handler)
   {
      throw new JBossXBRuntimeException("Simple types can't have attributes.");
   }

   public void addElement(QName qName, ElementBinding binding)
   {
      throw new JBossXBRuntimeException("Simple types can't have child elements.");
   }
}
