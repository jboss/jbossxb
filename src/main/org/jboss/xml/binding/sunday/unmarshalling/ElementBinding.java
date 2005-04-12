/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class ElementBinding
{
   private List elementHandlers = Collections.EMPTY_LIST;

   private final TypeBinding typeBinding;

   public ElementBinding(TypeBinding typeBinding)
   {
      this.typeBinding = typeBinding;
   }

   public List getElementHandlers()
   {
      return elementHandlers;
   }

   public TypeBinding getTypeBinding()
   {
      return typeBinding;
   }

   public void pushHandler(ElementHandler handler)
   {
      switch(elementHandlers.size())
      {
         case 0:
            elementHandlers = Collections.singletonList(handler);
            break;
         case 1:
            elementHandlers = new ArrayList(elementHandlers);
         default:
            elementHandlers.add(handler);

      }
   }
}
