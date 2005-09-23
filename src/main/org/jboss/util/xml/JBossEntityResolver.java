/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.util.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentReaderHashMap;
import org.jboss.logging.Logger;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Local entity resolver to handle standard J2EE DTDs and Schemas as well as JBoss
 * specific DTDs.
 * <p/>
 * Function boolean isEntityResolved() is here to avoid validation errors in
 * descriptors that do not have a DOCTYPE declaration.
 *
 * @author Scott.Stark@jboss.org
 * @author Thomas.Diesler@jboss.org
 * @version $Revision$
 */
public class JBossEntityResolver implements EntityResolver
{
   private static final Logger log = Logger.getLogger(EntityResolver.class);

   private static Map entities = new ConcurrentReaderHashMap();
   private boolean entityResolved = false;

   static
   {
      registerEntity("http://java.sun.com/xml/ns/j2ee/j2ee_1_4.xsd", "j2ee_1_4.xsd");
      registerEntity("http://java.sun.com/xml/ns/j2ee/application_1_4.xsd", "application_1_4.xsd");
      registerEntity("http://java.sun.com/xml/ns/j2ee/application-client_1_4.xsd", "application-client_1_4.xsd");
      registerEntity("http://java.sun.com/xml/ns/j2ee/connector_1_5.xsd", "connector_1_5.xsd");
      registerEntity("http://java.sun.com/xml/ns/j2ee/ejb-jar_2_1.xsd", "ejb-jar_2_1.xsd");
      registerEntity("http://www.ibm.com/webservices/xsd/j2ee_web_services_client_1_1.xsd", "j2ee_web_services_client_1_1.xsd");
      registerEntity("http://www.ibm.com/webservices/xsd/j2ee_web_services_1_1.xsd", "j2ee_web_services_1_1.xsd");
      registerEntity("http://www.ibm.com/webservices/xsd/j2ee_jaxrpc_mapping_1_1.xsd", "j2ee_jaxrpc_mapping_1_1.xsd");
      registerEntity("http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd", "web-app_2_4.xsd");
      registerEntity("http://www.w3.org/2001/xml.xsd", "xml.xsd");

      // ejb related
      registerEntity("-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 1.1//EN", "ejb-jar.dtd");
      registerEntity("-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 2.0//EN", "ejb-jar_2_0.dtd");
      // ear stuff
      registerEntity("-//Sun Microsystems, Inc.//DTD J2EE Application 1.2//EN", "application_1_2.dtd");
      registerEntity("-//Sun Microsystems, Inc.//DTD J2EE Application 1.3//EN", "application_1_3.dtd");
      registerEntity("-//Sun Microsystems, Inc.//DTD J2EE Application Client 1.3//EN", "application-client_1_3.dtd");
      // connector descriptors
      registerEntity("-//Sun Microsystems, Inc.//DTD Connector 1.0//EN", "connector_1_0.dtd");
      // war meta-data
      registerEntity("-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN", "web-app_2_2.dtd");
      registerEntity("-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN", "web-app_2_3.dtd");
      // jboss-specific
      registerEntity("-//JBoss//DTD J2EE Application 1.3//EN", "jboss-app_3_0.dtd");
      registerEntity("-//JBoss//DTD J2EE Application 1.3V2//EN", "jboss-app_3_2.dtd");
      registerEntity("-//JBoss//DTD J2EE Application 1.4//EN", "jboss-app_4_0.dtd");
      registerEntity("-//JBoss//DTD JAWS//EN", "jaws.dtd");
      registerEntity("-//JBoss//DTD JAWS 2.4//EN", "jaws_2_4.dtd");
      registerEntity("-//JBoss//DTD JAWS 3.0//EN", "jaws_3_0.dtd");
      registerEntity("-//JBoss//DTD JBOSS//EN", "jboss.dtd");
      registerEntity("-//JBoss//DTD JBOSS 2.4//EN", "jboss_2_4.dtd");
      registerEntity("-//JBoss//DTD JBOSS 3.0//EN", "jboss_3_0.dtd");
      registerEntity("-//JBoss//DTD JBOSS 3.2//EN", "jboss_3_2.dtd");
      registerEntity("-//JBoss//DTD JBOSS 4.0//EN", "jboss_4_0.dtd");
      registerEntity("-//JBoss//DTD JBOSSCMP-JDBC 3.0//EN", "jbosscmp-jdbc_3_0.dtd");
      registerEntity("-//JBoss//DTD JBOSSCMP-JDBC 3.2//EN", "jbosscmp-jdbc_3_2.dtd");
      registerEntity("-//JBoss//DTD JBOSSCMP-JDBC 4.0//EN", "jbosscmp-jdbc_4_0.dtd");
      registerEntity("-//JBoss//DTD Web Application 2.2//EN", "jboss-web.dtd");
      registerEntity("-//JBoss//DTD Web Application 2.3//EN", "jboss-web_3_0.dtd");
      registerEntity("-//JBoss//DTD Web Application 2.3V2//EN", "jboss-web_3_2.dtd");
      registerEntity("-//JBoss//DTD Web Application 2.4//EN", "jboss-web_4_0.dtd");
      registerEntity("-//JBoss//DTD Application Client 3.2//EN", "jboss-client_3_2.dtd");
      registerEntity("-//JBoss//DTD Application Client 4.0//EN", "jboss-client_4_0.dtd");
      registerEntity("-//JBoss//DTD MBean Service 3.2//EN", "jboss-service_3_2.dtd");
      registerEntity("-//JBoss//DTD MBean Service 4.0//EN", "jboss-service_4_0.dtd");
      registerEntity("-//JBoss//DTD JBOSS Security Config 3.0//EN", "security_config.dtd");
      registerEntity("-//JBoss//DTD JBOSS JCA Config 1.0//EN", "jboss-ds_1_0.dtd");
      registerEntity("-//JBoss//DTD JBOSS JCA Config 1.5//EN", "jboss-ds_1_5.dtd");
      registerEntity("http://www.jboss.org/j2ee/schema/security-config_4_0.xsd", "security-config_4_0.xsd");
      registerEntity("urn:jboss:bean-deployer", "bean-deployer_1_0.xsd");
      registerEntity("urn:jboss:security-config:4.1", "security-config_4_1.xsd");
      registerEntity("urn:jboss:jndi-binding-service:1.0", "jndi-binding-service_1_0.xsd");
      registerEntity("urn:jboss:user-roles:1.0", "user-roles_1_0.xsd");
      // xml
      registerEntity("-//W3C//DTD/XMLSCHEMA 200102//EN", "XMLSchema.dtd");
      registerEntity("datatypes", "datatypes.dtd"); // This dtd doesn't have a publicId - see XMLSchema.dtd
   }

