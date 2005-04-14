/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling;

import javax.xml.namespace.QName;
import org.xml.sax.Attributes;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface ElementHandler
{
   Object startElement(Object parent, QName qName);

   void attributes(Object o, QName elementName, TypeBinding type, Attributes attrs);

   void characters(Object o, QName qName, String text);

   Object endElement(Object o, QName qName);

   void add(Object o, Object child, QName qName);
}
