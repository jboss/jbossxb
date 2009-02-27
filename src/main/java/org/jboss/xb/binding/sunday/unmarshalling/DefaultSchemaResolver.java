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

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.jboss.logging.Logger;
import org.jboss.util.xml.JBossEntityResolver;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.builder.JBossXBBuilder;
import org.w3c.dom.ls.LSInput;
import org.xml.sax.InputSource;

/**
 * A default SchemaBindingResolver that uses a JBossEntityResolver to locate
 * the schema xsd.
 * 
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
public class DefaultSchemaResolver implements SchemaBindingResolver, UriToClassMapping
{
   private static Logger log = Logger.getLogger(DefaultSchemaResolver.class);

   private String baseURI;
   private JBossEntityResolver resolver;
   private boolean cacheResolvedSchemas = true;
   /** Namespace to SchemaBinding cache */
   private Map<String, SchemaBinding> schemasByUri = Collections.emptyMap();
   /** Namespace to JBossXBBuilder binding class */
   private WeakHashMap<String, Class<?>> uriToClass = new WeakHashMap<String, Class<?>>();
   /** SchemaLocation to JBossXBBuilder binding class */
   private WeakHashMap<String, Class<?>> schemaLocationToClass = new WeakHashMap<String, Class<?>>();
   /** Namespace to SchemaBindingInitializer */
   private Map<String, SchemaBindingInitializer> schemaInitByUri = Collections.emptyMap();
   /** Namespace to processAnnotations flag used with the XsdBinder.bind call */
   private Map<String, Boolean> schemaParseAnnotationsByUri = Collections.emptyMap();

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
         schemasByUri = Collections.emptyMap();
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
            schemaParseAnnotationsByUri = new HashMap<String, Boolean>(schemaParseAnnotationsByUri);
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
      return schemaParseAnnotationsByUri.remove(nsUri);
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
      Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(sbiClassName);
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
            schemaInitByUri = new HashMap<String, SchemaBindingInitializer>(schemaInitByUri);
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
      return schemaInitByUri.remove(nsUri);
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
      if (reference == null)
         throw new IllegalArgumentException("Null reference class");

      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      Class<?> clazz = cl.loadClass(reference);
      addClassBinding(nsUri, clazz);
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
      boolean trace = log.isTraceEnabled();
      // Was the schema binding based on the nsURI
      boolean foundByNS = false;
      SchemaBinding schema = schemasByUri.get(nsURI);
      if(schema != null)
      {
         if(trace)
            log.trace("resolved cached schema, nsURI="+nsURI+", schema: " + schema);
         return schema;
      }

      // Look for a class binding by schemaLocation
      Class<?> bindingClass = resolveClassFromSchemaLocation(schemaLocation, trace);
      if (bindingClass == null)
      {
         // Next look by namespace
         bindingClass = uriToClass.get(nsURI);
         if(bindingClass != null)
            foundByNS = true;
      }
      if (bindingClass != null)
      {
         if( trace )
         {
            log.trace("found bindingClass, nsURI="+nsURI
                  +", baseURI="+baseURI
                  +", schemaLocation="+schemaLocation
                  +", class="+bindingClass);
         }
         schema = JBossXBBuilder.build(bindingClass);
      }
      else
      {
         // Parse the schema
         InputSource is = getInputSource(nsURI, baseURI, schemaLocation);
         if( trace )
         {
            String msg = (is == null ? "couldn't find" : "found") +
                  " schema InputSource, nsURI=" + nsURI +
                  ", baseURI=" + baseURI + ", schemaLocation=" +
                  schemaLocation;
            log.trace(msg);
         }
         
         if (is != null)
         {
            if( baseURI == null )
               baseURI = this.baseURI;
   
            Boolean processAnnotationsBoolean = schemaParseAnnotationsByUri.get(nsURI);
            boolean processAnnotations = (processAnnotationsBoolean == null) ? true : processAnnotationsBoolean.booleanValue();
            try
            {
               schema = XsdBinder.bind(is.getByteStream(), null, baseURI, processAnnotations);
               foundByNS = true;
            }
            catch(RuntimeException e)
            {
               String msg = "Failed to parse schema for nsURI="+nsURI
                  +", baseURI="+baseURI
                  +", schemaLocation="+schemaLocation;
               throw new JBossXBRuntimeException(msg, e);
            }
         }
      }

      if(schema != null)
      {
         schema.setSchemaResolver(this);
         SchemaBindingInitializer sbi = schemaInitByUri.get(nsURI);
         if(sbi != null)
         {
            schema = sbi.init(schema);
         }

         if(schema != null && nsURI.length() > 0 && cacheResolvedSchemas && foundByNS)
         {
            if(schemasByUri.isEmpty())
            {
               schemasByUri = new HashMap<String, SchemaBinding>();
            }
            schemasByUri.put(nsURI, schema);
         }
      }

      if(trace)
      {
         log.trace("resolved schema: " + schema);
      }

      return schema;
   }

   /**
    * Lookup a binding class by schemaLocation. This first uses the
    * schemaLocation as is, then parses this as a URI to obtain the
    * final path component. This allows registration of a binding class
    * using jboss_5_0.dtd rather than http://www.jboss.org/j2ee/schema/jboss_5_0.xsd
    * 
    * @param schemaLocation the schema location from the parser
    * @param trace - logging trace flag
    * @return the binding class if found.
    */
   protected Class<?> resolveClassFromSchemaLocation(String schemaLocation,
         boolean trace)
   {
      Class<?> bindingClass = schemaLocationToClass.get(schemaLocation);
      if (bindingClass == null && schemaLocation != null && schemaLocation.length() > 0)
      {
         // Parse the schemaLocation as a uri to get the final path component
         try
         {
            URI url = new URI(schemaLocation);
            String path = url.getPath();
            if( path == null )
               path = url.getSchemeSpecificPart();
            int slash = path.lastIndexOf('/');
            String filename;
            if( slash >= 0 )
               filename = path.substring(slash + 1);
            else
               filename = path;
      
            if(path.length() == 0)
               return null;
      
            if (trace)
               log.trace("Mapped schemaLocation to filename: " + filename);
            bindingClass = schemaLocationToClass.get(filename);
         }
         catch (URISyntaxException e)
         {
            if (trace)
               log.trace("schemaLocation: is not a URI, using systemId as resource", e);
         }
      }
      return bindingClass;
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
         {
            String msg = (is == null ? "Couldn't resolve" : "Resolved") +
            " schema using namespace as publicId and schemaLocation as systemId";
            log.trace(msg);
         }
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

   public void mapUriToClass(String nsUri, String reference) throws ClassNotFoundException
   {
      addClassBinding(nsUri, reference);
   }

   public void mapUriToClass(String nsUri, Class<?> clazz)
   {
      addClassBinding(nsUri, clazz);
   }

   public void mapUriToClasses(String nsUri, String... reference) throws ClassNotFoundException
   {
      throw new UnsupportedOperationException("Namespace URI mapping to multiple classes is not supported by this implementation.");
   }

   public void mapUriToClasses(String nsUri, Class<?>... clazz)
   {
      throw new UnsupportedOperationException("Namespace URI mapping to multiple classes is not supported by this implementation.");
   }

   public Class<?>[] removeUriToClassMapping(String nsUri)
   {
      Class<?> clazz = removeClassBinding(nsUri);
      if(clazz != null)
         return new Class<?>[]{clazz};
      return null;
   }

}
