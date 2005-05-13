/*
 * JBoss, the OpenSource webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling;

import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import org.xml.sax.Attributes;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class DefaultElementHandler
   implements ElementHandler
{
   public static final DefaultElementHandler INSTANCE = new DefaultElementHandler();

   private AttributesHandler attrsHandler;

   public DefaultElementHandler()
   {
      this(AttributesHandler.INSTANCE);
   }

   public DefaultElementHandler(AttributesHandler attrsHandler)
   {
      this.attrsHandler = attrsHandler;
   }

   public Object startElement(Object parent, QName qName, TypeBinding type)
   {
      return parent;
   }

   public void attributes(Object o, QName elementName, TypeBinding type, Attributes attrs, NamespaceContext nsCtx)
   {
      if(attrsHandler != null)
      {
         attrsHandler.attributes(o, elementName, type, attrs, nsCtx);
      }
   }

   public Object endElement(Object o, QName qName, TypeBinding type)
   {
      return o;
   }

   public void setParent(Object parent, Object o, QName qName, ElementBinding element)
   {
   }

   protected void setData(Object o, QName elementName, TypeBinding type, Object data)
   {
   }
}
