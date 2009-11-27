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

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jboss.xb.binding.AttributesImpl;
import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.MappingObjectModelProvider;
import org.jboss.xb.binding.MarshallingContext;
import org.jboss.xb.binding.ObjectLocalMarshaller;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.XercesXsMarshaller;
import org.jboss.xb.binding.AbstractMarshaller;
import org.jboss.xb.binding.metadata.ClassMetaData;
import org.jboss.xb.binding.metadata.PropertyMetaData;
import org.jboss.xb.binding.metadata.ValueMetaData;
import org.jboss.xb.binding.sunday.marshalling.MarshallerImpl;
import org.jboss.xb.binding.sunday.unmarshalling.CharactersHandler;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleHandler;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBindingResolver;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.WildcardBinding;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;
import org.jboss.xb.util.DomCharactersHandler;
import org.jboss.xb.util.DomLocalMarshaller;
import org.jboss.xb.util.DomParticleHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.LSInput;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;


/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 46112 $</tt>
 */
public class WildcardUnresolvedElementsUnitTestCase
   extends AbstractJBossXBTest
{
   private static final String XSD =
      "<schema targetNamespace='http://org.jboss.ws/jbws434/types'" +
      " xmlns:tns='http://org.jboss.ws/jbws434/types'" +
      " xmlns='http://www.w3.org/2001/XMLSchema'>" +
      " <complexType name='ArrayOfAny'>" +
      "   <sequence>" +
      "     <any namespace='##other' processContents='lax' minOccurs='0' maxOccurs='unbounded'/>" +
      "   </sequence>" +
      " </complexType>" +
      " <element name='e' type='tns:ArrayOfAny'/>" +
      "</schema>";

   private static final String XML =
      "<e xmlns='http://org.jboss.ws/jbws434/types'" +
      "  xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'" +
      "  xsi:schemaLocation='http://org.jboss.ws/jbws434/types ns1.xsd'" +
      ">" +
      "  <child1 xmlns='http://somens'>" +
      "    <child2 child2_attr1='attr1' child2_attr2='attr2'/>" +
      "    <child3>text content3</child3>" +
      "    <child4 child4_attr1='attr1'>" +
      "      <child5/>" +
      "    </child4>" +
      "  </child1>" +
      "  <child6 xmlns='http://anotherns'/>" +
      "  <ns:child7 xmlns:ns='http://child7ns'>" +
      "    <ns:child8 xmlns:ns='http://child8ns'/>" +
      "  </ns:child7>" +
      "</e>";

   static
   {
      //Validator.assertValidXml(XSD, XML);
   }

   public WildcardUnresolvedElementsUnitTestCase(String name)
   {
      super(name);
   }

   public void testUnmarshallingGenericElement() throws Exception
   {
      SchemaBinding schema = getSchemaBinding(true);
      
      // unmarshal
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(XML), schema);

      assertArrayOfAnyGenericElement(o);
   }

   public void testUnmarshallingDom() throws Exception
   {
      SchemaBinding schema = getSchemaBinding(false);

      // unmarshal
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Object o = unmarshaller.unmarshal(new StringReader(XML), schema);

      assertArrayOfAnyDom(o);
   }

   public void testMarshallingXercesGenericElement() throws Exception
   {
      XercesXsMarshaller marshaller = new XercesXsMarshaller();
      setupGeMarshaller(marshaller);

      MappingObjectModelProvider provider = new MappingObjectModelProvider();

      StringWriter writer = new StringWriter();
      marshaller.marshal(new StringReader(XSD), provider, ArrayOfAny.GE_INSTANCE, writer);
      String marshalled = writer.getBuffer().toString();
      assertXmlEqual(XML, marshalled);
   }

   public void testMarshallingXercesDom() throws Exception
   {
      XercesXsMarshaller marshaller = new XercesXsMarshaller();
      setupDomMarshaller(marshaller);

      MappingObjectModelProvider provider = new MappingObjectModelProvider();

      StringWriter writer = new StringWriter();
      marshaller.marshal(new StringReader(XSD), provider, ArrayOfAny.DOM_INSTANCE, writer);
      String marshalled = writer.getBuffer().toString();
      assertXmlEqual(XML, marshalled);
   }

   public void testMarshallingSundayGenericElement() throws Exception
   {
      MarshallerImpl marshaller = new MarshallerImpl();
      setupGeMarshaller(marshaller);
      StringWriter writer = new StringWriter();
      marshaller.marshal(new StringReader(XSD), null, ArrayOfAny.GE_INSTANCE, writer);
      String marshalled = writer.getBuffer().toString();
      assertXmlEqual(XML, marshalled);
   }

   public void testMarshallingSundayDom() throws Exception
   {
      MarshallerImpl marshaller = new MarshallerImpl();
      setupDomMarshaller(marshaller);
      StringWriter writer = new StringWriter();
      marshaller.marshal(new StringReader(XSD), null, ArrayOfAny.DOM_INSTANCE, writer);
      String marshalled = writer.getBuffer().toString();
      assertXmlEqual(XML, marshalled);
   }

   // Private

   private void setupDomMarshaller(AbstractMarshaller marshaller)
   {
      marshaller.mapClassToGlobalElement(ArrayOfAny.class, "e", "http://org.jboss.ws/jbws434/types", null, null);
      marshaller.mapFieldToWildcard(ArrayOfAny.class, "_any", DomLocalMarshaller.INSTANCE);
   }

   private void setupGeMarshaller(AbstractMarshaller marshaller)
   {
      marshaller.mapClassToGlobalElement(ArrayOfAny.class, "e", "http://org.jboss.ws/jbws434/types", null, null);
      marshaller.mapFieldToWildcard(ArrayOfAny.class, "_any",
         new ObjectLocalMarshaller()
         {
            public void marshal(MarshallingContext ctx, Object o)
            {
               ContentHandler ch = ctx.getContentHandler();
               GenericElement ge = (GenericElement)o;

               try
               {
                  AttributesImpl attrs = null;
                  if(ge.getAttributesTotal() > 0)
                  {
                     attrs = new AttributesImpl(ge.getAttributesTotal() + 1);
                     Set<?> attrNames = ge.getAttributeNames();
                     for(Iterator<?> i = attrNames.iterator(); i.hasNext();)
                     {
                        String attrName = (String)i.next();
                        String attrValue = ge.getAttribute(attrName);
                        attrs.add(null, attrName, attrName, null, attrValue);
                     }
                  }
                  else
                  {
                     attrs = new AttributesImpl(1);
                  }

                  attrs.add(Constants.NS_XML_SCHEMA, "xmlns", "xmlns", null, ge.getNsUri());

                  ch.startElement(ge.getNsUri(), ge.getLocalName(), ge.getLocalName(), attrs);

                  String text = ge.getTextContent();
                  if(text != null && text.length() > 0)
                  {
                     ch.characters(text.toCharArray(), 0, text.length());
                  }

                  if(ge.getChildElementsTotal() > 0)
                  {
                     for(Iterator<?> i = ge.getChildElements(); i.hasNext();)
                     {
                        GenericElement child = (GenericElement)i.next();
                        this.marshal(ctx, child);
                     }
                  }

                  ch.endElement(null, ge.getLocalName(), ge.getLocalName());
               }
               catch(Exception e)
               {
                  throw new JBossXBRuntimeException(e);
               }
            }
         }
      );
   }

   private SchemaBinding getSchemaBinding(boolean genericElement)
   {
      // bind
      SchemaBinding schema = XsdBinder.bind(new StringReader(XSD), null);

      // get bound components
      TypeBinding type = schema.getType(new QName("http://org.jboss.ws/jbws434/types", "ArrayOfAny"));
      WildcardBinding wildcard = type.getWildcard();
      ElementBinding e = schema.getElement(new QName("http://org.jboss.ws/jbws434/types", "e"));

      // adjust binding
      ParticleHandler unresolvedElementHandler;
      CharactersHandler unresolvedCharactersHandler;
      if(genericElement)
      {
         unresolvedElementHandler = new GenericElementHandler();
         unresolvedCharactersHandler = new GenericCharactersHandler();
      }
      else
      {
         unresolvedElementHandler = new DomParticleHandler()
         {
            public void setParent(Object parent, Object o, QName elementName, ParticleBinding particle,
                  ParticleBinding parentParticle)
            {
               if (parent instanceof Element)
               {
                  ((Element) parent).appendChild((Element) o);
               }
               else
               {
                  ArrayOfAny arr = (ArrayOfAny)parent;
                  Object[] any = arr.get_any();
                  if(any == null)
                     any = new Object[1];
                  else
                  {
                     Object[] tmp = any;
                     any = new Object[any.length + 1];
                     System.arraycopy(tmp, 0, any, 0, tmp.length);
                  }
                  arr.set_any(any);
                  any[any.length - 1] = o;
               }
            }
         };
         unresolvedCharactersHandler = DomCharactersHandler.INSTANCE;
      }

      wildcard.setUnresolvedElementHandler(unresolvedElementHandler);
      wildcard.setUnresolvedCharactersHandler(unresolvedCharactersHandler);
      PropertyMetaData property = new PropertyMetaData();
      property.setName("_any");
      wildcard.setPropertyMetaData(property);

      ClassMetaData clsBinding = new ClassMetaData();
      clsBinding.setImpl(ArrayOfAny.class.getName());
      e.setClassMetaData(clsBinding);

      // to fail the resolution quickly
      schema.setSchemaResolver(new SchemaBindingResolver()
      {
         public String getBaseURI()
         {
            return null;
         }

         public void setBaseURI(String baseURI)
         {
         }

         public SchemaBinding resolve(String nsUri, String baseURI, String schemaLocation)
         {
            return null;
         }

         public LSInput resolveAsLSInput(String nsUri, String baseUri, String schemaLocation)
         {
            return null;
         }
      });

      return schema;
   }

   private static void assertArrayOfAnyGenericElement(Object o)
   {
      assertNotNull(o);
      assertTrue(o instanceof ArrayOfAny);
      ArrayOfAny arrayOfAny = (ArrayOfAny)o;
      assertNotNull(arrayOfAny._any);
      assertEquals(3, arrayOfAny._any.length);

      // child1
      GenericElement ge = (GenericElement)arrayOfAny._any[0];
      assertEquals("child1", ge.getLocalName());
      assertAttributesTotal(ge, 0);
      assertNoTextContent(ge);
      assertChildElementsTotal(ge, 3);

      for(Iterator<?> i = ge.getChildElements(); i.hasNext();)
      {
         ge = (GenericElement)i.next();
         String localName = ge.getLocalName();
         if("child2".equals(localName))
         {
            assertNoTextContent(ge);
            assertChildElementsTotal(ge, 0);
            assertAttributesTotal(ge, 2);
            assertAttribute(ge, "child2_attr1", "attr1");
            assertAttribute(ge, "child2_attr2", "attr2");
         }
         else if("child3".equals(localName))
         {
            assertTextContent(ge, "text content3");
            assertChildElementsTotal(ge, 0);
            assertAttributesTotal(ge, 0);
         }
         else if("child4".equals(localName))
         {
            assertNoTextContent(ge);
            assertAttributesTotal(ge, 1);
            assertAttribute(ge, "child4_attr1", "attr1");
            assertChildElementsTotal(ge, 1);

            ge = (GenericElement)ge.getChildElements().next();
            assertEquals("child5", ge.getLocalName());
            assertNoTextContent(ge);
            assertAttributesTotal(ge, 0);
            assertChildElementsTotal(ge, 0);
         }
         else
         {
            fail("child1 expected to have only child2, child3 and child4 as child elements but got: " + localName);
         }
      }

      ge = (GenericElement)arrayOfAny._any[1];
      assertEquals("child6", ge.getLocalName());
      assertAttributesTotal(ge, 0);
      assertNoTextContent(ge);
      assertChildElementsTotal(ge, 0);

      ge = (GenericElement)arrayOfAny._any[2];
      assertEquals("child7", ge.getLocalName());
      assertAttributesTotal(ge, 0);
      assertNoTextContent(ge);
      assertChildElementsTotal(ge, 1);

      ge = (GenericElement)ge.getChildElements().next();
      assertEquals("child8", ge.getLocalName());
      assertAttributesTotal(ge, 0);
      assertNoTextContent(ge);
      assertChildElementsTotal(ge, 0);
   }

   private static void assertArrayOfAnyDom(Object o)
   {
      assertNotNull(o);
      assertTrue(o instanceof ArrayOfAny);
      ArrayOfAny arrayOfAny = (ArrayOfAny)o;
      assertNotNull(arrayOfAny._any);
      assertEquals(3, arrayOfAny._any.length);

      // child1
      String somens = "http://somens";
      Element e = (Element)arrayOfAny._any[0];
      assertEquals("child1", e.getLocalName());
      assertEquals(somens, e.getNamespaceURI());
      assertEquals(0, e.getAttributes().getLength());
      assertNoTextContent(e);
      assertChildElementsTotal(e, 3);

      NodeList childNodes = e.getChildNodes();
      for(int i = 0; i < childNodes.getLength(); ++i)
      {
         Node node = childNodes.item(i);
         if(node.getNodeType() == Node.ELEMENT_NODE)
         {
            e = (Element)node;
            String localName = e.getLocalName();
            if("child2".equals(localName))
            {
               assertEquals(somens, e.getNamespaceURI());
               assertNoTextContent(e);
               assertChildElementsTotal(e, 0);
               assertAttributesTotal(e, e.getAttributes().getLength());
               assertAttribute(e, "child2_attr1", "attr1");
               assertAttribute(e, "child2_attr2", "attr2");
            }
            else if("child3".equals(localName))
            {
               assertEquals(somens, e.getNamespaceURI());
               assertTextContent(e, "text content3");
               assertChildElementsTotal(e, 0);
               assertAttributesTotal(e, 0);
            }
            else if("child4".equals(localName))
            {
               assertEquals(somens, e.getNamespaceURI());
               assertNoTextContent(e);
               assertAttributesTotal(e, 1);
               assertAttribute(e, "child4_attr1", "attr1");
               assertChildElementsTotal(e, 1);

               e = getChildElement(e, "child5");
               if(e == null)
               {
                  fail("Element child4 expected to have child element child5");
               }
               assertEquals(somens, e.getNamespaceURI());
               assertEquals("child5", e.getLocalName());
               assertNoTextContent(e);
               assertAttributesTotal(e, 0);
               assertChildElementsTotal(e, 0);
            }
            else
            {
               fail("child1 expected to have only child2, child3 and child4 as child elements but got: " + localName);
            }
         }
      }

      e = (Element)arrayOfAny._any[1];
      assertEquals("child6", e.getLocalName());
      assertEquals("http://anotherns", e.getNamespaceURI());
      assertAttributesTotal(e, 0);
      assertNoTextContent(e);
      assertChildElementsTotal(e, 0);

      e = (Element)arrayOfAny._any[2];
      assertEquals("child7", e.getLocalName());
      assertEquals("http://child7ns", e.getNamespaceURI());
      assertAttributesTotal(e, 0);
      assertNoTextContent(e);
      assertChildElementsTotal(e, 1);

      e = getChildElement(e, "child8");
      assertNotNull(e);
      assertEquals("http://child8ns", e.getNamespaceURI());
      assertAttributesTotal(e, 0);
      assertNoTextContent(e);
      assertChildElementsTotal(e, 0);
   }

   private static void assertChildElementsTotal(GenericElement e, int total)
   {
      if(e.getChildElementsTotal() != total)
      {
         fail("element " +
            e.getLocalName() +
            " was expected to have " +
            total +
            " elements but got " +
            e.getChildElementsTotal()
         );
      }
   }

   private static void assertChildElementsTotal(Element e, int total)
   {
      int was = 0;
      NodeList childNodes = e.getChildNodes();
      if(childNodes != null && childNodes.getLength() > 0)
      {
         for(int i = 0; i < childNodes.getLength(); ++i)
         {
            Node node = childNodes.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE)
            {
               ++was;
            }
         }
      }

      if(was != total)
      {
         fail("element " +
            e.getLocalName() +
            " was expected to have " +
            total +
            " elements but got " +
            was
         );
      }
   }

   private static void assertAttribute(GenericElement e, String attrName, String attrValue)
   {
      assertEquals(attrValue, e.getAttribute(attrName));
   }

   private static void assertAttribute(Element e, String attrName, String attrValue)
   {
      assertEquals(attrValue, e.getAttribute(attrName));
   }

   private static void assertAttributesTotal(GenericElement e, int total)
   {
      if(e.getAttributesTotal() != total)
      {
         fail("element " +
            e.getLocalName() +
            " was expected to have " +
            total +
            " attributes but got " +
            e.getAttributesTotal() + ": " + e.getAttributeNames()
         );
      }
   }

   private static void assertAttributesTotal(Element e, int total)
   {
      NamedNodeMap attributes = e.getAttributes();
      int was = attributes == null ? 0 : attributes.getLength();
      if(was != total)
      {
         fail("element " +
            e.getLocalName() +
            " was expected to have " +
            total +
            " attributes but got " +
            was
         );
      }
   }

   private static void assertTextContent(GenericElement e, String text)
   {
      assertEquals(text, e.getTextContent());
   }

   private static void assertTextContent(Element e, String text)
   {
      NodeList childNodes = e.getChildNodes();
      if(childNodes != null && childNodes.getLength() > 0)
      {
         for(int i = 0; i < childNodes.getLength(); ++i)
         {
            Node node = childNodes.item(i);
            if(node.getNodeType() == Node.TEXT_NODE)
            {
               String value = node.getNodeValue().trim();
               if(value.length() > 0)
               {
                  assertEquals(text, value);
                  return;
               }
            }
         }
      }
      fail("Element " + e.getLocalName() + " expected to have text content " + text);
   }

   private static void assertNoTextContent(GenericElement e)
   {
      if(e.getTextContent() != null)
      {
         fail("element " +
            e.getLocalName() +
            " was not expected to have text content but got " +
            e.getTextContent()
         );
      }
   }

   private static void assertNoTextContent(Element e)
   {
      NodeList childNodes = e.getChildNodes();
      if(childNodes != null && childNodes.getLength() > 0)
      {
         for(int i = 0; i < childNodes.getLength(); ++i)
         {
            Node child = childNodes.item(i);
            if(child.getNodeType() == Node.TEXT_NODE)
            {
               fail("element " +
                  e.getLocalName() +
                  " was not expected to have text content but got " +
                  child.getNodeValue()
               );
            }
         }
      }
   }

   private static Element getChildElement(Element e, String name)
   {
      NodeList childNodes = e.getChildNodes();
      if(childNodes != null && childNodes.getLength() > 0)
      {
         for(int i = 0; i < childNodes.getLength(); ++i)
         {
            Node node = childNodes.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE)
            {
               if(name.equals(node.getLocalName()))
               {
                  return (Element)node;
               }
            }
         }
      }
      return null;
   }

   // Inner

   public static class ArrayOfAny
   {
      public static final ArrayOfAny GE_INSTANCE;
      public static final ArrayOfAny DOM_INSTANCE;

      static
      {
         GE_INSTANCE = new ArrayOfAny();
         GE_INSTANCE._any = new GenericElement[3];

         GenericElement child1 = new GenericElement("http://somens", "child1");
         GE_INSTANCE._any[0] = child1;

         GenericElement child2 = new GenericElement("http://somens", "child2");
         child1.addChild(child2);
         child2.setAttribute("child2_attr1", "attr1");
         child2.setAttribute("child2_attr2", "attr2");

         GenericElement child3 = new GenericElement("http://somens", "child3");
         child1.addChild(child3);
         child3.setTextContent("text content3");

         GenericElement child4 = new GenericElement("http://somens", "child4");
         child1.addChild(child4);
         child4.setAttribute("child4_attr1", "attr1");

         GenericElement child5 = new GenericElement("http://somens", "child5");
         child4.addChild(child5);

         GenericElement child6 = new GenericElement("http://anotherns", "child6");
         GE_INSTANCE._any[1] = child6;

         GenericElement child7 = new GenericElement("http://child7ns", "child7");
         GE_INSTANCE._any[2] = child7;

         GenericElement child8 = new GenericElement("http://child8ns", "child8");
         child7.addChild(child8);

         assertArrayOfAnyGenericElement(GE_INSTANCE);
      }

      static
      {
         DOM_INSTANCE = new ArrayOfAny();
         DOM_INSTANCE._any = new Element[3];

         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder domBuilder = null;
         try
         {
            domBuilder = factory.newDocumentBuilder();
         }
         catch(ParserConfigurationException e)
         {
            throw new JBossXBRuntimeException("Failed to create document builder instance", e);
         }
         Document doc = domBuilder.newDocument();

         Element child1 = doc.createElementNS("http://somens", "child1");
         DOM_INSTANCE._any[0] = child1;

         Element child2 = doc.createElementNS("http://somens", "child2");
         child1.appendChild(child2);
         child2.setAttribute("child2_attr1", "attr1");
         child2.setAttribute("child2_attr2", "attr2");

         Element child3 = doc.createElementNS("http://somens", "child3");
         child1.appendChild(child3);
         child3.appendChild(doc.createTextNode("text content3"));

         Element child4 = doc.createElementNS("http://somens", "child4");
         child1.appendChild(child4);
         child4.setAttribute("child4_attr1", "attr1");

         Element child5 = doc.createElementNS("http://somens", "child5");
         child4.appendChild(child5);

         Element child6 = doc.createElementNS("http://anotherns", "child6");
         DOM_INSTANCE._any[1] = child6;

         Element child7 = doc.createElementNS("http://child7ns", "child7");
         DOM_INSTANCE._any[2] = child7;

         Element child8 = doc.createElementNS("http://child8ns", "child8");
         child7.appendChild(child8);

         assertArrayOfAnyDom(DOM_INSTANCE);
      }

      private Object[] _any;

      public Object[] get_any()
      {
         return _any;
      }

      public void set_any(Object[] _any)
      {
         this._any = _any;
      }
   }

   public static class GenericElement
   {
      private String localName;
      private String nsUri;
      private Map<String, String> attributes = Collections.emptyMap();
      private Map<String, GenericElement> childElements = Collections.emptyMap();
      private String textContent;

      public GenericElement(String localName)
      {
         this.localName = localName;
      }

      public GenericElement(String nsUri, String localName)
      {
         this.localName = localName;
         this.nsUri = nsUri;
      }

      public String getLocalName()
      {
         return localName;
      }

      public String getNsUri()
      {
         return nsUri;
      }

      public String getTextContent()
      {
         return textContent;
      }

      public void setTextContent(String textContent)
      {
         this.textContent = textContent;
      }

      public void setAttribute(String name, String value)
      {
         switch(attributes.size())
         {
            case 0:
               attributes = Collections.singletonMap(name, value);
               break;
            case 1:
               attributes = new HashMap<String, String>(attributes);
            default:
               attributes.put(name, value);
         }
      }

      public String getAttribute(String name)
      {
         return (String)attributes.get(name);
      }

      public void addChild(GenericElement child)
      {
         switch(childElements.size())
         {
            case 0:
               childElements = Collections.singletonMap(child.getLocalName(), child);
               break;
            case 1:
               childElements = new LinkedHashMap<String, GenericElement>(childElements);
            default:
               childElements.put(child.getLocalName(), child);
         }
      }

      public GenericElement getChild(String name)
      {
         return (GenericElement)childElements.get(name);
      }

      public Iterator<?> getChildElements()
      {
         return childElements.values().iterator();
      }

      public int getChildElementsTotal()
      {
         return childElements.size();
      }

      public Set<?> getAttributeNames()
      {
         return attributes.keySet();
      }

      public int getAttributesTotal()
      {
         return attributes.size();
      }
   }

   public static class GenericCharactersHandler
      extends CharactersHandler
   {
      public Object unmarshalEmpty(QName qName,
                                   TypeBinding typeBinding,
                                   NamespaceContext nsCtx,
                                   ValueMetaData valueMetaData)
      {
         return "";
      }

      public Object unmarshal(QName qName,
                              TypeBinding typeBinding,
                              NamespaceContext nsCtx,
                              ValueMetaData valueMetaData,
                              String value)
      {
         return value;
      }

      public void setValue(QName qName, ElementBinding element, Object owner, Object value)
      {
         GenericElement e = (GenericElement)owner;
         String text = (String)value;
         e.setTextContent(text);
      }
   }

   public static class GenericElementHandler
      implements ParticleHandler
   {
      public Object startParticle(Object parent,
                                  QName elementName,
                                  ParticleBinding particle,
                                  Attributes attrs,
                                  NamespaceContext nsCtx)
      {
         GenericElement el = new GenericElement(elementName.getNamespaceURI(), elementName.getLocalPart());

         if(attrs != null)
         {
            for(int i = 0; i < attrs.getLength(); ++i)
            {
               el.setAttribute(attrs.getLocalName(i), attrs.getValue(i));
            }
         }

         return el;
      }

      public Object endParticle(Object o, QName elementName, ParticleBinding particle)
      {
         return o;
      }

      public void setParent(Object parent,
                            Object o,
                            QName elementName,
                            ParticleBinding particle,
                            ParticleBinding parentParticle)
      {
         if(parent instanceof GenericElement)
         {
            ((GenericElement)parent).addChild((GenericElement)o);
         }
         else
         {
            ArrayOfAny arr = (ArrayOfAny)parent;
            Object[] any = arr.get_any();
            if(any == null)
               any = new Object[1];
            else
            {
               Object[] tmp = any;
               any = new Object[any.length + 1];
               System.arraycopy(tmp, 0, any, 0, tmp.length);
            }
            arr.set_any(any);
            any[any.length - 1] = o;
         }
      }
   }
}
