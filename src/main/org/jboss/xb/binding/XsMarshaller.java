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

import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.apache.ws.jaxme.xs.XSParser;
import org.apache.ws.jaxme.xs.XSSchema;
import org.apache.ws.jaxme.xs.XSElement;
import org.apache.ws.jaxme.xs.XSType;
import org.apache.ws.jaxme.xs.XSSimpleType;
import org.apache.ws.jaxme.xs.XSComplexType;
import org.apache.ws.jaxme.xs.XSParticle;
import org.apache.ws.jaxme.xs.XSGroup;
import org.apache.ws.jaxme.xs.XSAttributable;
import org.apache.ws.jaxme.xs.XSAttribute;
import org.apache.ws.jaxme.xs.xml.XsQName;
import org.jboss.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.namespace.QName;
import java.io.Reader;
import java.io.Writer;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;


/**
 * An XML schema based org.jboss.xb.binding.Marshaller implementation.
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class XsMarshaller
   extends AbstractMarshaller
{
   private static final Logger log = Logger.getLogger(XsMarshaller.class);

   private Stack stack = new StackImpl();

   /**
    * ObjectModelProvider for this marshaller
    */
   private GenericObjectModelProvider provider;
   /**
    * Content the result is written to
    */
   private Content content = new Content();
   /**
    * Declared namespaces
    */
   private final Map prefixByUri = new HashMap();

   private Object root;

   public void marshal(Reader schema, ObjectModelProvider provider, Object root, Writer writer)
      throws IOException, SAXException, ParserConfigurationException
   {
      InputSource source = new InputSource(schema);

      XSParser xsParser = new XSParser();
      xsParser.setValidating(false);
      XSSchema xsSchema = xsParser.parse(source);

      this.provider = provider instanceof GenericObjectModelProvider ?
         (GenericObjectModelProvider)provider : new DelegatingObjectModelProvider(provider);

      this.root = root;

      //stack.push(document);
      content.startDocument();

      if(rootQNames.isEmpty())
      {
         XSElement[] elements = xsSchema.getElements();
         for(int i = 0; i < elements.length; ++i)
         {
            //processElement(elements[i], addedAttributes, 1);
            processElement(elements[i], 1, true);
         }
      }
      else
      {
         for(int i = 0; i < rootQNames.size(); ++i)
         {
            QName qName = (QName)rootQNames.get(i);
            XsQName rootName = new XsQName(qName.getNamespaceURI(), qName.getLocalPart(), qName.getPrefix());

            final XSElement xsRoot = xsSchema.getElement(rootName);
            if(xsRoot == null)
            {
               throw new IllegalStateException("Root element not found: " + rootName);
            }

            //processElement(xsRoot, addedAttributes, 1);
            processElement(xsRoot, 1, true);
         }
      }

      content.endDocument();

      // version & encoding
      writeXmlVersion(writer);

      ContentWriter contentWriter = new ContentWriter(writer,
         propertyIsTrueOrNotSet(Marshaller.PROP_OUTPUT_INDENTATION)
      );
      content.handleContent(contentWriter);
   }

   /**
    * Defines a namespace. The namespace declaration will appear in the root element.
    * <p>If <code>name</code> argument is <code>null</code> or is an empty string then
    * the passed in URI will be used for the default namespace, i.e. <code>xmlns</code>.
    * Otherwise, the declaration will follow the format <code>xmlns:name=uri</code>.
    * <p>If the namespace with the given name was already declared, its value is overwritten.
    *
    * @param prefix the prefix of the namespace to declare
    * @param uri    the URI of the namespace.
    */
   public void declareNamespace(String prefix, String uri)
   {
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
      // todo what was that?..
      /*
      final String uri;
      if(prefix != null && prefix.length() > 0)
      {
         uri = (String)prefixByUri.get(prefix);
         if(uri == null)
         {
            throw new IllegalStateException("Namespace prefix " + prefix + " is not declared. Use declareNamespace().");
         }
      }
      else
      {
         uri = null;
      }
      */
   }

   /**
    * Adds an attribute to the top most elements declaring namespace prefix if it is not yet declared.
    *
    * @param namespaceUri attribute's namespace URI
    * @param prefix       attribute's prefix
    * @param localName    attribute's local name
    * @param type         attribute's type
    * @param value        attribute's value
    */
   public void addAttribute(String namespaceUri, String prefix, String localName, String type, String value)
   {
      declareNamespace(prefix, namespaceUri);
      addAttribute(prefix, localName, type, value);
   }

   // Private

   private void processElement(XSElement element, int maxOccurs, boolean declareNs) throws SAXException
   {
      XSType type = element.getType();
      processType(element, type, maxOccurs, declareNs);
   }

   private void processType(XSElement element, XSType type, int maxOccurs, boolean declareNs)
      throws SAXException
   {
      if(type.isSimple())
      {
         processSimpleType(element, type, null, declareNs);
      }
      else
      {
         XSComplexType complexType = type.getComplexType();
         processComplexType(element, complexType, maxOccurs, declareNs);
      }
   }

   private void processSimpleType(XSElement element, XSType xsType, AttributesImpl attrs, boolean declareNs)
      throws SAXException
   {
      XSSimpleType type = xsType.getSimpleType();
      if(type.isAtomic())
      {
         if(log.isTraceEnabled())
         {
            log.trace("atomic simple type");
         }
      }
      else if(type.isList())
      {
         if(log.isTraceEnabled())
         {
            log.trace("list of types");
         }
      }
      else if(type.isRestriction())
      {
         if(log.isTraceEnabled())
         {
            log.trace("restricted type");
         }
      }
      else if(type.isUnion())
      {
         if(log.isTraceEnabled())
         {
            log.trace("union of types");
         }
      }
      else
      {
         throw new IllegalStateException("Simple type is not atomic, list, restriction or union!");
      }

      XsQName name = element.getName();
      final String prefix = name.getPrefix();
      String qName = (
         prefix == null || prefix.length() == 0 ?
         name.getLocalName() : prefix + ':' + name.getLocalName()
         );

      if(declareNs && prefixByUri.size() > 0)
      {
         if(attrs == null)
         {
            attrs = new AttributesImpl(prefixByUri.size());
         }
         declareNs(attrs);
      }

      Object parent;
      if(stack.isEmpty())
      {
         parent = provider.getRoot(this.root, null, name.getNamespaceURI(), name.getLocalName());
         if(parent == null)
         {
            return;
         }
         char[] ch = parent.toString().toCharArray();
         content.startElement(name.getNamespaceURI(), name.getLocalName(), qName, attrs);
         content.characters(ch, 0, ch.length);
         content.endElement(name.getNamespaceURI(), name.getLocalName(), qName);
      }
      else
      {
         parent = stack.peek();
         Object value = provider.getElementValue(parent, null, name.getNamespaceURI(), name.getLocalName());

         if(value != null)
         {
            XsQName typeName = xsType.getName();
            String marshalled;
            if(Constants.NS_XML_SCHEMA.equals(typeName.getNamespaceURI()))
            {
               marshalled = SimpleTypeBindings.marshal(typeName.getLocalName(), value, null);
            }
            else
            {
               marshalled = value.toString();
            }

            char[] ch = marshalled.toCharArray();
            content.startElement(name.getNamespaceURI(), name.getLocalName(), qName, attrs);
            content.characters(ch, 0, ch.length);
            content.endElement(name.getNamespaceURI(), name.getLocalName(), qName);
         }
      }
   }

   private void processComplexType(XSElement element,
                                         XSComplexType type,
                                         int maxOccurs,
                                         boolean declareNs)
      throws SAXException
   {
      final XsQName xsName = element.getName();

      AttributesImpl attrs = null;
      if(declareNs && prefixByUri.size() > 0)
      {
         attrs = new AttributesImpl(prefixByUri.size());
         declareNs(attrs);
      }

      Object parent;
      boolean popRoot = false;
      if(stack.isEmpty())
      {
         parent = provider.getRoot(this.root, null, xsName.getNamespaceURI(), xsName.getLocalName());
         if(parent == null)
         {
            return;
         }

         if(type.getAttributes() != null)
         {
            if(attrs == null)
            {
               attrs = provideAttributes(type.getAttributes(), parent);
            }
            else
            {
               attrs.addAll(provideAttributes(type.getAttributes(), parent));
            }
         }

         String qName = xsName.getPrefix() == null ?
            xsName.getLocalName() :
            xsName.getPrefix() + ":" + xsName.getLocalName();
         content.startElement(xsName.getNamespaceURI(), xsName.getLocalName(), qName, attrs);

         stack.push(parent);
         popRoot = true;
      }
      else
      {
         parent = stack.peek();
      }

      // todo rewrite
      Object children = null;
      if(!popRoot)
      {
         children = provider.getChildren(parent, null, xsName.getNamespaceURI(), xsName.getLocalName());
      }

      if(children != null)
      {
         String qName = null;
         if(maxOccurs == 1)
         {
            if(type.getAttributes() != null)
            {
               if(attrs != null)
               {
                  attrs.addAll(provideAttributes(type.getAttributes(), parent));
               }
               else
               {
                  attrs = provideAttributes(type.getAttributes(), parent);
               }
            }

            qName = xsName.getPrefix() == null ?
               xsName.getLocalName() :
               xsName.getPrefix() + ":" + xsName.getLocalName();
            content.startElement(xsName.getNamespaceURI(), xsName.getLocalName(), qName, attrs);
         }

         handleChildren(element, type, children, maxOccurs);

         if(qName != null)
         {
            content.endElement(xsName.getNamespaceURI(), xsName.getLocalName(), qName);
         }
      }
      else
      {
         if(type.hasSimpleContent())
         {
            processSimpleType(element, type.getSimpleContent().getType(), null, declareNs);
         }
         else
         {
            if(type.isEmpty())
            {
               final XsQName name = element.getName();

               final Object value = provider.getElementValue(parent,
                  null,
                  name.getNamespaceURI(),
                  name.getLocalName()
               );
               if(Boolean.TRUE.equals(value))
               {
                  final String prefix = name.getPrefix();
                  String qName = (
                     prefix == null || prefix.length() == 0 ?
                     name.getLocalName() : prefix + ':' + name.getLocalName()
                     );

                  AttributesImpl ownAttrs = provideAttributes(type.getAttributes(), parent);

                  if(declareNs && prefixByUri.size() > 0)
                  {
                     if(ownAttrs == null)
                     {
                        ownAttrs = new AttributesImpl(prefixByUri.size());
                     }
                     declareNs(ownAttrs);
                  }

                  content.startElement(name.getNamespaceURI(), name.getLocalName(), qName, ownAttrs);
                  content.endElement(name.getNamespaceURI(), name.getLocalName(), qName);
               }
            }
            else
            {
               final XSParticle particle = type.getParticle();
               if(particle != null)
               {
                  processParticle(particle);
               }
               else
               {
                  // anyType for example
               }
            }
         }
      }

      if(popRoot)
      {
         stack.pop();
         String qName = xsName.getPrefix() == null ?
            xsName.getLocalName() :
            xsName.getPrefix() + ":" + xsName.getLocalName();
         content.endElement(xsName.getNamespaceURI(), xsName.getLocalName(), qName);
      }
   }

   private void handleChildren(XSElement parent,
                               XSComplexType type,
                               Object children,
                               int maxOccurs)
      throws SAXException
   {
      if(children != null)
      {
         if(children instanceof List)
         {
            handleChildrenList(parent, type, (List)children, maxOccurs);
         }
         else if(children instanceof Collection)
         {
            handleChildrenIterator(parent, type, ((Collection)children).iterator(), maxOccurs);
         }
         else if(children instanceof Iterator)
         {
            handleChildrenIterator(parent, type, (Iterator)children, maxOccurs);
         }
         else if(children.getClass().isArray())
         {
            handleChildrenArray(parent, type, (Object[])children, maxOccurs);
         }
         else
         {
            handleChild(parent, type, children, false);
         }
      }
   }

   private AttributesImpl provideAttributes(XSAttributable[] xsAttrs, Object container)
   {
      AttributesImpl attrs = new AttributesImpl(xsAttrs.length);
      for(int i = 0; i < xsAttrs.length; ++i)
      {
         final XSAttributable attributable = xsAttrs[i];
         if(attributable instanceof XSAttribute)
         {
            final XSAttribute attr = (XSAttribute)attributable;

            final XsQName attrQName = attr.getName();
            final Object attrValue = provider.getAttributeValue(container,
               null, attrQName.getNamespaceURI(),
               attrQName.getLocalName()
            );

            if(attrValue != null)
            {
               final String prefix = attrQName.getPrefix();
               String qName = (
                  prefix == null || prefix.length() == 0 ?
                  attrQName.getLocalName() : attrQName.getPrefix() + ':' + attrQName.getLocalName()
                  );

               attrs.add(attrQName.getNamespaceURI(),
                  attrQName.getLocalName(),
                  qName,
                  attr.getType().getName().getLocalName(),
                  attrValue.toString()
               );
            }
         }
      }
      return attrs;
   }

   private void processParticle(XSParticle particle) throws SAXException
   {
      if(particle.isElement())
      {
         XSElement element = particle.getElement();
         processElement(element, particle.getMaxOccurs(), false);
      }
      else if(particle.isGroup())
      {
         XSGroup group = particle.getGroup();
         processGroup(group);
      }
      else if(particle.isWildcard())
      {
         if(log.isTraceEnabled())
         {
            log.trace("any");
         }
      }
      else
      {
         throw new IllegalStateException("Particle is not an element, group or wildcard!");
      }
   }

   private void processGroup(XSGroup group) throws SAXException
   {
      if(group.isSequence())
      {
      }
      else if(group.isChoice())
      {
      }
      else if(group.isAll())
      {
      }
      else
      {
         throw new IllegalStateException("Group is not a sequence, choice or all!");
      }

      XSParticle[] particles = group.getParticles();
      for(int i = 0; i < particles.length; ++i)
      {
         XSParticle particle = particles[i];
         processParticle(particle);
      }
   }

   private void handleChildrenList(XSElement parent,
                                   XSComplexType type,
                                   List children,
                                   int maxOccurs)
      throws SAXException
   {
      /*
      for(int i = 0; i < children.size(); ++i)
      {
         handleChild(parent, type, children.get(i), addedAttrs);
      }
      */
      handleChildrenIterator(parent, type, children.iterator(), maxOccurs);
   }

   private void handleChildrenIterator(XSElement parent,
                                       XSComplexType type,
                                       Iterator children,
                                       int maxOccurs)
      throws SAXException
   {
      //XSParticle particle = type.getParticle();
      XsQName name = parent.getName();
      String qName = null;
      if(maxOccurs == -1 || maxOccurs > 0)
      {
         qName = name.getPrefix() == null ? name.getLocalName() : name.getPrefix() + ':' + name.getLocalName();
      }

      while(children.hasNext())
      {
         Object child = children.next();

         if(qName != null)
         {
            AttributesImpl attrs = null;
            if(type.getAttributes() != null)
            {
               attrs = provideAttributes(type.getAttributes(), child);
            }

            content.startElement(name.getNamespaceURI(), name.getLocalName(), qName, attrs);
         }

         handleChild(parent, type, child, false);

         if(qName != null)
         {
            content.endElement(name.getNamespaceURI(), name.getLocalName(), qName);
         }
      }
   }

   private void handleChildrenArray(XSElement parent,
                                    XSComplexType type,
                                    Object[] children,
                                    int maxOccurs)
      throws SAXException
   {
      /*
      for(int i = 0; i < children.length; ++i)
      {
         handleChild(parent, type, children[i], addedAttrs);
      }
      */
      handleChildrenIterator(parent, type, Arrays.asList(children).iterator(), maxOccurs);
   }

   private void handleChild(XSElement parent, XSComplexType type, Object child, boolean declareNs)
      throws SAXException
   {
      stack.push(child);

      final XSAttributable[] xsAttrs = type.getAttributes();
      AttributesImpl ownAttrs = (xsAttrs == null ? null : provideAttributes(xsAttrs, child));

      if(type.hasSimpleContent())
      {
         processSimpleType(parent, type.getSimpleContent().getType(), ownAttrs, declareNs);
      }
      else
      {
         if(!type.isEmpty() && type.getParticle() != null)
         {
            processParticle(type.getParticle());
         }
         else
         {
            ClassMapping mapping = getClassMapping(child.getClass());

            InputSource source = new InputSource(mapping.schemaUrl);

            XSParser xsParser = new XSParser();
            xsParser.setValidating(false);
            XSSchema xsSchema;
            try
            {
               xsSchema = xsParser.parse(source);
            }
            catch(Exception e)
            {
               log.error(e);
               throw new IllegalStateException(e.getMessage());
            }

            XsQName rootName = new XsQName(mapping.elementName.getNamespaceURI(), mapping.elementName.getLocalPart());
            XSElement root = xsSchema.getElement(rootName);
            // name with the prefix
            rootName = root.getName();

            String rootPrefix = rootName.getPrefix();
            String rootQName = (rootPrefix == null || rootPrefix.length() == 0 ?
               rootName.getLocalName() : rootPrefix + ':' + rootName.getLocalName()
               );

            Stack oldStack = this.stack;
            this.stack = new StackImpl();
            Object oldRoot = this.root;
            this.root = child;
            GenericObjectModelProvider oldProvider = this.provider;
            this.provider = mapping.provider;
            content.startElement(rootName.getNamespaceURI(), rootName.getLocalName(), rootQName, null);

            processElement(root, 1, false);

            content.endElement(rootName.getNamespaceURI(), rootName.getLocalName(), rootQName);
            this.root = oldRoot;
            this.stack = oldStack;
            this.provider = oldProvider;
         }
      }

      stack.pop();
   }

   private void declareNs(AttributesImpl ownAttrs)
   {
      for(Iterator i = prefixByUri.entrySet().iterator(); i.hasNext();)
      {
         Map.Entry entry = (Map.Entry)i.next();
         String localName = (String)entry.getValue();
         ownAttrs.add(null,
            localName,
            localName == null ? "xmlns" : "xmlns:" + localName,
            null,
            (String)entry.getKey()
         );
      }
   }

   /*
   private static String getQName(String prefix, String localName)
   {
      return (prefix == null || prefix.length() == 0 ? localName : prefix + ':' + localName);
   }
   */
}
