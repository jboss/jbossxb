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
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import org.jboss.logging.Logger;
import org.jboss.util.Classes;
import org.jboss.xb.binding.AbstractMarshaller;
import org.jboss.xb.binding.AttributesImpl;
import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.Content;
import org.jboss.xb.binding.ContentWriter;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.ObjectModelProvider;
import org.jboss.xb.binding.SimpleTypeBindings;
import org.jboss.xb.binding.Util;
import org.jboss.xb.binding.metadata.PropertyMetaData;
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
      SchemaBinding model = XsdBinder.bind(xsdReader, null, schemaResolver);
      marshallInternal(provider, root, model, writer);
   }

   public void marshal(String xsdURL, ObjectModelProvider provider, Object root, Writer writer) throws IOException,
      SAXException
   {
      SchemaBinding model = XsdBinder.bind(xsdURL, schemaResolver);
      marshallInternal(provider, root, model, writer);
   }

   public void marshal(SchemaBinding model, ObjectModelProvider provider, Object root, Writer writer)
      throws IOException,
      SAXException
   {
      marshallInternal(provider, root, model, writer);
   }

   private void marshallInternal(ObjectModelProvider provider, Object root, SchemaBinding model, Writer writer)
      throws IOException, SAXException
   {
      if(model == null)
      {
         throw new JBossXBRuntimeException("XSModel is not available!");
      }

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

         TypeBinding type = model.getType(rootTypeQName);
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
               true
            );
            stack.pop();
         }
         else
         {
            ElementBinding element = new ElementBinding(model, rootQName, type);
            marshalElement(element, 1, 1, true,
               model.isIgnoreLowLine(), model.isIgnoreUnresolvedFieldOrClass()
            );
         }
      }
      else if(rootQNames.isEmpty())
      {
         Iterator elements = model.getElements();
         if(!elements.hasNext())
         {
            throw new JBossXBRuntimeException("The schema doesn't contain global element declarations.");
         }

         while(elements.hasNext())
         {
            ElementBinding element = (ElementBinding)elements.next();
            marshalElement(element, 1, 1,
               true,
               model.isIgnoreLowLine(),
               model.isIgnoreUnresolvedFieldOrClass()
            );// todo fix min/max
         }
      }
      else
      {
         for(int i = 0; i < rootQNames.size(); ++i)
         {
            QName qName = (QName)rootQNames.get(i);
            ElementBinding element = model.getElement(qName);
            if(element == null)
            {
               Iterator components = model.getElements();
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

            marshalElement(element,
               1,
               1,
               true,
               model.isIgnoreLowLine(),
               model.isIgnoreUnresolvedFieldOrClass()
            );// todo fix min/max
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

   private boolean marshalElement(ElementBinding element,
                                  int minOccurs,
                                  int maxOccurs,
                                  boolean declareNs,
                                  boolean ignoreLowLine,
                                  boolean ignoreNotFoundField)
   {
      String elementNs = element.getQName().getNamespaceURI();
      String elementLocal = element.getQName().getLocalPart();
      TypeBinding type = element.getType();
      boolean nillable = element.isNillable();

      Object value;
      if(stack.isEmpty())
      {
         value = root;
         if(value == null)
         {
            return false;
         }
      }
      else
      {
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
               fieldName = Util.xmlNameToFieldName(elementLocal, ignoreLowLine);
            }

            value = getChildren(element.getQName(), peeked, fieldName, ignoreNotFoundField);
            if(value == null)
            {
               value = getElementValue(element.getQName(), peeked, fieldName, ignoreNotFoundField);
            }
         }
      }

      boolean result = minOccurs == 0 || value != null;
      boolean trace = log.isTraceEnabled() && result;
      if(trace)
      {
         String prefix = (String)prefixByUri.get(elementNs);
         log.trace("started element ns=" + elementNs + ", local=" + elementLocal + ", prefix=" + prefix);
      }

      if(value != null)
      {
         stack.push(value);

         if(maxOccurs != 1)
         {
            Iterator i = getIterator(value);
            if(i == null)
            {
               marshalElementType(elementNs, elementLocal, type, declareNs, nillable);
            }
            else
            {
               while(i.hasNext())
               {
                  Object item = i.next();
                  if(item == null)
                  {
                     writeNillable(elementNs, elementLocal, nillable);
                  }
                  else
                  {
                     stack.push(item);
                     marshalElementType(elementNs, elementLocal, type, declareNs, nillable);
                     stack.pop();
                  }
               }
            }
         }
         else
         {
            marshalElementType(elementNs, elementLocal, type, declareNs, nillable);
         }

         stack.pop();
      }
      else
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
                                   boolean nillable)
   {
      if(type.isSimple())
      {
         marshalSimpleType(elementNs, elementLocal, type, declareNs, nillable);
      }
      else
      {
         marshalComplexType(elementNs, elementLocal, type, declareNs);
      }
   }

   private void marshalSimpleType(String elementUri,
                                  String elementLocal,
                                  TypeBinding type,
                                  boolean declareNs,
                                  boolean nillable)
   {
      Object value = stack.peek();
      if(value != null)
      {
         AttributesImpl attrs = null;
         String prefix = (String)prefixByUri.get(elementUri);
         boolean genPrefix = prefix == null && elementUri != null && elementUri.length() > 0;
         if(genPrefix)
         {
            prefix = "ns_" + elementLocal;
         }

         String marshalled;
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

            marshalled = SimpleTypeBindings.marshal(typeName, value, null);
         }
         else
         {
            marshalled = value.toString();
         }

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
                                   TypeBinding type,
                                   boolean declareNs)
   {
      ParticleBinding particle = type.getParticle();

      Collection attributeUses = type.getAttributes();
      int attrsTotal = declareNs ? prefixByUri.size() + attributeUses.size() : attributeUses.size();
      AttributesImpl attrs = attrsTotal > 0 ? new AttributesImpl(attrsTotal) : null;

      if(declareNs && !prefixByUri.isEmpty())
      {
         declareNs(attrs);
      }

      for(Iterator i = attributeUses.iterator(); i.hasNext();)
      {
         AttributeBinding attrUse = (AttributeBinding)i.next();
         QName attrQName = attrUse.getQName();
         Object attrValue = getElementValue(attrUse.getQName(), stack.peek(),
            attrQName.getLocalPart(),
            particle.getTerm().getSchema().isIgnoreUnresolvedFieldOrClass()
         );
         if(attrValue != null)
         {
            //todo: fix qName
            attrs.add(attrQName.getNamespaceURI(),
               attrQName.getLocalPart(),
               attrQName.getLocalPart(),
               attrUse.getType().getQName().getLocalPart(),
               attrValue.toString()
            );
         }
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

      String qName = createQName(prefix, elementLocalName);
      content.startElement(elementNsUri, elementLocalName, qName, attrs);

      if(particle != null)
      {
         marshalParticle(particle, false);
      }

      content.endElement(elementNsUri, elementLocalName, qName);

      if(genPrefix)
      {
         prefixByUri.remove(elementNsUri);
      }
   }

   private boolean marshalParticle(ParticleBinding particle, boolean declareNs)
   {
      boolean marshalled;
      TermBinding term = particle.getTerm();
      if(term.isModelGroup())
      {
         marshalled = marshalModelGroup(particle, declareNs);
      }
      else if(term.isWildcard())
      {
         marshalled = marshalWildcard((WildcardBinding)term, declareNs);
      }
      else
      {
         ElementBinding element = (ElementBinding)term;
         marshalled =
            marshalElement(element,
               particle.getMinOccurs(),
               particle.getMaxOccurs(),
               declareNs,
               term.getSchema().isIgnoreLowLine(),
               term.getSchema().isIgnoreUnresolvedFieldOrClass()
            );
      }
      return marshalled;
   }

   private boolean marshalWildcard(WildcardBinding wildcard, boolean declareNs)
   {
      // todo class resolution
      Object o = stack.peek();
      ClassMapping mapping = getClassMapping(o.getClass());
      if(mapping == null)
      {
         throw new IllegalStateException("Failed to marshal wildcard. Class mapping not found for " +
            o.getClass() +
            "@" +
            o.hashCode() +
            ": " + o
         );
      }

      Object parentRoot = this.root;
      Stack parentStack = this.stack;

      this.root = o;
      this.stack = new StackImpl();

      boolean marshalled;
      SchemaBinding model = XsdBinder.bind(mapping.schemaUrl, schemaResolver);
      if(mapping.elementName != null)
      {
         ElementBinding elDec = model.getElement(mapping.elementName);
         marshalled =
            marshalElement(elDec,
               1,
               1,
               declareNs,
               model.isIgnoreLowLine(),
               model.isIgnoreUnresolvedFieldOrClass()
            );// todo fix min/max
      }
      else if(mapping.typeName != null)
      {
         TypeBinding typeDef = model.getType(mapping.typeName);
         if(wildcard.getQName() == null)
         {
            throw new JBossXBRuntimeException("Expected the wildcard to have a non-null QName.");
         }

         ElementBinding element = new ElementBinding(model, new QName(wildcard.getQName().getNamespaceURI(),
            wildcard.getQName().getLocalPart()
         ), typeDef
         );
         marshalled = marshalElement(element,
            1,
            1,
            declareNs,
            model.isIgnoreLowLine(),
            model.isIgnoreUnresolvedFieldOrClass()
         );
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

      return marshalled;
   }

   private boolean marshalModelGroup(ParticleBinding particle, boolean declareNs)
   {
      ModelGroupBinding modelGroup = (ModelGroupBinding)particle.getTerm();
      boolean marshalled = true;
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

         Object value = getChildren(null, stack.peek(), propertyMetaData.getName(),
            modelGroup.getSchema().isIgnoreUnresolvedFieldOrClass()
         );

         if(particle.isRepeatable() && value != null)
         {
            Iterator i = getIterator(value);
            if(i == null)
            {
               throw new JBossXBRuntimeException("Failed to create an iterator for " + value);
            }

            while(i.hasNext() && marshalled)
            {
               value = i.next();
               stack.push(value);
               marshalled &= marshalModelGroup(modelGroup, declareNs);
               stack.pop();
            }
         }
         else
         {
            stack.push(value);
            marshalled = marshalModelGroup(modelGroup, declareNs);
            stack.pop();
         }
      }
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
         marshalled = marshalModelGroupSequence(modelGroup.getParticles(), declareNs);
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

   private boolean marshalModelGroupSequence(Collection particles, boolean declareNs)
   {
      boolean marshalled = true;
      for(Iterator i = particles.iterator(); i.hasNext();)
      {
         ParticleBinding particle = (ParticleBinding)i.next();
         marshalled &= marshalParticle(particle, declareNs);
      }
      return marshalled;
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
            ": Java value is null but the element is not nillable.");
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
                  log.trace("getChildren: found neither getter nor field for " + qName + " in " + o.getClass());
               }
            }
            else
            {
               throw new JBossXBRuntimeException("getChildren: found neither getter nor field for " +
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
         type == java.util.Date.class;
   }
}
