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

import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.sunday.unmarshalling.AttributeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleHandler;
import org.jboss.xb.binding.sunday.unmarshalling.CharactersHandler;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class ValueListInitializer
{
   private int initializedState;
   private Map attrIndex = Collections.EMPTY_MAP;
   private Map elemIndex = Collections.EMPTY_MAP;
   private List requiredBindings = Collections.EMPTY_LIST;

   public void addRequiredAttribute(QName qName, AttributeBinding binding)
   {
/*
      Integer index = new Integer(requiredBindings.size());
      switch(attrIndex.size())
      {
         case 0:
            attrIndex = Collections.singletonMap(qName, index);
            break;
         case 1:
            attrIndex = new HashMap(attrIndex);
         default:
            attrIndex.put(qName, index);
      }
      addBinding(binding);
      initializedState += Math.abs(qName.hashCode());
*/
      throw new UnsupportedOperationException();
   }

   public void addRequiredElement(QName qName, ElementBinding binding)
   {
/*
      Integer index = new Integer(requiredBindings.size());
      switch(elemIndex.size())
      {
         case 0:
            elemIndex = Collections.singletonMap(qName, index);
            break;
         case 1:
            elemIndex = new HashMap(elemIndex);
         default:
            elemIndex.put(qName, index);
      }
      addBinding(binding);
      initializedState += Math.abs(qName.hashCode());
*/
      throw new UnsupportedOperationException();
   }

   public ValueList newValueList(ValueListHandler handler, Class targetClass)
   {
      return new ValueList(this, handler, targetClass);
   }

   public void addAttributeValue(QName qName, AttributeBinding binding, ValueList valueList, Object value)
   {
      Integer index = (Integer)attrIndex.get(qName);
      if(index == null)
      {
         valueList.setAttributeValue(qName, binding, value);
      }
      else
      {
         if(isInitialized(valueList))
         {
            throw new JBossXBRuntimeException("The value list has already been initialized!");
         }
         valueList.setRequiredValue(index.intValue(), qName.hashCode(), value);
      }
   }

   public void addTextValue(QName qName,
                            ParticleBinding particle,
                            CharactersHandler handler,
                            ValueList valueList,
                            Object value)
   {
      valueList.addTextValue(qName, particle, handler, value);
   }

   public void addTermValue(QName qName, ParticleBinding binding, Object handler, ValueList valueList, Object value)
   {
      Integer index = (Integer)elemIndex.get(qName);
      if(index == null)
      {
         valueList.addTermValue(qName, binding, handler, value);
      }
      else
      {
         if(isInitialized(valueList))
         {
            throw new JBossXBRuntimeException("The value list has already been initialized!");
         }
         valueList.setRequiredValue(index.intValue(), qName.hashCode(), value);
      }
   }

   public boolean isInitialized(ValueList valueList)
   {
//      return requiredBindings.size() == 0 || initializedState == valueList.getState();
      throw new UnsupportedOperationException();
   }

   public Object getAttributeValue(QName qName, ValueList valueList)
   {
/*
      Object value;
      Integer index = (Integer)attrIndex.get(qName);
      if(index == null)
      {
         value = valueList.getNonRequiredValue(qName);
      }
      else
      {
         value = valueList.getRequiredValue(index.intValue());
      }
      return value;
*/
      throw new UnsupportedOperationException();
   }

   public Object getElementValue(QName qName, ValueList valueList)
   {
/*
      Object value;
      Integer index = (Integer)elemIndex.get(qName);
      if(index == null)
      {
         value = valueList.getNonRequiredValue(qName);
      }
      else
      {
         value = valueList.getRequiredValue(index.intValue());
      }
      return value;
*/
      throw new UnsupportedOperationException();
   }

   public List getRequiredBindings()
   {
//      return requiredBindings;
      throw new UnsupportedOperationException();
   }

   // Private

   private void addBinding(Object binding)
   {
      if(requiredBindings == Collections.EMPTY_LIST)
      {
         requiredBindings = new ArrayList();
      }
      requiredBindings.add(binding);
   }
}
