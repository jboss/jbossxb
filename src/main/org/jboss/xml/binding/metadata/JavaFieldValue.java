/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata;

import org.jboss.xml.binding.metadata.ArrayValue;
import org.jboss.xml.binding.metadata.CollectionValue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface JavaFieldValue
   extends JavaValue
{
   void bindAsField(JavaValue value, String fieldName);
   void bindAsCollectionItem(CollectionValue value);
   void bindAsArrayItem(ArrayValue value);
   MapEntryValue bindAsMapEntry(MapValue value);
   void bindAsMapKey(MapEntryValue value);
   void bindAsMapValue(MapEntryValue value);

   Field getField();
   Method getGetter();
   Method getSetter();
   Class getFieldType();
}
