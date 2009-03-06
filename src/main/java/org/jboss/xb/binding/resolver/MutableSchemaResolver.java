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
package org.jboss.xb.binding.resolver;

import org.jboss.xb.binding.sunday.unmarshalling.SchemaBindingInitializer;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBindingResolver;

/**
 * A MutableSchemaResolver.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public interface MutableSchemaResolver extends SchemaBindingResolver
{
   /**
    * @return true if resolved SchemaBinding's are cached, false otherwise
    */
   boolean isCacheResolvedSchemas();
   
   /**
    * If the implementation supports caching of the resolved schema bindings then
    * passing in true will enable caching.
    * False will (possibly) flush the cache and make the schema resolver resolve schemas
    * on each request.
    * @param cacheResolvedSchemas
    */
   void setCacheResolvedSchemas(boolean cacheResolvedSchemas);
   
   /**
    * Registers a location for the namespace URI.<p>
    * 
    * The location may be a classpath location if the implementation supports searching in the classpath
    * (e.g. using JBossEntityResolver)
    * 
    * @param nsUri the namespace URI
    * @param location  the schema location
    */
   void mapSchemaLocation(String nsUri, String location);
   
   /**
    * Removes a location for the namespace URI.
    * 
    * @param nsUri the namespace location
    */
   void removeSchemaLocation(String nsUri);

   /**
    * Whether to parse XSD annotations for this namespace.
    * 
    * @param nsUri the namespace
    * @param value the value of the option
    */
   void setParseXSDAnnotations(String nsUri, boolean value);
   
   /**
    * Clears the flag to parse XSD annotations for the namespace URI.
    * The default setting for parsing XSD annotations will be applied to this namespace URI after this method is called.
    * 
    * @param nsURI  the namespace URI
    * @return  the value previously set or null, if no value was set for this namespace URI
    */
   Boolean unsetParseXSDAnnotations(String nsURI);

   /**
    * Registers a SchemaBindingInitializer for the namespace URI.
    * When the schema binding that corresponds to the namespace URI
    * is resolved, the init(SchemaBinding schema) method will be invoked on the
    * instance of SchemaBindingInitializer with the SchemaBinding returned from the
    * XsdBinder.bind() method.
    *
    * @param nsUri  the namespace URI to register the schema initializer for
    * @param sbiClassName  the class name SchemaBindingInitializer
    * @throws Exception for any error
    */
   void mapSchemaInitializer(String nsUri, String sbiClassName) throws Exception;

   /**
    * Registers an instance of SchemaBindingInitializer for the namespace URI.
    * When the schema binding that corresponds to the namespace URI
    * is resolved, the init(SchemaBinding schema) method will be invoked on the
    * instance of SchemaBindingInitializer with the SchemaBinding returned from the
    * XsdBinder.bind() method.
    *
    * @param nsUri  the namespace URI to register the schema initializer for
    * @param sbi  an instance of SchemaBindingInitializer
    */
   void mapSchemaInitializer(String nsUri, SchemaBindingInitializer sbi);

   /**
    * Unregisters and returns the SchemaBindingInitializer for the namespace URI.
    * @param nsUri  the namespace URI to unregister SchemaBindingInitializer for
    * @return  unregistered SchemaBindingInitializer for the namespace URI or null
    * if there was no SchemaBindingInitialzer registered for the namespace URI
    */
   SchemaBindingInitializer removeSchemaInitializer(String nsUri);

   /**
    * Maps a namespace URI to a class which will be used as the base for the SchemaBinding.
    * 
    * @param nsUri  the namespace URI
    * @param reference  fully qualified class name to build the SchemaBinding from
    * @throws ClassNotFoundException if the reference cannot be loaded
    */
   void mapURIToClass(String nsUri, String reference) throws ClassNotFoundException;

   /**
    * Maps a namespace URI to a class which will be used as the base for the SchemaBinding.
    * 
    * @param nsUri  the namespace URI
    * @param clazz  class to build the SchemaBinding from
    */
   void mapURIToClass(String nsUri, Class<?> clazz);

   /**
    * Maps a namespace URI to an array of classes that will be used as the base for the SchemaBinding.
    * 
    * @param nsUri  the namespace URI
    * @param reference  array of fully qualified class names to build the SchemaBinding from
    * @throws ClassNotFoundException if at least one of the references cannot be loaded
    */
   void mapURIToClasses(String nsUri, String... reference) throws ClassNotFoundException;

   /**
    * Maps a namespace URI to an array of classes that will be used as the base for the SchemaBinding.
    * 
    * @param nsUri  the namespace URI
    * @param reference  array of classes to build the SchemaBinding from
    */
   void mapURIToClasses(String nsUri, Class<?>... clazz);

   /**
    * Removes namespace URI to class mapping
    * 
    * @param nsUri  the namespace URI to unmap
    * @return classes mapped to the namespace URI or null if the URI was not mapped.
    */
   Class<?>[] removeURIToClassMapping(String nsUri);

   /**
    * Maps schema location to a class which should be used as the base for the SchemaBinding.
    * 
    * @param schemaLocation  the location of the schema
    * @param reference  the fully qualified class name to build the SchemaBinding from
    * @throws ClassNotFoundException  if the reference cannot be loaded
    */
   void mapLocationToClass(String schemaLocation, String reference) throws ClassNotFoundException;

   /**
    * Maps schema location to a class which should be used as the base for the SchemaBinding.
    * 
    * @param schemaLocation  the location of the schema
    * @param clazz  the class to build the SchemaBinding from
    */
   void mapLocationToClass(String schemaLocation, Class<?> clazz);

   /**
    * Maps schema location to an array of classes that should be used as the base for the SchemaBinding.
    * 
    * @param schemaLocation  the location of the schema
    * @param reference  the array of fully qualified class names to build the SchemaBinding from
    * @throws ClassNotFoundException  if at least one of the references cannot be loaded
    */
   void mapLocationToClasses(String schemaLocation, String... reference) throws ClassNotFoundException;

   /**
    * Maps schema location to an array of classes that should be used as the base for the SchemaBinding.
    * 
    * @param schemaLocation  the location of the schema
    * @param classes  the array of classes to build the SchemaBinding from
    */
   void mapLocationToClasses(String schemaLocation, Class<?>... classes);
   
   /**
    * Removes schema location to class mapping.
    * 
    * @param schemaLocation  the schema location
    * @return  the array of classes used to build the SchemaBinding or null, if the schema location wasn't mapped.
    */
   Class<?>[] removeLocationToClassMapping(String schemaLocation);
}
