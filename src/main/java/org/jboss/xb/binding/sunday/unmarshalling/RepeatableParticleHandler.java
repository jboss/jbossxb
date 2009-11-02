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

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.namespace.QName;

import org.jboss.xb.binding.GenericValueContainer;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.group.ValueList;
import org.jboss.xb.binding.group.ValueListHandler;
import org.jboss.xb.binding.group.ValueListInitializer;
import org.jboss.xb.binding.group.ValueList.NonRequiredValue;

/**
 * A RepeatableParticleHandler.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public interface RepeatableParticleHandler
{
   RepeatableParticleHandler VALUE_LIST = new RepeatableParticleHandler()
   {
      @Override
      public Object startRepeatableParticle(Object parent, QName startName, ParticleBinding particle)
      {
         TermBinding term = particle.getTerm();
         if(term.isSkip())
         {
            return null;
         }
         
         if(parent != null &&
               !(parent instanceof GenericValueContainer) &&
               (parent instanceof Collection == false) &&
               term.getAddMethodMetaData() == null &&
               term.getMapEntryMetaData() == null &&
               term.getPutMethodMetaData() == null)
         {
            ValueListHandler handler = ValueListHandler.FACTORY.lazy(parent);
            Class<?> cls = parent.getClass();
            return new ValueListInitializer().newValueList(handler, cls);
         }
         return null;
      }      

      @Override
      public void endRepeatableParticle(Object parent, Object o, QName elementName, ParticleBinding particle, ParticleBinding parentParticle)
      {
         if (o == null)
            throw new IllegalArgumentException("Collection value is null for repeatable particle " + particle.getTerm());

         ValueList valueList = (ValueList) o;
         if (valueList.size() == 0)
            return;

         if (particle.getTerm().isWildcard())
         {
            ParticleHandler handler = null;

            // that's not good. some elements can be handled as "unresolved" and some as "resolved"
            QName qName = valueList.getValue(0).qName;
            Collection<Object> col = new ArrayList<Object>();
            for (int i = 0; i < valueList.size(); ++i)
            {
               NonRequiredValue value = valueList.getValue(i);
               col.add(value.value);

               if (handler != value.handler)
               {
                  if (handler == null && i == 0)
                  {
                     handler = (ParticleHandler) value.handler;
                  }
                  else
                  {
                     throw new JBossXBRuntimeException("Handlers in the list are supposed to be the same.");
                  }
               }
            }

            handler.setParent(parent, col, qName, particle, parentParticle);
         }
         else
         {
            valueList.getHandler().newInstance(particle, valueList);
         }
      }

      @Override
      public void addTermValue(Object parent, Object particleValue, Object termValue, QName elementName,
            ParticleBinding particle, ParticleBinding parentParticle, ParticleHandler handler)
      {
         if(parent == termValue)
            return;

         ValueList valueList = (ValueList)particleValue;
         valueList.getInitializer().addTermValue(elementName, particle, handler, valueList, termValue, parentParticle);
      }
   };

   RepeatableParticleHandler NOOP = new RepeatableParticleHandler()
   {
      @Override
      public Object startRepeatableParticle(Object parent, QName startName, ParticleBinding particle)
      {
         return null;
      }

      @Override
      public void endRepeatableParticle(Object parent, Object o, QName elementName, ParticleBinding particle, ParticleBinding parentParticle)
      {
      }

      @Override
      public void addTermValue(Object parent, Object particleValue, Object termValue, QName elementName,
            ParticleBinding particle, ParticleBinding parentParticle, ParticleHandler handler)
      {
      }
   };

   RepeatableParticleHandler LIST = new RepeatableParticleHandler()
   {
      @Override
      public Object startRepeatableParticle(Object parent, QName startName, ParticleBinding particle)
      {
         return new ArrayList<Object>();
      }

      @Override
      public void endRepeatableParticle(Object parent, Object o, QName elementName, ParticleBinding particle, ParticleBinding parentParticle)
      {
         if(o == null)
            return;
         
         ParticleHandler handler;
         TermBinding term = particle.getTerm();
         if(term.isElement())
         {
            handler = ((ElementBinding)term).getType().getHandler();
         }
         else if(term.isModelGroup())
         {
            handler = ((ModelGroupBinding)term).getHandler();
         }
         else
         {
            handler = ((WildcardBinding)term).getWildcardHandler();
         }
         
         handler.setParent(parent, o, elementName, particle, parentParticle);
      }

      @Override
      public void addTermValue(Object parent, Object particleValue, Object termValue, QName elementName,
            ParticleBinding particle, ParticleBinding parentParticle, ParticleHandler handler)
      {
         ((Collection<Object>)particleValue).add(termValue);
      }
   };

   Object startRepeatableParticle(Object parent, QName startName, ParticleBinding particle);
   
   void addTermValue(Object parent, Object particleValue, Object termValue, QName elementName, ParticleBinding particle, ParticleBinding parentParticle, ParticleHandler handler);
   
   void endRepeatableParticle(Object parent, Object o, QName elementName, ParticleBinding particle, ParticleBinding parentParticle);
}
