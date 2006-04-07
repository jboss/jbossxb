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
package org.jboss.util.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import org.jboss.logging.Logger;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentReaderHashMap;

/**
 * Local entity resolver to handle standard J2EE DTDs and Schemas as well as JBoss
 * specific DTDs.
 * <p/>
 * Function boolean isEntityResolved() is here to avoid validation errors in
 * descriptors that do not have a DOCTYPE declaration.
 *
 * @author Scott.Stark@jboss.org
 * @author Thomas.Diesler@jboss.org
 * @author <a href="wiesed@gmail.com">Daniel Wiese</a>
 * @version $Revision$
 */
public class JBossEntityResolver implements EntityResolver
{
   private static final Logger log = Logger.getLogger(JBossEntityResolver.class);

   /** A class wide Map<String, String> of publicId/systemId to dtd/xsd file */
   private final static Map entities = new ConcurrentReaderHashMap();

   /** 
    * We maintain a main catalog (located in the current_server/conf directory). If a antity has no local 
    * catalog definition we delegate the request to this main catalog
    */
   private static CatalogLocation mainCatalog = null;

   /** A thread specific list of oasis catalog files **/
   private final static Map/*<CatalogLocation>*/catalogs = new ConcurrentReaderHashMap/*<CatalogLocation>*/();

   /** A local entities map that overrides the class level entities */
   private Map localEntities;

   private boolean entityResolved = false;

   /**
    * Register the mapping from the public id/system id to the dtd/xsd file
    * name. This overwrites any existing mapping.
    *
    * @param id  the DOCTYPE public id or system id such as
    * "-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 1.1//EN"
    * @param dtdFileName the simple dtd/xsd file name, "ejb-jar.dtd"
    */
   public static void registerEntity(String id, String dtdFileName)
   {
      entities.put(id, dtdFileName);
   }

   /**
    * Register the mapping from the public id/system id to the dtd/xsd file
    * name. This overwrites any existing mapping.
    *
    * @param id  the DOCTYPE public id or system id such as
    * "-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 1.1//EN"
    * @param dtdOrSchema the simple dtd/xsd file name, "ejb-jar.dtd"
    */
   public synchronized void registerLocalEntity(String id, String dtdOrSchema)
   {
      if (localEntities == null)
         localEntities = new ConcurrentReaderHashMap();
      localEntities.put(id, dtdOrSchema);
   }

   /**
    * Returns the boolean value to inform id DTD was found in the XML file or not
    * TODO: remove is not thread save
    *
    * @return boolean - true if DTD was found in XML
    */
   public boolean isEntityResolved()
   {
      return this.entityResolved;
   }

   /**
    * Returns DTD/Schema inputSource. The resolution logic is:
    * (1) Try to resolve the catalog using the old resolution sematic 
    * (see <code>resolveSpecial(publicId, systemId)</code>
    * 
    * (2) Try to locate a catalog file <code>jax-ws-catalog.xml</code> using
    * <code>Thread.currentThread().getContextClassLoader()</code>. If found this oasis catalog
    * file will be parsed and cached by using the location as a key.
    * 
    * (3) Try to resolve the entity using the current oasis catolog file (see (2)), if this will fail we will
    * use the main catalog file located in the <code>server/conf</code> directory.
    * 
    * @param publicId - Public ID of DTD, or null if it is a schema
    * @param systemId - the system ID of DTD or Schema
    * @return InputSource of entity
    * @throws SAXException - in error case
    * @throws IOException - in error case
    */
   public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
   {

      this.entityResolved=false;
      //nothing to do
      if (publicId==null && systemId==null){
         return null;
      }
      
      InputSource back = null;

      //old backward compatibility resolution
      back = this.resolveSpecial(publicId, systemId);

      final URL locationKey = CatalogLocation.lookupCatalogFiles();
      if (locationKey != null)
      {
         CatalogLocation cat = (CatalogLocation) catalogs.get(locationKey);
         // Create a new catolog location, if this location has none yet
         if (cat == null)
         {
            cat = new CatalogLocation(locationKey);
            //if this is the first request (from the main thread), this will be the main catalog
            //this will locate the jax-ws-catalog.xml in the conf directory.
            if (mainCatalog == null)
            {
               mainCatalog = cat;
            }
            //assotiate the parsed catalog with the location
            catalogs.put(locationKey, cat);
         }

         //new oasis catalog resolution
         if (back == null)
         {
            synchronized (cat)
            {
               back = cat.resolveEntity(publicId, systemId);
            }
            //if the current catalog could not resove the entity, ask the main catalog 
            if (back == null)
            {
               log.debug("Try to resolve the Entity (" + publicId + ") (" + systemId + ") using main catalog");
               synchronized (mainCatalog)
               {
                  back = mainCatalog.resolveEntity(publicId, systemId);
               }
            }
         }
      }
      
      //TODO: we should remove this
      if (back!=null){
         this.entityResolved=true;
      }
      return back;
   }

