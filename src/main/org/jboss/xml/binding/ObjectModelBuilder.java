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
import org.apache.xerces.xs.XSTypeDefinition;

import javax.xml.namespace.QName;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.LinkedList;
import java.util.StringTokenizer;
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
   implements ContentNavigator, JBossXBParser.ContentHandler
{
   /**
    * logger
    */
   private static final Logger log = Logger.getLogger(ObjectModelBuilder.class);

   /**
    * the object that is pushed in the stack when the element read from the XML field is ignored by the
    * metadata factory
    */
   private static final Object IGNORED = new Object();

   private Object root;

   /**
    * the stack of all the metadata objects including IGNORED
    */
   private Stack all = new StackImpl();
   /**
    * the stack of only accepted metadata objects
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

   private Map prefixToUri = new HashMap();

   /**
    * the value of a simple element (i.e. the element that does not contain nested elements) being read
    */
   private StringBuffer value = new StringBuffer();

   private XSTypeDefinition currentType;

   // Public

   public void mapFactoryToNamespace(GenericObjectModelFactory factory, String namespaceUri)
   {
      if(factoriesToNs == Collections.EMPTY_MAP)
      {
         factoriesToNs = new HashMap();
      }
      factoriesToNs.put(namespaceUri, factory);
   }

   public void init(GenericObjectModelFactory defaultFactory, Object root)
   {
      this.defaultFactory = defaultFactory;

      all.clear();
      accepted.clear();
      value.delete(0, value.length());

      if(root != null)
      {
         all.push(root);
         accepted.push(root);
      }
   }

   /*
   public Object build(GenericObjectModelFactory defaultFactory, Object root, Content content)
      throws Exception
   {
      this.defaultFactory = defaultFactory;
      this.content = content;

      all.clear();
      accepted.clear();
      value.delete(0, value.length());

      boolean popRoot = false;
      if(root != null)
      {
         all.push(root);
         accepted.push(root);
         popRoot = true;
      }

      content.build(this);

      if(popRoot)
      {
         root = all.pop();
         accepted.pop();
      }

      return this.root;
   }
   */

   // ContentNavigator implementation

   public Map getPrefixToNamespaceMap()
   {
      return Collections.unmodifiableMap(prefixToUri);
   }

   public String resolveNamespacePrefix(String prefix)
   {
      String uri;
      LinkedList prefixStack = (LinkedList) prefixToUri.get(prefix);
      if(prefixStack != null && !prefixStack.isEmpty())
      {
         uri = (String) prefixStack.getFirst();
      }
      else
      {
         uri = null;
      }
      return uri;
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
      String nsURI = resolveNamespacePrefix(prefix);

      return new QName(nsURI, local);
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
      LinkedList prefixStack = (LinkedList) prefixToUri.get(prefix);
      if(prefixStack == null || prefixStack.isEmpty())
      {
         prefixStack = new LinkedList();
         prefixToUri.put(prefix, prefixStack);
      }
      prefixStack.addFirst(uri);
   }

   public void endPrefixMapping(String prefix)
   {
      LinkedList prefixStack = (LinkedList) prefixToUri.get(prefix);
      if(prefixStack != null)
      {
         prefixStack.removeFirst();
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

   public void startElement(String namespaceURI, String localName, String qName, Attributes atts, XSTypeDefinition type)
   {
      Object parent = null;
      if(!accepted.isEmpty())
      {
         parent = accepted.peek();
      }

      // todo currentType assignment
      currentType = type;

      Object element;
      if(!namespaceURI.equals(curNsSwitchingFactory))
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
         curFactory = getFactory(namespaceURI);

         element = curFactory.newRoot(parent, this, namespaceURI, localName, atts);
         if(root == null)
         {
            root = element;
         }
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
            root = curFactory.completedRoot(element, this, namespaceURI, localName);
         }
      }
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
         log.error("Failed to invoke method " + method.getName(), e.getTargetException());
         throw new IllegalStateException("Failed to invoke method " + method.getName());
      }
      catch(Exception e)
      {
         log.error("Failed to invoke method " + method.getName(), e);
         throw new IllegalStateException("Failed to invoke method " + method.getName());
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
         throw new IllegalStateException(e.getMessage());
      }

      return method;
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
