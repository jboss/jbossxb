/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata.unmarshalling;

import javax.xml.namespace.QName;


/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface BasicElementBinding
{
   QName getElementName();
   Class getJavaType();
   DocumentBinding getDocument();
   ElementBinding getElement(QName elementName);
   AttributeBinding getAttribute(QName attributeName);
}
