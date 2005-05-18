/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling;

import org.jboss.xml.binding.metadata.JaxbProperty;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class AttributeBinding
{
   private final TypeBinding type;
   private AttributeHandler handler;
   private JaxbProperty jaxbProperty;

   public AttributeBinding(TypeBinding type, AttributeHandler handler)
   {
      this.type = type;
      this.handler = handler;
   }

   public TypeBinding getType()
   {
      return type;
   }

   public AttributeHandler getHandler()
   {
      return handler;
   }

   public void setHandler(AttributeHandler handler)
   {
      this.handler = handler;
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
