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
package org.jboss.test.xml;

import java.net.URL;

import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSImplementation;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSModelGroup;
import org.jboss.test.BaseTestCase;
import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.Util;
import org.jboss.xb.binding.sunday.unmarshalling.MultiClassSchemaResolver;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBindingResolver;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinderTerminatingErrorHandler;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * @author <a href="mailto:adrian@jboss.org">Adrian Brock</a>
 * @version <tt>$Revision: 41616 $</tt>
 */
public class XercesBugTestCase extends BaseTestCase
{
   public XercesBugTestCase(String localName)
   {
      super(localName);
   }

   public void testXerces280Bug()
   {
      String xsd =
         "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema'>" +
         "  <xsd:complexType name='valueType'>" +
         "    <xsd:sequence>" +
         "      <xsd:element name='value' type='xsd:string'/>" +
         "    </xsd:sequence>" +
         "  </xsd:complexType>" +
         "  <xsd:complexType name='annotatedValueType'>" +
         "    <xsd:complexContent>" +
         "      <xsd:extension base='valueType'>" +
         "        <xsd:sequence>" +
         "          <xsd:element name='annotation' type='xsd:string'/>" +
         "        </xsd:sequence>" +
         "      </xsd:extension>" +
         "    </xsd:complexContent>" +
         "  </xsd:complexType>" +
         "</xsd:schema>";

      XSModel model = Util.loadSchema(xsd, (String)null);

      XSComplexTypeDefinition type = (XSComplexTypeDefinition)model.getTypeDefinition("valueType", "");
      XSModelGroup modelGroup = (XSModelGroup)type.getParticle().getTerm();
      assertNull(modelGroup.getAnnotation());

      type = (XSComplexTypeDefinition)model.getTypeDefinition("annotatedValueType", "");
      modelGroup = (XSModelGroup)type.getParticle().getTerm();
      assertNull(modelGroup.getAnnotation());
   }

   public void testXercesBug()
   {
      String name = "xml/xerces-bug.xsd";
      URL xsdUrl = Thread.currentThread().getContextClassLoader().getResource(name);

      MultiClassSchemaResolver resolver = new MultiClassSchemaResolver();
      resolver.setBaseURI(xsdUrl.toString());
      XSImplementation impl = getXSImplementation();
      XSLoader schemaLoader = impl.createXSLoader(null);
      setResourceResolver(schemaLoader, resolver);
      setDOMErrorHandler(schemaLoader);
      XSModel model = schemaLoader.loadURI(xsdUrl.toString());

      XSNamedMap types = model.getComponents(XSConstants.TYPE_DEFINITION);
      for(int i = 0; i < types.getLength(); ++i)
      {
         XSTypeDefinition type = (XSTypeDefinition)types.item(i);
         if(!Constants.NS_XML_SCHEMA.equals(type.getNamespace()))
         {
            if (type.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE)
            {
               XSComplexTypeDefinition typeDef = (XSComplexTypeDefinition) type;
               XSObjectList annotations = typeDef.getAnnotations();
               assertEquals(1, annotations.getLength());
            }
         }
      }
   }

   private static void setResourceResolver(XSLoader schemaLoader, final SchemaBindingResolver schemaResolver)
   {
      DOMConfiguration config = schemaLoader.getConfig();
      config.setParameter("resource-resolver", new LSResourceResolver()
      {
         public LSInput resolveResource(String type,
                                                       String namespaceURI,
                                                       String publicId,
                                                       String systemId,
                                                       String baseURI)
         {
            if(Constants.NS_XML_SCHEMA.equals(type))
            {
               return schemaResolver.resolveAsLSInput(namespaceURI, null, null);
            }
            return null;
         }
      }
      );
   }

   private static void setDOMErrorHandler(XSLoader schemaLoader)
   {
      DOMConfiguration config = schemaLoader.getConfig();
      config.setParameter("error-handler", XsdBinderTerminatingErrorHandler.newInstance());
   }

   private static XSImplementation getXSImplementation()
   {
      // Get DOM Implementation using DOM Registry
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      try
      {
         // Try the 2.6.2 version
         String name = "org.apache.xerces.dom.DOMXSImplementationSourceImpl";
         loader.loadClass(name);
         System.setProperty(DOMImplementationRegistry.PROPERTY, name);
      }
      catch(ClassNotFoundException e)
      {
         // Try the 2.7.0 version
         String name = "org.apache.xerces.dom.DOMXSImplementationSourceImpl";
         System.setProperty(DOMImplementationRegistry.PROPERTY, name);
      }

      XSImplementation impl;
      try
      {
         DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
         impl = (XSImplementation)registry.getDOMImplementation("XS-Loader");
      }
      catch(Exception e)
      {
         throw new IllegalStateException("Failed to create schema loader: " + e.getMessage());
      }
      return impl;
   }
}
