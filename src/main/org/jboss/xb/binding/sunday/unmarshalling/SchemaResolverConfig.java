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

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.jboss.logging.Logger;
import org.jboss.xb.binding.sunday.unmarshalling.DefaultSchemaResolver;

/**
 * SchemaResolverConfig.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision$
 */
public class SchemaResolverConfig implements SchemaResolverConfigMBean
{
   /** The log */
   private static final Logger log = Logger.getLogger(SchemaResolverConfig.class);
   
   /** The singleton schema resolver */
   protected static DefaultSchemaResolver resolver = (DefaultSchemaResolver) SingletonSchemaResolverFactory.getInstance().getSchemaBindingResolver();
   
   /** The initializers by namespace */
   protected Properties schemaInitializers;

   /** The locations by namespace */
   protected Properties schemaLocations;

   /** The parse annotations by namespace */
   protected Properties parseAnnotations;

   public Properties getSchemaInitializers()
   {
      return schemaInitializers;
   }

   public void setSchemaInitializers(Properties schemaInitializers)
   {
      this.schemaInitializers = schemaInitializers;
      if (schemaInitializers != null && schemaInitializers.size() != 0)
      {
         for (Iterator i = schemaInitializers.entrySet().iterator(); i.hasNext();)
         {
            Map.Entry entry = (Map.Entry) i.next();
            String namespace = (String) entry.getKey();
            String initializer = (String) entry.getValue();
            try
            {
               resolver.addSchemaInitializer(namespace, initializer);
            }
            catch (Exception ignored)
            {
               log.debug("Ignored: ", ignored);
            }
         }
      }
   }

   public Properties getSchemaLocations()
   {
      return schemaLocations;
   }

   public void setSchemaLocations(Properties schemaLocations)
   {
      this.schemaLocations = schemaLocations;
      if (schemaLocations != null && schemaLocations.size() != 0)
      {
         for (Iterator i = schemaLocations.entrySet().iterator(); i.hasNext();)
         {
            Map.Entry entry = (Map.Entry) i.next();
            String namespace = (String) entry.getKey();
            String location = (String) entry.getValue();
            resolver.addSchemaLocation(namespace, location);
         }
      }
   }

   public Properties getParseAnnotations()
   {
      return parseAnnotations;
   }

   public void setParseAnnotations(Properties parseAnnotations)
   {
      this.parseAnnotations = parseAnnotations;
      if (parseAnnotations != null && parseAnnotations.size() != 0)
      {
         for (Iterator i = parseAnnotations.entrySet().iterator(); i.hasNext();)
         {
            Map.Entry entry = (Map.Entry) i.next();
            String namespace = (String) entry.getKey();
            String value = (String) entry.getValue();
            Boolean booleanValue = Boolean.valueOf(value); 
            resolver.addSchemaParseAnnotations(namespace, booleanValue);
         }
      }
   }
}
