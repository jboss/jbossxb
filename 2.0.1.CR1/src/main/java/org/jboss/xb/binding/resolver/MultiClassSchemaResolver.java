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

import java.util.WeakHashMap;

import org.jboss.logging.Logger;
import org.jboss.util.xml.JBossEntityResolver;

/**
 * A default SchemaBindingResolver that uses a JBossEntityResolver to locate
 * the schema xsd.
 * 
 * @author Scott.Stark@jboss.org
 * @author alex@jboss.org
 * @version $Revision: 2913 $
 */
public class MultiClassSchemaResolver extends AbstractMutableSchemaResolver
{
   private static Logger log = Logger.getLogger(MultiClassSchemaResolver.class);

   /** Namespace to JBossXBBuilder binding class */
   private WeakHashMap<String, Class<?>[]> uriToClass = new WeakHashMap<String, Class<?>[]>();
   /** SchemaLocation to JBossXBBuilder binding class */
   private WeakHashMap<String, Class<?>[]> schemaLocationToClass = new WeakHashMap<String, Class<?>[]>();

   public MultiClassSchemaResolver()
   {
      super(log);
   }

   public MultiClassSchemaResolver(JBossEntityResolver resolver)
   {
      super(log, resolver);
   }

   @Override
   protected Class<?>[] getClassesForSchemaLocation(String uri)
   {
      return schemaLocationToClass.get(uri);
   }

   @Override
   protected Class<?>[] getClassesForURI(String uri)
   {
      return uriToClass.get(uri);
   }

   public void mapLocationToClass(String schemaLocation, Class<?> clazz)
   {
      if(schemaLocation == null)
         throw new IllegalArgumentException("schemaLocation is null");
      if(clazz == null)
         throw new IllegalArgumentException("clazz is null");
      schemaLocationToClass.put(schemaLocation, new Class[]{clazz});
   }

   public void mapLocationToClasses(String schemaLocation, Class<?>... classes)
   {      
      if(schemaLocation == null)
         throw new IllegalArgumentException("schemaLocation is null");
      if(classes == null)
         throw new IllegalArgumentException("classes is null");
      schemaLocationToClass.put(schemaLocation, classes);
   }

   public void mapURIToClass(String nsUri, Class<?> clazz)
   {
      if(nsUri == null)
         throw new IllegalArgumentException("nsUri is null");
      if(clazz == null)
         throw new IllegalArgumentException("clazz is null");
      uriToClass.put(nsUri, new Class[]{clazz});
   }

   public void mapURIToClasses(String nsUri, Class<?>... classes)
   {
      if(nsUri == null)
         throw new IllegalArgumentException("schemaLocation is null");
      if(classes == null)
         throw new IllegalArgumentException("classes is null");
      uriToClass.put(nsUri, classes);
   }

   public Class<?>[] removeLocationToClassMapping(String schemaLocation)
   {
      return schemaLocationToClass.remove(schemaLocation);
   }

   public Class<?>[] removeURIToClassMapping(String nsUri)
   {
      return uriToClass.remove(nsUri);
   }
}
