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
package org.jboss.test.xb.builder.object.attribute.test;


import javax.xml.namespace.QName;

import junit.framework.Test;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.test.xb.builder.object.attribute.support.DefaultAttribute;
import org.jboss.util.xml.JBossEntityResolver;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.resolver.MultiClassSchemaResolver;
import org.jboss.xb.binding.sunday.unmarshalling.AttributeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBindingInitializer;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xb.builder.runtime.BeanHandler;

/**
 * A SchemaDefaultAttributeValueUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class SchemaDefaultAttributeValueUnitTestCase extends AbstractBuilderTest
{
   public static Test suite()
   {
      return suite(SchemaDefaultAttributeValueUnitTestCase.class);
   }
   
   public SchemaDefaultAttributeValueUnitTestCase(String name)
   {
      super(name);
   }

   /**
    * This test demonstrates unmarshalling with default attribute values
    * 
    * @throws Exception
    */
   public void testMain() throws Exception
   {
      UnmarshallerFactory unmarshallerFactory = UnmarshallerFactory.newInstance();
      Unmarshaller unmarshaller = unmarshallerFactory.newUnmarshaller();
      
      // this is to make the SAX parser parse the XSD as well and include default attribute values in the startElement
      unmarshaller.setSchemaValidation(true);

      // this is EntityResolver that SAX parser (xerces) will use to resolve XSD location for the XML being parsed
      JBossEntityResolver xmlResolver = new JBossEntityResolver();
      // Here we map schema location specified in the XML file to the local schema location,
      // which is a path relative to the resources directory visible in the classpath.
      // Note, we have to specify here and XML the complete URL including the protocol part into the schemaLocation.
      // The reason is that if we specify schemaLocation in XML as just a filename,
      // the SAX parser will compose its schemaLocation by adding the protocol, baseURI (which will depend on the
      // environment the code is run in) and the schemaLocation we specified in the XML and then
      // will pass this new schemaLocation to the resolver.
      xmlResolver.registerLocalEntity("http://www.hostame.org/SchemaDefaultAttributeValue.xsd", "org/jboss/test/xb/builder/object/attribute/test/SchemaDefaultAttributeValue.xsd");
      unmarshaller.setEntityResolver(xmlResolver);
      
      // this is to resolve namespace to class mapping and build the SchemaBinding from the class
      MultiClassSchemaResolver schemaBindingResolver = new MultiClassSchemaResolver();
      schemaBindingResolver.mapURIToClass("xb:test:default-attribute", DefaultAttribute.class);
      // the reason we configured a separate EntityResolver for unmarshaller above instead of
      // calling schemaBindingResolver.mapSchemaLocation(nsURI, location) is that
      // entity resolution inside schemaBindingResolver is used only for nsURI to class resolution (by XsdBinder)
      // and is not related to SAX parser entity resolution and XML validation.

      String xml = findXML("SchemaDefaultAttributeValue.xml");
      Object result = unmarshaller.unmarshal(xml, schemaBindingResolver);
      
      assertNotNull(result);
      assertTrue(result instanceof DefaultAttribute);
      DefaultAttribute da = (DefaultAttribute) result;
      assertEquals(new Integer(123), da.getAttribute());
   }
   
   /**
    * This test demonstrates unmarshalling with default attribute values not added by the sax parser
    * 
    * @throws Exception
    */
   public void testValidationDisabled() throws Exception
   {
      UnmarshallerFactory unmarshallerFactory = UnmarshallerFactory.newInstance();
      Unmarshaller unmarshaller = unmarshallerFactory.newUnmarshaller();
      
      // this is to make the SAX parser parse the XSD as well and include default attribute values in the startElement
      //unmarshaller.setSchemaValidation(true);

      JBossEntityResolver xmlResolver = new JBossEntityResolver();
      xmlResolver.registerLocalEntity("http://www.hostame.org/SchemaDefaultAttributeValue.xsd", "org/jboss/test/xb/builder/object/attribute/test/SchemaDefaultAttributeValue.xsd");
      unmarshaller.setEntityResolver(xmlResolver);
      
      MultiClassSchemaResolver schemaBindingResolver = new MultiClassSchemaResolver();
      schemaBindingResolver.mapURIToClass("xb:test:default-attribute", DefaultAttribute.class);

      // add the default constraint to the attribute
      schemaBindingResolver.mapSchemaInitializer("xb:test:default-attribute",
         new SchemaBindingInitializer()
         {
            public SchemaBinding init(SchemaBinding schema)
            {
               ElementBinding element = schema.getElement(new QName("xb:test:default-attribute", "default-attribute"));
               assertNotNull(element);
               TypeBinding type = element.getType();
               AttributeBinding attribute = type.getAttribute(new QName("attribute"));
               assertNotNull(attribute);
               attribute.setDefaultConstraint("123");
               // with this kind attributes handler this has to be done...
               BeanHandler handler = (BeanHandler) type.getHandler();
               handler.getAttributesHandler().addAttribute(attribute);
               return schema;
            }
         }
      );
      
      String xml = findXML("SchemaDefaultAttributeValue.xml");
      Object result = unmarshaller.unmarshal(xml, schemaBindingResolver);
      
      assertNotNull(result);
      assertTrue(result instanceof DefaultAttribute);
      DefaultAttribute da = (DefaultAttribute) result;
      assertEquals(new Integer(123), da.getAttribute());
   }
}
