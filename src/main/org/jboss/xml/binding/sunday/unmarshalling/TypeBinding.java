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
import javax.xml.namespace.NamespaceContext;
import org.xml.sax.Attributes;
import org.jboss.xml.binding.metadata.JaxbClass;
import org.jboss.xml.binding.metadata.JaxbJavaType;
import org.jboss.xml.binding.metadata.JaxbProperty;
import org.jboss.xml.binding.sunday.unmarshalling.impl.runtime.RtElementHandler;
import org.jboss.xml.binding.sunday.unmarshalling.impl.runtime.RtCharactersHandler;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class TypeBinding
{
   private final QName qName;
   private Map elements = Collections.EMPTY_MAP;
   private QName arrayItemQName;
   private ElementBinding arrayItem;
   private Map attrs = Collections.EMPTY_MAP;
   private ElementHandler handler = RtElementHandler.INSTANCE;//DefaultElementHandler.INSTANCE;
   private CharactersHandler simpleType;
   private JaxbClass jaxbClass;
   private JaxbJavaType jaxbJavaType;
   private JaxbProperty jaxbProperty;
   private SchemaBinding schemaBinding; // todo it's optional for now...

   public TypeBinding()
   {
      this(null);
   }

   public TypeBinding(QName qName)
   {
      //this(qName, CharactersHandler.DEFAULT);
      this(qName, RtCharactersHandler.INSTANCE);
   }

   public TypeBinding(QName qName, CharactersHandler simple)
   {
      this.qName = qName;
      this.simpleType = simple;
   }

   public QName getQName()
   {
      return qName;
   }

   public ElementBinding getElement(QName name)
   {
      return (ElementBinding)elements.get(name);
   }

   public void addElement(QName qName, ElementBinding binding)
   {
      switch(elements.size())
      {
         case 0:
            elements = Collections.singletonMap(qName, binding);
            if(binding.isMultiOccurs())
            {
               arrayItem = binding;
               arrayItemQName = qName;
            }
            break;
         case 1:
            elements = new HashMap(elements);
            arrayItem = null;
            arrayItemQName = null;
         default:
            elements.put(qName, binding);
      }
   }

   public ElementBinding addElement(QName name, TypeBinding type)
   {
      ElementBinding el = new ElementBinding(type);
      addElement(name, el);
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

   public AttributeBinding getAttribute(QName qName)
   {
      return (AttributeBinding)attrs.get(qName);
   }

   public AttributeBinding addAttribute(QName name, AttributeHandler handler)
   {
      return addAttribute(name, new TypeBinding(), handler);
   }

   public AttributeBinding addAttribute(QName name, TypeBinding type, AttributeHandler handler)
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

   public CharactersHandler getSimpleType()
   {
      return simpleType;
   }

   public void setSimpleType(CharactersHandler simpleType)
   {
      this.simpleType = simpleType;
   }

   public Object startElement(Object parent, QName qName)
   {
      return handler.startElement(parent, qName, this);
   }

   public void attributes(Object o, QName elementName, Attributes attrs, NamespaceContext nsCtx)
   {
      handler.attributes(o, elementName, this, attrs, nsCtx);
   }

   public Object endElement(Object parent, Object o, QName qName)
   {
      return handler.endElement(o, qName, this);
   }

   public void setHandler(ElementHandler handler)
   {
      this.handler = handler;
   }

   public ElementHandler getHandler()
   {
      return handler;
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

   public boolean isSimple()
   {
      return elements.isEmpty() && attrs.isEmpty();
   }

   public boolean hasSimpleContent()
   {
      return elements.isEmpty();
   }

   public boolean isArrayWrapper()
   {
      return arrayItem != null;
   }

   public ElementBinding getArrayItem()
   {
      return arrayItem;
   }

   public QName getArrayItemQName()
   {
      return arrayItemQName;
   }

   public JaxbClass getJaxbClass()
   {
      return jaxbClass;
   }

   public void setJaxbClass(JaxbClass jaxbClass)
   {
      this.jaxbClass = jaxbClass;
   }

   public SchemaBinding getSchemaBinding()
   {
      return schemaBinding;
   }

   public void setSchemaBinding(SchemaBinding schemaBinding)
   {
      this.schemaBinding = schemaBinding;
   }

   public void setJaxbJavaType(JaxbJavaType jaxbJavaType)
   {
      this.jaxbJavaType = jaxbJavaType;
   }

   public JaxbJavaType getJaxbJavaType()
   {
      return jaxbJavaType;
   }

   public JaxbProperty getJaxbProperty()
   {
      return jaxbProperty;
   }

   public void setJaxbProperty(JaxbProperty jaxbProperty)
   {
      this.jaxbProperty = jaxbProperty;
   }
}
