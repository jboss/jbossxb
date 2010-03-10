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
package org.jboss.xb.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jboss.xb.spi.BeanAdapterBuilder;
import org.jboss.xb.spi.DefaultBeanAdapterBuilder;

/**
 * JBossXmlType.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JBossXmlType
{
   /**
    * The bean adapter builder
    * 
    * @return the builder
    */
   Class<? extends BeanAdapterBuilder> beanAdapterBuilder() default DefaultBeanAdapterBuilder.class;
   
   /**
    * @return  model group type which should be used for the xml type
    */
   String modelGroup() default JBossXmlConstants.DEFAULT;
   
   JBossXmlAccessMode accessMode() default JBossXmlAccessMode.NOT_SPECIFIED;
}
