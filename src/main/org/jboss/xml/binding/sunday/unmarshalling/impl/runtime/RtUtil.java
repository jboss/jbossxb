/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling.impl.runtime;

import org.jboss.util.Classes;
import org.jboss.xml.binding.JBossXBRuntimeException;
import org.jboss.xml.binding.Immutable;
import org.jboss.xml.binding.Util;
import org.jboss.logging.Logger;

import javax.xml.namespace.QName;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.Collection;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class RtUtil
{
   private static final Logger log = Logger.getLogger(RtUtil.class);

   public static Object cast(Object o, Class cls)
   {
      Object result;
      if(cls == o.getClass() || cls.isAssignableFrom(o.getClass()))
      {
         result = o;
      }
      else
      {
         if(cls.isPrimitive())
         {
            cls = Classes.getPrimitiveWrapper(cls);
         }

         Constructor ctor;
         try
         {
            ctor = cls.getConstructor(new Class[]{o.getClass()});
         }
         catch(NoSuchMethodException e)
         {
            throw new JBossXBRuntimeException("Failed to cast value " +
               o +
               " to type " +
               cls +
               ": target type does not declare constructor that takes an argument of type " + o.getClass()
            );
         }

         try
         {
            result = ctor.newInstance(new Object[]{o});
         }
         catch(Exception e)
         {
            throw new JBossXBRuntimeException("Failed to cast value " + o + " to type " + cls + ": " + e.getMessage(),
               e
            );
         }
      }
      return result;
   }

   public static FieldSetter resolveSetter(Object owner, QName name)
   {
      FieldSetter setter;
      if(owner instanceof Collection)
      {
         setter = FieldSetter.COLLECTION_ITEM;
      }
      else if(owner instanceof Immutable)
      {
         Class ownerClass = ((Immutable)owner).cls;
         String methodBaseName = Util.xmlNameToClassName(name.getLocalPart(), true);
         Class fieldType;
         try
         {
            Method getMethod = ownerClass.getMethod("get" + methodBaseName, null);
            fieldType = getMethod.getReturnType();
         }
         catch(NoSuchMethodException e)
         {
            String fieldName = Util.xmlNameToFieldName(name.getLocalPart(), true);
            try
            {
               Field field = ownerClass.getField(fieldName);
               fieldType = field.getType();
            }
            catch(NoSuchFieldException e1)
            {
               throw new JBossXBRuntimeException("Neither getter nor field were found for " +
                  fieldName +
                  " in " +
                  ownerClass +
                  " for element " +
                  name
               );
            }
         }

         setter = new FieldSetter.ImmutableSetter(fieldType);
      }
      else
      {
         Class ownerClass = owner.getClass();

         String methodBaseName = Util.xmlNameToClassName(name.getLocalPart(), true);
         try
         {
            Method getMethod = ownerClass.getMethod("get" + methodBaseName, null);
            Method setMethod = ownerClass.getMethod("set" + methodBaseName, new Class[]{getMethod.getReturnType()});
            setter = new FieldSetter.MethodBasedSetter(setMethod);
         }
         catch(NoSuchMethodException e)
         {
            String fieldName = Util.xmlNameToFieldName(name.getLocalPart(), true);
            try
            {
               Field field = ownerClass.getField(fieldName);
               setter = new FieldSetter.FieldBasedSetter(field);
            }
            catch(NoSuchFieldException e1)
            {
               throw new JBossXBRuntimeException("Neither getter/setter nor field were found for " +
                  fieldName +
                  " in " +
                  ownerClass +
                  " for element " +
                  name
               );
            }
         }
      }

      return setter;
   }

   public static Class resolveClass(QName name, boolean required)
   {
      String className = Util.xmlNameToClassName(name.getNamespaceURI(), name.getLocalPart(), true);
      Class cls = null;
      try
      {
         cls = Thread.currentThread().getContextClassLoader().loadClass(className);
      }
      catch(ClassNotFoundException e)
      {
         if(required)
         {
            throw new JBossXBRuntimeException(
               "Failed to bind element " + name + " to class " + className + ": class not found."
            );
         }

         log.warn("Class for " + name + " not found: " + className);
      }
      return cls;
   }
}
