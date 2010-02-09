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

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.jboss.reflect.spi.ArrayInfo;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleHandler;
import org.jboss.xb.binding.sunday.unmarshalling.RepeatableParticleHandler;
import org.jboss.xb.binding.sunday.unmarshalling.ValueAdapter;

/**
 * A ArrayWrapperRepeatableParticleHandler.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class ArrayWrapperRepeatableParticleHandler implements RepeatableParticleHandler
{
   /** The log */
   protected static final Logger log = Logger.getLogger(ArrayWrapperRepeatableParticleHandler.class);
   
   /** Whether trace is enabled */
   protected boolean trace = log.isTraceEnabled();

   private final AbstractPropertyHandler setParentProperty;
   
   public ArrayWrapperRepeatableParticleHandler(AbstractPropertyHandler setParentProperty)
   {
      if(setParentProperty == null)
         throw new IllegalArgumentException("setParentProperty is null");
      this.setParentProperty = setParentProperty;
   }
   
   public void addTermValue(Object particleValue, Object termValue, QName elementName, ParticleBinding particle,
         ParticleBinding parentParticle, ParticleHandler handler)
   {
//      ValueAdapter valueAdapter = particle.getTerm().getValueAdapter();
//      if(valueAdapter != null)
//         termValue = valueAdapter.cast(termValue, null);
      ((List<Object>)particleValue).add(termValue);
      
      if(trace)
         log.trace("added " + elementName + " " + termValue);
   }

   public void endRepeatableParticle(Object parent, Object o, QName elementName, ParticleBinding particle,
         ParticleBinding parentParticle)
   {
      if(trace)
         log.trace("endRepeatableParticle " + elementName);
      
      ValueAdapter valueAdapter = particle.getTerm().getValueAdapter();
      if(valueAdapter != null)
         o = valueAdapter.cast(o, null);

      o = toArray((List<Object>) o, (ArrayInfo) setParentProperty.getPropertyType());
      setParentProperty.doHandle(parent, o, elementName);
   }

   public Object startRepeatableParticle(Object parent, QName startName, ParticleBinding particle)
   {
      if(trace)
         log.trace("startRepeatableParticle " + startName);
      return new ArrayList<Object>();
   }

   private Object[] toArray(List<Object> elements, ArrayInfo arrayInfo)
   {
      Object[] arr;
      try
      {
         arr = (Object[]) arrayInfo.newArrayInstance(elements.size());
         for (int i = 0; i < arr.length; ++i)
            arr[i] = elements.get(i);
         return arr;
      }
      catch (Throwable t)
      {
         throw new RuntimeException("Error creating array of type " + arrayInfo.getName() + " from " + elements, t);
      }
   }
}
