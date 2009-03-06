/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
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

import java.util.WeakHashMap;

import org.jboss.logging.Logger;
import org.jboss.util.xml.JBossEntityResolver;
import org.jboss.xb.binding.resolver.AbstractMutableSchemaResolver;

/**
 * A default SchemaBindingResolver that uses a JBossEntityResolver to locate
 * the schema xsd.
 * 
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
public class DefaultSchemaResolver extends AbstractMutableSchemaResolver
{
   private static Logger log = Logger.getLogger(DefaultSchemaResolver.class);

   /** Namespace to JBossXBBuilder binding class */
   private WeakHashMap<String, Class<?>> uriToClass = new WeakHashMap<String, Class<?>>();
   /** SchemaLocation to JBossXBBuilder binding class */
   private WeakHashMap<String, Class<?>> schemaLocationToClass = new WeakHashMap<String, Class<?>>();

   public DefaultSchemaResolver()
   {
      super(log);
   }

   public DefaultSchemaResolver(JBossEntityResolver resolver)
   {
      super(log, resolver);
   }

   /**
    * Registers a location for the namespace URI.<p>
    * This method delegates to mapSchemaLocation(nsUri, location).
    * 
    * This location is looked using the JBossEntityResolver, i.e. it is a classpath location
    * 
    * @param nsUri the namespace location
    * @param location the classpath location
    */
   public void addSchemaLocation(String nsUri, String location)
   {
      super.mapSchemaLocation(nsUri, location);
   }
   

   /**
    * Whether to parse annotations for this namespace.
    * This method delegates to parseXSDAnnotations(nsUri, value).
    * 
    * @param nsUri the namespace
    * @param value the value of the option
    */
   public void addSchemaParseAnnotations(String nsUri, Boolean value)
   {
      super.setParseXSDAnnotations(nsUri, value);
   }
   
   /**
    * Removes the parse annotation configuration for this namespace.
    * This method delegates to unsetParseXSDAnnotations(nsURI).
    * 
    * @param nsUri the namespace
    * @return the previous value
    */
   public Boolean removeSchemaParseAnnotations(String nsUri)
   {
      return super.unsetParseXSDAnnotations(nsUri);
   }

   /**
    * Registers a SchemaBindingInitializer for the namespace URI.
    * When the schema binding that corresponds to the namespace URI
    * is resolved, the init(SchemaBinding schema) method will be invoked on the
    * instance of SchemaBindingInitializer with the SchemaBinding returned from the
    * XsdBinder.bind() method.
    * 
    * This method delegates to mapSchemaInitializer(nsUri, sbiClassName).
    *
    * @param nsUri  the namespace URI to register the schema initializer for
    * @param sbiClassName  the class name SchemaBindingInitializer
    * @throws Exception for any error
    */
   public void addSchemaInitializer(String nsUri, String sbiClassName) throws Exception
   {
      super.mapSchemaInitializer(nsUri, sbiClassName);
   }

   /**
    * Registers an instance of SchemaBindingInitializer for the namespace URI.
    * When the schema binding that corresponds to the namespace URI
    * is resolved, the init(SchemaBinding schema) method will be invoked on the
    * instance of SchemaBindingInitializer with the SchemaBinding returned from the
    * XsdBinder.bind() method.
    * 
    * This method delegates to mapSchemaInitializer(nsUri, sbi).
    *
    * @param nsUri  the namespace URI to register the schema initializer for
    * @param sbi  an instance of SchemaBindingInitializer
    */
   public void addSchemaInitializer(String nsUri, SchemaBindingInitializer sbi)
   {
      super.mapSchemaInitializer(nsUri, sbi);
   }

   /**
    * Add an in-memory schema.
    *
    * @param nsUri schema namespace
    * @param reference the schema reference class name
    * @throws Exception for any error
    */
   public void addClassBinding(String nsUri, String reference) throws ClassNotFoundException
   {
      super.mapURIToClass(nsUri, reference);
   }

   public void addClassBinding(String nsUri, Class<?> clazz)
   {
      uriToClass.put(nsUri, clazz);
   }

   public Class<?> removeClassBinding(String nsUri)
   {
      return uriToClass.remove(nsUri);      
   }

   public void addClassBindingForLocation(String schemaLocation, Class<?> clazz)
   {
      schemaLocationToClass.put(schemaLocation, clazz);
   }
   
   public Class<?> removeClassBindingForLocation(String schemaLocation)
   {
      return schemaLocationToClass.remove(schemaLocation);
   }

   @Override
   protected Class<?>[] getClassesForSchemaLocation(String uri)
   {
      Class<?> c = schemaLocationToClass.get(uri);
      return c == null ? null : new Class<?>[]{c};
   }

   @Override
   protected Class<?>[] getClassesForURI(String uri)
   {
      Class<?> c = uriToClass.get(uri);
      return c == null ? null : new Class<?>[]{c};
   }

   public void mapLocationToClass(String schemaLocation, Class<?> clazz)
   {
      this.addClassBindingForLocation(schemaLocation, clazz);
   }

   public void mapLocationToClasses(String schemaLocation, Class<?>... classes)
   {
      throw new UnsupportedOperationException("This implementation supports schema location mapping to a single class only.");
   }

   public void mapURIToClass(String nsUri, Class<?> clazz)
   {
      this.addClassBinding(nsUri, clazz);
   }

   public void mapURIToClasses(String nsUri, String... reference) throws ClassNotFoundException
   {
      throw new UnsupportedOperationException("This implementation supports URI mapping to a single class only.");      
   }

   public void mapURIToClasses(String nsUri, Class<?>... clazz)
   {
      throw new UnsupportedOperationException("This implementation supports URI mapping to a single class only.");      
   }

   public Class<?>[] removeLocationToClassMapping(String schemaLocation)
   {
      Class<?> c = removeClassBindingForLocation(schemaLocation);
      return c == null ? null : new Class[]{c};
   }

   public Class<?>[] removeURIToClassMapping(String nsUri)
   {
      Class<?> c = this.removeClassBinding(nsUri);
      return c == null ? null : new Class<?>[]{c};
   }
}
