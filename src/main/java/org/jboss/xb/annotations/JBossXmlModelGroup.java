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

import javax.xml.bind.annotation.XmlElement;

/**
 * JBossXmlModelGroup binds a Java class to a model group in the schema.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JBossXmlModelGroup
{
   String kind() default JBossXmlConstants.MODEL_GROUP_SEQUENCE;
   
   String name() default JBossXmlConstants.DEFAULT;
   
   /**
    * Used when a model group is bound to a Java class propeties
    * of which are bound to model group particles
    * 
    * @return
    */
   String[] propOrder() default {""};
   
   /**
    * Used when a model group is bound to a class hierarchy,
    * i.e. each subclass of the class annotated with JBossXmlModelGroup
    * is bound to particle of the model group.
    * Note: most likely the model group is going to be a choice (?)
    */
   Particle[] particles() default {};
   
   @interface Particle
   {
      XmlElement element();
      Class type();
   }
}
