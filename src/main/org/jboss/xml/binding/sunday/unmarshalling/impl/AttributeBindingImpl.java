/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling.impl;

import org.jboss.xml.binding.sunday.unmarshalling.AttributeBinding;
import org.jboss.xml.binding.sunday.unmarshalling.AttributeHandler;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class AttributeBindingImpl
   implements AttributeBinding
{
   private AttributeHandler handler;

   public AttributeBinding pushHandler(AttributeHandler handler)
   {
      if(this.handler != null)
      {
         handler.setNext(this.handler);
      }
      this.handler = handler;
      return this;
   }

   public void set(Object owner, String data, QName name)
   {
      handler.set(owner, data, name);
   }
}
