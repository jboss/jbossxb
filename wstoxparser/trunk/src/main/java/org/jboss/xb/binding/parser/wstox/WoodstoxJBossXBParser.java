/*
* JBoss, Home of Professional Open Source
* Copyright 2009, JBoss Inc., and individual contributors as indicated
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
package org.jboss.xb.binding.parser.wstox;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

//import org.codehaus.stax2.XMLEventReader2;
//import org.codehaus.stax2.XMLInputFactory2;
//import org.codehaus.stax2.XMLStreamReader2;
//import org.codehaus.stax2.evt.DTD2;
//import org.codehaus.stax2.io.Stax2Source;
//import org.codehaus.stax2.io.Stax2FileSource;
//import org.codehaus.stax2.io.Stax2URLSource;
import org.jboss.xb.binding.JBossXBException;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.parser.JBossXBParser;
import org.jboss.xb.binding.parser.JBossXBParser.ContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

//import com.ctc.wstx.api.ReaderConfig;
//import com.ctc.wstx.stax.WstxInputFactory;

/**
 * A WoodstoxJBossXBParser.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class WoodstoxJBossXBParser implements JBossXBParser
{

   private static final Attributes EMPTY_ATTRS = new Attributes()
   {
      @Override
      public int getIndex(String name)
      {
         return -1;
      }

      @Override
      public int getIndex(String uri, String localName)
      {
         return -1;
      }

      @Override
      public int getLength()
      {
         return 0;
      }

      @Override
      public String getLocalName(int index)
      {
         return null;
      }

      @Override
      public String getQName(int index)
      {
         return null;
      }

      @Override
      public String getType(int index)
      {
         return null;
      }

      @Override
      public String getType(String name)
      {
         return null;
      }

      @Override
      public String getType(String uri, String localName)
      {
         return null;
      }

      @Override
      public String getURI(int index)
      {
         return null;
      }

      @Override
      public String getValue(int index)
      {
         return null;
      }

      @Override
      public String getValue(String name)
      {
         return null;
      }

      @Override
      public String getValue(String uri, String localName)
      {
         return null;
      }      
   };
   
//   private static WstxInputFactory inputFactory = new WstxInputFactory();//(XMLInputFactory2) XMLInputFactory2.newInstance();
   
/*   public WoodstoxJBossXBParser()
   {
      System.out.println(inputFactory.getProperty(XMLInputFactory.IS_VALIDATING));
      System.out.println(inputFactory.getProperty(XMLInputFactory.IS_NAMESPACE_AWARE));
      System.out.println(inputFactory.getProperty(XMLInputFactory.IS_COALESCING));
      System.out.println(inputFactory.getProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES));
      System.out.println(inputFactory.getProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES));
      System.out.println(inputFactory.getProperty(XMLInputFactory.SUPPORT_DTD));
   }
*/
   @Override
   public void parse(final String source, ContentHandler handler) throws JBossXBException
   {
/*      Stax2Source src;
      try
      {
         src = new Stax2URLSource(new URL(source));
      }
      catch(MalformedURLException e)
      {
         src = new Stax2FileSource(new File(source));
      }
      
      try
      {
         //XMLEventReader2 stream = (XMLEventReader2) inputFactory.createXMLEventReader(src);
         XMLStreamReader2 stream = (XMLStreamReader2) inputFactory.createXMLStreamReader(src);
         parse(handler, stream);
      }
      catch (FactoryConfigurationError e)
      {
         throw new JBossXBException("Failed to create an instance of XMLStreamReader", e);
      }
      catch (XMLStreamException e)
      {
         throw new JBossXBException("Failed to parse XML stream", e);
      }
      catch(Throwable e)
      {
         throw new JBossXBException("Failed to parse source: ", e);
      }
*/   }

   @Override
   public void parse(InputStream is, ContentHandler handler) throws JBossXBException
   {
/*      try
      {
         //XMLEventReader2 stream = (XMLEventReader2) inputFactory.createXMLEventReader(is);
         XMLStreamReader2 stream = (XMLStreamReader2) inputFactory.createXMLStreamReader(is);
         parse(handler, stream);
      }
      catch (FactoryConfigurationError e)
      {
         throw new JBossXBException("Failed to create an instance of XMLStreamReader", e);
      }
      catch (XMLStreamException e)
      {
         throw new JBossXBException("Failed to parse XML stream", e);
      }
      catch(Throwable e)
      {
         throw new JBossXBException("Failed to parse source: ", e);
      }
*/   }

   @Override
   public void parse(Reader reader, ContentHandler handler) throws JBossXBException
   {
/*      try
      {
         //XMLEventReader2 stream = (XMLEventReader2) inputFactory.createXMLEventReader(reader);
         XMLStreamReader2 stream = (XMLStreamReader2) inputFactory.createXMLStreamReader(reader);
         parse(handler, stream);
      }
      catch (FactoryConfigurationError e)
      {
         throw new JBossXBException("Failed to create an instance of XMLStreamReader", e);
      }
      catch (XMLStreamException e)
      {
         throw new JBossXBException("Failed to parse XML stream", e);
      }
      catch(Throwable e)
      {
         throw new JBossXBException("Failed to parse source: ", e);
      }
*/   }

   @Override
   public void parse(InputSource source, ContentHandler handler) throws JBossXBException
   {
      InputStream bs = source.getByteStream();
      if(bs != null)
      {
         parse(bs, handler);
         return;
      }
      
      Reader cs = source.getCharacterStream();
      if(cs != null)
      {
         parse(cs, handler);
         return;
      }
      
      String systemId = source.getSystemId();
      if(systemId == null)
         throw new JBossXBRuntimeException("None of ByteStream, CharacterStream, systemId are available through the instance of InputSource.");
      parse(systemId, handler);
   }

   @Override
   public void setEntityResolver(final EntityResolver entityResolver) throws JBossXBException
   {
      XMLResolver resolver = new XMLResolver()
      {
         @Override
         public Object resolveEntity(String publicID, String systemID, String baseURI, String namespace)
               throws XMLStreamException
         {
            InputSource source;
            try
            {
               source = entityResolver.resolveEntity(publicID, systemID);
            }
            catch (Exception e)
            {
               throw new JBossXBRuntimeException("Failed to resolve publicId=" + publicID +
                     ", systemId=" + systemID +
                     ", baseURI=" + baseURI +
                     ", namespace=" + namespace, e);
            }
            
            InputStream stream = source.getByteStream();
            if(stream != null)
               return stream;

            throw new IllegalStateException("Only streams are supported atm");
         }
      };

/*      ReaderConfig config = inputFactory.getConfig();
      config.setDtdResolver(resolver);
      config.setEntityResolver(resolver);
*/   }

   @Override
   public void setFeature(String name, boolean value)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void setProperty(String name, Object value)
   {
      // TODO Auto-generated method stub

   }

