/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata.unmarshalling;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Represents non-top level element binding.
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface ElementBinding
   extends BasicElementBinding
{
   /**
    * @return Java field this element is bound to or null if the field is not available
    *         (in this case getter/setter must be available) or this element represents an item in a Java collection
    */
   Field getField();

   /**
    * @return Getter method of the field this element is bound to or null if the getter is not available
    *         (in this case the field itself must be available) or this element represents an item in a Java collection
    */
   Method getGetter();

   /**
    * @return Setter method of the field this element is bound to or null if the setter is not available
    *         (in this case the field itself must be available) or this element represents an item in a Java collection
    */
   Method getSetter();

   /**
    * @return Java field's type this element is bound to as it appears in the class or null
    *         if this element represents an item in a Java collection
    */
   Class getFieldType();
}
