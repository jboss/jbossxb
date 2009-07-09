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
package org.jboss.xb.util;

import javax.xml.namespace.QName;

import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.xml.sax.InputSource;

/**
 * Simple schema binding interface.
 *
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @author <a href="ales.justin@jboss.com">Ales Justin</a>
 */
public interface SchemaBindingValidator
{
   /**
    * Types and elements from the namespace passed into this method will be excluded from validation.
    *
    * @param ns  namespace to exclude
    */
   void excludeNs(String ns);

   /**
    * Checks if the specified namespace is excluded from validation.
    *
    * @param ns  the namespace to check
    * @return  true if the namespace is excluded
    */
   boolean isNsExcluded(String ns);

   /**
    * Removes the namespace from the excluded set. If the namespace has not been excluded, the method does nothing.
    *
    * @param ns  the namespace to remove from the excluded set.
    */
   void includeNs(String ns);

   /**
    * Excludes the specified type from validation.
    *
    * @param qName  the QName of the type to exclude from validation
    */
   void excludeType(QName qName);

   /**
    * Checks if the type is excluded from validation.
    *
    * @param qName  the QName of the type to check
    * @return  true if the type is excluded from validation
    */
   boolean isTypeExcluded(QName qName);

   /**
    * Removes the specified type from the excluded set. If the type has not been excluded, the method does nothing.
    *
    * @param qName  the QName of type to remove from the excluded set.
    */
   void includeType(QName qName);

   /**
    * This method will check that the XSD represented with InputSource and SchemaBinding are consistent.
    * The consistency is checked to certain degree and is far from 100%. Currently it checks just for basic things
    * such as the existence of type definitions, attribute and element declarations and element ordering.
    *
    * @param is  InputSource of the XSD
    * @param binding  SchemaBinding
    */
   void validate(InputSource is, SchemaBinding binding);

   /**
    * Validate xsd schema against classes.
    *
    * @param xsdName the schema name
    * @param cls the classes to check
    */
   void validate(String xsdName, Class<?>... cls);
}
