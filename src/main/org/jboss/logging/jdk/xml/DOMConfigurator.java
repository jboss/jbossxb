/*
 * Copyright 1999-2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.logging.jdk.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.ErrorManager;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.Formatter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;

import org.jboss.util.propertyeditor.PropertyEditors;
import org.jboss.util.StringPropertyReplacer;
import org.jboss.util.xml.JBossEntityResolver;
import org.jboss.logging.jdk.handlers.HandlerSkeleton;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

// Contributors:   Mark Womack
//                 Arun Katkere 

/**
 * Use this class to initialize the log4j environment using a DOM tree.
 * <p/>
 * <p>The DTD is specified in <a
 * href="log4j.dtd"><b>log4j.dtd</b></a>.
 * <p/>
 * <p>Sometimes it is useful to see how log4j is reading configuration
 * files. You can enable log4j internal logging by defining the
 * <b>log4j.debug</b> variable on the java command
 * line. Alternatively, set the <code>debug</code> attribute in the
 * <code>jdk:configuration</code> element. As in
 * <pre>
 * &lt;log4j:configuration <b>debug="true"</b> xmlns:log4j="http://jakarta.apache.org/log4j/">
 * ...
 * &lt;/log4j:configuration>
 * </pre>
 * <p/>
 * <p>There are sample XML files included in the package.
 *
 * @author Christopher Taylor
 * @author Ceki G&uuml;lc&uuml;
 * @author Anders Kristensen
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
public class DOMConfigurator
{

   static final String CONFIGURATION_TAG = "jdk:configuration";
   static final String OLD_CONFIGURATION_TAG = "configuration";
   static final String RENDERER_TAG = "renderer";
   static final String APPENDER_TAG = "appender";
   static final String APPENDER_REF_TAG = "appender-ref";
   static final String PARAM_TAG = "param";
   static final String LAYOUT_TAG = "layout";
   static final String CATEGORY = "category";
   static final String LOGGER = "logger";
   static final String LOGGER_REF = "logger-ref";
   static final String CATEGORY_FACTORY_TAG = "categoryFactory";
   static final String NAME_ATTR = "name";
   static final String CLASS_ATTR = "class";
   static final String VALUE_ATTR = "value";
   static final String ROOT_TAG = "root";
   static final String ROOT_REF = "root-ref";
   static final String LEVEL_TAG = "level";
   static final String PRIORITY_TAG = "priority";
   static final String FILTER_TAG = "filter";
   static final String ERROR_HANDLER_TAG = "errorHandler";
   static final String REF_ATTR = "ref";
   static final String ADDITIVITY_ATTR = "additivity";
   static final String THRESHOLD_ATTR = "threshold";
   static final String CONFIG_DEBUG_ATTR = "configDebug";
   static final String INTERNAL_DEBUG_ATTR = "debug";
   static final String RENDERING_CLASS_ATTR = "renderingClass";
   static final String RENDERED_CLASS_ATTR = "renderedClass";

   static final String EMPTY_STR = "";
   static final Class[] ONE_STRING_PARAM = new Class[]{String.class};

   final static String dbfKey = "javax.xml.parsers.DocumentBuilderFactory";


   // key: appenderName, value: appender
   private Hashtable appenderBag;
   private ErrorManager errorLog;
   private Properties props;
   private LogManager repository;
   private boolean debug;

   /**
    * Configure jdk using a <code>configuration</code> element as
    * defined in the jdk.dtd.
    */
   static public void configure(Element element)
   {
      DOMConfigurator configurator = new DOMConfigurator();
      configurator.doConfigure(element, LogManager.getLogManager());
   }

   /**
    * A static version of {@link #doConfigure(String, LogManager)}.
    */
   static public void configure(String filename)
      throws FactoryConfigurationError
   {
      new DOMConfigurator().doConfigure(filename,
         LogManager.getLogManager());
   }

   /**
    * A static version of {@link #doConfigure(java.net.URL, LogManager)}.
    */
   static public void configure(URL url)
      throws FactoryConfigurationError
   {
      new DOMConfigurator().doConfigure(url, LogManager.getLogManager());
   }

   /**
    * A static version of {@link #doConfigure(java.net.URL, LogManager)}.
    */
   static public void configure(InputStream is)
      throws FactoryConfigurationError
   {
      new DOMConfigurator().doConfigure(is, LogManager.getLogManager());
   }

   /**
    * No argument constructor.
    */
   public DOMConfigurator()
   {
      this(new ErrorManager());
   }

   public DOMConfigurator(ErrorManager errorLog)
   {
      appenderBag = new Hashtable();
      this.errorLog = errorLog;
   }

   /**
    * Used internally to parse appenders by IDREF name.
    */
   protected Handler findHandlerByName(Document doc, String appenderName)
   {
      Handler appender = (Handler) appenderBag.get(appenderName);

      if (appender != null)
      {
         return appender;
      }
      else
      {
         // Doesn't work on DOM Level 1 :
         Element element = doc.getElementById(appenderName);
         if (element == null)
         {
            errorLog.error("No appender named [" + appenderName + "] could be found.",
               null, ErrorManager.GENERIC_FAILURE);
            return null;
         }
         else
         {
            appender = parseHandler(element);
            appenderBag.put(appenderName, appender);
            return appender;
         }
      }
   }

   /**
    * Used internally to parse appenders by IDREF element.
    */
   protected Handler findHandlerByReference(Element appenderRef)
   {
      String appenderName = subst(appenderRef.getAttribute(REF_ATTR));
      Document doc = appenderRef.getOwnerDocument();
      return findHandlerByName(doc, appenderName);
   }

   /**
    * Used internally to parse an appender element.
    */
   protected Handler parseHandler(Element appenderElement)
   {
      String className = subst(appenderElement.getAttribute(CLASS_ATTR));
      debug("Class name: [" + className + ']');
      try
      {
         Object instance = instantiateByClassName(className, Handler.class, null);
         Handler appender = (Handler) instance;
         Properties beanProps = new Properties();
         String name = subst(appenderElement.getAttribute(NAME_ATTR));
         HandlerSkeleton handlerSkeleton = null;
         if( appender instanceof HandlerSkeleton )
         {
            handlerSkeleton = (HandlerSkeleton) appender;
            handlerSkeleton.setName(name);
         }

         NodeList children = appenderElement.getChildNodes();
         final int length = children.getLength();

         for (int loop = 0; loop < length; loop++)
         {
            Node currentNode = children.item(loop);

            /* We're only interested in Elements */
            if (currentNode.getNodeType() == Node.ELEMENT_NODE)
            {
               Element currentElement = (Element) currentNode;

               // Parse appender parameters 
               if (currentElement.getTagName().equals(PARAM_TAG))
               {
                  setParameter(currentElement, beanProps);
               }
               // Set appender layout
               else if (currentElement.getTagName().equals(LAYOUT_TAG))
               {
                  Formatter format = parseLayout(currentElement);
                  appender.setFormatter(format);
               }
               // Add filters
               else if (currentElement.getTagName().equals(FILTER_TAG))
               {
                  parseFilters(currentElement, appender);
               }
               else if (currentElement.getTagName().equals(ERROR_HANDLER_TAG))
               {
                  parseErrorManager(currentElement, appender);
               }
               else if (currentElement.getTagName().equals(APPENDER_REF_TAG))
               {
                  String refName = subst(currentElement.getAttribute(REF_ATTR));
                  errorLog.error("Requesting attachment of handler named [" +
                     refName + "] to handler named [" + appender +
                     "] which does not implement org.apache.jdk.spi.HandlerAttachable.",
                     null, ErrorManager.GENERIC_FAILURE);
               }
            }
         }
         PropertyEditors.mapJavaBeanProperties(appender, beanProps);
         if( handlerSkeleton != null )
            handlerSkeleton.activateOptions();
         return appender;
      }
      /* Yes, it's ugly, but all of these exceptions point to the same
      problem: we can't create an Handler
      */
      catch (Exception oops)
      {
         errorLog.error("Could not create an Handler. Reported error follows.",
            oops, ErrorManager.GENERIC_FAILURE);
         return null;
      }
   }

   /**
    * Used internally to parse an {@link ErrorManager} element.
    */
   protected void parseErrorManager(Element element, Handler appender)
      throws Exception
   {
      String className = subst(element.getAttribute(CLASS_ATTR));
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      Class ehClazz = loader.loadClass(className);
      ErrorManager eh = (ErrorManager) ehClazz.newInstance();
      appender.setErrorManager(eh);
   }

   /**
    * Used internally to parse a filter element.
    */
   protected void parseFilters(Element element, Handler appender)
      throws Exception
   {
      String clazz = subst(element.getAttribute(CLASS_ATTR));
      Filter filter = (Filter) instantiateByClassName(clazz,
         Filter.class, null);

      if (filter != null)
      {
         Properties beanProps = new Properties();
         NodeList children = element.getChildNodes();
         final int length = children.getLength();

         for (int loop = 0; loop < length; loop++)
         {
            Node currentNode = children.item(loop);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE)
            {
               Element currentElement = (Element) currentNode;
               String tagName = currentElement.getTagName();
               if (tagName.equals(PARAM_TAG))
               {
                  setParameter(currentElement, beanProps);
               }
            }
         }
         PropertyEditors.mapJavaBeanProperties(filter, beanProps);
         debug("Setting filter of type [" + filter.getClass()
            + "] to appender named [" + appender + "].");
         appender.setFilter(filter);
      }
   }

   /**
    * Used internally to parse an category element.
    */
   protected void parseCategory(Element loggerElement)
      throws Exception
   {
      // Create a new org.apache.jdk.Category object from the <category> element.
      String catName = subst(loggerElement.getAttribute(NAME_ATTR));

      Logger logger;

      String className = subst(loggerElement.getAttribute(CLASS_ATTR));


      if (EMPTY_STR.equals(className))
      {
         debug("Retreiving an instance of java.util.logging.Logger.");
         logger = repository.getLogger(catName);
         if( logger == null )
         {
            logger = Logger.getLogger(catName);
            repository.addLogger(logger);
         }
      }
      else
      {
         debug("Desired logger sub-class: [" + className + ']');
         try
         {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            Class c = loader.loadClass(className);
            Class[] sig = {String.class, String.class};
            Constructor ctor = c.getConstructor(sig);
            Object[] args =  {catName, null};
            logger = (Logger) ctor.newInstance(args);
         }
         catch (Exception oops)
         {
            errorLog.error("Could not retrieve category [" + catName +
               "]. Reported error follows.", oops, ErrorManager.GENERIC_FAILURE);
            return;
         }
      }

      // Setting up a category needs to be an atomic operation, in order
      // to protect potential log operations while category
      // configuration is in progress.
      synchronized (logger)
      {
         String flag = subst(loggerElement.getAttribute(ADDITIVITY_ATTR));
         boolean additivity = Boolean.valueOf(flag).booleanValue();
         debug("Setting [" + logger.getName() + "] additivity to [" + additivity + "].");
         logger.setUseParentHandlers(additivity);
         parseChildrenOfLoggerElement(loggerElement, logger, false);
      }
   }


   /**
    * Used internally to parse the category factory element.
    */
   protected void parseCategoryFactory(Element factoryElement)
      throws Exception
   {
      String className = subst(factoryElement.getAttribute(CLASS_ATTR));

      if (EMPTY_STR.equals(className))
      {
         errorLog.error("Category Factory tag " + CLASS_ATTR + " attribute not found.",
            null, ErrorManager.GENERIC_FAILURE);
      }
      else
      {
         debug("Desired category factory: [" + className + ']');
         Object catFactory = instantiateByClassName(className,
            Object.class,
            null);
         Properties beanProps = new Properties();

         Element currentElement = null;
         Node currentNode = null;
         NodeList children = factoryElement.getChildNodes();
         final int length = children.getLength();

         for (int loop = 0; loop < length; loop++)
         {
            currentNode = children.item(loop);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE)
            {
               currentElement = (Element) currentNode;
               if (currentElement.getTagName().equals(PARAM_TAG))
               {
                  setParameter(currentElement, beanProps);
               }
            }
         }
         PropertyEditors.mapJavaBeanProperties(catFactory, beanProps);
      }
   }


   /**
    * Used internally to parse the root category element.
    */
   protected void parseRoot(Element rootElement)
      throws Exception
   {
      Logger root = repository.getLogger("");
      if( root == null )
      {
         root = Logger.getLogger("");
         repository.addLogger(root);
      }
      // category configuration needs to be atomic
      synchronized (root)
      {
         parseChildrenOfLoggerElement(rootElement, root, true);
      }
   }


   /**
    * Used internally to parse the children of a category element.
    */
   protected void parseChildrenOfLoggerElement(Element catElement,
      Logger logger, boolean isRoot)
      throws Exception
   {
      Properties beanProps = new Properties();

      // Remove all existing appenders from logger. They will be
      // reconstructed if need be.
      Handler[] handlers = logger.getHandlers();
      for(int n = 0; n < handlers.length; n ++)
      {
         Handler h = handlers[n];
         logger.removeHandler(h);
      }

      NodeList children = catElement.getChildNodes();
      final int length = children.getLength();

      for (int loop = 0; loop < length; loop++)
      {
         Node currentNode = children.item(loop);

         if (currentNode.getNodeType() == Node.ELEMENT_NODE)
         {
            Element currentElement = (Element) currentNode;
            String tagName = currentElement.getTagName();

            if (tagName.equals(APPENDER_REF_TAG))
            {
               Element appenderRef = (Element) currentNode;
               Handler appender = findHandlerByReference(appenderRef);
               String refName = subst(appenderRef.getAttribute(REF_ATTR));
               if (appender != null)
                  debug("Adding appender named [" + refName +
                     "] to category [" + logger.getName() + "].");
               else
                  debug("Handler named [" + refName + "] not found.");

               logger.addHandler(appender);

            }
            else if (tagName.equals(LEVEL_TAG))
            {
               parseLevel(currentElement, logger, isRoot);
            }
            else if (tagName.equals(PRIORITY_TAG))
            {
               parseLevel(currentElement, logger, isRoot);
            }
            else if (tagName.equals(PARAM_TAG))
            {
               setParameter(currentElement, beanProps);
            }
         }
      }
      PropertyEditors.mapJavaBeanProperties(logger, beanProps);
   }

   /**
    * Used internally to parse a layout element.
    */
   protected Formatter parseLayout(Element layout_element)
   {
      String className = subst(layout_element.getAttribute(CLASS_ATTR));
      debug("Parsing layout of class: \"" + className + "\"");
      try
      {
         Object instance = instantiateByClassName(className, Formatter.class, null);
         Formatter layout = (Formatter) instance;
         Properties beanProps = new Properties();

         NodeList params = layout_element.getChildNodes();
         final int length = params.getLength();

         for (int loop = 0; loop < length; loop++)
         {
            Node currentNode = params.item(loop);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE)
            {
               Element currentElement = (Element) currentNode;
               String tagName = currentElement.getTagName();
               if (tagName.equals(PARAM_TAG))
               {
                  setParameter(currentElement, beanProps);
               }
            }
         }
         PropertyEditors.mapJavaBeanProperties(layout, beanProps);
         return layout;
      }
      catch (Exception oops)
      {
         errorLog.error("Could not create the Layout. Reported error follows.",
            oops, ErrorManager.GENERIC_FAILURE);
         return null;
      }
   }

   protected void parseRenderer(Element element)
   {
      String renderingClass = subst(element.getAttribute(RENDERING_CLASS_ATTR));
      String renderedClass = subst(element.getAttribute(RENDERED_CLASS_ATTR));
   }

   /**
    * Used internally to parse a level  element.
    */
   protected void parseLevel(Element element, Logger logger, boolean isRoot)
   {
      String catName = logger.getName();
      if (isRoot)
      {
         catName = "root";
      }

      String levelName = subst(element.getAttribute(VALUE_ATTR));
      // Check for a jdk level name
      levelName = mapLog4jLevel(levelName);
      debug("Level value for " + catName + " is  [" + levelName + "].");

      if ("INHERITED".equalsIgnoreCase(levelName) || "NULL".equalsIgnoreCase(levelName))
      {
         if (isRoot)
         {
            errorLog.error("Root level cannot be inherited. Ignoring directive.",
               null, ErrorManager.GENERIC_FAILURE);
         }
         else
         {
            logger.setLevel(null);
         }
      }
      else
      {
         String className = subst(element.getAttribute(CLASS_ATTR));
         if (EMPTY_STR.equals(className))
         {
            Level level =  Level.parse(levelName);
            logger.setLevel(level);
         }
         else if( className.equals("org.jboss.logging.XLevel") )
         {
            // Special handling of the jboss XLevel
            logger.setLevel(Level.FINER);
         }
         else
         {
            debug("Desired Level sub-class: [" + className + ']');
            try
            {
               ClassLoader loader = Thread.currentThread().getContextClassLoader();
               Class clazz = loader.loadClass(className);
               Class[] sig = {String.class, int.class};
               Object[] args = {levelName, new Integer(Level.FINEST.intValue())};
               Constructor ctor = clazz.getConstructor(sig);
               Level pri = (Level) ctor.newInstance(args);
               logger.setLevel(pri);
            }
            catch (Exception oops)
            {
               errorLog.error("Could not create level [" + levelName +
                  "]. Reported error follows.", oops, ErrorManager.GENERIC_FAILURE);
               return;
            }
         }
      }
      debug(catName + " level set to " + logger.getLevel());
   }

   protected void setParameter(Element elem, Properties beanProps)
   {
      String name = subst(elem.getAttribute(NAME_ATTR));
      String value = (elem.getAttribute(VALUE_ATTR));
      value = subst(convertSpecialChars(value));
      beanProps.setProperty(name, value);
   }


   private interface ParseAction
   {
      Document parse(final DocumentBuilder parser) throws SAXException, IOException;
   }


   public void doConfigure(final String filename, LogManager repository)
   {
      ParseAction action = new ParseAction()
      {
         public Document parse(final DocumentBuilder parser) throws SAXException, IOException
         {
            return parser.parse(new File(filename));
         }

         public String toString()
         {
            return "file [" + filename + "]";
         }
      };
      doConfigure(action, repository);
   }


   public void doConfigure(final URL url, LogManager repository)
   {
      ParseAction action = new ParseAction()
      {
         public Document parse(final DocumentBuilder parser) throws SAXException, IOException
         {
            return parser.parse(url.toString());
         }

         public String toString()
         {
            return "url [" + url.toString() + "]";
         }
      };
      doConfigure(action, repository);
   }

   /**
    * Configure jdk by reading in a jdk.dtd compliant XML
    * configuration file.
    */
   public void doConfigure(final InputStream inputStream, LogManager repository)
      throws FactoryConfigurationError
   {
      ParseAction action = new ParseAction()
      {
         public Document parse(final DocumentBuilder parser) throws SAXException, IOException
         {
            InputSource inputSource = new InputSource(inputStream);
            inputSource.setSystemId("dummy://jdk.dtd");
            return parser.parse(inputSource);
         }

         public String toString()
         {
            return "input stream [" + inputStream.toString() + "]";
         }
      };
      doConfigure(action, repository);
   }

   /**
    * Configure jdk by reading in a jdk.dtd compliant XML
    * configuration file.
    */
   public void doConfigure(final Reader reader, LogManager repository)
      throws FactoryConfigurationError
   {
      ParseAction action = new ParseAction()
      {
         public Document parse(final DocumentBuilder parser) throws SAXException, IOException
         {
            InputSource inputSource = new InputSource(reader);
            inputSource.setSystemId("dummy://jdk.dtd");
            return parser.parse(inputSource);
         }

         public String toString()
         {
            return "reader [" + reader.toString() + "]";
         }
      };
      doConfigure(action, repository);
   }

   /**
    * Configure jdk by reading in a jdk.dtd compliant XML
    * configuration file.
    */
   protected void doConfigure(final InputSource inputSource, LogManager repository)
      throws FactoryConfigurationError
   {
      if (inputSource.getSystemId() == null)
      {
         inputSource.setSystemId("dummy://jdk.dtd");
      }
      ParseAction action = new ParseAction()
      {
         public Document parse(final DocumentBuilder parser) throws SAXException, IOException
         {
            return parser.parse(inputSource);
         }

         public String toString()
         {
            return "input source [" + inputSource.toString() + "]";
         }
      };
      doConfigure(action, repository);
   }


   private final void doConfigure(final ParseAction action, final LogManager repository)
      throws FactoryConfigurationError
   {
      DocumentBuilderFactory dbf = null;
      this.repository = repository;
      try
      {
         dbf = DocumentBuilderFactory.newInstance();
         debug("Standard DocumentBuilderFactory search succeded.");
         debug("DocumentBuilderFactory is: " + dbf.getClass().getName());
      }
      catch (FactoryConfigurationError fce)
      {
         Exception e = fce.getException();
         errorLog.error("Could not instantiate a DocumentBuilderFactory.", e, ErrorManager.GENERIC_FAILURE);
         throw fce;
      }

      try
      {
         dbf.setValidating(true);

         DocumentBuilder docBuilder = dbf.newDocumentBuilder();
         JBossEntityResolver resolver = new JBossEntityResolver();
         resolver.registerLocalEntity("urn:jboss:jdklogger.dtd", "jdklogger.dtd");
         docBuilder.setEntityResolver(resolver);

         Document doc = action.parse(docBuilder);
         parse(doc.getDocumentElement());
      }
      catch (Exception e)
      {
         // I know this is miserable...
         errorLog.error("Could not parse " + action.toString() + ".", e, ErrorManager.GENERIC_FAILURE);
      }
   }

   /**
    * Configure by taking in an DOM element.
    */
   public void doConfigure(Element element, LogManager repository)
   {
      this.repository = repository;
      parse(element);
   }

   /**
    * Used internally to configure the jdk framework by parsing a DOM
    * tree of XML elements based on <a
    * href="doc-files/jdk.dtd">jdk.dtd</a>.
    */
   protected void parse(Element element)
   {

      String rootElementName = element.getTagName();

      if (!rootElementName.equals(CONFIGURATION_TAG))
      {
         if (rootElementName.equals(OLD_CONFIGURATION_TAG))
         {
            errorLog.error("The <" + OLD_CONFIGURATION_TAG +
               "> element has been deprecated."
               + ", use the <" + CONFIGURATION_TAG + "> element instead.", null,
               ErrorManager.GENERIC_FAILURE);
         }
         else
         {
            errorLog.error("DOM element is - not a <" + CONFIGURATION_TAG + "> element.", null,
               ErrorManager.GENERIC_FAILURE);
            return;
         }
      }

      String debugAttrib = subst(element.getAttribute(INTERNAL_DEBUG_ATTR));

      debug("debug attribute= '" + debugAttrib + "'.");
      // if the jdk.dtd is not specified in the XML file, then the
      // "debug" attribute is returned as the empty string.
      if (!debugAttrib.equals("") && !debugAttrib.equals("null"))
      {
         debug = Boolean.valueOf(debugAttrib).booleanValue();
      }
      else
      {
         debug("Ignoring " + INTERNAL_DEBUG_ATTR + " attribute.");
      }


      String confDebug = subst(element.getAttribute(CONFIG_DEBUG_ATTR));
      if (!confDebug.equals("") && !confDebug.equals("null"))
      {
         debug = true;
      }

      String thresholdStr = subst(element.getAttribute(THRESHOLD_ATTR));
      debug("Threshold ='" + thresholdStr + "'.");
      if (!"".equals(thresholdStr) && !"null".equals(thresholdStr))
      {
         Level threshold = Level.parse(thresholdStr);
         Logger root = repository.getLogger("");
         root.setLevel(threshold);
      }

      // First configure each category factory under the root element.
      // Category factories need to be configured before any of
      // categories they support.
      //
      String tagName = null;
      Element currentElement = null;
      Node currentNode = null;
      NodeList children = element.getChildNodes();
      final int length = children.getLength();

      for (int loop = 0; loop < length; loop++)
      {
         currentNode = children.item(loop);
         if (currentNode.getNodeType() == Node.ELEMENT_NODE)
         {
            currentElement = (Element) currentNode;
            tagName = currentElement.getTagName();

            if (tagName.equals(CATEGORY_FACTORY_TAG))
            {
               try
               {
                  parseCategoryFactory(currentElement);
               }
               catch(Exception e)
               {
                  errorLog.error("Failed to parse: "+tagName, e, ErrorManager.GENERIC_FAILURE);
               }
            }
         }
      }

      for (int loop = 0; loop < length; loop++)
      {
         currentNode = children.item(loop);
         if (currentNode.getNodeType() == Node.ELEMENT_NODE)
         {
            currentElement = (Element) currentNode;
            tagName = currentElement.getTagName();

            try
            {
               if (tagName.equals(CATEGORY) || tagName.equals(LOGGER))
               {
                  parseCategory(currentElement);
               }
               else if (tagName.equals(ROOT_TAG))
               {
                  parseRoot(currentElement);
               }
               else if (tagName.equals(RENDERER_TAG))
               {
                  parseRenderer(currentElement);
               }
            }
            catch(Exception e)
            {
               errorLog.error("Failed to parse element: "+tagName, e, ErrorManager.GENERIC_FAILURE);
            }
         }
      }
   }


   protected String subst(String value)
   {
      if( value == null )
         return null;

      try
      {
         return StringPropertyReplacer.replaceProperties(value);
      }
      catch (Exception e)
      {
         errorLog.error("Could not perform variable substitution.", e, ErrorManager.GENERIC_FAILURE);
         return value;
      }
   }

   static String mapLog4jLevel(String name)
   {
      String jdkName = null;
      if( name.equals("OFF") )
         jdkName = Level.OFF.getName();
      else if( name.equals("FATAL") )
         jdkName = Level.SEVERE.getName();
      else if( name.equals("ERROR") )
         jdkName = Level.WARNING.getName();
      else if( name.equals("ERROR") )
         jdkName = Level.WARNING.getName();
      else if( name.equals("WARN") )
         jdkName = Level.WARNING.getName();
      else if( name.equals("INFO") )
         jdkName = Level.INFO.getName();
      else if( name.equals("DEBUG") )
         jdkName = Level.FINE.getName();
      else if( name.equals("TRACE") )
         jdkName = Level.FINER.getName();
      return jdkName;
   }

   static String convertSpecialChars(String s)
   {
     char c;
     int len = s.length();
     StringBuffer sbuf = new StringBuffer(len);

     int i = 0;
     while(i < len) {
       c = s.charAt(i++);
       if (c == '\\') {
    c =  s.charAt(i++);
    if(c == 'n')      c = '\n';
    else if(c == 'r') c = '\r';
    else if(c == 't') c = '\t';
    else if(c == 'f') c = '\f';
    else if(c == '\b') c = '\b';
    else if(c == '\"') c = '\"';
    else if(c == '\'') c = '\'';
    else if(c == '\\') c = '\\';
       }
       sbuf.append(c);
     }
     return sbuf.toString();
   }

   protected void debug(String msg)
   {
      if( debug )
         System.out.println(msg);
   }
   Object instantiateByClassName(String className, Class superClass,
      Object defaultValue)
   {
      if (className != null)
      {
         try
         {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            Class classObj = loader.loadClass(className);
            if (!superClass.isAssignableFrom(classObj))
            {
               errorLog.error("A \"" + className + "\" object is not assignable to a \"" +
                  superClass.getName() + "\" variable."
                  +"The class \"" + superClass.getName() + "\" was loaded by "
               +"[" + superClass.getClassLoader() + "] whereas object of type "
               +"'" + classObj.getName() + "\" was loaded by ["
                  + classObj.getClassLoader() + "].", null, ErrorManager.GENERIC_FAILURE);
               return defaultValue;
            }
            return classObj.newInstance();
         }
         catch (Exception e)
         {
            errorLog.error("Could not instantiate class [" + className + "].", e, ErrorManager.GENERIC_FAILURE);
         }
      }
      return defaultValue;
   }

}
