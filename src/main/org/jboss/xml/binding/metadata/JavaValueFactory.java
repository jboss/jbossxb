/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata;

import org.jboss.xml.binding.metadata.JavaValueFactoryImpl;
import org.jboss.xml.binding.metadata.JavaFieldValue;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public abstract class JavaValueFactory
{
   private final static JavaValueFactory instance = new JavaValueFactoryImpl();

   public static final JavaValueFactory getInstance()
   {
      return instance;
   }

   protected JavaValueFactory()
   {
   }

   public abstract JavaFieldValue newJavaFieldValue(Class javaType);

   public abstract MapEntryValue newMapEntryValue(MapValue mapValue, Class entryType);
}
