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

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jboss.logging.Logger;
import org.jboss.util.xml.JBossEntityResolver;
import org.w3c.dom.ls.LSInput;
import org.xml.sax.InputSource;

/**
 * A default SchemaBindingResolver that uses a JBossEntityResolver to locate
 * the schema xsd.
 * 
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
public class DefaultSchemaResolver implements SchemaBindingResolver
{
   private static Logger log = Logger.getLogger(DefaultSchemaResolver.class);

   private String baseURI;
   private JBossEntityResolver resolver;
   private boolean cacheResolvedSchemas = true;
   private Map schemasByUri = Collections.EMPTY_MAP;
   private Map schemaInitByUri = Collections.EMPTY_MAP;
   private Map schemaParseAnnotationsByUri = Collections.EMPTY_MAP;

   public DefaultSchemaResolver()
   {
      this(new JBossEntityResolver());
   }

   public DefaultSchemaResolver(JBossEntityResolver resolver)
   {
      this.resolver = resolver;
   }

   /**
    * @return true if resolved SchemaBinding's are cached, false otherwise
    */
   public boolean isCacheResolvedSchemas()
   {
      return cacheResolvedSchemas;
   }

   /**
    * Passing in true will make the schema resolver to cache successfully resolved
    * schemas (which is the default) with namespace URI being the identifier of a schema.
    * False will flush the cache and make the schema resolver to resolve schemas
    * on each request.
    * @param cacheResolvedSchemas
    */
   public void setCacheResolvedSchemas(boolean cacheResolvedSchemas)
   {
      this.cacheResolvedSchemas = cacheResolvedSchemas;
      if(!cacheResolvedSchemas)
      {
         schemasByUri = Collections.EMPTY_MAP;
      }
   }

   /**
    * Registers a location for the namespace URI.<p>
    * 
    * This location is looked using the JBossEntityResolver, i.e. it is a classpath location
    * 
    * @param nsUri the namespace location
    * @param location the classpath location
    */
   public void addSchemaLocation(String nsUri, String location)
   {
      resolver.registerLocalEntity(nsUri, location);
   }
   

   /**
    * Removes a location for the namespace URI.
    * 
    * @todo actually remove it rather than setting null
    * @param nsUri the namespace location
    */
   public void removeSchemaLocation(String nsUri)
   {
      resolver.registerLocalEntity(nsUri, null);
   }

   /**
    * Whether to parse annotations for this namespace.
    * 
    * @param nsUri the namespace
    * @param value the value of the option
    */
   public void addSchemaParseAnnotations(String nsUri, Boolean value)
   {
      if (nsUri == null)
         throw new IllegalArgumentException("Null namespace uri");
      if (value == null)
         throw new IllegalArgumentException("Null value");
      switch(schemaParseAnnotationsByUri.size())
      {
         case 0:
            schemaParseAnnotationsByUri = Collections.singletonMap(nsUri, value);
            break;
         case 1:
            schemaParseAnnotationsByUri = new HashMap(schemaParseAnnotationsByUri);
         default:
            schemaParseAnnotationsByUri.put(nsUri, value);
      }
   }
   
   /**
    * Removes the parse annotation configuration for this namespace
    * 
    * @param nsUri the namespace
    * @return the previous value
    */
   public Boolean removeSchemaParseAnnotations(String nsUri)
   {
      if (nsUri == null)
         throw new IllegalArgumentException("Null namespace uri");
      return (Boolean) schemaParseAnnotationsByUri.remove(nsUri);
   }
   
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
   public void addSchemaInitializer(String nsUri, String sbiClassName) throws Exception
   {
      if (sbiClassName == null)
         throw new IllegalArgumentException("Null class name");
      Class clazz = Thread.currentThread().getContextClassLoader().loadClass(sbiClassName);
      Object object = clazz.newInstance();
      if (object instanceof SchemaBindingInitializer == false)
         throw new IllegalArgumentException(clazz.getName() + " is not an instance of " + SchemaBindingInitializer.class.getName());
      SchemaBindingInitializer sbi = (SchemaBindingInitializer) object;
      addSchemaInitializer(nsUri, sbi);
   }

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
   public void addSchemaInitializer(String nsUri, SchemaBindingInitializer sbi)
   {
      if (nsUri == null)
         throw new IllegalArgumentException("Null namespace uri");
      if (sbi == null)
         throw new IllegalArgumentException("Null schema binding initializer");
      switch(schemaInitByUri.size())
      {
         case 0:
            schemaInitByUri = Collections.singletonMap(nsUri, sbi);
            break;
         case 1:
            schemaInitByUri = new HashMap(schemaInitByUri);
         default:
            schemaInitByUri.put(nsUri, sbi);
      }
   }

   /**
    * Unregisters and returns the SchemaBindingInitializer for the namespace URI.
    * @param nsUri  the namespace URI to unregister SchemaBindingInitializer for
    * @return  unregistered SchemaBindingInitializer for the namespace URI or null
    * if there was no SchemaBindingInitialzer registered for the namespace URI
    */
   public SchemaBindingInitializer removeSchemaInitializer(String nsUri)
   {
      if (nsUri == null)
         throw new IllegalArgumentException("Null namespace uri");
      return (SchemaBindingInitializer)schemaInitByUri.remove(nsUri);
   }

   public String getBaseURI()
   {
      return baseURI;
   }

   public void setBaseURI(String baseURI)
   {
      this.baseURI = baseURI;
   }

   /**
    * Uses the JBossEntityResolver.resolveEntity by:
    * 
    * 1. Using the nsUri as the systemID
    * 2. Using the schemaLocation as the systemID
    * 3. If that fails, the baseURI is not null, the xsd is located using URL(baseURL, schemaLocation)
    * 4. If the baseURI is null, the xsd is located using URL(schemaLocation)
    */
   public SchemaBinding resolve(String nsURI, String baseURI, String schemaLocation)
   {
      SchemaBinding schema = (SchemaBinding)schemasByUri.get(nsURI);
      if(schema != null)
      {
         return schema;
      }

      InputSource is = getInputSource(nsURI, baseURI, schemaLocation);
      
      if (is != null)
      {
         if( baseURI == null )
            baseURI = this.baseURI;

         Boolean processAnnotationsBoolean = (Boolean) schemaParseAnnotationsByUri.get(nsURI);
         boolean processAnnotations = (processAnnotationsBoolean == null) ? true : processAnnotationsBoolean.booleanValue();
         schema = XsdBinder.bind(is.getByteStream(), null, baseURI, processAnnotations);
      }

      if(schema != null)
      {
         schema.setSchemaResolver(this);
         SchemaBindingInitializer sbi = (SchemaBindingInitializer)schemaInitByUri.get(nsURI);
         if(sbi != null)
         {
            schema = sbi.init(schema);
         }

         if(schema != null && cacheResolvedSchemas)
         {
            if(schemasByUri == Collections.EMPTY_MAP)
            {
               schemasByUri = new HashMap();
            }
            schemasByUri.put(nsURI, schema);
         }
      }

      if(log.isTraceEnabled())
      {
         log.trace("resolved schema: " + schema);
      }

      return schema;
   }

   public LSInput resolveAsLSInput(String nsURI, String baseURI, String schemaLocation)
   {
      LSInput lsInput = null;
      InputSource is = getInputSource(nsURI, baseURI, schemaLocation);
      if (is != null)
      {
         String publicId = is.getPublicId();
         String systemId = is.getSystemId();
         lsInput = new LSInputAdaptor(publicId, systemId, baseURI);
         lsInput.setCharacterStream(is.getCharacterStream());
         lsInput.setByteStream(is.getByteStream());
         lsInput.setEncoding(is.getEncoding());
      }
      return lsInput;
   }

   private InputSource getInputSource(String nsURI, String baseURI, String schemaLocation)
   {
      boolean trace = log.isTraceEnabled();
      InputSource is = null;

      if( trace )
         log.trace("getInputSource, nsURI="+nsURI+", baseURI="+baseURI+", schemaLocation="+schemaLocation);

      // First try what is requested
      try
      {
         is = resolver.resolveEntity(nsURI, schemaLocation);
         if (trace)
            log.trace("Resolved schema using namespace as publicId and schemaLocation as systemId");
      }
      catch (Exception e)
      {
         if (trace)
            log.trace("Failed to use nsUri/schemaLocation", e);
      }

      // Next, try to use the baseURI to resolve the schema location
      if(baseURI == null)
      {
         baseURI = this.baseURI;
      }

      if (is == null &&  baseURI != null && schemaLocation != null)
      {
         try
         {
            URL url = new URL(baseURI);
            url = new URL(url, schemaLocation);
            String resolvedSchemaLocation = url.toString();
            // No point if the schema location was already absolute
            if (schemaLocation.equals(resolvedSchemaLocation) == false)
            {
               is = resolver.resolveEntity(null, url.toString());
               if( trace && is != null )
                  log.trace("Resolved schema location using baseURI");
            }
         }
         catch (Exception e)
         {
            if (trace)
               log.trace("Failed to use schema location with baseURI", e);
         }
      }

      // Finally, just try the namespace as the system id
      if (is == null &&  nsURI != null)
      {
         try
         {
            is = resolver.resolveEntity(null, nsURI);
            if( trace && is != null )
               log.trace("Resolved namespace as system id");
         }
         catch (Exception e)
         {
            if (trace)
               log.trace("Failed to use namespace as system id", e);
         }
      }
      if( trace )
      {
         log.trace("getInputSource, nsURI="+nsURI+", baseURI="
            +baseURI+", schemaLocation="+schemaLocation+", is="+is);
      }
      return is;
   }
}
