/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

import org.xml.sax.Attributes;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.HashMap;

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
   private final Map addMethodsByParent = new HashMap();

   public DelegatingObjectModelFactory(ObjectModelFactory typedFactory)
   {
      this.typedFactory = typedFactory;

      Method[] methods = typedFactory.getClass().getMethods();
      for(int i = 0; i < methods.length; ++i)
      {
         Method method = methods[i];
         if("addChild".equals(method.getName()))
         {
            Class parent = method.getParameterTypes()[0];
            AddMethods addMethods = (AddMethods)addMethodsByParent.get(parent);
            if(addMethods == null)
            {
               addMethods = new AddMethods(parent);
               addMethodsByParent.put(parent, addMethods);
            }
            addMethods.addMethod(method);
         }
      }
   }

   public Object newRoot(Object root,
                         UnmarshallingContext navigator,
                         String namespaceURI,
                         String localName,
                         Attributes attrs)
   {
      return typedFactory.newRoot(root, navigator, namespaceURI, localName, attrs);
   }

   public Object newChild(Object parent,
                          UnmarshallingContext navigator,
                          String namespaceURI,
                          String localName,
                          Attributes attrs)
   {
      Method method = ObjectModelBuilder.getMethodForElement(typedFactory,
         "newChild",
         new Class[]{
            parent.getClass(),
            UnmarshallingContext.class,
            String.class,
            String.class,
            Attributes.class
         }
      );

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
            }
         );
      }
      return child;
   }

   public void addChild(Object parent,
                        Object child,
                        UnmarshallingContext navigator,
                        String namespaceURI,
                        String localName)
   {
      /*
      Method method = ObjectModelBuilder.getMethodForElement(typedFactory,
         "addChild",
         new Class[]{
            parent.getClass(),
            child.getClass(),
            ContentNavigator.class,
            String.class,
            String.class
         });
         */
      AddMethods addMethods = (AddMethods)addMethodsByParent.get(parent.getClass());
      if(addMethods != null)
      {
         Method method = addMethods.getMethodForChild(child.getClass());
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
               }
            );
         }
      }
   }

   public void setValue(Object o, UnmarshallingContext navigator, String namespaceURI, String localName, String value)
   {
      Method method = ObjectModelBuilder.getMethodForElement(typedFactory,
         "setValue",
         new Class[]{
            o.getClass(),
            UnmarshallingContext.class,
            String.class,
            String.class,
            String.class
         }
      );

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
            }
         );
      }
   }

   public Object completedRoot(Object root, UnmarshallingContext navigator, String namespaceURI, String localName)
   {
      return root;
   }

   // Inner

   private static class AddMethods
   {
      private static final int DEFAULT_METHODS_SIZE = 10;

      public final Class parent;
      private Method[] methods = new Method[DEFAULT_METHODS_SIZE];
      private int totalMethods;

      public AddMethods(Class parent)
      {
         this.parent = parent;
      }

      public void addMethod(Method m)
      {
         if(totalMethods == methods.length)
         {
            Method[] tmp = methods;
            methods = new Method[methods.length + DEFAULT_METHODS_SIZE];
            System.arraycopy(tmp, 0, methods, 0, tmp.length);
         }
         methods[totalMethods++] = m;
      }

      public Method getMethodForChild(Class child)
      {
         Class closestParam = null;
         Method closestMethod = null;
         for(int i = 0; i < totalMethods; ++i)
         {
            Method method = methods[i];
            Class param = method.getParameterTypes()[1];
            if(param == child)
            {
               return method;
            }
            else if(param.isAssignableFrom(child) && (closestParam == null || closestParam.isAssignableFrom(param)))
            {
               closestParam = param;
               closestMethod = method;
            }
         }
         return closestMethod;
      }

      public boolean equals(Object o)
      {
         if(this == o)
         {
            return true;
         }
         if(!(o instanceof AddMethods))
         {
            return false;
         }

         final AddMethods addMethods = (AddMethods)o;

         if(!parent.equals(addMethods.parent))
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         return parent.hashCode();
      }
   }
}
