/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface ElementTypeBinding
{
   AttributeBinding addAttribute(QName name);

   void addAttribute(QName name, AttributeBinding binding);

   AttributeBinding getAttribute(QName name);

   boolean hasAttributes();

   void pushAttributeHandler(QName name, AttributeHandler handler);

   void setTextContent(TextContentBinding binding);

   TextContentBinding getTextContent();

   void pushTextContentHandler(TextContentHandler handler);

   ElementBinding addElement(QName name);

   void addElement(QName name, ElementBinding binding);

   ElementBinding getElement(QName name);
}
