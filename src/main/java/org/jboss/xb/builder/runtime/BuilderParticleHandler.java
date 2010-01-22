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

import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ModelGroupBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleHandler;
import org.jboss.xb.binding.sunday.unmarshalling.TermBinding;
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
   
   public static final ParticleHandler PARENT_ELEMENT = new ParticleHandler()
   {
      public Object endParticle(Object o, QName elementName, ParticleBinding particle)
      {
         return o;
      }

      public void setParent(Object parent, Object o, QName elementName, ParticleBinding particle, ParticleBinding parentParticle)
      {
         if (particle.getTerm().isElement())
         {
            ParticleHandler particleHandler = ((ElementBinding)parentParticle.getTerm()).getType().getHandler();
            if(particleHandler != null)
               particleHandler.setParent(parent, o, elementName, particle, parentParticle);
         }
      }

      public Object startParticle(Object parent, QName elementName, ParticleBinding particle, Attributes attrs,
            NamespaceContext nsCtx)
      {
         return parent;
      }
   };

   public static final ParticleHandler PARENT_GROUP = new ParticleHandler()
   {
      public Object endParticle(Object o, QName elementName, ParticleBinding particle)
      {
         return o;
      }

      public void setParent(Object parent, Object o, QName elementName, ParticleBinding particle, ParticleBinding parentParticle)
      {
         if (particle.getTerm().isElement())
         {
            TermBinding parentTerm = parentParticle.getTerm();
            if (!parentTerm.isSkip())
            {
               ParticleHandler particleHandler = ((ModelGroupBinding)parentTerm).getHandler();
               if(particleHandler != null)
                  particleHandler.setParent(parent, o, elementName, particle, parentParticle);
            }
         }
      }

      public Object startParticle(Object parent, QName elementName, ParticleBinding particle, Attributes attrs,
            NamespaceContext nsCtx)
      {
         return parent;
      }
   };

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
         if(parentTerm.isElement())
         {
            particleHandler = ((ElementBinding)parentTerm).getType().getHandler();            
         }
         else if (!parentTerm.isSkip() && parentTerm.isModelGroup())
         {
            particleHandler = ((ModelGroupBinding)parentTerm).getHandler();
         }
         
         if(particleHandler != null)
            particleHandler.setParent(parent, o, elementName, particle, parentParticle);
      }
   }

   public Object endParticle(Object o, QName elementName, ParticleBinding particle)
   {
      return o;
   }
}
