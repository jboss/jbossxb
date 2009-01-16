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

/**
 * Sometimes, it is desirable to bind a class into different schemas
 * with different namespaces or bind a class hierarchy into a specific
 * target namespace and some of the classes into a different namespace
 * the value of which depends on the root element's namespace.
 * XmlElement's namespace attribute cannot be used in a case like that.
 * What we need is to specify a prefix instead of the namespace itself
 * and define the namespace to prefix mapping in the JBossXmlSchema annotation.
 * 
 * This annotation is used to reference a namespace by its prefix.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JBossXmlNsPrefix
{
   String prefix();
   
   /**
    * If the prefix mapping is not found and the value is true
    * then the target schema namespace will be used, if the value is
    * false then an exception will be thrown.
    * 
    * @return
    */
   boolean schemaTargetIfNotMapped() default false;
   
   /**
    * True means the element or model group the property is bound to will be bound to the namespace specified by the prefix.
    * False means the element or model group will be in the schema's target namespace.
    * 
    * @return
    */
   boolean applyToComponentQName() default true;
   
   /**
    * True means the type of the property (including its child elements, their types and model groups recursively)
    * will be bound to the namespace specified by the prefix.
    * False means the type of the property (including its child elements, their types and model groups recursively)
    * will be bound to the schema's target namespace.
    * 
    * @return
    */
   boolean applyToComponentContent() default true;
}
