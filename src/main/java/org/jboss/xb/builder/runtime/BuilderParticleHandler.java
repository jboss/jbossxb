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

import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.sunday.unmarshalling.DefaultHandlers;
import org.jboss.xb.binding.sunday.unmarshalling.ModelGroupBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleHandler;
import org.jboss.xb.binding.sunday.unmarshalling.TermBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ValueAdapter;
import org.jboss.xb.spi.BeanAdapter;
import org.xml.sax.Attributes;

/**
 * BuilderParticleHandler.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class BuilderParticleHandler implements ParticleHandler
{
   /** The singleton instance */
   public static final BuilderParticleHandler INSTANCE = new BuilderParticleHandler();

   public Object startParticle(Object parent, QName elementName, ParticleBinding particle, Attributes attrs, NamespaceContext nsCtx)
   {
      return parent;
   }

   public void setParent(Object parent, Object o, QName elementName, ParticleBinding particle, ParticleBinding parentParticle)
   {
      if (particle.getTerm().isModelGroup() == false)
      {
         ParticleHandler particleHandler = null;
         TermBinding parentTerm = parentParticle.getTerm();
         if(!parentTerm.isSkip())
            particleHandler = parentTerm.getHandler();         
         if(particleHandler != null)
            particleHandler.setParent(parent, o, elementName, particle, parentParticle);
      }
   }

   public Object endParticle(Object o, QName elementName, ParticleBinding particle)
   {
      ValueAdapter valueAdapter = particle.getTerm().getValueAdapter();
      if(valueAdapter != null)
         o = valueAdapter.cast(o, null);
      return o;
   }
   
   public static ParticleHandler setParentDelegate(final ParticleHandler typeHandler)
   {
      return new ParticleHandler()
      {
         private final ParticleHandler delegate = typeHandler;
         
         public Object endParticle(Object o, QName elementName, ParticleBinding particle)
         {
            return o;
         }

         public void setParent(Object parent, Object o, QName elementName, ParticleBinding particle, ParticleBinding parentParticle)
         {
            if (particle.getTerm().isElement())
               delegate.setParent(parent, o, elementName, particle, parentParticle);
         }

         public Object startParticle(Object parent, QName elementName, ParticleBinding particle, Attributes attrs,
               NamespaceContext nsCtx)
         {
            return parent;
         }
      };
   }
   
   public static ParticleHandler parentGroup(final ModelGroupBinding group)
   {
      if(group.isSkip())
         return DefaultHandlers.UOE_PARTICLE_HANDLER;
      
      ParticleHandler handler = group.getHandler();
      if(handler == null)
         throw new JBossXBRuntimeException("The group is expected to have a non-null handler: " + group);
      return setParentDelegate(handler);
   }
}
