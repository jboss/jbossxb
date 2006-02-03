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
package org.jboss.util.xml;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.NamespaceRegistry;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class Dom2Sax
{
   public static void dom2sax(Element e, ContentHandler ch) throws SAXException
   {
      ch.startDocument();

      process(e, ch, new NamespaceRegistry());

      ch.endDocument();
   }

   private static void process(Element e, ContentHandler ch, NamespaceRegistry nsReg) throws SAXException
   {
      String ns = e.getNamespaceURI();
      if(ns == null)
      {
         ns = "";
      }

      AttributesImpl attrs = new AttributesImpl();

      List startedPrefixes = Collections.EMPTY_LIST;

      NamedNodeMap domAttrs = e.getAttributes();
      if(domAttrs != null && domAttrs.getLength() > 0)
      {
         // run through xmlns first to declare all the namespaces
         for(int i = 0; i < domAttrs.getLength(); ++i)
         {
            Attr attr = (Attr)domAttrs.item(i);
            String attrNs = attr.getNamespaceURI();
            String attrLocal = attr.getLocalName();

            if(attrNs != null && isXmlns(attrNs))
            {
               String prefix;
               String attrPrefix;
               if("xmlns".equals(attrLocal))
               {
                  prefix = "";
                  attrPrefix = "";
               }
               else
               {
                  prefix = attrLocal;
                  attrPrefix = "xmlns";
               }

               String attrVal = attr.getValue();
               nsReg.addPrefixMapping(prefix, attrVal);
               ch.startPrefixMapping(prefix, attrVal);
               startedPrefixes = add(startedPrefixes, prefix);

               attrs.addAttribute(attrNs, attrLocal, buildQName(attrPrefix, attrLocal), null, attrVal);
            }
         }

         for(int i = 0; i < domAttrs.getLength(); ++i)
         {
            Attr attr = (Attr)domAttrs.item(i);
            String attrNs = attr.getNamespaceURI();
            String attrLocal = attr.getLocalName();

            if(attrNs == null)
            {
               attrNs = "";
            }

            if(!isXmlns(attrNs))
            {
               String prefix = nsReg.getPrefix(ns);
               if(prefix == null)
               {
                  prefix = attrLocal + "_ns";
                  nsReg.addPrefixMapping(prefix, ns);
                  ch.startPrefixMapping(prefix, ns);
                  startedPrefixes = add(startedPrefixes, prefix);
                  attrs.addAttribute(Constants.NS_XML_SCHEMA, prefix, "xmlns:" + prefix, null, ns);
               }

               attrs.addAttribute(attrNs, attrLocal, buildQName(prefix, attrLocal), null, attr.getValue());
            }
         }
      }

      String localName = e.getLocalName();
      String prefix = nsReg.getPrefix(ns);
      if(prefix == null)
      {
         prefix = localName + "_ns";
         nsReg.addPrefixMapping(prefix, ns);
         ch.startPrefixMapping(prefix, ns);
         startedPrefixes = add(startedPrefixes, prefix);
         attrs.addAttribute(Constants.NS_XML_SCHEMA, prefix, "xmlns:" + prefix, null, ns);
      }

      String qName = buildQName(prefix, localName);
      ch.startElement(ns, localName, qName, attrs);

      NodeList childNodes = e.getChildNodes();
      if(childNodes != null && childNodes.getLength() > 0)
      {
         for(int i = 0; i < childNodes.getLength(); ++i)
         {
            Node node = childNodes.item(i);
            switch(node.getNodeType())
            {
               case Node.ELEMENT_NODE:
                  process((Element)node, ch, nsReg);
                  break;
               case Node.CDATA_SECTION_NODE:
               case Node.TEXT_NODE:
                  String value = node.getNodeValue();
                  ch.characters(value.toCharArray(), 0, value.length());
                  break;
               case Node.ENTITY_REFERENCE_NODE:
                  String ref = '&' + node.getNodeName() + ';';
                  ch.characters(ref.toCharArray(), 0, ref.length());
                  break;
               case Node.ENTITY_NODE:
                  ch.skippedEntity(node.getNodeName());
                  break;
            }
         }
      }

      ch.endElement(ns, localName, qName);

      if(startedPrefixes.size() > 0)
      {
         for(int i = startedPrefixes.size() - 1; i >= 0; --i)
         {
           String pref = (String)startedPrefixes.get(i);
           nsReg.removePrefixMapping(pref);
           ch.endPrefixMapping(pref);
         }
      }
   }

   private static boolean isXmlns(String ns)
   {
      return ns.startsWith("http://www.w3.org/2000/xmlns");
   }

   private static String buildQName(String prefix, String localName)
   {
      return prefix == null || prefix.length() == 0 ?
         localName :
         prefix + ':' + localName;
   }

   private static List add(List list, Object o)
   {
      switch(list.size())
      {
         case 0:
            list = Collections.singletonList(o);
            break;
         case 1:
            list = new ArrayList(list);
         default:
            list.add(o);
      }
      return list;
   }
}
