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

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Traverse a DOM tree in order to print a document that is parsed.
 *
 * @author Andy Clark, IBM
 * @author Thomas.Diesler@jboss.org
 * @version $Revision$
 */
public class DOMWriter
{
   // Print writer
   private PrintWriter out;
   // True, if canonical output
   private boolean canonical;
   // True, if pretty printing should be used
   private boolean prettyprint;
   // True, if the XML declaration should be written
   private boolean writeXMLDeclaration;
   // Explicit character set encoding
   private String charsetName;

   // indent for the pretty printer
   private int prettyIndent;
   // True, if the XML declaration has been written
   private boolean wroteXMLDeclaration;

   public DOMWriter(Writer w)
   {
      this.out = new PrintWriter(w);
   }

   public DOMWriter(OutputStream stream) 
   {
      this.out = new PrintWriter(new OutputStreamWriter(stream));
   }

   public DOMWriter(OutputStream stream, String charsetName) 
   {
      try
      {
         this.out = new PrintWriter(new OutputStreamWriter(stream, charsetName));
         this.charsetName = charsetName;
         this.writeXMLDeclaration = true;
      }
      catch (UnsupportedEncodingException e)
      {
         throw new IllegalArgumentException("Unsupported encoding: " + charsetName);
      }
   }

   /** 
    * Print a node with explicit prettyprinting.
    * The defaults for all other DOMWriter properties apply. 
    *  
    */
   public static String printNode(Node node, boolean prettyprint)
   {
      StringWriter strw = new StringWriter();
      new DOMWriter(strw).setPrettyprint(prettyprint).print(node);
      return strw.toString();
   }

   public boolean isCanonical()
   {
      return canonical;
   }

   /** 
    * Set wheter entities should appear in their canonical form.
    * The default is false.
    */
   public DOMWriter setCanonical(boolean canonical)
   {
      this.canonical = canonical;
      return this;
   }

   public boolean isPrettyprint()
   {
      return prettyprint;
   }

   /** 
    * Set wheter element should be indented.
    * The default is false.
    */
   public DOMWriter setPrettyprint(boolean prettyprint)
   {
      this.prettyprint = prettyprint;
      return this;
   }

   public boolean isWriteXMLDeclaration()
   {
      return writeXMLDeclaration;
   }

   /** 
    * Set wheter the XML declaration should be written.
    * The default is false.
    */
   public DOMWriter setWriteXMLDeclaration(boolean writeXMLDeclaration)
   {
      this.writeXMLDeclaration = writeXMLDeclaration;
      return this;
   }

   public void print(Node node)
   {
      printInternal(node, false);
   }

