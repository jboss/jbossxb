/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata.unmarshalling;

import javax.xml.namespace.QName;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface AttributeBinding
{
   QName getAttributeName();
   Class getJavaType();
   Field getField();
   Method getGetter();
   Method getSetter();
   Class getFieldType();
}
