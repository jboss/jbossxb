/*
 * JBoss, the OpenSource webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling;

import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import org.xml.sax.Attributes;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface ElementHandler
{
   Object startElement(Object parent, QName elementName, TypeBinding type);

   void attributes(Object o, QName elementName, TypeBinding type, Attributes attrs, NamespaceContext nsCtx);

   void characters(Object o,
                   QName elementName,
                   TypeBinding type,
                   NamespaceContext nsCtx,
                   String text);

   Object endElement(Object o, QName elementName, TypeBinding type);

   void setParent(Object parent, Object o, QName qName);
}
