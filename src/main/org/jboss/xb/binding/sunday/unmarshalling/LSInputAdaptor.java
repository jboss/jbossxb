/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
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
