/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.Attributes;
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
import org.apache.log4j.Category;

import javax.xml.parsers.ParserConfigurationException;
import java.io.Reader;
import java.io.Writer;
import java.io.IOException;
import java.util.Stack;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;


/**
 * An XML schema based org.jboss.xml.binding.Marshaller implementation.
 *
 * @version <tt>$Revision$</tt>
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 */
public class XsMarshaller
   extends AbstractMarshaller
{
   private static final Category log = Category.getInstance(XsMarshaller.class);

   private final Stack stack = new Stack();
   private ObjectModelProvider provider;
   private Content content = new Content();

   public void marshal(Reader schema, ObjectModelProvider provider, Writer writer)
      throws IOException, SAXException, ParserConfigurationException
   {
      marshal(schema, provider, provider.getDocument(), writer);
   }

   public void marshal(Reader schema, ObjectModelProvider provider, Object document, Writer writer)
      throws IOException, SAXException, ParserConfigurationException
   {
      InputSource source = new InputSource(schema);

      XSParser xsParser = new XSParser();
      xsParser.setValidating(false);
      XSSchema xsSchema = xsParser.parse(source);

      this.provider = provider;
      stack.push(document);
      content.startDocument();

      for(int i = 0; i < rootQNames.size(); ++i)
      {
         AbstractMarshaller.QName qName = (AbstractMarshaller.QName)rootQNames.get(i);
         XsQName rootName = new XsQName(qName.namespaceUri, qName.name, qName.prefix);

         final XSElement root = xsSchema.getElement(rootName);
         if(root == null)
            throw new IllegalStateException("Root element not found: " + rootName);

         processElement(root);
      }

      content.endDocument();

      // version & encoding
      writer.write("<?xml version=\"");
      writer.write(version);
      writer.write("\" encoding=\"");
      writer.write(encoding);
      writer.write("\"?>\n");

      ContentWriter contentWriter = new ContentWriter(writer);
      content.handleContent(contentWriter);
   }

   // Private

   private final void processElement(XSElement element) throws SAXException
   {
      log.debug("processElement: " + element.getName());
      XSType type = element.getType();
      processType(element, type);
   }

   private final void processType(XSElement element, XSType type) throws SAXException
   {
      if(type.isSimple())
      {
         XSSimpleType simpleType = type.getSimpleType();
         processSimpleType(element, simpleType, null);
      }
      else
      {
         XSComplexType complexType = type.getComplexType();
         processComplexType(element, complexType);
      }
   }

   private final void processSimpleType(XSElement element, XSSimpleType type, Attributes attrs)
   {
      if(type.isAtomic())
      {
         log.debug("atomic simple type");
      }
      else if(type.isList())
      {
         log.debug("list of types");
      }
      else if(type.isRestriction())
      {
         log.debug("restricted type");
      }
      else if(type.isUnion())
      {
         log.debug("union of types");
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

      Object parent = stack.peek();
      ObjectModelProvider provider = getProvider(name.getNamespaceURI(), this.provider);
      Object value = provideValue(provider, parent, name.getNamespaceURI(), name.getLocalName());

      if(value != null)
      {
         char[] ch = value.toString().toCharArray();
         content.startElement(name.getNamespaceURI(), name.getLocalName(), qName, attrs);
         content.characters(ch, 0, ch.length);
         content.endElement(name.getNamespaceURI(), name.getLocalName(), qName);
      }
   }

   private final void processComplexType(XSElement element, XSComplexType type)
      throws SAXException
   {
      Object parent = stack.peek();
      final XsQName xsName = element.getName();
      ObjectModelProvider provider = getProvider(xsName.getNamespaceURI(), this.provider);
      Object children = provideChildren(provider, parent, xsName.getNamespaceURI(), xsName.getLocalName());

      if(children != null)
      {
         handleChildren(element, type, children);
      }
      else
      {
         if(type.hasSimpleContent())
         {
            processSimpleType(element, type.getSimpleContent().getType().getSimpleType(), null);
         }
         else
         {
            if(type.isEmpty())
            {
               final XsQName name = element.getName();

               final Object value = provideValue(provider, parent, name.getNamespaceURI(), name.getLocalName());
               if(Boolean.TRUE.equals(value))
               {
                  final String prefix = name.getPrefix();
                  String qName = (
                     prefix == null || prefix.length() == 0 ?
                     name.getLocalName() : prefix + ':' + name.getLocalName()
                     );

                  final Attributes attrs = provideAttributes(type.getAttributes(), parent);

                  content.startElement(name.getNamespaceURI(), name.getLocalName(), qName, attrs);
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
   }

   private void handleChildren(XSElement parent, XSComplexType type, Object children)
      throws SAXException
   {
      if(children != null)
      {
         if(children instanceof List)
         {
            handleChildrenList(parent, type, (List)children);
         }
         else if(children instanceof Collection)
         {
            handleChildrenIterator(parent, type, ((Collection)children).iterator());
         }
         else if(children instanceof Iterator)
         {
            handleChildrenIterator(parent, type, (Iterator)children);
         }
         else if(children.getClass().isArray())
         {
            handleChildrenArray(parent, type, (Object[])children);
         }
         else
         {
            handleChild(parent, type, children);
         }
      }
   }

   private Attributes provideAttributes(XSAttributable[] xsAttrs, Object container)
   {
      AttributesImpl attrs = new AttributesImpl(xsAttrs.length);
      for(int i = 0; i < xsAttrs.length; ++i)
      {
         final XSAttributable attributable = xsAttrs[i];
         if(attributable instanceof XSAttribute)
         {
            final XSAttribute attr = (XSAttribute)attributable;

            final XsQName attrQName = attr.getName();
            ObjectModelProvider provider = getProvider(attrQName.getNamespaceURI(), this.provider);
            final Object attrValue = provideAttributeValue(
               provider, container, attrQName.getNamespaceURI(), attrQName.getLocalName()
            );

            if(attrValue != null)
            {
               final String prefix = attrQName.getPrefix();
               String qName = (
                  prefix == null || prefix.length() == 0 ?
                  attrQName.getLocalName() : attrQName.getLocalName() + ':' + attrQName.getLocalName()
                  );

               attrs.addAttribute(
                  attrQName.getNamespaceURI(),
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

   private final void processParticle(XSParticle particle) throws SAXException
   {
      if(particle.isElement())
      {
         XSElement element = particle.getElement();
         processElement(element);
      }
      else if(particle.isGroup())
      {
         XSGroup group = particle.getGroup();
         processGroup(group);
      }
      else if(particle.isWildcard())
      {
         log.debug("any");
      }
      else
      {
         throw new IllegalStateException("Particle is not an element, group or wildcard!");
      }
   }

   private final void processGroup(XSGroup group) throws SAXException
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

   private void handleChildrenList(XSElement parent, XSComplexType type, List children)
      throws SAXException
   {
      for(int i = 0; i < children.size(); ++i)
      {
         handleChild(parent, type, children.get(i));
      }
   }

   private void handleChildrenIterator(XSElement parent, XSComplexType type, Iterator children)
      throws SAXException
   {
      while(children.hasNext())
      {
         handleChild(parent, type, children.next());
      }
   }

   private void handleChildrenArray(XSElement parent, XSComplexType type, Object[] children)
      throws SAXException
   {
      for(int i = 0; i < children.length; ++i)
      {
         handleChild(parent, type, children[i]);
      }
   }

   private void handleChild(XSElement parent, XSComplexType type, Object child)
      throws SAXException
   {
      stack.push(child);

      XsQName name = parent.getName();
      final String prefix = name.getPrefix();
      String qName = (
         prefix == null || prefix.length() == 0 ?
         name.getLocalName() : prefix + ':' + name.getLocalName()
         );

      final XSAttributable[] xsAttrs = type.getAttributes();
      Attributes attrs = (xsAttrs == null ? null : provideAttributes(xsAttrs, child));

      if(type.hasSimpleContent())
      {
         processSimpleType(parent, type.getSimpleContent().getType().getSimpleType(), attrs);
      }
      else
      {
         content.startElement(name.getNamespaceURI(), name.getLocalName(), qName, attrs);
         if(!type.isEmpty() && type.getParticle() != null)
            processParticle(type.getParticle());
         content.endElement(name.getNamespaceURI(), name.getLocalName(), qName);
      }

      stack.pop();
   }
}
