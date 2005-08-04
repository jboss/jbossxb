/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
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
      public Object startElement(Object parent, QName qName, ElementBinding element)
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
