/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

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
         ignoreLowLine);
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
         true);
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
                     "XML name contains no valid character to start Java identifier: " + xmlName);
               }
            }

            char[] buf = new char[xmlName.length() - i + 1];
            buf[0] = Character.toUpperCase(c);
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
       * XML name to Java constant name converter
       */
      XMLNameToJavaIdentifierConverter CONSTANT_NAME = new XMLNameToJavaIdentifierConverter()
      {
         public byte commandForNext(char prev, char next, boolean ignoreLowLine)
         {
            byte command;
            if(Character.isDigit(next))
            {
               command = Character.isDigit(prev) ? APPEND: APPEND_UPPER_CASED_WITH_LOW_LINE;
            }
            else if(Character.isJavaIdentifierPart(next))
            {
               if(Character.isDigit(prev))
               {
                  command = APPEND_UPPER_CASED_WITH_LOW_LINE;
               }
               else if(Character.isJavaIdentifierPart(prev))
               {
                  command = Character.isUpperCase(next) ? APPEND_WITH_LOW_LINE: APPEND_UPPER_CASED;
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
