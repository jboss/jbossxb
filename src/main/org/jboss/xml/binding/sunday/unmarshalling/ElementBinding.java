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
import org.jboss.xml.binding.metadata.JaxbProperty;
import org.jboss.xml.binding.metadata.JaxbProperty;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class ElementBinding
{
   private List interceptors = Collections.EMPTY_LIST;

   private final TypeBinding typeBinding;
   private JaxbProperty jaxbProperty;

   public ElementBinding(TypeBinding typeBinding)
   {
      this.typeBinding = typeBinding;
   }

   public List getInterceptors()
   {
      return interceptors;
   }

   public TypeBinding getType()
   {
      return typeBinding;
   }

   public void pushInterceptor(ElementInterceptor interceptor)
   {
      switch(interceptors.size())
      {
         case 0:
            interceptors = Collections.singletonList(interceptor);
            break;
         case 1:
            interceptors = new ArrayList(interceptors);
         default:
            interceptors.add(interceptor);

      }
   }

   public JaxbProperty getJaxbProperty()
   {
      return jaxbProperty;
   }

   public void setJaxbProperty(JaxbProperty jaxbProperty)
   {
      this.jaxbProperty = jaxbProperty;
   }
}
