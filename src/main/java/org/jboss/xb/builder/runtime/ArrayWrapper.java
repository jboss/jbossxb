/*
* JBoss, Home of Professional Open Source
* Copyright 2006, JBoss Inc., and individual contributors as indicated
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

import javax.xml.namespace.QName;

import org.jboss.reflect.spi.ArrayInfo;
import org.jboss.reflect.spi.TypeInfo;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;

/**
 * ArrayWrapper.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class ArrayWrapper
{
   /** The parent */
   private Object parent;
   
   /** The element name */
   private QName elementName;
   
   /** The elements */
   private ArrayList<Object> elements = new ArrayList<Object>();
   
   /** The parent particle */
   private ParticleBinding parentParticle;
   
   /** The child particle */
   private ParticleBinding childParticle;
   
   /**
    * Create a new ArrayWrapper.
    * 
    * @param parent the parent
    * @param elementName the elementName
    */
   public ArrayWrapper(Object parent, QName elementName)
   {
      this.parent = parent;
      this.elementName = elementName;
   }

   /**
    * The elements 
    * 
    * @return the elements
    */
   public ArrayList<Object> getElements()
   {
      return elements;
   }

   /**
    * Get the parent
    * 
    * @return the parent
    */
   public Object getParent()
   {
      return parent;
   }
   
   /**
    * Get the elementName.
    * 
    * @return the elementName.
    */
   public QName getElementName()
   {
      return elementName;
   }

   /**
    * Add to the array
    * 
    * @param obj the object
    */
   public void add(Object obj)
   {
      elements.add(obj);
   }

   /**
    * Get the childParticle.
    * 
    * @return the childParticle.
    */
   public ParticleBinding getChildParticle()
   {
      return childParticle;
   }

   /**
    * Set the childParticle.
    * 
    * @param childParticle the childParticle.
    */
   public void setChildParticle(ParticleBinding childParticle)
   {
      this.childParticle = childParticle;
   }

   /**
    * Get the parentParticle.
    * 
    * @return the parentParticle.
    */
   public ParticleBinding getParentParticle()
   {
      return parentParticle;
   }

   /**
    * Set the parentParticle.
    * 
    * @param parentParticle the parentParticle.
    */
   public void setParentParticle(ParticleBinding parentParticle)
   {
      this.parentParticle = parentParticle;
   }
   
   /**
    * Get the elements 
    *
    * @param propertyType the property type
    * @return the array
    */
   public Object[] getArray(TypeInfo propertyType)
   {
      ArrayInfo arrayInfo = (ArrayInfo) propertyType;
      try
      {
         Object[] result = arrayInfo.newArrayInstance(elements.size());
         for (int i = 0; i < result.length; ++i)
            result[i] = elements.get(i);
         return result;
      }
      catch (Throwable t)
      {
         throw new RuntimeException("Error creating array of type " + arrayInfo.getName() + " from " + elements, t);
      }
   }
}
