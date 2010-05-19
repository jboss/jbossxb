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
package org.jboss.xb.binding.sunday.unmarshalling;

import java.util.Collection;

import javax.xml.namespace.QName;

import org.jboss.reflect.spi.ClassInfo;
import org.jboss.reflect.spi.TypeInfo;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.builder.runtime.AbstractPropertyHandler;
import org.jboss.xb.util.CollectionFactory;

/**
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class CollectionRepeatableParticleHandler implements RepeatableParticleHandler
{
   private AbstractPropertyHandler propertyHandler;
   private CollectionFactory colFactory;
   private ValueAdapter valueAdapter;
   private TypeInfo componentType;
   
   public CollectionRepeatableParticleHandler(AbstractPropertyHandler propertyHandler, ClassInfo collectionType, ValueAdapter valueAdapter)
   {
      if(propertyHandler == null)
         throw new IllegalArgumentException("Null property handler.");
      colFactory = CollectionFactory.getFactory(collectionType);
      componentType = ((ClassInfo) collectionType).getComponentType();
      this.valueAdapter = valueAdapter;
      this.propertyHandler = propertyHandler;
   }

   public Object startRepeatableParticle(Object parent, QName startName, ParticleBinding particle)
   {
      try
      {
         return colFactory.createCollection();
      }
      catch (Throwable e)
      {
         throw new JBossXBRuntimeException("Failed to create collection for " + startName, e);
      }
   }

   public void endRepeatableParticle(Object parent, Object o, QName elementName, ParticleBinding particle, ParticleBinding parentParticle)
   {
      if(o == null)
         return;
      
      if(valueAdapter != null)
         o = valueAdapter.cast(o, null);
      
      propertyHandler.doHandle(parent, o, elementName);
   }

   public void addTermValue(Object particleValue, Object termValue, QName elementName,
         ParticleBinding particle, ParticleBinding parentParticle, ParticleHandler handler)
   {
      if (componentType != null && termValue != null)
      {
         if(!componentType.isInstance(termValue))
            throw new IllegalArgumentException("Child is not an instance of " + componentType + ", child: " + termValue);
      }
      ((Collection)particleValue).add(termValue);
   }
}