/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

import org.xml.sax.Attributes;

import java.lang.reflect.Method;

/**
 * todo come up with a nicer class name
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class DelegatingObjectModelFactory
   implements GenericObjectModelFactory
{
   private final ObjectModelFactory typedFactory;

   public DelegatingObjectModelFactory(ObjectModelFactory typedFactory)
   {
      this.typedFactory = typedFactory;
   }

   public Object newRoot(Object root,
                         ContentNavigator navigator,
                         String namespaceURI,
                         String localName,
                         Attributes attrs)
   {
      return typedFactory.newRoot(root, navigator, namespaceURI, localName, attrs);
   }

   public Object newChild(Object parent,
                          ContentNavigator navigator,
                          String namespaceURI,
                          String localName,
                          Attributes attrs)
   {
      Method method = ObjectModelBuilder.getMethodForElement(typedFactory,
         "newChild",
         new Class[]{
            parent.getClass(),
            ContentNavigator.class,
            String.class,
            String.class,
            Attributes.class
         });

      Object child = null;
      if(method != null)
      {
         child = ObjectModelBuilder.invokeFactory(typedFactory,
            method,
            new Object[]{
               parent,
               navigator,
               namespaceURI,
               localName,
               attrs
            });
      }
      return child;
   }

   public void addChild(Object parent,
                        Object child,
                        ContentNavigator navigator,
                        String namespaceURI,
                        String localName)
   {
      Method method = ObjectModelBuilder.getMethodForElement(typedFactory,
         "addChild",
         new Class[]{
            parent.getClass(),
            child.getClass(),
            ContentNavigator.class,
            String.class,
            String.class
         });

      if(method != null)
      {
         ObjectModelBuilder.invokeFactory(typedFactory,
            method,
            new Object[]{
               parent,
               child,
               navigator,
               namespaceURI,
               localName
            });
      }
   }

   public void setValue(Object o, ContentNavigator navigator, String namespaceURI, String localName, String value)
   {
      Method method = ObjectModelBuilder.getMethodForElement(typedFactory,
         "setValue",
         new Class[]{
            o.getClass(),
            ContentNavigator.class,
            String.class,
            String.class,
            String.class
         });

      if(method != null)
      {
         ObjectModelBuilder.invokeFactory(typedFactory,
            method,
            new Object[]{
               o,
               navigator,
               namespaceURI,
               localName,
               value
            });
      }
   }
}
