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
package org.jboss.test.xb.validator;


import org.jboss.test.xb.validator.test.BasicBindingValidatorUnitTestCase;
import org.jboss.test.xb.validator.test.ComplexTypeEquivalentToSimpleUnitTestCase;
import org.jboss.test.xb.validator.test.ImportedSchemaUnitTestCase;
import org.jboss.test.xb.validator.test.JavaBeanBindingValidationUnitTestCase;
import org.jboss.test.xb.validator.test.UnboundedChoiceAsUnorderedSequenceUnitTestCase;
import org.jboss.test.xb.validator.test.ValidatingResolverUnitTestCase;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * A SequencesRequirePropOrderTestSuite.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class ValidatorTestSuite extends TestSuite
{
   public static Test suite()
   {
      TestSuite suite = new TestSuite("Binding validator Tests");
      
      suite.addTest(BasicBindingValidatorUnitTestCase.suite());
      suite.addTest(ComplexTypeEquivalentToSimpleUnitTestCase.suite());
      suite.addTest(ImportedSchemaUnitTestCase.suite());
      suite.addTest(JavaBeanBindingValidationUnitTestCase.suite());
      suite.addTest(UnboundedChoiceAsUnorderedSequenceUnitTestCase.suite());
      suite.addTest(ValidatingResolverUnitTestCase.suite());
      
      return suite;
   }
}
