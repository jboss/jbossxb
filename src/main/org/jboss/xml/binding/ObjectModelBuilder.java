/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

import org.xml.sax.Attributes;
import org.jboss.logging.Logger;
import org.jboss.xml.binding.parser.JBossXBParser;
import org.jboss.xml.binding.metadata.unmarshalling.BindingCursor;
import org.apache.xerces.xs.XSTypeDefinition;

import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * An instance of this class translates SAX events into org.jboss.xml.binding.GenericObjectModelFactory calls
 * such as newChild, addChild and setValue.
 * WARN: this implementation is not thread-safe!
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class ObjectModelBuilder
   implements UnmarshallingContext, JBossXBParser.ContentHandler
{
   /**
    * logger
    */
   private static final Logger log = Logger.getLogger(ObjectModelBuilder.class);

   /**
    * The object that represents an ignored by the object model factory XML element, i.e. the factory returned null
    * from its newChild method
    */
   private static final Object IGNORED = new Object();

   /**
    * The root of the unmarshalled object graph
    */
   private Object root;

   /**
    * the stack of all the objects including IGNORED
    */
   private Stack all = new StackImpl();

   /**
    * the stack of only accepted objects (all - IGNORED)
    */
   private Stack accepted = new StackImpl();

   private GenericObjectModelFactory curFactory;
   private String curNameSwitchingFactory;
   private String curNsSwitchingFactory;
   private Stack nameSwitchingFactory;
   private Stack nsSwitchingFactory;

   /**
    * default object model factory
    */
   private GenericObjectModelFactory defaultFactory;

   /**
    * factories mapped to namespace URIs
    */
   private Map factoriesToNs = Collections.EMPTY_MAP;

   /**
    * NamespaceContext implementation
    */
   private final NamespaceRegistry nsRegistry = new NamespaceRegistry();

   /**
    * Buffer for simple element with text content
    */
   private StringBuffer value = new StringBuffer();

   private XSTypeDefinition currentType;

   private BindingCursor metadataCursor;

   // Public

   public void mapFactoryToNamespace(ObjectModelFactory factory, String namespaceUri)
   {
      if(factoriesToNs == Collections.EMPTY_MAP)
      {
         factoriesToNs = new HashMap();
      }
      factoriesToNs.put(namespaceUri, getGenericObjectModelFactory(factory));
   }

   public void init(ObjectModelFactory defaultFactory, Object root, BindingCursor cursor)
   {
      this.defaultFactory = getGenericObjectModelFactory(defaultFactory);
      this.metadataCursor = cursor;

      all.clear();
      accepted.clear();
      value.delete(0, value.length());
      this.root = root;
   }

   public void pushFactory(String namespaceURI, String localName, GenericObjectModelFactory factory)
   {
      if(curNsSwitchingFactory != null)
      {
         if(nsSwitchingFactory == null)
         {
            nsSwitchingFactory = new StackImpl();
            nameSwitchingFactory = new StackImpl();
         }
         nsSwitchingFactory.push(curNsSwitchingFactory);
         nameSwitchingFactory.push(curNameSwitchingFactory);
      }
      curNsSwitchingFactory = namespaceURI;
      curNameSwitchingFactory = localName;
      curFactory = factory;
   }

   public void popFactory()
   {
      if(nsSwitchingFactory == null || nsSwitchingFactory.isEmpty())
      {
         curNameSwitchingFactory = null;
         curNsSwitchingFactory = null;
      }
      else
      {
         curNameSwitchingFactory = (String)nameSwitchingFactory.pop();
         curNsSwitchingFactory = (String)nsSwitchingFactory.pop();
      }

      curFactory = getFactory(curNsSwitchingFactory);
   }

   // UnmarshallingContext implementation

   public Iterator getNamespaceURIs()
   {
      return nsRegistry.getRegisteredURIs();
   }

   public NamespaceContext getNamespaceContext()
   {
      return nsRegistry;
   }

   public Object getMetadata()
   {
      return metadataCursor.getElementBinding();
   }

   public Object getParentMetadata()
   {
      return metadataCursor.getParentElementBinding();
   }

   /**
    * Construct a QName from a value
    *
    * @param value A value that is of the form [prefix:]localpart
    */
   public QName resolveQName(String value)
   {
      StringTokenizer st = new StringTokenizer(value, ":");
      if(st.countTokens() == 1)
      {
         return new QName(value);
      }

      if(st.countTokens() != 2)
      {
         throw new IllegalArgumentException("Illegal QName: " + value);
      }

      String prefix = st.nextToken();
      String local = st.nextToken();
      String nsURI = nsRegistry.getNamespaceURI(prefix);

      //return new QName(nsURI, local);
      return new QName(nsURI,local,prefix);
   }

   public String getChildContent(String namespaceURI, String qName)
   {
      // todo reimplement later
      throw new UnsupportedOperationException();
      //return content.getChildContent(namespaceURI, qName);
   }

   public XSTypeDefinition getType()
   {
      return currentType;
   }
   
   // Public

   public void startPrefixMapping(String prefix, String uri)
   {
      nsRegistry.addPrefixMapping(prefix, uri);
   }

   public void endPrefixMapping(String prefix)
   {
      nsRegistry.removePrefixMapping(prefix);
   }

   public void processingInstruction(String target, String data)
   {
      if(!"jbossxb".equals(target))
      {
         return;
      }

      int i = data.indexOf("factory=\"");
      if(i != -1)
      {
         int end = data.indexOf('\"', i + 9);
         if(end == -1)
         {
            throw new JBossXBRuntimeException(
               "Property 'factory' is not terminated with '\"' in processing instruction: " + data
            );
         }

         String factoryProp = data.substring(i + 9, end);
         Class factoryCls;
         try
         {
            factoryCls = Thread.currentThread().getContextClassLoader().loadClass(factoryProp);
         }
         catch(ClassNotFoundException e)
         {
            throw new JBossXBRuntimeException("Failed to load factory class : " + e.getMessage(), e);
         }

         ObjectModelFactory factory;
         try
         {
            factory = (ObjectModelFactory)factoryCls.newInstance();
         }
         catch(Exception e)
         {
            throw new JBossXBRuntimeException("Failed to instantiate factory " + factoryProp + ": " + e.getMessage(), e);
         }

         i = data.indexOf("ns=\"");
         if(i == -1)
         {
            throw new JBossXBRuntimeException("Property 'ns' not found in factory mapping processing instruction: " + data);
         }

         end = data.indexOf("\"", i + 4);
         if(end == -1)
         {
            throw new JBossXBRuntimeException(
               "Property 'ns' is not terminated with '\"' in processing instruction: " + data
            );
         }

         String nsProp = data.substring(i + 4, end);
         mapFactoryToNamespace(factory, nsProp);
      }
      else
      {
         throw new JBossXBRuntimeException("Unexpected data in processing instruction: target=" + target + ", data=" + data);
      }
   }

   public Object getRoot()
   {
      if(!all.isEmpty())
      {
         all.pop();
         accepted.pop();
      }
      return root;
   }

   public void startElement(String namespaceURI,
                            String localName,
                            String qName,
                            Attributes atts,
                            XSTypeDefinition type)
   {
      Object parent = accepted.isEmpty() ? root : accepted.peek();
      metadataCursor.startElement(namespaceURI, localName);

      // todo currentType assignment
      currentType = type;

      Object element;
      if(!namespaceURI.equals(curNsSwitchingFactory))
      {
         GenericObjectModelFactory newFactory = getFactory(namespaceURI);
         if(newFactory != curFactory)
         {
            pushFactory(namespaceURI, localName, newFactory);
         }

         element = curFactory.newRoot(parent, this, namespaceURI, localName, atts);
      }
      else
      {
         element = curFactory.newChild(parent, this, namespaceURI, localName, atts);
      }

      if(element == null)
      {
         all.push(IGNORED);

         if(log.isTraceEnabled())
         {
            log.trace("ignored " + namespaceURI + ':' + qName);
         }
      }
      else
      {
         all.push(element);
         accepted.push(element);

         if(log.isTraceEnabled())
         {
            log.trace("accepted " + namespaceURI + ':' + qName);
         }
      }
   }

   public void endElement(String namespaceURI, String localName, String qName)
   {
      if(value.length() > 0)
      {
         Object element;
         try
         {
            element = accepted.peek();
         }
         catch(java.util.NoSuchElementException e)
         {
            log.error("value=" + value, e);
            throw e;
         }
         curFactory.setValue(element, this, namespaceURI, localName, value.toString().trim());
         value.delete(0, value.length());
      }

      if(localName.equals(curNameSwitchingFactory) && namespaceURI.equals(curNsSwitchingFactory))
      {
         popFactory();
      }

      Object element = all.pop();
      if(element != IGNORED)
      {
         element = accepted.pop();
         Object parent = (accepted.isEmpty() ? null : accepted.peek());

         if(parent != null)
         {
            curFactory.addChild(parent, element, this, namespaceURI, localName);
         }
         else
         {
            root = curFactory.completeRoot(element, this, namespaceURI, localName);
         }
      }

      metadataCursor.endElement(namespaceURI, localName);
   }

   public void characters(char[] ch, int start, int length)
   {
      value.append(ch, start, length);
   }

   // Private

   private GenericObjectModelFactory getFactory(String namespaceUri)
   {
      GenericObjectModelFactory factory = (GenericObjectModelFactory)factoriesToNs.get(namespaceUri);
      if(factory == null)
      {
         factory = defaultFactory;
      }
      return factory;
   }

   static Object invokeFactory(Object factory, Method method, Object[] args)
   {
      try
      {
         return method.invoke(factory, args);
      }
      catch(InvocationTargetException e)
      {
         Throwable te = e.getCause();
         if(te instanceof RuntimeException) throw (RuntimeException)te;

         String msg = "Failed to invoke method " + method + ", factory=" + factory;
         log.error(msg, e.getTargetException());
         
         IllegalStateException ise = new IllegalStateException(msg);
         ise.initCause(te);
         throw ise;
      }
      catch(Exception e)
      {
         String msg = "Failed to invoke method " + method.getName() + ", factory=" + factory;
         log.error(msg, e);
         IllegalStateException ise = new IllegalStateException(msg);
         ise.initCause(e);
         throw ise;
      }
   }

   static Method getMethodForElement(Object factory, String name, Class[] params)
   {
      Method method = null;
      try
      {
         method = factory.getClass().getMethod(name, params);
      }
      catch(NoSuchMethodException e)
      {
      }
      catch(SecurityException e)
      {
         throw e;
      }

      return method;
   }

   static final GenericObjectModelFactory getGenericObjectModelFactory(ObjectModelFactory factory)
   {
      if(!(factory instanceof GenericObjectModelFactory))
      {
         factory = new DelegatingObjectModelFactory(factory);
      }
      return factory instanceof GenericObjectModelFactory ? (GenericObjectModelFactory)factory : new DelegatingObjectModelFactory(factory);
   }

   private static interface Stack
   {
      void clear();

      void push(Object o);

      Object pop();

      Object peek();

      boolean isEmpty();
   }

   private static class StackImpl
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
