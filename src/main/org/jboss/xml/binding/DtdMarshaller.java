/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding;

import com.wutka.dtd.DTD;
import com.wutka.dtd.DTDAttribute;
import com.wutka.dtd.DTDContainer;
import com.wutka.dtd.DTDElement;
import com.wutka.dtd.DTDEmpty;
import com.wutka.dtd.DTDItem;
import com.wutka.dtd.DTDMixed;
import com.wutka.dtd.DTDName;
import com.wutka.dtd.DTDPCData;
import com.wutka.dtd.DTDParser;
import com.wutka.dtd.DTDCardinal;
import org.jboss.logging.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * A DTD based org.jboss.xml.binding.Marshaller implementation.
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class DtdMarshaller
   extends AbstractMarshaller
{
   private static final Logger log = Logger.getLogger(DtdMarshaller.class);

   private String publicId;
   private String systemId;

   private final Stack stack = new StackImpl();
   private DTD dtd;
   private GenericObjectModelProvider provider;
   private Content content = new Content();

   private final LinkedList elementStack = new LinkedList();

   public void mapPublicIdToSystemId(String publicId, String systemId)
   {
      this.publicId = publicId;
      this.systemId = systemId;
   }

   public void marshal(Reader dtdReader, ObjectModelProvider provider, Object document, Writer writer)
      throws IOException, SAXException
   {
      DTDParser parser = new DTDParser(dtdReader);
      dtd = parser.parse(true);

      this.provider = provider instanceof GenericObjectModelProvider ?
         (GenericObjectModelProvider)provider : new DelegatingObjectModelProvider(provider);
      //stack.push(document);

      DTDElement[] roots = null;
      if(dtd.rootElement != null)
      {
         handleRootElement(document, dtd.rootElement);
      }
      else
      {
         roots = getRootList(dtd);
         for(int i = 0; i < roots.length; ++i)
         {
            handleRootElement(document, roots[i]);
         }
      }

      //stack.pop();

      // version & encoding
      writeXmlVersion(writer);

      // DOCTYPE
      writer.write("<!DOCTYPE ");

      if(dtd.rootElement != null)
      {
         writer.write(dtd.rootElement.getName());
      }
      else
      {
         for(int i = 0; i < roots.length; ++i)
         {
            writer.write(", ");
            writer.write(roots[i].getName());
         }
      }

      writer.write(" PUBLIC \"");
      writer.write(publicId);
      writer.write("\" \"");
      writer.write(systemId);
      writer.write("\">\n");

      ContentWriter contentWriter = new ContentWriter(writer, propertyIsTrueOrNotSet(Marshaller.PROP_OUTPUT_INDENTATION));
      content.handleContent(contentWriter);
   }

   private void handleRootElement(Object o, final DTDElement dtdRoot)
   {
      Element el = new Element(dtdRoot, true);
      elementStack.addLast(el);
      content.startDocument();

      Object root = provider.getRoot(o, null, systemId, dtdRoot.getName());
      if(root == null)
      {
         return;
      }
      stack.push(root);

      Attributes attrs = provideAttributes(dtdRoot, root);
      content.startElement("", dtdRoot.getName(), dtdRoot.getName(), attrs);
      handleElement(dtd, dtdRoot, attrs);
      content.endElement("", dtdRoot.getName(), dtdRoot.getName());

      stack.pop();
      content.endDocument();
      elementStack.removeLast();
   }

   private final void handleElement(DTD dtd, DTDElement element, Attributes attrs)
   {
      DTDItem item = element.content;
      if(item instanceof DTDMixed)
      {
         handleMixedElement((DTDMixed)item, element.getName(), attrs);
      }
      else if(item instanceof DTDEmpty)
      {
         final Object value = provider.getElementValue(stack.peek(), null, systemId, element.getName());
         if(Boolean.TRUE.equals(value))
         {
            writeSkippedElements();
            content.startElement("", element.getName(), element.getName(), attrs);
            content.endElement("", element.getName(), element.getName());
         }
      }
      else if(item instanceof DTDContainer)
      {
         processContainer(dtd, (DTDContainer)item);
      }
      else
      {
         throw new IllegalStateException("Unexpected element: " + element.getName());
      }
   }

   private final void handleMixedElement(DTDMixed mixed, String elementName, Attributes attrs)
   {
      Object parent = stack.peek();
      DTDItem[] items = mixed.getItems();
      for(int i = 0; i < items.length; ++i)
      {
         DTDItem item = items[i];
         if(item instanceof DTDPCData)
         {
            Object value = provider.getElementValue(parent, null, systemId, elementName);
            if(value != null)
            {
               writeSkippedElements();

               char[] ch = value.toString().toCharArray();
               content.startElement("", elementName, elementName, attrs);
               content.characters(ch, 0, ch.length);
               content.endElement("", elementName, elementName);
            }
         }
      }
   }

   private final void handleChildren(DTD dtd, DTDElement element, DTDCardinal elementCardinal)
   {
      Object parent = stack.peek();
      Object children = provider.getChildren(parent, null, systemId, element.getName());

      if(children != null)
      {
         Iterator iter;
         if(children instanceof Iterator)
         {
            iter = (Iterator)children;
         }
         else if(children instanceof Collection)
         {
            iter = ((Collection)children).iterator();
         }
         else
         {
            iter = Collections.singletonList(children).iterator();
         }

         writeSkippedElements();

         Element el = new Element(element, true);
         elementStack.addLast(el);

         final boolean singleValued = elementCardinal == DTDCardinal.NONE || elementCardinal == DTDCardinal.OPTIONAL;
         if(singleValued)
         {
            // todo attributes!
            content.startElement("", element.getName(), element.getName(), null);
         }

         while(iter.hasNext())
         {
            Object child = iter.next();
            stack.push(child);

            AttributesImpl attrs = (element.attributes.isEmpty() ? null : provideAttributes(element, child));
            if(!singleValued)
            {
               content.startElement("", element.getName(), element.getName(), null);
            }

            handleElement(dtd, element, attrs);

            if(!singleValued)
            {
               content.endElement(systemId, element.getName(), element.getName());
            }

            stack.pop();
         }

         if(singleValued)
         {
            content.endElement(systemId, element.getName(), element.getName());
         }

         elementStack.removeLast();
      }
      else
      {
         boolean removeLast = false;
         if(!(element.getContent() instanceof DTDMixed || element.getContent() instanceof DTDEmpty))
         {
            Element el = new Element(element);
            elementStack.addLast(el);
            removeLast = true;
         }

         AttributesImpl attrs = (element.attributes.isEmpty() ? null : provideAttributes(element, parent));
         handleElement(dtd, element, attrs);

         if(removeLast)
         {
            Element el = (Element)elementStack.removeLast();
            if(el.started)
            {
               DTDElement started = el.element;
               content.endElement("", started.getName(), started.getName());
            }
         }
      }
   }

   private final void processContainer(DTD dtd, DTDContainer container)
   {
      DTDItem[] items = container.getItems();
      for(int i = 0; i < items.length; ++i)
      {
         DTDItem item = items[i];
         if(item instanceof DTDContainer)
         {
            processContainer(dtd, (DTDContainer)item);
         }
         else if(item instanceof DTDName)
         {
            DTDName name = (DTDName)item;
            DTDElement element = (DTDElement)dtd.elements.get(name.value);
            handleChildren(dtd, element, name.getCardinal());
         }
      }
   }

   private void writeSkippedElements()
   {
      Element el = (Element)elementStack.getLast();
      if(!el.started)
      {
         int firstNotStarted = elementStack.size() - 1;
         do
         {
            el = (Element)elementStack.get(--firstNotStarted);
         }
         while(!el.started);

         ++firstNotStarted;

         while(firstNotStarted < elementStack.size())
         {
            el = (Element)elementStack.get(firstNotStarted++);
            DTDElement notStarted = el.element;

            if(log.isTraceEnabled())
            {
               log.trace("starting skipped> " + notStarted.getName());
            }

            content.startElement("", notStarted.getName(), notStarted.getName(), null);
            el.started = true;
         }
      }
   }

   private AttributesImpl provideAttributes(DTDElement element, Object container)
   {
      final Hashtable attributes = element.attributes;
      AttributesImpl attrs = new AttributesImpl(attributes.size());

      for(Iterator attrIter = attributes.values().iterator(); attrIter.hasNext();)
      {
         DTDAttribute attr = (DTDAttribute)attrIter.next();
         final Object attrValue = provider.getAttributeValue(container, null, systemId, attr.getName());

         if(attrValue != null)
         {
            attrs.add(systemId,
               attr.getName(),
               attr.getName(),
               attr.getType().toString(),
               attrValue.toString()
            );
         }
      }

      return attrs;
   }

   /**
    * @param dtd the DTD object model
    * @return root element names
    */
   protected static DTDElement[] getRootList(DTD dtd)
   {
      Hashtable roots = new Hashtable();
      Enumeration e = dtd.elements.elements();
      while(e.hasMoreElements())
      {
         DTDElement element = (DTDElement)e.nextElement();
         roots.put(element.name, element);
      }

      e = dtd.elements.elements();
      while(e.hasMoreElements())
      {
         DTDElement element = (DTDElement)e.nextElement();
         if(!(element.content instanceof DTDContainer))
         {
            continue;
         }

         Enumeration items = ((DTDContainer)element.content).getItemsVec().elements();
         while(items.hasMoreElements())
         {
            removeElements(roots, dtd, (DTDItem)items.nextElement());
         }
      }

      final Collection rootCol = roots.values();
      return (DTDElement[])rootCol.toArray(new DTDElement[rootCol.size()]);
   }

   protected static void removeElements(Hashtable h, DTD dtd, DTDItem item)
   {
      if(item instanceof DTDName)
      {
         h.remove(((DTDName)item).value);
      }
      else if(item instanceof DTDContainer)
      {
         Enumeration e = ((DTDContainer)item).getItemsVec().elements();
         while(e.hasMoreElements())
         {
            removeElements(h, dtd, (DTDItem)e.nextElement());
         }
      }
   }

   // Inner

   private static final class Element
   {
      public final DTDElement element;
      public boolean started;

      public Element(DTDElement element, boolean started)
      {
         this.element = element;
         this.started = started;
      }

      public Element(DTDElement element)
      {
         this.element = element;
      }

      public String toString()
      {
         return "[element=" + element.getName() + ", started=" + started + "]";
      }
   }
}
