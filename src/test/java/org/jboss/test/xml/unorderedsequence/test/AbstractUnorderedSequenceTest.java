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
package org.jboss.test.xml.unorderedsequence.test;

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.jboss.config.plugins.property.PropertyConfiguration;
import org.jboss.reflect.spi.ClassInfo;
import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.xb.binding.JBossXBException;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.builder.JBossXBNoSchemaBuilder;

public abstract class AbstractUnorderedSequenceTest<T> extends AbstractBuilderTest
{

   private Class<?> rootClass;
   
   public AbstractUnorderedSequenceTest(String name, Class<? extends T> rootClass)
   {
      super(name);
      this.rootClass = rootClass;
   }

   protected abstract String getCorrectName();
   protected abstract String getIncorrectName();
   protected abstract void assertResult(T result);
   protected abstract String getValidationError();
   
   public void testValidXmlWithOrderedBinding() throws Exception
   {
      T result = unmarshal(false, getCorrectName());
      assertResult(result);
   }
   
   public void testInvalidXmlWithOrderedBinding() throws Exception
   {
      try
      {
         unmarshal(false, getIncorrectName());
         fail(getValidationError());
      }
      catch(JBossXBException e)
      {
         JBossXBRuntimeException re = (JBossXBRuntimeException) e.getCause();
         assertEquals(getValidationError(), re.getMessage());
      }
   }

   public void testValidXmlWithUnorderedBinding() throws Exception
   {
      T result = unmarshal(true, getCorrectName());
      assertResult(result);
   }

   public void testInvalidXmlWithUnorderedBinding() throws Exception
   {
      T result = unmarshal(true, getIncorrectName());
      assertResult(result);
   }

   @SuppressWarnings("unchecked")
   protected T unmarshal(boolean unordered, String fileName) throws Exception
   {
      PropertyConfiguration config = AccessController.doPrivileged(new PrivilegedAction<PropertyConfiguration>()
            {
               public PropertyConfiguration run()
               {
                  return new PropertyConfiguration();
               }
            });
      ClassInfo classInfo = config.getClassInfo(rootClass);
      JBossXBNoSchemaBuilder builder = new JBossXBNoSchemaBuilder(classInfo);
      builder.setUseUnorderedSequence(unordered);
      SchemaBinding schema = builder.build();
      
      return (T) unmarshal(fileName, schema);
   }

}