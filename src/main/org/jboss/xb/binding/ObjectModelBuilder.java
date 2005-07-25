/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding;

import org.xml.sax.Attributes;
import org.jboss.logging.Logger;
import org.jboss.xb.binding.parser.JBossXBParser;
import org.jboss.xb.binding.metadata.unmarshalling.BindingCursor;
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
      return new QName(nsURI, local, prefix);
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
            throw new JBossXBRuntimeException("Failed to instantiate factory " + factoryProp + ": " + e.getMessage(),
               e
            );
         }

         i = data.indexOf("ns=\"");
         if(i == -1)
         {
            throw new JBossXBRuntimeException(
               "Property 'ns' not found in factory mapping processing instruction: " + data
            );
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
         throw new JBossXBRuntimeException(
            "Unexpected data in processing instruction: target=" + target + ", data=" + data
         );
      }
   }

   public Object getRoot()
   {
      if(!all.isEmpty())
      {
         popAll();
         popAccepted();
      }
      return root;
   }

   public void startElement(String namespaceURI,
                            String localName,
                            String qName,
                            Attributes atts,
                            XSTypeDefinition type)
   {
      Object parent = accepted.isEmpty() ? root : peekAccepted();
      metadataCursor.startElement(namespaceURI, localName);

      // todo currentType assignment
      currentType = type;

      Object element;
      if(!namespaceURI.equals(curNsSwitchingFactory))
      {
         GenericObjectModelFactory newFactory = getFactory(namespaceURI);
         if(newFactory != curFactory)
         {
            element = newFactory.newRoot(parent, this, namespaceURI, localName, atts);
         }
         else
         {
            element = newFactory.newChild(parent, this, namespaceURI, localName, atts);
         }

         // still have to push since curNsSwitchingFactory needs to be updated to prevent
         // newRoot calls for the children
         pushFactory(namespaceURI, localName, newFactory);
      }
      else
      {
         element = curFactory.newChild(parent, this, namespaceURI, localName, atts);
      }

      if(element == null)
      {
         pushAll(IGNORED);

         if(log.isTraceEnabled())
         {
            log.trace("ignored " + namespaceURI + ':' + qName);
         }
      }
      else
      {
         pushAll(element);
         pushAccepted(element);

         if(log.isTraceEnabled())
         {
            log.trace("accepted " + namespaceURI + ':' + qName);
         }
      }
   }

   public void endElement(String namespaceURI, String localName, String qName)
   {
      AllElement element = popAll();

      if(!accepted.isEmpty())
      {
         Object acceptedElement = peekAccepted();
         String characters = element.characters;
         if(characters != null)
         {
            characters = characters.trim();
            if(characters.length() > 0)
            {
               curFactory.setValue(acceptedElement, this, namespaceURI, localName, characters);
            }
         }
      }

      if(localName.equals(curNameSwitchingFactory) && namespaceURI.equals(curNsSwitchingFactory))
      {
         popFactory();
      }

      if(element.element != IGNORED)
      {
         popAccepted();
         Object parent = (accepted.isEmpty() ? null : peekAccepted());

         if(parent != null)
         {
            curFactory.addChild(parent, element.element, this, namespaceURI, localName);
         }
         else
         {
            root = curFactory.completeRoot(element.element, this, namespaceURI, localName);
         }
      }

      metadataCursor.endElement(namespaceURI, localName);
   }

   public void characters(char[] ch, int start, int length)
   {
      if(!accepted.isEmpty())
      {
         String str = String.valueOf(ch, start, length);
         AllElement allElement = peekAll();
         if(allElement.characters == null)
         {
            allElement.characters = str;
         }
         else
         {
            allElement.characters += str;
         }
      }
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
         if(te instanceof RuntimeException)
         {
            throw (RuntimeException)te;
         }

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
      return factory instanceof GenericObjectModelFactory ?
         (GenericObjectModelFactory)factory :
         new DelegatingObjectModelFactory(factory);
   }

   private void pushAccepted(Object o)
   {
      accepted.push(o);
   }

   private Object popAccepted()
   {
      return accepted.pop();
   }

   private Object peekAccepted()
   {
      return accepted.peek();
   }

   private void pushAll(Object o)
   {
      all.push(new AllElement(o));
   }

   private AllElement popAll()
   {
      return (AllElement)all.pop();
   }

   private AllElement peekAll()
   {
      return (AllElement)all.peek();
   }

   private static final class AllElement
   {
      public final Object element;
      public String characters;

      public AllElement(Object element)
      {
         this.element = element;
      }
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
