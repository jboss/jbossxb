/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, JBoss Inc., and individual contributors as indicated
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
package org.jboss.test.xb.validator.test;

import junit.framework.Test;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.validator.support.EE2Root;
import org.jboss.test.xb.validator.support.ValidatorTestRoot;
import org.jboss.xb.binding.JBossXBException;
import org.jboss.xb.binding.resolver.MultiClassSchemaResolver;
import org.jboss.xb.builder.JBossXBBuilder;


/**
 * A ValidatingResolverUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class ValidatingResolverUnitTestCase extends AbstractBuilderTest
{
   public static Test suite()
   {
      return suite(ValidatingResolverUnitTestCase.class);
   }
   
   public ValidatingResolverUnitTestCase(String name)
   {
      super(name);
   }

   public void testInvalidBinding() throws Exception
   {
      MultiClassSchemaResolver resolver = new MultiClassSchemaResolver();
      resolver.mapURIToClass("urn:jboss:xb:test", EE2Root.class);
      String xsd = findXML("ValidatingResolverUnitTestCase.xsd");
      resolver.mapSchemaLocation("urn:jboss:xb:test", xsd);      
      resolver.setValidateBinding(true);
      
      try
      {
         unmarshal("ValidatingResolverUnitTestCase.xml", EE2Root.class, resolver);
         fail("Validation expected to fail");
      }
      catch(JBossXBException e)
      {
         String msg = e.getCause().getMessage();
         if(JBossXBBuilder.isUseUnorderedSequence())
         {
            // ordering hack
            boolean check = "ElementBinding {urn:jboss:xb:test}e is missing: [{urn:jboss:xb:test}e2, {urn:jboss:xb:test}e1]".equals(msg);
            check |=        "ElementBinding {urn:jboss:xb:test}e is missing: [{urn:jboss:xb:test}e1, {urn:jboss:xb:test}e2]".equals(msg);
            assertTrue(check);
         }
         else
         {
            assertEquals("Compared elements have different names: XSD QName is {urn:jboss:xb:test}e1, ElementBinding QName is {urn:jboss:xb:test}e", msg);
         }
      }
   }

   public void testValidBinding() throws Exception
   {
      MultiClassSchemaResolver resolver = new MultiClassSchemaResolver();
      resolver.mapURIToClass("urn:jboss:xb:test", ValidatorTestRoot.class);
      String xsd = findXML("ValidatingResolverUnitTestCase.xsd");
      resolver.mapSchemaLocation("urn:jboss:xb:test", xsd);      
      resolver.setValidateBinding(true);
      
      ValidatorTestRoot root = (ValidatorTestRoot) unmarshal("ValidatingResolverUnitTestCase.xml", ValidatorTestRoot.class, resolver);
      assertEquals("1", root.getE1());
      assertEquals("2", root.getE2());
   }
}
