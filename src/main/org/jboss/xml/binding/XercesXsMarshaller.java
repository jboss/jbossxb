/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

import org.apache.xerces.dom3.bootstrap.DOMImplementationRegistry;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSImplementation;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSWildcard;
import org.jboss.logging.Logger;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
    * Defines a namespace. The namespace declaration will appear in the root element.
    * <p>If <code>name</code> argument is <code>null</code> or is an empty string then
    * the passed in URI will be used for the default namespace, i.e. <code>xmlns</code>.
    * Otherwise, the declaration will follow the format <code>xmlns:name=uri</code>.
    * <p>If the namespace with the given name was already declared, its value is overwritten.
    *
    * @param name the name of the namespace to declare (can be null or empty string)
    * @param uri  the URI of the namespace.
    */
   public void declareNamespace(String name, String uri)
   {
      prefixByUri.put(uri, name);
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
      XSModel model = loadSchema(xsdReader);
      marshallInternal(provider, root, model, writer);
   }

   public void marshal(String xsdURL, ObjectModelProvider provider, Object root, Writer writer) throws IOException,
      SAXException
   {
      XSModel model = loadSchema(xsdURL);
      marshallInternal(provider, root, model, writer);
   }

   private void marshallInternal(ObjectModelProvider provider, Object root, XSModel model, Writer writer)
      throws IOException, SAXException
   {
      this.provider = provider instanceof GenericObjectModelProvider ?
         (GenericObjectModelProvider)provider : new DelegatingObjectModelProvider(provider);

      this.root = root;

      content.startDocument();

      if (rootQNames.isEmpty())
      {
         XSNamedMap components = model.getComponents(XSConstants.ELEMENT_DECLARATION);
         for (int i = 0; i < components.getLength(); ++i)
         {
            XSElementDeclaration element = (XSElementDeclaration)components.item(i);
            marshalElement(element, 1, 1);// todo fix min/max
         }
      }
      else
      {
         for (int i = 0; i < rootQNames.size(); ++i)
         {
            QName qName = (QName)rootQNames.get(i);
            XSElementDeclaration element = model.getElementDeclaration(qName.getLocalPart(), qName.getNamespaceURI());
            if (element == null)
            {
               XSNamedMap components = model.getComponents(XSConstants.ELEMENT_DECLARATION);
               String roots = "";
               for (int j = 0; j < components.getLength(); ++j)
               {
                  XSObject xsObject = components.item(j);
                  if (j > 0)
                  {
                     roots += ", ";
                  }
                  roots += "{" + xsObject.getNamespace() + "}" + xsObject.getName();
               }
               throw new IllegalStateException("Root element not found: " + qName + " among " + roots);
            }

            marshalElement(element, 1, 1);// todo fix min/max
         }
      }

      content.endDocument();

      // version & encoding
      writeXmlVersion(writer);

      ContentWriter contentWriter = new ContentWriter(writer, propertyIsTrueOrNotSet(Marshaller.PROP_OUTPUT_INDENTATION));
      content.handleContent(contentWriter);
   }

   private boolean marshalElement(XSElementDeclaration element, int minOccurs, int maxOccurs)
   {
      Object value;
      if (stack.isEmpty())
      {
         value = provider.getRoot(root, null, element.getNamespace(), element.getName());
         if (value == null)
         {
            return false;
         }
      }
      else
      {
         if (stack.peek() instanceof Collection)
         {
            // collection is the provider
            value = (Collection)stack.peek();
         }
         else
         {
            value = provider.getChildren(stack.peek(), null, element.getNamespace(), element.getName());
            if (value == null)
            {
               value = provider.getElementValue(stack.peek(), null, element.getNamespace(), element.getName());
            }
         }
      }

      if (value != null)
      {
         stack.push(value);

         if (maxOccurs != 1 && value instanceof Collection)
         {
            for (Iterator iter = ((Collection)value).iterator(); iter.hasNext();)
            {
               Object item = iter.next();
               stack.push(item);
               marshalElementType(element);
               stack.pop();
            }
         }
         else
         {
            marshalElementType(element);
         }

         stack.pop();
      }

      return minOccurs == 0 || value != null;
   }

   private void marshalElementType(XSElementDeclaration element)
   {
      XSTypeDefinition type = element.getTypeDefinition();
      switch (type.getTypeCategory())
      {
         case XSTypeDefinition.SIMPLE_TYPE:
            marshalSimpleType(element);
            break;
         case XSTypeDefinition.COMPLEX_TYPE:
            marshalComplexType(element);
            break;
         default:
            throw new IllegalStateException("Unexpected type category: " + type.getTypeCategory());
      }
   }

   private void marshalSimpleType(XSElementDeclaration element)
   {
      Object value = stack.peek();
      String valueStr = value.toString();

      String prefix = (String)prefixByUri.get(element.getNamespace());
      String qName = prefix == null ? element.getName() : prefix + ':' + element.getName();

      content.startElement(element.getNamespace(), element.getName(), qName, null);
      content.characters(valueStr.toCharArray(), 0, valueStr.length());
      content.endElement(element.getNamespace(), element.getName(), qName);
   }

   private void marshalComplexType(XSElementDeclaration element)
   {
      XSComplexTypeDefinition type = (XSComplexTypeDefinition)element.getTypeDefinition();
      XSParticle particle = type.getParticle();

      XSObjectList attributeUses = type.getAttributeUses();
      AttributesImpl attrs = attributeUses.getLength() > 0 ? new AttributesImpl(attributeUses.getLength()) : null;
      for (int i = 0; i < attributeUses.getLength(); ++i)
      {
         XSAttributeUse attrUse = (XSAttributeUse)attributeUses.item(i);
         XSAttributeDeclaration attrDec = attrUse.getAttrDeclaration();
         Object attrValue = provider.getAttributeValue(stack.peek(), null, attrDec.getNamespace(), attrDec.getName());
         if (attrValue != null)
         {
            attrs.add(attrDec.getNamespace(),
               attrDec.getName(),
               attrDec.getName(),
               attrDec.getTypeDefinition().getName(),
               attrValue.toString());
         }
      }

      String prefix = (String)prefixByUri.get(element.getNamespace());
      String qName = prefix == null ? element.getName() : prefix + ':' + element.getName();
      content.startElement(element.getNamespace(), element.getName(), qName, attrs);

      if (particle != null)
      {
         marshalParticle(particle);
      }

      content.endElement(element.getNamespace(), element.getName(), qName);
   }

   private boolean marshalParticle(XSParticle particle)
   {
      boolean marshalled;
      XSTerm term = particle.getTerm();
      switch (term.getType())
      {
         case XSConstants.MODEL_GROUP:
            marshalled = marshalModelGroup((XSModelGroup)term);
            break;
         case XSConstants.WILDCARD:
            marshalled = marshalWildcard((XSWildcard)term);
            break;
         case XSConstants.ELEMENT_DECLARATION:
            marshalled = marshalElement((XSElementDeclaration)term, particle.getMinOccurs(), particle.getMaxOccurs());
            break;
         default:
            throw new IllegalStateException("Unexpected term type: " + term.getType());
      }
      return marshalled;
   }

   private boolean marshalWildcard(XSWildcard wildcard)
   {
      // todo class resolution
      ClassMapping mapping = getClassMapping(stack.peek().getClass());
      if (mapping == null)
      {
         throw new IllegalStateException("Failed to marshal wildcard. Class mapping not found for " + stack.peek());
      }

      GenericObjectModelProvider parentProvider = this.provider;
      Object parentRoot = this.root;
      Stack parentStack = this.stack;

      this.root = stack.peek();
      this.provider = mapping.provider;
      this.stack = new StackImpl();

      boolean marshalled = false;
      XSModel model = loadSchema(mapping.schemaUrl);
      XSNamedMap components = model.getComponents(XSConstants.ELEMENT_DECLARATION);
      for (int i = 0; i < components.getLength(); ++i)
      {
         XSElementDeclaration element = (XSElementDeclaration)components.item(i);
         marshalled = marshalElement(element, 1, 1);// todo fix min/max
      }

      this.root = parentRoot;
      this.provider = parentProvider;
      this.stack = parentStack;

      return marshalled;
   }

   private boolean marshalModelGroup(XSModelGroup modelGroup)
   {
      boolean marshalled;
      switch (modelGroup.getCompositor())
      {
         case XSModelGroup.COMPOSITOR_ALL:
            marshalled = marshalModelGroupAll(modelGroup.getParticles());
            break;
         case XSModelGroup.COMPOSITOR_CHOICE:
            marshalled = marshalModelGroupChoice(modelGroup.getParticles());
            break;
         case XSModelGroup.COMPOSITOR_SEQUENCE:
            marshalled = marshalModelGroupSequence(modelGroup.getParticles());
            break;
         default:
            throw new IllegalStateException("Unexpected compsitor: " + modelGroup.getCompositor());
      }
      return marshalled;
   }

   private boolean marshalModelGroupAll(XSObjectList particles)
   {
      boolean marshalled = false;
      for (int i = 0; i < particles.getLength(); ++i)
      {
         XSParticle particle = (XSParticle)particles.item(i);
         marshalled |= marshalParticle(particle);
      }
      return marshalled;
   }

   private boolean marshalModelGroupChoice(XSObjectList particles)
   {
      boolean marshalled = false;
      Content mainContent = this.content;
      for (int i = 0; i < particles.getLength() && !marshalled; ++i)
      {
         XSParticle particle = (XSParticle)particles.item(i);
         this.content = new Content();
         marshalled = marshalParticle(particle);
      }

      if (marshalled)
      {
         mainContent.append(this.content);
      }
      this.content = mainContent;

      return marshalled;
   }

   private boolean marshalModelGroupSequence(XSObjectList particles)
   {
      boolean marshalled = true;
      for (int i = 0; i < particles.getLength(); ++i)
      {
         XSParticle particle = (XSParticle)particles.item(i);
         marshalled &= marshalParticle(particle);
      }
      return marshalled;
   }

   private static XSModel loadSchema(String xsdURL)
   {
      XSImplementation impl = getXSImplementation();
      XSLoader schemaLoader = impl.createXSLoader(null);
      XSModel model = schemaLoader.loadURI(xsdURL);
      if (model == null)
      {
         throw new IllegalArgumentException("Invalid URI for schema: " + xsdURL);
      }

      return model;
   }

   private static XSModel loadSchema(Reader xsdReader)
   {
      XSImplementation impl = getXSImplementation();
      XSLoader schemaLoader = impl.createXSLoader(null);
      // [TODO] load from reader
      XSModel model = schemaLoader.load(null);
      if (model == null)
      {
         throw new IllegalArgumentException("Cannot load schema");
      }

      return model;
   }

   private static XSImplementation getXSImplementation()
   {
      // Get DOM Implementation using DOM Registry
      System.setProperty(DOMImplementationRegistry.PROPERTY, "org.apache.xerces.dom.DOMXSImplementationSourceImpl");

      XSImplementation impl;
      try
      {
         DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
         impl = (XSImplementation)registry.getDOMImplementation("XS-Loader");
      }
      catch (Exception e)
      {
         log.error("Failed to create schema loader.", e);
         throw new IllegalStateException("Failed to create schema loader: " + e.getMessage());
      }
      return impl;
   }
}
