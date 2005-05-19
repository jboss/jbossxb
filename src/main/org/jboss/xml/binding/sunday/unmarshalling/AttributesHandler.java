/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling;

import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import org.xml.sax.Attributes;
import org.jboss.logging.Logger;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class AttributesHandler
{
   private static final Logger log = Logger.getLogger(AttributesHandler.class);

   public static final AttributesHandler INSTANCE = new AttributesHandler();

   public void attributes(Object o, QName elementName, TypeBinding type, Attributes attrs, NamespaceContext nsCtx)
   {
      for(int i = 0; i < attrs.getLength(); ++i)
      {
         QName qName = new QName(attrs.getURI(i), attrs.getLocalName(i));
         AttributeBinding binding = type.getAttribute(qName);
         if(binding != null)
         {
            AttributeHandler handler = binding.getHandler();
            Object value = handler.unmarshal(elementName, qName, binding, nsCtx, attrs.getValue(i));
            handler.attribute(elementName, qName, binding, o, value);
         }
         else
         {
            log.warn("Attribute is not bound: element owner " + elementName + ", attribute " + qName);
         }
      }
   }
}
