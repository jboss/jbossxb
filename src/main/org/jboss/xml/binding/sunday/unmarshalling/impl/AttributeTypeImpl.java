/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling.impl;

import org.jboss.xml.binding.sunday.unmarshalling.AttributeType;
import org.jboss.xml.binding.sunday.unmarshalling.AttributeHandler;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class AttributeTypeImpl
   implements AttributeType
{
   private AttributeHandler handler;

   public AttributeType pushHandler(AttributeHandler handler)
   {
      if(this.handler == null)
      {
         this.handler = handler;
      }
      else
      {
         this.handler.setNext(handler);
      }
      return this;
   }

   public void set(Object owner, String data, QName name)
   {
      handler.set(owner, data, name);
   }
}
