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
package org.jboss.test.ejb.metadata.test;

import java.math.BigDecimal;

import junit.framework.Test;

import org.jboss.ejb.metadata.spec.EjbJar21MetaData;
import org.jboss.ejb.metadata.spec.EjbJar2xMetaData;
//import org.jboss.metadata.ApplicationMetaData;
import org.jboss.test.javaee.metadata.AbstractJavaEEMetaDataTest;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBindingResolver;

/**
 * EjbJarUnitTestCase.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class EjbJar21UnitTestCase extends AbstractJavaEEMetaDataTest
{
   public static Test suite()
   {
      return suite(EjbJar21UnitTestCase.class);
   }
   
   public static SchemaBindingResolver initResolver()
   {
      return schemaResolverForClass(EjbJar21MetaData.class);
      //return AbstractJavaEEMetaDataTest.initResolverJ2EE(EjbJar21MetaData.class);
   }
   
   public EjbJar21UnitTestCase(String name)
   {
      super(name);
   }
   
   protected EjbJar21MetaData unmarshal() throws Exception
   {
      return unmarshal(EjbJar21MetaData.class);
   }
   
   public void testVersion() throws Exception
   {
      EjbJar2xMetaData result = unmarshal();
      assertEquals(new BigDecimal("2.1"), result.getVersion());
      assertFalse(result.isEJB1x());
      assertTrue(result.isEJB2x());
      assertTrue(result.isEJB21());
      assertFalse(result.isEJB3x());
      
/*      ApplicationMetaData old = new ApplicationMetaData(result);
      assertFalse(old.isEJB1x());
      assertTrue(old.isEJB2x());
      assertTrue(old.isEJB21());
      assertFalse(old.isEJB3x());
*/   }
}
