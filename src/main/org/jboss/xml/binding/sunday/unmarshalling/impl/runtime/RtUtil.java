/*
 * JBoss, the OpenSource webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling.impl.runtime;

import java.util.Collection;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import javax.xml.namespace.QName;
import org.jboss.xml.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xml.binding.Util;
import org.jboss.xml.binding.JBossXBRuntimeException;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class RtUtil
{
   private RtUtil()
   {
   }

   public static void set(Object o, QName elementName, TypeBinding type, Object value)
   {
      if(o instanceof Collection)
      {
         ((Collection)o).add(value);
      }
      else
      {
         Class cls = o.getClass();
         String methodBase = Util.xmlNameToClassName(elementName.getLocalPart(), true);
         Method setter = null;
         Field field = null;
         try
         {
            Method getter = cls.getMethod("get" + methodBase, null);
            setter = cls.getMethod("set" + methodBase, new Class[]{getter.getReturnType()});
         }
         catch(NoSuchMethodException e)
         {
            try
            {
               field = cls.getField(Util.xmlNameToFieldName(elementName.getLocalPart(), true));
            }
            catch(NoSuchFieldException e1)
            {
               throw new JBossXBRuntimeException(
                  "Neither getter/setter nor field were found for " + elementName + " in " + cls
               );
            }
         }

         try
         {
            set(o, value, setter, field);
         }
         catch(Exception e)
         {
            throw new JBossXBRuntimeException("Failed to set value " +
               (value == null ? "null" : value.getClass().getName() + '@' + value.hashCode() + "[" + value + "]")
               +
               (field == null ?
               (setter == null ? "" : " using setter " + setter.getName()) :
               " using field " + field.getName()
               ) +
               " on " +
               (o == null ? "null" : o.getClass().getName() + '@' + o.hashCode() + "[" + o + "]") +
               " for element (or attribute) " +
               elementName +
               " : " +
               e.getMessage(),
               e
            );
         }
      }
   }

   private static void set(Object o, Object value, Method setter, Field field)
      throws IllegalAccessException, InvocationTargetException
   {
      if(setter != null)
      {
         setter.invoke(o, new Object[]{value});
      }
      else if(field != null)
      {
         field.set(o, value);
      }
      else
      {
         throw new JBossXBRuntimeException("Neither setter nor field is available!");
      }
   }
}
