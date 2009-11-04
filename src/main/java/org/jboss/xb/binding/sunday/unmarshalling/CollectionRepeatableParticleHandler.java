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

/**
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class CollectionRepeatableParticleHandler implements RepeatableParticleHandler
{
   public static final CollectionRepeatableParticleHandler INSTANCE = new CollectionRepeatableParticleHandler();
   
   public Object startRepeatableParticle(Object parent, QName startName, ParticleBinding particle)
   {
      return createCollection();
   }

   public void endRepeatableParticle(Object parent, Object o, QName elementName, ParticleBinding particle, ParticleBinding parentParticle)
   {
      if(o == null)
         return;
      
      ParticleHandler handler;
      TermBinding term = particle.getTerm();
      if(term.isElement())
         handler = ((ElementBinding)term).getType().getHandler();
      else if(term.isModelGroup())
         handler = ((ModelGroupBinding)term).getHandler();
      else
         handler = ((WildcardBinding)term).getWildcardHandler();

      if(handler == null)
         handler = DefaultHandlers.ELEMENT_HANDLER;
      handler.setParent(parent, o, elementName, particle, parentParticle);
   }

   public void addTermValue(Object particleValue, Object termValue, QName elementName,
         ParticleBinding particle, ParticleBinding parentParticle, ParticleHandler handler)
   {
      ((Collection<Object>)particleValue).add(termValue);
   }
   
   protected Collection<Object> createCollection()
   {
      return new ArrayList<Object>();
   }
}