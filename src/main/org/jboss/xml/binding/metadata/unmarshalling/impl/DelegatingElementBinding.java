/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata.unmarshalling.impl;

import org.jboss.xml.binding.metadata.unmarshalling.ElementBinding;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class DelegatingElementBinding
   extends DelegatingBasicElementBinding
   implements ElementBinding
{
   public DelegatingElementBinding(ElementBinding delegate)
   {
      super(delegate);
   }

   public Field getField()
   {
      return getLastBinding().getField();
   }

   public Method getGetter()
   {
      return getLastBinding().getGetter();
   }

   public Method getSetter()
   {
      return getLastBinding().getSetter();
   }

   public Class getFieldType()
   {
      return getLastBinding().getFieldType();
   }

   BasicElementBindingImpl cloneLastBinding()
   {
      ElementBinding last = getLastBinding();
      return new ElementBindingImpl(last);
   }

   // Private

   private final ElementBinding getLastBinding()
   {
      return (ElementBinding)delegates.get(delegates.size() -1);
   }
}
