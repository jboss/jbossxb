/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling;

import javax.xml.namespace.QName;
import org.jboss.xml.binding.JBossXBRuntimeException;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class SimpleTypeBinding
   extends TypeBinding
{
   public SimpleTypeBinding()
   {
      setDefaultHandler();
   }

   public SimpleTypeBinding(QName qName)
   {
      super(qName);
      setDefaultHandler();
   }

   public SimpleTypeBinding(QName qName, CharactersHandler simple)
   {
      super(qName, simple);
      setDefaultHandler();
   }

   private void setDefaultHandler()
   {
      setHandler(new DefaultElementHandler(){
         public Object startElement(Object parent, QName qName, TypeBinding type)
         {
            return null;
         }
      });
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
