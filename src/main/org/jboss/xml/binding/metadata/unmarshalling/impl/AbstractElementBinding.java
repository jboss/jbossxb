/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata.unmarshalling.impl;

import org.jboss.xml.binding.metadata.unmarshalling.ElementBinding;
import org.jboss.xml.binding.metadata.unmarshalling.BasicElementBinding;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public abstract class AbstractElementBinding
   extends BasicElementBindingImpl
   implements ElementBinding
{
   protected final BasicElementBinding parent;

   public AbstractElementBinding(QName elementName, BasicElementBinding parent)
   {
      super(elementName, parent.getDocument());
      this.parent = parent;
   }

   public BasicElementBinding getParent()
   {
      return parent;
   }
}
