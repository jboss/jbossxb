/*
  * JBoss, Home of Professional Open Source
  * Copyright 2005, JBoss Inc., and individual contributors as indicated
  * by the @authors tag. See the copyright.txt in the distribution for a
  * full listing of individual contributors.
  *
  * This is free software; you can redistribute it and/or modify it
  * under the terms of the GNU Lesser General Public License as
  * published by the Free Software Foundation; either version 2.1 of
  * the License, or (at your option) any later version.
  *
  * This software is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  * Lesser General Public License for more details.
  *
  * You should have received a copy of the GNU Lesser General Public
  * License along with this software; if not, write to the Free
  * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */
package org.jboss.xb.binding;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import org.jboss.xb.binding.parser.JBossXBParser;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public abstract class UnmarshallerFactory
{
   protected Map<String, Object> features;
   protected Boolean validation;
   protected Boolean namespaces;
   protected Boolean warnOnParserErrors;

   public static UnmarshallerFactory newInstance()
   {
      return new UnmarshallerFactoryImpl();
   }

   public abstract Unmarshaller newUnmarshaller();

   public void setFeature(String name, Object value)
   {
      Boolean bValue;
      if(value == null)
      {
         bValue = null;
      }
      else if(value instanceof String)
      {
         bValue = Boolean.valueOf((String)value);
      }
      else if(value instanceof Boolean)
      {
         bValue = (Boolean)value;
      }
      else
      {
         throw new JBossXBRuntimeException(
            "Allowed feature values are null, 'true, 'false', Boolean.TRUE, Boolean.FALSE. Passed in value: " + value
         );
      }

      if(Unmarshaller.VALIDATION.equals(name))
      {
         validation = bValue;
      }
      else if(Unmarshaller.NAMESPACES.equals(name))
      {
         namespaces = bValue;
      }
      else
      {
         if(features == null)
         {
            features = new HashMap<String, Object>();
         }
         features.put(name, value);
      }
   }

   /**
    * This property controls whether the (underlying) parser errors should be
    * logged as warnings or should they terminate parsing with errors.
    */
   public void setWarnOnParserErrors(boolean value)
   {
      this.warnOnParserErrors = value;
   }

   /**
    * This property controls whether the (underlying) parser errors should be
    * logged as warnings or should they terminate parsing with errors.
    * The default is to terminate parsing by re-throwing parser errors.
    * 
    * @return false if parser errors should be logged as warnings, otherwise - true
    */
   public boolean isWarnOnParserErrors()
   {
      return warnOnParserErrors == null ? false : warnOnParserErrors;
   }

   // Inner

   static class UnmarshallerFactoryImpl
      extends UnmarshallerFactory
   {
      public Unmarshaller newUnmarshaller()
      {
         UnmarshallerImpl unmarshaller;
         try
         {
            unmarshaller = new UnmarshallerImpl();
         }
         catch(JBossXBException e)
         {
            throw new JBossXBRuntimeException(e.getMessage(), e);
         }

         JBossXBParser parser = unmarshaller.getParser();
         if(validation != null)
            parser.setFeature(Unmarshaller.VALIDATION, validation.booleanValue());
         if(namespaces != null)
            parser.setFeature(Unmarshaller.NAMESPACES, namespaces.booleanValue());

         if(features != null)
         {
            for(Iterator<?> i = features.entrySet().iterator(); i.hasNext();)
            {
               Map.Entry<?, ?> entry = (Map.Entry<?, ?>)i.next();
               if(entry.getValue() != null)
               {
                  Boolean value = (Boolean)entry.getValue();
                  parser.setFeature((String)entry.getKey(), value.booleanValue());
               }
            }
         }

         //parser.setFeature(Unmarshaller.SCHEMA_VALIDATION, true);
         //parser.setFeature(Unmarshaller.SCHEMA_FULL_CHECKING, true);

         try
         {
            parser.setFeature(Unmarshaller.DYNAMIC_VALIDATION, true);
         }
         catch(JBossXBRuntimeException e)
         {
            // dynamic_validation is a required xerces-specific feature
         }

         return unmarshaller;
      }
   }
}
