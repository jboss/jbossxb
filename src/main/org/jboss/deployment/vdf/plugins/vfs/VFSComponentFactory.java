/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/
package org.jboss.deployment.vdf.plugins.vfs;

import java.net.URI;

import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.cache.NullFilesCache;
import org.apache.commons.vfs.impl.StandardFileSystemManager;
import org.jboss.deployment.vdf.spi.VDFComponent;
import org.jboss.deployment.vdf.spi.VDFComponentFactoryAdmin;
import org.jboss.deployment.vdf.spi.VDFException;
import org.jboss.logging.Logger;
import org.w3c.dom.Element;

/**
 * Implements a Virtual Deployment Framework 
 * component factory on top of apache commons-vfs
 * 
 * @author <a href="mailto:dimitris@jboss.org">Dimitris Andreadis</a>
 */
public class VFSComponentFactory implements VDFComponentFactoryAdmin
{
   // Static --------------------------------------------------------
   
   /** The Logger */
   private static final Logger log = Logger.getLogger(VFSComponentFactory.class);
   
   // Private -------------------------------------------------------
   
   /** The file system manager */
   StandardFileSystemManager fsManager;
   
   // Constructor ---------------------------------------------------
   
   public VFSComponentFactory()
   {
      // empty
   }
   
   // VDFComponentFactoryAdmin --------------------------------------
   
   public void create(Element config) throws VDFException
   {
      try
      {
         fsManager = new StandardFileSystemManager();
         fsManager.setFilesCache(new NullFilesCache());
         fsManager.init();
         
         if (log.isDebugEnabled())
         {
            log.debug("BaseFile: " + fsManager.getBaseFile());
            String[] schemes = fsManager.getSchemes();
            log.debug("Supported schemes == " + schemes.length);
            for (int i = 0; i < schemes.length; i++)
            {
               log.debug("  scheme #" + i + ": " + schemes[i]);
            }
         }         
      }
      catch (FileSystemException e)
      {
         throw new VDFException(e);
      }
   }

   public void destroy()
   {
      fsManager.close();
      fsManager = null;
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
         return new VFSComponent(uri, fsManager.resolveFile(uri.toString()));
      }
      catch (FileSystemException e)
      {
         throw new VDFException(e);
      }
   }
}