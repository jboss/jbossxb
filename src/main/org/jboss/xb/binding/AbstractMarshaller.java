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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import org.jboss.logging.Logger;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public abstract class AbstractMarshaller
   implements Marshaller
{
   protected static final Logger log = Logger.getLogger(AbstractMarshaller.class);

   protected String version = VERSION;
   protected String encoding = ENCODING;
   protected List rootQNames = new ArrayList();

   private Map classMappings = Collections.EMPTY_MAP;

   private Properties props;

   // Marshaller implementation

   public void marshal(String schemaUri, ObjectModelProvider provider, Object root, Writer writer) throws IOException,
      ParserConfigurationException,
      SAXException
   {
      URL url;
      try
      {
         url = new URL(schemaUri);
      }
      catch(MalformedURLException e)
      {
         throw new IllegalArgumentException("Malformed schema URI " + schemaUri + ": " + e.getMessage());
      }

      InputStream is;
      try
      {
         is = url.openStream();
      }
      catch(IOException e)
      {
         throw new IllegalStateException("Failed to open input stream for schema " + schemaUri + ": " + e.getMessage());
      }

      try
      {
         InputStreamReader reader = new InputStreamReader(is);
         marshal(reader, provider, root, writer);
      }
      finally
      {
         is.close();
      }
   }

   public void mapClassToGlobalElement(Class cls,
                                       String localName,
                                       String namespaceUri,
                                       String schemaUrl,
                                       ObjectModelProvider provider)
   {
      GenericObjectModelProvider delProv = null;
      if(provider != null)
      {
         delProv = provider instanceof GenericObjectModelProvider ?
         (GenericObjectModelProvider)provider :
         new DelegatingObjectModelProvider(provider);
      }

      ClassMapping mapping = new ClassMapping(cls,
         localName,
         null,
         namespaceUri,
         schemaUrl,
         delProv
      );

      addClassMapping(mapping);
   }

   public void mapClassToGlobalType(Class cls,
                                    String localName,
                                    String nsUri,
                                    String schemaUrl,
                                    ObjectModelProvider provider)
   {
      ClassMapping mapping = new ClassMapping(cls,
         null,
         localName,
         nsUri,
         schemaUrl,
         provider instanceof GenericObjectModelProvider ?
         (GenericObjectModelProvider)provider :
         new DelegatingObjectModelProvider(provider)
      );

      addClassMapping(mapping);
   }

   public void setVersion(String version)
   {
      this.version = version;
   }

   public void setEncoding(String encoding)
   {
      this.encoding = encoding;
   }

   public void mapPublicIdToSystemId(String publicId, String systemId)
   {
      throw new UnsupportedOperationException();
   }

   public void addRootElement(String namespaceUri, String prefix, String name)
   {
      addRootElement(new QName(namespaceUri, name, prefix));
   }

   public void addRootElement(QName qName)
   {
      rootQNames.add(qName);
   }

   public void setProperty(String name, String value)
   {
      if(props == null)
      {
         props = new Properties();
      }
      props.setProperty(name, value);
   }

   public String getProperty(String name)
   {
      return props == null ? null : props.getProperty(name);
   }

   public abstract void declareNamespace(String prefix, String uri);

   public abstract void addAttribute(String prefix, String localName, String type, String value);

   // Protected

   protected boolean propertyIsTrueOrNotSet(String name)
   {
      String value = getProperty(name);
      return value == null || "true".equalsIgnoreCase(value);
   }

   protected void writeXmlVersion(Writer writer) throws IOException
   {
      String xmlVersion = getProperty(Marshaller.PROP_OUTPUT_XML_VERSION);
      if(xmlVersion == null || "true".equalsIgnoreCase(xmlVersion))
      {
         writer.write("<?xml version=\"");
         writer.write(version);
         writer.write("\" encoding=\"");
         writer.write(encoding);
         writer.write("\"?>\n");
      }
   }

   protected ClassMapping getClassMapping(Class cls)
   {
      return (ClassMapping)classMappings.get(cls);
   }

   private void addClassMapping(ClassMapping mapping)
   {
      if(classMappings == Collections.EMPTY_MAP)
      {
         classMappings = new HashMap();
      }
      classMappings.put(mapping.cls, mapping);
   }

   static Object provideChildren(ObjectModelProvider provider,
                                 Object parent,
                                 String namespaceUri,
                                 String name)
   {
      Class providerClass = provider.getClass();
      Class parentClass = parent.getClass();
      String methodName = "getChildren";

      Object container = null;
      Method method = getProviderMethod(providerClass,
         methodName,
         new Class[]{parentClass, String.class, String.class}
      );
      if(method != null)
      {
         try
         {
            container = method.invoke(provider, new Object[]{parent, namespaceUri, name});
         }
         catch(Exception e)
         {
            log.error("Failed to invoke method " + methodName, e);
            throw new IllegalStateException("Failed to invoke method " + methodName);
         }
      }
      else if(log.isTraceEnabled())
      {
         log.trace("No " + methodName + " for " + name);
      }
      return container;
   }

   static Object provideValue(ObjectModelProvider provider,
                              Object parent,
                              String namespaceUri,
                              String name)
   {
      Class providerClass = provider.getClass();
      Class parentClass = parent.getClass();
      String methodName = "getElementValue";

      Object value = null;
      Method method = getProviderMethod(providerClass,
         methodName,
         new Class[]{parentClass, String.class, String.class}
      );
      if(method != null)
      {
         try
         {
            value = method.invoke(provider, new Object[]{parent, namespaceUri, name});
         }
         catch(Exception e)
         {
            throw new IllegalStateException("Failed to invoke method " + methodName);
         }
      }
      else if(log.isTraceEnabled())
      {
         log.trace("No " + methodName + " for " + name);
      }
      return value;
   }

   static Object provideAttributeValue(ObjectModelProvider provider,
                                       Object object,
                                       String namespaceUri,
                                       String name)
   {
      Class providerClass = provider.getClass();
      Class objectClass = object.getClass();
      String methodName = "getAttributeValue";

      Object value = null;
      Method method = getProviderMethod(providerClass,
         methodName,
         new Class[]{objectClass, String.class, String.class}
      );
      if(method != null)
      {
         try
         {
            value = method.invoke(provider, new Object[]{object, namespaceUri, name});
         }
         catch(Exception e)
         {
            throw new IllegalStateException("Failed to invoke method " + methodName);
         }
      }
      else if(log.isTraceEnabled())
      {
         log.trace("No " + methodName + " for " + name);
      }
      return value;
   }

   private static Method getProviderMethod(Class providerClass, String methodName, Class[] args)
   {
      Method method = null;
      try
      {
         method = providerClass.getMethod(methodName, args);
      }
      catch(NoSuchMethodException e)
      {
         // no method
      }
      return method;
   }

   // Inner

   protected class ClassMapping
   {
      public final Class cls;
      public final QName elementName;
      public final QName typeName;
      public final String schemaUrl;
      public final GenericObjectModelProvider provider;

      public ClassMapping(Class cls,
                          String elementName,
                          String typeName,
                          String nsUri,
                          String schemaUrl,
                          GenericObjectModelProvider provider)
      {
         this.cls = cls;
         if(elementName != null)
         {
            this.elementName = nsUri == null ? new QName(elementName) : new QName(nsUri, elementName);
            this.typeName = null;
         }
         else if(typeName != null)
         {
            this.elementName = null;
            this.typeName = nsUri == null ? new QName(typeName) : new QName(nsUri, typeName);
         }
         else
         {
            throw new JBossXBRuntimeException("Element or type name must not null for " + cls);
         }

         this.schemaUrl = schemaUrl;
         this.provider = provider;
      }

      public boolean equals(Object o)
      {
         if(this == o)
         {
            return true;
         }
         if(o == null || getClass() != o.getClass())
         {
            return false;
         }

         final ClassMapping that = (ClassMapping)o;

         if(cls != null ? !cls.equals(that.cls) : that.cls != null)
         {
            return false;
         }
         if(elementName != null ? !elementName.equals(that.elementName) : that.elementName != null)
         {
            return false;
         }
         if(provider != null ? !provider.equals(that.provider) : that.provider != null)
         {
            return false;
         }
         if(schemaUrl != null ? !schemaUrl.equals(that.schemaUrl) : that.schemaUrl != null)
         {
            return false;
         }
         if(typeName != null ? !typeName.equals(that.typeName) : that.typeName != null)
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         int result;
         result = (cls != null ? cls.hashCode() : 0);
         result = 29 * result + (elementName != null ? elementName.hashCode() : 0);
         result = 29 * result + (typeName != null ? typeName.hashCode() : 0);
         result = 29 * result + (schemaUrl != null ? schemaUrl.hashCode() : 0);
         result = 29 * result + (provider != null ? provider.hashCode() : 0);
         return result;
      }
   }

   protected static interface Stack
   {
      void clear();

      void push(Object o);

      Object pop();

      Object peek();

      boolean isEmpty();
   }

   public static class StackImpl
      implements Stack
   {
      private LinkedList list = new LinkedList();

      public void clear()
      {
         list.clear();
      }

      public void push(Object o)
      {
         list.addLast(o);
      }

      public Object pop()
      {
         return list.removeLast();
      }

      public Object peek()
      {
         return list.getLast();
      }

      public boolean isEmpty()
      {
         return list.isEmpty();
      }
   }
}
