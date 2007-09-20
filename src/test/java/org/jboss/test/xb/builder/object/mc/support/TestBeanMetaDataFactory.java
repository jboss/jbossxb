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
package org.jboss.test.xb.builder.object.mc.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jboss.test.xb.builder.object.mc.support.model.AbstractBeanMetaData;
import org.jboss.test.xb.builder.object.mc.support.model.BeanMetaData;
import org.jboss.test.xb.builder.object.mc.support.model.BeanMetaDataFactory;
import org.jboss.util.JBossObject;

/**
 * TestBeanMetaDataFactory.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 59941 $
 */
public class TestBeanMetaDataFactory extends JBossObject implements BeanMetaDataFactory, Serializable
{
   private static final long serialVersionUID = 1L;

   public List<BeanMetaData> getBeans()
   {
      ArrayList<BeanMetaData> result = new ArrayList<BeanMetaData>();
      AbstractBeanMetaData bean = new AbstractBeanMetaData("Bean1");
      result.add(bean);
      bean = new AbstractBeanMetaData("Bean2");
      result.add(bean);
      return result;
   }
}
