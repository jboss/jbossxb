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
import org.jboss.xb.spi.BeanAdapterFactory;

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


   private final BeanAdapterFactory beanAdapterFactory;
   
   public ArrayWrapperRepeatableParticleHandler(BeanAdapterFactory beanAdapterFactory)
   {
      if(beanAdapterFactory == null)
         throw new IllegalArgumentException("beanAdapterFactory is null");
      this.beanAdapterFactory = beanAdapterFactory;
   }
   
   public void addTermValue(Object particleValue, Object termValue, QName elementName, ParticleBinding particle,
         ParticleBinding parentParticle, ParticleHandler handler)
   {
      ValueAdapter valueAdapter = particle.getTerm().getValueAdapter();
      if(valueAdapter != null)
         termValue = valueAdapter.cast(termValue, null);
      //ArrayWrapper aw = (ArrayWrapper) particleValue;
      //aw.add(termValue);
      ((List<Object>)particleValue).add(termValue);
      
      if(trace)
         log.trace("added " + elementName + " " + termValue);
   }

   public void endRepeatableParticle(Object parent, Object o, QName elementName, ParticleBinding particle,
         ParticleBinding parentParticle)
   {
      if(trace)
         log.trace("endRepeatableParticle " + elementName);

      QName qName = particle.getTerm().getQName();
      if(qName == null)
         qName = elementName;
      AbstractPropertyHandler propertyHandler = beanAdapterFactory.getPropertyHandler(qName);
      if (propertyHandler == null)
      {
         AbstractPropertyHandler wildcardHandler = beanAdapterFactory.getWildcardHandler();
         if (wildcardHandler != null && o != null)
         {
            o = toArray((List<Object>) o, (ArrayInfo) wildcardHandler.getPropertyType());
            wildcardHandler.doHandle(parent, o, qName);
            return;
         }

         if (particle.getTerm().getSchema().isStrictSchema())
            throw new RuntimeException("QName " + qName + " unknown property parent=" + BuilderUtil.toDebugString(parent) + " child=" + BuilderUtil.toDebugString(o) + " available=" + beanAdapterFactory.getAvailable());

         if (trace)
            log.trace("QName " + qName + " unknown property parent=" + BuilderUtil.toDebugString(parent) + " child=" + BuilderUtil.toDebugString(o));
         
         return;
      }

      o = toArray((List<Object>) o, (ArrayInfo) propertyHandler.getPropertyType());
      propertyHandler.doHandle(parent, o, qName);
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
