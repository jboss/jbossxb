/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata.unmarshalling.impl;

import org.jboss.xml.binding.metadata.unmarshalling.TopElementBinding;


/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class DelegatingTopElementBinding
   extends DelegatingBasicElementBinding
   implements TopElementBinding
{
   public DelegatingTopElementBinding(TopElementBinding delegate)
   {
      super(delegate);
   }

   BasicElementBindingImpl cloneLastBinding()
   {
      TopElementBinding last = (TopElementBinding)delegates.get(delegates.size() - 1);
      return new TopElementBindingImpl(last);
   }
}
