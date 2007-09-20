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

import java.util.Collection;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

import org.jboss.beans.info.spi.BeanInfo;
import org.jboss.reflect.spi.ClassInfo;
import org.jboss.reflect.spi.TypeInfo;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleHandler;
import org.jboss.xb.spi.BeanAdapter;
import org.xml.sax.Attributes;

/**
 * CollectionPropertyWildcardHandler.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class ChildCollectionWildcardHandler  implements ParticleHandler
{
   /** Singleton */
   public static ChildCollectionWildcardHandler SINGLETON = new ChildCollectionWildcardHandler();
   
   /** The wrapper type */
   private BeanInfo beanInfo;
   
   /** The wrapper property */
   private String property;

   /**
    * Create a new ChildCollectionWildcardHandler.
    */
   public ChildCollectionWildcardHandler()
   {
   }

   /**
    * Create a new ChildCollectionWildcardHandler.
    * 
    * @param beanInfo the wrapper class
    * @param property the wrapper property
    * @throws IllegalArgumentException for a null parameter
    */
   public ChildCollectionWildcardHandler(BeanInfo beanInfo, String property)
   {
      if (beanInfo == null)
         throw new IllegalArgumentException("Null beanInfo");
      if (property == null)
         throw new IllegalArgumentException("Null property");
      this.beanInfo = beanInfo;
      this.property = property;
   }
   
   public Object startParticle(Object parent, QName elementName, ParticleBinding particle, Attributes attrs, NamespaceContext nsCtx)
   {
      return parent;
   }

   @SuppressWarnings("unchecked")
   public void setParent(Object parent, Object o, QName elementName, ParticleBinding particle, ParticleBinding parentParticle)
   {
      BeanAdapter beanAdapter = (BeanAdapter) parent;
      Collection collection = (Collection) beanAdapter.getValue();
      
      if (beanInfo != null)
      {
         try
         {
            ClassInfo classInfo = beanInfo.getClassInfo();
            TypeInfo valueType = classInfo.getTypeInfoFactory().getTypeInfo(o.getClass());
            if (classInfo.isAssignableFrom(valueType) == false)
            {
               Object wrapper = beanInfo.newInstance();
               beanInfo.setProperty(wrapper, property, o);
               o = wrapper;
            }
         }
         catch (Throwable t)
         {
            throw new RuntimeException("Error wrapping object in " + beanInfo.getName()); 
         }
      }
      collection.add(o);
   }

   public Object endParticle(Object o, QName elementName, ParticleBinding particle)
   {
      return o;
   }
}
