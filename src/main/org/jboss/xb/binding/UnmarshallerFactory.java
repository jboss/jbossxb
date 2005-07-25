/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public abstract class UnmarshallerFactory
{
   public static UnmarshallerFactory newInstance()
   {
      return new UnmarshallerFactoryImpl();
   }

   public abstract Unmarshaller newUnmarshaller();

   // Inner

   static class UnmarshallerFactoryImpl
      extends UnmarshallerFactory
   {
      public Unmarshaller newUnmarshaller()
      {
         try
         {
            return new UnmarshallerImpl();
         }
         catch(JBossXBException e)
         {
            throw new JBossXBRuntimeException(e.getMessage(), e);
         }
      }
   }
}
