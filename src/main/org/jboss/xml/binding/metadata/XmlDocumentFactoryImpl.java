/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata;

import org.jboss.xml.binding.metadata.XmlDocumentFactory;
import org.jboss.xml.binding.metadata.XmlDocument;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class XmlDocumentFactoryImpl
   extends XmlDocumentFactory
{
   public XmlDocument newDocument()
   {
      return new XmlDocumentImpl();
   }
}
