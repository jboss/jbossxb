/*
 * JBoss, the OpenSource webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface Constants
{
   String NS_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
   String NS_XML_SCHEMA_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance";

   String NS_JAXB = "http://java.sun.com/xml/ns/jaxb";
   String NS_JBXB = "http://www.jboss.org/xml/ns/jbxb";

   //
   // XML schema type names
   //

   QName QNAME_ANYSIMPLETYPE = new QName(NS_XML_SCHEMA, "anySimpleType");

   // primitive datatypes
   QName QNAME_STRING = new QName(NS_XML_SCHEMA, "string");
   QName QNAME_BOOLEAN = new QName(NS_XML_SCHEMA, "boolean");
   QName QNAME_DECIMAL = new QName(NS_XML_SCHEMA, "decimal");
   QName QNAME_FLOAT = new QName(NS_XML_SCHEMA, "float");
   QName QNAME_DOUBLE = new QName(NS_XML_SCHEMA, "double");
   QName QNAME_DURATION = new QName(NS_XML_SCHEMA, "duration");
   QName QNAME_DATETIME = new QName(NS_XML_SCHEMA, "dateTime");
   QName QNAME_TIME = new QName(NS_XML_SCHEMA, "time");
   QName QNAME_DATE = new QName(NS_XML_SCHEMA, "date");
   QName QNAME_GYEARMONTH = new QName(NS_XML_SCHEMA, "gYearMonth");
   QName QNAME_GYEAR = new QName(NS_XML_SCHEMA, "gYear");
   QName QNAME_GMONTHDAY = new QName(NS_XML_SCHEMA, "gMonthDay");
   QName QNAME_GDAY = new QName(NS_XML_SCHEMA, "gDay");
   QName QNAME_GMONTH = new QName(NS_XML_SCHEMA, "gMonth");
   QName QNAME_HEXBINARY = new QName(NS_XML_SCHEMA, "hexBinary");
   QName QNAME_BASE64BINARY = new QName(NS_XML_SCHEMA, "base64Binary");
   QName QNAME_ANYURI = new QName(NS_XML_SCHEMA, "anyURI");
   QName QNAME_QNAME = new QName(NS_XML_SCHEMA, "QName");
   QName QNAME_NOTATION = new QName(NS_XML_SCHEMA, "NOTATION");

   // derived datatypes
   QName QNAME_NORMALIZEDSTRING = new QName(NS_XML_SCHEMA, "normalizedString");
   QName QNAME_TOKEN = new QName(NS_XML_SCHEMA, "token");
   QName QNAME_LANGUAGE = new QName(NS_XML_SCHEMA, "language");
   QName QNAME_NMTOKEN = new QName(NS_XML_SCHEMA, "NMToken");
   QName QNAME_NMTOKENS = new QName(NS_XML_SCHEMA, "NMTokens");
   QName QNAME_NAME = new QName(NS_XML_SCHEMA, "Name");
   QName QNAME_NCNAME = new QName(NS_XML_SCHEMA, "NCName");
   QName QNAME_ID = new QName(NS_XML_SCHEMA, "ID");
   QName QNAME_IDREF = new QName(NS_XML_SCHEMA, "IDREF");
   QName QNAME_IDREFS = new QName(NS_XML_SCHEMA, "IDREFS");
   QName QNAME_ENTITY = new QName(NS_XML_SCHEMA, "ENTITY");
   QName QNAME_ENTITIES = new QName(NS_XML_SCHEMA, "ENTITIES");
   QName QNAME_INTEGER = new QName(NS_XML_SCHEMA, "integer");
   QName QNAME_NONPOSITIVEINTEGER = new QName(NS_XML_SCHEMA, "nonPositiveInteger");
   QName QNAME_NEGATIVEINTEGER = new QName(NS_XML_SCHEMA, "negativeInteger");
   QName QNAME_LONG = new QName(NS_XML_SCHEMA, "long");
   QName QNAME_INT = new QName(NS_XML_SCHEMA, "int");
   QName QNAME_SHORT = new QName(NS_XML_SCHEMA, "short");
   QName QNAME_BYTE = new QName(NS_XML_SCHEMA, "byte");
   QName QNAME_NONNEGATIVEINTEGER = new QName(NS_XML_SCHEMA, "nonNegativeInteger");
   QName QNAME_UNSIGNEDLONG = new QName(NS_XML_SCHEMA, "unsignedLong");
   QName QNAME_UNSIGNEDINT = new QName(NS_XML_SCHEMA, "unsignedInt");
   QName QNAME_UNSIGNEDSHORT = new QName(NS_XML_SCHEMA, "unsignedShort");
   QName QNAME_UNSIGNEDBYTE = new QName(NS_XML_SCHEMA, "unsignedByte");
   QName QNAME_POSITIVEINTEGER = new QName(NS_XML_SCHEMA, "positiveInteger");

}