   /**
    * Register the mapping from the public id to the dtd file name.
    *
    * @param publicId    the DOCTYPE public id, "-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 1.1//EN"
    * @param dtdFileName the simple dtd file name, "ejb-jar.dtd"
    */
   public static void registerEntity(String publicId, String dtdFileName)
   {
      entities.put(publicId, dtdFileName);
   }

   /**
    Returns DTD/Schema inputSource. The resolution logic is:
    1. Check the publicId against the current registered values in the class
    mapping of entity name to dtd/schema file name.
    2. Attempt to use the systemId as a URL from which the schema can be read.
    3. 
    @param publicId - Public ID of DTD, or null if it is a schema
    @param systemId - the system ID of DTD or Schema
    @return InputSource of entity
    */
   public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
   {
      entityResolved = false;

      // nothing to resolve
      if( publicId == null && systemId == null )
         return null;

      boolean trace = log.isTraceEnabled();
      // Look for a registered publicID
      InputSource inputSource = resolvePublicID(publicId, trace);

      if( inputSource == null )
      {
         // Try to resolve the systemID as a absolute URL
         inputSource = resolveSystemID(systemId, trace);
      }

      if( inputSource == null )
      {
         // Try to resolve the systemID as as a classpath reference under dtd or schema
         inputSource = resolveClasspathName(systemId, trace);
      }


      entityResolved = (inputSource != null);
      return inputSource;
   }

   /**
    * Returns the boolean value to inform id DTD was found in the XML file or not
    *
    * @todo this is not thread safe and should be removed?
    *
    * @return boolean - true if DTD was found in XML
    */
   public boolean isEntityResolved()
   {
      return entityResolved;
   }

