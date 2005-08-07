/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package org.jboss.xb.binding.sunday.unmarshalling;

import java.io.InputStream;
import java.net.URL;

import org.jboss.util.xml.JBossEntityResolver;
import org.jboss.logging.Logger;
import org.xml.sax.InputSource;
import org.w3c.dom.ls.LSInput;

/**
 * A default SchemaBindingResolver that uses a JBossEntityResolver to locate
 * the schema xsd.
 * 
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
public class DefaultSchemaResolver
   implements SchemaBindingResolver
{
   private static Logger log = Logger.getLogger(DefaultSchemaResolver.class);

   private String baseURI;
   private JBossEntityResolver resolver;

   public DefaultSchemaResolver()
   {
      this(new JBossEntityResolver());
   }

   public DefaultSchemaResolver(JBossEntityResolver resolver)
   {
      this.resolver =  resolver;
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
    * The uses the JBossEntityResolver.resolveEntity by:
    * 1. Using the nsUri as the systemID
    * 2. Using the schemaLocation as the systemID
    * 
    * 3. If that fails, the baseURI is not null, the xsd is located using
    * URL(baseURL, schemaLocation)
    * 
    * 4. If the baseURI is null, the xsd is located using URL(schemaLocation)
    * 
    * @param nsUri
    * @param baseURI
    * @param schemaLocation
    * @return
    */
   public SchemaBinding resolve(String nsUri, String baseURI, String schemaLocation)
   {
      boolean trace = log.isTraceEnabled();
      InputSource is = null;

      // First try to resolve the namespace as a systemID
      try
      {
         is = resolver.resolveEntity(null, nsUri);
      }
      catch (Exception e)
      {
         if( trace )
            log.trace("Failed to use nsUri as systemID", e);
      }

      if( is == null && schemaLocation != null )
      {
         // Next try the schemaLocation as a systemID
         try
         {
            is = resolver.resolveEntity(null, schemaLocation);
         }
         catch (Exception e)
         {
            if( trace )
               log.trace("Failed to use schemaLocation as systemID", e);
         }

         if( is == null )
         {
            // Just try resolving the schemaLocation against the baseURI
            try
            {
               if(baseURI == null)
               {
                  baseURI = this.baseURI;
               }

               URL schemaURL = null;
               if( baseURI != null )
               {
                  URL baseURL = new URL(baseURI);
                  schemaURL = new URL(baseURL, schemaLocation);
               }
               else
               {
                  schemaURL = new URL(schemaLocation);
               }

               if( schemaURL != null )
               {
                  InputStream is2 = schemaURL.openStream();
                  is = new InputSource(is2);
               }
            }
            catch(Exception e)
            {
               if( trace )
                  log.trace("Failed to use schemaLocation as URL", e);
            }
         }
      }


      SchemaBinding schema = null;
      if( is != null )
      {
         schema = XsdBinder.bind(is.getByteStream(), null, baseURI);
      }

      return schema;
   }

   public LSInput resolveAsLSInput(String nsUri, String baseUri, String schemaLocation)
   {
      throw new UnsupportedOperationException("resolveResource is not implemented.");
   }
}
