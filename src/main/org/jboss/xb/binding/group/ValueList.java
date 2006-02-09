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
package org.jboss.xb.binding.group;

import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleHandler;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class ValueList
{
   private final ValueListInitializer initializer;
   private final ValueListHandler handler;
   private final Class targetClass;
   //private final int requiredTotal;

   //private final Object[] requiredValues;
   //private int state;
   //private Map nonRequiredValues = Collections.EMPTY_MAP;
   //private List nonRequiredBindings;
   private List nonRequiredValues = Collections.EMPTY_LIST;

   ValueList(ValueListInitializer initializer, ValueListHandler handler, Class targetClass)
   {
      this.initializer = initializer;
      this.handler = handler;
      this.targetClass = targetClass;
      //this.requiredTotal = initializer.getRequiredBindings().size();
      //requiredValues = new Object[requiredTotal];
   }

   void setRequiredValue(int index, int stateIncrement, Object value)
   {
      throw new UnsupportedOperationException();
/*
      if(index >= requiredTotal)
      {
         throw new JBossXBRuntimeException(
            "Maximum argument index for this value list is " + requiredTotal + " but got " + index
         );
      }
      requiredValues[index] = value;
      state += stateIncrement;
*/
   }

   Object getRequiredValue(int index)
   {
/*
      if(index >= requiredTotal)
      {
         throw new JBossXBRuntimeException(
            "Maximum argument index for this value list is " + requiredTotal + " but got " + index
         );
      }
      return requiredValues[index];
*/
      throw new UnsupportedOperationException();
   }

   int getState()
   {
//      return state;
      throw new UnsupportedOperationException();
   }

   void setNonRequiredValue(QName qName, Object binding, Object handler, Object value)
   {
      NonRequiredValue val = new NonRequiredValue(qName, binding, handler, value);
      switch(nonRequiredValues.size())
      {
         case 0:
            nonRequiredValues = Collections.singletonList(val);
            break;
         case 1:
            nonRequiredValues = new ArrayList(nonRequiredValues);
         default:
            nonRequiredValues.add(val);
      }
/*
      switch(nonRequiredValues.size())
      {
         case 0:
            nonRequiredValues = Collections.singletonMap(qName, value);
            nonRequiredBindings = new ArrayList();
            nonRequiredBindings.add(binding);
            break;
         case 1:
            nonRequiredValues = new LinkedHashMap(nonRequiredValues);
         default:
            nonRequiredValues.put(qName, value);
            nonRequiredBindings.add(binding);
      }
*/
   }

   Object getNonRequiredValue(QName qName)
   {
//      return nonRequiredValues.get(qName);
      throw new UnsupportedOperationException();
   }

   public ValueListInitializer getInitializer()
   {
      return initializer;
   }

   public List getRequiredValues()
   {
//      return Arrays.asList(requiredValues);
      throw new UnsupportedOperationException();
   }

   public Map getNonRequiredValues()
   {
//      return nonRequiredValues;
      throw new UnsupportedOperationException();
   }

   public List getNonRequiredBindings()
   {
//      return nonRequiredBindings;
      throw new UnsupportedOperationException();
   }

   public ValueListHandler getHandler()
   {
      return handler;
   }

   public Class getTargetClass()
   {
      return targetClass;
   }

   public NonRequiredValue getValue(int i)
   {
      return (NonRequiredValue)nonRequiredValues.get(i);
   }

   public int size()
   {
      return nonRequiredValues.size();
   }

   public static final class NonRequiredValue
   {
      public final QName qName;
      public final Object binding;
      public final Object handler;
      public final Object value;

      public NonRequiredValue(QName qName, Object binding, Object handler, Object value)
      {
         this.qName = qName;
         this.binding = binding;
         this.handler = handler;
         this.value = value;
      }
   }
}
