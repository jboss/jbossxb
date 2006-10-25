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
package org.jboss.test.xml;

import org.jboss.test.AbstractTestCase;
import org.jboss.xb.binding.sunday.xop.XOPObject;
import org.jboss.xb.binding.sunday.xop.XOPUnmarshaller;

import javax.xml.transform.stream.StreamSource;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Heiko Braun <heiko.braun@jboss.com>
 * @version $Id$
 * @since Oct 5, 2006
 */
public class XOPUnmarshallerImpl implements XOPUnmarshaller {

   private AbstractTestCase callback;
   private String resourceDir;

   public XOPUnmarshallerImpl(AbstractTestCase callback, String resourceDir) {
      this.callback = callback;
      this.resourceDir = resourceDir;
   }

   public boolean isXOPPackage()
   {
      return true;
   }

   public XOPObject getAttachmentAsDataHandler(String cid)
   {
      XOPObject xopObject;

      if(cid.endsWith("string"))
      {
         xopObject = new XOPObject("A plain text attachment");
         xopObject.setContentType("text/plain");
      }
      else if (cid.endsWith("source"))
      {
         StreamSource source = createTestSource();

         xopObject = new XOPObject(source);
         xopObject.setContentType("application/xml");

      }
      else if(cid.endsWith("jpeg") || cid.endsWith("image"))
      {
         Image image = createTestImage();

         xopObject = new XOPObject(image);
         xopObject.setContentType("image/jpeg");
      }
      else if(cid.endsWith("octets"))
      {
         xopObject = new XOPObject("octets".getBytes());
         xopObject.setContentType("application/octet-stream");
      }
      else if (cid.endsWith("applxml"))
      {
         xopObject = new XOPObject("applxml".getBytes());
         xopObject.setContentType("application/octet-stream");
      }
      else
      {
         throw new RuntimeException("Unmapped content: " + cid);
      }

      return xopObject;
   }

   public byte[] getAttachmentAsByteArray(String cid)
   {
      return cid.substring(4).getBytes();
   }
   
   public StreamSource createTestSource()
   {
      String filename = callback.getResource(resourceDir+"_data.xml").getFile();
      FileInputStream stream = null;
      try
      {
         stream = new FileInputStream(filename);
      }
      catch (FileNotFoundException e)
      {
         throw new RuntimeException("Failed to load file: " + filename);
      }

      return new StreamSource(stream);
   }

   public Image createTestImage() {
      String filename = callback.getResource(resourceDir+"_data.jpg")!= null ?
          callback.getResource(resourceDir+"_data.jpg").getFile() : null;

      if(null == filename)
         throw new IllegalArgumentException("Failed to create image");

      URL url = null;
      try
      {
         url = new File(filename).toURL();
      }
      catch (MalformedURLException e)
      {
         throw new RuntimeException("Failed to load file: " + filename);
      }

      // On Linux the X11 server must be installed properly to create images successfully.
      // If the image cannot be created in the test VM, we assume it cannot be done on the
      // server either, so we just skip the test
      Image image = null;
      try
      {
         image = Toolkit.getDefaultToolkit().createImage(url);
      }
      catch (Throwable th)
      {
         //log.warn("Cannot create Image: " + th);
      }
      return image;
   }


}
