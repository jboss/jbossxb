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

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestSuite;

import org.jboss.util.Strings;
import org.jboss.util.xml.DOMWriter;
import org.jboss.xb.binding.metadata.ClassMetaData;
import org.jboss.xb.binding.metadata.ValueMetaData;
import org.jboss.xb.binding.sunday.unmarshalling.CharactersHandler;
import org.jboss.xb.binding.sunday.unmarshalling.DefaultElementInterceptor;
import org.jboss.xb.binding.sunday.unmarshalling.DefaultHandlers;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleHandler;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.WildcardBinding;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;

/**
 * AnyComplexTypeUnitTestCase.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class AnyComplexTypeUnitTestCase extends AbstractJBossXBTest
{  
   private static final String NS = "http://www.jboss.org/test/xml/anyComplexType";
   
   public static final TestSuite suite()
   {
      return new TestSuite(AnyComplexTypeUnitTestCase.class);
   }
   
   public AnyComplexTypeUnitTestCase(String name)
   {
      super(name);
   }

   public void testDOMFromWildcard() throws Exception
   {
      SchemaBinding schema = bind("AnyComplexType.xsd");
      schema.setIgnoreUnresolvedFieldOrClass(false);

      ClassMetaData classMetaData = new ClassMetaData();
      classMetaData.setImpl(Top.class.getName());
      ElementBinding element = schema.getElement(new QName(NS, "top"));
      assertNotNull(element);
      element.setClassMetaData(classMetaData);
      
      DOMUnresolvedHandler unresolved = new DOMUnresolvedHandler();
      TypeBinding type = schema.getType(new QName(NS, "any-complexType"));
      type.setStartElementCreatesObject(false);
      WildcardBinding wild = type.getWildcard();
      assertNotNull(wild);
      wild.setUnresolvedElementHandler(unresolved);
      wild.setUnresolvedCharactersHandler(unresolved);

      Top top = (Top) unmarshal("AnyComplexTypeFromWildCard.xml", schema, Top.class);
      Element dom = top.element;
      assertNotNull(dom);
      getLog().debug(DOMWriter.printNode(dom, true));
      assertElement("e1", dom);
      Element e1s1 = getUniqueChild(dom, "e1s1");
      assertElement("e1s1", e1s1);
      Element e1s1s1 = getUniqueChild(e1s1, "e1s1s1");
      assertElement("e1s1s1", e1s1s1);
      Element e1s1s2 = getUniqueChild(e1s1, "e1s1s2");
      assertElement("e1s1s2", e1s1s2);
      Element e1s2 = getUniqueChild(dom, "e1s2");
      assertElement("e1s2", e1s2);
   }

   public void testDOMFromWildcardAndElement() throws Exception
   {
      SchemaBinding schema = bind("AnyComplexType.xsd");
      schema.setIgnoreUnresolvedFieldOrClass(false);

      ClassMetaData classMetaData = new ClassMetaData();
      classMetaData.setImpl(Top.class.getName());
      ElementBinding element = schema.getElement(new QName(NS, "top"));
      assertNotNull(element);
      element.setClassMetaData(classMetaData);
      
      DOMUnresolvedHandler unresolved = new DOMUnresolvedHandler();
      TypeBinding type = schema.getType(new QName(NS, "any-complexType"));
      type.setStartElementCreatesObject(false);
      WildcardBinding wild = type.getWildcard();
      assertNotNull(wild);
      wild.setUnresolvedElementHandler(unresolved);
      wild.setUnresolvedCharactersHandler(unresolved);
      
      DOMInterceptor interceptor = new DOMInterceptor();
      element = schema.getElement(new QName(NS, "element"));
      element.pushInterceptor(interceptor);

      Top top = (Top) unmarshal("AnyComplexTypeFromWildCardAndElement.xml", schema, Top.class);
      Element dom = top.element;
      assertNotNull(dom);
      getLog().debug(DOMWriter.printNode(dom, true));
      assertElement("element", dom, true); // TODO FIXME
      Element e1 = getUniqueChild(dom, "e1");
      assertElement("e1", e1);
      Element e1s1 = getUniqueChild(e1, "e1s1");
      assertElement("e1s1", e1s1);
      Element e1s1s1 = getUniqueChild(e1s1, "e1s1s1");
      assertElement("e1s1s1", e1s1s1);
      Element e1s1s2 = getUniqueChild(e1s1, "e1s1s2");
      assertElement("e1s1s2", e1s1s2);
      Element e1s2 = getUniqueChild(e1, "e1s2");
      assertElement("e1s2", e1s2);
   }

   public void testDOMFromMultipleWildcardAndElement() throws Exception
   {
      SchemaBinding schema = bind("AnyComplexType.xsd");
      schema.setIgnoreUnresolvedFieldOrClass(false);

      ClassMetaData classMetaData = new ClassMetaData();
      classMetaData.setImpl(Top.class.getName());
      ElementBinding element = schema.getElement(new QName(NS, "top"));
      assertNotNull(element);
      element.setClassMetaData(classMetaData);
      
      DOMUnresolvedHandler unresolved = new DOMUnresolvedHandler();
      TypeBinding type = schema.getType(new QName(NS, "any-complexType"));
      //type.setStartElementCreatesObject(false);
      type.setHandler(new DOMUnresolvedHandler()
      {
         public void setParent(Object parent, Object o, QName qName, ParticleBinding particle,
               ParticleBinding parentParticle)
         {
            DefaultHandlers.ELEMENT_HANDLER.setParent(parent, o, qName, particle, parentParticle);
         }
      }
      );
      WildcardBinding wild = type.getWildcard();
      assertNotNull(wild);
      wild.setUnresolvedElementHandler(unresolved);
      wild.setUnresolvedCharactersHandler(unresolved);
      
      //DOMInterceptor interceptor = new DOMInterceptor();
      //element = schema.getElement(new QName(NS, "element"));
      //element.pushInterceptor(interceptor);

      Top top = (Top) unmarshal("AnyComplexTypeFromMultipleWildCardAndElement.xml", schema, Top.class);
      Element dom = top.element;
      assertNotNull(dom);
      getLog().debug(DOMWriter.printNode(dom, true));
      assertElement("element", dom, true); // TODO FIXME
      Element e1 = getUniqueChild(dom, "e1");
      assertElement("e1", e1);
      Element e1s1 = getUniqueChild(e1, "e1s1");
      assertElement("e1s1", e1s1);
      Element e1s1s1 = getUniqueChild(e1s1, "e1s1s1");
      assertElement("e1s1s1", e1s1s1);
      Element e1s1s2 = getUniqueChild(e1s1, "e1s1s2");
      assertElement("e1s1s2", e1s1s2);
      Element e1s2 = getUniqueChild(e1, "e1s2");
      assertElement("e1s2", e1s2);
      Element e2 = getUniqueChild(dom, "e2");
      assertElement("e2", e2);
   }
   
   protected void assertElement(String elementName, Element element)
   {
      assertElement(elementName, element, false);
   }
   
   protected void assertElement(String elementName, Element element, boolean ignoreChars)
   {
      assertNotNull(element);
      assertEquals(elementName, element.getLocalName());
      assertEquals(elementName + "a1", element.getAttribute(elementName + "a1"));
      assertEquals(elementName + "a2", element.getAttribute(elementName + "a2"));
      if (ignoreChars == false)
         assertEquals(elementName + "c1", getElementContent(element));
   }
   
   protected static String getElementContent(Element element)
   {
      if (element == null)
         return null;
      NodeList children = element.getChildNodes();
      StringBuffer result = new StringBuffer();
      for (int i = 0; i < children.getLength(); ++i)
      {
         Node child = children.item(i);
         if (child.getNodeType() == Node.TEXT_NODE || child.getNodeType() == Node.CDATA_SECTION_NODE)
            result.append(child.getNodeValue());
      }
      return result.toString().trim();
   }
   
   protected static Element getUniqueChild(Element element, String childName)
   {
      if (element == null)
         return null;
      NodeList children = element.getChildNodes();
      Element result = null;
      for (int i = 0; i < children.getLength(); ++i)
      {
         Node child = children.item(i);
         if (child.getNodeType() == Node.ELEMENT_NODE && childName.equals(child.getNodeName()))
         {
            if (result != null)
               fail(childName + " not unique");
            result = (Element) child;
         }
      }
      
      if (result == null)
         fail("No " + childName);
      return result;
   }
   
   public Element createTopElement(String namespace, String name)
   {
      try
      {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         factory.setNamespaceAware(true);
         DocumentBuilder builder = factory.newDocumentBuilder();
         DOMImplementation impl = builder.getDOMImplementation();
         Document document = impl.createDocument(null, null, null);
         
         Element element = document.createElementNS(namespace, name);
         document.appendChild(element);
         getLog().debug("createTopElement " + namespace + ":" + name + " result=" + toDebugString(element));
         return element;
      }
      catch (RuntimeException e)
      {
         throw e;
      }
      catch (Exception e)
      {
         throw new RuntimeException("Error creating dom", e);
      }
   }
   
   public Element appendChildElement(Element parentElement, String namespace, String name)
   {
      Document document = parentElement.getOwnerDocument();
      Element element = document.createElementNS(namespace, name);
      parentElement.appendChild(element);
      getLog().debug("appendChild parent=" + toDebugString(parentElement) + " child=" + toDebugString(element));
      return element;
   }
   
   public void addAttributes(Element element, Attributes attrs)
   {
      for (int i = 0; i < attrs.getLength(); ++i)
      {
         String local = attrs.getLocalName(i);
         String nsURI = attrs.getURI(i);
         String value = attrs.getValue(i);
         getLog().debug("setAttribute " + nsURI + " " + local + " element=" + toDebugString(element) + " value=" + value);
         element.setAttributeNS(nsURI, local, value);
      }
   }
   
   public void setText(Object owner, Object value, QName qName)
   {
      if (value == null)
         return;
      if (owner == null || owner instanceof Element == false)
         throw new IllegalStateException("Unexpected owner: " + owner + " for " + qName);
      if (value instanceof String == false)
         throw new IllegalStateException("Unexpected value " + value + " for " + qName);
      Element element = (Element) owner;
      Text text = element.getOwnerDocument().createTextNode((String) value);
      getLog().debug("setText " + qName + " parent=" + toDebugString(owner) + " child=" + toDebugString(value));
      element.appendChild(text);
   }

   public class DOMUnresolvedHandler extends CharactersHandler implements ParticleHandler
   {
      public Object startParticle(Object parent, QName elementName, ParticleBinding particle, Attributes attrs, NamespaceContext nsCtx)
      {
         getLog().debug("startParticle " + elementName + " parent=" + toDebugString(parent));
         Element element = null;
         if (parent == null || parent instanceof Element == false)
            element = createTopElement(elementName.getNamespaceURI(), elementName.getLocalPart());
         else
            element = appendChildElement((Element) parent, elementName.getNamespaceURI(), elementName.getLocalPart());
         addAttributes(element, attrs);
         return element;
      }

      public Object endParticle(Object o, QName elementName, ParticleBinding particle)
      {
         getLog().debug("endParticle " + elementName + " result=" + toDebugString(o));
         return o;
      }

      public void setParent(Object parent, Object o, QName elementName, ParticleBinding particle, ParticleBinding parentParticle)
      {
         getLog().debug("setParent " + elementName + " parent=" + toDebugString(parent) + " o=" + toDebugString(o));
      }

      public void setValue(QName qName, ElementBinding element, Object owner, Object value)
      {
         setText(owner, value, qName);
      }

      public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx, ValueMetaData valueMetaData, String value)
      {
         return value;
      }

      public Object unmarshalEmpty(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx, ValueMetaData valueMetaData)
      {
         return null;
      }
   }

   public class DOMInterceptor extends DefaultElementInterceptor
   {
      public Object startElement(Object parent, QName qName, TypeBinding type)
      {
         getLog().debug("startElement " + qName + " parent=" + toDebugString(parent));
         Element element = null;
         if (parent == null || parent instanceof Element == false)
         {
            element = createTopElement(qName.getNamespaceURI(), qName.getLocalPart());
            ((Top) parent).element = element;
         }
         else
            element = appendChildElement((Element) parent, qName.getNamespaceURI(), qName.getLocalPart());
         return element;
      }

      public void attributes(Object o, QName elementName, TypeBinding type, Attributes attrs, NamespaceContext nsCtx)
      {
         getLog().debug("setAttributes " + elementName + " parent=" + toDebugString(o));
         if (o == null || o instanceof Element == false)
            throw new IllegalStateException(Strings.defaultToString(o) + " is not an instanceof Element");
         addAttributes((Element) o, attrs);
      }

      public void characters(Object o, QName qName, TypeBinding type, NamespaceContext nsCtx, String text)
      {
         if (o == null || o instanceof Element == false)
            throw new IllegalStateException(Strings.defaultToString(o) + " is not an instanceof Element");
         setText(o, text, qName);
      }

      public void add(Object parent, Object child, QName qName)
      {
         getLog().debug("add " + qName + " parent=" + toDebugString(parent) + " child=" + toDebugString(child));
         if (parent == null || parent instanceof Element == false)
            throw new IllegalStateException(Strings.defaultToString(parent) + " is not an instanceof Element");
         if (child == null || child instanceof Element == false)
            throw new IllegalStateException(Strings.defaultToString(child) + " is not an instanceof Element");
         
         Element parentElement = (Element) parent;
         Element childElement = (Element) child;
         
         parentElement.getOwnerDocument().adoptNode(childElement);
         parentElement.appendChild(childElement);
      }
   }
   
   public static class Top
   {
      public Element element;
   }
   
   public static String toDebugString(Object object)
   {
      if (object == null)
         return "null";
      if (object instanceof String)
         return object.toString();
      if (object instanceof Element == false)
         return Strings.defaultToString(object);
      Element element = (Element) object;
      return "Element@" + System.identityHashCode(element) + "{" + element.getLocalName() + "}";
   }
}
