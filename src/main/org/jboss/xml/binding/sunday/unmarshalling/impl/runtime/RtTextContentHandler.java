/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling.impl.runtime;

import org.jboss.xml.binding.sunday.unmarshalling.impl.TextContentHandlerImpl;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class RtTextContentHandler
   extends TextContentHandlerImpl
{
   public void set(Object owner, Object value, QName name)
   {
      FieldSetter setter = RtUtil.resolveSetter(owner, name);
      setter.set(owner, value, name);
      invokeNext(owner, value, name);
   }
}
