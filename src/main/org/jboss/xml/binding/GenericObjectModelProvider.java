/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;


/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface GenericObjectModelProvider
   extends ObjectModelProvider
{
   Object getChildren(Object o, String namespaceURI, String localName);

   Object getElementValue(Object o, String namespaceURI, String localName);

   Object getAttributeValue(Object o, String namespaceURI, String localName);
}
