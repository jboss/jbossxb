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
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface ParticleHandler
{
   Object startParticle(Object parent,
                        QName elementName,
                        ParticleBinding particle,
                        Attributes attrs,
                        NamespaceContext nsCtx);

   Object endParticle(Object o, QName elementName, ParticleBinding particle);

   void setParent(Object parent,
                  Object o,
                  QName elementName,
                  ParticleBinding particle,
                  ParticleBinding parentParticle);
}
