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
package org.jboss.xb.binding;

import org.jboss.logging.Logger;
import org.xml.sax.SAXException;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import java.lang.reflect.Method;
import java.lang.reflect.Array;

/**
 * Sandbox. Very testcase specific impl.
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class SchemalessMarshaller
{
   private static final Logger log = Logger.getLogger(SchemalessMarshaller.class);

   public static final String PROPERTY_JAXB_SCHEMA_LOCATION = "jaxb.schemaLocation";

   private final Properties props = new Properties();

   private final Map<Class<?>, List> gettersPerClass = new HashMap<Class<?>, List>();

   private final Content content = new Content();

   public void setProperty(String name, String value)
   {
      props.setProperty(name, value);
   }

   public void marshal(Object root, StringWriter writer)
   {
      log.debug("marshal: root=" + root);

      content.startDocument();

      marshalObject(root, root.getClass().getName(), writer);

      content.endDocument();

      writer.write("<?xml version=\"");
      writer.write("1.0");
      writer.write("\" encoding=\"");
      writer.write("UTF-8");
      writer.write("\"?>\n");

      ContentWriter contentWriter = new ContentWriter(writer, true);
      try
      {
         content.handleContent(contentWriter);
      }
      catch(SAXException e)
      {
         log.error("Failed to write content.", e);
         throw new IllegalStateException("Failed to write content: " + e.getMessage());
      }
   }

   private void marshalObject(Object root, String localName, StringWriter writer)
   {
      List<Method> getters = getGetterList(root.getClass());
      AttributesImpl attrs = null; //new AttributesImpl(5);
      content.startElement(null, localName, localName, attrs);

      for(int i = 0; i < getters.size(); ++i)
      {
         Method getter = getters.get(i);
         Object child;
         try
         {
            child = getter.invoke(root, null);
         }
         catch(Exception e)
         {
            log.error("Failed to invoke getter " + getter.getName() + " on " + root, e);
            throw new IllegalStateException(
               "Failed to invoke getter " + getter.getName() + " on " + root + ": " + e.getMessage()
            );
         }

         if(child != null)
         {
            String childName = getter.getName().substring(3);
            if(isAttributeType(child.getClass()))
            {
               marshalAttributeType(childName, child);

               /*
               attrs.add(null,
                  getter.getName().substring(3),
                  getter.getName().substring(3),
                  getter.getClass().getName(),
                  child.toString()
               );
               */
            }
            else if(child.getClass().isArray())
            {
               content.startElement(null, childName, childName, null);
               for(int arrInd = 0; arrInd < Array.getLength(child); ++arrInd)
               {
                  Object o = Array.get(child, arrInd);
                  marshalCollectionItem(o, o.getClass().getName(), o.getClass().getName(), writer);
               }
               content.endElement(null, childName, childName);
            }
            else if(Collection.class.isAssignableFrom(child.getClass()))
            {
               content.startElement(null, childName, childName, null);
               Collection<?> col = (Collection<?>)child;
               for(Iterator<?> iter = col.iterator(); iter.hasNext();)
               {
                  Object o = iter.next();
                  marshalCollectionItem(o, o.getClass().getName(), o.getClass().getName(), writer);
               }
               content.endElement(null, childName, childName);
            }
            else
            {
               marshalObject(child, childName, writer);
            }
         }
      }

      content.endElement(null, localName, localName);
   }

   private void marshalCollectionItem(Object o, String childName, String qName, StringWriter writer)
   {
      if(o != null)
      {
         if(isAttributeType(o.getClass()))
         {
            marshalAttributeType(childName, o);
         }
         else
         {
            marshalObject(o, qName, writer);
         }
      }
   }

   private void marshalAttributeType(String qName, Object child)
   {
      content.startElement(null, qName, qName, null);
      String value = child.toString();
      content.characters(value.toCharArray(), 0, value.length());
      content.endElement(null, qName, qName);
   }

   private List<Method> getGetterList(Class<?> aClass)
   {
      List<Method> getters = gettersPerClass.get(aClass);
      if(getters == null)
      {
         getters = new ArrayList<Method>();
         Method[] methods = aClass.getMethods();
         for(int i = 0; i < methods.length; ++i)
         {
            Method method = methods[i];
            if(method.getDeclaringClass() != Object.class)
            {
               if((method.getName().startsWith("get") || method.getName().startsWith("is")) &&
                  (method.getParameterTypes() == null || method.getParameterTypes().length == 0))
               {
                  getters.add(method);
               }
            }
         }
         gettersPerClass.put(aClass, getters);
      }
      return getters;
   }

   static boolean isAttributeType(Class<?> cls)
   {
      if(cls.isPrimitive() ||
         cls == Byte.class ||
         cls == Short.class ||
         cls == Integer.class ||
         cls == Long.class ||
         cls == Float.class ||
         cls == Double.class ||
         cls == Character.class ||
         cls == Boolean.class ||
         cls == String.class ||
         cls == java.util.Date.class)
      {
         return true;
      }
      else
      {
         return false;
      }
   }
}
