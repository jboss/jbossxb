/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding.metadata.marshalling;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface FieldValueBinding
{
   int SIMPLE_VALUE = 0;
   int FINAL_CLASS = 1;
   int NON_FINAL_CLASS = 2;
   int COLLECTION = 4;

   FieldBinding getFieldBinding();
   int getCategory();
   Class getJavaClass();
}
