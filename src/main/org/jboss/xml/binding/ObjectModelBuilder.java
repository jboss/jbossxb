/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

import org.apache.log4j.Category;
import org.xml.sax.Attributes;

import java.util.Stack;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * An instance of this class translates SAX events into org.jboss.xml.binding.ObjectModelFactory calls
 * such as newChild, addChild and setValue.
 * This implementation is not thread-safe.
 *
 * @version <tt>$Revision$</tt>
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 */
public class ObjectModelBuilder
   implements ContentNavigator
{
   /** logger */
   private static final Category log = Category.getInstance(ObjectModelBuilder.class);

   /** the object that is pushed in the stack when the element read from the XML field is ignored by the
    *  metadata factory */
   private static final Object IGNORED = new Object();

   /** the stack of all the metadata objects including IGNORED */
   private Stack all = new Stack();
   /** the stack of only accepted metadata objects */
   private Stack accepted = new Stack();

   /** default object model factory */
   private ObjectModelFactory defaultFactory;
   /** factories mapped to namespace URIs */
   private Map factoriesToNs = Collections.EMPTY_MAP;

   /** content */
   private Content content;

   /** the value of a simple element (i.e. the element that does not contain nested elements) being read */
   private StringBuffer value = new StringBuffer();

   // Public

   public void mapFactoryToNamespace(ObjectModelFactory factory, String namespaceUri)
   {
      if(factoriesToNs == Collections.EMPTY_MAP)
      {
         factoriesToNs = new HashMap();
      }
      factoriesToNs.put(namespaceUri, factory);
   }

   public Object build(ObjectModelFactory defaultFactory, Object root, Content content)
           throws Exception
   {
      this.defaultFactory = defaultFactory;
      this.content = content;

      all.clear();
      accepted.clear();
      value.delete(0, value.length());

      if(root == null)
         root = defaultFactory.startDocument();
      all.push(root);
      accepted.push(root);

      content.build(this);

      root = all.pop();
      accepted.pop();
      defaultFactory.endDocument(root);

      return root;
   }

   // ContentNavigator implementation

   public String getChildContent(String namespaceURI, String qName)
   {
      return content.getChildContent(namespaceURI, qName);
   }

   // Public

   public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
   {
      Object parent = null;
      if(!accepted.isEmpty())
         parent = accepted.peek();

      Object factory = getFactory(namespaceURI);
      Object element = newChild(factory, parent, this, namespaceURI, localName, atts);
      if(element == null)
      {
         all.push(IGNORED);
         log.debug("ignored> " + namespaceURI + ':' + qName);
      }
      else
      {
         all.push(element);
         accepted.push(element);
         log.debug("accepted> " + namespaceURI + ':' + qName);
      }
   }

   public void endElement(String namespaceURI, String localName, String qName)
   {
      Object factory = null;

      if(value.length() > 0)
      {
         Object element = accepted.peek();
         factory = getFactory(namespaceURI);
         setValue(factory, element, this, namespaceURI, localName, value.toString().trim());
         value.delete(0, value.length());
      }

      Object element = all.pop();
      if(element != IGNORED)
      {
         element = accepted.pop();
         Object parent = accepted.peek();

         if(factory == null)
         {
            factory = getFactory(namespaceURI);
         }

         addChild(factory, parent, element, this);
      }
   }

   public void characters(char[] ch, int start, int length)
   {
      value.append(ch, start, length);
   }

   // Private

   private Object getFactory(String namespaceUri)
   {
      Object factory = factoriesToNs.get(namespaceUri);
      if(factory == null)
      {
         factory = defaultFactory;
      }
      return factory;
   }

   private static Object newChild(Object factory,
                                  Object element,
                                  ContentNavigator navigator,
                                  String namespaceURI,
                                  String qName,
                                  Attributes atts)
   {
      Method method = getMethodForElement(
         factory,
         "newChild",
         new Class[]{
            element.getClass(),
            ContentNavigator.class,
            String.class,
            String.class,
            Attributes.class
         }
      );

      Object child = null;
      if(method != null)
      {
         child = invokeFactory(
            factory,
            method,
            new Object[]{
               element,
               navigator,
               namespaceURI,
               qName,
               atts
            }
         );
      }
      else
      {
         log.debug("No newChild method found for " + element.getClass().getName());
      }

      return child;
   }

   private static void addChild(Object factory, Object parent, Object element, ContentNavigator navigator)
   {
      Method method = getMethodForElement(
         factory,
         "addChild",
         new Class[]{
            parent.getClass(),
            element.getClass(),
            ContentNavigator.class
         }
      );

      if(method != null)
      {
         log.debug("addChild> element=" + element.getClass().getName());
         invokeFactory(
            factory,
            method,
            new Object[]{
               parent,
               element,
               navigator
            }
         );
      }
      else
      {
         log.debug("No addChild method found for " + element.getClass().getName());
      }
   }

   private static void setValue(Object factory,
                                Object element,
                                ContentNavigator navigator,
                                String namespaceURI,
                                String qName,
                                String value)
   {
      Method method = getMethodForElement(
         factory,
         "setValue",
         new Class[]{
            element.getClass(),
            ContentNavigator.class,
            String.class,
            String.class,
            String.class
         }
      );

      if(method != null)
      {
         log.debug("setValue> element=" + element.getClass().getName()
            + ", namespaceURI=" + namespaceURI + ", qName=" + qName + ", value=" + value
         );
         invokeFactory(
            factory,
            method,
            new Object[] {
               element,
               navigator,
               namespaceURI,
               qName,
               value
            }
         );
      }
      else
      {
         log.debug(
            "No setValue method found for " + element.getClass().getName()
            + ", namespaceURI=" + namespaceURI + ", qName=" + qName + ", value=" + value
         );
      }
   }

   private static Object invokeFactory(Object factory, Method method, Object[] args)
   {
      try
      {
         return method.invoke(factory, args);
      }
      catch (InvocationTargetException e)
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

   private static Method getMethodForElement(Object factory, String name, Class[] params)
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
}
