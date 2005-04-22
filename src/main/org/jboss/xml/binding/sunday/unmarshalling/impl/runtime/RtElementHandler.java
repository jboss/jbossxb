/*
 * JBoss, the OpenSource webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling.impl.runtime;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import javax.xml.namespace.QName;
import org.jboss.xml.binding.sunday.unmarshalling.ElementHandler;
import org.jboss.xml.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xml.binding.sunday.unmarshalling.AttributeBinding;
import org.jboss.xml.binding.sunday.unmarshalling.AttributeHandler;
import org.jboss.xml.binding.sunday.unmarshalling.SimpleTypeBinding;
import org.jboss.xml.binding.Util;
import org.jboss.xml.binding.JBossXBRuntimeException;
import org.xml.sax.Attributes;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class RtElementHandler
   implements ElementHandler
{
   public Object startElement(Object parent, QName elementName, TypeBinding type)
   {
      Object o = null;
      if(!type.isSimple())
      {
         QName qName = type.getQName();
         if(qName == null)
         {
            qName = type.getQName();
         }

         String className = Util.xmlNameToClassName(qName.getNamespaceURI(), qName.getLocalPart(), true);
         Class cls;
         try
         {
            cls = Thread.currentThread().getContextClassLoader().loadClass(className);
         }
         catch(ClassNotFoundException e)
         {
            throw new JBossXBRuntimeException("Failed to resolve class name for " +
               elementName +
               " of type " +
               type.getQName() +
               ": " +
               e.getMessage()
            );
         }

         try
         {
            Constructor ctor = cls.getConstructor(null);

            try
            {
               o = ctor.newInstance(null);
            }
            catch(Exception e)
            {
               throw new JBossXBRuntimeException("Failed to create an instance of " +
                  cls +
                  " using default constructor for element " +
                  elementName +
                  " of type " +
                  type.getQName()
               );
            }
         }
         catch(NoSuchMethodException e)
         {
            throw new JBossXBRuntimeException(
               "" + cls + " doesn't declare no-arg constructor: element=" + elementName + ", type=" + type.getQName()
            );
         }
      }
      return o;
   }

   public void attributes(Object o, QName elementName, TypeBinding type, Attributes attrs)
   {
      for(int i = 0; i < attrs.getLength(); ++i)
      {
         QName attrName = new QName(attrs.getURI(i), attrs.getLocalName(i));
         AttributeBinding binding = type.getAttribute(attrName);
         if(binding != null)
         {
            AttributeHandler handler = binding.getHandler();
            if(handler != null)
            {
               Object value = handler.unmarshal(elementName, attrName, binding.getType(), attrs.getValue(i));
               handler.attribute(elementName, attrName, o, value);
            }
            else
            {
               throw new JBossXBRuntimeException(
                  "Attribute binding present but has no handler: element=" + elementName + ", attrinute=" + attrName
               );
            }
         }
         else
         {
            set(o, attrName, type.getSimpleType(), attrs.getValue(i));
         }
      }
   }

   public void characters(Object o, QName elementName, TypeBinding type, String text)
   {
      set(o, elementName, type.getSimpleType(), text);
   }

   public Object endElement(Object o, QName elementName, TypeBinding type)
   {
      // todo: immutables
      return o;
   }

   public void setParent(Object parent, Object o, QName qName)
   {
   }

   // Private

   private static void set(Object o, QName elementName, SimpleTypeBinding type, String text)
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

      Object value = type == null ? text : type.unmarshal(elementName, text);
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
            " for element (or attribute) " + elementName + " : " +
            e.getMessage(),
            e
         );
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
