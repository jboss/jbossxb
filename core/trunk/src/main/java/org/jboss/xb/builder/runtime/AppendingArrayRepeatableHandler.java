/*
* JBoss, Home of Professional Open Source
* Copyright 2009, JBoss Inc., and individual contributors as indicated
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
package org.jboss.xb.builder.runtime;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.jboss.beans.info.spi.PropertyInfo;
import org.jboss.reflect.spi.ArrayInfo;
import org.jboss.reflect.spi.TypeInfo;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleHandler;
import org.jboss.xb.binding.sunday.unmarshalling.RepeatableParticleHandler;
import org.jboss.xb.binding.sunday.unmarshalling.ValueAdapter;
import org.jboss.xb.spi.BeanAdapter;

/**
 * This repeatable particle handler is used when repeatable particle handlers are actually disabled.
 * The reason is instead of creating, copying and setting a new array for every new element added,
 * it collects elements that appear one after another in XML and then appends to the current
 * property value.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class AppendingArrayRepeatableHandler implements RepeatableParticleHandler
{
   private final PropertyInfo propertyInfo;
   private final ArrayInfo arrayInfo;
   private final AbstractPropertyHandler targetHandler;
   
   public AppendingArrayRepeatableHandler(AbstractPropertyHandler propertyHandler)
   {
      this.propertyInfo = propertyHandler.getPropertyInfo();
      this.arrayInfo = (ArrayInfo) propertyHandler.getPropertyType();
      this.targetHandler = propertyHandler;
   }

   public void handle(PropertyInfo propertyInfo, TypeInfo propertyType, Object parent, Object child, QName name)
   {
   }

   public void addTermValue(Object particleValue, Object termValue, QName elementName, ParticleBinding particle,
         ParticleBinding parentParticle, ParticleHandler handler)
   {
      ((List<Object>)particleValue).add(termValue);
   }

   public void endRepeatableParticle(Object parent, Object o, QName name, ParticleBinding particle,
         ParticleBinding parentParticle)
   {
      ValueAdapter valueAdapter = particle.getTerm().getValueAdapter();
      if(valueAdapter != null)
         o = valueAdapter.cast(o, null);

      BeanAdapter beanAdapter = (BeanAdapter) parent;
      
      Object currentArray = null;
      try
      {
         if (propertyInfo.getGetter() != null)
            currentArray = beanAdapter.get(propertyInfo);
      }
      catch (Throwable t)
      {
         throw new RuntimeException("QName " + name + " error getting array property " + propertyInfo.getName() + " for " + BuilderUtil.toDebugString(parent), t);
      }

      List<Object> elements = (List<Object>) o;
      Object[] arr;
      if (currentArray == null)
      {
         try
         {
            arr = (Object[]) arrayInfo.newArrayInstance(elements.size());
         }
         catch (Throwable t)
         {
            throw new RuntimeException("Error creating array of type " + arrayInfo.getName() + " from " + elements, t);
         }

         for (int i = 0; i < arr.length; ++i)
            arr[i] = elements.get(i);
      }
      else
      {
         int currentLength = Array.getLength(currentArray);
         try
         {
            arr = (Object[]) arrayInfo.newArrayInstance(currentLength + elements.size());
         }
         catch (Throwable e)
         {
            throw new RuntimeException("Error creating array of type " + arrayInfo.getName() + " from " + elements, e);
         }
         
         System.arraycopy(currentArray, 0, arr, 0, currentLength);
         for (int i = 0; i < elements.size(); ++i)
            arr[currentLength + i] = elements.get(i);
      }
      
      targetHandler.doHandle(parent, arr, name);
   }

   public Object startRepeatableParticle(Object parent, QName startName, ParticleBinding particle)
   {
      return new ArrayList<Object>();
   }
}
