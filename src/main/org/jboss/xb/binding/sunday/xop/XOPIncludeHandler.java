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
package org.jboss.xb.binding.sunday.xop;

import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleHandler;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.DefaultHandlers;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.xml.sax.Attributes;

/**
 * Handler impl for xop:Include type.
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class XOPIncludeHandler
   implements ParticleHandler
{
   // type that can be XOP-optimized (should actually be the element)
   private final TypeBinding type;

   public XOPIncludeHandler(TypeBinding type)
   {
      this.type = type;
   }

   public Object startParticle(Object parent,
                               QName elementName,
                               ParticleBinding particle,
                               Attributes attrs,
                               NamespaceContext nsCtx)
   {
      ElementBinding xopInclude = (ElementBinding)particle.getTerm();
      if(!Constants.QNAME_XOP_INCLUDE.equals(xopInclude.getQName()))
      {
         throw new JBossXBRuntimeException(
            "Expected " + Constants.QNAME_XOP_INCLUDE + " but got " + xopInclude.getQName()
         );
      }

      XOPUnmarshaller xopUnmarshaller = type.getXopUnmarshaller();
      if(xopUnmarshaller == null)
      {
         throw new JBossXBRuntimeException(
            "Failed to process " + Constants.QNAME_XOP_INCLUDE + ": XOPUnmarshaller is not provided."
         );
      }

      String cid = attrs.getValue("href");
      if(cid == null)
      {
         throw new JBossXBRuntimeException(Constants.QNAME_XOP_INCLUDE + " doesn't contain required href attribute");
      }

      return xopUnmarshaller.getAttachmentAsByteArray(cid);
   }

   public Object endParticle(Object o, QName elementName, ParticleBinding particle)
   {
      return o;
   }

   public void setParent(Object parent,
                         Object o,
                         QName elementName,
                         ParticleBinding particle,
                         ParticleBinding parentParticle)
   {
      ElementBinding parentElement = (ElementBinding)parentParticle.getTerm();
      DefaultHandlers.ELEMENT_HANDLER.setParent(parent, o, parentElement.getQName(), particle, parentParticle);
   }
}
