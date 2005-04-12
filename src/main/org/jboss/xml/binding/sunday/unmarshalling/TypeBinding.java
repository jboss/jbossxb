/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class TypeBinding
{
   private Map elements = Collections.EMPTY_MAP;
   private ElementHandler handler = DefaultElementHandler.INSTANCE;

   // Schema navigation API

   public ElementBinding getElement(QName name)
   {
      return (ElementBinding)elements.get(name);
   }

   public ElementBinding addElement(QName name, TypeBinding type)
   {
      ElementBinding el = new ElementBinding(type);
      switch(elements.size())
      {
         case 0:
            elements = Collections.singletonMap(name, el);
            break;
         case 1:
            elements = new HashMap(elements);
         default:
            elements.put(name, el);
      }
      return el;
   }

   public Object startElement(Object parent, QName qName)
   {
      return handler.startElement(parent, qName);
   }

   public void attributes(Object o, QName elementName, Attributes attrs)
   {
      handler.attributes(o, elementName, attrs);
   }

   public void characters(Object o, QName qName, String text)
   {
      handler.characters(o, qName, text);
   }

   public Object endElement(Object parent, Object o, QName qName)
   {
      return handler.endElement(o, qName);
   }

   public void setHandler(ElementHandler handler)
   {
      this.handler = handler;
   }

   public void pushElementHandler(QName qName, ElementHandler handler)
   {
      ElementBinding el = getElement(qName);
      if(el == null)
      {
         el = addElement(qName, new TypeBinding());
      }
      el.pushHandler(handler);
   }
}
