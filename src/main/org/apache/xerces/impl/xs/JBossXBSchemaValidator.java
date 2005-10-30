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
package org.apache.xerces.impl.xs;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xs.XSElementDeclaration;

/**
 * This class extends the XMLSchemaValidator from Xerces-2 distribution to give
 * access to package protected instance variables.
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
      // this.fCurrentElemDecl is not longer accessible in xerces 2.7.0
      return null;
   }
}
