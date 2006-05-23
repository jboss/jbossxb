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
package org.jboss.xb.binding.sunday.unmarshalling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.jboss.xb.binding.metadata.AddMethodMetaData;
import org.jboss.xb.binding.metadata.CharactersMetaData;
import org.jboss.xb.binding.metadata.ClassMetaData;
import org.jboss.xb.binding.metadata.MapEntryMetaData;
import org.jboss.xb.binding.metadata.PropertyMetaData;
import org.jboss.xb.binding.metadata.ValueMetaData;
import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.sunday.xop.XOPUnmarshaller;
import org.jboss.xb.binding.sunday.xop.XOPMarshaller;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class TypeBinding
{
   protected QName qName;
   private ElementBinding arrayItem;
   /** Map<QName, AttributeBinding>  */
   private Map attrs = Collections.EMPTY_MAP;
   private ParticleHandler handler;//todo default handler is now in SundayContentHandler.
   //private ParticleHandler handler = DefaultHandlers.ELEMENT_HANDLER;
   private CharactersHandler charactersHandler;
   private ClassMetaData classMetaData;
   private ValueMetaData valueMetaData;
   private PropertyMetaData propertyMetaData;
   private MapEntryMetaData mapEntryMetaData;
   private SchemaBinding schemaBinding; // todo it's optional for now...
   private TypeBinding baseType;
   private boolean skip;
   private CharactersMetaData charMetaData;
   private AddMethodMetaData addMethodMetaData;
   private ValueAdapter valueAdapter = ValueAdapter.NOOP;
   private Boolean startElementCreatesObject;
   private Boolean simple;

   private WildcardBinding wildcard;
   private ParticleBinding particle;

   private List patternValues;
   private List enumValues;
   private TypeBinding itemType; // the type is a list type with this item type
   private TypeBinding simpleType;

   private XOPUnmarshaller xopUnmarshaller;
   private XOPMarshaller xopMarshaller;

   public TypeBinding()
   {
      this.qName = null;
   }

   public TypeBinding(QName qName)
   {
      //this(qName, (CharactersHandler)null);
      this(qName, DefaultHandlers.CHARACTERS_HANDLER);
   }

   public TypeBinding(CharactersHandler charactersHandler)
   {
      this(null, charactersHandler);
   }

   public TypeBinding(QName qName, CharactersHandler charactersHandler)
   {
      this.qName = qName;
      this.charactersHandler = charactersHandler;
   }

   public TypeBinding(QName qName, TypeBinding baseType)
   {
      this(qName, baseType.charactersHandler);

      if(baseType.particle != null)
      {
         // todo
         this.particle = baseType.particle;
      }

      this.arrayItem = baseType.arrayItem;
      this.attrs = new HashMap(baseType.attrs);
      this.classMetaData = baseType.classMetaData;
      this.valueMetaData = baseType.valueMetaData;
      this.propertyMetaData = baseType.propertyMetaData;
      this.mapEntryMetaData = baseType.mapEntryMetaData;
      this.schemaBinding = baseType.schemaBinding;
      this.baseType = baseType;

      if(!baseType.isStartElementCreatesObject())
      {
         this.handler = baseType.handler;
      }
   }

   public QName getQName()
   {
      return qName;
   }

   public ElementBinding getElement(QName name)
   {
      return getElement(name, true);
   }

   private ElementBinding getElement(QName name, boolean ignoreWildcards)
   {
      ElementBinding element = null;
      if(particle != null)
      {
         ModelGroupBinding modelGroup = (ModelGroupBinding)particle.getTerm();
         element = modelGroup.newCursor(particle).getElement(name, null, ignoreWildcards);
      }

      if(element == null && !ignoreWildcards && wildcard != null)
      {
         element = wildcard.getElement(name, null);
      }
      return element;
   }

   public void addParticle(ParticleBinding particle)
   {
      ModelGroupBinding modelGroup;
      if(this.particle == null)
      {
         modelGroup = new AllBinding(schemaBinding);
         this.particle = new ParticleBinding(modelGroup);

         if(particle.isRepeatable() && particle.getTerm() instanceof ElementBinding)
         {
            arrayItem = (ElementBinding)particle.getTerm();
         }
      }
      else
      {
         modelGroup = (ModelGroupBinding)this.particle.getTerm();
         arrayItem = null;
      }
      modelGroup.addParticle(particle);
   }

   public void addElement(ElementBinding element)
   {
      addElement(element, 1, false);
   }

   public void addElement(ElementBinding element, int minOccurs, boolean unbounded)
   {
      ParticleBinding particle = new ParticleBinding(element);
      particle.setMinOccurs(minOccurs);
      particle.setMaxOccursUnbounded(unbounded);
      addParticle(particle);
   }

   public ElementBinding addElement(QName name, TypeBinding type)
   {
      return addElement(name, type, 1, false);
   }

   public ElementBinding addElement(QName name, TypeBinding type, int minOccurs, boolean unbounded)
   {
      ElementBinding el = new ElementBinding(schemaBinding, name, type);
      addElement(el, minOccurs, unbounded);
      return el;
   }

   public void addGroup(Map group)
   {
      for(Iterator i = group.entrySet().iterator(); i.hasNext();)
      {
         Map.Entry entry = (Map.Entry)i.next();
         QName name = (QName)entry.getKey();
         TypeBinding type = (TypeBinding)entry.getValue();
         addElement(name, type);
      }
   }

   public AttributeBinding getAttribute(QName qName)
   {
      return (AttributeBinding)attrs.get(qName);
   }

   /**
    * Go through the type attributes to see if there are any with defaults
    * that do not appears in the attrs list.
    *
    * @param attrs - the attributes seen in the document
    * @return a possibly augmented list that includes unspecified attributes
    *    with default values.
    */
   public Attributes expandWithDefaultAttributes(Attributes attrs)
   {
      if(this.attrs.size() == 0)
      {
         return attrs;
      }

      // Map<QName, AttributeBinding>
      HashMap attrsNotSeen = new HashMap(this.attrs);
      for(int n = 0; n < attrs.getLength(); n ++)
      {
         QName name = new QName(attrs.getURI(n), attrs.getLocalName(n));
         attrsNotSeen.remove(name);
      }

      Attributes expandedAttrs = attrs;
      if( attrsNotSeen.size() > 0 )
      {
         AttributesImpl tmp = new AttributesImpl(attrs);
         Iterator iter = attrsNotSeen.entrySet().iterator();
         while( iter.hasNext() )
         {
            Map.Entry entry = (Map.Entry) iter.next();
            QName name = (QName) entry.getKey();
            AttributeBinding binding = (AttributeBinding) entry.getValue();
            String constraint = binding.getDefaultConstraint();
            if( constraint != null )
            {
               // the Javadoc for Attributes.getType(i) says:
               // "The attribute type is one of the strings
               // "CDATA", "ID", "IDREF", "IDREFS", "NMTOKEN", "NMTOKENS", "ENTITY", "ENTITIES",
               // or "NOTATION" (always in upper case)."
               tmp.addAttribute(name.getNamespaceURI(), name.getLocalPart(),
                  name.toString(), "CDATA", constraint);
            }
         }
         expandedAttrs = tmp;
      }

      return expandedAttrs;
   }

   public AttributeBinding addAttribute(QName name, TypeBinding type, AttributeHandler handler)
   {
      AttributeBinding attr = new AttributeBinding(schemaBinding, name, type, handler);
      switch(attrs.size())
      {
         case 0:
            attrs = Collections.singletonMap(name, attr);
            break;
         case 1:
            attrs = new HashMap(attrs);
         default:
            attrs.put(name, attr);
      }
      return attr;
   }

   public Collection getAttributes()
   {
      return attrs.values();
   }

   public CharactersHandler getCharactersHandler()
   {
      return charactersHandler;
   }

   /**
    * This method will create a new simple type binding with the passed in characters handler
    * and set this simple type as the simple type of the complex type the method was invoked on.
    * @param charactersHandler
    */
   public void setSimpleType(CharactersHandler charactersHandler)
   {
      setSimpleType(new TypeBinding(charactersHandler));
   }

   public TypeBinding getSimpleType()
   {
      return simpleType;
   }

   public void setSimpleType(TypeBinding simpleType)
   {
      this.simpleType = simpleType;
   }

   public void setHandler(ParticleHandler handler)
   {
      this.handler = handler;
   }

   public ParticleHandler getHandler()
   {
      return handler;
   }

   public void pushInterceptor(QName qName, ElementInterceptor interceptor)
   {
      ElementBinding el = getElement(qName);
      if(el == null)
      {
         el = addElement(qName, new TypeBinding());
      }
      el.pushInterceptor(interceptor);
   }

   public TypeBinding getBaseType()
   {
      return baseType;
   }

   public void setBaseType(TypeBinding baseType)
   {
      this.baseType = baseType;
   }

   public boolean isSimple()
   {
      return simple == null ? particle == null && attrs.isEmpty() : simple.booleanValue();
   }

   public void setSimple(boolean simple)
   {
      this.simple = simple ? Boolean.TRUE : Boolean.FALSE;
   }

   public ClassMetaData getClassMetaData()
   {
      return classMetaData;
   }

   public void setClassMetaData(ClassMetaData classMetaData)
   {
      this.classMetaData = classMetaData;
   }

   public SchemaBinding getSchemaBinding()
   {
      return schemaBinding;
   }

   public void setSchemaBinding(SchemaBinding schemaBinding)
   {
      this.schemaBinding = schemaBinding;
   }

   public void setValueMetaData(ValueMetaData valueMetaData)
   {
      this.valueMetaData = valueMetaData;
   }

   public ValueMetaData getValueMetaData()
   {
      return valueMetaData;
   }

   public PropertyMetaData getPropertyMetaData()
   {
      return propertyMetaData;
   }

   public void setPropertyMetaData(PropertyMetaData propertyMetaData)
   {
      this.propertyMetaData = propertyMetaData;
   }

   public MapEntryMetaData getMapEntryMetaData()
   {
      return mapEntryMetaData;
   }

   public void setMapEntryMetaData(MapEntryMetaData mapEntryMetaData)
   {
      this.mapEntryMetaData = mapEntryMetaData;
   }

   public void setSkip(boolean skip)
   {
      this.skip = skip;
   }

   public boolean isSkip()
   {
      return skip;
   }

   public CharactersMetaData getCharactersMetaData()
   {
      return charMetaData;
   }

   public void setCharactersMetaData(CharactersMetaData charMetaData)
   {
      this.charMetaData = charMetaData;
   }

   public void setAddMethodMetaData(AddMethodMetaData addMethodMetaData)
   {
      this.addMethodMetaData = addMethodMetaData;
   }

   public AddMethodMetaData getAddMethodMetaData()
   {
      return addMethodMetaData;
   }

   public ValueAdapter getValueAdapter()
   {
      return valueAdapter;
   }

   public void setValueAdapter(ValueAdapter valueAdapter)
   {
      this.valueAdapter = valueAdapter;
   }

   public boolean isStartElementCreatesObject()
   {
      return startElementCreatesObject == null ?
         particle != null || !attrs.isEmpty() : startElementCreatesObject.booleanValue();
   }

   public void setStartElementCreatesObject(boolean startElementCreatesObject)
   {
      this.startElementCreatesObject = startElementCreatesObject ? Boolean.TRUE : Boolean.FALSE;
   }

   public void setWildcard(WildcardBinding wildcard)
   {
      this.wildcard = wildcard;
   }

   public WildcardBinding getWildcard()
   {
      return wildcard;
   }

   public boolean hasWildcard()
   {
      return wildcard != null;
   }

   public ParticleBinding getParticle()
   {
      return particle;
   }

   public void setParticle(ParticleBinding particle)
   {
      this.particle = particle;
   }

   public List getLexicalPattern()
   {
      return patternValues;
   }

   public void addLexicalPattern(String patternValue)
   {
      if(patternValues == null)
      {
         patternValues = Collections.singletonList(patternValue);
      }
      else
      {
         if(patternValues.size() == 1)
         {
            patternValues = new ArrayList(patternValues);
         }
         patternValues.add(patternValue);
      }
   }

   public List getLexicalEnumeration()
   {
      return enumValues;
   }

   public void addEnumValue(String value)
   {
      if(enumValues == null)
      {
         enumValues = Collections.singletonList(value);
      }
      else
      {
         if(enumValues.size() == 1)
         {
            enumValues = new ArrayList(enumValues);
         }
         enumValues.add(value);
      }
   }

   public void setItemType(TypeBinding itemType)
   {
      this.itemType = itemType;
   }

   public TypeBinding getItemType()
   {
      return itemType;
   }

   public XOPUnmarshaller getXopUnmarshaller()
   {
      return xopUnmarshaller == null ?
         (schemaBinding == null ? null : schemaBinding.getXopUnmarshaller()) : xopUnmarshaller;
   }

   public void setXopUnmarshaller(XOPUnmarshaller xopUnmarshaller)
   {
      this.xopUnmarshaller = xopUnmarshaller;
   }

   public XOPMarshaller getXopMarshaller()
   {
      return xopMarshaller == null ?
         (schemaBinding == null ? null : schemaBinding.getXopMarshaller()) : xopMarshaller;
   }

   public void setXopMarshaller(XOPMarshaller xopMarshaller)
   {
      this.xopMarshaller = xopMarshaller;
   }

   public boolean hasOnlyXmlMimeAttributes()
   {
      if(attrs.isEmpty())
      {
         return false;
      }
      else
      {
         Iterator iter = attrs.keySet().iterator();
         while(iter.hasNext())
         {
            QName qName = (QName)iter.next();
            if(!Constants.NS_XML_MIME.equals(qName.getNamespaceURI()))
            {
               return false;
            }
         }
      }
      return true;
   }

   public String toString()
   {
      return super.toString() + "[" + qName + "]";
   }
}
