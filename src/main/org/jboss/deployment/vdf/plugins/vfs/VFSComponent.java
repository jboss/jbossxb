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
import java.net.URISyntaxException;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.jboss.deployment.vdf.plugins.AbstractVDFComponent;
import org.jboss.deployment.vdf.spi.VDFComponent;
import org.jboss.deployment.vdf.spi.VDFRuntimeException;

/**
 * Implements a Virtual Deployment Framework 
 * component on top of apache's commons-vfs
 * 
 * @author <a href="mailto:dimitris@jboss.org">Dimitris Andreadis</a>
 * @version $Revision$
 */
public class VFSComponent extends AbstractVDFComponent
{
   // Private Data --------------------------------------------------
   
   /** the wrapped FileObject */
   private FileObject fObject;
   
   // Constructors --------------------------------------------------
   
   public VFSComponent(URI uri, FileObject fObject)
   {
      super(uri);
      this.fObject = fObject;
   }
   
   // VDFComponent implementation -----------------------------------
   
   public String getBaseName()
   {
      return fObject.getName().getBaseName();
   }
   
   public long getLastModified()
   {
      try
      {
         return fObject.getContent().getLastModifiedTime();
      }
      catch (FileSystemException e)
      {
         throw new VDFRuntimeException(e);
      }      
   }
   
   public boolean exists()
   {
      try
      {
         return fObject.exists();
      }
      catch (FileSystemException e)
      {
         throw new VDFRuntimeException(e);
      }
   }
   
   public boolean isFile()
   {
      try
      {
         return fObject.getType().equals(FileType.FILE);
      }
      catch (FileSystemException e)
      {
         throw new VDFRuntimeException(e);
      }      
   }
   
   public boolean isDirectory()
   {
      try
      {
         return fObject.getType().equals(FileType.FOLDER);
      }
      catch (FileSystemException e)
      {
         throw new VDFRuntimeException(e);
      }      
   }
   
   public VDFComponent[] getChildren()
   {
      try
      {
         // apparently this is necessary to get an
         // updated view of the children nodes
         fObject.close();
         
         FileObject[] files = fObject.getChildren();
         
         VDFComponent[] components = new VDFComponent[files.length];
         for (int i = 0; i < files.length; i++)
         {
            FileObject file = files[i];
            String uriString = file.getURL().toExternalForm();
            
            if (file.getType().equals(FileType.FOLDER) && !uriString.endsWith("/"))
            {
               // force directories to end with '/'
               uriString += '/';
            }
            URI uri = new URI(uriString);
            components[i] = new VFSComponent(uri, file);
         }
         return components;
      }
      catch (FileSystemException e)
      {
         // not a directory, or some other filesystem error
         throw new VDFRuntimeException(e);
      }
      catch (URISyntaxException e)
      {
         throw new VDFRuntimeException(e);
      }
   }   
}
