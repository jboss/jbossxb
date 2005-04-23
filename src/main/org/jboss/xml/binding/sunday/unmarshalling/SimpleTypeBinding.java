/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling;

import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import org.jboss.xml.binding.SimpleTypeBindings;
import org.jboss.xml.binding.Constants;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface SimpleTypeBinding
{
   SimpleTypeBinding NOOP = new SimpleTypeBinding()
   {
      public Object unmarshal(QName qName, QName typeQName, NamespaceContext nsCtx, String text)
      {
         return text;
      }
   };

   SimpleTypeBinding DEFAULT = new SimpleTypeBinding()
   {
      public Object unmarshal(QName qName, QName typeQName, NamespaceContext nsCtx, String text)
      {
         Object o;
         if(typeQName != null && Constants.NS_XML_SCHEMA.equals(typeQName.getNamespaceURI()))
         {
            o = SimpleTypeBindings.unmarshal(typeQName.getLocalPart(), text, nsCtx);
         }
         else
         {
            o = text;
         }
         return o;
      }
   };

   Object unmarshal(QName qName, QName typeQName, NamespaceContext nsCtx, String text);
}
