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
package org.jboss.test.xb.validator.test;

import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;

import org.jboss.test.xb.builder.AbstractBuilderTest;
import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.sunday.unmarshalling.AllBinding;
import org.jboss.xb.binding.sunday.unmarshalling.AnyAttributeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.AttributeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ChoiceBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ModelGroupBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SequenceBinding;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.WildcardBinding;
import org.jboss.xb.util.DefaultSchemaBindingValidator;
import org.jboss.xb.util.SchemaBindingValidator;
import org.xml.sax.InputSource;


/**
 * A BasicBindingValidatorUnitTestCase.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class BasicBindingValidatorUnitTestCase extends AbstractBuilderTest
{
   public BasicBindingValidatorUnitTestCase(String name)
   {
      super(name);
   }

   private SchemaBinding schema;
   private Map<String, ErrorHandler> handlerByMsg;

   SchemaBindingValidator validator = new DefaultSchemaBindingValidator()
   {
/*      protected void log(String msg)
      {
         System.out.println(msg);
      }      
*/   };

   public void setUp() throws Exception
   {
      super.setUp();
      
      schema = new SchemaBinding();
      handlerByMsg = new HashMap<String, ErrorHandler>();

      //validator.enableLogging(true);

      // this is basically handlers for each error that occurs and that build the schema binding from scratch

      new AbstractErrorHandler("TypeBinding {urn:jboss:xb:test}excludedType is not found in the SchemaBinding.")
      {
         public void handle(SchemaBindingValidator validator, SchemaBinding schema)
         {
            validator.excludeType(new QName("urn:jboss:xb:test", "excludedType"));
         }
      };

      new AbstractErrorHandler("TypeBinding {urn:jboss:xb:test}attributesType is not found in the SchemaBinding.")
      {
         public void handle(SchemaBindingValidator validator, SchemaBinding schema)
         {
            TypeBinding type = new TypeBinding(new QName("urn:jboss:xb:test", "attributesType"));
            schema.addType(type);
         }
      };

      new AbstractErrorHandler("Attribute attr1 is not found in TypeBinding {urn:jboss:xb:test}attributesType")
      {
         public void handle(SchemaBindingValidator validator, SchemaBinding schema)
         {
            TypeBinding type = schema.getType(new QName("urn:jboss:xb:test", "attributesType"));
            AttributeBinding attr = new AttributeBinding(schema, new QName("attr1"), schema.getType(Constants.QNAME_STRING), null);
            type.addAttribute(attr);
         }
      };

      new AbstractErrorHandler("TypeBinding {urn:jboss:xb:test}choiceType is not found in the SchemaBinding.")
      {
         public void handle(SchemaBindingValidator validator, SchemaBinding schema)
         {
            TypeBinding type = new TypeBinding(new QName("urn:jboss:xb:test", "choiceType"));
            schema.addType(type);
            ChoiceBinding choice = new ChoiceBinding(schema);
            type.setParticle(new ParticleBinding(choice));
         }
      };

      new AbstractErrorHandler("ModelGroupBinding expected to have 3 particle(s) but has 0")
      {
         public void handle(SchemaBindingValidator validator, SchemaBinding schema)
         {
            TypeBinding type = schema.getType(new QName("urn:jboss:xb:test", "choiceType"));
            ParticleBinding particle = type.getParticle();
            ChoiceBinding choice = (ChoiceBinding) particle.getTerm();
            ElementBinding e = new ElementBinding(schema, new QName("urn:jboss:xb:test", "choice1"), schema.getType(Constants.QNAME_STRING));
            choice.addParticle(new ParticleBinding(e));
            e = new ElementBinding(schema, new QName("urn:jboss:xb:test", "choice2"), schema.getType(Constants.QNAME_STRING));
            choice.addParticle(new ParticleBinding(e));
            SequenceBinding sequence = new SequenceBinding(schema);
            choice.addParticle(new ParticleBinding(sequence));
            //e = new ElementBinding(schema, new QName("urn:jboss:xb:test", "choice4"), schema.getType(Constants.QNAME_STRING));
            e = new ElementBinding(schema, new QName("urn:jboss:xb:test", "choice3_1"), schema.getType(Constants.QNAME_STRING));
            sequence.addParticle(new ParticleBinding(e));
            //e = new ElementBinding(schema, new QName("urn:jboss:xb:test", "choice3"), schema.getType(Constants.QNAME_STRING));
            e = new ElementBinding(schema, new QName("urn:jboss:xb:test", "choice3_2"), schema.getType(Constants.QNAME_STRING));
            sequence.addParticle(new ParticleBinding(e));
         }
      };

/*      new AbstractErrorHandler("ElementBinding {urn:jboss:xb:test}choice3 is missing: [{urn:jboss:xb:test}choice1, {urn:jboss:xb:test}choice2, {urn:jboss:xb:test}choice3_2, {urn:jboss:xb:test}choice3_1]")
      {
         public void handle(SchemaBindingValidator validator, SchemaBinding schema)
         {
            TypeBinding type = schema.getType(new QName("urn:jboss:xb:test", "choiceType"));
            ParticleBinding particle = type.getParticle();
            ChoiceBinding choice = (ChoiceBinding) particle.getTerm();
            SequenceBinding sequence = null;
            for(ParticleBinding p : choice.getParticles())
            {
               if(p.getTerm().isModelGroup())
                  sequence = (SequenceBinding) p.getTerm();
            }
            
            if(sequence == null)
               throw new IllegalStateException("sequence not found in choice");
            
            assertEquals(2, sequence.getParticles().size());
            Iterator<ParticleBinding> iterator = sequence.getParticles().iterator();
            ParticleBinding p = iterator.next();
            p.setTerm(new ElementBinding(schema, new QName("urn:jboss:xb:test", "choice3_1"), schema.getType(Constants.QNAME_STRING)));
            p = iterator.next();
            p.setTerm(new ElementBinding(schema, new QName("urn:jboss:xb:test", "choice3_2"), schema.getType(Constants.QNAME_STRING)));
         }
      };
*/
      new AbstractErrorHandler("TypeBinding {urn:jboss:xb:test}aComplexType is not found in the SchemaBinding.")
      {
         public void handle(SchemaBindingValidator validator, SchemaBinding schema)
         {
            TypeBinding type = new TypeBinding(new QName("urn:jboss:xb:test", "aComplexType"));
            schema.addType(type);
         }
      };

      new AbstractErrorHandler("TypeBinding {urn:jboss:xb:test}aComplexType doesn't have AnyAttributeBinding")
      {
         public void handle(SchemaBindingValidator validator, SchemaBinding schema)
         {
            TypeBinding type = schema.getType(new QName("urn:jboss:xb:test", "aComplexType"));
            type.setAnyAttribute(new AnyAttributeBinding(schema, null));
         }
      };

      new AbstractErrorHandler("TypeBinding {urn:jboss:xb:test}aComplexType doesn't contain a ParticleBinding.")
      {
         public void handle(SchemaBindingValidator validator, SchemaBinding schema)
         {
            TypeBinding type = schema.getType(new QName("urn:jboss:xb:test", "aComplexType"));
            ParticleBinding particle = new ParticleBinding(new ChoiceBinding(schema));
            type.setParticle(particle);
         }
      };

      new AbstractErrorHandler("ModelGroupBinding expected to be a sequence but was choice:")
      {
         public void handle(SchemaBindingValidator validator, SchemaBinding schema)
         {
            TypeBinding type = schema.getType(new QName("urn:jboss:xb:test", "aComplexType"));
            ParticleBinding particle = type.getParticle();
            particle.setTerm(new SequenceBinding(schema));
         }
      };

      new AbstractErrorHandler("ModelGroupBinding expected to have 1 particle(s) but has 0")
      {
         public void handle(SchemaBindingValidator validator, SchemaBinding schema)
         {
            TypeBinding type = schema.getType(new QName("urn:jboss:xb:test", "aComplexType"));
            ParticleBinding particle = type.getParticle();
            ModelGroupBinding group = (ModelGroupBinding) particle.getTerm();
            group.addParticle(new ParticleBinding(new AllBinding(schema)));
         }
      };

      new AbstractErrorHandler("TermBinding expected to be a wildcard but was all:")
      {
         public void handle(SchemaBindingValidator validator, SchemaBinding schema)
         {
            TypeBinding type = schema.getType(new QName("urn:jboss:xb:test", "aComplexType"));
            ParticleBinding particle = type.getParticle();
            //ModelGroupBinding group = (ModelGroupBinding) particle.getTerm();
            //group.getParticles().clear();
            SequenceBinding group = new SequenceBinding(schema);
            particle.setTerm(group);
            WildcardBinding wildcard = new WildcardBinding(schema);
            group.addParticle(new ParticleBinding(wildcard));
         }
      };

      new AbstractErrorHandler("Wildcard processContents doesn't match: XSD processContents is 3, WildcardBinding processContents is 1")
      {
         public void handle(SchemaBindingValidator validator, SchemaBinding schema)
         {
            TypeBinding type = schema.getType(new QName("urn:jboss:xb:test", "aComplexType"));
            ParticleBinding particle = type.getParticle();
            SequenceBinding group = (SequenceBinding) particle.getTerm();
            particle = group.getParticles().iterator().next();
            WildcardBinding wildcard = (WildcardBinding) particle.getTerm();
            wildcard.setProcessContents((short) 3);
         }
      };

      new AbstractErrorHandler("XSD particle has maxOccurs unbounded but ParticleBinding of wildcard processContents=lax does not.")
      {
         public void handle(SchemaBindingValidator validator, SchemaBinding schema)
         {
            TypeBinding type = schema.getType(new QName("urn:jboss:xb:test", "aComplexType"));
            ParticleBinding particle = type.getParticle();
            SequenceBinding group = (SequenceBinding) particle.getTerm();
            particle = group.getParticles().iterator().next();
            particle.setMaxOccursUnbounded(true);
         }
      };

   }
   
   public void testMain() throws Exception
   {
      String xsd = findXML("BasicBindingValidatorUnitTestCase.xsd");
      InputSource xsdIs = new InputSource(xsd);
      
      boolean retry = true;
      while(retry)
      {
         try
         {
            validator.validate(xsdIs, schema);
            retry = false;
         }
         catch(IllegalStateException e)
         {
            ErrorHandler handler = handlerByMsg.remove(e.getMessage());
            if(handler == null)
               fail("unexpected error: " + e.getMessage());
            handler.handle(validator, schema);
         }
      }
      
      if(!handlerByMsg.isEmpty())
         fail("Not all the expected errors have been caught: " + handlerByMsg.keySet());
   }
      
   private void addErrorHandler(ErrorHandler handler)
   {
      handlerByMsg.put(handler.getMessage(), handler);
   }

   private abstract class AbstractErrorHandler implements ErrorHandler
   {
      private final String msg;

      public AbstractErrorHandler(String msg)
      {
         this.msg = msg;
         addErrorHandler(this);
      }
      
      public String getMessage()
      {
         return msg;
      }
   }
   
   public interface ErrorHandler
   {
      String getMessage();
      
      void handle(SchemaBindingValidator validator, SchemaBinding schema);
   }
}
