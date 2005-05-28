/*
 * JBoss, the OpenSource webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling.impl.runtime;

import javax.xml.namespace.QName;
import org.jboss.xml.binding.sunday.unmarshalling.AttributeHandler;
import org.jboss.xml.binding.sunday.unmarshalling.AttributeBinding;
import org.jboss.xml.binding.Util;
import org.jboss.xml.binding.metadata.PropertyMetaData;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class RtAttributeHandler
   extends AttributeHandler
{
   public static final RtAttributeHandler INSTANCE = new RtAttributeHandler();

   public void attribute(QName elemName, QName attrName, AttributeBinding binding, Object owner, Object value)
   {
      String property = null;
      if(binding != null)
      {
         PropertyMetaData propertyMetaData = binding.getPropertyMetaData();
         if(propertyMetaData != null)
         {
            property = propertyMetaData.getName();
         }
      }

      if(property == null)
      {
         property = Util.xmlNameToFieldName(attrName.getLocalPart(), true);
      }

      RtUtil.set(owner, value, property, null, true);
   }
}
