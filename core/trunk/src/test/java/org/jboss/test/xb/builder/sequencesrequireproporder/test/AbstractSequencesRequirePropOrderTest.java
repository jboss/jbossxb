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
package org.jboss.test.xb.builder.sequencesrequireproporder.test;

import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.builder.JBossXBBuilder;

import junit.framework.TestCase;

/**
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public abstract class AbstractSequencesRequirePropOrderTest extends TestCase
{
   protected abstract boolean isPropOrderProvided();
   protected abstract Class<?> getRootType();
   
   protected boolean isUseUnorderedSequence()
   {
      return false;
   }
   
   protected boolean isPropOrderConfigured()
   {
      return isUseUnorderedSequence() || isPropOrderProvided();
   }
   
   public void testSequencesRequirePropOrderTrue() throws Exception
   {
      boolean defSequencesRequirePropOrder = JBossXBBuilder.isSequencesRequirePropOrder();
      JBossXBBuilder.setSequencesRequirePropOrder(true);
      boolean defUseUnorderedSequence = JBossXBBuilder.isUseUnorderedSequence();
      JBossXBBuilder.setUseUnorderedSequence(isUseUnorderedSequence());
      try
      {
         JBossXBBuilder.build(getRootType(), true);
         if(!isPropOrderConfigured())
            fail("Binding didn't fail for " + getRootType().getName());
      }
      catch(JBossXBRuntimeException e)
      {
         if(isPropOrderProvided())
            throw e;
      }
      finally
      {
         JBossXBBuilder.setSequencesRequirePropOrder(defSequencesRequirePropOrder);
         JBossXBBuilder.setUseUnorderedSequence(defUseUnorderedSequence);
      }
   }
   
   public void testSequencesRequirePropOrderFalse() throws Exception
   {
      boolean defSequencesRequirePropOrder = JBossXBBuilder.isSequencesRequirePropOrder();
      JBossXBBuilder.setSequencesRequirePropOrder(false);
      boolean defUseUnorderedSequence = JBossXBBuilder.isUseUnorderedSequence();
      JBossXBBuilder.setUseUnorderedSequence(isUseUnorderedSequence());
      try
      {
         JBossXBBuilder.build(getRootType(), true);
      }
      finally
      {
         JBossXBBuilder.setSequencesRequirePropOrder(defSequencesRequirePropOrder);
         JBossXBBuilder.setUseUnorderedSequence(defUseUnorderedSequence);
      }      
   }
}