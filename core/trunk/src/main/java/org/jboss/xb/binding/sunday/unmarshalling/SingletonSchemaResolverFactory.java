/*
* JBoss, Home of Professional Open Source
* Copyright 2006, JBoss Inc., and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jboss.xb.binding.sunday.unmarshalling;

import org.jboss.logging.Logger;
import org.jboss.xb.binding.resolver.MultiClassSchemaResolver;
import org.jboss.xb.binding.resolver.MutableSchemaResolver;

/**
 * SingletonSchemaResolverFactory.
 * 
 * @author <a href="ales.justin@jboss.com">Ales Justin</a>
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision$
 */
public class SingletonSchemaResolverFactory implements SchemaResolverFactory
{
   /** The log */
   private static final Logger log = Logger.getLogger(SingletonSchemaResolverFactory.class);

   /** The factory instance */
   private static final SingletonSchemaResolverFactory singleton = new SingletonSchemaResolverFactory();

   /** The resolver */
   private final MutableSchemaResolver resolver = new MultiClassSchemaResolver();

   /**
    * Get the factory instance
    * 
    * @return the instance
    */
   public static SingletonSchemaResolverFactory getInstance()
   {
      return singleton;
   }

   /**
    * Create a new SingletonSchemaResolverFactory.
    */
   private SingletonSchemaResolverFactory()
   {
      // old initializer 
      //addSchema("urn:jboss:aop-beans:1.0", "org.jboss.aop.microcontainer.beans.xml.AOPBeansSchemaInitializer", Boolean.FALSE);
      addSchema("urn:jboss:spring-beans:2.0", "org.jboss.spring.deployment.xml.SpringSchemaInitializer", Boolean.FALSE);
      addSchema("urn:jboss:osgi-beans:1.0", "org.jboss.osgi.deployment.xml.OSGiSchemaInitializer", Boolean.FALSE);
      addSchema("urn:jboss:seam-components:1.0", "org.jboss.seam.ioc.microcontainer.xml.SeamSchemaInitializer", Boolean.FALSE);
      // new jaxb
      addJaxbSchema("urn:jboss:aop-beans:1.0", "org.jboss.aop.microcontainer.beans.metadata.AOPDeployment");
      addJaxbSchema("urn:jboss:bean-deployer", "org.jboss.kernel.plugins.deployment.AbstractKernelDeployment10");
      addJaxbSchema("urn:jboss:bean-deployer:2.0", "org.jboss.kernel.plugins.deployment.AbstractKernelDeployment");
      addJaxbSchema("urn:jboss:javabean:1.0", "org.jboss.javabean.plugins.jaxb.JavaBean10");
      addJaxbSchema("urn:jboss:javabean:2.0", "org.jboss.javabean.plugins.jaxb.JavaBean20");
      addJaxbSchema("urn:jboss:policy:1.0", "org.jboss.beans.metadata.plugins.policy.AbstractPolicyMetaData");
      addJaxbSchema("urn:jboss:classloading:1.0", "org.jboss.classloading.spi.metadata.ClassLoadingMetaData10");
      addJaxbSchema("urn:jboss:classloader:1.0", "org.jboss.classloading.spi.vfs.metadata.VFSClassLoaderFactory10");
      addJaxbSchema("urn:jboss:deployers:2.0", "org.jboss.deployers.vfs.plugins.xb.SchemaResolverDeployerMetaData");
   }

   public MutableSchemaResolver getSchemaBindingResolver()
   {
      return resolver;
   }

   /**
    * Add a schema
    * 
    * @param namespace the namespace
    * @param initializer the initializer
    * @return true when added
    */
   protected boolean addSchema(String namespace, String initializer)
   {
      try
      {
         resolver.mapSchemaInitializer(namespace, initializer);
         if (log.isTraceEnabled())
            log.trace("Mapped initializer '" + namespace + "' to '" + initializer + "'");
         return true;
      }
      catch (Exception ignored)
      {
         if (log.isTraceEnabled())
            log.trace("Ignored: ", ignored);
         return false;
      }
   }

   /**
    * Add a schema
    * 
    * @param namespace the namespace
    * @param initializer the initializer
    * @param parseAnnotations whether to parse annotations
    * @return true when added
    */
   protected boolean addSchema(String namespace, String initializer, Boolean parseAnnotations)
   {
      if (addSchema(namespace, initializer) == false)
         return false;
      setParseAnnotations(namespace, parseAnnotations);
      return true;
   }

   /**
    * Add a schema
    * 
    * @param namespace the namespace
    * @param initializer the initializer
    * @param location the location
    * @return true when added
    */
   protected boolean addSchema(String namespace, String initializer, String location)
   {
      if (addSchema(namespace, initializer) == false)
         return false;
      try
      {
         resolver.mapSchemaLocation(namespace, location);
         if (log.isTraceEnabled())
            log.trace("Mapped location '" + namespace + "' to '" + location + "'");
         return true;
      }
      catch (Exception ignored)
      {
         if (log.isTraceEnabled())
            log.trace("Ignored: ", ignored);
         return false;
      }
   }

   /**
    * Add a schema
    * 
    * @param namespace the namespace
    * @param initializer the initializer
    * @param location the location
    * @param parseAnnotations whether to parse annotations
    * @return true when added
    */
   protected boolean addSchema(String namespace, String initializer, String location, Boolean parseAnnotations)
   {
      if (addSchema(namespace, initializer, location) == false)
         return false;
      setParseAnnotations(namespace, parseAnnotations);
      return true;
   }

   /**
    * Set the parse annotations for a schema
    * 
    * @param namespace the namespace
    * @param parseAnnotations whether to parse annotations
    */
   protected void setParseAnnotations(String namespace, Boolean parseAnnotations)
   {
      if(parseAnnotations != null)
         resolver.setParseXSDAnnotations(namespace, parseAnnotations);
      else
         resolver.unsetParseXSDAnnotations(namespace);
      if (log.isTraceEnabled())
         log.trace("Parse annotations '" + namespace + "' set to '" + parseAnnotations + "'");
   }

   /**
    * Add a schema.
    *
    * @param namespace the namespace
    * @param reference the schema reference class
    * @return true when added
    */
   public boolean addJaxbSchema(String namespace, String reference)
   {
      try
      {
         resolver.mapURIToClass(namespace, reference);
         if (log.isTraceEnabled())
            log.trace("Mapped '" + namespace + "' to '" + reference + "'");
         return true;
      }
      catch (Exception ignored)
      {
         if (log.isTraceEnabled())
            log.trace("Ignored: ", ignored);
         return false;
      }
   }
}
