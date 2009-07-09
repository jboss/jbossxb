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

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.xml.namespace.QName;

import org.apache.xerces.xs.XSModel;
import org.jboss.logging.Logger;
import org.jboss.xb.binding.Util;
import org.jboss.xb.binding.resolver.MultiClassSchemaResolver;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBindingResolver;
import org.xml.sax.InputSource;

/**
 * Mostly a holder for excluded and validated types, namespaces.
 *
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @author <a href="ales.justin@jboss.com">Ales Justin</a>
 * @version $Revision: 1.1 $
 */
public abstract class AbstractSchemaBindingValidator implements SchemaBindingValidator
{
   protected static final Logger log = Logger.getLogger(SchemaBindingValidator.class);

   protected static final QName WILDCARD = new QName("wildcard", "wildcard");

   protected Set<String> excludedNs = new HashSet<String>();
   protected Set<QName> excludedTypes = new HashSet<QName>();

   protected Set<QName> validatedTypes = new HashSet<QName>();
   protected Set<QName> validatedElements = new HashSet<QName>();

   private SchemaBindingResolver resolver;

   private boolean loggingEnabled;

   protected AbstractSchemaBindingValidator()
   {
   }

   /**
    * @param resolver  default schema resolver
    */
   protected AbstractSchemaBindingValidator(SchemaBindingResolver resolver)
   {
      this();
      this.resolver = resolver;
   }

   /**
    * Resets instance variables (such as a set of validated types, elements and also loggingEnabled property).
    * This method is required to invoked before another validation.
    * It is called internally at the end of validate(XSModel xsSchema, SchemaBinding schemaBinding).
    * NOTE: this method doesn't clear excluded namespaces and types.
    */
   public void reset()
   {
      loggingEnabled = log.isTraceEnabled();
      validatedTypes.clear();
      validatedElements.clear();
   }

   public boolean isLoggingEnabled()
   {
      return loggingEnabled;
   }

   public void enableLogging(boolean value)
   {
      loggingEnabled = value;
   }

   public void excludeNs(String ns)
   {
      excludedNs.add(ns);
   }

   public boolean isNsExcluded(String ns)
   {
      return excludedNs.contains(ns);
   }

   public void includeNs(String ns)
   {
      excludedNs.remove(ns);
   }

   public void excludeType(QName qName)
   {
      excludedTypes.add(qName);
   }

   public boolean isTypeExcluded(QName qName)
   {
      return excludedTypes.contains(qName);
   }

   public void includeType(QName qName)
   {
      excludedTypes.remove(qName);
   }

   /**
    * @return The default resolver used to resolve schemas
    */
   public SchemaBindingResolver getSchemaResolver()
   {
      return resolver;
   }

   /**
    * @param resolver  The default resolver used to resolve schemas
    */
   public void setSchemaResolver(SchemaBindingResolver resolver)
   {
      this.resolver = resolver;
   }

   public void validate(InputSource is, SchemaBinding binding)
   {
      SchemaBindingResolver resolver = binding.getSchemaResolver();
      if(resolver == null)
      {
         resolver = this.resolver;
         if(resolver == null)
            log("Schema resolver was not provided");
      }
      XSModel xsModel = Util.loadSchema(is, resolver);
      validate(xsModel, binding);
   }

   public void validate(String xsdName, Class<?>... cls)
   {
      log("validate: " + xsdName + ", " + Arrays.asList(cls));

      URL xsdUrl = Thread.currentThread().getContextClassLoader().getResource("schema/" + xsdName);
      if(xsdUrl == null)
         handleError("Failed to load schema from the classpath: schema/" + xsdName);

      MultiClassSchemaResolver multiClassResolver = new MultiClassSchemaResolver();
      multiClassResolver.mapLocationToClasses(xsdName, cls);
      SchemaBinding binding = resolver.resolve("", null, xsdName);

      SchemaBindingResolver resolver = this.resolver;
      if(resolver == null)
         resolver = multiClassResolver;

      XSModel xsModel;
      try
      {
         xsModel = Util.loadSchema(xsdUrl.openStream(), null, resolver);
      }
      catch (IOException e)
      {
         throw new IllegalStateException("Failed to read schema " + xsdName, e);
      }

      validate(xsModel, binding);
   }

   protected abstract void validate(XSModel xsSchema, SchemaBinding schemaBinding);   

   /**
    * This an error handler method. Default implementation throws IllegalStateException with the message passed in as the argument.
    *
    * @param msg  the error message
    */
   protected void handleError(String msg)
   {
      throw new IllegalStateException(msg);
   }

   /**
    * This method is supposed to log a message. Default implementation uses trace logging.
    *
    * @param msg  the message to log.
    */
   protected void log(String msg)
   {
      if(loggingEnabled)
         log.trace(msg);
   }
}