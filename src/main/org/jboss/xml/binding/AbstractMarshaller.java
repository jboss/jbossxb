/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

import org.jboss.logging.Logger;

import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public abstract class AbstractMarshaller
   implements Marshaller
{
   private static final Logger log = Logger.getLogger(AbstractMarshaller.class);

   protected String version = VERSION;
   protected String encoding = ENCODING;
   protected List rootQNames = new ArrayList();

   /**
    * object model providers mapped to namespace URIs
    */
   private Map providerToNs = Collections.EMPTY_MAP;

   // Marshaller implementation

   public void mapProviderToNamespace(ObjectModelProvider provider, String namespaceUri)
   {
      if(!(provider instanceof GenericObjectModelProvider))
      {
         provider = new DelegatingObjectModelProvider(provider);
      }

      if(providerToNs.isEmpty())
      {
         providerToNs = new HashMap();
      }
      providerToNs.put(namespaceUri, provider);
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
      QName qName = new QName(namespaceUri, prefix, name);
      rootQNames.add(qName);
   }

   // Protected

   protected GenericObjectModelProvider getProvider(String namespaceUri, GenericObjectModelProvider defaultProvider)
   {
      GenericObjectModelProvider provider = (GenericObjectModelProvider) providerToNs.get(namespaceUri);
      if(provider == null)
      {
         provider = defaultProvider;
      }
      return provider;
   }

   static final Object provideChildren(ObjectModelProvider provider,
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
         new Class[]{parentClass, String.class, String.class});
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

   static final Object provideValue(ObjectModelProvider provider,
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
         new Class[]{parentClass, String.class, String.class});
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

   static final Object provideAttributeValue(ObjectModelProvider provider,
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
         new Class[]{objectClass, String.class, String.class});
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

   private static final Method getProviderMethod(Class providerClass, String methodName, Class[] args)
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

   protected final static class QName
   {
      public final String namespaceUri;
      public final String prefix;
      public final String name;

      public QName(String namespaceUri, String prefix, String name)
      {
         this.namespaceUri = namespaceUri;
         this.prefix = prefix;
         this.name = name;
      }

      public boolean equals(Object o)
      {
         if(this == o) return true;
         if(!(o instanceof QName)) return false;

         final QName qName = (QName) o;

         if(name != null ? !name.equals(qName.name) : qName.name != null) return false;
         if(namespaceUri != null ? !namespaceUri.equals(qName.namespaceUri) : qName.namespaceUri != null) return false;
         if(prefix != null ? !prefix.equals(qName.prefix) : qName.prefix != null) return false;

         return true;
      }

      public int hashCode()
      {
         int result;
         result = (namespaceUri != null ? namespaceUri.hashCode() : 0);
         result = 29 * result + (prefix != null ? prefix.hashCode() : 0);
         result = 29 * result + (name != null ? name.hashCode() : 0);
         return result;
      }
   }
}
