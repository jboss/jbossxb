/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

import org.apache.xerces.xs.XSTypeDefinition;
import org.jboss.xml.binding.metadata.unmarshalling.BasicElementBinding;

import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import java.util.Map;
import java.util.Iterator;

/**
 * An interface for content navigation. At the moment it has only one method to get child's content.
 * But it could also implement XPath navigation.
 *
 * @version <tt>$Revision$</tt>
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 */
public interface UnmarshallingContext
{
   /** Construct a QName from a value
    * @param value A value that is of the form [prefix:]localpart
    */
   QName resolveQName(String value);

   /**
    * @return  all the known namespace URIs
    */
   Iterator getNamespaceURIs();

   /**
    * @return  NamespaceContext instance
    */
   NamespaceContext getNamespaceContext();

   BasicElementBinding getMetadata();

   /**
    * Returns child's content.
    * todo consider deprecating this method
    * @param namespaceURI
    * @param qName
    * @return
    */
   String getChildContent(String namespaceURI, String qName);

   /**
    * @return current element's type definition or null if this info is not available
    */
   XSTypeDefinition getType();
}