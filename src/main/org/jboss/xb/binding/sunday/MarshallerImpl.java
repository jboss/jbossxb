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
package org.jboss.xb.binding.sunday;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import org.jboss.logging.Logger;
import org.jboss.util.Classes;
import org.jboss.xb.binding.AbstractMarshaller;
import org.jboss.xb.binding.AttributesImpl;
import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.Content;
import org.jboss.xb.binding.ContentWriter;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.MarshallingContext;
import org.jboss.xb.binding.ObjectLocalMarshaller;
import org.jboss.xb.binding.ObjectModelProvider;
import org.jboss.xb.binding.SimpleTypeBindings;
import org.jboss.xb.binding.Util;
import org.jboss.xb.binding.metadata.CharactersMetaData;
import org.jboss.xb.binding.metadata.PropertyMetaData;
import org.jboss.xb.binding.metadata.marshalling.FieldBinding;
import org.jboss.xb.binding.sunday.unmarshalling.AllBinding;
import org.jboss.xb.binding.sunday.unmarshalling.AttributeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ChoiceBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ModelGroupBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBindingResolver;
import org.jboss.xb.binding.sunday.unmarshalling.TermBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.WildcardBinding;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class MarshallerImpl
   extends AbstractMarshaller
{
   private static final Logger log = Logger.getLogger(MarshallerImpl.class);

   private Stack stack = new StackImpl();

   private Object root;

   /**
    * Whether NULL values should be ignored or marshalled as xsi:nil='1'
    */
   private boolean supportNil = true;

   private boolean ignoreUnresolvedWildcard;

   private QName rootTypeQName;

   private SchemaBindingResolver schemaResolver;

   private SchemaBinding schema;

   private XOPMarshaller xopMarshaller;

   private MarshallingContext ctx = new MarshallingContext()
   {
      private ContentHandler ch;

      public FieldBinding getFieldBinding()
      {
         throw new UnsupportedOperationException("getFieldBinding is not implemented.");
      }

      public boolean isAttributeRequired()
      {
         throw new UnsupportedOperationException();
      }

      public boolean isTypeComplex()
      {
         throw new UnsupportedOperationException();
      }

      public String getSimpleContentProperty()
      {
         throw new UnsupportedOperationException();
      }

      public ContentHandler getContentHandler()
      {
         if(ch == null)
         {
            ch = new ContentHandlerAdaptor();
         }
         return ch;
      }
   };

   public boolean isIgnoreUnresolvedWildcard()
   {
      return ignoreUnresolvedWildcard;
   }

   public void setIgnoreUnresolvedWildcard(boolean ignoreUnresolvedWildcard)
   {
      this.ignoreUnresolvedWildcard = ignoreUnresolvedWildcard;
   }

   public SchemaBindingResolver getSchemaResolver()
   {
      return schemaResolver;
   }

   public void setSchemaResolver(SchemaBindingResolver schemaResolver)
   {
      this.schemaResolver = schemaResolver;
   }

   public QName getRootTypeQName()
   {
      return rootTypeQName;
   }

   public void setRootTypeQName(QName rootTypeQName)
   {
      this.rootTypeQName = rootTypeQName;
   }

   public boolean isSupportNil()
   {
      return supportNil;
   }

   public void setSupportNil(boolean supportNil)
   {
      this.supportNil = supportNil;
   }

   public XOPMarshaller getXopMarshaller()
   {
      return xopMarshaller;
   }

   public void setXopMarshaller(XOPMarshaller xopMarshaller)
   {
      this.xopMarshaller = xopMarshaller;
   }

   /**
    * Adds an attribute to the top most elements.
    * First, we check whether there is a namespace associated with the passed in prefix.
    * If the prefix was not declared, an exception is thrown.
    *
    * @param prefix    the prefix of the attribute to be declared
    * @param localName local name of the attribute
    * @param type      the type of the attribute
    * @param value     the value of the attribute
    */
   public void addAttribute(String prefix, String localName, String type, String value)
   {
      // todo addAttribute(String prefix, String localName, String type, String value)
   }

   // AbstractMarshaller implementation

   public void marshal(Reader xsdReader, ObjectModelProvider provider, Object root, Writer writer)
      throws IOException, SAXException, ParserConfigurationException
   {
      SchemaBinding model = XsdBinder.bind(xsdReader, null, schemaResolver);
      marshallInternal(root, model, writer);
   }

   public void marshal(String xsdURL, ObjectModelProvider provider, Object root, Writer writer) throws IOException,
      SAXException
   {
      SchemaBinding model = XsdBinder.bind(xsdURL, schemaResolver);
      marshallInternal(root, model, writer);
   }

   public void marshal(SchemaBinding model, ObjectModelProvider provider, Object root, Writer writer)
      throws IOException,
      SAXException
   {
      marshallInternal(root, model, writer);
   }

   private void marshallInternal(Object root, SchemaBinding schema, Writer writer)
      throws IOException, SAXException
   {
      if(schema == null)
      {
         throw new JBossXBRuntimeException("XSModel is not available!");
      }

      this.schema = schema;
      this.root = root;

      content.startDocument();

      if(rootTypeQName != null)
      {
         if(rootQNames.isEmpty())
         {
            throw new JBossXBRuntimeException("If type name (" +
               rootTypeQName +
               ") for the root element is specified then the name for the root element is required!"
            );
         }
         QName rootQName = (QName)rootQNames.get(0);

         TypeBinding type = schema.getType(rootTypeQName);
         if(type == null)
         {
            throw new JBossXBRuntimeException("Global type definition is not found: " + rootTypeQName);
         }

         if(isArrayWrapper(type))
         {
            stack.push(root);
            marshalComplexType(rootQName.getNamespaceURI(),
               rootQName.getLocalPart(),
               type,
               true,
               false
            );
            stack.pop();
         }
         else
         {
            ElementBinding element = new ElementBinding(schema, rootQName, type);
            marshalElementOccurence(element, root, false, true);
         }
      }
      else if(rootQNames.isEmpty())
      {
         Iterator elements = schema.getElements();
         if(!elements.hasNext())
         {
            throw new JBossXBRuntimeException("The schema doesn't contain global element declarations.");
         }

         while(elements.hasNext())
         {
            ElementBinding element = (ElementBinding)elements.next();
            marshalElementOccurence(element, root, true, true);
         }
      }
      else
      {
         for(int i = 0; i < rootQNames.size(); ++i)
         {
            QName qName = (QName)rootQNames.get(i);
            ElementBinding element = schema.getElement(qName);
            if(element == null)
            {
               Iterator components = schema.getElements();
               String roots = "";
               for(int j = 0; components.hasNext(); ++j)
               {
                  ElementBinding xsObject = (ElementBinding)components.next();
                  if(j > 0)
                  {
                     roots += ", ";
                  }
                  roots += xsObject.getQName();
               }
               throw new IllegalStateException("Root element not found: " + qName + " among " + roots);
            }

            marshalElementOccurence(element, root, true, true);
         }
      }

      content.endDocument();

      // version & encoding
      writeXmlVersion(writer);

      ContentWriter contentWriter = new ContentWriter(writer,
         propertyIsTrueOrNotSet(org.jboss.xb.binding.Marshaller.PROP_OUTPUT_INDENTATION)
      );
      content.handleContent(contentWriter);

      if(log.isTraceEnabled())
      {
         java.io.StringWriter traceWriter = new java.io.StringWriter();
         contentWriter = new ContentWriter(traceWriter,
            propertyIsTrueOrNotSet(org.jboss.xb.binding.Marshaller.PROP_OUTPUT_INDENTATION)
         );
         content.handleContent(contentWriter);
         log.trace("marshalled:\n" + traceWriter.getBuffer().toString());
      }
   }

   private boolean marshalElementOccurence(ElementBinding element,
                                           Object value,
                                           boolean optional,
                                           boolean declareNs)
   {
      QName xsiTypeQName = null;
      TypeBinding xsiType = null;
      if(value != null)
      {
         QName typeQName = element.getType().getQName();
         xsiTypeQName = (QName)cls2TypeMap.get(value.getClass());
         // in case xsiTypeQName is not null, typeQName should also be not null
         if(xsiTypeQName != null &&
            !(typeQName.getLocalPart().equals(xsiTypeQName.getLocalPart()) &&
            typeQName.getNamespaceURI().equals(xsiTypeQName.getNamespaceURI())
            ))
         {
            if(log.isTraceEnabled())
            {
               log.trace(value.getClass() + " is mapped to xsi:type " + xsiTypeQName);
            }

            xsiType = schema.getType(xsiTypeQName);
            if(xsiType == null)
            {
               log.warn("Class " +
                  value.getClass() +
                  " is mapped to type " +
                  xsiTypeQName +
                  " but the type is not found in schema."
               );
            }
            // todo should check derivation also, i.e. if(xsiType.derivedFrom())
         }
      }

      stack.push(value);
      boolean marshalled = marshalElement(element, xsiType, optional, declareNs);
      stack.pop();

      return marshalled;
   }

   private boolean marshalElement(ElementBinding element, TypeBinding xsiType, boolean optional, boolean declareNs)
   {
      Object value = stack.peek();
      boolean nillable = element.isNillable();
      boolean result = value != null || value == null && (optional || nillable);
      String elementNs = element.getQName().getNamespaceURI();
      String elementLocal = element.getQName().getLocalPart();
      boolean trace = log.isTraceEnabled() && result;
      if(trace)
      {
         String prefix = getPrefix(elementNs);
         log.trace("started element ns=" + elementNs + ", local=" + elementLocal + ", prefix=" + prefix);
      }

      if(value != null)
      {
         boolean declareXsiType = xsiType != null;
         marshalElementType(elementNs,
            elementLocal,
            declareXsiType ? xsiType : element.getType(),
            declareNs,
            declareXsiType,
            nillable
         );
      }
      else if(nillable)
      {
         writeNillable(elementNs, elementLocal, nillable);
      }

      if(trace)
      {
         log.trace("finished element ns=" + elementNs + ", local=" + elementLocal);
      }

      return result;
   }

   private void marshalElementType(String elementNs,
                                   String elementLocal,
                                   TypeBinding type,
                                   boolean declareNs,
                                   boolean declareXsiType,
                                   boolean nillable)
   {
      if(type.isSimple())
      {
         marshalSimpleType(elementNs, elementLocal, type, declareNs, declareXsiType, nillable);
      }
      else
      {
         marshalComplexType(elementNs, elementLocal, type, declareNs, declareXsiType);
      }
   }

   private void marshalSimpleType(String elementUri,
                                  String elementLocal,
                                  TypeBinding type,
                                  boolean declareNs,
                                  boolean declareXsiType,
                                  boolean nillable)
   {
      Object value = stack.peek();
      if(value != null)
      {
         String prefix = getPrefix(elementUri);
         boolean genPrefix = prefix == null && elementUri != null && elementUri.length() > 0;
         if(genPrefix)
         {
            prefix = "ns_" + elementLocal;
         }

         AttributesImpl attrs = null;
         String typeName = type.getQName().getLocalPart();
         if(SimpleTypeBindings.XS_QNAME_NAME.equals(typeName) ||
            SimpleTypeBindings.XS_NOTATION_NAME.equals(typeName) ||
            type.getItemType() != null &&
            (SimpleTypeBindings.XS_QNAME_NAME.equals(type.getItemType().getQName().getLocalPart()) ||
            SimpleTypeBindings.XS_NOTATION_NAME.equals(type.getItemType().getQName().getLocalPart())
            )
         )
         {
            attrs = new AttributesImpl(5);
         }

         String marshalled = marshalCharacters(elementUri, prefix, type, value, attrs);

/*
         if(Constants.NS_XML_SCHEMA.equals(type.getQName().getNamespaceURI()))
         {
            // todo: pass non-null namespace context
            String typeName = type.getQName().getLocalPart();

            if(SimpleTypeBindings.XS_QNAME_NAME.equals(typeName) ||
               SimpleTypeBindings.XS_NOTATION_NAME.equals(typeName))
            {
               QName qNameValue = (QName)value;
               String prefixValue = qNameValue.getPrefix();
               if((elementUri != null && !qNameValue.getNamespaceURI().equals(elementUri) ||
                  elementUri == null && qNameValue.getNamespaceURI().length() > 0
                  ) &&
                  (prefixValue.equals(prefix) || prefixValue.length() == 0 && prefix == null))
               {
                  // how to best resolve this conflict?
                  prefixValue += 'x';
                  value = new QName(qNameValue.getNamespaceURI(), qNameValue.getLocalPart(), prefixValue);
               }

               attrs = new AttributesImpl(1);
               attrs.add(null,
                  prefixValue,
                  prefixValue.length() == 0 ? "xmlns" : "xmlns:" + prefixValue,
                  null,
                  qNameValue.getNamespaceURI()
               );
            }

            try
            {
               marshalled = SimpleTypeBindings.marshal(typeName, value, null);
            }
            catch(ClassCastException e)
            {
               throw new JBossXBRuntimeException(
                  "ClassCastException marshalling " + new QName(elementUri, elementLocal) + " of type " +
                  type.getQName() + ": " + e.getMessage()
               );
            }
         }
         // todo: this is a quick fix for boolean pattern (0|1 or true|false) should be refactored
         else if(type.getLexicalPattern() != null &&
            type.getBaseType() != null &&
            Constants.QNAME_BOOLEAN.equals(type.getBaseType().getQName()))
         {
            String item = (String)type.getLexicalPattern().get(0);
            if(item.indexOf('0') != -1 && item.indexOf('1') != -1)
            {
               marshalled = ((Boolean)value).booleanValue() ? "1" : "0";
            }
            else
            {
               marshalled = ((Boolean)value).booleanValue() ? "true" : "false";
            }
         }
         else
         {
            marshalled = value.toString();
         }
*/

         if((declareNs || declareXsiType) && !prefixByUri.isEmpty())
         {
            if(attrs == null)
            {
               attrs = new AttributesImpl(prefixByUri.size() + 1);
            }
            declareNs(attrs);
         }

         if(declareXsiType)
         {
            declareXsiType(type.getQName(), attrs);
         }

         if(genPrefix)
         {
            if(attrs == null)
            {
               attrs = new AttributesImpl(1);
            }

            attrs.add(null, prefix, "xmlns:" + prefix, null, (String)elementUri);
         }

         String qName = createQName(prefix, elementLocal);

         content.startElement(elementUri, elementLocal, qName, attrs);
         content.characters(marshalled.toCharArray(), 0, marshalled.length());
         content.endElement(elementUri, elementLocal, qName);
      }
      else
      {
         writeNillable(elementUri, elementLocal, nillable);
      }
   }

   private void marshalComplexType(String elementNsUri,
                                   String elementLocalName,
                                   TypeBinding type,
                                   boolean declareNs,
                                   boolean declareXsiType)
   {
      Object o = stack.peek();

      String cid = null;
      if(xopMarshaller != null && xopMarshaller.isXOPPackage() &&
         Constants.QNAME_XMIME_BASE64BINARY.equals(type.getQName()))
      {
         // todo: contentType
         DataSource ds = new JAFDataSource(o, "text/plain");
         cid = xopMarshaller.addMtomAttachment(new DataHandler(ds), elementNsUri, elementLocalName);
         if(cid == null)
         {
            TypeBinding base64Binary = schema.getType(Constants.QNAME_BASE64BINARY);
            if(base64Binary == null)
            {
               throw new JBossXBRuntimeException("Type base64Binary is not bound!");
            }

            marshalSimpleType(elementNsUri, elementLocalName, base64Binary, declareNs, declareXsiType, false);
            return;
         }
      }

      ParticleBinding particle = type.getParticle();

      Collection attributeUses = type.getAttributes();
      int attrsTotal = declareNs || declareXsiType ? prefixByUri.size() + attributeUses.size() + 1: attributeUses.size();
      AttributesImpl attrs = attrsTotal > 0 ? new AttributesImpl(attrsTotal) : null;

      if(declareNs && !prefixByUri.isEmpty())
      {
         declareNs(attrs);
      }

      String typeNsWithGeneratedPrefix = null;
      if(declareXsiType)
      {
         String generatedPrefix = declareXsiType(type.getQName(), attrs);
         if(generatedPrefix != null)
         {
            typeNsWithGeneratedPrefix = type.getQName().getNamespaceURI();
            declareNs(attrs, generatedPrefix, typeNsWithGeneratedPrefix);
            declareNamespace(generatedPrefix, typeNsWithGeneratedPrefix);
         }
      }

      String prefix = getPrefix(elementNsUri);
      boolean genPrefix = prefix == null && elementNsUri != null && elementNsUri.length() > 0;
      if(genPrefix)
      {
         // todo: it's possible that the generated prefix already mapped. this should be fixed
         prefix = "ns_" + elementLocalName;
         declareNamespace(prefix, elementNsUri);
         if(attrs == null)
         {
            attrs = new AttributesImpl(1);
         }
         attrs.add(null, prefix, "xmlns:" + prefix, null, elementNsUri);
      }

      String characters = null;
      String qName = createQName(prefix, elementLocalName);

      if(cid != null)
      {
         content.startElement(elementNsUri, elementLocalName, qName, attrs);

         AttributesImpl xopAttrs = new AttributesImpl(2);
         xopAttrs.add(Constants.NS_XML_SCHEMA, "xop", "xmlns:xop", "CDATA", Constants.NS_XOP_INCLUDE);
         xopAttrs.add(null, "href", "href", "CDATA", cid);

         content.startElement(Constants.NS_XOP_INCLUDE, "Include", "xop:Include", xopAttrs);
         content.endElement(Constants.NS_XOP_INCLUDE, "Include", "xop:Include");

         content.endElement(elementNsUri, elementLocalName, qName);
      }
      else
      {
         boolean ignoreUnresolvedFieldOrClass = type.getSchemaBinding().isIgnoreUnresolvedFieldOrClass();
         for(Iterator i = attributeUses.iterator(); i.hasNext();)
         {
            AttributeBinding attrUse = (AttributeBinding)i.next();
            QName attrQName = attrUse.getQName();

            String fieldName = null;
            PropertyMetaData propertyMetaData = attrUse.getPropertyMetaData();
            if(propertyMetaData != null)
            {
               fieldName = propertyMetaData.getName();
            }

            if(fieldName == null)
            {
               fieldName =
                  Util.xmlNameToFieldName(attrQName.getLocalPart(), type.getSchemaBinding().isIgnoreLowLine());
            }

            Object attrValue = getJavaValue(attrQName, fieldName, o, false, ignoreUnresolvedFieldOrClass);

            if(attrValue != null)
            {
               if(attrs == null)
               {
                  attrs = new AttributesImpl(5);
               }

               String attrNs = attrQName.getNamespaceURI();
               String attrLocal = attrQName.getLocalPart();
               String attrPrefix = null;
               if(attrNs != null)
               {
                  attrPrefix = getPrefix(attrNs);
                  if(attrPrefix == null && attrNs != null && attrNs.length() > 0)
                  {
                     attrPrefix = "ns_" + attrLocal;
                     attrs.add(null, attrPrefix, "xmlns:" + attrPrefix, null, attrNs);
                  }
               }

               TypeBinding attrType = attrUse.getType();
               if(attrType.getItemType() != null)
               {
                  TypeBinding itemType = attrType.getItemType();
                  if(Constants.NS_XML_SCHEMA.equals(itemType.getQName().getNamespaceURI()))
                  {
                     List list;
                     if(attrValue instanceof List)
                     {
                        list = (List)attrValue;
                     }
                     else if(attrValue.getClass().isArray())
                     {
                        list = Arrays.asList((Object[])attrValue);
                     }
                     else
                     {
                        throw new JBossXBRuntimeException("Expected value for list type is an array or " +
                           List.class.getName() +
                           " but got: " +
                           attrValue);
                     }

                     if(Constants.QNAME_QNAME.getLocalPart().equals(itemType.getQName().getLocalPart()))
                     {
                        for(int listInd = 0; listInd < list.size(); ++listInd)
                        {
                           QName item = (QName)list.get(listInd);
                           String itemNs = item.getNamespaceURI();
                           if(itemNs != null && itemNs.length() > 0)
                           {
                              String itemPrefix;
                              if(itemNs.equals(elementNsUri))
                              {
                                 itemPrefix = prefix;
                              }
                              else
                              {
                                 itemPrefix = getPrefix(itemNs);
                                 if(itemPrefix == null)
                                 {
                                    itemPrefix = attrLocal + listInd;
                                    declareNs(attrs, itemPrefix, itemNs);
                                 }
                              }
                              item = new QName(item.getNamespaceURI(), item.getLocalPart(), itemPrefix);
                              list.set(listInd, item);
                           }
                        }
                     }

                     attrValue = SimpleTypeBindings.marshalList(itemType.getQName().getLocalPart(), list, null);
                  }
                  else
                  {
                     throw new JBossXBRuntimeException("Marshalling of list types with item types not from " +
                        Constants.NS_XML_SCHEMA + " is not supported.");
                  }
               }
               else if(attrType.getLexicalPattern() != null &&
                  attrType.getBaseType() != null &&
                  Constants.QNAME_BOOLEAN.equals(attrType.getBaseType().getQName()))
               {
                  String item = (String)attrType.getLexicalPattern().get(0);
                  if(item.indexOf('0') != -1 && item.indexOf('1') != -1)
                  {
                     attrValue = ((Boolean)attrValue).booleanValue() ? "1" : "0";
                  }
                  else
                  {
                     attrValue = ((Boolean)attrValue).booleanValue() ? "true" : "false";
                  }
               }
               else
               {
                  attrValue = attrValue.toString();
               }

               String prefixedName = attrPrefix == null || attrPrefix.length() == 0 ?
                  attrLocal :
                  attrPrefix + ":" + attrLocal;
               QName typeQName = attrType.getQName();
               String typeName = typeQName == null ? null : typeQName.getLocalPart();
               attrs.add(attrNs,
                  attrLocal,
                  prefixedName,
                  typeName,
                  attrValue.toString());
            }
         }

         TypeBinding simpleType = type.getSimpleType();
         if(simpleType != null)
         {
            String fieldName = "value";
            CharactersMetaData charactersMetaData = type.getCharactersMetaData();
            PropertyMetaData propertyMetaData = charactersMetaData == null ? null : charactersMetaData.getProperty();
            if(propertyMetaData != null)
            {
               fieldName = propertyMetaData.getName();
            }

            if(fieldName != null)
            {
               Object value = getElementValue(new QName(elementNsUri, elementLocalName),
                  o,
                  fieldName,
                  ignoreUnresolvedFieldOrClass);
               if(value != null)
               {
                  String typeName = simpleType.getQName().getLocalPart();
                  if(attrs == null && (SimpleTypeBindings.XS_QNAME_NAME.equals(typeName) ||
                     SimpleTypeBindings.XS_NOTATION_NAME.equals(typeName) ||
                     simpleType.getItemType() != null &&
                     (SimpleTypeBindings.XS_QNAME_NAME.equals(simpleType.getItemType().getQName().getLocalPart()) ||
                     SimpleTypeBindings.XS_NOTATION_NAME.equals(simpleType.getItemType().getQName().getLocalPart())
                     )
                     )
                  )
                  {
                     attrs = new AttributesImpl(5);
                  }

                  characters = marshalCharacters(elementNsUri, prefix, simpleType, value, attrs);
               }
            }
         }

         content.startElement(elementNsUri, elementLocalName, qName, attrs);

         if(particle != null)
         {
            marshalParticle(particle, false);
         }

         if(characters != null)
         {
            content.characters(characters.toCharArray(), 0, characters.length());
         }
         content.endElement(elementNsUri, elementLocalName, qName);
      }

      if(genPrefix)
      {
         removeNamespace(elementNsUri);
      }

      if(typeNsWithGeneratedPrefix != null)
      {
         removeNamespace(typeNsWithGeneratedPrefix);
      }
   }

   private boolean marshalParticle(ParticleBinding particle, boolean declareNs)
   {
      boolean marshalled;
      TermBinding term = particle.getTerm();
      Object o;
      Iterator i;
      if(term.isModelGroup())
      {
         ModelGroupBinding modelGroup = (ModelGroupBinding)term;
         if(modelGroup.isSkip() || stack.isEmpty())
         {
            marshalled = marshalModelGroup(modelGroup, declareNs);
         }
         else
         {
            PropertyMetaData propertyMetaData = modelGroup.getPropertyMetaData();
            if(propertyMetaData == null)
            {
               throw new JBossXBRuntimeException(
                  "Currently, property binding metadata must be available for a model group to be marshalled!"
               );
            }

            o = getChildren(null, stack.peek(), propertyMetaData.getName(),
               modelGroup.getSchema().isIgnoreUnresolvedFieldOrClass()
            );

            i = o != null && isRepeatable(particle) ? getIterator(o) : null;
            if(i != null)
            {
               marshalled = true;
               while(i.hasNext() && marshalled)
               {
                  Object value = i.next();
                  stack.push(value);
                  marshalled = marshalModelGroup(modelGroup, declareNs);
                  stack.pop();
               }
            }
            else
            {
               stack.push(o);
               marshalled = marshalModelGroup(modelGroup, declareNs);
               stack.pop();
            }
         }
      }
      else if(term.isWildcard())
      {
         o = stack.peek();

         boolean popWildcardValue = false;
         ObjectLocalMarshaller marshaller = null;
         FieldToWildcardMapping mapping = (FieldToWildcardMapping)field2WildcardMap.get(o.getClass());
         if(mapping != null)
         {
            marshaller = mapping.marshaller;
            if(mapping.getter != null)
            {
               try
               {
                  o = mapping.getter.invoke(o, null);
               }
               catch(Exception e)
               {
                  throw new JBossXBRuntimeException("Failed to invoke getter " +
                     mapping.getter.getName() +
                     " on " +
                     o.getClass() +
                     " to get wildcard value: " + e.getMessage()
                  );
               }
            }
            else
            {
               try
               {
                  o = mapping.field.get(o);
               }
               catch(Exception e)
               {
                  throw new JBossXBRuntimeException("Failed to invoke get on field " +
                     mapping.field.getName() +
                     " in " +
                     o.getClass() +
                     " to get wildcard value: " + e.getMessage()
                  );
               }
            }
            stack.push(o);
            popWildcardValue = true;
         }

         i = o != null && isRepeatable(particle) ? getIterator(o) : null;
         if(i != null)
         {
            marshalled = true;
            while(i.hasNext() && marshalled)
            {
               Object value = i.next();
               marshalled = marshalWildcardOccurence(particle, marshaller, value, declareNs);
            }
         }
         else
         {
            marshalled = marshalWildcardOccurence(particle, marshaller, o, declareNs);
         }

         if(popWildcardValue)
         {
            stack.pop();
         }
      }
      else
      {
         ElementBinding element = (ElementBinding)term;
         SchemaBinding schema = element.getSchema();
         o = getElementValue(element, schema.isIgnoreLowLine(), schema.isIgnoreUnresolvedFieldOrClass());

         i = o != null && isRepeatable(particle) ? getIterator(o) : null;
         if(i != null)
         {
            marshalled = true;
            while(i.hasNext() && marshalled)
            {
               Object value = i.next();
               marshalled = marshalElementOccurence(element, value, particle.getMinOccurs() == 0, declareNs);
            }
         }
         else
         {
            marshalled = marshalElementOccurence(element, o, particle.getMinOccurs() == 0, declareNs);
         }
      }
      return marshalled;
   }

   private boolean marshalWildcardOccurence(ParticleBinding particle,
                                            ObjectLocalMarshaller marshaller,
                                            Object value,
                                            boolean declareNs)
   {
      boolean marshalled = true;
      if(marshaller != null)
      {
         marshaller.marshal(ctx, value);
      }
      else
      {
         stack.push(value);
         marshalled = marshalWildcard(particle, declareNs);
         stack.pop();
      }
      return marshalled;
   }

   private boolean marshalWildcard(ParticleBinding particle, boolean declareNs)
   {
      WildcardBinding wildcard = (WildcardBinding)particle.getTerm();
      Object o = stack.peek();
      ClassMapping mapping = getClassMapping(o.getClass());
      if(mapping == null)
      {
         // todo: YAH (yet another hack)
         QName autoType = SimpleTypeBindings.typeQName(o.getClass());
         if(autoType != null)
         {
            String marshalled = SimpleTypeBindings.marshal(autoType.getLocalPart(), o, null);
            content.characters(marshalled.toCharArray(), 0, marshalled.length());
            return true;
         }
         else
         {
            if(ignoreUnresolvedWildcard)
            {
               log.warn("Failed to marshal wildcard. Class mapping not found for " +
                  o.getClass() +
                  "@" +
                  o.hashCode() +
                  ": " + o
               );
               return true;
            }
            else
            {
               throw new IllegalStateException("Failed to marshal wildcard. Class mapping not found for " +
                  o.getClass() +
                  "@" +
                  o.hashCode() +
                  ": " + o
               );
            }
         }
      }

      Object parentRoot = this.root;
      Stack parentStack = this.stack;
      SchemaBinding parentSchema = this.schema;

      this.root = o;
      this.stack = new StackImpl();
      this.schema = XsdBinder.bind(mapping.schemaUrl, schemaResolver);

      boolean marshalled;
      if(mapping.elementName != null)
      {
         ElementBinding elDec = schema.getElement(mapping.elementName);
         if(elDec == null)
         {
            throw new JBossXBRuntimeException("Element " + mapping.elementName + " is not declared in the schema.");
         }

         marshalled = marshalElementOccurence(elDec, root, particle.getMinOccurs() == 0, declareNs);
      }
      else if(mapping.typeName != null)
      {
         TypeBinding typeDef = schema.getType(mapping.typeName);
         if(typeDef == null)
         {
            throw new JBossXBRuntimeException("Type " +
               mapping.typeName +
               " is not defined in the schema."
            );
         }

         if(wildcard.getQName() == null)
         {
            throw new JBossXBRuntimeException("Expected the wildcard to have a non-null QName.");
         }

         ElementBinding element = new ElementBinding(schema, wildcard.getQName(), typeDef);
         marshalled = marshalElementOccurence(element, root, particle.getMinOccurs() == 0, declareNs);
      }
      else
      {
         throw new JBossXBRuntimeException("Class mapping for " +
            mapping.cls +
            " is associated with neither global element name nor global type name."
         );
      }

      this.root = parentRoot;
      this.stack = parentStack;
      this.schema = parentSchema;

      return marshalled;
   }

   private boolean marshalModelGroup(ModelGroupBinding modelGroup, boolean declareNs)
   {
      boolean marshalled;
      if(modelGroup instanceof AllBinding)
      {
         marshalled = marshalModelGroupAll(modelGroup.getParticles(), declareNs);
      }
      else if(modelGroup instanceof ChoiceBinding)
      {
         marshalled = marshalModelGroupChoice(modelGroup.getParticles(), declareNs);
      }
      else
      {
         marshalled = marshalModelGroupSequence(modelGroup, declareNs);
      }
      return marshalled;
   }

   private boolean marshalModelGroupAll(Collection particles, boolean declareNs)
   {
      boolean marshalled = false;
      for(Iterator i = particles.iterator(); i.hasNext();)
      {
         ParticleBinding particle = (ParticleBinding)i.next();
         marshalled |= marshalParticle(particle, declareNs);
      }
      return marshalled;
   }

   private boolean marshalModelGroupChoice(Collection particles, boolean declareNs)
   {
      boolean marshalled = false;
      Content mainContent = this.content;
      for(Iterator i = particles.iterator(); i.hasNext() && !marshalled;)
      {
         ParticleBinding particle = (ParticleBinding)i.next();
         this.content = new Content();
         marshalled = marshalParticle(particle, declareNs);
      }

      if(marshalled)
      {
         mainContent.append(this.content);
      }
      this.content = mainContent;

      return marshalled;
   }

   private boolean marshalModelGroupSequence(ModelGroupBinding sequence, boolean declareNs)
   {
      // if sequence is bound to a collection,
      // we assume the iterator over the collection is in sync with the particle iterator
      Iterator valueIterator = null;
      if(!sequence.isSkip() && !stack.isEmpty())
      {
         Object o = stack.peek();
         if(o != null && (Collection.class.isAssignableFrom(o.getClass()) || o.getClass().isArray()))
         {
            valueIterator = getIterator(o);
         }
      }

      boolean marshalled = true;
      for(Iterator i = sequence.getParticles().iterator(); i.hasNext();)
      {
         if(valueIterator != null)
         {
            Object o = valueIterator.hasNext() ? valueIterator.next() : null;
            stack.push(o);
         }

         ParticleBinding particle = (ParticleBinding)i.next();
         marshalled &= marshalParticle(particle, declareNs);

         if(valueIterator != null)
         {
            stack.pop();
         }
      }
      return marshalled;
   }

   private String marshalCharacters(String elementUri,
                                    String elementPrefix,
                                    TypeBinding simpleType,
                                    Object value,
                                    AttributesImpl attrs)
   {
      String marshalled;
      if(simpleType.getItemType() != null)
      {
         TypeBinding itemType = simpleType.getItemType();
         if(Constants.NS_XML_SCHEMA.equals(itemType.getQName().getNamespaceURI()))
         {
            List list;
            if(value instanceof List)
            {
               list = (List)value;
            }
            else if(value.getClass().isArray())
            {
               list = asList(value);
            }
            else
            {
               // todo: qname are also not yet supported
               throw new JBossXBRuntimeException(
                  "Expected value for list type is an array or " + List.class.getName() + " but got: " + value
               );
            }

            marshalled = SimpleTypeBindings.marshalList(itemType.getQName().getLocalPart(), list, null);
         }
         else
         {
            throw new JBossXBRuntimeException("Marshalling of list types with item types not from " +
               Constants.NS_XML_SCHEMA + " is not supported."
            );
         }
      }
      else if(Constants.NS_XML_SCHEMA.equals(simpleType.getQName().getNamespaceURI()))
      {
         // todo: pass non-null namespace context
         String typeName = simpleType.getQName().getLocalPart();

         if(SimpleTypeBindings.XS_QNAME_NAME.equals(typeName) ||
            SimpleTypeBindings.XS_NOTATION_NAME.equals(typeName))
         {
            QName qNameValue = (QName)value;
            String prefixValue = qNameValue.getPrefix();
            if((elementUri != null && !qNameValue.getNamespaceURI().equals(elementUri) ||
               elementUri == null && qNameValue.getNamespaceURI().length() > 0
               ) &&
               (prefixValue.equals(elementPrefix) || prefixValue.length() == 0 && elementPrefix == null))
            {
               // how to best resolve this conflict?
               prefixValue += 'x';
               value = new QName(qNameValue.getNamespaceURI(), qNameValue.getLocalPart(), prefixValue);
            }

            attrs = new AttributesImpl(1);
            declareNs(attrs, prefixValue, qNameValue.getNamespaceURI());
         }

         marshalled = SimpleTypeBindings.marshal(typeName, value, null);
      }
      // todo: this is a quick fix for boolean pattern (0|1 or true|false) should be refactored
      else if(simpleType.getLexicalPattern() != null &&
         simpleType.getBaseType() != null &&
         Constants.QNAME_BOOLEAN.equals(simpleType.getBaseType().getQName()))
      {
         String item = (String)simpleType.getLexicalPattern().get(0);
         if(item.indexOf('0') != -1 && item.indexOf('1') != -1)
         {
            marshalled = ((Boolean)value).booleanValue() ? "1" : "0";
         }
         else
         {
            marshalled = ((Boolean)value).booleanValue() ? "true" : "false";
         }
      }
      else
      {
         if(simpleType.getLexicalEnumeration() != null)
         {
            Method getValue;
            try
            {
               getValue = value.getClass().getMethod("value", null);
            }
            catch(NoSuchMethodException e)
            {
               try
               {
                  getValue = value.getClass().getMethod("getValue", null);
               }
               catch(NoSuchMethodException e1)
               {
                  throw new JBossXBRuntimeException("Failed to find neither value() nor getValue() in " +
                     value.getClass() +
                     " which is bound to enumeration type " + simpleType.getQName()
                  );
               }
            }

            try
            {
               value = getValue.invoke(value, null);
            }
            catch(Exception e)
            {
               throw new JBossXBRuntimeException(
                  "Failed to invoke getValue() on " + value + " to get the enumeration value", e
               );
            }
         }

         marshalled = marshalCharacters(elementUri,
            elementPrefix,
            simpleType.getBaseType(),
            value, attrs
         );
      }
      return marshalled;
   }

   private void declareNs(AttributesImpl attrs, String prefix, String ns)
   {
      attrs.add(null,
         prefix,
         prefix.length() == 0 ? "xmlns" : "xmlns:" + prefix,
         null,
         ns
      );
   }

   private void writeNillable(String elementNs, String elementLocal, boolean nillable)
   {
      if(!supportNil)
      {
         return;
      }

      if(!nillable)
      {
         throw new JBossXBRuntimeException("Failed to marshal " +
            new QName(elementNs, elementLocal) +
            ": Java value is null but the element is not nillable."
         );
      }

      AttributesImpl attrs;
      String prefix = getPrefix(elementNs);
      if(prefix == null && elementNs != null && elementNs.length() > 0)
      {
         prefix = "ns_" + elementLocal;
         attrs = new AttributesImpl(2);
         attrs.add(null, prefix, "xmlns:" + prefix, null, elementNs);
      }
      else
      {
         attrs = new AttributesImpl(1);
      }

      String xsiPrefix = getPrefix(Constants.NS_XML_SCHEMA_INSTANCE);
      if(xsiPrefix == null)
      {
         xsiPrefix = "xsi";
         attrs.add(null,
            xsiPrefix,
            "xmlns:xsi",
            null,
            Constants.NS_XML_SCHEMA_INSTANCE
         );
      }

      String nilQName = xsiPrefix + ":nil";
      attrs.add(Constants.NS_XML_SCHEMA_INSTANCE, "nil", nilQName, null, "1");

      String qName = createQName(prefix, elementLocal);
      content.startElement(elementNs, elementLocal, qName, attrs);
      content.endElement(elementNs, elementLocal, qName);
   }

   private Object getElementValue(ElementBinding element,
                                  boolean ignoreLowLine,
                                  boolean ignoreNotFoundField)
   {
      Object value;
      Object peeked = stack.peek();
      if(peeked == null)
      {
         value = null;
      }
      else if(peeked instanceof Collection || peeked.getClass().isArray())
      {
         value = peeked;
      }
      else
      {
         String fieldName = null;
         PropertyMetaData propertyMetaData = element.getPropertyMetaData();
         if(propertyMetaData != null)
         {
            fieldName = propertyMetaData.getName();
         }

         if(fieldName == null)
         {
            fieldName = Util.xmlNameToFieldName(element.getQName().getLocalPart(), ignoreLowLine);
         }

         value = getChildren(element.getQName(), peeked, fieldName, ignoreNotFoundField);
         if(value == null)
         {
            value = getElementValue(element.getQName(), peeked, fieldName, ignoreNotFoundField);
         }
      }
      return value;
   }

   private void declareNs(AttributesImpl attrs)
   {
      for(Iterator i = prefixByUri.entrySet().iterator(); i.hasNext();)
      {
         Map.Entry entry = (Map.Entry)i.next();
         String localName = (String)entry.getValue();
         attrs.add(null,
            localName,
            localName == null || localName.length() == 0 ? "xmlns" : "xmlns:" + localName,
            null,
            (String)entry.getKey()
         );
      }
   }

   private static String createQName(String prefix, String local)
   {
      return prefix == null || prefix.length() == 0 ? local : prefix + ':' + local;
   }

   private static boolean isArrayWrapper(TypeBinding type)
   {
      boolean is = false;
      if(!type.isSimple())
      {
         ParticleBinding particle = type.getParticle();
         if(particle != null)
         {
            is = particle.getMaxOccursUnbounded() || particle.getMaxOccurs() > 1;
         }
      }
      return is;
   }

   private Iterator getIterator(Object value)
   {
      Iterator i = null;
      if(value instanceof Collection)
      {
         i = ((Collection)value).iterator();
      }
      else if(value.getClass().isArray())
      {
         final Object arr = value;
         i = new Iterator()
         {
            private int curInd = 0;
            private int length = Array.getLength(arr);

            public boolean hasNext()
            {
               return curInd < length;
            }

            public Object next()
            {
               return Array.get(arr, curInd++);
            }

            public void remove()
            {
               throw new UnsupportedOperationException("remove is not implemented.");
            }
         };
      }
      else if(value instanceof Iterator)
      {
         i = (Iterator)value;
      }
      else
      {
         //throw new JBossXBRuntimeException("Unexpected type for children: " + value.getClass());
      }
      return i;
   }

   private static Object getChildren(QName qName, Object o,
                                     String fieldName,
                                     boolean ignoreNotFoundField)
   {
      Object children = null;
      if(!writeAsValue(o.getClass()))
      {
         children = getJavaValue(qName, fieldName, o, true, ignoreNotFoundField);
      }
      return children;
   }

   private static Object getJavaValue(QName qName, String fieldName,
                                      Object o,
                                      boolean forComplexType,
                                      boolean ignoreNotFoundField)
   {
      Method getter = null;
      Field field = null;
      Class fieldType = null;

      try
      {
         getter = Classes.getAttributeGetter(o.getClass(), fieldName);
         fieldType = getter.getReturnType();
      }
      catch(NoSuchMethodException e)
      {
         try
         {
            field = o.getClass().getField(fieldName);
            fieldType = field.getType();
         }
         catch(NoSuchFieldException e3)
         {
            if(ignoreNotFoundField)
            {
               if(log.isTraceEnabled())
               {
                  log.trace("Found neither getter nor field for " + qName + " in " + o.getClass());
               }
            }
            else
            {
               throw new JBossXBRuntimeException("Found neither getter nor field for " +
                  qName +
                  " in "
                  + o.getClass()
               );
            }
         }
      }

      Object value = null;
      if(fieldType != null && (!forComplexType || forComplexType && !writeAsValue(fieldType)))
      {
         if(getter != null)
         {
            try
            {
               value = getter.invoke(o, null);
            }
            catch(Exception e)
            {
               log.error("Failed to invoke getter '" + getter + "' on object: " + o);
               throw new JBossXBRuntimeException("Failed to provide value for " + qName + " from " + o, e);
            }
         }
         else
         {
            try
            {
               value = field.get(o);
            }
            catch(Exception e)
            {
               log.error("Failed to invoke get on field '" + field + "' on object: " + o);
               throw new JBossXBRuntimeException("Failed to provide value for " + qName + " from " + o, e);
            }
         }
      }

      return value;
   }

   private static Object getElementValue(QName qName, Object o,
                                         String fieldName,
                                         boolean ignoreNotFoundField)
   {
      Object value;
      if(writeAsValue(o.getClass()))
      {
         value = o;
      }
      else
      {
         value = getJavaValue(qName, fieldName, o, false, ignoreNotFoundField);
      }
      return value;
   }

   private static boolean writeAsValue(final Class type)
   {
      return Classes.isPrimitive(type) ||
         type == String.class ||
         type == java.util.Date.class ||
         type == java.math.BigDecimal.class ||
         type == java.math.BigInteger.class;
   }

   private static boolean isRepeatable(ParticleBinding particle)
   {
      return particle.getMaxOccursUnbounded() || particle.getMaxOccurs() > 1 || particle.getMinOccurs() > 1;
   }

   /**
    * Adds xsi:type attribute and optionally declares namespaces for xsi and type's namespace.
    *
    * @param typeQName the type to declare xsi:type attribute for
    * @param attrs     the attributes to add xsi:type attribute to
    * @return prefix for the type's ns if it was generated
    */
   private String declareXsiType(QName typeQName, AttributesImpl attrs)
   {
      String result = null;
      if(!prefixByUri.containsKey(Constants.NS_XML_SCHEMA_INSTANCE))
      {
         attrs.add(Constants.NS_XML_SCHEMA, "xmlns", "xmlns:xsi", null, Constants.NS_XML_SCHEMA_INSTANCE);
      }

      String pref = getPrefix(typeQName.getNamespaceURI());
      if(pref == null)
      {
         // the ns is not declared
         result = pref = typeQName.getLocalPart() + "_ns";
      }

      String qName = pref == null ? typeQName.getLocalPart() : pref + ':' + typeQName.getLocalPart();
      attrs.add(Constants.NS_XML_SCHEMA_INSTANCE, "type", "xsi:type", null, qName);
      return result;
   }

   private static final List asList(final Object arr)
   {
      return new AbstractList()
      {
         private final Object array = arr;

         public Object get(int index)
         {
            return Array.get(array, index);
         }

         public int size()
         {
            return Array.getLength(array);
         }
      };
   }

   private static class JAFDataSource
      implements DataSource
   {
      public final byte[] bytes;
      public final String contentType;

      public JAFDataSource(Object o, String contentType)
      {
         if(o instanceof byte[])
         {
            bytes = (byte[])o;
         }
         else
         {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = null;
            try
            {
               oos = new ObjectOutputStream(baos);
               oos.writeObject(o);
            }
            catch(IOException e)
            {
               throw new JBossXBRuntimeException("XOP failed to serialize object " + o + ": " + e.getMessage());
            }
            finally
            {
               if(oos != null)
               {
                  try
                  {
                     oos.close();
                  }
                  catch(IOException e)
                  {
                  }
               }
            }
            bytes = baos.toByteArray();
         }

         this.contentType = contentType;
      }

      public String getContentType()
      {
         return contentType;
      }

      public InputStream getInputStream() throws IOException
      {
         return new ObjectInputStream(new ByteArrayInputStream(bytes));
      }

      public String getName()
      {
         throw new UnsupportedOperationException("getName is not implemented.");
      }

      public OutputStream getOutputStream() throws IOException
      {
         throw new UnsupportedOperationException("getOutputStream is not implemented.");
      }
   }
}
