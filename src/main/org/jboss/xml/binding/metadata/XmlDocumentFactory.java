/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata;

import org.jboss.xml.binding.metadata.XmlDocumentFactoryImpl;
import org.jboss.xml.binding.metadata.XmlDocument;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public abstract class XmlDocumentFactory
{
   public static final XmlDocumentFactory newInstance()
   {
      return new XmlDocumentFactoryImpl();
   }

   protected XmlDocumentFactory()
   {
   }

   public abstract XmlDocument newDocument();
}
