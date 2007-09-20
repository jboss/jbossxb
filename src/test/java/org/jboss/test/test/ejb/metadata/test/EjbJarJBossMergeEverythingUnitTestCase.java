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

import junit.framework.Test;

import org.jboss.ejb.metadata.jboss.JBoss50MetaData;
import org.jboss.ejb.metadata.jboss.JBossEnterpriseBeanMetaData;
import org.jboss.ejb.metadata.spec.EjbJar21MetaData;
import org.jboss.ejb.metadata.spec.EjbJar30MetaData;
import org.jboss.ejb.metadata.spec.EjbJar3xMetaData;
import org.jboss.ejb.metadata.spec.SessionBeanMetaData;
import org.jboss.javaee.metadata.spec.JavaEEMetaDataConstants;
import org.jboss.test.ejb.AbstractEJBEverythingTest;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBindingResolver;
import org.jboss.xb.builder.JBossXBBuilder;
import org.w3c.dom.ls.LSInput;

/**
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class EjbJarJBossMergeEverythingUnitTestCase
   extends AbstractEJBEverythingTest
{
   public static Test suite()
   {
      return suite(EjbJarJBossMergeEverythingUnitTestCase.class);
   }

   public static SchemaBindingResolver initResolver()
   {
      return new SchemaBindingResolver()
      {
         public String getBaseURI()
         {
            return null;
         }

         public SchemaBinding resolve(String nsUri, String baseURI, String schemaLocation)
         {
            SchemaBinding schema;
            if(JavaEEMetaDataConstants.JAVAEE_NS.equals(nsUri))
            {
               schema = JBossXBBuilder.build(EjbJar30MetaData.class);
            }
            else if(JavaEEMetaDataConstants.J2EE_NS.equals(nsUri))
            {
               schema = JBossXBBuilder.build(EjbJar21MetaData.class);
            }
            else if(JavaEEMetaDataConstants.JBOSS_NS.equals(nsUri))
            {
               schema = JBossXBBuilder.build(JBoss50MetaData.class);
            }
            else
            {
               throw new IllegalStateException("Unexpected namespace: " + nsUri);
            }
            return schema;
         }

         public LSInput resolveAsLSInput(String nsUri, String baseUri, String schemaLocation)
         {
            return null;
         }

         public void setBaseURI(String baseURI)
         {
         }
      };
   }
   
   public EjbJarJBossMergeEverythingUnitTestCase(String name)
   {
      super(name);
   }

   /**
    * Very basic merge test
    */
   public void testEJB3xEverything() throws Exception
   {
      EjbJar3xMetaData ejbJarMetaData = unmarshal("EjbJar3xEverything_testEverything.xml", EjbJar30MetaData.class, null);
      EjbJar3xEverythingUnitTestCase ejbJar = new EjbJar3xEverythingUnitTestCase("ejb-jar");
      ejbJar.assertEverything(ejbJarMetaData);

      JBoss50MetaData jbossMetaData = unmarshal("JBoss5xEverything_testEverything.xml", JBoss50MetaData.class, null);
      JBoss5xEverythingUnitTestCase jboss = new JBoss5xEverythingUnitTestCase("jboss");
      jboss.assertEverything(jbossMetaData);
      
      jbossMetaData.setOverridenMetaData(ejbJarMetaData);
      
      JBossEnterpriseBeanMetaData ejb = jbossMetaData.getMergedEnterpriseBean("session1EjbName");      
      assertNotNull(ejb);
      ejbJar.assertFullSessionBean("session1", (SessionBeanMetaData) ejb.getOverridenMetaData());
   }

   /**
    * Very basic merge test
    */
   public void testEJB21Everything() throws Exception
   {
      EjbJar21MetaData ejbJarMetaData = unmarshal("EjbJar21Everything_testEverything.xml", EjbJar21MetaData.class, null);
      EjbJar21EverythingUnitTestCase ejbJar = new EjbJar21EverythingUnitTestCase("ejb-jar");
      ejbJar.assertEverything(ejbJarMetaData);

      JBoss50MetaData jbossMetaData = unmarshal("JBoss5xEverything_testEverything.xml", JBoss50MetaData.class, null);
      JBoss5xEverythingUnitTestCase jboss = new JBoss5xEverythingUnitTestCase("jboss");
      jboss.assertEverything(jbossMetaData);

      jbossMetaData.setOverridenMetaData(ejbJarMetaData);

      JBossEnterpriseBeanMetaData ejb = jbossMetaData.getMergedEnterpriseBean("session1EjbName");      
      assertNotNull(ejb);
      ejbJar.assertFullSessionBean("session1", (SessionBeanMetaData) ejb.getOverridenMetaData());
   }
}
