/*
 * JBoss, the OpenSource EJB server
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.util.xml;

import org.jboss.logging.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * DOM2 utilites
 *
 * @author Thomas.Diesler@jboss.org
 */
public final class DOMUtils
{
   private static Logger log = Logger.getLogger(DOMUtils.class);

   // The DocumentBuilder
   private static DocumentBuilder builder = getDocumentBuilder();

   // All elements created by the same thread belong to the same doc
   private static InheritableThreadLocal documentThreadLocal = new InheritableThreadLocal();

   // Hide the constructor
   private DOMUtils()
   {
   }

   /** Initialise the the DocumentBuilder
    */
   public static DocumentBuilder getDocumentBuilder()
   {
      if (builder == null)
      {
         try
         {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(true);
            builder = factory.newDocumentBuilder();
         }
         catch (ParserConfigurationException e)
         {
            log.error(e);
         }
      }
      return builder;
   }

   /** Parse the given XML string and return the root Element
    */
   public static Element parse(String xmlString) throws IOException
   {
      try
      {
         return parse(new ByteArrayInputStream(xmlString.getBytes()));
      }
      catch (IOException e)
      {
         log.error("Cannot parse: " + xmlString);
         throw e;
      }
   }

   /** Parse the given XML stream and return the root Element
    */
   public static Element parse(InputStream xmlStream) throws IOException
   {
      try
      {
         Document doc = builder.parse(xmlStream);
         Element root = doc.getDocumentElement();
         return root;
      }
      catch (SAXException e)
      {
         throw new IOException(e.toString());
      }
   }

   /** Create an Element for a given name
    */
   public static Element createElement(String localPart)
   {
      Document doc = getOwnerDocument();
      log.trace("createElement {}" + localPart);
      return doc.createElement(localPart);
   }

   /** Create an Element for a given name and prefix
    */
   public static Element createElement(String localPart, String prefix)
   {
      Document doc = getOwnerDocument();
      log.trace("createElement {}" + prefix + ":" + localPart);
      return doc.createElement(prefix + ":" + localPart);
   }

   /** Create an Element for a given name, prefix and uri
    */
   public static Element createElement(String localPart, String prefix, String uri)
   {
      Document doc = getOwnerDocument();
      if (prefix == null || prefix.length() == 0)
      {
         log.trace("createElement {" + uri + "}" + localPart);
         return doc.createElementNS(uri, localPart);
      }
      else
      {
         log.trace("createElement {" + uri + "}" + prefix + ":" + localPart);
         return doc.createElementNS(uri, prefix + ":" + localPart);
      }
   }

   /** Create an Element for a given QName
    */
   public static Element createElement(QName qname)
   {
      return createElement(qname.getLocalPart(), qname.getPrefix(), qname.getNamespaceURI());
   }

   /** Create a org.w3c.dom.Text node
    */
   public static Text createTextNode(String value)
   {
      Document doc = getOwnerDocument();
      return doc.createTextNode(value);
   }

   /** Get the value from the given attribute
    *
    * @return null if the attribute value is empty or the attribute is not present
    */
   public static String getAttributeValue(Element el, String attrName)
   {
      return getAttributeValue(el, new QName(attrName));
   }

   /** Get the value from the given attribute
    *
    * @return null if the attribute value is empty or the attribute is not present
    */
   public static String getAttributeValue(Element el, QName attrName)
   {
      String attr = null;
      if ("".equals(attrName.getNamespaceURI()))
         attr = el.getAttribute(attrName.getLocalPart());
      else attr = el.getAttributeNS(attrName.getNamespaceURI(), attrName.getLocalPart());

      if ("".equals(attr))
         attr = null;

      return attr;
   }

   /** Get the qname value from the given attribute
    */
   public static QName getAttributeValueAsQName(Element el, String attrName)
   {
      return getAttributeValueAsQName(el, new QName(attrName));

   }

   /** Get the qname value from the given attribute
    */
   public static QName getAttributeValueAsQName(Element el, QName attrName)
   {
      QName qname = null;

      String attr = getAttributeValue(el, attrName);
      if (attr != null)
      {
         String prefix = "";
         String localPart = attr;
         String namespaceURI = "";

         int colonIndex = attr.indexOf(":");
         if (colonIndex > 0)
         {
            prefix = attr.substring(0, colonIndex);
            localPart = attr.substring(colonIndex + 1);

            Element nsElement = el;
            while (namespaceURI.equals("") && nsElement != null)
            {
               namespaceURI = nsElement.getAttribute("xmlns:" + prefix);
               if (namespaceURI.equals(""))
                  nsElement = getParentElement(nsElement);
            }

            if (namespaceURI.equals(""))
               throw new IllegalArgumentException("Cannot find namespace uri for: " + attr);
         }

         qname = new QName(namespaceURI, localPart, prefix);
      }

      return qname;
   }

