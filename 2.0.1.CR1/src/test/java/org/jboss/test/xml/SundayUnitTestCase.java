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

import junit.framework.TestCase;

import org.jboss.xb.binding.Immutable;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.sunday.unmarshalling.CharactersHandler;
import org.jboss.xb.binding.sunday.unmarshalling.DefaultElementHandler;
import org.jboss.xb.binding.sunday.unmarshalling.DefaultElementInterceptor;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ElementInterceptor;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.test.xml.config.Config;
import org.jboss.test.xml.config.Config.Bean;
import org.jboss.test.xml.config.Config.ConfigAttr;
import org.jboss.test.xml.config.Config.Depends;
import org.jboss.test.xml.config.Config.ListValue;
import org.xml.sax.Attributes;

import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collection;
import java.net.URL;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 56873 $</tt>
 */
public class SundayUnitTestCase
   extends TestCase
{
   public SundayUnitTestCase()
   {
   }

   public SundayUnitTestCase(String localName)
   {
      super(localName);
   }

   public void testConfigUnmarshalling() throws Exception
   {
      //
      // Type declarations
      //

      TypeBinding configType = new TypeBinding();
      TypeBinding attributeType = new TypeBinding();
      TypeBinding attributeValue1Type = new TypeBinding();
      TypeBinding attributeValue2Type = new TypeBinding();

      TypeBinding beansType = new TypeBinding();
      TypeBinding beanType = new TypeBinding();

      TypeBinding listType = new TypeBinding();
      TypeBinding listValueType = new TypeBinding();
      TypeBinding listDependsType = new TypeBinding();

      TypeBinding mapType = new TypeBinding();
      TypeBinding entry1Type = new TypeBinding();
      TypeBinding entry2Type = new TypeBinding();
      TypeBinding entry3Type = new TypeBinding();
      TypeBinding entry4Type = new TypeBinding();

      //
      // Schema assembling
      //

      configType.addElement(new QName("attribute"), attributeType, 0, true);
      configType.addElement(new QName("beans"), beansType);
      configType.addElement(new QName("list"), listType);
      configType.addElement(new QName("map"), mapType);

      attributeType.addElement(new QName("value1"), attributeValue1Type);
      attributeType.addElement(new QName("value2"), attributeValue2Type);

      beansType.addElement(new QName("bean"), beanType, 0, true);

      listType.addElement(new QName("value"), listValueType);
      listType.addElement(new QName("depends"), listDependsType);
      listType.addElement(new QName("list"), listType);

      mapType.addElement(new QName("entry1"), entry1Type);
      mapType.addElement(new QName("entry2"), entry2Type);
      mapType.addElement(new QName("entry3"), entry3Type);
      mapType.addElement(new QName("entry4"), entry4Type);
      mapType.addElement(new QName("map"), mapType);

      //
      // Handler implementations
      //

      configType.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object parent, QName name, ElementBinding element)
         {
            return new Config();
         }
      }
      );

      configType.pushInterceptor(new QName("attribute"), new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            Config config = (Config)parent;
            Config.ConfigAttr attr = (Config.ConfigAttr)child;
            Collection<ConfigAttr> attrs = config.getAttrs();
            if(attrs == null)
            {
               attrs = new ArrayList<ConfigAttr>();
               config.setAttrs(attrs);
            }
            attrs.add(attr);
         }
      }
      );

      configType.pushInterceptor(new QName("beans"), new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            Config config = (Config)parent;
            config.beans = (Collection<Bean>)child;
         }
      }
      );

      configType.pushInterceptor(new QName("list"), new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            Config config = (Config)parent;
            config.list = (Collection<Object>)child;
         }
      }
      );

      configType.pushInterceptor(new QName("map"), new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            Config config = (Config)parent;
            config.map = (Map<?, ?>)child;
         }
      }
      );

      attributeType.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object parent, QName name, ElementBinding element)
         {
            return new Immutable(Config.ConfigAttr.class);
         }

         public Object endElement(Object o, QName name, ElementBinding element)
         {
            Immutable imm = (Immutable)o;
            return imm.newInstance();
         }
      }
      );
      attributeType.setSimpleType(new CharactersHandler(){
         public void setValue(QName qName, ElementBinding element, Object owner, Object value)
         {
            Immutable imm = (Immutable)owner;
            Config.ConfigAttr.ConfigAttrDataValue o = new Config.ConfigAttr.ConfigAttrDataValue();
            o.setData((String)value);
            imm.addChild(qName.getLocalPart(), o);
         }
      });

      attributeType.pushInterceptor(new QName("value1"), new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            Immutable imm = (Immutable)parent;
            Config.ConfigAttr.ConfigAttrValue1 value1 = (Config.ConfigAttr.ConfigAttrValue1)child;
            imm.addChild(name.getLocalPart(), value1);
         }
      }
      );

      attributeType.pushInterceptor(new QName("value2"), new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            Immutable imm = (Immutable)parent;
            Config.ConfigAttr.ConfigAttrValue2 value2 = (Config.ConfigAttr.ConfigAttrValue2)child;
            imm.addChild(name.getLocalPart(), value2);
         }
      }
      );

      attributeValue1Type.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object parent, QName name, ElementBinding element)
         {
            return new Config.ConfigAttr.ConfigAttrValue1();
         }
      }
      );
      attributeValue1Type.pushInterceptor(new QName("property"), new DefaultElementInterceptor()
      {
         public void characters(Object o, QName name, TypeBinding type, NamespaceContext nsCtx, String text)
         {
            Config.ConfigAttr.ConfigAttrValue1 value1 = (Config.ConfigAttr.ConfigAttrValue1)o;
            value1.setProperty(text);
         }
      }
      );

      attributeValue2Type.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object parent, QName name, ElementBinding element)
         {
            return new Config.ConfigAttr.ConfigAttrValue2();
         }

      }
      );
      attributeValue2Type.setSimpleType(new CharactersHandler(){
         public void setValue(QName qName, ElementBinding element, Object owner, Object value)
         {
            Config.ConfigAttr.ConfigAttrValue2 value2 = (Config.ConfigAttr.ConfigAttrValue2)owner;
            value2.setValue2((String)value);
         }
      });

      beansType.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object parent, QName name, ElementBinding element)
         {
            return new ArrayList();
         }
      }
      );
      beansType.pushInterceptor(new QName("bean"), new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            Collection<Bean> beans = (Collection<Bean>)parent;
            Config.Bean bean = (Config.Bean)child;
            beans.add(bean);
         }
      }
      );

      beanType.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object parent, QName name, ElementBinding element)
         {
            return new Immutable(Config.Bean.class);
         }

         public Object endElement(Object o, QName name, ElementBinding element)
         {
            Immutable imm = (Immutable)o;
            Config.Bean bean = (Config.Bean)imm.newInstance();
            return bean;
         }
      }
      );

      beanType.pushInterceptor(new QName("name"), new DefaultElementInterceptor()
      {
         public void characters(Object o, QName name, TypeBinding type, NamespaceContext nsCtx, String text)
         {
            Immutable imm = (Immutable)o;
            imm.addChild(name.getLocalPart(), text);
         }
      }
      );

      listType.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object parent, QName name, ElementBinding element)
         {
            return new ArrayList();
         }
      }
      );
      listType.pushInterceptor(new QName("value"), new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            Collection<ListValue> list = (Collection<ListValue>)parent;
            Config.ListValue value = (Config.ListValue)child;
            list.add(value);
         }
      }
      );
      listType.pushInterceptor(new QName("depends"), new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            Collection<Depends> list = (Collection<Depends>)parent;
            Config.Depends depends = (Config.Depends)child;
            list.add(depends);
         }
      }
      );
      listType.pushInterceptor(new QName("list"), new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            Collection<Collection<?>> list = (Collection<Collection<?>>)parent;
            Collection<?> sublist = (Collection<?>)child;
            list.add(sublist);
         }
      }
      );

      listValueType.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object parent, QName name, ElementBinding element)
         {
            return new Immutable(Config.ListValue.class);
         }

         public void attributes(Object o,
                                QName elementName,
                                ElementBinding element,
                                Attributes attrs,
                                NamespaceContext nsCtx)
         {
            Immutable imm = (Immutable)o;
            for(int i = 0; i < attrs.getLength(); ++i)
            {
               String lName = attrs.getLocalName(i);
               if("type".equals(lName))
               {
                  imm.addChild(lName, attrs.getValue(i));
               }
            }
         }

         public Object endElement(Object o, QName name, ElementBinding element)
         {
            Immutable imm = (Immutable)o;
            Config.ListValue value = (Config.ListValue)imm.newInstance();
            return value;
         }
      }
      );
      listValueType.setSimpleType(new CharactersHandler(){
         public void setValue(QName qName, ElementBinding element, Object owner, Object value)
         {
            Immutable imm = (Immutable)owner;
            imm.addChild(qName.getLocalPart(), value);
         }
      });

      listDependsType.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object parent, QName name, ElementBinding element)
         {
            return new Immutable(Config.Depends.class);
         }

         public void attributes(Object o,
                                QName elementName,
                                ElementBinding element,
                                Attributes attrs,
                                NamespaceContext nsCtx)
         {
            Immutable imm = (Immutable)o;
            for(int i = 0; i < attrs.getLength(); ++i)
            {
               String lName = attrs.getLocalName(i);
               if("value".equals(lName))
               {
                  imm.addChild(lName, attrs.getValue(i));
               }
            }
         }

         public Object endElement(Object o, QName name, ElementBinding element)
         {
            Immutable imm = (Immutable)o;
            Config.Depends depends = (Config.Depends)imm.newInstance();
            return depends;
         }
      }
      );

      mapType.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object parent, QName name, ElementBinding element)
         {
            return new HashMap();
         }
      }
      );

      ElementInterceptor addMapEntryInterceptor = new DefaultElementInterceptor()
      {
         public void add(Object parent, Object child, QName name)
         {
            Map<Object, Object> map = (Map<Object, Object>)parent;
            Object[] arr = (Object[])child;
            map.put(arr[0], arr[1]);
         }
      };
      mapType.pushInterceptor(new QName("entry1"), addMapEntryInterceptor);
      mapType.pushInterceptor(new QName("entry2"), addMapEntryInterceptor);
      mapType.pushInterceptor(new QName("entry3"), addMapEntryInterceptor);
      mapType.pushInterceptor(new QName("entry4"), addMapEntryInterceptor);
      mapType.pushInterceptor(new QName("map"), addMapEntryInterceptor);

      entry1Type.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object parent, QName name, ElementBinding element)
         {
            return new Object[2];
         }
      }
      );
      entry1Type.pushInterceptor(new QName("key1"), new DefaultElementInterceptor()
      {
         public void characters(Object o, QName name, TypeBinding type, NamespaceContext nsCtx, String text)
         {
            Object[] arr = (Object[])o;
            arr[0] = text;
         }
      }
      );
      entry1Type.pushInterceptor(new QName("value1"), new DefaultElementInterceptor()
      {
         public void characters(Object o, QName name, TypeBinding type, NamespaceContext nsCtx, String text)
         {
            Object[] arr = (Object[])o;
            arr[1] = text;
         }
      }
      );

      entry2Type.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object parent, QName name, ElementBinding element)
         {
            return new Object[2];
         }

         public void attributes(Object o,
                                QName elementName,
                                ElementBinding element,
                                Attributes attrs,
                                NamespaceContext nsCtx)
         {
            Object[] arr = (Object[])o;
            for(int i = 0; i < attrs.getLength(); ++i)
            {
               String lName = attrs.getLocalName(i);
               if("key2".equals(lName))
               {
                  arr[0] = attrs.getValue(i);
               }
            }
         }
      }
      );
      entry2Type.pushInterceptor(new QName("value2"), new DefaultElementInterceptor()
      {
         public void characters(Object o, QName name, TypeBinding type, NamespaceContext nsCtx, String text)
         {
            Object[] arr = (Object[])o;
            arr[1] = text;
         }
      }
      );

      entry3Type.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object parent, QName name, ElementBinding element)
         {
            return new Object[2];
         }

         public void attributes(Object o,
                                QName elementName,
                                ElementBinding element,
                                Attributes attrs,
                                NamespaceContext nsCtx)
         {
            Object[] arr = (Object[])o;
            for(int i = 0; i < attrs.getLength(); ++i)
            {
               String lName = attrs.getLocalName(i);
               if("key3".equals(lName))
               {
                  arr[0] = attrs.getValue(i);
               }
               else if("value3".equals(lName))
               {
                  arr[1] = attrs.getValue(i);
               }
            }
         }
      }
      );

      entry4Type.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object parent, QName name, ElementBinding element)
         {
            return new Object[2];
         }

         public void attributes(Object o,
                                QName elementName,
                                ElementBinding element,
                                Attributes attrs,
                                NamespaceContext nsCtx)
         {
            Object[] arr = (Object[])o;
            for(int i = 0; i < attrs.getLength(); ++i)
            {
               String lName = attrs.getLocalName(i);
               if("key4".equals(lName))
               {
                  arr[0] = attrs.getValue(i);
               }
            }
         }

      }
      );
      entry4Type.setSimpleType(new CharactersHandler(){
         public void setValue(QName qName, ElementBinding element, Object owner, Object value)
         {
            Object[] arr = (Object[])owner;
            arr[1] = value;
         }
      });

      mapType.pushInterceptor(new QName("map"), new DefaultElementInterceptor()
      {
         public Object startElement(Object parent, QName name, TypeBinding type)
         {
            return new Object[2];
         }

         public void attributes(Object o,
                                QName elementName,
                                TypeBinding type,
                                Attributes attrs,
                                NamespaceContext nsCtx)
         {
            Object[] arr = (Object[])o;
            for(int i = 0; i < attrs.getLength(); ++i)
            {
               String lName = attrs.getLocalName(i);
               if("key".equals(lName))
               {
                  arr[0] = attrs.getValue(i);
               }
            }
         }

         public void add(Object parent, Object child, QName qName)
         {
            Object[] arr = (Object[])parent;
            Map<?, ?> submap = (Map<?, ?>)child;
            arr[1] = submap;
         }
      }
      );

      SchemaBinding cursor = new SchemaBinding();
      cursor.addElement(new QName("config"), configType);

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object config = unmarshaller.unmarshal(getXmlUrl("xml/newmetadata.xml"), cursor);

      //log.debug("unmarshalled: " + config);
      assertEquals(Config.getInstance(), config);
   }

   // Private

   private static String getXmlUrl(String name)
   {
      URL xmlUrl = Thread.currentThread().getContextClassLoader().getResource(name);
      if(xmlUrl == null)
      {
         throw new IllegalStateException(name + " not found");
      }
      return xmlUrl.getFile();
   }
}
