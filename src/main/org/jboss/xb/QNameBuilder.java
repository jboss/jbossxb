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
package org.jboss.xb;

import org.jboss.logging.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.namespace.QName;

/**
 * A QName builder that discovers the namespaceURI for a given prefix by walking
 * up the document tree.
 *
 * The combined name is of the form [prefix:]localPart
 *
 * @author Thomas.Diesler@jboss.org
 * @version $Revision$
 */
public final class QNameBuilder
{
   private static Logger log = Logger.getLogger(QNameBuilder.class);

   /**
    * Build a QName from a combined name
    * @param element The current element
    * @param combinedName A name of form prefix:localPart
    * @return A QName, or null
    */
   public static QName buildQName(Element element, String combinedName)
   {
      if (combinedName == null)
         return null;

      int colonIndex = combinedName.indexOf(":");
      if (colonIndex < 0)
         return new QName(combinedName);

      String prefix = combinedName.substring(0, colonIndex);
      String localPart = combinedName.substring(colonIndex + 1);

      Node currNode = element;
      String namespaceURI = getNamespaceURI(currNode, prefix);
      while (namespaceURI == null && currNode != null)
      {
         Node parentNode = currNode.getParentNode();
         if (parentNode != null && parentNode != currNode)
            namespaceURI = getNamespaceURI(parentNode, prefix);

         if (parentNode == currNode)
            break;

         currNode = parentNode;
      }

      if (namespaceURI != null)
         return new QName(namespaceURI, localPart, prefix);

      log.warn("Cannot find namespaceURI for name: " + combinedName);
      return new QName(localPart);
   }

   /**
    * Get the namespaceURI from a given prefix from the current node.
    */
   private static String getNamespaceURI(Node node, String prefix)
   {
      String namespaceURI = null;
      NamedNodeMap attrs = node.getAttributes();
      if (attrs != null)
      {
         for (int i = 0; namespaceURI == null && i < attrs.getLength(); i++)
         {
            Node attr = attrs.item(i);
            if (("xmlns:" + prefix).equals(attr.getNodeName()))
               namespaceURI = attr.getNodeValue();
         }
      }
      return namespaceURI;
   }
}
