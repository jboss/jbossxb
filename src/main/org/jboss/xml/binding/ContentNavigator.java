/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

/**
 * An interface for content navigation. At the moment it has only one method to get child's content.
 * But it could also implement XPath navigation.
 *
 * @version <tt>$Revision$</tt>
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 */
public interface ContentNavigator
{
   /**
    * @param prefix  the prefix to resolve
    * @return the namespace URI the prefix was mapped to
    */
   String resolveNamespacePrefix(String prefix);

   String getChildContent(String namespaceURI, String qName);
}