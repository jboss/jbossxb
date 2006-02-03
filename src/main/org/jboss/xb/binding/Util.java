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
package org.jboss.xb.binding;

import java.io.InputStream;
import java.io.Reader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.StringTokenizer;

import javax.xml.XMLConstants;

import org.apache.xerces.xs.XSImplementation;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.jboss.logging.Logger;
import org.jboss.util.Classes;
import org.jboss.xb.binding.sunday.unmarshalling.LSInputAdaptor;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBindingResolver;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinderTerminatingErrorHandler;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.Attributes;

/**
 * Various utilities for XML binding.
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public final class Util
{
   /**
    * Characters that are considered to be word separators while convertinging XML names to Java identifiers
    * according to JAXB 2.0 spec.
    */
   public static final char HYPHEN_MINUS = '\u002D';
   public static final char FULL_STOP = '\u002E';
   public static final char COLLON = '\u003A';
   public static final char LOW_LINE = '\u005F';
   public static final char MIDDLE_DOT = '\u00B7';
   public static final char GREEK_ANO_TELEIA = '\u0387';
   public static final char ARABIC_END_OF_AYAH = '\u06DD';
   public static final char ARABIC_START_OF_RUB_EL_HIZB = '\u06DE';

   private static final Logger log = Logger.getLogger(Util.class);

   /**
    * Converts XML name to Java class name according to
    * Binding XML Names to Java Identifiers
    * C.2. The Name to Identifier Mapping Algorithm
    * jaxb-2_0-edr-spec-10_jun_2004.pdf
    *
    * @param name          XML name
    * @param ignoreLowLine whether low lines should not be parts of Java identifiers
    * @return Java class name
    */
   public static String xmlNameToClassName(String name, boolean ignoreLowLine)
   {
      return XMLNameToJavaIdentifierConverter.PARSER.parse(XMLNameToJavaIdentifierConverter.CLASS_NAME,
         name,
         ignoreLowLine
      );
   }

   public static String xmlNameToFieldName(String name, boolean ignoreLowLine)
   {
      return XMLNameToJavaIdentifierConverter.PARSER.parse(XMLNameToJavaIdentifierConverter.FIELD_NAME,
         name,
         ignoreLowLine
      );
   }

   /**
    * Converts XML name to Java getter method name according to
    * Binding XML Names to Java Identifiers
    * C.2. The Name to Identifier Mapping Algorithm
    * jaxb-2_0-edr-spec-10_jun_2004.pdf
    *
    * @param name          XML name
    * @param ignoreLowLine whether low lines should not be parts of Java identifiers
    * @return Java getter method name
    */
   public static String xmlNameToGetMethodName(String name, boolean ignoreLowLine)
   {
      return "get" + xmlNameToClassName(name, ignoreLowLine);
   }

   /**
    * Converts XML name to Java setter method name according to
    * Binding XML Names to Java Identifiers
    * C.2. The Name to Identifier Mapping Algorithm
    * jaxb-2_0-edr-spec-10_jun_2004.pdf
    *
    * @param name          XML name
    * @param ignoreLowLine whether low lines should not be parts of Java identifiers
    * @return Java setter method name
    */
   public static String xmlNameToSetMethodName(String name, boolean ignoreLowLine)
   {
      return "set" + xmlNameToClassName(name, ignoreLowLine);
   }

   /**
    * Converts XML name to Java constant name according to
    * Binding XML Names to Java Identifiers
    * C.2. The Name to Identifier Mapping Algorithm
    * jaxb-2_0-edr-spec-10_jun_2004.pdf
    *
    * @param name XML name
    * @return Java constant name
    */
   public static String xmlNameToConstantName(String name)
   {
      return XMLNameToJavaIdentifierConverter.PARSER.parse(XMLNameToJavaIdentifierConverter.CONSTANT_NAME,
         name,
         true
      );
   }

   /**
    * Converts XML namespace to Java package name.
    * The base algorithm is described in JAXB-2.0 spec in 'C.5 Generating a Java package name'.
    *
    * @param namespace XML namespace
    * @return Java package name
    */
   public static String xmlNamespaceToJavaPackage(String namespace)
   {
      if(namespace.length() == 0)
      {
         return namespace;
      }

      char[] src = namespace.toLowerCase().toCharArray();
      char[] dst = new char[namespace.length()];

      int srcInd = 0;
      // skip protocol part, i.e. http://, urn://
      while(src[srcInd++] != ':')
      {
      }

      while(src[srcInd] == '/')
      {
         ++srcInd;
      }

      // skip www part
      if(src[srcInd] == 'w' && src[srcInd + 1] == 'w' && src[srcInd + 2] == 'w')
      {
         srcInd += 4;
      }

      // find domain start and end indexes
      int domainStart = srcInd;
      while(srcInd < src.length && src[srcInd] != '/')
      {
         ++srcInd;
      }

      int dstInd = 0;
      // copy domain parts in the reverse order
      for(int start = srcInd - 1, end = srcInd; true; --start)
      {
         if(start == domainStart)
         {
            System.arraycopy(src, start, dst, dstInd, end - start);
            dstInd += end - start;
            break;
         }

         if(src[start] == '.')
         {
            System.arraycopy(src, start + 1, dst, dstInd, end - start - 1);
            dstInd += end - start;
            dst[dstInd - 1] = '.';
            end = start;
         }
      }

      // copy the rest
      while(srcInd < src.length)
      {
         char c = src[srcInd++];
         if(c == '/')
         {
            if(srcInd < src.length)
            {
               dst = append(dst, dstInd++, '.');
               if(!Character.isJavaIdentifierStart(src[srcInd]))
               {
                  dst = append(dst, dstInd++, '_');
               }
            }
         }
         else if(c == '.')
         {
            // for now assume it's an extention, i.e. '.xsd'
            break;
         }
         else
         {
            dst = append(dst, dstInd++, Character.isJavaIdentifierPart(c) ? c : '_');
         }
      }

      return String.valueOf(dst, 0, dstInd);
   }

   /**
    * Converts XML namespace URI and local name to fully qualified class name.
    *
    * @param namespaceUri  namespace URI
    * @param localName     local name
    * @param ignoreLowLine should low lines be ignored in the class name
    * @return fully qualified class name
    */
   public static String xmlNameToClassName(String namespaceUri, String localName, boolean ignoreLowLine)
   {
      return namespaceUri == null || namespaceUri.length() == 0 ?
         xmlNameToClassName(localName, ignoreLowLine) :
         xmlNamespaceToJavaPackage(namespaceUri) + '.' + xmlNameToClassName(localName, ignoreLowLine);
   }

   public static boolean isAttributeType(final Class type)
   {
      return Classes.isPrimitive(type) ||
         type == String.class ||
         type == java.util.Date.class;
   }

   /**
    * Parse the namespace location pairs in the schemaLocation and return the
    * location that matches the nsURI argument.
    *
    * @return the location uri if found, null otherwise
    */
   public static String getSchemaLocation(Attributes attrs, String nsUri)
   {
      String location = null;
      String schemaLocation = attrs.getValue(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "schemaLocation");
      if(schemaLocation != null)
      {
         StringTokenizer tokenizer = new StringTokenizer(schemaLocation, " \t\n\r");
         while (tokenizer.hasMoreTokens())
         {
            String namespace = tokenizer.nextToken();
            if (namespace.equals(nsUri) && tokenizer.hasMoreTokens())
            {
               location = tokenizer.nextToken();
               break;
            }
         }
      }
      return location;
   }

   public static XSModel loadSchema(String xsdURL, SchemaBindingResolver schemaResolver)
   {
      boolean trace = log.isTraceEnabled();
      long start = System.currentTimeMillis();
      if(trace)
         log.trace("loading xsd: " + xsdURL);

      XSImplementation impl = getXSImplementation();
      XSLoader schemaLoader = impl.createXSLoader(null);
      if(schemaResolver != null)
      {
         setResourceResolver(schemaLoader, schemaResolver);
      }

      setDOMErrorHandler(schemaLoader);
      XSModel model = schemaLoader.loadURI(xsdURL);
      if(model == null)
      {
         throw new IllegalArgumentException("Invalid URI for schema: " + xsdURL);
      }

      if (trace)
         log.trace("Loaded xsd: " + xsdURL + " in " + (System.currentTimeMillis() - start) + "ms");
      return model;
   }

   public static XSModel loadSchema(InputStream is, String encoding, SchemaBindingResolver schemaResolver)
   {
      if(log.isTraceEnabled())
      {
         log.trace("loading xsd from InputStream");
      }

      LSInputAdaptor input = new LSInputAdaptor(is, encoding);

      XSImplementation impl = getXSImplementation();
      XSLoader schemaLoader = impl.createXSLoader(null);
      setDOMErrorHandler(schemaLoader);
      if(schemaResolver != null)
      {
         setResourceResolver(schemaLoader, schemaResolver);
      }

      return schemaLoader.load(input);
   }

   public static XSModel loadSchema(Reader reader, String encoding, SchemaBindingResolver schemaResolver)
   {
      if(log.isTraceEnabled())
      {
         log.trace("loading xsd from Reader");
      }

      LSInputAdaptor input = new LSInputAdaptor(reader, encoding);

      XSImplementation impl = getXSImplementation();
      XSLoader schemaLoader = impl.createXSLoader(null);
      setDOMErrorHandler(schemaLoader);
      if(schemaResolver != null)
      {
         setResourceResolver(schemaLoader, schemaResolver);
      }

      return schemaLoader.load(input);
   }

   public static XSModel loadSchema(String data, String encoding)
   {
      if(log.isTraceEnabled())
      {
         log.trace("loading xsd from string");
      }

      LSInputAdaptor input = new LSInputAdaptor(data, encoding);

      XSImplementation impl = getXSImplementation();
      XSLoader schemaLoader = impl.createXSLoader(null);
      setDOMErrorHandler(schemaLoader);
      return schemaLoader.load(input);
   }

   // Private

   /**
    * Sets an array character's element with the given index to a character value.
    * If index is more or equal to the length of the array, a new array is created with enough length to set
    * the element.
    *
    * @param buf   array of characters
    * @param index index of the element to set
    * @param ch    character to set
    * @return if the index parameter is less then array's length then the original array is returned,
    *         otherwise a new array is returned
    */
   private static char[] append(char[] buf, int index, char ch)
   {
      if(index >= buf.length)
      {
         char[] tmp = buf;
         buf = new char[index + 4];
         System.arraycopy(tmp, 0, buf, 0, tmp.length);
      }
      buf[index] = ch;
      return buf;
   }

   private static void setResourceResolver(XSLoader schemaLoader, final SchemaBindingResolver schemaResolver)
   {
      DOMConfiguration config = schemaLoader.getConfig();
      config.setParameter("resource-resolver", new LSResourceResolver()
      {
         public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI)
         {
            if (Constants.NS_XML_SCHEMA.equals(type))
            {
               String schemaLocation = systemId;
               return schemaResolver.resolveAsLSInput(namespaceURI, baseURI, schemaLocation);
            }
            return null;
         }
      }
      );
   }

   private static void setDOMErrorHandler(XSLoader schemaLoader)
   {
      DOMConfiguration config = schemaLoader.getConfig();
      DOMErrorHandler errorHandler = (DOMErrorHandler)config.getParameter("error-handler");
      if (errorHandler == null)
      {
         config.setParameter("error-handler", XsdBinderTerminatingErrorHandler.newInstance());
      }
   }

   private static XSImplementation getXSImplementation()
   {
      return (XSImplementation) AccessController.doPrivileged(new PrivilegedAction()
      {
         public Object run()
         {
            
            // Get DOM Implementation using DOM Registry
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            try
            {
               // Try the 2.6.2 version
               String name = "org.apache.xerces.dom.DOMXSImplementationSourceImpl";
               loader.loadClass(name);
               System.setProperty(DOMImplementationRegistry.PROPERTY, name);
            }
            catch(ClassNotFoundException e)
            {
               // Try the 2.7.0 version
               String name = "org.apache.xerces.dom.DOMXSImplementationSourceImpl";
               System.setProperty(DOMImplementationRegistry.PROPERTY, name);
            }

            XSImplementation impl;
            try
            {
               DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
               impl = (XSImplementation)registry.getDOMImplementation("XS-Loader");
            }
            catch(Exception e)
            {
               log.error("Failed to create schema loader.", e);
               throw new IllegalStateException("Failed to create schema loader: " + e.getMessage());
            }
            return impl;
         }
      });
   }

   // Inner

   /**
    * An interface for XML name to Java identifier (class name, get/set methods, constant names) converter.
    * The following rules and algorithms should be supported
    * <ul>
    * <li>Binding XML Names to Java Identifiers,
    * C.2. The Name to Identifier Mapping Algorithm,
    * jaxb-2_0-edr-spec-10_jun_2004.pdf</li>
    * <li>http://www.w3.org/TR/soap12-part2/#namemap</li>
    * </ul>
    * <p/>
    * But these are not guaranteed to work yet. Instead, a simplified implementation is provided.
    * Incompatabilities should be fixed.
    */
   interface XMLNameToJavaIdentifierConverter
   {
      // commands indicating what should be done with the next character from the XML name
      byte IGNORE = 0;
      byte APPEND = 1;
      byte APPEND_WITH_LOW_LINE = 2;
      byte APPEND_UPPER_CASED = 3;
      byte APPEND_UPPER_CASED_WITH_LOW_LINE = 4;

      /**
       * Returns a command for the next character given the previous character.
       *
       * @param prev          previous character
       * @param next          next character
       * @param ignoreLowLine whether low lines are allowed in the Java identifier or should be ignored
       * @return command for the next character
       */
      byte commandForNext(char prev, char next, boolean ignoreLowLine);

      char firstCharacter(char ch, String str, int secondCharIndex);

      /**
       * An XML name parser class that parses the XML name and asks the outer interface implementation
       * what to do with the next parsed character from the XML name.
       */
      final class PARSER
      {
         /**
          * Parses an XML name, asks the converter for a command for the next parsed character,
          * applies the command, composed the resulting Java identifier.
          *
          * @param converter     an implementation of XMLNameToJavaIdentifierConverter
          * @param xmlName       XML name
          * @param ignoreLowLine indicated whether low lines are allowed as part of the Java identifier or
          *                      should be ignored
          * @return Java identifier
          */
         static String parse(XMLNameToJavaIdentifierConverter converter, String xmlName, boolean ignoreLowLine)
         {
            if(xmlName == null || xmlName.length() == 0)
            {
               throw new IllegalArgumentException("Bad XML name: " + xmlName);
            }

            char c = xmlName.charAt(0);
            int i = 1;
            if(!Character.isJavaIdentifierStart(c) || (c == LOW_LINE && ignoreLowLine))
            {
               while(i < xmlName.length())
               {
                  c = xmlName.charAt(i++);
                  if(Character.isJavaIdentifierStart(c) && !(c == LOW_LINE && ignoreLowLine))
                  {
                     break;
                  }
               }

               if(i == xmlName.length())
               {
                  throw new IllegalArgumentException(
                     "XML name contains no valid character to start Java identifier: " + xmlName
                  );
               }
            }

            char[] buf = new char[xmlName.length() - i + 1];
            buf[0] = converter.firstCharacter(c, xmlName, i);
            int bufInd = 1;
            while(i < xmlName.length())
            {
               char prev = c;
               c = xmlName.charAt(i++);
               byte command = converter.commandForNext(prev, c, ignoreLowLine);
               switch(command)
               {
                  case IGNORE:
                     break;
                  case APPEND:
                     buf = Util.append(buf, bufInd++, c);
                     break;
                  case APPEND_WITH_LOW_LINE:
                     buf = Util.append(buf, bufInd++, LOW_LINE);
                     buf = Util.append(buf, bufInd++, c);
                     break;
                  case APPEND_UPPER_CASED:
                     buf = Util.append(buf, bufInd++, Character.toUpperCase(c));
                     break;
                  case APPEND_UPPER_CASED_WITH_LOW_LINE:
                     buf = Util.append(buf, bufInd++, LOW_LINE);
                     buf = Util.append(buf, bufInd++, Character.toUpperCase(c));
                     break;
                  default:
                     throw new IllegalArgumentException("Unexpected command: " + command);
               }
            }

            return new String(buf, 0, bufInd);
         }
      }

      /**
       * XML name to Java class name converter
       */
      XMLNameToJavaIdentifierConverter CLASS_NAME = new XMLNameToJavaIdentifierConverter()
      {
         public char firstCharacter(char ch, String str, int secondCharIndex)
         {
            return Character.toUpperCase(ch);
         }

         public byte commandForNext(char prev, char next, boolean ignoreLowLine)
         {
            byte command;
            if(Character.isDigit(next))
            {
               command = APPEND;
            }
            else if(next == LOW_LINE)
            {
               command = ignoreLowLine ? IGNORE : APPEND;
            }
            else if(Character.isJavaIdentifierPart(next))
            {
               if(Character.isJavaIdentifierPart(prev) && !Character.isDigit(prev))
               {
                  command = prev == LOW_LINE ? APPEND_UPPER_CASED : APPEND;
               }
               else
               {
                  command = APPEND_UPPER_CASED;
               }
            }
            else
            {
               command = IGNORE;
            }
            return command;
         }
      };

      /**
       * XML name to Java class name converter
       */
      XMLNameToJavaIdentifierConverter FIELD_NAME = new XMLNameToJavaIdentifierConverter()
      {
         public char firstCharacter(char ch, String str, int secondCharIndex)
         {
            if(Character.isLowerCase(ch))
            {
               return ch;
            }
            else
            {
               return (str.length() > secondCharIndex &&
                  Character.isJavaIdentifierPart(str.charAt(secondCharIndex)) &&
                  Character.isUpperCase(str.charAt(secondCharIndex))
                  ) ?
                  Character.toUpperCase(ch) :
                  Character.toLowerCase(ch);
            }
         }

         public byte commandForNext(char prev, char next, boolean ignoreLowLine)
         {
            return CLASS_NAME.commandForNext(prev, next, ignoreLowLine);
         }
      };

      /**
       * XML name to Java constant name converter
       */
      XMLNameToJavaIdentifierConverter CONSTANT_NAME = new XMLNameToJavaIdentifierConverter()
      {
         public char firstCharacter(char ch, String str, int secondCharIndex)
         {
            return Character.toUpperCase(ch);
         }

         public byte commandForNext(char prev, char next, boolean ignoreLowLine)
         {
            byte command;
            if(Character.isDigit(next))
            {
               command = Character.isDigit(prev) ? APPEND : APPEND_UPPER_CASED_WITH_LOW_LINE;
            }
            else if(Character.isJavaIdentifierPart(next))
            {
               if(Character.isDigit(prev))
               {
                  command = APPEND_UPPER_CASED_WITH_LOW_LINE;
               }
               else if(Character.isJavaIdentifierPart(prev))
               {
                  command = Character.isUpperCase(next) ?
                     (Character.isUpperCase(prev) ? APPEND_UPPER_CASED : APPEND_WITH_LOW_LINE) :
                     APPEND_UPPER_CASED;
               }
               else
               {
                  command = APPEND_UPPER_CASED_WITH_LOW_LINE;
               }
            }
            else
            {
               command = IGNORE;
            }
            return command;
         }
      };
   }
}
