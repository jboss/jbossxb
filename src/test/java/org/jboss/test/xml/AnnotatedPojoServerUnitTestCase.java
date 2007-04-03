/*
  * JBoss, Home of Professional Open Source
  * Copyright 2005, JBoss Inc., and individual contributors as indicated
  * by the @authors tag. See the copyright.txt in the distribution for a
  * full listing of individual contributors.
  *
  * This is free software; you can redistribute it and/or modify it
  * under the terms of the GNU Lesser General Public License as
  * published by the Free Software Foundation; either version 2.1 of
  * the License, or (at your option) any later version.
  *
  * This software is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  * Lesser General Public License for more details.
  *
  * You should have received a copy of the GNU Lesser General Public
  * License along with this software; if not, write to the Free
  * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */
package org.jboss.test.xml;

import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 37728 $</tt>
 */
public class AnnotatedPojoServerUnitTestCase
   extends PojoServerTestBase
{
   public AnnotatedPojoServerUnitTestCase(String localName)
   {
      super(localName);
   }

   protected SchemaBinding getSchemaBinding()
   {
      SchemaBinding cursor = readXsd();
/** TODO
      TypeBinding namedValueType = cursor.getType(namedValueTypeQName);
      TypeBinding valueWithClassType = cursor.getType(valueWithClassTypeQName);
      TypeBinding listType = cursor.getType(listTypeQName);
      TypeBinding mapType = cursor.getType(mapTypeQName);
      TypeBinding propsType = cursor.getType(propsTypeQName);

      //
      // add handlers
      //

      namedValueType.setSimpleType(new CharactersHandler()
      {
         public Object unmarshal(QName qName,
                                 TypeBinding typeBinding,
                                 NamespaceContext nsCtx,
                                 org.jboss.xb.binding.metadata.ValueMetaData valueMetaData,
                                 String value)
         {
            return value == null ? null : new StringValueMetaData(value);
         }

         public void setValue(QName qName, ElementBinding element, Object owner, Object value)
         {
            AbstractPropertyMetaData prop = (AbstractPropertyMetaData)owner;
            prop.setValue((ValueMetaData)value);
         }
      }
      );

      namedValueType.pushInterceptor(parameterQName, new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            // todo how to add?
            //AbstractPropertyMetaData prop = (AbstractPropertyMetaData)parent;
            //AbstractParameterMetaData param = (AbstractParameterMetaData)child;
            //prop.setValue(param.getType(), param);
         }
      }
      );
      namedValueType.pushInterceptor(propertyQName, new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            // todo how to add?
            AbstractPropertyMetaData propParent = (AbstractPropertyMetaData)parent;
            AbstractPropertyMetaData propChild = (AbstractPropertyMetaData)child;
            propParent.setValue(propChild.getName(), propChild);
         }
      }
      );
      namedValueType.pushInterceptor(dependencyQName, new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            AbstractPropertyMetaData prop = (AbstractPropertyMetaData)parent;
            AbstractDependencyValueMetaData dependency = (AbstractDependencyValueMetaData)child;
            prop.setValue(dependency);
         }
      }
      );
      namedValueType.pushInterceptor(beanQName, new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            // todo how to add?
            AbstractPropertyMetaData prop = (AbstractPropertyMetaData)parent;
            AbstractBeanMetaData bean = (AbstractBeanMetaData)child;
            prop.setValue(bean.getName(), bean);
         }
      }
      );
      namedValueType.pushInterceptor(listQName, new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName qName)
         {
            AbstractPropertyMetaData prop = (AbstractPropertyMetaData)parent;
            AbstractListMetaData list = (AbstractListMetaData)child;
            prop.setValue(list);
         }
      }
      );
      namedValueType.pushInterceptor(setQName, new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName qName)
         {
            AbstractPropertyMetaData prop = (AbstractPropertyMetaData)parent;
            AbstractSetMetaData set = (AbstractSetMetaData)child;
            prop.setValue(set);
         }
      }
      );
      namedValueType.pushInterceptor(mapQName, new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName qName)
         {
            AbstractPropertyMetaData prop = (AbstractPropertyMetaData)parent;
            AbstractMapMetaData map = (AbstractMapMetaData)child;
            prop.setValue(map);
         }
      }
      );
      namedValueType.pushInterceptor(propsQName, new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName qName)
         {
            AbstractPropertyMetaData prop = (AbstractPropertyMetaData)parent;
            Map children = (Map)child;
            for(Iterator i = children.entrySet().iterator(); i.hasNext();)
            {
               Map.Entry entry = (Map.Entry)i.next();
               prop.setValue((String)entry.getKey(), entry.getValue());
            }
         }
      }
      );

      valueWithClassType.setSimpleType(new CharactersHandler()
      {
         public Object unmarshal(QName qName,
                                 TypeBinding typeBinding,
                                 NamespaceContext nsCtx,
                                 org.jboss.xb.binding.metadata.ValueMetaData valueMetaData,
                                 String value)
         {
            return value == null ? null : new StringValueMetaData(value);
         }

         public void setValue(QName qName, ElementBinding element, Object owner, Object value)
         {
            AbstractParameterMetaData param = (AbstractParameterMetaData)owner;
            param.setValue((ValueMetaData)value);
         }
      }
      );
      valueWithClassType.pushInterceptor(dependencyQName, new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName qName)
         {
            AbstractParameterMetaData param = (AbstractParameterMetaData)parent;
            AbstractDependencyValueMetaData dependency = (AbstractDependencyValueMetaData)child;
            param.setValue(dependency);
         }
      }
      );

      listType.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object root, QName qName, ElementBinding element)
         {
            AbstractCollectionMetaData col;
            if("set".equals(qName.getLocalPart()))
            {
               col = new AbstractSetMetaData();
            }
            else
            {
               col = new AbstractListMetaData();
            }
            return col;
         }

         public void attributes(Object o, QName qName, ElementBinding element, Attributes attrs, NamespaceContext nsCtx)
         {
            AbstractCollectionMetaData col = (AbstractCollectionMetaData)o;
            for(int i = 0; i < attrs.getLength(); ++i)
            {
               String lName = attrs.getLocalName(i);
               if("class".equals(lName))
               {
                  // todo what should I do with this?
               }
            }
         }
      }
      );
      listType.pushInterceptor(valueQName, new DefaultElementInterceptor()
      {
         public void characters(Object o, QName qName, TypeBinding type, NamespaceContext nsCtx, String text)
         {
            AbstractCollectionMetaData col = (AbstractCollectionMetaData)o;
            col.add(text);
         }
      }
      );

      propsType.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object parent, QName name, ElementBinding element)
         {
            return new HashMap();
         }
      }
      );
      propsType.pushInterceptor(propsEntryQName, new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            Map map = (Map)parent;
            Object[] arr = (Object[])child;
            map.put(arr[0], arr[1]);
         }
      }
      );
      propsType.pushInterceptor(propsEntryQName, new DefaultElementInterceptor()
      {
         public Object startElement(Object parent, QName qName, TypeBinding type)
         {
            return new Object[2];
         }

         public void attributes(Object o, QName name, TypeBinding type, Attributes attrs, NamespaceContext nsCtx)
         {
            Object[] arr = (Object[])o;
            for(int i = 0; i < attrs.getLength(); ++i)
            {
               if("name".equals(attrs.getLocalName(i)))
               {
                  arr[0] = attrs.getValue(i);
               }
            }
         }

         public void characters(Object o, QName name, TypeBinding type, NamespaceContext nsCtx, String text)
         {
            Object[] arr = (Object[])o;
            arr[1] = text;
         }
      }
      );

      mapType.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object parent, QName name, ElementBinding element)
         {
            return new AbstractMapMetaData();
         }
      }
      );
      mapType.pushInterceptor(mapEntryQName, new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            AbstractMapMetaData map = (AbstractMapMetaData)parent;
            AbstractPropertyMetaData prop = (AbstractPropertyMetaData)child;
            map.put(prop.getName(), prop);
         }
      }
      );
*/
      return cursor;
   }

   protected String getXsd()
   {
      return "xml/annotated-bean-deployer_1_0.xsd";
   }
}
