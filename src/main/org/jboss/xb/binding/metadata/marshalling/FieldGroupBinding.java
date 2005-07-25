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
public interface FieldGroupBinding
{
   int SEQUENCE = 0;
   int CHOICE = 1;
   int FIELD = 2;

   int getCategory();
   BaseClassBinding getDeclaringClassBinding();
}
