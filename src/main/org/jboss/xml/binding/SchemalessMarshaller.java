/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

import org.jboss.logging.Logger;
import org.xml.sax.SAXException;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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

   private final Map gettersPerClass = new HashMap();

   private final Content content = new Content();

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

      ContentWriter contentWriter = new ContentWriter(writer);
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
      List getters = getGetterList(root.getClass());
      AttributesImpl attrs = null; //new AttributesImpl(5);
      content.startElement(null, localName, localName, attrs);

      for(int i = 0; i < getters.size(); ++i)
      {
         Method getter = (Method)getters.get(i);
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
            String qName = childName;
            if(isAttributeType(child.getClass()))
            {
               marshalAttributeType(qName, child);

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
               for(int arrInd = 0; arrInd < Array.getLength(child); ++arrInd)
               {
                  Object o = Array.get(child, arrInd);
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
            }
            else if(Collection.class.isAssignableFrom(child.getClass()))
            {
               content.startElement(null, childName, qName, null);

               Collection col = (Collection)child;
               for(Iterator iter = col.iterator(); iter.hasNext();)
               {
                  Object o = iter.next();
                  if(o != null)
                  {
                     if(isAttributeType(o.getClass()))
                     {
                        marshalAttributeType(o.getClass().getName(), o);
                     }
                     else
                     {
                        marshalObject(o, o.getClass().getName(), writer);
                     }
                  }
               }

               content.endElement(null, childName, qName);
            }
            else
            {
               marshalObject(child, qName, writer);
            }
         }
      }

      content.endElement(null, localName, localName);
   }

   private void marshalAttributeType(String qName, Object child)
   {
      content.startElement(null, qName, qName, null);
      String value = child.toString();
      content.characters(value.toCharArray(), 0, value.length());
      content.endElement(null, qName, qName);
   }

   private List getGetterList(Class aClass)
   {
      List getters = (List)gettersPerClass.get(aClass);
      if(getters == null)
      {
         getters = new ArrayList();
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

   static boolean isAttributeType(Class cls)
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
