/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.util.xml;

import java.io.PrintWriter;
import java.io.Writer;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A sample DOM writer. This sample program illustrates how to
 * traverse a DOM tree in order to print a document that is parsed.
 *
 * @version $Revision$
 */
public class DOMWriter
{
   /** Print writer. */
   protected PrintWriter out;

   /** Canonical output. */
   protected boolean canonical;

   // is pretty printer
   private boolean prettyprint;

   // indent for the pretty printer
   private int prettyIndent;

   public DOMWriter(Writer w)
   {
      this(w, false);
   }

   public DOMWriter(Writer w, boolean canonical)
   {
      out = new PrintWriter(w);
      this.canonical = canonical;
   }

   public void print(Node node)
   {
      printInternal(node, false);
   }

   public void print(Node node, boolean prettyprint)
   {
      this.prettyprint = prettyprint;
      printInternal(node, false);
   }

   private void printInternal(Node node, boolean hasChildren)
   {

      // is there anything to do?
      if (node == null)
      {
         return;
      }

      int type = node.getNodeType();
      switch (type)
      {
         // print document
         case Node.DOCUMENT_NODE:
         {
            if (!canonical)
            {
               out.println("<?xml version=\"1.0\"?>");
            }

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
               out.print('\n');
               for (int i = 0; i < prettyIndent; i++)
                  out.print(' ');
            }

            out.print('<');
            out.print(node.getNodeName());
            Attr attrs[] = sortAttributes(node.getAttributes());
            for (int i = 0; i < attrs.length; i++)
            {
               Attr attr = attrs[i];
               out.print(' ');
               out.print(attr.getNodeName());
               out.print("=\"");
               out.print(normalize(attr.getNodeValue()));
               out.print('"');
            }

            if (prettyprint)
               prettyIndent++;

            NodeList children = node.getChildNodes();

            int len = children.getLength();
            if (len > 0)
            {
               hasChildren = true;
               out.print('>');
            }

            for (int i = 0; i < len; i++)
            {
               Node childNode = children.item(i);
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
            out.print(normalize(node.getNodeValue()));
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
            if (prettyprint)
            {
               out.print('\n');
               for (int i = 0; i < prettyIndent; i++)
                  out.print(' ');
            }

            out.print("<!--");
            String data = node.getNodeValue();
            if (data != null)
            {
               out.print(data);
            }
            out.print("-->");
            break;
         }
      }

      if (type == Node.ELEMENT_NODE)
      {
         if (prettyprint)
            prettyIndent--;
         
         if (hasChildren == false)
         {
            out.print("/>");
         }
         else
         {
            if (prettyprint)
            {
               out.print('\n');
               for (int i = 0; i < prettyIndent; i++)
                  out.print(' ');
            }

            out.print("</");
            out.print(node.getNodeName());
            out.print('>');
         }
      }

      out.flush();
   } 

   /** Returns a sorted list of attributes. */
   protected Attr[] sortAttributes(NamedNodeMap attrs)
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

   } // sortAttributes(NamedNodeMap):Attr[]

   /** Normalizes the given string. */
   protected String normalize(String s)
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

   } // normalize(String):String
}