/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.util.jmx;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;

import java.util.HashMap;

import javax.management.Attribute;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.MalformedObjectNameException;

/**
 * A factory for producing MBean proxies.
 *
 * <p><b>Revisions:</b>
 * <p><b>20020321 Adrian Brock:</b>
 * <ul>
 * <li>Don't process attributes using invoke.
 * </ul>
 *
 * @author <a href="mailto:rickard.oberg@telkel.com">Rickard Öberg</a>.
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @author <a href="mailto:adrian.brock@happeningtimes.com">Adrian Brock</a>.
 * @version $Revision$
 */
public class MBeanProxy
   implements InvocationHandler
{
   /** The server to proxy invoke calls to. */
   private final MBeanServer server;

   /** The name of the object to invoke. */
   private final ObjectName name;

   /** The MBean's attributes */
   private HashMap attributeMap  = new HashMap();
   
   /**
    * Construct a MBeanProxy.
    */
   MBeanProxy(final ObjectName name)
   {
      this(name, MBeanServerLocator.locate());
   }
   
   /**
    * Construct a MBeanProxy.
    */
   MBeanProxy(final ObjectName name, final MBeanServer server)
   {
      this.name = name;
      this.server = server;
        
      // The MBean's attributes
      try
      {
         MBeanInfo info = server.getMBeanInfo(name);
         MBeanAttributeInfo[] attributes = info.getAttributes();

         for (int i = 0; i < attributes.length; ++i)
            attributeMap.put(attributes[i].getName(), attributes[i]);
      }
      catch (Exception e)
      {
         throw new RuntimeException("Error creating MBeanProxy: " + name);
      }
   }
       
   /**
    * Invoke the configured MBean via the target MBeanServer and decode
    * any resulting JMX exceptions that are thrown.
    */
   public Object invoke(final Object proxy,
                        final Method method,
                        Object[] args)
      throws Throwable
   {
      String methodName = method.getName();

      // Get attribute
      if (methodName.startsWith("get") && args == null)
      {
         String attrName = methodName.substring(3);
         MBeanAttributeInfo info = (MBeanAttributeInfo) attributeMap.get(attrName);
         if (info != null)
         {
            String retType = method.getReturnType().getName();
            if (retType.equals(info.getType()))
            {
               try
               {
                  return server.getAttribute(name, attrName);
               }
               catch (Exception e)
               {
                  throw JMXExceptionDecoder.decode(e);
               }
            }
         }
      }

      // Is attribute
      else if (methodName.startsWith("is") && args == null)
      {
         String attrName = methodName.substring(2);
         MBeanAttributeInfo info = (MBeanAttributeInfo) attributeMap.get(attrName);
         if (info != null && info.isIs())
         {
            Class retType = method.getReturnType();
            if (retType.equals(Boolean.class) || retType.equals(Boolean.TYPE))
            {
               try
               {
                  return server.getAttribute(name, attrName);
               }
               catch (Exception e)
               {
                  throw JMXExceptionDecoder.decode(e);
               }
            }
         }
      }

      // Set attribute
      else if (methodName.startsWith("set") && args != null && args.length == 1)
      {
         String attrName = methodName.substring(3);
         MBeanAttributeInfo info = (MBeanAttributeInfo) attributeMap.get(attrName);
         if (info != null && method.getReturnType() == Void.TYPE)
         {
            try
            {
               server.setAttribute(name, new Attribute(attrName, args[0]));
               return null;
            }
            catch (Exception e)
            {
               throw JMXExceptionDecoder.decode(e);
            }
         }
      }

      // Operation
      if (args == null) args = new Object[0];

      // convert the parameter types to strings for JMX
      Class[] types = method.getParameterTypes();
      String[] sig = new String[types.length];
      for (int i = 0; i < types.length; i++) {
         sig[i] = types[i].getName();
      }

      // invoke the server and decode JMX exceptions
      try {
         return server.invoke(name, methodName, args, sig);
      }
      catch (Exception e) {
         throw JMXExceptionDecoder.decode(e);
      }
   }


   ///////////////////////////////////////////////////////////////////////////
   //                            Factory Methods                            //
   ///////////////////////////////////////////////////////////////////////////

   /**
    * Create an MBean proxy.
    *
    * @param intf    The interface which the proxy will implement.
    * @param name    A string used to construct the ObjectName of the
    *                MBean to proxy to.
    * @return        A MBean proxy.
    *
    * @throws MalformedObjectNameException    Invalid object name.
    */
   public static Object create(final Class intf, final String name)
      throws MalformedObjectNameException
   {
      return Proxy.newProxyInstance(intf.getClassLoader(),
                                    new Class[] { intf },
                                    new MBeanProxy(new ObjectName(name)));
   }

   /**
    * Create an MBean proxy.
    *
    * @param intf      The interface which the proxy will implement.
    * @param name      A string used to construct the ObjectName of the
    *                  MBean to proxy to.
    * @param server    The MBeanServer that contains the MBean to proxy to.
    * @return          A MBean proxy.
    *
    * @throws MalformedObjectNameException    Invalid object name.
    */
   public static Object create(final Class intf,
                               final String name,
                               final MBeanServer server)
      throws MalformedObjectNameException
   {
      return Proxy.newProxyInstance
         (intf.getClassLoader(),
          new Class[] { intf },
          new MBeanProxy(new ObjectName(name), server));
   }    
   
   /**
    * Create an MBean proxy.
    *
    * @param intf    The interface which the proxy will implement.
    * @param name    The name of the MBean to proxy invocations to.
    * @return        A MBean proxy.
    */
   public static Object create(final Class intf, final ObjectName name)
   {
      return Proxy.newProxyInstance(intf.getClassLoader(),
                                    new Class[] { intf },
                                    new MBeanProxy(name));
   }

   /**
    * Create an MBean proxy.
    *
    * @param intf      The interface which the proxy will implement.
    * @param name      The name of the MBean to proxy invocations to.
    * @param server    The MBeanServer that contains the MBean to proxy to.
    * @return          A MBean proxy.
    */
   public static Object create(final Class intf,
                               final ObjectName name,
                               final MBeanServer server)
   {
      return Proxy.newProxyInstance(intf.getClassLoader(),
                                    new Class[] { intf },
                                    new MBeanProxy(name, server));
   }
}

