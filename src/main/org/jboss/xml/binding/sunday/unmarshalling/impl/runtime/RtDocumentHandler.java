/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling.impl.runtime;

import org.jboss.xml.binding.sunday.unmarshalling.impl.DocumentHandlerImpl;
import org.jboss.xml.binding.sunday.unmarshalling.ElementBinding;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class RtDocumentHandler
   extends DocumentHandlerImpl
{
   private static final RtElementBinding ELEMENT_BINDING = new RtElementBinding();

   public ElementBinding getElement(QName name)
   {
      ElementBinding element = super.getElement(name);
      if(element == null)
      {
         element = ELEMENT_BINDING;
      }
      return element;
   }
}
