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

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.MalformedObjectNameException;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import javax.management.RuntimeMBeanException;
import javax.management.RuntimeErrorException;

/**
 * A factory for producing MBean proxies.
 *      
 * @author <a href="mailto:rickard.oberg@telkel.com">Rickard �berg</a>.
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @version $Revision$
 */
public class MBeanProxy
   implements InvocationHandler
{
   /** The server to proxy invoke calls to. */
   private final MBeanServer server;

   /** The name of the object to invoke. */
   private final ObjectName name;
   
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
      if (args == null) args = new Object[0];

      // convert the parameter types to strings for JMX
      Class[] types = method.getParameterTypes();
      String[] sig = new String[types.length];
      for (int i = 0; i < types.length; i++) {
         sig[i] = types[i].getName();
      }

      // invoke the server and decode JMX exceptions
      try {
         return server.invoke(name, method.getName(), args, sig);
      }
      catch (MBeanException e) {
         throw e.getTargetException();
      }
      catch (ReflectionException e) {
         throw e.getTargetException();
      }
      catch (RuntimeOperationsException e) {
         throw e.getTargetException();
      }
      catch (RuntimeMBeanException e) {
         throw e.getTargetException();
      }
      catch (RuntimeErrorException e) {
         throw e.getTargetError();
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
