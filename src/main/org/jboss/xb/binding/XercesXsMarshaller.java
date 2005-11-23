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

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSWildcard;
import org.jboss.logging.Logger;
import org.jboss.xb.binding.metadata.marshalling.FieldBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBindingResolver;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class XercesXsMarshaller
   extends AbstractMarshaller
{
   private static final Logger log = Logger.getLogger(XercesXsMarshaller.class);

   private Stack stack = new StackImpl();

   /**
    * ObjectModelProvider for this marshaller
    */
   private GenericObjectModelProvider provider;

   /**
    * Content the result is written to
    */
   private Content content = new Content();

   private final Map prefixByUri = new HashMap();

   private Object root;

   /**
    * Whether NULL values should be ignored or marshalled as xsi:nil='1'
    */
   private boolean supportNil;

   private QName rootTypeQName;

   private SchemaBindingResolver schemaResolver;

   private XSModel model;

   private boolean ignoreUnresolvedWildcard;

   private XSAttributeUse currentAttribute;
   private XSTypeDefinition currentElementType;

   private String simpleContentProperty = "value";

   private MarshallingContext ctx = new MarshallingContext()
   {
      public FieldBinding getFieldBinding()
      {
         throw new UnsupportedOperationException("getFieldBinding is not implemented.");
      }

      public boolean isAttributeRequired()
      {
         if(currentAttribute == null)
         {
            throw new JBossXBRuntimeException("There is no current attribute!");
         }
         return currentAttribute.getRequired();
      }

      public boolean isTypeComplex()
      {
         if(currentElementType == null)
         {
            throw new JBossXBRuntimeException("There is no current element!");
         }
         return currentElementType.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE;
      }

      public String getSimpleContentProperty()
      {
         return simpleContentProperty;
      }
   };

   public String getSimpleContentProperty()
   {
      return simpleContentProperty;
   }

   public void setSimpleContentProperty(String simpleContentProperty)
   {
      this.simpleContentProperty = simpleContentProperty;
   }

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

   /**
    * Defines a namespace. The namespace declaration will appear in the root element.
    * <p>If <code>prefix</code> argument is <code>null</code> or is an empty string then
    * the passed in URI will be used for the default namespace, i.e. <code>xmlns</code>.
    * Otherwise, the declaration will follow the format <code>xmlns:prefix=uri</code>.
    * <p>If the namespace with the given prefix was already declared, its value is overwritten.
    *
    * @param prefix the prefix for the namespace to declare (can be null or empty string)
    * @param uri    the URI of the namespace.
    */
   public void declareNamespace(String prefix, String uri)
   {
      if(prefix == null)
      {
         return;
      }
      prefixByUri.put(uri, prefix);
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
      XSModel model = Util.loadSchema(xsdReader, null, schemaResolver);
      marshallInternal(provider, root, model, writer);
   }

   public void marshal(String xsdURL, ObjectModelProvider provider, Object root, Writer writer) throws IOException,
      SAXException
   {
      XSModel model = Util.loadSchema(xsdURL, schemaResolver);
      marshallInternal(provider, root, model, writer);
   }

   public void marshal(XSModel model, ObjectModelProvider provider, Object root, Writer writer) throws IOException,
      SAXException
   {
      marshallInternal(provider, root, model, writer);
   }

   private void marshallInternal(ObjectModelProvider provider, Object root, XSModel model, Writer writer)
      throws IOException, SAXException
   {
      if(model == null)
      {
         throw new JBossXBRuntimeException("XSModel is not available!");
      }

      this.model = model;
      this.provider = provider instanceof GenericObjectModelProvider ?
         (GenericObjectModelProvider)provider : new DelegatingObjectModelProvider(provider);

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

         XSTypeDefinition type = model.getTypeDefinition(rootTypeQName.getLocalPart(),
            rootTypeQName.getNamespaceURI()
         );
         if(type == null)
         {
            throw new JBossXBRuntimeException("Global type definition is not found: " + rootTypeQName);
         }

         if(isArrayWrapper(type))
         {
            Object o = provider.getRoot(root, null, rootQName.getNamespaceURI(), rootQName.getLocalPart());
            stack.push(o);
            marshalComplexType(rootQName.getNamespaceURI(),
               rootQName.getLocalPart(),
               (XSComplexTypeDefinition)type,
               true
            );
            stack.pop();
         }
         else
         {
            Object o = provider.getRoot(root, null, rootQName.getNamespaceURI(), rootQName.getLocalPart());
            stack.push(o);
            marshalElement(rootQName.getNamespaceURI(), rootQName.getLocalPart(), type, false, true, true);
            stack.pop();
         }
      }
      else if(rootQNames.isEmpty())
      {
         XSNamedMap components = model.getComponents(XSConstants.ELEMENT_DECLARATION);
         if(components.getLength() == 0)
         {
            throw new JBossXBRuntimeException("The schema doesn't contain global element declarations.");
         }

         for(int i = 0; i < components.getLength(); ++i)
         {
            XSElementDeclaration element = (XSElementDeclaration)components.item(i);
            Object o = provider.getRoot(root, null, element.getNamespace(), element.getName());
            stack.push(o);
            marshalElement(element.getNamespace(),
               element.getName(),
               element.getTypeDefinition(),
               element.getNillable(),
               true,
               true
            );
            stack.pop();
         }
      }
      else
      {
         for(int i = 0; i < rootQNames.size(); ++i)
         {
            QName qName = (QName)rootQNames.get(i);
            XSElementDeclaration element = model.getElementDeclaration(qName.getLocalPart(), qName.getNamespaceURI());
            if(element == null)
            {
               XSNamedMap components = model.getComponents(XSConstants.ELEMENT_DECLARATION);
               String roots = "";
               for(int j = 0; j < components.getLength(); ++j)
               {
                  XSObject xsObject = components.item(j);
                  if(j > 0)
                  {
                     roots += ", ";
                  }
                  roots += "{" + xsObject.getNamespace() + "}" + xsObject.getName();
               }
               throw new IllegalStateException("Root element not found: " + qName + " among " + roots);
            }

            Object o = provider.getRoot(root, null, element.getNamespace(), element.getName());
            stack.push(o);
            marshalElement(element.getNamespace(),
               element.getName(),
               element.getTypeDefinition(),
               element.getNillable(),
               true,
               true
            );
            stack.pop();
         }
      }

      content.endDocument();

      // version & encoding
      writeXmlVersion(writer);

      ContentWriter contentWriter = new ContentWriter(writer,
         propertyIsTrueOrNotSet(Marshaller.PROP_OUTPUT_INDENTATION)
      );
      content.handleContent(contentWriter);

      if(log.isTraceEnabled())
      {
         java.io.StringWriter traceWriter = new java.io.StringWriter();
         contentWriter = new ContentWriter(traceWriter,
            propertyIsTrueOrNotSet(Marshaller.PROP_OUTPUT_INDENTATION)
         );
         content.handleContent(contentWriter);
         log.trace("marshalled:\n" + traceWriter.getBuffer().toString());
      }
   }

   private boolean marshalElement(String elementNs, String elementLocal,
                                  XSTypeDefinition type,
                                  boolean optional,
                                  boolean nillable,
                                  boolean declareNs)
   {
      Object value = stack.peek();
      boolean result = value != null || value == null && (optional || nillable);
      boolean trace = log.isTraceEnabled() && result;
      if(trace)
      {
         String prefix = (String)prefixByUri.get(elementNs);
         log.trace("started element ns=" + elementNs + ", local=" + elementLocal + ", prefix=" + prefix);
      }

      if(value != null)
      {
         marshalElementType(elementNs, elementLocal, type, declareNs, nillable);
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
                                   XSTypeDefinition type,
                                   boolean declareNs,
                                   boolean nillable)
   {
      switch(type.getTypeCategory())
      {
         case XSTypeDefinition.SIMPLE_TYPE:
            marshalSimpleType(elementNs, elementLocal, (XSSimpleTypeDefinition)type, declareNs, nillable);
            break;
         case XSTypeDefinition.COMPLEX_TYPE:
            marshalComplexType(elementNs, elementLocal, (XSComplexTypeDefinition)type, declareNs);
            break;
         default:
            throw new IllegalStateException("Unexpected type category: " + type.getTypeCategory());
      }
   }

   private void marshalSimpleType(String elementUri,
                                  String elementLocal,
                                  XSSimpleTypeDefinition type,
                                  boolean declareNs,
                                  boolean nillable)
   {
      Object value = stack.peek();
      if(value != null)
      {
         String prefix = (String)prefixByUri.get(elementUri);
         boolean genPrefix = prefix == null && elementUri != null && elementUri.length() > 0;
         if(genPrefix)
         {
            prefix = "ns_" + elementLocal;
         }

         AttributesImpl attrs = null;
         String typeName = type.getName();
         if(SimpleTypeBindings.XS_QNAME_NAME.equals(typeName) ||
            SimpleTypeBindings.XS_NOTATION_NAME.equals(typeName) ||
            type.getItemType() != null &&
            (SimpleTypeBindings.XS_QNAME_NAME.equals(type.getItemType().getName()) ||
            SimpleTypeBindings.XS_NOTATION_NAME.equals(type.getItemType().getName())
            )
         )
         {
            attrs = new AttributesImpl(5);
         }

         String marshalled = marshalCharacters(elementUri, prefix, type, value, attrs);

         if(declareNs && !prefixByUri.isEmpty())
         {
            if(attrs == null)
            {
               attrs = new AttributesImpl(prefixByUri.size());
            }
            declareNs(attrs);
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
                                   XSComplexTypeDefinition type,
                                   boolean declareNs)
   {
      Object o = stack.peek();
      XSParticle particle = type.getParticle();

      XSObjectList attributeUses = type.getAttributeUses();
      int attrsTotal = declareNs ? prefixByUri.size() + attributeUses.getLength() : attributeUses.getLength();
      AttributesImpl attrs = attrsTotal > 0 ? new AttributesImpl(attrsTotal) : null;

      if(declareNs && !prefixByUri.isEmpty())
      {
         declareNs(attrs);
      }

      String prefix = (String)prefixByUri.get(elementNsUri);
      boolean genPrefix = prefix == null && elementNsUri != null && elementNsUri.length() > 0;
      if(genPrefix)
      {
         // todo: it's possible that the generated prefix already mapped. this should be fixed
         prefix = "ns_" + elementLocalName;
         prefixByUri.put(elementNsUri, prefix);
         if(attrs == null)
         {
            attrs = new AttributesImpl(1);
         }
         attrs.add(null, prefix, "xmlns:" + prefix, null, elementNsUri);
      }

      for(int i = 0; i < attributeUses.getLength(); ++i)
      {
         currentAttribute = (XSAttributeUse)attributeUses.item(i);
         XSAttributeDeclaration attrDec = currentAttribute.getAttrDeclaration();
         String attrNs = attrDec.getNamespace();
         String attrLocal = attrDec.getName();
         Object attrValue = provider.getAttributeValue(o, ctx, attrNs, attrLocal);

         if(attrValue != null)
         {
            if(attrs == null)
            {
               attrs = new AttributesImpl(5);
            }

            String attrPrefix = null;
            if(attrNs != null)
            {
               attrPrefix = (String)prefixByUri.get(attrNs);
               if(attrPrefix == null && attrNs != null && attrNs.length() > 0)
               {
                  attrPrefix = "ns_" + attrLocal;
                  attrs.add(null, attrPrefix, "xmlns:" + attrPrefix, null, attrNs);
               }
            }

            String qName = attrPrefix == null || attrPrefix.length() == 0 ? attrLocal : attrPrefix + ":" + attrLocal;

            // todo: this is a quick fix for boolean pattern (0|1 or true|false) should be refactored
            XSSimpleTypeDefinition attrType = attrDec.getTypeDefinition();
            if(attrType.getItemType() != null)
            {
               XSSimpleTypeDefinition itemType = attrType.getItemType();
               if(Constants.NS_XML_SCHEMA.equals(itemType.getNamespace()))
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
                        attrValue
                     );
                  }

                  if(Constants.QNAME_QNAME.getLocalPart().equals(itemType.getName()))
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
                              itemPrefix = (String)prefixByUri.get(itemNs);
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

                  attrValue = SimpleTypeBindings.marshalList(itemType.getName(), list, null);
               }
               else
               {
                  throw new JBossXBRuntimeException("Marshalling of list types with item types not from " +
                     Constants.NS_XML_SCHEMA + " is not supported."
                  );
               }
            }
            else if(attrType.getLexicalPattern() != null &&
               attrType.derivedFrom(Constants.NS_XML_SCHEMA,
                  Constants.QNAME_BOOLEAN.getLocalPart(),
                  XSConstants.DERIVATION_RESTRICTION
               ))
            {
               String item = attrType.getLexicalPattern().item(0);
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

            attrs.add(attrNs,
               attrLocal,
               qName,
               attrDec.getTypeDefinition().getName(),
               attrValue.toString()
            );
         }
      }
      currentAttribute = null;

      String characters = null;
      if(type.getSimpleType() != null)
      {
         Object value = getElementValue(elementNsUri, elementLocalName, type);
         if(value != null)
         {
            XSSimpleTypeDefinition simpleType = type.getSimpleType();
            String typeName = simpleType.getName();
            if(attrs == null && (SimpleTypeBindings.XS_QNAME_NAME.equals(typeName) ||
               SimpleTypeBindings.XS_NOTATION_NAME.equals(typeName) ||
               simpleType.getItemType() != null &&
               (SimpleTypeBindings.XS_QNAME_NAME.equals(simpleType.getItemType().getName()) ||
               SimpleTypeBindings.XS_NOTATION_NAME.equals(simpleType.getItemType().getName())
               )
               )
            )
            {
               attrs = new AttributesImpl(5);
            }

            characters = marshalCharacters(elementNsUri, prefix, simpleType, value, attrs);
         }
      }

      String qName = createQName(prefix, elementLocalName);
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

      if(genPrefix)
      {
         prefixByUri.remove(elementNsUri);
      }
   }

   private boolean marshalParticle(XSParticle particle, boolean declareNs)
   {
      boolean marshalled;
      XSTerm term = particle.getTerm();
      Object o;
      Iterator i;
      switch(term.getType())
      {
         case XSConstants.MODEL_GROUP:
            o = stack.peek();
            i = o != null && isRepeatable(particle) ? getIterator(o) : null;
            if(i != null)
            {
               marshalled = true;
               while(i.hasNext() && marshalled)
               {
                  Object value = i.next();
                  stack.push(value);
                  marshalled = marshalModelGroup(particle, declareNs);
                  stack.pop();
               }
            }
            else
            {
               marshalled = marshalModelGroup(particle, declareNs);
            }
            break;
         case XSConstants.WILDCARD:
            o = stack.peek();
            i = o != null && isRepeatable(particle) ? getIterator(o) : null;
            if(i != null)
            {
               marshalled = true;
               while(i.hasNext() && marshalled)
               {
                  Object value = i.next();
                  stack.push(value);
                  marshalled = marshalWildcard(particle, declareNs);
                  stack.pop();
               }
            }
            else
            {
               marshalled = marshalWildcard(particle, declareNs);
            }
            break;
         case XSConstants.ELEMENT_DECLARATION:
            XSElementDeclaration element = (XSElementDeclaration)term;
            o = getElementValue(element.getNamespace(), element.getName(), element.getTypeDefinition());
            i = o != null && isRepeatable(particle) ? getIterator(o) : null;
            if(i != null)
            {
               marshalled = true;
               while(i.hasNext() && marshalled)
               {
                  Object value = i.next();
                  stack.push(value);
                  marshalled = marshalElement(element.getNamespace(),
                     element.getName(),
                     element.getTypeDefinition(),
                     particle.getMinOccurs() == 0,
                     element.getNillable(),
                     declareNs
                  );
                  stack.pop();
               }
            }
            else
            {
               stack.push(o);
               marshalled = marshalElement(element.getNamespace(),
                  element.getName(),
                  element.getTypeDefinition(),
                  particle.getMinOccurs() == 0,
                  element.getNillable(),
                  declareNs
               );
               stack.pop();
            }
            break;
         default:
            throw new IllegalStateException("Unexpected term type: " + term.getType());
      }
      return marshalled;
   }

   private boolean marshalWildcard(XSParticle particle, boolean declareNs)
   {
      XSWildcard wildcard = (XSWildcard)particle.getTerm();
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
                  ": " + o);
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

      GenericObjectModelProvider parentProvider = this.provider;
      Object parentRoot = this.root;
      Stack parentStack = this.stack;
      XSModel parentModel = this.model;

      this.root = o;
      this.provider = mapping.provider;
      this.stack = new StackImpl();
      this.model = mapping.schemaUrl == null ? this.model : Util.loadSchema(mapping.schemaUrl, schemaResolver);

      boolean marshalled;
      if(mapping.elementName != null)
      {
         XSElementDeclaration elDec = model.getElementDeclaration(mapping.elementName.getLocalPart(),
            mapping.elementName.getNamespaceURI()
         );

         if(elDec == null)
         {
            throw new JBossXBRuntimeException("Element " + mapping.elementName + " is not declared in the schema.");
         }

         Object elementValue = provider.getRoot(root, null, elDec.getNamespace(), elDec.getName());
         stack.push(elementValue);
         marshalled = marshalElement(elDec.getNamespace(),
            elDec.getName(),
            elDec.getTypeDefinition(),
            particle.getMinOccurs() == 0,
            elDec.getNillable(),
            declareNs
         );
         stack.pop();
      }
      else if(mapping.typeName != null)
      {
         XSTypeDefinition typeDef = model.getTypeDefinition(mapping.typeName.getLocalPart(),
            mapping.typeName.getNamespaceURI()
         );

         if(typeDef == null)
         {
            List typeNames = new ArrayList();
            XSNamedMap types = model.getComponents(XSConstants.TYPE_DEFINITION);
            for(int i = 0; i < types.getLength(); ++i)
            {
               XSObject type = types.item(i);
               if(!Constants.NS_XML_SCHEMA.equals(type.getNamespace()))
               {
                  typeNames.add(new QName(type.getNamespace(), type.getName()));
               }
            }
            throw new JBossXBRuntimeException("Type " +
               mapping.typeName +
               " is not defined in the schema." +
               " Defined types are: " + typeNames
            );
         }

         Object elementValue = provider.getRoot(root, null, wildcard.getNamespace(), wildcard.getName());
         stack.push(elementValue);
         marshalled =
            marshalElement(wildcard.getNamespace(),
               wildcard.getName(),
               typeDef,
               particle.getMinOccurs() == 0,
               false,
               declareNs
            );
         stack.pop();
      }
      else
      {
         throw new JBossXBRuntimeException("Class mapping for " +
            mapping.cls +
            " is associated with neither global element name nor global type name."
         );
      }

      this.root = parentRoot;
      this.provider = parentProvider;
      this.stack = parentStack;
      this.model = parentModel;

      return marshalled;
   }

   private boolean marshalModelGroup(XSParticle particle, boolean declareNs)
   {
      XSModelGroup modelGroup = (XSModelGroup)particle.getTerm();
      boolean marshalled;
      switch(modelGroup.getCompositor())
      {
         case XSModelGroup.COMPOSITOR_ALL:
            marshalled = marshalModelGroupAll(modelGroup.getParticles(), declareNs);
            break;
         case XSModelGroup.COMPOSITOR_CHOICE:
            marshalled = marshalModelGroupChoice(modelGroup.getParticles(), declareNs);
            break;
         case XSModelGroup.COMPOSITOR_SEQUENCE:
            marshalled = marshalModelGroupSequence(modelGroup.getParticles(), declareNs);
            break;
         default:
            throw new IllegalStateException("Unexpected compsitor: " + modelGroup.getCompositor());
      }
      return marshalled;
   }

   private boolean marshalModelGroupAll(XSObjectList particles, boolean declareNs)
   {
      boolean marshalled = false;
      for(int i = 0; i < particles.getLength(); ++i)
      {
         XSParticle particle = (XSParticle)particles.item(i);
         marshalled |= marshalParticle(particle, declareNs);
      }
      return marshalled;
   }

   private boolean marshalModelGroupChoice(XSObjectList particles, boolean declareNs)
   {
      boolean marshalled = false;
      Content mainContent = this.content;
      for(int i = 0; i < particles.getLength() && !marshalled; ++i)
      {
         XSParticle particle = (XSParticle)particles.item(i);
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

   private boolean marshalModelGroupSequence(XSObjectList particles, boolean declareNs)
   {
      boolean marshalled = true;
      for(int i = 0; i < particles.getLength(); ++i)
      {
         XSParticle particle = (XSParticle)particles.item(i);
         marshalled &= marshalParticle(particle, declareNs);
      }
      return marshalled;
   }

   private String marshalCharacters(String elementUri,
                                    String elementPrefix,
                                    XSSimpleTypeDefinition type,
                                    Object value,
                                    AttributesImpl attrs)
   {
      String marshalled;
      if(type.getItemType() != null)
      {
         XSSimpleTypeDefinition itemType = type.getItemType();
         if(Constants.NS_XML_SCHEMA.equals(itemType.getNamespace()))
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

            marshalled = SimpleTypeBindings.marshalList(itemType.getName(), list, null);
         }
         else
         {
            throw new JBossXBRuntimeException("Marshalling of list types with item types not from " +
               Constants.NS_XML_SCHEMA + " is not supported."
            );
         }
      }
      else if(Constants.NS_XML_SCHEMA.equals(type.getNamespace()))
      {
         // todo: pass non-null namespace context
         String typeName = type.getName();

         if(SimpleTypeBindings.XS_QNAME_NAME.equals(typeName) ||
            SimpleTypeBindings.XS_NOTATION_NAME.equals(typeName))
         {
            QName qName = (QName)value;
            String prefixValue = qName.getPrefix();
            if((elementUri != null && !qName.getNamespaceURI().equals(elementUri) ||
               elementUri == null && qName.getNamespaceURI().length() > 0
               ) &&
               (prefixValue.equals(elementPrefix) || prefixValue.length() == 0 && elementPrefix == null))
            {
               // how to best resolve this conflict?
               prefixValue += 'x';
               qName = new QName(qName.getNamespaceURI(), qName.getLocalPart(), prefixValue);
            }

            declareNs(attrs, prefixValue, qName.getNamespaceURI());
            value = qName;
         }

         marshalled = SimpleTypeBindings.marshal(typeName, value, null);
      }
      // todo: this is a quick fix for boolean pattern (0|1 or true|false) should be refactored
      else if(type.getLexicalPattern() != null &&
         type.derivedFrom(Constants.NS_XML_SCHEMA,
            Constants.QNAME_BOOLEAN.getLocalPart(),
            XSConstants.DERIVATION_RESTRICTION
         ))
      {
         String item = type.getLexicalPattern().item(0);
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
         StringList lexicalEnumeration = type.getLexicalEnumeration();
         if(lexicalEnumeration != null && lexicalEnumeration.getLength() > 0)
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
                  List values = new ArrayList(lexicalEnumeration.getLength());
                  for(int i = 0; i < lexicalEnumeration.getLength(); ++i)
                  {
                     values.add(lexicalEnumeration.item(i));
                  }

                  throw new JBossXBRuntimeException("Failed to find neither value() nor getValue() in " +
                     value.getClass() +
                     " which is bound to enumeration type (" +
                     type.getNamespace() +
                     ", " +
                     type.getName() + "): " + values
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
            (XSSimpleTypeDefinition)type.getBaseType(),
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

   private Object getElementValue(String elementNs, String elementLocal, XSTypeDefinition type)
   {
      Object value;
      Object peeked = stack.isEmpty() ? root : stack.peek();
      if(peeked == null)
      {
         value = null;
      }
      else if(peeked instanceof Collection || peeked.getClass().isArray())
      {
         // collection is the provider
         value = peeked;
      }
      else
      {
         XSTypeDefinition parentType = currentElementType;
         currentElementType = type;

         value = provider.getChildren(peeked, null, elementNs, elementLocal);
         if(value == null)
         {
            value = provider.getElementValue(peeked, ctx, elementNs, elementLocal);
         }

         currentElementType = parentType;
      }
      return value;
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
      String prefix = (String)prefixByUri.get(elementNs);
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

      String xsiPrefix = (String)prefixByUri.get(Constants.NS_XML_SCHEMA_INSTANCE);
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

   private static boolean isArrayWrapper(XSTypeDefinition type)
   {
      boolean is = false;
      if(XSTypeDefinition.COMPLEX_TYPE == type.getTypeCategory())
      {
         XSComplexTypeDefinition cType = (XSComplexTypeDefinition)type;
         XSParticle particle = cType.getParticle();
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

   private static boolean isRepeatable(XSParticle particle)
   {
      return particle.getMaxOccursUnbounded() || particle.getMaxOccurs() > 1 || particle.getMinOccurs() > 1;
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
}
