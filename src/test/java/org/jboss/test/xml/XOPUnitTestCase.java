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

import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.sunday.marshalling.MarshallerImpl;
import org.jboss.xb.binding.sunday.unmarshalling.DefaultSchemaResolver;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;
import org.jboss.xb.binding.sunday.xop.XOPMarshaller;
import org.jboss.xb.binding.sunday.xop.XOPObject;
import org.jboss.xb.binding.sunday.xop.XOPUnmarshaller;
import org.jboss.xb.binding.sunday.xop.SimpleDataSource;
import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import javax.activation.DataSource;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;


/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class XOPUnitTestCase
   extends AbstractJBossXBTest
{
	   public XOPUnitTestCase(String name)
	   {
	      super(name);
	   }

   private static final XOPMarshaller NULLCID_XOP_MARSH = new XOPMarshaller()
   {
      public boolean isXOPPackage()
      {
         return true;
      }

      public String addMtomAttachment(byte[] data, String elementNamespace, String elementName)
      {
         return null;
      }

      public String addSwaRefAttachment(XOPObject dataHandler)
      {
         return null;
      }

      public String addMtomAttachment(XOPObject dataHandler, String elementNamespace, String elementName)
      {
         return null;
      }
   };

   private static final XOPMarshaller XOP_ENABLED_MARSH = new XOPMarshaller()
   {
      public boolean isXOPPackage()
      {
         return true;
      }

      public String addMtomAttachment(byte[] data, String elementNamespace, String elementName)
      {
         return "cid:" + elementName;
      }

      public String addMtomAttachment(XOPObject dataHandler, String elementNamespace, String elementName)
      {
         return "cid:" + elementName;
      }
   };

   private static final XOPMarshaller XOP_DISABLED_MARSH = new XOPMarshaller()
   {
      public boolean isXOPPackage()
      {
         return false;
      }

      public String addMtomAttachment(byte[] data, String elementNamespace, String elementName)
      {
         return "cid:" + elementName;
      }

      public String addMtomAttachment(XOPObject dataHandler, String elementNamespace, String elementName)
      {
         return "cid:" + elementName;
      }
   };

   private static final Image AWT_IMAGE = new Image()
   {
      private final int width = 5;
      private final int height = 7;

      public int getWidth(ImageObserver observer)
      {
         return width;
      }

      public int getHeight(ImageObserver observer)
      {
         return height;
      }

      public ImageProducer getSource()
      {
         throw new UnsupportedOperationException("getSource is not implemented.");
      }

      public Graphics getGraphics()
      {
         throw new UnsupportedOperationException("getGraphics is not implemented.");
      }

      public Object getProperty(String name, ImageObserver observer)
      {
         throw new UnsupportedOperationException("getProperty is not implemented.");
      }

      public void flush()
      {
         throw new UnsupportedOperationException("flush is not implemented.");
      }
   };

   private static final Source SOURCE = new Source()
   {
      private String systemId = "http://www.jboss.org/test/xml/xop";

      public void setSystemId(String systemId)
      {
         this.systemId = systemId;
      }

      public String getSystemId()
      {
         return systemId;
      }
   };

   private static final XOPUnmarshaller XOP_ENABLED_UNMARSH = new XOPUnmarshaller()
   {
      public boolean isXOPPackage()
      {
         return true;
      }

      public XOPObject getAttachmentAsDataHandler(String cid)
      {
         XOPObject xopObject;

         if(cid.endsWith("awtImage"))
         {
            xopObject = new XOPObject(AWT_IMAGE);
            xopObject.setContentType("image/jpeg");
         }
         else if(cid.endsWith("source"))
         {
            xopObject = new XOPObject(SOURCE);
            xopObject.setContentType("application/xml");
         }
         else if(cid.endsWith("string"))
         {
            xopObject = new XOPObject("string");
            xopObject.setContentType("text/xml");
         }
         else
         {
            try
            {
               DataSource ds = new SimpleDataSource(cid.substring(4).getBytes(), "application/octet-stream");
               xopObject = new XOPObject(ds.getInputStream());
               xopObject.setContentType("application/octet-stream");
            }
            catch (IOException e)
            {
               throw new IllegalStateException(e.getMessage());

            }
         }

         return xopObject;
      }

      public byte[] getAttachmentAsByteArray(String cid)
      {
         return cid.substring(4).getBytes();
      }
   };

   private static final XOPUnmarshaller XOP_DISABLED_UNMARSH = new XOPUnmarshaller()
   {
      public boolean isXOPPackage()
      {
         return false;
      }

      public XOPObject getAttachmentAsDataHandler(String cid)
      {
         throw new UnsupportedOperationException("getAttachmentAsDataHandler is not implemented.");
      }

      public byte[] getAttachmentAsByteArray(String cid)
      {
         throw new UnsupportedOperationException("getAttachmentAsByteArray is not implemented.");
      }
   };

   private static SchemaBinding SCHEMA;
   private static String NON_OPT_XML;

   protected void setUp() throws Exception
   {
      super.setUp();

      if(SCHEMA == null)
      {
         String testXsd = findXML(rootName + ".xsd");

         DefaultSchemaResolver resolver = new DefaultSchemaResolver();
         String xmimeXsd = getSchemaLocation(getClass(), "xmlmime.xsd");
         resolver.addSchemaLocation("http://www.w3.org/2005/05/xmlmime", xmimeXsd);

         SCHEMA = XsdBinder.bind(testXsd, resolver);
         SCHEMA.setIgnoreUnresolvedFieldOrClass(false);
      }

      if(NON_OPT_XML == null)
      {
         NON_OPT_XML = readXml(rootName + ".xml");
      }
   }

   public void testUnmarshalNonOptimized() throws Exception
   {
      SCHEMA.setXopUnmarshaller(XOP_DISABLED_UNMARSH);

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(NON_OPT_XML), SCHEMA);

      assertNotNull(o);
      assertTrue(o instanceof E);

      E e = (E)o;
      assertNotNull(e.image);
      assertEquals("image", new String(e.image));
      assertNotNull(e.sig);
      assertEquals("sig", new String(e.sig));
   }

   public void testMarshalWithNullCid() throws Exception
   {
      assertXmlEqual(NON_OPT_XML, marshal(NULLCID_XOP_MARSH));
   }

   public void testMarshalWithDisabledXop() throws Exception
   {
      assertXmlEqual(NON_OPT_XML, marshal(XOP_DISABLED_MARSH));
   }

   public void testUnmarshalOptimized() throws Exception
   {
      SCHEMA.setXopUnmarshaller(XOP_ENABLED_UNMARSH);

      String xml = readXml(rootName + "Optimized.xml");

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(xml), SCHEMA);

      assertNotNull(o);
      assertTrue(o instanceof E);

      E e = (E)o;
      assertNotNull(e.image);
      assertEquals("image", new String(e.image));
      assertNotNull(e.sig);
      assertEquals("sig", new String(e.sig));

   }

   public void testMarshalWithEnabledXop() throws Exception
   {
      String xml = readXml(rootName + "Optimized.xml");
      assertXmlEqual(xml, marshal(XOP_ENABLED_MARSH));
   }

   public void testUnmarshalImageWithOptimized() throws Exception
   {
      SCHEMA.setXopUnmarshaller(XOP_ENABLED_UNMARSH);

      String xml = readXml(rootName + "ImageWithContentType.xml");

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(xml), SCHEMA);

      assertNotNull(o);
      assertTrue(o instanceof E);

      E e = (E)o;
      assertNotNull(e.imageWithContentType);
      assertEquals("imageWithContentType", new String(e.imageWithContentType));

   }

   public void testMarshalImageWithContentType() throws Exception
   {
      String xml = readXml(rootName + "ImageWithContentType.xml");
      E e = new E();
      e.imageWithContentType = "imageWithContentType".getBytes();
      String marshalled = marshal(XOP_ENABLED_MARSH, e);
      assertXmlEqual(xml, marshalled);

   }

   public void testUnmarshalJpegOptimized() throws Exception
   {
      SCHEMA.setXopUnmarshaller(XOP_ENABLED_UNMARSH);

      String xml = readXml(rootName + "Jpeg.xml");

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(xml), SCHEMA);

      assertNotNull(o);
      assertTrue(o instanceof E);

      E e = (E)o;
      assertNotNull(e.jpeg);
      assertEquals("jpeg", new String(e.jpeg));
   }

   public void testMarshalJpeg() throws Exception
   {
      String xml = readXml(rootName + "Jpeg.xml");
      E e = new E();
      e.jpeg = "jpeg".getBytes();
      String marshalled = marshal(XOP_ENABLED_MARSH, e);
      assertXmlEqual(xml, marshalled);
   }

   public void testMarshalAWTImage() throws Exception
   {
      E e = new E();
      e.awtImage = AWT_IMAGE;
      String marshalled = marshal(XOP_ENABLED_MARSH, e);
      assertXmlEqual(getOptimizedXml("awtImage"), marshalled);
   }

   public void testUnmarshalAwtImage() throws Exception
   {
      SCHEMA.setXopUnmarshaller(XOP_ENABLED_UNMARSH);

      String xml = getOptimizedXml("awtImage");

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(xml), SCHEMA);

      assertNotNull(o);
      assertTrue(o instanceof E);

      E e = (E)o;
      assertNotNull(e.awtImage);
      assertEquals(5, e.awtImage.getWidth(null));
      assertEquals(7, e.awtImage.getHeight(null));
   }

   public void testMarshalSource() throws Exception
   {
      E e = new E();
      e.source = SOURCE;
      String marshalled = marshal(XOP_ENABLED_MARSH, e);
      assertXmlEqual(getOptimizedXml("source"), marshalled);
   }

   public void testUnmarshalSource() throws Exception
   {
      SCHEMA.setXopUnmarshaller(XOP_ENABLED_UNMARSH);

      String xml = getOptimizedXml("source");

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(xml), SCHEMA);

      assertNotNull(o);
      assertTrue(o instanceof E);

      E e = (E)o;
      assertNotNull(e.source);
      assertEquals("http://www.jboss.org/test/xml/xop", e.source.getSystemId());
   }

   public void testMarshalString() throws Exception
   {
      E e = new E();
      e.string = "string";
      String marshalled = marshal(XOP_ENABLED_MARSH, e);
      assertXmlEqual(getOptimizedXml("string"), marshalled);
   }

   public void testUnmarshalString() throws Exception
   {
      SCHEMA.setXopUnmarshaller(XOP_ENABLED_UNMARSH);

      String xml = getOptimizedXml("string");

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(xml), SCHEMA);

      assertNotNull(o);
      assertTrue(o instanceof E);

      E e = (E)o;
      assertNotNull(e.string);
      assertEquals("string", e.string);
   }

   public void testTopLevelUnmarshalling() throws Exception
   {
      String xsd =
         "<schema" +
         "  xmlns='http://www.w3.org/2001/XMLSchema'" +
         "  xmlns:xmime='http://www.w3.org/2005/05/xmlmime'" +
         "  targetNamespace='http://www.jboss.org/xml/test/xop'>" +
         "  <import namespace='http://www.w3.org/2005/05/xmlmime' schemaLocation='xmlmime.xsd'/>" +
         "  <element name='applxml' xmime:expectedContentTypes='application/xml' type='xmime:base64Binary'/>" +
         "</schema>";

      DefaultSchemaResolver resolver = new DefaultSchemaResolver();
      String xmimeXsd = getSchemaLocation(getClass(), "xmlmime.xsd");
      resolver.addSchemaLocation("http://www.w3.org/2005/05/xmlmime", xmimeXsd);

      SchemaBinding schema = XsdBinder.bind(new StringReader(xsd), null, resolver);
      schema.setIgnoreUnresolvedFieldOrClass(false);
      schema.setXopUnmarshaller(XOP_ENABLED_UNMARSH);

      String xml =
         "<applxml xmlns='http://www.jboss.org/xml/test/xop'>" +
         "  <xop:Include href='cid:applxml' xmlns:xop='http://www.w3.org/2004/08/xop/include'/>" +
         "</applxml>";

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(xml), schema);
      assertNotNull(o);
      assertTrue(o instanceof byte[]);
      assertTrue(Arrays.equals("applxml".getBytes(), (byte[])o));
   }

   // Private

   private String readXml(String name)
      throws IOException
   {
      String testXml = getResource(name).getFile();
      FileInputStream fis = new FileInputStream(testXml);
      byte[] bytes = new byte[fis.available()];
      fis.read(bytes);
      fis.close();
      return new String(bytes);
   }

   private static String marshal(XOPMarshaller xopMarshaller)
      throws IOException, SAXException
   {
      E e = new E();
      e.image = "image".getBytes();
      e.sig = "sig".getBytes();
      return marshal(xopMarshaller, e);
   }

   private static String marshal(XOPMarshaller xopMarshaller, E e)
      throws IOException, SAXException
   {
      SCHEMA.setXopMarshaller(xopMarshaller);
      StringWriter writer = new StringWriter();
      MarshallerImpl marshaller = new MarshallerImpl();
      marshaller.marshal(SCHEMA, null, e, writer);
      return writer.getBuffer().toString();
   }

   private static String getOptimizedXml(String elementName)
   {
      return
         "<e xmlns='http://www.jboss.org/xml/test/xop'>" +
         "  <" +
         elementName +
         ">" +
         "    <xop:Include xmlns:xop='http://www.w3.org/2004/08/xop/include' href='cid:" +
         elementName +
         "'/>" +
         "  </" +
         elementName +
         ">" +
         "</e>";
   }

   // Inner

   public static class E
   {
      public byte[] image;
      public byte[] sig;
      public byte[] imageWithContentType;
      public byte[] jpeg;

      public Image awtImage;
      public Source source;
      public String string;
      public byte[] octets;
   }
}
