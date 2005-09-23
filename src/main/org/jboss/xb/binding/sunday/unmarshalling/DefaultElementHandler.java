/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding.sunday.unmarshalling;

import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import org.xml.sax.Attributes;

/**
 * This handler can only be used if model group binding is not used.
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class DefaultElementHandler
   implements ElementHandler, ParticleHandler
{
   public static final DefaultElementHandler INSTANCE = new DefaultElementHandler();

   private AttributesHandler attrsHandler;

   public DefaultElementHandler()
   {
      this(AttributesHandler.INSTANCE);
   }

   public DefaultElementHandler(AttributesHandler attrsHandler)
   {
      this.attrsHandler = attrsHandler;
   }

   public Object startElement(Object parent, QName qName, ElementBinding element)
   {
      return parent;
   }

   public void attributes(Object o, QName elementName, ElementBinding element, Attributes attrs, NamespaceContext nsCtx)
   {
      if(attrsHandler != null)
      {
         attrsHandler.attributes(o, elementName, element.getType(), attrs, nsCtx);
      }
   }

   public Object endElement(Object o, QName qName, ElementBinding element)
   {
      return o;
   }

   public void setParent(Object parent, Object o, QName qName, ElementBinding element, ElementBinding parentElement)
   {
   }

   // ParticleHandler impl

   public Object startParticle(Object parent,
                               QName elementName,
                               ParticleBinding particle,
                               Attributes attrs,
                               NamespaceContext nsCtx)
   {
      ElementBinding element = (ElementBinding)particle.getTerm();
      Object o = startElement(parent, elementName, element);
      if(o != null)
      {
         attrs = element.getType().expandWithDefaultAttributes(attrs);
         attributes(o, elementName, element, attrs, nsCtx);
      }
      return o;
   }

   public Object endParticle(Object o, QName elementName, ParticleBinding particle)
   {
      return endElement(o, elementName, (ElementBinding)particle.getTerm());
   }

   public void setParent(Object parent,
                         Object o,
                         QName elementName,
                         ParticleBinding particle,
                         ParticleBinding parentParticle)
   {
      ElementBinding element = (ElementBinding)particle.getTerm();
      ElementBinding parentElement = (ElementBinding)parentParticle.getTerm();
      setParent(parent, o, elementName, element, parentElement);
   }
}
