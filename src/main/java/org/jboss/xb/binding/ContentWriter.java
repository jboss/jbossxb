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
package org.jboss.xb.binding;

import java.io.IOException;
import java.io.Writer;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * org.xml.sax.ContentHandler implementation that serializes an instance of org.jboss.xb.binding.Content
 * to a java.io.Writer.
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class ContentWriter
   implements ContentHandler
{
   final boolean useIndent;
   private String indent = "   ";
   private int depth = 0;
   private boolean started = false;

   private final Writer writer;

   public ContentWriter(Writer writer, boolean indent)
   {
      this.writer = writer;
      this.useIndent = indent;
   }

   public void setDocumentLocator(Locator locator)
   {
      throw new UnsupportedOperationException();
   }

   public void startDocument()
      throws SAXException
   {
   }

   public void endDocument()
      throws SAXException
   {
   }

   public void startPrefixMapping(String prefix, String uri)
      throws SAXException
   {
      throw new UnsupportedOperationException();
   }

   public void endPrefixMapping(String prefix)
      throws SAXException
   {
      throw new UnsupportedOperationException();
   }

   public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
      throws SAXException
   {
      if(useIndent)
      {
         write(writer, '\n');
         for(int j = 0; j < depth; ++j)
         {
            write(writer, indent);
         }
      }

      if(!started)
      {
         started = true;
      }

      ++depth;

      write(writer, '<');
      write(writer, qName);

      if(atts != null && atts.getLength() > 0)
      {
         for(int i = 0; i < atts.getLength(); ++i)
         {
            write(writer, ' ');
            write(writer, atts.getQName(i));
            write(writer, "=\"");
            writeNormalized(writer, atts.getValue(i));
            write(writer, '\"');
         }
      }

      /*if(namespaceURI != null && namespaceURI.length() > 1)
      {
         int colon = qName.indexOf(':');
         / *
         if(colon < 0)
         {
            throw new IllegalStateException(
               "Namespace URI specified (" + namespaceURI + ") but no qName found in qualified name '" + qName
            );
         }
         * /

         if(colon >= 0)
         {
            String prefix = qName.substring(0, colon);
            if(useIndent)
            {
               write(writer, '\n');
               for(int i = 0; i < depth + 1; ++i)
               {
                  write(writer, indent);
               }
            }
            else
            {
               write(writer, ' ');
            }

            write(writer, "xmlns:");
            write(writer, prefix);
            write(writer, "=\"");
            write(writer, namespaceURI);
            write(writer, "\"");
         }
      }*/

      write(writer, '>');
   }

   public void endElement(String namespaceURI, String localName,
                          String qName)
      throws SAXException
   {
      --depth;
      if(!started)
      {
         if(useIndent)
         {
            write(writer, '\n');
            for(int j = 0; j < depth; ++j)
            {
               write(writer, indent);
            }
         }
      }
      else
      {
         started = false;
      }

      write(writer, "</");
      write(writer, qName);
      write(writer, '>');
   }

   public void characters(char ch[], int start, int length)
      throws SAXException
   {
      writeNormalized(writer, ch, start, length);
   }

   public void ignorableWhitespace(char ch[], int start, int length)
      throws SAXException
   {
      throw new UnsupportedOperationException();
   }

   public void processingInstruction(String target, String data)
      throws SAXException
   {
      throw new UnsupportedOperationException();
   }

   public void skippedEntity(String name)
      throws SAXException
   {
      throw new UnsupportedOperationException();
   }

   // Private

   private static void write(Writer writer, String str) throws SAXException
   {
      try
      {
         writer.write(str);
      }
      catch(IOException e)
      {
         throw new SAXException("Writting failed: " + e.getMessage(), e);
      }
   }

   private static void writeNormalized(Writer writer, String str) throws SAXException
   {
      writeNormalized(writer, str.toCharArray(), 0, str.length());
   }

   private static void write(Writer writer, int ch) throws SAXException
   {
      try
      {
         writer.write(ch);
      }
      catch(IOException e)
      {
         throw new SAXException("Writting failed: " + e.getMessage(), e);
      }
   }

   private static void writeNormalized(Writer writer, char[] ch, int start, int length) throws SAXException
   {
      try
      {
         int left = start;
         int i = start;
         while(i < start + length)
         {
            char c = ch[i++];
            if(c == '<')
            {
               writer.write(ch, left, i - left - 1);
               writer.write("&lt;");
               left = i;
            }
            else if(c == '>')
            {
               writer.write(ch, left, i - left - 1);
               writer.write("&gt;");
               left = i;
            }
            else if(c == '&')
            {
               writer.write(ch, left, i - left - 1);
               writer.write("&amp;");
               left = i;
            }
            else if(c == '\'')
            {
               writer.write(ch, left, i - left - 1);
               writer.write("&apos;");
               left = i;
            }
            else if(c == '\"')
            {
               writer.write(ch, left, i - left - 1);
               writer.write("&quot;");
               left = i;
            }
         }

         if(left < i)
         {
            writer.write(ch, left, i - left);
         }
      }
      catch(IOException e)
      {
         throw new SAXException("Writting failed: " + e.getMessage(), e);
      }
   }
}
