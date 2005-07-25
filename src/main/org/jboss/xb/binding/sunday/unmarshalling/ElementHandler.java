/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding.sunday.unmarshalling;

import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import org.xml.sax.Attributes;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface ElementHandler
{
   Object startElement(Object parent, QName elementName, ElementBinding element);

   void attributes(Object o, QName elementName, ElementBinding element, Attributes attrs, NamespaceContext nsCtx);

   Object endElement(Object o, QName elementName, ElementBinding element);

   /**
    * WARN: currently, this method is called only if there are no interceptors in the element binding.
    */
   void setParent(Object parent, Object o, QName qName, ElementBinding element, ElementBinding parentElement);
}
