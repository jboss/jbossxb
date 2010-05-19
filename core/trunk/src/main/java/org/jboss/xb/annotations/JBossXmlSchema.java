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

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;

/**
 * JBossXmlSchema.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
@Target({ElementType.TYPE, ElementType.PACKAGE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JBossXmlSchema
{
   /** The namespace **/
   String namespace() default JBossXmlConstants.DEFAULT;
   
   /** The element form default */
   XmlNsForm elementFormDefault() default XmlNsForm.UNSET;

   /** The attribute form default */
   XmlNsForm attributeFormDefault() default XmlNsForm.UNSET;
   
   XmlNs[] xmlns() default {};
   
   /** Whether to ignore unresolved field and class names, default true */
   boolean ignoreUnresolvedFieldOrClass() default true;

   /** Should _ be considered as part of a java identifier, default true */
   boolean ignoreLowLine() default true;

   /** Should system properties be replaced, default true */
   boolean replacePropertyRefs() default true;

   /** The package name to resolve classes, default is package or the package of the type */
   String packageName() default JBossXmlConstants.DEFAULT;
   
   /** Whether the schema is strict */
   boolean strict() default true;
   
   /** Whether to trim string values */
   boolean normalizeSpace() default false;
   
   JBossXmlAccessMode accessMode() default JBossXmlAccessMode.PROPERTY;
}