   private void printInternal(Node node, boolean indentEndMarker)
   {
      // is there anything to do?
      if (node == null)
      {
         return;
      }

      if (wroteXMLDeclaration == false && writeXMLDeclaration == true && canonical == false)
      {
         out.print("<?xml version='1.0'");
         if (charsetName != null)
            out.print(" encoding='" + charsetName + "'");

         out.println("?>");
         wroteXMLDeclaration = true;
      }

      int type = node.getNodeType();
      boolean hasChildNodes = node.getChildNodes().getLength() > 0;

      switch (type)
      {
         // print document
         case Node.DOCUMENT_NODE:
         {
            NodeList children = node.getChildNodes();
            for (int iChild = 0; iChild < children.getLength(); iChild++)
            {
               printInternal(children.item(iChild), false);
            }
            out.flush();
            break;
         }

         // print element with attributes
         case Node.ELEMENT_NODE:
         {
            if (prettyprint)
            {
               for (int i = 0; i < prettyIndent; i++)
               {
                  out.print(' ');
               }
               prettyIndent++;
            }

            out.print('<');
            out.print(node.getNodeName());
            Attr attrs[] = sortAttributes(node.getAttributes());
            for (int i = 0; i < attrs.length; i++)
            {
               Attr attr = attrs[i];
               out.print(' ');
               out.print(attr.getNodeName());
               out.print("='");
               out.print(normalize(attr.getNodeValue()));
               out.print("'");
            }

            if (hasChildNodes)
            {
               out.print('>');
            }

            // Find out if the end marker is indented
            indentEndMarker = isEndMarkerIndented(node);

            if (indentEndMarker)
            {
               out.print('\n');
            }

            NodeList childNodes = node.getChildNodes();
            int len = childNodes.getLength();
            for (int i = 0; i < len; i++)
            {
               Node childNode = childNodes.item(i);
               printInternal(childNode, false);
            }
            break;
         }

         // handle entity reference nodes
         case Node.ENTITY_REFERENCE_NODE:
         {
            if (canonical)
            {
               NodeList children = node.getChildNodes();
               if (children != null)
               {
                  int len = children.getLength();
                  for (int i = 0; i < len; i++)
                  {
                     printInternal(children.item(i), false);
                  }
               }
            }
            else
            {
               out.print('&');
               out.print(node.getNodeName());
               out.print(';');
            }
            break;
         }

         // print cdata sections
         case Node.CDATA_SECTION_NODE:
         {
            if (canonical)
            {
               out.print(normalize(node.getNodeValue()));
            }
            else
            {
               out.print("<![CDATA[");
               out.print(node.getNodeValue());
               out.print("]]>");
            }
            break;
         }

         // print text
         case Node.TEXT_NODE:
         {
            String text = normalize(node.getNodeValue());
            if (prettyprint == false || text.trim().length() > 0)
               out.print(text);
            break;
         }

         // print processing instruction
         case Node.PROCESSING_INSTRUCTION_NODE:
         {
            out.print("<?");
            out.print(node.getNodeName());
            String data = node.getNodeValue();
            if (data != null && data.length() > 0)
            {
               out.print(' ');
               out.print(data);
            }
            out.print("?>");
            break;
         }

         // print comment
         case Node.COMMENT_NODE:
         {
            for (int i = 0; i < prettyIndent; i++)
            {
               out.print(' ');
            }

            out.print("<!--");
            String data = node.getNodeValue();
            if (data != null)
            {
               out.print(data);
            }
            out.print("-->");

            if (prettyprint)
            {
               out.print('\n');
            }

            break;
         }
      }

      if (type == Node.ELEMENT_NODE)
      {
         if (prettyprint)
            prettyIndent--;

         if (hasChildNodes == false)
         {
            out.print("/>");
         }
         else
         {
            if (indentEndMarker)
            {
               for (int i = 0; i < prettyIndent; i++)
               {
                  out.print(' ');
               }
            }

            out.print("</");
            out.print(node.getNodeName());
            out.print('>');
         }

         if (prettyIndent > 0)
         {
            out.print('\n');
         }
      }

      out.flush();
   }

   private boolean isEndMarkerIndented(Node node)
   {
      if (prettyprint)
      {
         NodeList childNodes = node.getChildNodes();
         int len = childNodes.getLength();
         for (int i = 0; i < len; i++)
         {
            Node children = childNodes.item(i);
            if (children.getNodeType() == Node.ELEMENT_NODE)
            {
               return true;
            }
         }
      }
      return false;
   }

   /** Returns a sorted list of attributes. */
   private Attr[] sortAttributes(NamedNodeMap attrs)
   {

      int len = (attrs != null) ? attrs.getLength() : 0;
      Attr array[] = new Attr[len];
      for (int i = 0; i < len; i++)
      {
         array[i] = (Attr)attrs.item(i);
      }
      for (int i = 0; i < len - 1; i++)
      {
         String name = array[i].getNodeName();
         int index = i;
         for (int j = i + 1; j < len; j++)
         {
            String curName = array[j].getNodeName();
            if (curName.compareTo(name) < 0)
            {
               name = curName;
               index = j;
            }
         }
         if (index != i)
         {
            Attr temp = array[i];
            array[i] = array[index];
            array[index] = temp;
         }
      }

      return (array);
   }

   /** Normalizes the given string. */
   private String normalize(String s)
   {
      StringBuffer str = new StringBuffer();

      int len = (s != null) ? s.length() : 0;
      for (int i = 0; i < len; i++)
      {
         char ch = s.charAt(i);
         switch (ch)
         {
            case '<':
            {
               str.append("&lt;");
               break;
            }
            case '>':
            {
               str.append("&gt;");
               break;
            }
            case '&':
            {
               str.append("&amp;");
               break;
            }
            case '"':
            {
               str.append("&quot;");
               break;
            }
            case '\r':
            case '\n':
            {
               if (canonical)
               {
                  str.append("&#");
                  str.append(Integer.toString(ch));
                  str.append(';');
                  break;
               }
               // else, default append char
            }
            default:
            {
               str.append(ch);
            }
         }
      }

      return (str.toString());
   }
}