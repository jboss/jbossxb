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

import java.io.InputStream;
import java.io.Reader;

import org.w3c.dom.ls.LSInput;

/**
 * A simple implementation of the dom3 LSInput
 * 
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
public class LSInputAdaptor
   implements LSInput
{
   private String baseURI;
   private String publicId;
   private String systemId;
   private String encoding;
   private InputStream byteStream;
   private Reader characterStream;
   private String stringData;
   private boolean certifiedText;

   public LSInputAdaptor(String publicId, String systemId, String baseURI)
   {
      this.publicId = publicId;
      this.systemId = systemId;
      this.baseURI = baseURI;
   }

   public LSInputAdaptor(InputStream is, String encoding)
   {
      setByteStream(is);
      setEncoding(encoding);
   }

   public LSInputAdaptor(Reader charStream, String encoding)
   {
      this.setCharacterStream(charStream);
      setEncoding(encoding);
   }

   public LSInputAdaptor(String data, String encoding)
   {
      this.setStringData(data);
      setEncoding(encoding);
   }

   public String getBaseURI()
   {
      return baseURI;
   }

   public void setBaseURI(String baseURI)
   {
      this.baseURI = baseURI;
   }

   public String getPublicId()
   {
      return publicId;
   }

   public void setPublicId(String publicId)
   {
      this.publicId = publicId;
   }

   public String getSystemId()
   {
      return systemId;
   }

   public void setSystemId(String systemId)
   {
      this.systemId = systemId;
   }

   public InputStream getByteStream()
   {
      return byteStream;
   }

   public void setByteStream(InputStream is)
   {
      this.byteStream = is;
   }

   public Reader getCharacterStream()
   {
      return characterStream;
   }

   public void setCharacterStream(Reader reader)
   {
      this.characterStream = reader;
   }

   public String getStringData()
   {
      return stringData;
   }

   public void setStringData(String stringData)
   {
      this.stringData = stringData;
   }

   public String getEncoding()
   {
      return encoding;
   }

   public void setEncoding(String encoding)
   {
      this.encoding = encoding;
   }

   public boolean getCertifiedText()
   {
      return certifiedText;
   }

   public void setCertifiedText(boolean flag)
   {
      this.certifiedText = flag;
   }
}
