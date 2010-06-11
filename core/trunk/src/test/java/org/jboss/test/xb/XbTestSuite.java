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
package org.jboss.test.xb;


import org.jboss.test.javabean.test.JavaBeanTestSuite;
import org.jboss.test.xb.builder.BuilderTestSuite;
import org.jboss.test.xb.validator.ValidatorTestSuite;
import org.jboss.test.xml.AnnotatedPojoServerUnitTestCase;
import org.jboss.test.xml.AnnotationsUnitTestCase;
import org.jboss.test.xml.AnyComplexTypeUnitTestCase;
import org.jboss.test.xml.AnyTypeDomBindingUnitTestCase;
import org.jboss.test.xml.AnyUnitTestCase;
import org.jboss.test.xml.ArrayWrapperUnitTestCase;
import org.jboss.test.xml.AttributeRefUnitTestCase;
import org.jboss.test.xml.AttributesUnitTestCase;
import org.jboss.test.xml.BasicArraysUnitTestCase;
import org.jboss.test.xml.BeforeMarshalAfterUnmarshalHandlerTestCase;
import org.jboss.test.xml.BooleanPatternUnitTestCase;
import org.jboss.test.xml.ChoiceMinOccurs0UnitTestCase;
import org.jboss.test.xml.CollectionOverridePropertyUnitTestCase;
import org.jboss.test.xml.CollectionsUnitTestCase;
import org.jboss.test.xml.Dom2SaxUnitTestCase;
import org.jboss.test.xml.DuplicateInterceptorUnitTestCase;
import org.jboss.test.xml.EnumUnitTestCase;
import org.jboss.test.xml.ExceptionUnitTestCase;
import org.jboss.test.xml.GlobalGroupUnitTestCase;
import org.jboss.test.xml.IgnorableWhitespaceUnitTestCase;
import org.jboss.test.xml.IntrospectionUnitTestCase;
import org.jboss.test.xml.JNDIBindingUnitTestCase;
import org.jboss.test.xml.JbxbCharactersUnitTestCase;
import org.jboss.test.xml.JbxbPojoServerUnitTestCase;
import org.jboss.test.xml.JbxbSchemaBindingAttributeUnitTestCase;
import org.jboss.test.xml.ListValueUnitTestCase;
import org.jboss.test.xml.LoginConfigUnitTestCase;
import org.jboss.test.xml.MappingTestCase;
import org.jboss.test.xml.MapsUnitTestCase;
import org.jboss.test.xml.MinOccurs0UnitTestCase;
import org.jboss.test.xml.MiscUnitTestCase;
import org.jboss.test.xml.ModelGroupBindingUnitTestCase;
import org.jboss.test.xml.MultispacedUnitTestCase;
import org.jboss.test.xml.NamespaceRegistryUnitTestCase;
import org.jboss.test.xml.NestedWildcardUnitTestCase;
import org.jboss.test.xml.PoUnitTestCase;
import org.jboss.test.xml.PojoServerUnitTestCase;
import org.jboss.test.xml.QNameAttributesUnitTestCase;
import org.jboss.test.xml.RepeatableTermsUnitTestCase;
import org.jboss.test.xml.RepeatedElementsUnitTestCase;
import org.jboss.test.xml.RequiredAttributesUnitTestCase;
import org.jboss.test.xml.SchemaBindingInitializerUnitTestCase;
import org.jboss.test.xml.SchemaImportUnitTestCase;
import org.jboss.test.xml.SchemaIncludeUnitTestCase;
import org.jboss.test.xml.SchemalessTestCase;
import org.jboss.test.xml.SharedElementUnitTestCase;
import org.jboss.test.xml.SimpleContentUnitTestCase;
import org.jboss.test.xml.SimpleTestCase;
import org.jboss.test.xml.SimpleTypeBindingUnitTestCase;
import org.jboss.test.xml.SoapEncUnitTestCase;
import org.jboss.test.xml.SundayUnitTestCase;
import org.jboss.test.xml.TopLevelValueAdapterUnitTestCase;
import org.jboss.test.xml.WildcardUnresolvedElementsUnitTestCase;
import org.jboss.test.xml.WildcardWrapperUnitTestCase;
import org.jboss.test.xml.XIncludeUnitTestCase;
import org.jboss.test.xml.XMLNameToJavaIdentifierUnitTestCase;
import org.jboss.test.xml.XOPUnitTestCase;
import org.jboss.test.xml.XercesBugTestCase;
import org.jboss.test.xml.XsiNilUnitTestCase;
import org.jboss.test.xml.XsiTypeUnitTestCase;
import org.jboss.test.xml.choiceresolution.test.Foo2BarSequenceOrBarUnitTestCase;
import org.jboss.test.xml.choiceresolution.test.FooBarSequenceOrBarUnitTestCase;
import org.jboss.test.xml.elementorder.test.ElementOrderUnitTestCase;
import org.jboss.test.xml.jbxb.defaults.DefaultsUnitTestCase;
import org.jboss.test.xml.jbxb.minOccurs.Schema1UnitTestCase;
import org.jboss.test.xml.unorderedsequence.UnorderedSequenceTestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * A SequencesRequirePropOrderTestSuite.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class XbTestSuite extends TestSuite
{
   public static Test suite()
   {
      TestSuite suite = new TestSuite("Binding validator Tests");
      
      suite.addTest(AnnotatedPojoServerUnitTestCase.suite());
      suite.addTest(AnnotationsUnitTestCase.suite());
      suite.addTest(AnyComplexTypeUnitTestCase.suite());
      suite.addTest(AnyTypeDomBindingUnitTestCase.suite());
      suite.addTest(AnyUnitTestCase.suite());
      suite.addTest(ArrayWrapperUnitTestCase.suite());
      suite.addTest(AttributeRefUnitTestCase.suite());
      suite.addTest(AttributesUnitTestCase.suite());
      suite.addTest(BasicArraysUnitTestCase.suite());
      suite.addTest(BeforeMarshalAfterUnmarshalHandlerTestCase.suite());
      suite.addTest(BooleanPatternUnitTestCase.suite());
      suite.addTest(ChoiceMinOccurs0UnitTestCase.suite());
      suite.addTest(CollectionOverridePropertyUnitTestCase.suite());
      suite.addTest(CollectionsUnitTestCase.suite());
      suite.addTest(Dom2SaxUnitTestCase.suite());
      suite.addTest(DuplicateInterceptorUnitTestCase.suite());
      suite.addTest(EnumUnitTestCase.suite());
      suite.addTest(ExceptionUnitTestCase.suite());
      suite.addTest(GlobalGroupUnitTestCase.suite());
      suite.addTest(IgnorableWhitespaceUnitTestCase.suite());
      suite.addTest(IntrospectionUnitTestCase.suite());
      suite.addTest(JbxbCharactersUnitTestCase.suite());
      suite.addTest(JbxbPojoServerUnitTestCase.suite());
      suite.addTest(JbxbSchemaBindingAttributeUnitTestCase.suite());
      suite.addTest(JNDIBindingUnitTestCase.suite());
      suite.addTest(ListValueUnitTestCase.suite());
      suite.addTest(LoginConfigUnitTestCase.suite());
      suite.addTest(MappingTestCase.suite());
      suite.addTest(MapsUnitTestCase.suite());
      suite.addTest(MinOccurs0UnitTestCase.suite());
      suite.addTest(MiscUnitTestCase.suite());
      suite.addTest(ModelGroupBindingUnitTestCase.suite());
      suite.addTest(MultispacedUnitTestCase.suite());
      suite.addTest(NamespaceRegistryUnitTestCase.suite());
      suite.addTest(NestedWildcardUnitTestCase.suite());
      suite.addTest(PojoServerUnitTestCase.suite());
      suite.addTest(PoUnitTestCase.suite());
      suite.addTest(QNameAttributesUnitTestCase.suite());
      suite.addTest(RepeatableTermsUnitTestCase.suite());
      suite.addTest(RepeatedElementsUnitTestCase.suite());
      suite.addTest(RequiredAttributesUnitTestCase.suite());
      suite.addTest(SchemaBindingInitializerUnitTestCase.suite());
      suite.addTest(SchemaImportUnitTestCase.suite());
      suite.addTest(SchemaIncludeUnitTestCase.suite());
      suite.addTest(SchemalessTestCase.suite());
      suite.addTest(SharedElementUnitTestCase.suite());
      suite.addTest(SimpleContentUnitTestCase.suite());
      suite.addTest(SimpleTestCase.suite());
      suite.addTest(SimpleTypeBindingUnitTestCase.suite());
      suite.addTest(SoapEncUnitTestCase.suite());
      suite.addTest(SundayUnitTestCase.suite());
      suite.addTest(TopLevelValueAdapterUnitTestCase.suite());
      suite.addTest(WildcardUnresolvedElementsUnitTestCase.suite());
      suite.addTest(WildcardWrapperUnitTestCase.suite());
      suite.addTest(XercesBugTestCase.suite());
      suite.addTest(XIncludeUnitTestCase.suite());
      suite.addTest(XMLNameToJavaIdentifierUnitTestCase.suite());
      suite.addTest(XOPUnitTestCase.suite());
      suite.addTest(XsiNilUnitTestCase.suite());
      suite.addTest(XsiTypeUnitTestCase.suite());

      suite.addTest(Foo2BarSequenceOrBarUnitTestCase.suite());
      suite.addTest(FooBarSequenceOrBarUnitTestCase.suite());
      suite.addTest(ElementOrderUnitTestCase.suite());
      suite.addTest(DefaultsUnitTestCase.suite());
      suite.addTest(Schema1UnitTestCase.suite());
      suite.addTest(UnorderedSequenceTestSuite.suite());

      suite.addTest(BuilderTestSuite.suite());
      suite.addTest(ValidatorTestSuite.suite());

      suite.addTest(JavaBeanTestSuite.suite());
      
      return suite;
   }   
}
