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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import javax.activation.DataHandler;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.sunday.unmarshalling.DefaultHandlers;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleHandler;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
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

      DataHandler dataHandler = xopUnmarshaller.getAttachmentAsDataHandler(cid);
      if(dataHandler == null)
      {
         throw new JBossXBRuntimeException("DataHandler is not available for cid '" + cid + "'");
      }

      Object content = null;
      try
      {
         content = dataHandler.getContent();
      }
      catch(IOException e)
      {
         throw new JBossXBRuntimeException("Failed to get content from DataHandler: " + e.getMessage(), e);
      }

      if(content instanceof InputStream)
      {
         try
         {
            ObjectInputStream ois = new ObjectInputStream((InputStream)content);
            content = ois.readObject();
         }
         catch(IOException e)
         {
            throw new JBossXBRuntimeException("Failed to deserialize object: " + e.getMessage());
         }
         catch(ClassNotFoundException e)
         {
            throw new JBossXBRuntimeException("Failed to load the class to deserialize object: " + e.getMessage());
         }
      }

      return content;
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
      if(parent instanceof XOPElementHandler.XOPElement)
      {
         ((XOPElementHandler.XOPElement)parent).value = o;
      }
      else
      {
         ElementBinding parentElement = (ElementBinding)parentParticle.getTerm();
         DefaultHandlers.ELEMENT_HANDLER.setParent(parent, o, parentElement.getQName(), particle, parentParticle);
      }
   }
}
