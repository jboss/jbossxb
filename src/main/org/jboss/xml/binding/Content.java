/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.jboss.logging.Logger;

import java.util.LinkedList;
import java.io.StringWriter;

/**
 * An instance of this class represents XML content.
 * It is populated on unmarshalling with org.jboss.xml.binding.ContentPopulator and used
 * to implement content navigation in object model factories.
 * And on marshalling, first, an instance of this class is created and then it
 * is serialized into XML content with org.jboss.xml.binding.ContentWriter.
 *
 * @version <tt>$Revision$</tt>
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 */
public class Content
{
   private static Logger log = Logger.getLogger(Content.class);

   private LinkedList content = new LinkedList();

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

      for(int i = 0; i < content.size(); ++i)
      {
         Object item = content.get(i);
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
         ContentWriter contentWriter = new ContentWriter(writer);
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
      content.addLast(node);
   }

   public void endPrefixMapping(String prefix)
   {
      EndPrefixMapping node = new EndPrefixMapping(prefix);
      content.addLast(node);
   }

   public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
   {
      StartElement startElement = new StartElement(namespaceURI, localName, qName, atts);
      content.addLast(startElement);

      if(log.isTraceEnabled())
      {
         log.trace("startElement> uri=" + namespaceURI + ", local=" + localName + ", qn=" + qName + ", attrs=" + atts);
      }
   }

   public void endElement(String namespaceURI, String localName, String qName)
   {
      EndElement endElement = new EndElement(namespaceURI, localName, qName);
      content.addLast(endElement);

      if(log.isTraceEnabled())
      {
         log.trace("endElement> uri=" + namespaceURI + ", local=" + localName + ", qn=" + qName);
      }
   }

   public void characters(char[] ch, int start, int length)
   {
      Characters characters = new Characters(ch, start, length);
      // ignore whitespace-only characters
      if (characters.toString().trim().length() > 0)
      {
         content.addLast(characters);

         if(log.isTraceEnabled())
         {
            log.trace("characters> " + characters);
         }
      }
   }

   // Methods that navigate through the content

   private int index;

   public void build(ObjectModelBuilder builder)
   {
      for(index = 0; index < content.size(); ++index)
      {
         ((Node)content.get(index)).read(builder);
      }
   }

   // Inner

   private static interface Node
   {
      void read(ObjectModelBuilder builder);
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

      // Node implementation

      public void read(ObjectModelBuilder builder)
      {
         builder.characters(ch, start, length);
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

      // Node implementation

      public void read(ObjectModelBuilder builder)
      {
         builder.endElement(namespaceURI, localName, qName);
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

      // Node implementation

      public void read(ObjectModelBuilder builder)
      {
         log.info("read: qName=" + qName);
         builder.startElement(namespaceURI, localName, qName, attrs);
      }
   }

   public static class StartPrefixMapping implements Node
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
         if(this == o) return true;
         if(!(o instanceof StartPrefixMapping)) return false;

         final StartPrefixMapping startPrefixMapping = (StartPrefixMapping) o;

         if(prefix != null ? !prefix.equals(startPrefixMapping.prefix) : startPrefixMapping.prefix != null) return false;
         if(uri != null ? !uri.equals(startPrefixMapping.uri) : startPrefixMapping.uri != null) return false;

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

   public static class EndPrefixMapping implements Node
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