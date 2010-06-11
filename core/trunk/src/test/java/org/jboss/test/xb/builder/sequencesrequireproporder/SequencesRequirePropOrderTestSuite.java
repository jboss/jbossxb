/*
  * JBoss, Home of Professional Open Source
  * Copyright 2010, JBoss Inc., and individual contributors as indicated
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
package org.jboss.test.xb.builder.sequencesrequireproporder;

import org.jboss.test.xb.builder.sequencesrequireproporder.test.InitializationUnitTestCase;
import org.jboss.test.xb.builder.sequencesrequireproporder.test.RootWithSequencePropertyUnitTestCase;
import org.jboss.test.xb.builder.sequencesrequireproporder.test.RootWithUndefinedPropOrderUnitTestCase;
import org.jboss.test.xb.builder.sequencesrequireproporder.test.RootWithUnorderedSequencePropertyUnitTestCase;
import org.jboss.test.xb.builder.sequencesrequireproporder.test.RootWithUnorderedSequenceUnitTestCase;
import org.jboss.test.xb.builder.sequencesrequireproporder.test.RootWithXmlAccessorOrderUnitTestCase;
import org.jboss.test.xb.builder.sequencesrequireproporder.test.UnorderedSequenceForRootWithSequencePropertyUnitTestCase;
import org.jboss.test.xb.builder.sequencesrequireproporder.test.UnorderedSequenceForRootWithUndefinedPropOrderUnitTestCase;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * A SequencesRequirePropOrderTestSuite.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class SequencesRequirePropOrderTestSuite extends TestSuite
{
   public static Test suite()
   {
      TestSuite suite = new TestSuite("Sequences Require propOrder Tests");
      
      suite.addTest(InitializationUnitTestCase.suite());
      suite.addTest(RootWithSequencePropertyUnitTestCase.suite());
      suite.addTest(RootWithUndefinedPropOrderUnitTestCase.suite());
      suite.addTest(RootWithUnorderedSequencePropertyUnitTestCase.suite());
      suite.addTest(RootWithUnorderedSequenceUnitTestCase.suite());
      suite.addTest(RootWithXmlAccessorOrderUnitTestCase.suite());
      suite.addTest(UnorderedSequenceForRootWithSequencePropertyUnitTestCase.suite());
      suite.addTest(UnorderedSequenceForRootWithUndefinedPropOrderUnitTestCase.suite());

      return suite;
   }
}
