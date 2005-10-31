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
package org.jboss.logging.jdk;

import java.util.logging.LogManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.net.URL;
import org.jboss.logging.jdk.xml.DOMConfigurator;

/**
 @author Scott.Stark@jboss.org
 @version $Revision$
 */
public class JDKLogManager extends LogManager
{
   private static final String DEFAULT_CONFIG_PROPS = "jdklogger.properties";
   private static final String DEFAULT_CONFIG_XML = "jdklogger.xml";

   public JDKLogManager()
   {
   }

   /**
    * Overriden to attempt to load the java.util.logging.config.file property value
    * as a classpath resource before treating this as a file as is done by the
    * standard jdk LogManager.
    * 
    * In additional, if the resource ends in a .xml suffix, the
    * org.jboss.logging.jdk.xml.DOMConfigurator is used to parse a logging
    * configuration that is similar to the 1.2 log4j.xml format. 
    * 
    * @throws IOException
    * @throws SecurityException
    */
   public void readConfiguration()
      throws IOException, SecurityException
   {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      String config = SecurityActions.getProperty("java.util.logging.config.file");
      URL configURL = null;
      if( config == null )
      {
         // Search for a default configuration on the classpath
         config = DEFAULT_CONFIG_XML;
         configURL = loader.getResource(DEFAULT_CONFIG_XML);
         if( configURL == null )
         {
            config = DEFAULT_CONFIG_PROPS;
            configURL = loader.getResource(DEFAULT_CONFIG_PROPS);
         }

         // Search for a default configuration as a file
         if( configURL == null )
         {
            config = DEFAULT_CONFIG_XML;
            File test = new File(DEFAULT_CONFIG_XML);
            if( test.exists() == true )
               configURL = test.toURL();
            else
            {
               config = DEFAULT_CONFIG_PROPS;
               test = new File(DEFAULT_CONFIG_PROPS);
               if( test.exists() == true )
                  configURL = test.toURL();
            }
            // If there still is no file, throw an exception
            if( configURL == null )
            {
               String msg = "No java.util.logging.config.file specified, and neither the default "
                  + DEFAULT_CONFIG_XML + " or " + DEFAULT_CONFIG_PROPS + " was found";
               throw new FileNotFoundException(msg);
            }
         }
      }

      // If there was a config specified, try to load it from the classpath
      if( configURL == null )
         configURL = loader.getResource(config);
      InputStream is = null;
      if( configURL == null )
      {
         // If the config was not on the classpath try it as a file
         InputStream in = new FileInputStream(config);
         is = new BufferedInputStream(in);
      }
      else
      {
         // Use the located config URL
         is = configURL.openStream();
      }

      // Is this an xml file?
      boolean isXML = config.endsWith(".xml");
      try
      {
         if( isXML )
         {
            DOMConfigurator.configure(is);
         }
         else
         {
            // Parse the standard jdk properties file format
            super.readConfiguration(is);
         }
      }
      finally
      {
         if( is != null )
            is.close();
      }
   }

   /**
    * Ignore the reset operation because the default behavior by the jdk LogManager
    * is to close all handlers. This results in loss of logging information during
    * the jdk shutdown. The jboss kernel can call doReset to cause a reset of the
    * logging layer as the last step in shutdown.
    * 
    * @see #doReset() to force a reset
    */
   public void reset()
   {
   }

   /**
    * Invokes the LogManager.reset() method.
    */
   public void doReset()
   {
      super.reset();
   }
}

