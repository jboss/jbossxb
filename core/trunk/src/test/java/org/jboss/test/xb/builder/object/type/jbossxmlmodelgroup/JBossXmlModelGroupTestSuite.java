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
package org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.test.ParticleChoiceBindingTestCase;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.test.ParticleChoiceUnmarshallingTestCase;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.test.PropertyAllBindingTestCase;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.test.PropertyAllUnmarshallingTestCase;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.test.PropertyChoiceBindingTestCase;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.test.PropertyChoiceUnmarshallingTestCase;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.test.PropertySequenceBindingTestCase;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.test.PropertySequenceUnmarshallingTestCase;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.test.RepeatableParticleChoiceBindingTestCase;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.test.RepeatableParticleChoiceUnmarshallingTestCase;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.test.RepeatablePropertyAllBindingTestCase;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.test.RepeatablePropertyAllUnmarshallingTestCase;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.test.RepeatablePropertyChoiceBindingTestCase;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.test.RepeatablePropertyChoiceUnmarshallingTestCase;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.test.RepeatablePropertySequenceBindingTestCase;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.test.RepeatablePropertySequenceUnmarshallingTestCase;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.test.RootWithTwoParticleGroupsBindingTestCase;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.test.RootWithTwoParticleGroupsUnmarshallingTestCase;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.test.RootWithTwoPropertyGroupsBindingTestCase;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.test.RootWithTwoPropertyGroupsUnmarshallingTestCase;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.test.XmlTypeChoiceCollectionBindingTestCase;
import org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.test.XmlTypeChoiceCollectionUnmarshallingTestCase;

/**
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class JBossXmlModelGroupTestSuite extends TestSuite
{
   public static void main(String[] args)
   {
      TestRunner.run(suite());
   }

   public static Test suite()
   {
      TestSuite suite = new TestSuite("JBossXmlModelGroup Tests");

      suite.addTest(ParticleChoiceBindingTestCase.suite());
      suite.addTest(ParticleChoiceUnmarshallingTestCase.suite());
      suite.addTest(PropertyAllBindingTestCase.suite());
      suite.addTest(PropertyAllUnmarshallingTestCase.suite());
      suite.addTest(PropertyChoiceBindingTestCase.suite());
      suite.addTest(PropertyChoiceUnmarshallingTestCase.suite());
      suite.addTest(PropertySequenceBindingTestCase.suite());
      suite.addTest(PropertySequenceUnmarshallingTestCase.suite());
      suite.addTest(RepeatableParticleChoiceBindingTestCase.suite());
      suite.addTest(RepeatableParticleChoiceUnmarshallingTestCase.suite());
      suite.addTest(RepeatablePropertyAllBindingTestCase.suite());
      suite.addTest(RepeatablePropertyAllUnmarshallingTestCase.suite());
      suite.addTest(RepeatablePropertyChoiceBindingTestCase.suite());
      suite.addTest(RepeatablePropertyChoiceUnmarshallingTestCase.suite());
      suite.addTest(RepeatablePropertySequenceBindingTestCase.suite());
      suite.addTest(RepeatablePropertySequenceUnmarshallingTestCase.suite());
      suite.addTest(RootWithTwoParticleGroupsBindingTestCase.suite());
      suite.addTest(RootWithTwoParticleGroupsUnmarshallingTestCase.suite());
      suite.addTest(RootWithTwoPropertyGroupsBindingTestCase.suite());
      suite.addTest(RootWithTwoPropertyGroupsUnmarshallingTestCase.suite());
      suite.addTest(XmlTypeChoiceCollectionBindingTestCase.suite());
      suite.addTest(XmlTypeChoiceCollectionUnmarshallingTestCase.suite());

      return suite;
   }
}
