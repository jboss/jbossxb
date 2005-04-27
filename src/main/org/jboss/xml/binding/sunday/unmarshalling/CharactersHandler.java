/*
 * JBoss, the OpenSource webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling;

import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import org.jboss.xml.binding.Constants;
import org.jboss.xml.binding.SimpleTypeBindings;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public abstract class CharactersHandler
{
   public static final CharactersHandler NOOP = new CharactersHandler()
   {
      public Object unmarshal(QName qName, QName typeQName, NamespaceContext nsCtx, String value)
      {
         return value;
      }
   };

   public static final CharactersHandler DEFAULT = new CharactersHandler()
   {
   };

   public Object unmarshal(QName qName, QName typeQName, NamespaceContext nsCtx, String value)
   {
      Object o;
      if(typeQName != null && Constants.NS_XML_SCHEMA.equals(typeQName.getNamespaceURI()))
      {
         o = SimpleTypeBindings.unmarshal(typeQName.getLocalPart(), value, nsCtx);
      }
      else
      {
         o = value;
      }
      return o;
   }

   public void setValue(QName qName, Object owner, Object value)
   {
   }
}
