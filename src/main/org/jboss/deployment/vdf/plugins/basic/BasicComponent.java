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

import org.jboss.deployment.vdf.plugins.AbstractVDFComponent;
import org.jboss.deployment.vdf.spi.VDFComponent;
import org.jboss.deployment.vdf.spi.VDFRuntimeException;

/**
 * Implements a simple Virtual Deployment Framework 
 * component for wrapping local files
 * 
 * @author <a href="mailto:dimitris@jboss.org">Dimitris Andreadis</a>
 * @version $Revision$
 */
public class BasicComponent extends AbstractVDFComponent
{
   // Private Data --------------------------------------------------
   
   /** The file this URI points to */
   File file;
   
   // Constructors --------------------------------------------------
   
   public BasicComponent(URI uri, File file)
   {
      super(uri);
      this.file = file;
   }
   
   // VDFComponent implementation -----------------------------------
   
   public String getBaseName()
   {
      return file.getName();
   }
   
   public long getLastModified()
   {
      return file.lastModified();
   }
   
   public boolean exists()
   {
      return file.exists();
   }
   
   public boolean isFile()
   {
      return file.isFile();
   }
   
   public boolean isDirectory()
   {
      return file.isDirectory();
   }
   
   public VDFComponent[] getChildren()
   {
      if (!file.isDirectory())
      {
         throw new VDFRuntimeException("Not a directory: " + uri);
      }
      
      File[] files = file.listFiles();
      
      VDFComponent[] components = new VDFComponent[files.length];
      
      for (int i = 0; i < files.length; i++)
      {
         components[i] = new BasicComponent(files[i].toURI(), files[i]);
      }
      
      return components;
   }
   
   // Private -------------------------------------------------------
   
}
