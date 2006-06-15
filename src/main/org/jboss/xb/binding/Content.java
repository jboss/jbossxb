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

import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.jboss.logging.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * An instance of this class represents XML content.
 * It is populated on unmarshalling with org.jboss.xb.binding.ContentPopulator and used
 * to implement content navigation in object model factories.
 * And on marshalling, first, an instance of this class is created and then it
 * is serialized into XML content with org.jboss.xb.binding.ContentWriter.
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class Content
{
   private static Logger log = Logger.getLogger(Content.class);

   private List content = new ArrayList();

   // Public

   public String getChildContent(String namespaceURI, String qName)
   {
      StartElement current = ((StartElement)content.get(index));

      boolean lookingForStart = true;
      StartElement start = null;
      boolean childFound = false;
      StringBuffer value = new StringBuffer();

      int i = index + 1;
      Object next = content.get(i++);
      while(!current.isMyEnd(next))
      {
         if(lookingForStart)
         {
            if(next instanceof StartElement)
            {
               start = (StartElement)next;
               lookingForStart = false;

               if(qName.equals(start.qName) &&
                  (namespaceURI == null ? start.namespaceURI == null : namespaceURI.equals(start.namespaceURI)))
               {
                  childFound = true;
               }
            }
         }
         else if(next instanceof EndElement)
         {
            if(start.isMyEnd(next))
            {
               if(childFound)
               {
                  break;
               }
               else
               {
                  lookingForStart = true;
               }
            }
         }
         else if(childFound && next instanceof Characters)
         {
            Characters chars = (Characters)next;
            value.append(chars.ch, chars.start, chars.length);
         }
         next = content.get(i++);
      }
      return value.toString().trim();
   }

   public void handleContent(ContentHandler handler) throws SAXException
   {
      handler.startDocument();

      for(Iterator i = content.iterator(); i.hasNext();)
      {
         Object item = i.next();
         if(item instanceof StartElement)
         {
            StartElement start = (StartElement)item;
            handler.startElement(start.namespaceURI, start.localName, start.qName, start.attrs);
         }
         else if(item instanceof EndElement)
         {
            EndElement end = (EndElement)item;
            handler.endElement(end.namespaceURI, end.localName, end.qName);
         }
         else if(item instanceof Characters)
         {
            Characters ch = (Characters)item;
            handler.characters(ch.ch, ch.start, ch.length);
         }
         else if(item instanceof StartPrefixMapping)
         {
/*
            if(log.isTraceEnabled())
            {
               StartPrefixMapping startPrefix = (StartPrefixMapping)item;
               log.trace("start prefix mapping: " + startPrefix.prefix + "=" + startPrefix.uri);
            }
*/
         }
         else if(item instanceof EndPrefixMapping)
         {
/*
            if(log.isTraceEnabled())
            {
               EndPrefixMapping endPrefix = (EndPrefixMapping)item;
               log.trace("end prefix mapping: " + endPrefix.prefix);
            }
*/
         }
         else
         {
            throw new IllegalStateException("Unexpected element type: " + item);
         }
      }

      handler.endDocument();
   }

   public String toString()
   {
      StringWriter writer = new StringWriter();
      try
      {
         ContentWriter contentWriter = new ContentWriter(writer, true);
         handleContent(contentWriter);
      }
      catch(SAXException e)
      {
         writer.write(e.getMessage());
      }
      return writer.getBuffer().toString();
   }

   // Methods that populate the content

   public void startDocument()
   {
      content.clear();
   }

   public void endDocument()
   {
   }

   public void startPrefixMapping(String prefix, String uri)
   {
      StartPrefixMapping node = new StartPrefixMapping(prefix, uri);
      content.add(node);
   }

   public void endPrefixMapping(String prefix)
   {
      EndPrefixMapping node = new EndPrefixMapping(prefix);
      content.add(node);
   }

   public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
   {
      StartElement startElement = new StartElement(namespaceURI, localName, qName, atts);
      content.add(startElement);

      if(log.isTraceEnabled())
      {
         log.trace("startElement> uri=" + namespaceURI + ", local=" + localName + ", qn=" + qName + ", attrs=" + atts);
      }
   }

   public void endElement(String namespaceURI, String localName, String qName)
   {
      EndElement endElement = new EndElement(namespaceURI, localName, qName);
      content.add(endElement);

      if(log.isTraceEnabled())
      {
         log.trace("endElement> uri=" + namespaceURI + ", local=" + localName + ", qn=" + qName);
      }
   }

   public void characters(char[] ch, int start, int length)
   {
      Characters characters = new Characters(ch, start, length);
      // ignore whitespace-only characters
      if(characters.toString().trim().length() > 0)
      {
         content.add(characters);

         if(log.isTraceEnabled())
         {
            log.trace("characters> " + characters);
         }
      }
   }

   // Methods that navigate through the content

   private int index;

   public void append(Content content)
   {
      for(Iterator i = content.content.iterator(); i.hasNext();)
      {
         this.content.add(i.next());
      }
   }

   // Inner

   private static interface Node
   {
   }

   public static class Characters
      implements Node
   {
      private final char[] ch;
      private final int start;
      private final int length;

      public Characters(char[] ch, int start, int length)
      {
         /*
         this.ch = ch;
         this.start = start;
         this.length = length;
         */
         this.ch = new char[length];
         System.arraycopy(ch, start, this.ch, 0, length);
         this.start = 0;
         this.length = length;
      }

      public String toString()
      {
         return String.valueOf(ch, start, length);
      }
   }

   public static class EndElement
      implements Node
   {
      private final String namespaceURI;
      private final String localName;
      private final String qName;

      public EndElement(String namespaceURI, String localName, String qName)
      {
         this.namespaceURI = namespaceURI;
         this.localName = localName;
         this.qName = qName;
      }

      public String toString()
      {
         return '[' + namespaceURI + ',' + localName + ',' + qName + ']';
      }
   }

   public static class StartElement
      implements Node
   {
      private final String namespaceURI;
      private final String localName;
      private final String qName;
      private final Attributes attrs;

      public StartElement(String namespaceURI, String localName, String qName, Attributes attrs)
      {
         this.namespaceURI = namespaceURI;
         this.localName = localName;
         this.qName = qName;
         this.attrs = new AttributesImpl(attrs);
      }

      public boolean isMyEnd(Object element)
      {
         boolean itis = false;
         if(element instanceof EndElement)
         {
            EndElement end = (EndElement)element;
            itis = (namespaceURI == null ? end.namespaceURI == null : namespaceURI.equals(end.namespaceURI))
               && qName.equals(end.qName);
         }
         return itis;
      }

      public String toString()
      {
         return '[' + namespaceURI + ',' + localName + ',' + qName + ']';
      }
   }

   public static class StartPrefixMapping
      implements Node
   {
      public final String prefix;
      public final String uri;

      public StartPrefixMapping(String prefix, String uri)
      {
         this.prefix = prefix;
         this.uri = uri;
      }

      public void read(ObjectModelBuilder builder)
      {
         builder.startPrefixMapping(prefix, uri);
      }

      public boolean equals(Object o)
      {
         if(this == o)
         {
            return true;
         }
         if(!(o instanceof StartPrefixMapping))
         {
            return false;
         }

         final StartPrefixMapping startPrefixMapping = (StartPrefixMapping)o;

         if(prefix != null ? !prefix.equals(startPrefixMapping.prefix) : startPrefixMapping.prefix != null)
         {
            return false;
         }
         if(uri != null ? !uri.equals(startPrefixMapping.uri) : startPrefixMapping.uri != null)
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         int result;
         result = (prefix != null ? prefix.hashCode() : 0);
         result = 29 * result + (uri != null ? uri.hashCode() : 0);
         return result;
      }
   }

   public static class EndPrefixMapping
      implements Node
   {
      public final String prefix;

      public EndPrefixMapping(String prefix)
      {
         this.prefix = prefix;
      }

      public void read(ObjectModelBuilder builder)
      {
         builder.endPrefixMapping(prefix);
      }
   }
}
