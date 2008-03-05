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
package org.jboss.xb.builder;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashSet;

import javax.xml.XMLConstants;

import org.jboss.config.plugins.property.PropertyConfiguration;
import org.jboss.config.spi.Configuration;
import org.jboss.reflect.spi.ClassInfo;
import org.jboss.reflect.spi.PackageInfo;
import org.jboss.reflect.spi.TypeInfo;
import org.jboss.xb.annotations.JBossXmlConstants;
import org.jboss.xb.annotations.JBossXmlSchema;
import org.jboss.xb.binding.metadata.PackageMetaData;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBindingInitializer;

/**
 * JBossXBBuilder.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
public class JBossXBBuilder
{
   /** The configuration */
   static Configuration configuration;
   
   /** The string type */
   static final ClassInfo STRING;
   
   /** The object type */
   static final ClassInfo OBJECT;
   
   static
   {
      configuration = AccessController.doPrivileged(new PrivilegedAction<PropertyConfiguration>()
      {
         public PropertyConfiguration run()
         {
            return new PropertyConfiguration();
         }
      });
      
      STRING = configuration.getClassInfo(String.class);
      OBJECT = configuration.getClassInfo(Object.class);
   }
   
   /**
    * Create a new schema binding initializer
    * 
    * @param <T> the root type
    * @param root the root class
    * @return the initializer
    * @throws IllegalArgumentException for a null root
    */
   public static <T> SchemaBindingInitializer newInitializer(Class<T> root)
   {
      return new BuilderSchemaBindingInitializer<T>(root);
   }
   
   /**
    * Build from a preparsed schema binding
    * 
    * @param schemaBinding the schema binding
    * @param root the root
    * @throws IllegalArgumentException for a null schema binding or root
    */
   public static void build(SchemaBinding schemaBinding, Class<?> root)
   {
      if (schemaBinding == null)
         throw new IllegalArgumentException("Null schemaBinding");
      if (root == null)
         throw new IllegalArgumentException("Null root");

      // TODO build
   }
   
   /**
    * Build the SchemaBinding from the class
    * 
    * @param root the root
    * @return the schema binding
    * @throws IllegalArgumentException for a null  root
    */
   public static SchemaBinding build(Class<?> root)
   {
      ClassInfo classInfo = JBossXBBuilder.configuration.getClassInfo(root);

      SchemaBinding binding = classInfo.getAttachment(SchemaBinding.class);
      if (binding == null)
      {
         JBossXBNoSchemaBuilder builder = new JBossXBNoSchemaBuilder(classInfo);
         binding = builder.build();
         classInfo.setAttachment(SchemaBinding.class.getName(), binding);
      }
      return binding;
   }
   
   /**
    * Initialize the schema binding from the root
    * 
    * @param schemaBinding the schema binding
    * @param classInfo the classInfo
    */
   protected static void initSchema(SchemaBinding schemaBinding, ClassInfo classInfo)
   {
      // Look for a schema attribute on either the root or the root's package
      JBossXmlSchema schema = classInfo.getUnderlyingAnnotation(JBossXmlSchema.class);
      PackageInfo packageInfo = classInfo.getPackage();
      if (schema == null && packageInfo != null)
      {
         schema = packageInfo.getUnderlyingAnnotation(JBossXmlSchema.class);
      }
      
      // Use the root's package name
      String packageName = null;
      if (packageInfo != null)
         packageName = packageInfo.getName();
      // Look for annotation override
      if (schema != null)
      {
         String schemaPackageName = schema.packageName();
         if (JBossXmlConstants.DEFAULT.equals(schemaPackageName) == false)
            packageName = schemaPackageName;
         
         if(schema.xmlns().length > 0)
         {
            for(int i = 0; i < schema.xmlns().length; ++i)
            {
               schemaBinding.addPrefixMapping(schema.xmlns()[i].prefix(), schema.xmlns()[i].namespaceURI());
            }
         }
      }

      // Set the default namespace, if there are none already
      if (schemaBinding.getNamespaces().isEmpty())
      {
         String namespace = XMLConstants.NULL_NS_URI;
         if (schema != null)
         {
            String schemaNamespace = schema.namespace();
            if (JBossXmlConstants.DEFAULT.equals(schemaNamespace) == false)
               namespace = schemaNamespace;
         }
         schemaBinding.setNamespaces(new HashSet<String>(Collections.singleton(namespace)));
      }
      
      // Apply the package name
      if (packageName != null)
      {
         PackageMetaData packageMetaData = new PackageMetaData();
         packageMetaData.setName(packageName);
         schemaBinding.setPackageMetaData(packageMetaData);
      }

      // Nothing more to do if no annotation
      if (schema == null)
         return;
      
      // Apply the annotation values
      schemaBinding.setIgnoreUnresolvedFieldOrClass(schema.ignoreUnresolvedFieldOrClass());
      schemaBinding.setIgnoreLowLine(schema.ignoreLowLine());
      schemaBinding.setReplacePropertyRefs(schema.replacePropertyRefs());
      schemaBinding.setStrictSchema(schema.strict());
   }

   /**
    * Generate an xml name from a clazz name
    * 
    * @param typeInfo the typeInfo
    * @param ignoreLowerLine whether to ignore the lower line
    * @return the xml name
    */
   public static String generateXMLNameFromClassName(TypeInfo typeInfo, boolean ignoreLowerLine)
   {
      return generateXMLNameFromJavaName(typeInfo.getSimpleName(), true, ignoreLowerLine);
   }
   
   /**
    * Generate an xml name from a java name
    * 
    * @param string the java name
    * @param dash whether to insert dashes to seperate words
    * @param ignoreLowerLine TODO ignore the lower line
    * @return the xml name
    */
   public static String generateXMLNameFromJavaName(String string, boolean dash, boolean ignoreLowerLine)
   {
      // Whether we have seen a lower case character
      boolean seenLower = false;

      // Whether this is the first character
      boolean first = true;
      
      StringBuilder result = new StringBuilder(string.length());
      for (int i = 0; i < string.length(); ++i)
      {
         char c = string.charAt(i);
         // Lowercase until we see an uppercase character (but always on the first character)
         if (first || seenLower == false && Character.isUpperCase(c))
         {
            result.append(Character.toLowerCase(c));
            first = false;
         }
         // Insert the dash and start the next word with lowercase
         // but only if we have seen a lower case character
         else if (seenLower && Character.isUpperCase(c) && dash)
         {
            result.append('-');
            result.append(Character.toLowerCase(c));
            seenLower = false;
         }
         // Just append the character
         else
         {
            result.append(c);
            seenLower = true;
         }
      }
      return result.toString();
   }
}
