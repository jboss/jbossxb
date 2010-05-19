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

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.sunday.unmarshalling.CharactersHandler;
import org.jboss.xb.binding.sunday.unmarshalling.DefaultElementHandler;
import org.jboss.xb.binding.sunday.unmarshalling.DefaultElementInterceptor;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ElementInterceptor;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleHandler;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SequenceBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;
import org.xml.sax.Attributes;
import junit.framework.TestCase;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 37406 $</tt>
 */
public class MapsUnitTestCase
   extends TestCase
{
   private static final String NS = "http://www.jboss.org/test/xml/maps";
   private static final QName QNAME_MAP = new QName(NS, "map");
   private static final QName QNAME_SUBMAP = new QName(NS, "submap");
   private static final QName QNAME_ENTRY1 = new QName(NS, "entry1");
   private static final QName QNAME_ENTRY2 = new QName(NS, "entry2");
   private static final QName QNAME_ENTRY3 = new QName(NS, "entry3");
   private static final QName QNAME_ENTRY4 = new QName(NS, "entry4");
   private static final QName QNAME_KEY1 = new QName(NS, "key1");
   private static final QName QNAME_VALUE1 = new QName(NS, "value1");
   private static final QName QNAME_VALUE2 = new QName(NS, "value2");

   private static final Map<?, ?> EXPECTED;

   static
   {
      Map<String, Object> expected = new HashMap<String, Object>();
      expected.put("key1", "value1");
      expected.put("key2", "value2");
      expected.put("key3", "value3");
      expected.put("key4", "value4");
      expected.put("key_1", "value_1");
      expected.put("key_2", "value_2");
      expected.put("submap", Collections.singletonMap("submapKey", "submapValue"));
      EXPECTED = expected;
   }

   public MapsUnitTestCase()
   {
   }

   public MapsUnitTestCase(String localName)
   {
      super(localName);
   }

   public void testXsd() throws Exception
   {
      SchemaBinding schema = XsdBinder.bind(getFullPath("xml/maps.xsd"));

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object map = unmarshaller.unmarshal(getFullPath("xml/maps.xml"), schema);

      assertNotNull(map);
      assertTrue(map instanceof Map);

      assertEquals(EXPECTED, map);
   }

   public void testManual() throws Exception
   {
      SchemaBinding schema = new SchemaBinding();

      // Type declarations
      TypeBinding mapType = new TypeBinding();
      // default model group is all, we want sequence
      mapType.setParticle(new ParticleBinding(new SequenceBinding(schema)));

      TypeBinding entry1Type = new TypeBinding();
      TypeBinding entry2Type = new TypeBinding();
      TypeBinding entry3Type = new TypeBinding();
      TypeBinding entry4Type = new TypeBinding();

      // Schema assembling
      mapType.addElement(QNAME_ENTRY1, entry1Type, 0, true);
      mapType.addElement(QNAME_ENTRY2, entry2Type, 0, true);
      mapType.addElement(QNAME_ENTRY3, entry3Type, 0, true);
      mapType.addElement(QNAME_ENTRY4, entry4Type, 0, true);

      // sequence as entry
      SequenceBinding entrySeq = new SequenceBinding(schema);
      ElementBinding keyElement = new ElementBinding(schema, new QName(NS, "key"), new TypeBinding());
      entrySeq.addParticle(new ParticleBinding(keyElement));
      ElementBinding valueElement = new ElementBinding(schema, new QName(NS, "value"), new TypeBinding());
      entrySeq.addParticle(new ParticleBinding(valueElement));

      ParticleBinding entryParticle = new ParticleBinding(entrySeq);
      entryParticle.setMinOccurs(0);
      entryParticle.setMaxOccursUnbounded(true);
      mapType.addParticle(entryParticle);

      mapType.addElement(QNAME_SUBMAP, mapType, 0, true);
      schema.addElement(QNAME_MAP, mapType);

      // Handler implementations
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
      mapType.pushInterceptor(QNAME_ENTRY1, addMapEntryInterceptor);
      mapType.pushInterceptor(QNAME_ENTRY2, addMapEntryInterceptor);
      mapType.pushInterceptor(QNAME_ENTRY3, addMapEntryInterceptor);
      mapType.pushInterceptor(QNAME_ENTRY4, addMapEntryInterceptor);
      mapType.pushInterceptor(QNAME_SUBMAP, addMapEntryInterceptor);

      entry1Type.setHandler(new DefaultElementHandler()
      {
         public Object startElement(Object parent, QName name, ElementBinding element)
         {
            return new Object[2];
         }
      }
      );
      entry1Type.pushInterceptor(QNAME_KEY1, new DefaultElementInterceptor()
      {
         public void characters(Object o, QName name, TypeBinding type, NamespaceContext nsCtx, String text)
         {
            Object[] arr = (Object[])o;
            arr[0] = text;
         }
      }
      );
      entry1Type.pushInterceptor(QNAME_VALUE1, new DefaultElementInterceptor()
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
      entry2Type.pushInterceptor(QNAME_VALUE2, new DefaultElementInterceptor()
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
      entry4Type.setSimpleType(new CharactersHandler()
      {
         public void setValue(QName qName, ElementBinding element, Object owner, Object value)
         {
            Object[] arr = (Object[])owner;
            arr[1] = value;
         }
      }
      );

      mapType.pushInterceptor(QNAME_SUBMAP, new DefaultElementInterceptor()
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

      // sequence entry
      entrySeq.setSkip(false);
      entrySeq.setHandler(new ParticleHandler()
      {
         public Object startParticle(Object parent,
                                     QName elementName,
                                     ParticleBinding particle,
                                     Attributes attrs,
                                     NamespaceContext nsCtx)
         {
            return new Object[2];
         }

         public Object endParticle(Object o, QName elementName, ParticleBinding particle)
         {
            return o;
         }

         public void setParent(Object parent,
                               Object o,
                               QName elementName,
                               ParticleBinding particle,
                               ParticleBinding parentParticle)
         {
            Map<Object, Object> map = (Map<Object, Object>)parent;
            Object[] arr = (Object[])o;
            map.put(arr[0], arr[1]);
         }
      }
      );

      keyElement.pushInterceptor(new DefaultElementInterceptor()
      {
         public void characters(Object o, QName name, TypeBinding type, NamespaceContext nsCtx, String text)
         {
            Object[] arr = (Object[])o;
            arr[0] = text;
         }
      });

      valueElement.pushInterceptor(new DefaultElementInterceptor()
      {
         public void characters(Object o, QName name, TypeBinding type, NamespaceContext nsCtx, String text)
         {
            Object[] arr = (Object[])o;
            arr[1] = text;
         }
      });

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object map = unmarshaller.unmarshal(getFullPath("xml/maps.xml"), schema);

      assertNotNull(map);
      assertTrue(map instanceof Map);

      assertEquals(EXPECTED, map);
   }

   // Private

   private String getFullPath(String name)
   {
      URL url = Thread.currentThread().getContextClassLoader().getResource(name);
      if(url == null)
      {
         fail("Resource not found: " + name);
      }
      return url.getFile();
   }
}
