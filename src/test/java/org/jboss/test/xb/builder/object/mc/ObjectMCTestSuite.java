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
package org.jboss.test.xb.builder.object.mc;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.jboss.test.xb.builder.object.mc.test.AliasTestCase;
import org.jboss.test.xb.builder.object.mc.test.AnnotationTestCase;
import org.jboss.test.xb.builder.object.mc.test.ArrayTestCase;
import org.jboss.test.xb.builder.object.mc.test.BeanFactoryTestCase;
import org.jboss.test.xb.builder.object.mc.test.BeanTestCase;
import org.jboss.test.xb.builder.object.mc.test.CallbackTestCase;
import org.jboss.test.xb.builder.object.mc.test.ClassLoaderTestCase;
import org.jboss.test.xb.builder.object.mc.test.CollectionTestCase;
import org.jboss.test.xb.builder.object.mc.test.ConstructorTestCase;
import org.jboss.test.xb.builder.object.mc.test.DemandTestCase;
import org.jboss.test.xb.builder.object.mc.test.DependencyTestCase;
import org.jboss.test.xb.builder.object.mc.test.DeploymentTestCase;
import org.jboss.test.xb.builder.object.mc.test.FactoryTestCase;
import org.jboss.test.xb.builder.object.mc.test.InjectionTestCase;
import org.jboss.test.xb.builder.object.mc.test.InstallTestCase;
import org.jboss.test.xb.builder.object.mc.test.LifecycleTestCase;
import org.jboss.test.xb.builder.object.mc.test.ListTestCase;
import org.jboss.test.xb.builder.object.mc.test.MapTestCase;
import org.jboss.test.xb.builder.object.mc.test.ParameterTestCase;
import org.jboss.test.xb.builder.object.mc.test.PropertyTestCase;
import org.jboss.test.xb.builder.object.mc.test.SetTestCase;
import org.jboss.test.xb.builder.object.mc.test.SupplyTestCase;
import org.jboss.test.xb.builder.object.mc.test.ValueTestCase;

/**
 * MC Test Suite.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 62474 $
 */
public class ObjectMCTestSuite extends TestSuite
{
   public static void main(String[] args)
   {
      TestRunner.run(suite());
   }

   public static Test suite()
   {
      TestSuite suite = new TestSuite("MC Tests");

      suite.addTest(DeploymentTestCase.suite());
      suite.addTest(BeanTestCase.suite());
      suite.addTest(BeanFactoryTestCase.suite());
      suite.addTest(ClassLoaderTestCase.suite());
      suite.addTest(ConstructorTestCase.suite());
      suite.addTest(FactoryTestCase.suite());
      suite.addTest(ParameterTestCase.suite());
      suite.addTest(PropertyTestCase.suite());
      suite.addTest(LifecycleTestCase.suite());
      suite.addTest(DependencyTestCase.suite());
      suite.addTest(DemandTestCase.suite());
      suite.addTest(SupplyTestCase.suite());
      suite.addTest(InstallTestCase.suite());
      suite.addTest(ValueTestCase.suite());
      suite.addTest(InjectionTestCase.suite());
      suite.addTest(CollectionTestCase.suite());
      suite.addTest(ListTestCase.suite());
      suite.addTest(SetTestCase.suite());
      suite.addTest(ArrayTestCase.suite());
      suite.addTest(MapTestCase.suite());
      suite.addTest(AnnotationTestCase.suite());
      suite.addTest(AliasTestCase.suite());
      suite.addTest(CallbackTestCase.suite());

      return suite;
   }
}
