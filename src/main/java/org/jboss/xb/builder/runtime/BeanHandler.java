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

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.jboss.xb.binding.sunday.unmarshalling.DefaultElementHandler;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ModelGroupBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TermBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ValueAdapter;
import org.jboss.xb.spi.BeanAdapter;
import org.jboss.xb.spi.BeanAdapterFactory;
import org.xml.sax.Attributes;

/**
 * BeanHandler.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class BeanHandler extends DefaultElementHandler
{
   /** The log */
   private final Logger log = Logger.getLogger(getClass());
   
   /** Whether trace is enabled */
   private boolean trace = log.isTraceEnabled();

   /** The bean name */
   private String name;
   
   /** The BeanAdapter */
   private BeanAdapterFactory beanAdapterFactory;
   
   /**
    * Create a new bean info element handler
    * 
    * @param name the bean name
    * @param beanAdapterFactory the bean adapterFactory
    * @throws IllegalArgumentException for a null parameter
    */
   public BeanHandler(String name, BeanAdapterFactory beanAdapterFactory)
   {
      if (name == null)
         throw new IllegalArgumentException("Null name");
      if (beanAdapterFactory == null)
         throw new IllegalArgumentException("Null bean adapter factory");
      this.name = name;
      this.beanAdapterFactory = beanAdapterFactory;
   }
   
   /**
    * Get the beanAdapterFactory.
    * 
    * @return the beanAdapterFactory.
    */
   public BeanAdapterFactory getBeanAdapterFactory()
   {
      return beanAdapterFactory;
   }
/*
   @Override
   public Object startElement(Object parent, QName qName, ElementBinding element)
   {
      if (trace)
         log.trace(" startElement " + qName + " bean=" + name + " parent=" + BuilderUtil.toDebugString(parent));
      try
      {
         return beanAdapterFactory.newInstance();
      }
      catch (Throwable t)
      {
         throw new RuntimeException("QName " + qName + " error invoking beanAdapterFactory.newInstance() for bean=" + name, t);
      }
   }
*/
   @Override
   public Object startParticle(Object parent,
         QName elementName,
         ParticleBinding particle,
         Attributes attrs,
         NamespaceContext nsCtx)
   {
      if (trace)
         log.trace(" startElement " + elementName + " bean=" + name + " parent=" + BuilderUtil.toDebugString(parent));
      
      Object o = null;
      try
      {
         o = beanAdapterFactory.newInstance();
      }
      catch (Throwable t)
      {
         throw new RuntimeException("QName " + elementName + " error invoking beanAdapterFactory.newInstance() for bean=" + name, t);
      }

      if (o != null && particle.getTerm().isElement())
      {
         ElementBinding element = (ElementBinding) particle.getTerm();
         attrs = element.getType().expandWithDefaultAttributes(attrs);
         attributes(o, elementName, element, attrs, nsCtx);
      }
      return o;
   }

   @Override
   public void setParent(Object parent, Object o, QName qName,  ParticleBinding particle, ParticleBinding parentParticle)
   {
      if (trace)
         log.trace("setParent " + qName + " parent=" + BuilderUtil.toDebugString(parent) + " child=" + BuilderUtil.toDebugString(o));

      TermBinding term = particle.getTerm();
      ValueAdapter valueAdapter = null;
      if(term.isModelGroup())
      {
         QName modelGroupName = ((ModelGroupBinding)term).getQName();
         if(modelGroupName != null)
         {
            qName = modelGroupName;
         }
      }
      else if(term.isElement())
      {
         valueAdapter = ((ElementBinding)term).getValueAdapter();
      }

      if (parent != null && parent instanceof ArrayWrapper)
      {
         ArrayWrapper wrapper = (ArrayWrapper) parent;
         wrapper.add(o);
         wrapper.setChildParticle(particle);
         wrapper.setParentParticle(parentParticle);
         return;
      }

      BeanAdapter beanAdapter;
      if (parent instanceof ArrayWrapper)
         beanAdapter = (BeanAdapter) ((ArrayWrapper) parent).getParent();
      else
         beanAdapter = (BeanAdapter) parent;
      AbstractPropertyHandler propertyHandler = beanAdapter.getPropertyHandler(qName);
      if (propertyHandler == null)
      {
         AbstractPropertyHandler wildcardHandler = beanAdapter.getWildcardHandler();
         if (wildcardHandler != null)
         {
            if (o != null && o instanceof ArrayWrapper)
            {
               ArrayWrapper wrapper = (ArrayWrapper) o;
               wildcardHandler.doHandle(beanAdapter, wrapper, wrapper.getElementName());
               return;
            }
         }
         TermBinding element = term;
         if (element.getSchema().isStrictSchema())
         {
            throw new RuntimeException("QName " + qName + " unknown property parent=" + BuilderUtil.toDebugString(parent) + " child=" + BuilderUtil.toDebugString(o) + " available=" + beanAdapter.getAvailable());
         }
         if (trace)
            log.trace("QName " + qName + " unknown property parent=" + BuilderUtil.toDebugString(parent) + " child=" + BuilderUtil.toDebugString(o));
         return;
      }

      if(particle.isRepeatable() && !(propertyHandler instanceof PropertyHandler) &&
            o != null && java.util.Collection.class.isAssignableFrom(o.getClass()))
      {
         // TODO this is not optimal!
         // repeatable particles are collected into java.util.Collection
         for (Object item : (java.util.Collection<?>) o)
         {
            if (valueAdapter != null)
            {
               item = valueAdapter.cast(item, null/*propertyHandler.getPropertyType().getType()*/);
            }
            propertyHandler.doHandle(beanAdapter, item, qName);
         }
      }
      else
      {
         // TODO looks like value adapter should be used earlier in the stack
         if(valueAdapter != null)
         {
            o = valueAdapter.cast(o, null/*propertyHandler.getPropertyType().getType()*/);
         }
         propertyHandler.doHandle(beanAdapter, o, qName);
      }
   }

/*   @Override
   public Object endElement(Object o, QName qName, ElementBinding element)
   {
      if (trace)
         log.trace("endElement " + qName + " o=" + BuilderUtil.toDebugString(o));
      BeanAdapter beanAdapter = (BeanAdapter) o;
      return beanAdapter.getValue();
   }
*/
   @Override
   public Object endParticle(Object o, QName qName, ParticleBinding particle)
   {
      if (trace)
         log.trace("endElement " + qName + " o=" + BuilderUtil.toDebugString(o));

      BeanAdapter beanAdapter = (BeanAdapter) o;
      return beanAdapter.getValue();
   }
}
