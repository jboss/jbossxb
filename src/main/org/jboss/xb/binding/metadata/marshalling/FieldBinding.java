/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding.metadata.marshalling;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface FieldBinding
   extends FieldGroupBinding
{
   String getFieldName();
   Field getField();
   Method getGetter();
   Class getFieldType();
   FieldValueBinding getValueBinding();
}
