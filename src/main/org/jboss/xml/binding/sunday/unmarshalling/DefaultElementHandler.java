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
public class DefaultElementHandler
   implements ElementHandler
{
   public static final ElementHandler INSTANCE = new DefaultElementHandler();

   public Object startElement(Object parent, QName qName)
   {
      return parent;
   }

   public void attributes(Object o, QName elementName, Attributes attrs)
   {
   }

   public void characters(Object o, QName qName, String text)
   {
   }

   public Object endElement(Object o, QName qName)
   {
      return o;
   }

   public void add(Object parent, Object child, QName qName)
   {
   }
}
