/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import javax.xml.namespace.QName;
import org.jboss.xml.binding.metadata.unmarshalling.BindingCursor;
import org.jboss.xml.binding.JBossXBRuntimeException;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class DocumentHandler
   implements BindingCursor
{
   private Map tops = new HashMap();
   private LinkedList stack = new LinkedList();

   public ElementBinding getTypeBinding(QName name)
   {
      return (ElementBinding)tops.get(name);
   }

   public ElementBinding addElement(QName name, TypeBinding type)
   {
      ElementBinding element = new ElementBinding(type);
      tops.put(name, element);
      return element;
   }

   public void startElement(String namespaceURI, String localName)
   {
      QName qName = new QName(namespaceURI, localName);
      ElementBinding element;
      if(stack.isEmpty())
      {
         element = (ElementBinding)tops.get(qName);
      }
      else
      {
         TypeBinding parentType = ((ElementBinding)stack.getLast()).getTypeBinding();
         element = parentType.getElement(qName);
      }

      if(element == null)
      {
         throw new JBossXBRuntimeException("Element is not bound: " + qName);
      }

      stack.addLast(element);
   }

   public void endElement(String namespaceURI, String localName)
   {
      stack.removeLast();
   }

   public Object getElementBinding()
   {
      return stack.getLast();
   }

   public Object getParentElementBinding()
   {
      return stack.size() > 1 ? stack.get(stack.size() - 2) : null;
   }
}