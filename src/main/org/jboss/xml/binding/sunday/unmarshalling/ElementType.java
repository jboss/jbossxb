/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling;

import org.xml.sax.Attributes;

import javax.xml.namespace.QName;
import java.util.List;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface ElementType
{
   ElementType pushElementHandler(ElementHandler handler);

   List getHandlers();

   AttributeType addAttribute(QName name);

   void addAttribute(QName name, AttributeType binding);

   ElementHandler pushAttributeHandler(QName name, AttributeHandler handler);

   void setTextContentBinding(TextContent binding);

   ElementHandler pushTextContentHandler(TextContentHandler handler);

   ElementType addElement(QName name);

   void addElement(QName name, ElementType binding);

   int start(Object parent, QName name, Attributes attrs, ObjectModelStack stack, int startIndex);

   Object end(Object parent, QName name, ObjectModelStack stack, int startIndex, int endIndex, String dataContent);
}
