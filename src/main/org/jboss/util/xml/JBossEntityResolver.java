/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.util.xml;

// $Id$

import org.jboss.logging.Logger;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

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

   private static Hashtable entities = new Hashtable();
   private boolean entityResolved = false;

   static
   {
      // the key does not really matter, neither does the location, we only look for the value
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
      registerEntity("-//JBoss//DTD Web Application 2.2//EN", "jboss-web.dtd");
      registerEntity("-//JBoss//DTD Web Application 2.3//EN", "jboss-web_3_0.dtd");
      registerEntity("-//JBoss//DTD Web Application 2.3V2//EN", "jboss-web_3_2.dtd");
      registerEntity("-//JBoss//DTD MBean Service 3.2//EN", "jboss-service_3_2.dtd");
      registerEntity("-//JBoss//DTD Application Client 3.2//EN", "jboss-client_3_2.dtd");
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
    * Returns DTD/Schema inputSource. If DTD/Schema was found in the hashtable and inputSource
    * was created flag isEntityResolved is set to true.
    *
    * @param publicId - Public ID of DTD, or null if it is a schema
    * @param systemId - the system ID of DTD or Schema
    * @return InputSource of entity
    */
   public InputSource resolveEntity(String publicId, String systemId)
   {
      entityResolved = false;

      // nothing to resolve
      if (publicId == null && systemId == null)
         return null;

      InputSource inputSource = null;
      String entityFileName = getLocalEntityName(publicId, systemId);
      if (entityFileName != null)
      {
         try
         {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = loader.getResourceAsStream(entityFileName);
            if (inputStream != null)
               inputSource = new InputSource(inputStream);
         }
         catch (Exception e)
         {
            log.error("Cannot load local entity: " + entityFileName);
         }
      }

      entityResolved = (inputSource != null);
      return inputSource;
   }

   /**
    * Get the local entity name by looking it up in the entities Map
    *
    * @param publicId the public id for DTD, probably null for schema
    * @param systemId the system id for the DTD, we ignore the location
    * @return the local filename
    */
   private String getLocalEntityName(String publicId, String systemId)
   {
      String filename = null;

      // First try the public id
      if (publicId != null)
         filename = (String) entities.get(publicId);

      // Next try the system id
      if (filename == null && systemId != null)
         filename = (String) entities.get(systemId);

      // Finally see if we know the file name
      if (filename == null && systemId != null)
      {
         try
         {
            URL url = new URL(systemId);
            String path = url.getPath();
            int slash = path.lastIndexOf('/');
            filename = path.substring(slash + 1);
         }
         catch (MalformedURLException ignored)
         {
            log.trace("SystemId is not a url: " + systemId, ignored);
            return null;
         }
      }

      // at this point we have a filename, even if it is not
      // registered with this entity resolver
      if (entities.values().contains(filename) == false)
         log.warn("Entity is not registered, publicId=" + publicId + " systemId=" + systemId);

      if (filename.endsWith(".dtd"))
         filename = "dtd/" + filename;
      else if (filename.endsWith(".xsd"))
         filename = "schema/" + filename;

      return filename;
   }

   /**
    * Returns the boolean value to inform id DTD was found in the XML file or not
    *
    * @return boolean - true if DTD was found in XML
    */
   public boolean isEntityResolved()
   {
      return entityResolved;
   }
}