   /**
    Load the schema from the class entity to schema file mapping.
    @see #registerEntity(String, String)

    @param publicId - the public entity name of the schema
    @param trace - trace level logging flag
    @return the InputSource for the schema file found on the classpath, null
      if the publicId is not registered or found.
    */
   private InputSource resolvePublicID(String publicId, boolean trace)
   {
      if( publicId == null )
         return null;

      InputSource inputSource = null;
      String filename = (String) entities.get(publicId);
      if( filename != null )
      {
         if (trace)
            log.trace("Found entity from publicId=" + publicId + " fileName=" + filename);
         try
         {
            InputStream inputStream = loadClasspathResource(filename, trace);
            if( inputStream != null )
            {
               inputSource = new InputSource(inputStream);
               inputSource.setPublicId(publicId);
            }
         }
         catch(Exception e)
         {
            log.debug("Cannot load publicId from resource: " + filename, e);
         }
      }

      return inputSource;
   }

   /**
    Attempt to use the systemId as a URL from which the schema can be read. This
    first checks to see whether the systemId is a key to an entry in the class
    entity map. If that fails the systemId is used as a URL.

    @param systemId - the systemId
    @param trace - trace level logging flag
    @return the URL InputSource if the URL input stream can be opened, null
      if the systemId is not a URL or could not be opened.
    */
   private InputSource resolveSystemID(String systemId, boolean trace)
   {
      if( systemId == null )
         return null;

      InputSource inputSource = null;
      // First try to resolve the systemId as an entity key
      String filename = (String) entities.get(systemId);
      if ( filename != null)
      {
         if( trace )
            log.trace("Found entity systemId=" + systemId + " fileName=" + filename);
         InputStream is = loadClasspathResource(filename, trace);
         if( is != null )
         {
            inputSource = new InputSource(is);
            inputSource.setSystemId(systemId);
         }
      }

      // Next try to use the systemId as a URL to the schema
      if( inputSource == null )
      {
         try
         {
            URL url = new URL(systemId);
            InputStream is = url.openStream();
            inputSource = new InputSource(is);
            inputSource.setSystemId(systemId);
         }
         catch(MalformedURLException ignored)
         {
            if( trace )
               log.trace("SystemId is not a url: " + systemId, ignored);
            return null;
         }
         catch (IOException e)
         {
            log.debug("Failed to obtain InputStream from systemId: "+systemId, e);
         }
      }
      return inputSource;
   }

   /**
    Resolve the systemId as a classpath resource. If not found, the
    systemId is simply used as a classpath resource name.

    @param systemId - the system ID of DTD or Schema 
    @param trace - trace level logging flag
    @return the InputSource for the schema file found on the classpath, null
      if the systemId is not registered or found.
    */
   private InputSource resolveClasspathName(String systemId, boolean trace)
   {
      if( systemId == null )
         return null;

      String filename = systemId;
      // Parse the systemId as a uri to get the final path component
      try
      {
         URI url = new URI(systemId);
         String path = url.getPath();
         int slash = path.lastIndexOf('/');
         filename = path.substring(slash + 1);
         if (trace)
            log.trace("Mapped systemId to filename: " + filename);
      }
      catch (URISyntaxException e)
      {
         if (trace)
            log.trace("systemId: is not a URI, using systemId as resource", e);
      }

      // Resolve the filename as a classpath resource
      InputStream is = loadClasspathResource(filename, trace);
      InputSource inputSource = null;
      if( is != null )
      {
         inputSource = new InputSource(is);
         inputSource.setSystemId(systemId);
      }
      return inputSource;
   }

   /**
    Look for the resource name on the thread context loader resource path. This
    first simply tries the resource name as is, and if not found, the resource
    is prepended with either "dtd/" or "schema/" depending on whether the
    resource ends in ".dtd" or ".xsd".

    @param resource - the classpath resource name of the schema
    @param trace - trace level logging flag
    @return the resource InputStream if found, null if not found.
    */
   private InputStream loadClasspathResource(String resource, boolean trace)
   {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      URL url = loader.getResource(resource);
      if( url == null )
      {
         /* Prefix the simple filename with the schema type patch as this is the
               naming convention for the jboss bundled schemas.
            */
         if( resource.endsWith(".dtd") )
            resource = "dtd/" + resource;
         else if( resource.endsWith(".xsd") )
            resource = "schema/" + resource;
         url = loader.getResource(resource);
      }

      InputStream inputStream = null;
      if( url != null )
      {
         if( trace )
            log.trace(resource+" maps to URL: "+url);
         try
         {
            inputStream = url.openStream();
         }
         catch(IOException e)
         {
            log.debug("Failed to open url stream", e);
         }
      }
      return inputStream;
   }

}
