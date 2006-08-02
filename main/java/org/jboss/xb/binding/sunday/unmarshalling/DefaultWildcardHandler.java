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
package org.jboss.xb.binding.sunday.unmarshalling;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

import org.jboss.logging.Logger;
import org.xml.sax.Attributes;

/**
 * DefaultWildcardHandler.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision$
 */
public class DefaultWildcardHandler implements ParticleHandler
{
   /** The log */
   private static final Logger log = Logger.getLogger(DefaultWildcardHandler.class);

   public Object startParticle(Object parent, QName elementName, ParticleBinding particle, Attributes attrs,
         NamespaceContext nsCtx)
   {
      return parent;
   }

   public Object endParticle(Object o, QName elementName, ParticleBinding particle)
   {
      return o;
   }

   public void setParent(Object parent, Object o, QName elementName, ElementBinding element,
         ElementBinding parentElement)
   {
      if (log.isTraceEnabled())
         log.trace("Not setting " + o + " on " + parent + " for " + elementName);
   }

   public void setParent(Object parent, Object o, QName elementName, ParticleBinding particle,
         ParticleBinding parentParticle)
   {
      ElementBinding element = (ElementBinding) particle.getTerm();
      ElementBinding parentElement = (ElementBinding) parentParticle.getTerm();
      setParent(parent, o, elementName, element, parentElement);
   }
}