   /**
    * 
    * We use this method for backward compatibility reasons. 
    *  1. Check the publicId against the current registered values in the object/class
    *  mapping of entity name to dtd/schema file name. If found, the resulting
    *  file name is passed to the loadClasspathResource to locate the file as a
    *  classpath resource.
    *  
    *  2. Check the systemId against the current registered values in the class
    *  mapping of entity name to dtd/schema file name. If found, the resulting
    *  file name is passed to the loadClasspathResource to locate the file as a
    *  classpath resource.
    *  
    *  3. Strip the systemId name down to the simple file name by removing an URL
    *  style path elements (myschemas/x.dtd becomes x.dtd), and call
    *  loadClasspathResource to locate the simple file name as a classpath resource.
    *  
    *  4. Attempt to resolve the systemId as a URL from which the schema can be
    *  read. If the URL input stream can be opened this returned as the resolved
    *  input.
    * 
    * @param publicId - Public ID of DTD, or null if it is a schema
    * @param systemId - the system ID of DTD or Schema
    * @return InputSource of entity
    */
   private InputSource resolveSpecial(String publicId, String systemId)
   {
      // Look for a registered publicID
      InputSource inputSource = resolvePublicID(publicId);

      if (inputSource == null)
      {
         // Try to resolve the systemID from the registry
         inputSource = resolveSystemID(systemId);
      }

      return inputSource;
   }

   /**
    Load the schema from the class entity to schema file mapping.
    @see #registerEntity(String, String)

    @param publicId - the public entity name of the schema
    @return the InputSource for the schema file found on the classpath, null
    if the publicId is not registered or found.
    */
   private InputSource resolvePublicID(String publicId)
   {
      if (publicId == null)
         return null;

      InputSource inputSource = null;

      String filename = null;
      if (localEntities != null)
      {
         filename = (String) localEntities.get(publicId);
      }
      else if (filename == null)
      {
         filename = (String) entities.get(publicId);
      }

      if (filename != null)
      {
         try
         {
            InputStream inputStream = loadClasspathResource(filename);
            if (inputStream != null)
            {
               inputSource = new InputSource(inputStream);
               inputSource.setPublicId(publicId);
            }
         }
         catch (Exception e)
         {
            log.debug("Cannot load publicId from resource: " + filename, e);
         }
      }

      return inputSource;
   }

   /**
    Attempt to use the systemId as a URL from which the schema can be read. This
    checks to see whether the systemId is a key to an entry in the class
    entity map.

    @param systemId - the systemId
    @return the URL InputSource if the URL input stream can be opened, null
    if the systemId is not a URL or could not be opened.
    */
   private InputSource resolveSystemID(String systemId)
   {
      if (systemId == null)
         return null;

      InputSource inputSource = null;

      // Try to resolve the systemId as an entity key
      String filename = null;
      if (localEntities != null)
      {
         filename = (String) localEntities.get(systemId);
      }
      else if (filename == null)
      {
         filename = (String) entities.get(systemId);
      }

      if (filename != null)
      {
         InputStream is = loadClasspathResource(filename);
         if (is != null)
         {
            inputSource = new InputSource(is);
            inputSource.setSystemId(systemId);
         }
      }

      return inputSource;
   }

   /**
    Look for the resource name on the thread context loader resource path. This
    first simply tries the resource name as is, and if not found, the resource
    is prepended with either "dtd/" or "schema/" depending on whether the
    resource ends in ".dtd" or ".xsd".

    @param resource - the classpath resource name of the schema
    @return the resource InputStream if found, null if not found.
    */
   private InputStream loadClasspathResource(String resource)
   {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      URL url = loader.getResource(resource);
      if (url == null)
      {
         /* Prefix the simple filename with the schema type patch as this is the
          naming convention for the jboss bundled schemas.
          */
         if (resource.endsWith(".dtd"))
            resource = "dtd/" + resource;
         else if (resource.endsWith(".xsd"))
            resource = "schema/" + resource;
         url = loader.getResource(resource);
      }

      InputStream inputStream = null;
      if (url != null)
      {
         try
         {
            inputStream = url.openStream();
         }
         catch (IOException e)
         {
            log.debug("Failed to open url stream", e);
         }
      }
      return inputStream;
   }

}
