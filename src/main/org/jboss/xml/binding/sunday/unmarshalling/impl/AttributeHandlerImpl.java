/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling.impl;

import org.jboss.xml.binding.sunday.unmarshalling.AttributeHandler;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class AttributeHandlerImpl implements AttributeHandler
{
   private AttributeHandler next;

   public AttributeHandler getNext()
   {
      return next;
   }

   public void setNext(AttributeHandler handler)
   {
      this.next = handler;
   }

   public void set(Object owner, Object value, QName name)
   {
      invokeNext(owner, value, name);
   }

   protected void invokeNext(Object owner, Object value, QName name)
   {
      if(next != null)
      {
         next.set(owner, value, name);
      }
   }

   protected String trimString(Object str)
   {
      return str == null ? null : ((String)str).trim();
   }
}
