/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling;

import org.xml.sax.Attributes;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface ElementHandler
{
   ElementTypeBinding getElementType();

   AttributeBinding addAttribute(QName name);

   void addAttribute(QName name, AttributeBinding binding);

   AttributeBinding getAttribute(QName name);

   ElementHandler pushAttributeHandler(QName name, AttributeHandler handler);

   void setTextContent(TextContentBinding binding);

   TextContentBinding getTextContent();

   ElementHandler pushTextContentHandler(TextContentHandler handler);

   ElementBinding addElement(QName name);

   void addElement(QName name, ElementBinding binding);

   ElementBinding getElement(QName name);

   void start(Object parent, QName name, Attributes attrs, ElementHandlerCallback callback);

   void end(Object parent, Object child, QName name, String dataContent);
}
