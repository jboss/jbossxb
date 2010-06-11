package org.jboss.xb.binding.parser;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.jboss.xb.binding.JBossXBRuntimeException;

/**
 * A ParserFactoryBindingUtil.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class ParserFactoryResolverUtil
{
   private static final String BINDING_IMPL_RESOURCE = "org/jboss/xb/binding/parser/JBossXBParserFactoryImpl.class";
   private static final String FACTORY_IMPL = "org.jboss.xb.binding.parser.JBossXBParserFactoryImpl";

   private static JBossXBParserFactory factory;

   static
   {
      ensureSingleBinding();
   }
   
   private static void ensureSingleBinding()
   {
      ClassLoader tcl = Thread.currentThread().getContextClassLoader();
      
      Enumeration<URL> urls;
      try
      {
         urls = tcl.getResources(BINDING_IMPL_RESOURCE);
      }
      catch (IOException e)
      {
         throw new JBossXBRuntimeException("Failed to locate " + BINDING_IMPL_RESOURCE, e);
      }

      if(!urls.hasMoreElements())
         throw new JBossXBRuntimeException("Failed to locate " + BINDING_IMPL_RESOURCE);

      URL url = urls.nextElement();
      System.out.println(url.toExternalForm());
      if(urls.hasMoreElements())
      {
         StringBuffer message = new StringBuffer();
         message.append("There should be only one ").append(BINDING_IMPL_RESOURCE).append(" in the classpath but found:\n");
         int urlsTotal = 1;
         message.append(urlsTotal).append(") ").append(url.toExternalForm()).append("\n");
         while (!urls.hasMoreElements())
         {
            url = urls.nextElement();
            ++urlsTotal;
            message.append(urlsTotal).append(") ").append(url.toExternalForm()).append("\n");
         }
         throw new JBossXBRuntimeException(message.toString());
      }
   
      Class<?> factoryCl;
      try
      {
         factoryCl = tcl.loadClass(FACTORY_IMPL);
      }
      catch (ClassNotFoundException e)
      {
         throw new JBossXBRuntimeException("Failed to load " + FACTORY_IMPL, e);
      }
      
      try
      {
         factory = (JBossXBParserFactory) factoryCl.newInstance();
      }
      catch(ClassCastException cce)
      {
         throw new JBossXBRuntimeException(FACTORY_IMPL + " doesn't implement " + JBossXBParserFactory.class.getName(), cce);
      }
      catch (Exception e)
      {
         throw new JBossXBRuntimeException("Failed to instantiate " + factoryCl.getName(), e);
      }
   }
   
   public static JBossXBParserFactory resolveParserFactory()
   {
      return factory;
   }
}
