/*
 * JBoss, the OpenSource webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling.impl.runtime;

import java.lang.reflect.Method;
import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import org.jboss.xml.binding.sunday.unmarshalling.CharactersHandler;
import org.jboss.xml.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xml.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xml.binding.metadata.ValueMetaData;
import org.jboss.xml.binding.metadata.PropertyMetaData;
import org.jboss.xml.binding.Util;
import org.jboss.xml.binding.JBossXBRuntimeException;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class RtCharactersHandler
   extends CharactersHandler
{
   public static final RtCharactersHandler INSTANCE = new RtCharactersHandler();

   public Object unmarshal(QName qName,
                           TypeBinding typeBinding,
                           NamespaceContext nsCtx,
                           ValueMetaData valueMetaData,
                           String value)
   {
      Object unmarshalled = null;
      if(valueMetaData != null)
      {
         String parseMethod = valueMetaData.getUnmarshalMethod();
         if(parseMethod == null)
         {
            throw new JBossXBRuntimeException(
               "javaType annotation is specified for " + qName + " but does not contain parseMethod attribute"
            );
         }

         int lastDot = parseMethod.lastIndexOf('.');
         String clsName = parseMethod.substring(0, lastDot);
         Class cls;
         try
         {
            cls = Thread.currentThread().getContextClassLoader().loadClass(clsName);
         }
         catch(ClassNotFoundException e)
         {
            throw new JBossXBRuntimeException("Failed to load class " + clsName + " for parseMethod " + parseMethod);
         }

         String methodName = parseMethod.substring(lastDot + 1);
         Method method;
         try
         {
            method = cls.getMethod(methodName, new Class[]{String.class, NamespaceContext.class});
         }
         catch(NoSuchMethodException e)
         {
            throw new JBossXBRuntimeException("Failed to find method " +
               methodName +
               "(" +
               String.class.getName() +
               " p1, " +
               NamespaceContext.class.getName() +
               " p2) in " +
               cls
            );
         }

         try
         {
            unmarshalled = method.invoke(null, new Object[]{value, nsCtx});
         }
         catch(Exception e)
         {
            throw new JBossXBRuntimeException("Failed to invoke parseMethod " +
               parseMethod +
               " for element " +
               qName +
               " and value " +
               value +
               ": " +
               e.getMessage(),
               e
            );
         }
      }
      else
      {
         unmarshalled = super.unmarshal(qName, typeBinding, nsCtx, valueMetaData, value);
      }

      return unmarshalled;
   }

   public void setValue(QName qName, ElementBinding element, Object owner, Object value)
   {
      if(owner != null)
      {
         String propName = null;
         String colType = null;
         TypeBinding type = element.getType();
         if(type != null && !type.isSimple()/* && type.hasSimpleContent()*/)
         {
            PropertyMetaData propertyMetaData = type.getPropertyMetaData();
            if(propertyMetaData != null)
            {
               propName = propertyMetaData.getName();
               colType = propertyMetaData.getCollectionType();
            }

            if(propName == null)
            {
               propName = "value";
            }
         }
         else
         {
            PropertyMetaData PropertyMetaData = element.getPropertyMetaData();
            if(PropertyMetaData != null)
            {
               propName = PropertyMetaData.getName();
               colType = PropertyMetaData.getCollectionType();
            }

            if(propName == null)
            {
               propName = Util.xmlNameToFieldName(qName.getLocalPart(), true);
            }
         }

         RtUtil.set(owner, value, propName, colType, true);
      }
   }
}