   /** Get the boolean value from the given attribute
    */
   public static boolean getAttributeValueAsBoolean(Element el, String attrName)
   {
      return getAttributeValueAsBoolean(el, new QName(attrName));
   }

   /** Get the boolean value from the given attribute
    */
   public static boolean getAttributeValueAsBoolean(Element el, QName attrName)
   {
      String attrVal = getAttributeValue(el, attrName);
      boolean ret = "true".equalsIgnoreCase(attrVal) || "1".equalsIgnoreCase(attrVal);
      return ret;
   }

   /** Get the integer value from the given attribute
    */
   public static Integer getAttributeValueAsInteger(Element el, String attrName)
   {
      return getAttributeValueAsInteger(el, new QName(attrName));
   }

   /** Get the integer value from the given attribute
    */
   public static Integer getAttributeValueAsInteger(Element el, QName attrName)
   {
      String attrVal = getAttributeValue(el, attrName);
      return (attrVal != null ? new Integer(attrVal) : null);
   }

   /** Copy attributes between elements
    */
   public static void copyAttributes(Element destElement, Element srcElement)
   {
      NamedNodeMap attribs = srcElement.getAttributes();
      for (int i = 0; i < attribs.getLength(); i++)
      {
         Attr attr = (Attr)attribs.item(i);
         destElement.setAttributeNS(attr.getNamespaceURI(), attr.getName(), attr.getNodeValue());
      }
   }

   /** True if the node has child elements
    */
   public static boolean hasChildElements(Node node)
   {
      NodeList nlist = node.getChildNodes();
      for (int i = 0; i < nlist.getLength(); i++)
      {
         Node child = nlist.item(i);
         if (child.getNodeType() == Node.ELEMENT_NODE)
            return true;
      }
      return false;
   }

   /** Gets child elements
    */
   public static List getChildElements(Node node)
   {
      ArrayList list = new ArrayList();
      NodeList nlist = node.getChildNodes();
      for (int i = 0; i < nlist.getLength(); i++)
      {
         Node child = nlist.item(i);
         if (child.getNodeType() == Node.ELEMENT_NODE)
            list.add(child);
      }
      return list;
   }

   /** Get the concatenated text content, or null.
    */
   public static String getTextContent(Node node)
   {
      boolean hasTextContent = false;
      StringBuffer buffer = new StringBuffer();
      NodeList nlist = node.getChildNodes();
      for (int i = 0; i < nlist.getLength(); i++)
      {
         Node child = nlist.item(i);
         if (child.getNodeType() == Node.TEXT_NODE)
         {
            buffer.append(child.getNodeValue());
            hasTextContent = true;
         }
      }
      return (hasTextContent ? buffer.toString() : null);
   }

   /** Gets the first child element
    */
   public static Element getFirstChildElement(Node node)
   {
      return getFirstChildElementIntern(node, null);
   }

   /** Gets the first child element for a given name
    */
   public static Element getFirstChildElement(Node node, String nodeName)
   {
      return getFirstChildElementIntern(node, new QName(nodeName));
   }
   
   /** Gets the first child element for a given name
    */
   public static Element getFirstChildElement(Node node, QName nodeName)
   {
      return getFirstChildElementIntern(node, nodeName);
   }
   
   private static Element getFirstChildElementIntern(Node node, QName nodeName)
   {
      Element childElement = null;
      Iterator it = getChildElements(node).iterator();
      while (childElement == null && it.hasNext())
      {
         Element el = (Element)it.next();
         QName elementName = new QName(el.getNamespaceURI(), el.getLocalName());
         if (nodeName == null || nodeName.equals(elementName))
            childElement = (Element)el;
      }
      return childElement;
   }

   /** Gets parent element or null if there is none
    */
   public static Element getParentElement(Node node)
   {
      Node parent = node.getParentNode();
      return (parent instanceof Element ? (Element)parent : null);
   }

   /** Get the owner document that is associated with the current thread */
   public static Document getOwnerDocument()
   {
      Document doc = (Document)documentThreadLocal.get();
      if (doc == null)
      {
         doc = builder.newDocument();
         documentThreadLocal.set(doc);
      }
      return doc;
   }

   /** Set the owner document that is associated with the current thread */
   public static void setOwnerDocument(Document doc)
   {
      documentThreadLocal.set(doc);
   }
}
