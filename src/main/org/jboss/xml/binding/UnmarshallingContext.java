/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

import org.apache.xerces.xs.XSTypeDefinition;

import javax.xml.namespace.QName;
import java.util.Map;

/**
 * An interface for content navigation. At the moment it has only one method to get child's content.
 * But it could also implement XPath navigation.
 *
 * @version <tt>$Revision$</tt>
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 */
public interface UnmarshallingContext
{
   Map getPrefixToNamespaceMap();
   
   /**
    * @param prefix  the prefix to resolve
    * @return the namespace URI the prefix was mapped to
    */
   String resolveNamespacePrefix(String prefix);

   /** Construct a QName from a value
    * @param value A value that is of the form [prefix:]localpart
    */
   QName resolveQName(String value);

   String getChildContent(String namespaceURI, String qName);

   XSTypeDefinition getType();
}