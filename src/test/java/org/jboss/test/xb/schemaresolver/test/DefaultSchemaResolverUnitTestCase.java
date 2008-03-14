/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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
package org.jboss.test.xb.schemaresolver.test;

import java.net.URL;

import org.jboss.test.xml.AbstractJBossXBTest;
import org.jboss.xb.binding.Util;
import org.jboss.xb.binding.sunday.unmarshalling.DefaultSchemaResolver;


/**
 * A DefaultSchemaResolverUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class DefaultSchemaResolverUnitTestCase
   extends AbstractJBossXBTest
{
   public DefaultSchemaResolverUnitTestCase(String name)
   {
      super(name);
   }

   public void testRedefine() throws Exception
   {
      enableTrace("org.jboss.xb");
      /**
       * it *has to* be in the classpath, not found with findXML()
       */
      String redefiningName = getRootName() + "_" + getName() + "_redefining.xsd";
      URL redefiningURL = Thread.currentThread().getContextClassLoader().getResource(redefiningName);
      assertNotNull("Expected to find " + redefiningName + " in the classpath", redefiningURL);
      
      DefaultSchemaResolver resolver = new DefaultSchemaResolver();
      resolver.addSchemaLocation("urn:jboss:xb:test", "DefaultSchemaResolverUnitTestCase_testRedefine_redefining.xsd");
      Util.loadSchema(redefiningURL.toExternalForm(), resolver);
   }
}
