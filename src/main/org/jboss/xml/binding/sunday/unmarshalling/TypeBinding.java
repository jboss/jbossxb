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
import java.util.Iterator;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class TypeBinding
{
   private final QName qName;
   private Map elements = Collections.EMPTY_MAP;
   private Map attrs = Collections.EMPTY_MAP;
   private ElementHandler handler = DefaultElementHandler.INSTANCE;
   private SimpleTypeBinding simpleType = SimpleTypeBinding.NOOP;

   public TypeBinding()
   {
      this(null);
   }

   public TypeBinding(QName qName)
   {
      this.qName = qName;
   }

   public QName getQName()
   {
      return qName;
   }

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

   public void addGroup(Map group)
   {
      for(Iterator i = group.entrySet().iterator(); i.hasNext();)
      {
         Map.Entry entry = (Map.Entry)i.next();
         QName name = (QName)entry.getKey();
         TypeBinding type = (TypeBinding)entry.getValue();
         addElement(name, type);
      }
   }

   public AttributeBinding getAttributeBinding(QName qName)
   {
      return (AttributeBinding)attrs.get(qName);
   }

   public AttributeBinding addAttribute(QName name, AttributeHandler handler)
   {
      return addAttribute(name, SimpleTypeBinding.NOOP, handler);
   }

   public AttributeBinding addAttribute(QName name, SimpleTypeBinding type, AttributeHandler handler)
   {
      AttributeBinding attr = new AttributeBinding(type, handler);
      switch(attrs.size())
      {
         case 0:
            attrs = Collections.singletonMap(name, attr);
            break;
         case 1:
            attrs = new HashMap(attrs);
         default:
            attrs.put(name, attr);
      }
      return attr;
   }

   public SimpleTypeBinding getSimpleType()
   {
      return simpleType;
   }

   public void setSimpleType(SimpleTypeBinding simpleType)
   {
      this.simpleType = simpleType;
   }

   public Object startElement(Object parent, QName qName)
   {
      return handler.startElement(parent, qName, this);
   }

   public void attributes(Object o, QName elementName, Attributes attrs)
   {
      handler.attributes(o, elementName, this, attrs);
   }

   public void characters(Object o, QName qName, String text)
   {
      handler.characters(o, qName, this, text);
   }

   public Object endElement(Object parent, Object o, QName qName)
   {
      return handler.endElement(o, qName, this);
   }

   public void setHandler(ElementHandler handler)
   {
      this.handler = handler;
   }

   public void pushInterceptor(QName qName, ElementInterceptor interceptor)
   {
      ElementBinding el = getElement(qName);
      if(el == null)
      {
         el = addElement(qName, new TypeBinding());
      }
      el.pushInterceptor(interceptor);
   }
}
