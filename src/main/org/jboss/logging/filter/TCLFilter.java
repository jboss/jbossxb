package org.jboss.logging.filter;

import java.lang.reflect.Method;
import java.net.URL;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

import org.jboss.util.collection.WeakSet;

/** An appender filter that accepts log events based on whether the thread
 context class loader has a classpath URL that has the DeployURL
 attribute as a substring. A sample usage would be:

   <appender name="JMX-CONSOLE" class="org.jboss.logging.appender.FileAppender">
      <errorHandler class="org.jboss.logging.util.OnlyOnceErrorHandler"/>
      <param name="File" value="${jboss.server.home.dir}/log/jmx-console.log"/>
      <layout class="org.apache.log4j.PatternLayout">
         <!-- The default pattern: Date Priority [Category] Message\n -->
         <param name="ConversionPattern" value="%d %-5p [%c] %m%n"/>
      </layout>
      <filter class="org.jboss.logging.filter.TCLFilter">
         <param name="AcceptOnMatch" value="true"/>
         <param name="DeployURL" value="jmx-console.war"/>
      </filter>
   </appender>

 @author Scott.Stark@jboss.org
 @version $Revison:$
 */
public class TCLFilter extends Filter
{
   /** The set of TCLs seen to match DeployURL */
   private WeakSet matchSet = new WeakSet();
   /** The set of TCLs seen to not match DeployURL */
   private WeakSet missSet = new WeakSet();
   /** The deployment URL string fragment to match against */
   private String deployURL;
   /** Whether a match should return ACCEPT or DENY */
   private boolean acceptOnMatch = true;

   public boolean isAcceptOnMatch()
   {
      return acceptOnMatch;
   }
   public void setAcceptOnMatch(boolean acceptOnMatch)
   {
      this.acceptOnMatch = acceptOnMatch;
   }
   public String getDeployURL()
   {
      return deployURL;
   }
   public void setDeployURL(String deployURL)
   {
      this.deployURL = deployURL;
   }

   public int decide(LoggingEvent event)
   {
      int ok = Filter.DENY;
      if( acceptOnMatch == true )
      {
         ok = Filter.DENY;
         if( isMatchingTCL() )
            ok = Filter.ACCEPT;
      }
      else
      {
         ok = Filter.ACCEPT;
         if( isMatchingTCL() )
            ok = Filter.DENY;
      }
      return ok;
   }

   /** Start with the current thread context class loader 
    * @return true if the caller tcl has a url matching our deployURL
    */ 
   private boolean isMatchingTCL()
   {
      ClassLoader tcl = Thread.currentThread().getContextClassLoader();
      if( matchSet.contains(tcl) )
         return true;
      if( missSet.contains(tcl) )
         return false;

      // Search the class loader URLs for a match
      ClassLoader cl = tcl;
      boolean match = false;
      while( cl != null )
      {
         URL[] urls = getClassLoaderURLs(cl);
         for(int n = 0; n < urls.length; n ++)
         {
            URL u = urls[n];
            String file = u.getFile();
            if( file.indexOf(deployURL) > 0 )
            {
               match = true;
               break;
            }
         }
         cl = cl.getParent();
      }
      if( match == true )
         matchSet.add(tcl);
      else
         missSet.add(tcl);

      return match;
   }

   /** Use reflection to access a URL[] getURLs method so that non-URLClassLoader
    class loaders that support this method can provide info.
    */
   private static URL[] getClassLoaderURLs(ClassLoader cl)
   {
      URL[] urls = {};
      try
      {
         Class returnType = urls.getClass();
         Class[] parameterTypes = {};
         Method getURLs = cl.getClass().getMethod("getURLs", parameterTypes);
         if( returnType.isAssignableFrom(getURLs.getReturnType()) )
         {
            Object[] args = {};
            urls = (URL[]) getURLs.invoke(cl, args);
         }
         if( urls == null || urls.length == 0 )
         {
            getURLs = cl.getClass().getMethod("getClasspath", parameterTypes);
            if( returnType.isAssignableFrom(getURLs.getReturnType()) )
            {
               Object[] args = {};
               urls = (URL[]) getURLs.invoke(cl, args);
            }
         }
      }
      catch(Exception ignore)
      {
      }
      return urls;
   }

}

