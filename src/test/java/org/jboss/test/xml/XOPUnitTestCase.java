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
import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.sunday.marshalling.MarshallerImpl;
import org.jboss.xb.binding.sunday.marshalling.TermBeforeMarshallingCallback;
import org.jboss.xb.binding.sunday.marshalling.MarshallingContext;
import org.jboss.xb.binding.sunday.unmarshalling.DefaultSchemaResolver;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SequenceBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TermBeforeSetParentCallback;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.UnmarshallingContext;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;
import org.jboss.xb.binding.sunday.xop.XOPMarshaller;
import org.jboss.xb.binding.sunday.xop.XOPObject;
import org.jboss.xb.binding.sunday.xop.XOPUnmarshaller;
import org.jboss.xb.binding.sunday.xop.SimpleDataSource;
import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import javax.xml.namespace.QName;
import javax.activation.DataSource;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Iterator;

import junit.framework.TestSuite;


/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class XOPUnitTestCase
      extends AbstractJBossXBTest
{

   public static final TestSuite suite()
   {
      return new TestSuite(XOPUnitTestCase.class);
   }

   public XOPUnitTestCase(String name)
   {
      super(name);
   }

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
         else if(cid.endsWith("octets"))
         {
            xopObject = new XOPObject("octets".getBytes());
            xopObject.setContentType("application/octet-stream");
         }
         else if(cid.endsWith("xopContent"))
         {
            // The XOPUnmarshaller returns an object
            // that doesn't match that actual java property
            xopObject = new XOPObject("xopContent".getBytes());
            xopObject.setContentType("application/octet-stream");
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

         TermBeforeSetParentCallback callback = new TermBeforeSetParentCallback()
         {
            public Object beforeSetParent(Object o, UnmarshallingContext ctx)
            {
               ElementBinding e = (ElementBinding) ctx.getParticle().getTerm();
               Class propType = ctx.resolvePropertyType();

               String localPart = e.getQName().getLocalPart();
               if("image".equals(localPart) ||
                     "sig".equals(localPart) ||
                     "imageWithContentType".equals(localPart) ||
                     "octets".equals(localPart) ||
                     "jpeg".equals(localPart))
               {
                  assertEquals("expected " + byte[].class + " for " + localPart, byte[].class, propType);
               }
               else if("awtImage".equals(localPart))
               {
                  assertEquals(java.awt.Image.class, propType);
               }
               else if("string".equals(localPart))
               {
                  assertEquals(String.class, propType);
               }
               else if("source".equals(localPart))
               {
                  assertEquals(javax.xml.transform.Source.class, propType);
               }
               else
               {
                  fail("unexpected element: " + e.getQName());
               }
               return o;
            }
         };

         ElementBinding e = SCHEMA.getElement(new javax.xml.namespace.QName("http://www.jboss.org/xml/test/xop", "e"));
         SequenceBinding seq = (SequenceBinding) e.getType().getParticle().getTerm();
         for(Iterator i = seq.getParticles().iterator(); i.hasNext();)
         {
            ParticleBinding particle = (ParticleBinding) i.next();
            ElementBinding child = (ElementBinding) particle.getTerm();
            if(! "xopContent".equals( child.getQName().getLocalPart()))
               child.setBeforeSetParentCallback(callback);
         }

         TermBeforeSetParentCallback xmimeBase64Callback = new TermBeforeSetParentCallback()
         {
            public Object beforeSetParent(Object o, UnmarshallingContext ctx)
            {
               ElementBinding e = (ElementBinding) ctx.getParticle().getTerm();
               Class propType = ctx.resolvePropertyType();

               assertNotNull("Failed to resolve property type for "+e.getQName(), propType);

               String localPart = e.getQName().getLocalPart();
               if("xopContent".equals(localPart))
               {
                  assertEquals(String.class, propType);
                  if(propType.equals(String.class))
                  {
                     o = new String( (byte[])o);
                  }
               }
/* alexey: this is never the case
               else if("Include".equals(localPart))
               {
                  assertEquals(String.class, propType);
                  assertTrue( (o instanceof byte[]));
                  
                  // Type conversion required
                  if(propType.equals(String.class))
                     o = new String( (byte[])o);
               }
*/
               return o;
            }
         };

         // xmime complex types
         TypeBinding xmimeBase64Type = SCHEMA.getType(new QName("http://www.w3.org/2005/05/xmlmime", "base64Binary"));
         if(xmimeBase64Type!=null)
         {
            xmimeBase64Type.setBeforeSetParentCallback( xmimeBase64Callback );

            // alexey: the following shouldn't be used.
            // callbacks should be set on the types and/or elements
            // that can have xop:Include as their content

            // xop:Include
            // Uncomment the following lines in order to intercept the
            // XOPUnmarshaller result _before_ the actual setter is invoked

            /*
            ModelGroupBinding modelGroup = (ModelGroupBinding)xmimeBase64Type.getParticle().getTerm();
            ParticleBinding particle = (ParticleBinding)modelGroup.getParticles().iterator().next();
            ElementBinding xopInclude = (ElementBinding)particle.getTerm();

            if(! xopInclude.getQName().equals(new QName("http://www.w3.org/2004/08/xop/include", "Include")))
               throw new RuntimeException("Looks like the JBossXB XOP implementation has changed, please open a JIRA issue");

            xopInclude.setBeforeSetParentCallback(interceptXOPUnmarshallerResults);
            */
         }
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

   /**
    * Test a simple xsd:base64Binary declaration
    */
   public void testMarshalOctets() throws Exception
   {
      E e = new E();
      e.octets = "octets".getBytes();
      String marshalled = marshal(XOP_ENABLED_MARSH, e);
      assertXmlEqual(getOptimizedXml("octets"), marshalled);
   }

   /**
    * Test unmarshalling of a simple xsd:base64Binary declaration
    */
   public void testUnmarshalOctets() throws Exception
   {
      SCHEMA.setXopUnmarshaller(XOP_ENABLED_UNMARSH);

      String xml = getOptimizedXml("octets");

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(xml), SCHEMA);

      assertNotNull(o);
      assertTrue(o instanceof E);

      E e = (E)o;
      assertNotNull(e.octets);
      assertEquals("octets", new String(e.octets));
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

   public void testUnmarshalStringWithTypeConversion() throws Exception
   {
      SCHEMA.setXopUnmarshaller(XOP_ENABLED_UNMARSH);

      String xml = getOptimizedXml("xopContent");

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(xml), SCHEMA);

      assertNotNull(o);
      assertTrue(o instanceof E);

      E e = (E)o;
      assertNotNull(e.xopContent);
      assertEquals("xopContent", e.xopContent);
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

   /**
    * Test JBWS-1604
    * 
    * @throws Exception
    */
   public void testContentAdaptor() throws Exception
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

      // interesting part for JBWS-1604
      registerContentAdapter(schema);

      // has to be an inlined request that causes the exception
      String xml =
            "<applxml xmlns='http://www.jboss.org/xml/test/xop'>" +
                  "/9j/4AAQSkZJRgABAgAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRofHh0a\n" +
                  "HBwgJC4nICIsIxwcKDcpLDAxNDQ0Hyc5PTgyPC4zNDL/2wBDAQkJCQwLDBgNDRgyIRwhMjIyMjIy\n" +
                  "MjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjIyMjL/wAARCADLAfIDASIA\n" +
                  "AhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQA\n" +
                  "AAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3\n" +
                  "ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWm\n" +
                  "p6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEA\n" +
                  "AwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSEx\n" +
                  "BhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElK\n" +
                  "U1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3\n" +
                  "uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwCvRRR0\n" +
                  "FCY7MKKKOgoQWYUUUUILMKKKIXcTspGdWtt80ckTokcAldzu+Xl8EI2c3mcEcE5OYuRjiPsVk9q9\n" +
                  "2t7Cw/vy3SyAmEeVvC5EhabBYLu2KmNuTupYz/eEsNhuuJYkdWthWZJqVwpkljtY3tUlEQczEO53\n" +
                  "BThduPvZXkgcZyBzUg1a3EkqyblVXKhgrMMDgs2B8g3BhlsD5SelAcrL9c3pvh+0vNKs7qe51UzT\n" +
                  "QJJIUuFCgBiMDI6Vn/8AfB+f/nn/AJ/yBz6OWg2s9f62FH/6tv8Aon/oNH/MB2tRt/yP+XXBpNv/\n" +
                  "ANf/AD/8RRyhyt9DRjluCkwV5mXA/sbPUc//AKqrhHSNZQB/ZmCM+lKUdkdhsCBug7/+y/5/Nxmm\n" +
                  "aJULEIKj0K9i1engIPtWX5x9xfil6L4x2xoTFJkMBgkjnHfH+f4akeRXMhkAVjzkHrTAMOygKwbg\n" +
                  "8/8AxvFO2Ix6quSeMcf/ABNaX0u31/hakqgoKNTFPTRqrHXe1Wzgn/iesezsQY/yOaSpZI3Vv7Lu\n" +
                  "vEFpLdXb+VeIclBz3bHOKgNH3SDkb1FpM/8A1/8AP/6+5pf9XQopBZh5v7/6f8fv+eaKK8/2t6H8\n" +
                  "qaBpiUUu1vQ/lRtb0P5VRNmJRS7W9D+VG1vQ/lQFmJRS7W9D+VG1vQ/lQFmJRS7W9D+VG1vQ/lQF" +
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

      public String xopContent;
   }

   private static final QName XMIME_BASE_64 = new QName(Constants.NS_XML_MIME, "base64Binary");
   
   public static void registerContentAdapter(SchemaBinding schemaBinding)
   {
      TestContentAdapter contentAdapter = new TestContentAdapter();

      // base64 simple types
      TypeBinding base64Type = schemaBinding.getType(org.jboss.xb.binding.Constants.QNAME_BASE64BINARY);
      base64Type.setBeforeMarshallingCallback( contentAdapter );
      base64Type.setBeforeSetParentCallback( contentAdapter );

      // xmime complex types
      TypeBinding xmimeBase64Type = schemaBinding.getType(XMIME_BASE_64);
      if(xmimeBase64Type!=null)
      {
         System.out.println("Register with " + xmimeBase64Type);
         xmimeBase64Type.setBeforeMarshallingCallback( contentAdapter );
         xmimeBase64Type.setBeforeSetParentCallback( contentAdapter );
      }
      else
      {
         System.out.println("XMIME_BASE_64 not registered");  
      }
   }

   /**
    * Example content adapter
    */
   static class TestContentAdapter implements TermBeforeSetParentCallback, TermBeforeMarshallingCallback
   {
      public Object beforeSetParent(Object object, UnmarshallingContext ctx)
      {
         System.out.println("beforeSetParent " + object);
         
         if(null==object)
            return object;
         
         // FIXME: may be null when it's actually an encoded request ?!
         Class targetClass = ctx.resolvePropertyType();

         if(null==targetClass) {
            throw new IllegalStateException("Failed to resolve target property type on "+ ctx.getParticle());
         }

         return object;
      }

      public Object beforeMarshalling(Object object, MarshallingContext ctx)
      {
         System.out.println("beforeMarshalling " + object);
         return object;
      }
   }

}
