/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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

// $Id: $

import org.jboss.logging.Logger;
import org.jboss.xb.annotations.JBossXmlSchema;
import org.jboss.xb.binding.FeatureAware;
import org.jboss.xb.binding.ObjectModelFactory;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.resolver.MutableSchemaResolver;
import org.jboss.xb.binding.sunday.unmarshalling.SingletonSchemaResolverFactory;
import org.xml.sax.InputSource;

/**
 * JBossXB deployer helper.
 *
 * @param <T> the expected type
 * @author <a href="ales.justin@jboss.com">Ales Justin</a>
 * @author <a href="thomas.diesler@jboss.com">Thomas Diesler</a>
 */
public class JBossXBHelper<T> implements FeatureAware
{
   /** The log */
   private Logger log = Logger.getLogger(JBossXBHelper.class);

   /** Unmarshaller factory */
   private static final UnmarshallerFactory factory = UnmarshallerFactory.newInstance();

   /** The output */
   private Class<T> output;

   /** Whether the Unmarshaller will use schema validation */
   private boolean useSchemaValidation = true;

   /** Whether to validate */
   private boolean useValidation = true;

   /**
    * Create a new SchemaResolverDeployer.
    *
    * @param output the output
    * @throws IllegalArgumentException for a null output
    */
   public JBossXBHelper(Class<T> output)
   {
      if (output == null)
         throw new IllegalArgumentException("Null output.");
      this.output = output;
   }

   public void setFeature(String featureName, boolean flag) throws Exception
   {
      factory.setFeature(featureName, flag);
   }

   /**
    * Get the useSchemaValidation.
    *
    * @return the useSchemaValidation.
    */
   public boolean isUseSchemaValidation()
   {
      return useSchemaValidation;
   }

   /**
    * Set the useSchemaValidation.
    *
    * @param useSchemaValidation the useSchemaValidation.
    */
   public void setUseSchemaValidation(boolean useSchemaValidation)
   {
      this.useSchemaValidation = useSchemaValidation;
   }

   /**
    * Get the useValidation.
    *
    * @return the useValidation.
    */
   public boolean isUseValidation()
   {
      return useValidation;
   }

   /**
    * Set the useValidation.
    *
    * @param useValidation the useValidation.
    */
   public void setUseValidation(boolean useValidation)
   {
      this.useValidation = useValidation;
   }

   /**
    * Add class binding.
    *
    * @param namespace the namespace
    * @param metadata the metadata
    * @deprecated Use {@link #addTypeBinding(String, Class)}
    */
   public static void addClassBinding(String namespace, Class<?> metadata)
   {
      SingletonSchemaResolverFactory factory = SingletonSchemaResolverFactory.getInstance();
      MutableSchemaResolver resolver = factory.getSchemaBindingResolver();
      resolver.mapURIToClass(namespace, metadata);
   }

   /**
    * Remove class binding.
    *
    * @param namespace the namespace
    * @deprecated Use {@link #removeTypeBinding(String)}
    */
   public static void removeClassBinding(String namespace)
   {
      SingletonSchemaResolverFactory factory = SingletonSchemaResolverFactory.getInstance();
      MutableSchemaResolver resolver = factory.getSchemaBindingResolver();
      resolver.removeURIToClassMapping(namespace);
   }

   /**
    * Add class binding.
    *
    * @param namespace the namespace
    * @param metadata the metadata
    */
   public void addTypeBinding(String namespace, Class<?> metadata)
   {
      getResolver().mapURIToClass(namespace, metadata);
   }

   /**
    * Remove class binding.
    *
    * @param namespace the namespace
    */
   public void removeTypeBinding(String namespace)
   {
      getResolver().removeURIToClassMapping(namespace);
   }

   /**
    * Get the schema resolver.
    * 
    * This implementation returns a singleton schema resolver.
    *
    * @return the resolver
    */
   protected MutableSchemaResolver getResolver()
   {
      SingletonSchemaResolverFactory factory = SingletonSchemaResolverFactory.getInstance();
      return factory.getSchemaBindingResolver();
   }

   /**
    * Find the namespace on class/package
    *
    * @param metadata the metadata class
    * @return jboss xml schema namespace
    */
   public static String findNamespace(Class<?> metadata)
   {
      JBossXmlSchema jBossXmlSchema = metadata.getAnnotation(JBossXmlSchema.class);
      if (jBossXmlSchema == null)
      {
         Package pckg = metadata.getPackage();
         if (pckg != null)
            jBossXmlSchema = pckg.getAnnotation(JBossXmlSchema.class);
      }
      return jBossXmlSchema != null ? jBossXmlSchema.namespace() : null;
   }

   /**
    * Parse file to output metadata.
    *
    * @param source the source to parse
    * @return new metadata instance
    * @throws Exception for any error
    */
   public T parse(InputSource source) throws Exception
   {
      return parse(output, source);
   }

   /**
    * Parse the file to create metadata instance.
    *
    * @param <U> the expect type
    * @param expectedType the expected type
    * @param source the source
    * @return new metadata instance
    * @throws Exception for any error
    */
   public <U> U parse(Class<U> expectedType, InputSource source) throws Exception
   {
      if (expectedType == null)
         throw new IllegalArgumentException("Null expected type");
      if (source == null)
         throw new IllegalArgumentException("Null source");

      boolean trace = log.isTraceEnabled();
      if(trace)
         log.trace("Parsing file: " + source + " for type: " + expectedType);
      Unmarshaller unmarshaller = factory.newUnmarshaller();
      unmarshaller.setSchemaValidation(isUseSchemaValidation());
      unmarshaller.setValidation(isUseValidation());
      Object parsed = unmarshaller.unmarshal(source, getResolver());
      if (parsed == null)
         throw new Exception("Failed to resolve Java binding for " + source + " (check the SchemaBinding resolver configuration)");

      if(trace)
         log.trace("Parsed file: " + source + " to: " + parsed);
      return expectedType.cast(parsed);
   }

   /**
    * Parse the file using object model factory.
    *
    * @param source the source to parse
    * @param root the previous root
    * @param omf the object model factory
    * @return new metadata instance
    * @throws Exception for any error
    */
   public T parse(InputSource source, T root, ObjectModelFactory omf) throws Exception
   {
      return parse(output, source, root, omf);
   }

   /**
    * Parse the file using object model factory.
    *
    * @param <U> the expect type
    * @param expectedType the expected type
    * @param source the source to parse
    * @param root the previous root
    * @param omf the object model factory
    * @return new metadata instance
    * @throws Exception for any error
    */
   public <U> U parse(Class<U> expectedType, InputSource source, U root, ObjectModelFactory omf) throws Exception
   {
      if (source == null)
         throw new IllegalArgumentException("Null source");

      boolean trace = log.isTraceEnabled();
      if(trace)
         log.trace("Parsing source: " + source + " for deploymentType: " + expectedType);

      Unmarshaller unmarshaller = factory.newUnmarshaller();
      unmarshaller.setSchemaValidation(isUseSchemaValidation());
      unmarshaller.setValidation(isUseValidation());
      Object parsed = unmarshaller.unmarshal(source, omf, root);
      if (parsed == null)
         throw new Exception("Failed to resolve Java binding for " + source);

      if(trace)
         log.trace("Parsed file: " + source + " to: "+parsed);
      return expectedType.cast(parsed);
   }
}