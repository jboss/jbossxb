/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata;

import org.jboss.xml.binding.metadata.unmarshalling.BindingCursor;

import java.util.LinkedList;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class CursorImpl
   implements BindingCursor
{
   private final XmlDocument doc;
   private final LinkedList stack = new LinkedList();

   public CursorImpl(XmlDocument doc)
   {
      this.doc = doc;
   }

   public void startElement(String namespaceURI, String localName)
   {
      XmlElement element;
      if(stack.isEmpty())
      {
         XmlNamespace ns = doc.getNamespace(namespaceURI);
         element = ns.getElement(localName);
      }
      else
      {
         element = (XmlElement)stack.getLast();
         element = ((XmlComplexType)element.getType()).getElement(namespaceURI, localName);
      }
      stack.addLast(element);
   }

   public void endElement(String namespaceURI, String localName)
   {
      stack.removeLast();
   }

   public Object getElementBinding()
   {
      return (XmlElement)stack.getLast();
   }

   public Object getParentElementBinding()
   {
      return stack.size() - 2 >= 0 ? stack.get(stack.size() - 2) : null;
   }
}
