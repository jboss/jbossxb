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
   public DelegatingElementBinding(DelegatingDocumentBinding doc, ElementBinding delegate)
   {
      super(doc, delegate);
   }

   public Field getField()
   {
      return getLatestBinding().getField();
   }

   public Method getGetter()
   {
      return getLatestBinding().getGetter();
   }

   public Method getSetter()
   {
      return getLatestBinding().getSetter();
   }

   public Class getFieldType()
   {
      return getLatestBinding().getFieldType();
   }

   BasicElementBindingImpl cloneLastBinding()
   {
      // todo for now let's rely on this
      AbstractElementBinding latest = (AbstractElementBinding)getLatestBinding();
      return new ElementBindingImpl(latest);
   }

   // Private

   private final ElementBinding getLatestBinding()
   {
      return (ElementBinding)delegates.get(delegates.size() -1);
   }
}