/*   private void parse(ContentHandler handler, final XMLStreamReader2 stream) throws XMLStreamException
   {
      while (stream.hasNext())
      {
         int event = stream.next();
         
         switch(event)
         {
            case XMLStreamConstants.ATTRIBUTE:
               System.out.println("attribute");
               break;
            case XMLStreamConstants.CDATA:
               System.out.println("cdata");
               break;
            case XMLStreamConstants.CHARACTERS:
               System.out.println("characters");
               break;
            case XMLStreamConstants.COMMENT:
               System.out.println("comment");
               break;
            case XMLStreamConstants.DTD:
               System.out.println("dtd");
               break;
            case XMLStreamConstants.END_DOCUMENT:
               System.out.println("end doc");
               break;
            case XMLStreamConstants.END_ELEMENT:
               System.out.println("end element " + stream.getLocalName());
               break;
            case XMLStreamConstants.ENTITY_DECLARATION:
               System.out.println("entity dec");
               break;
            case XMLStreamConstants.ENTITY_REFERENCE:
               System.out.println("entity ref");
               break;
            case XMLStreamConstants.NAMESPACE:
               System.out.println("namespace");
               break;
            case XMLStreamConstants.NOTATION_DECLARATION:
               System.out.println("notation");
               break;
            case XMLStreamConstants.PROCESSING_INSTRUCTION:
               System.out.println("pi");
               break;
            case XMLStreamConstants.SPACE:
               System.out.println("space");
               break;
            case XMLStreamConstants.START_DOCUMENT:
               System.out.println("start doc");
               break;
            case XMLStreamConstants.START_ELEMENT:
               System.out.println("start element " + stream.getLocalName());
               break;
            default:
               throw new IllegalStateException("Unknown event " + event);
         }

         if (event == XMLStreamConstants.START_ELEMENT)
         {
            //System.out.println("start " + stream.getLocalName());
            
            int nsTotal = stream.getNamespaceCount();
            if(nsTotal > 0)
            {
               for(int i = 0; i < nsTotal; ++i)
               {
                  String ns = stream.getNamespaceURI(i);
                  if(ns == null)
                     ns = "";
                  String prefix = stream.getNamespacePrefix(i);
                  if(prefix == null)
                     prefix = "";
                  handler.startPrefixMapping(prefix, ns);
               }
            }
            
            Attributes attrs = EMPTY_ATTRS;
            int attrsTotal = stream.getAttributeCount();
            if(attrsTotal > 0)
            {
               attrs = new Attributes()
               {
                  @Override
                  public int getIndex(String name)
                  {
                     return getIndex(null, name);
                  }

                  @Override
                  public int getIndex(String uri, String localName)
                  {
                     return -1;
                  }

                  @Override
                  public int getLength()
                  {
                     return stream.getAttributeCount();
                  }

                  @Override
                  public String getLocalName(int index)
                  {
                     return stream.getAttributeLocalName(index);
                  }

                  @Override
                  public String getQName(int index)
                  {
                     return null;
                  }

                  @Override
                  public String getType(int index)
                  {
                     return stream.getAttributeType(index);
                  }

                  @Override
                  public String getType(String name)
                  {
                     return getType(null, name);
                  }

                  @Override
                  public String getType(String uri, String localName)
                  {
                     throw new UnsupportedOperationException();
                  }

                  @Override
                  public String getURI(int index)
                  {
                     String ns = stream.getAttributeNamespace(index);
                     if(ns == null)
                        return "";
                     return ns;
                  }

                  @Override
                  public String getValue(int index)
                  {
                     return stream.getAttributeValue(index);
                  }

                  @Override
                  public String getValue(String name)
                  {
                     return getValue(null, name);
                  }

                  @Override
                  public String getValue(String uri, String localName)
                  {
                     return stream.getAttributeValue(uri, localName);
                  }
               };
            }

            String ns = stream.getNamespaceURI();
            if(ns == null)
               ns = "";
            handler.startElement(ns, stream.getLocalName(), null, attrs);
         }
//            else if (event == XMLStreamConstants.ATTRIBUTE)
//            {
//            }
         else if (event == XMLStreamConstants.CHARACTERS)
         {
            handler.characters(stream.getTextCharacters(), stream.getTextStart(), stream.getTextLength());
         }
//            else if (event == XMLStreamConstants.COMMENT)
//            {
//               //System.out.println("Comment Text:" + stream.getText());
//            }
//            else if (event == XMLStreamConstants.END_DOCUMENT)
//            {
//            }
         else if (event == XMLStreamConstants.END_ELEMENT)
         {
            String ns = stream.getNamespaceURI();
            if(ns == null)
               ns = "";
            handler.endElement(ns, stream.getLocalName(), null);
            int nsTotal = stream.getNamespaceCount();
            if(nsTotal > 0)
            {
               for(int i = 0; i < nsTotal; ++i)
               {
                  String prefix = stream.getNamespacePrefix(i);
                  if(prefix == null)
                     prefix = "";
                  handler.endPrefixMapping(prefix);
               }
            }
         }
//         else if(event == XMLStreamConstants.DTD)
//         {
//            System.out.println("dtd " + stream.getText());
//         }
//         else if (event == XMLStreamConstants.NAMESPACE)
//         {
//            //System.out.println("ns: " + stream.getText());
//         }
//            if (event == XMLStreamConstants.PROCESSING_INSTRUCTION)
//            {
//               System.out.println("PI Target:" + stream.getPITarget());
//               System.out.println("PI Data:" + stream.getPIData());
//            }
//            if (event == XMLStreamConstants.SPACE)
//            {
//               System.out.println("Event Type: SPACE");
//               System.out.println("Text:" + stream.getText());
//
//            }
      }
   }
   
   private void parse(ContentHandler handler, final XMLEventReader2 stream) throws XMLStreamException
   {
      while (stream.hasNext())
      {
         XMLEvent event = stream.nextEvent();
         if (event.isStartElement())
         {
            //System.out.println("start " + stream.getLocalName());
            
            final StartElement start = (StartElement)event;
            Iterator<Namespace> i = start.getNamespaces();
            while(i.hasNext())
            {
               Namespace declaredNs = i.next();
               String ns = declaredNs.getNamespaceURI();
               if(ns == null)
                  ns = "";
               String prefix = declaredNs.getPrefix();
               if(prefix == null)
                  prefix = "";
               handler.startPrefixMapping(prefix, ns);
            }
            
            Attributes attrs = EMPTY_ATTRS;
            Iterator<Attribute> eAttrs = start.getAttributes();
            if(eAttrs.hasNext())
            {
               final List<Attribute> attrList = new ArrayList<Attribute>();
               while(eAttrs.hasNext())
                  attrList.add(eAttrs.next());
               
               attrs = new Attributes()
               {
                  @Override
                  public int getIndex(String name)
                  {
                     return getIndex(null, name);
                  }

                  @Override
                  public int getIndex(String uri, String localName)
                  {
                     return -1;
                  }

                  @Override
                  public int getLength()
                  {
                     return attrList.size();
                  }

                  @Override
                  public String getLocalName(int index)
                  {
                     return attrList.get(index).getName().getLocalPart();
                  }

                  @Override
                  public String getQName(int index)
                  {
                     return null;
                  }

                  @Override
                  public String getType(int index)
                  {
                     return attrList.get(index).getDTDType();
                  }

                  @Override
                  public String getType(String name)
                  {
                     return getType(null, name);
                  }

                  @Override
                  public String getType(String uri, String localName)
                  {
                     throw new UnsupportedOperationException();
                  }

                  @Override
                  public String getURI(int index)
                  {
                     return attrList.get(index).getName().getNamespaceURI();
                  }

                  @Override
                  public String getValue(int index)
                  {
                     return attrList.get(index).getValue();
                  }

                  @Override
                  public String getValue(String name)
                  {
                     return getValue(null, name);
                  }

                  @Override
                  public String getValue(String uri, String localName)
                  {
                     Attribute attr = start.getAttributeByName(new QName(uri, localName));
                     if(attr == null)
                        return null;
                     return attr.getValue();
                  }
               };
            }

            String ns = start.getName().getNamespaceURI();
            if(ns == null)
               ns = "";
            try
            {
            handler.startElement(ns, start.getName().getLocalPart(), null, attrs);
            }
            catch(JBossXBRuntimeException e)
            {
               System.out.println(start.getLocation());
               throw e;
            }
         }
//            else if (event == XMLStreamConstants.ATTRIBUTE)
//            {
//            }
         else if (event.isCharacters())
         {
            Characters chars = (Characters) event;
            handler.characters(chars.getData().toCharArray(), 0, chars.getData().length());
         }
//            else if (event == XMLStreamConstants.COMMENT)
//            {
//               //System.out.println("Comment Text:" + stream.getText());
//            }
//            else if (event == XMLStreamConstants.END_DOCUMENT)
//            {
//            }
         else if (event.isEndElement())
         {
            EndElement end = (EndElement) event;
            String ns = end.getName().getNamespaceURI();
            if(ns == null)
               ns = "";
            handler.endElement(ns, end.getName().getLocalPart(), null);
            Iterator<Namespace> i = end.getNamespaces();
            while(i.hasNext())
            {
               Namespace declaredNs = i.next();
               String prefix = declaredNs.getPrefix();
               if(prefix == null)
                  prefix = "";
               handler.endPrefixMapping(prefix);
            }
         }
         else if(event instanceof DTD2 && handler instanceof JBossXBParser.DtdAwareContentHandler)
         {
            DTD2 dtd = (DTD2) event;
            JBossXBParser.DtdAwareContentHandler dtdHandler = (JBossXBParser.DtdAwareContentHandler)handler;
            dtdHandler.startDTD(dtd.getRootName(), dtd.getPublicId(), dtd.getSystemId());
            dtdHandler.endDTD();
         }
//            if (event == XMLStreamConstants.PROCESSING_INSTRUCTION)
//            {
//               System.out.println("PI Target:" + stream.getPITarget());
//               System.out.println("PI Data:" + stream.getPIData());
//            }
//            if (event == XMLStreamConstants.SPACE)
//            {
//               System.out.println("Event Type: SPACE");
//               System.out.println("Text:" + stream.getText());
//
//            }
      }
   }
   */
}
