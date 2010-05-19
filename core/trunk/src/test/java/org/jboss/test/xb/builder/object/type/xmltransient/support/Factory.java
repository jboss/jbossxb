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
package org.jboss.test.xb.builder.object.type.xmltransient.support;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Factory.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class Factory extends Bean
{
   private String beanClassName;

   public Factory()
   {
      super.setClassName(FactoryBean.class.getName());
   }

   /* Uncommenting this makes no difference 
   public String getClassName()
   {
      return super.getClassName();
   } */
   
   @XmlTransient
   @Override
   public void setClassName(String className)
   {
      throw new RuntimeException("This method is marked @XmlTransient why is it being invoked by JAXB?");
   }
   
   public String getBeanClassName()
   {
      return beanClassName;
   }
   
   @XmlAttribute(name="className")
   public void setBeanClassName(String beanClassName)
   {
      this.beanClassName = beanClassName;
   }

   @Override   
   public Object getBean() throws Exception
   {
      FactoryBean factoryBean = (FactoryBean) super.getBean();
      return factoryBean.create();
   }
   
   @Override
   protected Object initialize() throws Exception
   {
      FactoryBean factoryBean = (FactoryBean) super.initialize();
      factoryBean.setClassName(beanClassName);
      return factoryBean;
   }
}
