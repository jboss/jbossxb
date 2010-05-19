/*
* JBoss, Home of Professional Open Source
* Copyright 2005, JBoss Inc., and individual contributors as indicated
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

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

import org.jboss.xb.binding.sunday.unmarshalling.CharactersHandler.UnmarshalCharactersHandler;
import org.jboss.xb.binding.sunday.unmarshalling.impl.runtime.RtAttributeHandler;
import org.jboss.xb.binding.sunday.unmarshalling.impl.runtime.RtCharactersHandler;
import org.jboss.xb.binding.sunday.unmarshalling.impl.runtime.RtElementHandler;
import org.jboss.xb.binding.sunday.xop.XOPElementHandler;
import org.xml.sax.Attributes;

/**
 * The DefaultHandlers.
 * 
 * @FIXME This just exposes the default handlers because
 *        they are hardwired to the RtHandlers without being
 *        overridable.
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision$
 */
public class DefaultHandlers
{
   public static ParticleHandler ELEMENT_HANDLER = RtElementHandler.INSTANCE;

   public static ParticleHandler MODEL_GROUP_HANDLER = RtElementHandler.INSTANCE;

   public static ParticleHandler SIMPLE_HANDLER = new RtElementHandler()
   {
      @Override
      public Object startParticle(Object parent, QName elementName, ParticleBinding particle, Attributes attrs, NamespaceContext nsCtx)
      {
         return null;
      }
   };

   public static AttributeHandler ATTRIBUTE_HANDLER = RtAttributeHandler.INSTANCE;

   public static CharactersHandlerFactory CHARACTERS_HANDLER_FACTORY = new CharactersHandlerFactory()
   {
      public CharactersHandler newCharactersHandler()
      {
         return RtCharactersHandler.INSTANCE;
      }

      public CharactersHandler newCharactersHandler(UnmarshalCharactersHandler unmarshalHandler)
      {
         return new RtCharactersHandler(unmarshalHandler);
      }
   };
   
   public static ParticleHandler XOP_HANDLER = new XOPElementHandler();
   
   public static ParticleHandler UOE_PARTICLE_HANDLER = new ParticleHandler()
   {
      public Object endParticle(Object o, QName elementName, ParticleBinding particle)
      {
         throw new UnsupportedOperationException();
      }

      public void setParent(Object parent, Object o, QName elementName, ParticleBinding particle,
            ParticleBinding parentParticle)
      {
         throw new UnsupportedOperationException();
      }

      public Object startParticle(Object parent, QName elementName, ParticleBinding particle, Attributes attrs,
            NamespaceContext nsCtx)
      {
         throw new UnsupportedOperationException();
      }
   };

   public static ParticleHandler NOOP_PARTICLE_HANDLER = new ParticleHandler()
   {
      public Object endParticle(Object o, QName elementName, ParticleBinding particle)
      {
         return o;
      }

      public void setParent(Object parent, Object o, QName elementName, ParticleBinding particle,
            ParticleBinding parentParticle)
      {
      }

      public Object startParticle(Object parent, QName elementName, ParticleBinding particle, Attributes attrs,
            NamespaceContext nsCtx)
      {
         return parent;
      }
   };

   public static RepeatableParticleHandler REPEATABLE_HANDLER = new RepeatableParticleHandler()
   {
      public Object startRepeatableParticle(Object parent, QName startName, ParticleBinding particle)
      {
         return null;
      }

      public void endRepeatableParticle(Object parent, Object o, QName elementName, ParticleBinding particle, ParticleBinding parentParticle)
      {
      }

      public void addTermValue(Object particleValue, Object termValue, QName elementName,
            ParticleBinding particle, ParticleBinding parentParticle, ParticleHandler handler)
      {
      }
   };
}
