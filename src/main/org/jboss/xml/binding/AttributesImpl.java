/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

import org.xml.sax.Attributes;

import java.util.List;
import java.util.ArrayList;

/**
 * org.xml.sax.Attributes implementation.
 *
 * @version <tt>$Revision$</tt>
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 */
public class AttributesImpl
   implements Attributes
{
   private final List attrList;

   public AttributesImpl(Attributes attrs)
   {
      this(attrs == null ? 0 : attrs.getLength());

      if(attrs != null)
      {
         for(int i = 0; i < attrs.getLength(); ++i)
         {
            add(
               attrs.getURI(i),
               attrs.getLocalName(i),
               attrs.getQName(i),
               attrs.getType(i),
               attrs.getValue(i)
            );
         }
      }
   }

   public AttributesImpl(int size)
   {
      this.attrList = new ArrayList(size);
   }

   public void add(String namespaceUri, String localName, String qName, String type, String value)
   {
      attrList.add(new AttributeImpl(namespaceUri, localName, qName, type, value));
   }

   public void addAll(Attributes attrs)
   {
      for(int i = 0; i < attrs.getLength(); ++i)
      {
         add(attrs.getURI(i), attrs.getLocalName(i), attrs.getQName(i), attrs.getType(i), attrs.getValue(i));
      }
   }

   // Attributes implementation

   public int getLength()
   {
      return attrList.size();
   }

   public String getURI(int index)
   {
      return getAttribute(index).namespaceUri;
   }

   public String getLocalName(int index)
   {
      return getAttribute(index).localName;
   }

   public String getQName(int index)
   {
      return getAttribute(index).qName;
   }

   public String getType(int index)
   {
      return getAttribute(index).type;
   }

   public String getValue(int index)
   {
      return getAttribute(index).value;
   }

   public int getIndex(String uri, String localName)
   {
      int i = 0;
      while(i < attrList.size())
      {
         final AttributeImpl attr = getAttribute(i++);
         if(
            (attr.namespaceUri == null ? uri == null : attr.namespaceUri.equals(uri)) &&
            (attr.localName == null ? localName == null : attr.localName.equals(localName))
         )
         {
            break;
         }
      }

      if (i == attrList.size())
         return -1;

      return i;
   }

   public int getIndex(String qName)
   {
      int i = 0;
      while(i < attrList.size())
      {
         final AttributeImpl attr = getAttribute(i++);
         if(attr.qName.equals(qName))
         {
            break;
         }
      }

      if (i == attrList.size())
         return -1;

      return i;
   }

   public String getType(String uri, String localName)
   {
      AttributeImpl attr = null;
      int i = 0;
      while(i < attrList.size())
      {
         attr = getAttribute(i++);
         if(
            (attr.namespaceUri == null ? uri == null : attr.namespaceUri.equals(uri)) &&
            (attr.localName == null ? localName == null : attr.localName.equals(localName))
         )
         {
            break;
         }
      }

      if (attr == null)
         return null;

      return attr.type;
   }

   public String getType(String qName)
   {
      AttributeImpl attr = null;
      int i = 0;
      while(i < attrList.size())
      {
         attr = getAttribute(i++);
         if(attr.qName.equals(qName))
         {
            break;
         }
      }

      if (attr == null)
         return null;

      return attr.type;
   }

   public String getValue(String uri, String localName)
   {
      AttributeImpl attr = null;
      int i = 0;
      while(i < attrList.size())
      {
         attr = getAttribute(i++);
         if(
            (attr.namespaceUri == null ? uri == null : attr.namespaceUri.equals(uri)) &&
            (attr.localName == null ? localName == null : attr.localName.equals(localName))
         )
         {
            break;
         }
      }

      if (attr == null)
         return null;

      return attr.value;
   }

   public String getValue(String qName)
   {
      AttributeImpl attr = null;
      int i = 0;
      while(i < attrList.size())
      {
         attr = getAttribute(i++);
         if(attr.qName.equals(qName))
         {
            break;
         }
      }

      if (attr == null)
         return null;

      return attr.value;
   }

   // Public

   public String toString()
   {
      String result;
      if(this.attrList.isEmpty())
      {
         result = "[]";
      }
      else
      {
         StringBuffer sb = new StringBuffer();
         sb.append('[');
         sb.append(getQName(0)).append('=').append(getValue(0));
         for(int i = 1; i < attrList.size(); ++i)
         {
            sb.append(", ").append(getQName(i)).append('=').append(getValue(i));
         }
         sb.append(']');
         result = sb.toString();
      }
      return result;
   }

   // Private

   private AttributeImpl getAttribute(int index)
   {
      return (AttributeImpl)attrList.get(index);
   }

   // Inner

   private static final class AttributeImpl
   {
      public final String namespaceUri;
      public final String localName;
      public final String qName;
      public final String type;
      public final String value;

      public AttributeImpl(String namespaceUri, String localName, String qName, String type, String value)
      {
         this.namespaceUri = namespaceUri;
         this.localName = localName;
         this.qName = qName;
         this.type = type;
         this.value = value;
      }

      public boolean equals(Object o)
      {
         if(this == o) return true;
         if(!(o instanceof AttributeImpl)) return false;

         final AttributeImpl attribute = (AttributeImpl)o;

         if(localName != null ? !localName.equals(attribute.localName) : attribute.localName != null) return false;
         if(namespaceUri != null ? !namespaceUri.equals(attribute.namespaceUri) : attribute.namespaceUri != null) return false;
         if(qName != null ? !qName.equals(attribute.qName) : attribute.qName != null) return false;
         if(type != null ? !type.equals(attribute.type) : attribute.type != null) return false;
         if(value != null ? !value.equals(attribute.value) : attribute.value != null) return false;

         return true;
      }

      public int hashCode()
      {
         int result;
         result = (namespaceUri != null ? namespaceUri.hashCode() : 0);
         result = 29 * result + (localName != null ? localName.hashCode() : 0);
         result = 29 * result + (qName != null ? qName.hashCode() : 0);
         result = 29 * result + (type != null ? type.hashCode() : 0);
         result = 29 * result + (value != null ? value.hashCode() : 0);
         return result;
      }
   }
}