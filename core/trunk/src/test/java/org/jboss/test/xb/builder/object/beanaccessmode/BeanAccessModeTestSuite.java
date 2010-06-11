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
package org.jboss.test.xb.builder.object.beanaccessmode;

import org.jboss.test.xb.builder.object.beanaccessmode.test.AllAccessModeGroupOverrideUnitTestCase;
import org.jboss.test.xb.builder.object.beanaccessmode.test.AllAccessModeTypeOverrideUnitTestCase;
import org.jboss.test.xb.builder.object.beanaccessmode.test.PropertyAccessModeGroupOverrideUnitTestCase;
import org.jboss.test.xb.builder.object.beanaccessmode.test.PropertyAccessModeTypeOverrideUnitTestCase;
import org.jboss.test.xb.builder.object.beanaccessmode.test.PublicMemberAccessModeGroupOverrideUnitTestCase;
import org.jboss.test.xb.builder.object.beanaccessmode.test.PublicMemberAccessModeTypeOverrideUnitTestCase;
import org.jboss.test.xb.builder.object.beanaccessmode.test.SchemaLevelAllAccessModeUnitTestCase;
import org.jboss.test.xb.builder.object.beanaccessmode.test.SchemaLevelPropertyAccessModeUnitTestCase;
import org.jboss.test.xb.builder.object.beanaccessmode.test.SchemaLevelPublicMemberAccessModeUnitTestCase;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


/**
 * ObjectAttributeTestSuite.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class BeanAccessModeTestSuite extends TestSuite
{
   public static void main(String[] args)
   {
      TestRunner.run(suite());
   }

   public static Test suite()
   {
      TestSuite suite = new TestSuite("Object Bean Access Mode Tests");

      suite.addTest(AllAccessModeGroupOverrideUnitTestCase.suite());
      suite.addTest(AllAccessModeTypeOverrideUnitTestCase.suite());
      suite.addTest(PropertyAccessModeGroupOverrideUnitTestCase.suite());
      suite.addTest(PropertyAccessModeTypeOverrideUnitTestCase.suite());
      suite.addTest(PublicMemberAccessModeGroupOverrideUnitTestCase.suite());
      suite.addTest(PublicMemberAccessModeTypeOverrideUnitTestCase.suite());
      suite.addTest(SchemaLevelAllAccessModeUnitTestCase.suite());
      suite.addTest(SchemaLevelPropertyAccessModeUnitTestCase.suite());
      suite.addTest(SchemaLevelPublicMemberAccessModeUnitTestCase.suite());

      return suite;
   }
}
