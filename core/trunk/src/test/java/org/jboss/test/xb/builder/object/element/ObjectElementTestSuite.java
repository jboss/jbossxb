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
package org.jboss.test.xb.builder.object.element;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.jboss.test.xb.builder.object.element.javatypeadapter.test.JavaTypeAdapterUnitTestCase;
import org.jboss.test.xb.builder.object.element.jbossxmlnsprefix.test.JBossXmlNsPrefixUnitTestCase;
import org.jboss.test.xb.builder.object.element.wrapper.test.WrapperUnitTestCase;
import org.jboss.test.xb.builder.object.element.xmlelement.ObjectXmlElementTestSuite;
import org.jboss.test.xb.builder.object.element.xmlelements.test.XmlElementsUnitTestCase;
import org.jboss.test.xb.builder.object.element.xmlrootelement.ObjectXmlRootElementTestSuite;

/**
 * ObjectElementTestSuite.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class ObjectElementTestSuite extends TestSuite
{
   public static void main(String[] args)
   {
      TestRunner.run(suite());
   }

   public static Test suite()
   {
      TestSuite suite = new TestSuite("Object Element Tests");

      suite.addTest(JavaTypeAdapterUnitTestCase.suite());
      suite.addTest(JBossXmlNsPrefixUnitTestCase.suite());
      suite.addTest(WrapperUnitTestCase.suite());
      suite.addTest(ObjectXmlElementTestSuite.suite());
      suite.addTest(XmlElementsUnitTestCase.suite());
      suite.addTest(ObjectXmlRootElementTestSuite.suite());
      
      return suite;
   }
}
