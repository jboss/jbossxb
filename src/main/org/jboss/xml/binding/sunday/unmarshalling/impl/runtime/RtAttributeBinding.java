/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling.impl.runtime;

import org.jboss.xml.binding.sunday.unmarshalling.AttributeBinding;
import org.jboss.xml.binding.sunday.unmarshalling.AttributeHandler;
import org.jboss.xml.binding.sunday.unmarshalling.impl.AttributeBindingImpl;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class RtAttributeBinding
   extends AttributeBindingImpl
{
   public RtAttributeBinding()
   {
      pushHandler(new RtAttributeHandler());
   }

   public AttributeBinding pushHandler(AttributeHandler handler)
   {
      super.pushHandler(handler);
      return this;
   }

   public void set(Object parent, String data, QName name)
   {
      super.set(parent, data, name);
   }
}
