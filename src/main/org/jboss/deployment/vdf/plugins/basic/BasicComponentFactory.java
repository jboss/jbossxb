/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/
package org.jboss.deployment.vdf.plugins.basic;

import java.io.File;
import java.net.URI;

import org.jboss.deployment.vdf.spi.VDFComponent;
import org.jboss.deployment.vdf.spi.VDFComponentFactoryAdmin;
import org.jboss.deployment.vdf.spi.VDFException;
import org.w3c.dom.Element;

/**
 * Implements a simple Virtual Deployment Framework 
 * component factory for wrapping local files
 * 
 * @author <a href="mailto:dimitris@jboss.org">Dimitris Andreadis</a>
 * @version $Revision$
 */
public class BasicComponentFactory implements VDFComponentFactoryAdmin
{
   // Private -------------------------------------------------------
   
   // Constructor ---------------------------------------------------
   
   public BasicComponentFactory()
   {
      // empty
   }
   
   // VDFComponentFactoryAdmin --------------------------------------
   
   public void create(Element config)
      throws VDFException
   {
      // empty
   }

   public void destroy()
   {
      // empty
   }
   
   // VDFComponentFactory -------------------------------------------
   
   public VDFComponent createVDFComponent(URI uri) throws VDFException
   {
      if (uri == null)
      {
         throw new VDFException("null URI");
      }
      
      try
      {
         // we can only handle local files for the time being
         if ("file".equals(uri.getScheme()))
         {
            return new BasicComponent(uri, new File(uri));
         }
         else
         {
            throw new VDFException("Not a 'file' URI: " + uri);
         }
      }
      catch (IllegalArgumentException e)
      {
         throw new VDFException(e);
      }
   }
}