/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.apache.xerces.impl.xs;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xs.XSElementDeclaration;

/**
 * This class extends the XMLSchemaValidator from Xerces-2 distribution to give access to package protected instance variables.
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class JBossXBSchemaValidator
   extends XMLSchemaValidator
{
   Augmentations handleStartElement(QName element, XMLAttributes attributes, Augmentations augs)
   {
      Augmentations modifiedAugs = super.handleStartElement(element, attributes, augs);
      if(modifiedAugs != null)
      {
         modifiedAugs.putItem("jbossxb.validator", this);
      }
      return modifiedAugs;
   }

   public XSElementDeclaration getCurrentElementDelcaration()
   {
      return this.fCurrentElemDecl;
   }
}
