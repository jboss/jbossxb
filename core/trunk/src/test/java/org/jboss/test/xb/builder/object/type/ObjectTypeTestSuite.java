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
package org.jboss.test.xb.builder.object.type;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.jboss.test.xb.builder.object.type.accessor.ObjectTypeAccessorTestSuite;
import org.jboss.test.xb.builder.object.type.collection.ObjectTypeCollectionTestSuite;
import org.jboss.test.xb.builder.object.type.simple.ObjectTypeSimpleTestSuite;
import org.jboss.test.xb.builder.object.type.value.ObjectTypeValueTestSuite;
import org.jboss.test.xb.builder.object.type.xmlanyelement.ObjectTypeXmlAnyElementTestSuite;
import org.jboss.test.xb.builder.object.type.xmlenum.ObjectTypeXmlEnumTestSuite;
import org.jboss.test.xb.builder.object.type.xmltransient.ObjectTypeXmlTransientTestSuite;
import org.jboss.test.xb.builder.object.type.xmltype.ObjectTypeXmlTypeTestSuite;

/**
 * ObjectTypeXmlTypeTestSuite.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @author <a href="ales.justin@jboss.com">Ales Justin</a>
 * @version $Revision: 1.1 $
 */
public class ObjectTypeTestSuite extends TestSuite
{
   public static void main(String[] args)
   {
      TestRunner.run(suite());
   }

   public static Test suite()
   {
      TestSuite suite = new TestSuite("Object Type Tests");

      suite.addTest(ObjectTypeXmlTypeTestSuite.suite());
      suite.addTest(ObjectTypeXmlEnumTestSuite.suite());
      suite.addTest(ObjectTypeSimpleTestSuite.suite());
      suite.addTest(ObjectTypeCollectionTestSuite.suite());
      suite.addTest(ObjectTypeValueTestSuite.suite());
      suite.addTest(ObjectTypeXmlAnyElementTestSuite.suite());
      suite.addTest(ObjectTypeXmlTransientTestSuite.suite());
      suite.addTest(ObjectTypeAccessorTestSuite.suite());

      return suite;
   }
}
