/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

import java.io.Writer;
import java.io.IOException;

/**
 * org.xml.sax.ContentHandler implementation that serializes an instance of org.jboss.xml.binding.Content
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
            write(writer, atts.getValue(i));
            write(writer, '\"');
         }
      }

      if(namespaceURI != null && namespaceURI.length() > 1)
      {
         int colon = qName.indexOf(':');
         /*
         if(colon < 0)
         {
            throw new IllegalStateException(
               "Namespace URI specified (" + namespaceURI + ") but no qName found in qualified name '" + qName
            );
         }
         */

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
      }

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
      write(writer, ch, start, length);
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

   private static void write(Writer writer, char[] ch, int start, int length) throws SAXException
   {
      try
      {
         writer.write(ch, start, length);
      }
      catch(IOException e)
      {
         throw new SAXException("Writting failed: " + e.getMessage(), e);
      }
   }
}
