/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata;

import org.jboss.xml.binding.metadata.JavaFieldValue;
import org.jboss.xml.binding.metadata.XmlDataContent;
import org.jboss.xml.binding.metadata.XmlNamespace;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface XmlType
{
   short SIMPLE = 0;
   short COMPLEX = 1;

   String getName();

   XmlNamespace getNs();

   int getCategory();

   JavaFieldValue getJavaValue();

   XmlDataContent getDataContent();
}
