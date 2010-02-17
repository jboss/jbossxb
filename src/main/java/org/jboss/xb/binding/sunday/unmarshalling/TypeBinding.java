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
import org.jboss.xb.binding.Util;
import org.jboss.xb.binding.sunday.marshalling.TermBeforeMarshallingCallback;
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
   private static final List<ElementInterceptor> EMPTY_ICEPTOR_LIST = Collections.<ElementInterceptor>emptyList();
   
   protected QName qName;
   /** Map<QName, AttributeBinding>  */
   private Map<QName, AttributeBinding> attrs;
   private AnyAttributeBinding anyAttribute;
   private ParticleHandler handler;//todo default handler is now in SundayContentHandler.
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
   private TermBeforeMarshallingCallback beforeMarshallingCallback;
   private TermBeforeSetParentCallback beforeSetParentCallback;
   
   private int startElementCreatesObject;
   private int simple;
   private int ignoreEmptyString;

   private WildcardBinding wildcard;
   private ParticleBinding particle;

   private List<String> patternValues;
   private List<String> enumValues;
   private TypeBinding itemType; // the type is a list type with this item type
   private TypeBinding simpleType;

   private XOPUnmarshaller xopUnmarshaller;
   private XOPMarshaller xopMarshaller;

   /** Map<QName, List<ElementInterceptor>>
    * these are local element interceptors that are "added" to the interceptor stack
    * defined in the element binding */
   private Map<QName, List<ElementInterceptor>> interceptors;
   
   public TypeBinding()
   {
      this.qName = null;
   }

   public TypeBinding(QName qName)
   {
      //this(qName, (CharactersHandler)null);
      this(qName, DefaultHandlers.CHARACTERS_HANDLER_FACTORY.newCharactersHandler());
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

      this.attrs = baseType.attrs == null ? null : new HashMap<QName, AttributeBinding>(baseType.attrs);
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
         element = modelGroup.getElement(name, null, ignoreWildcards);
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
      }
      else
      {
         modelGroup = (ModelGroupBinding)this.particle.getTerm();
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

   public void addGroup(Map<QName, TypeBinding> group)
   {
      for(Iterator<Map.Entry<QName, TypeBinding>> i = group.entrySet().iterator(); i.hasNext();)
      {
         Map.Entry<QName, TypeBinding> entry = i.next();
         QName name = entry.getKey();
         TypeBinding type = entry.getValue();
         addElement(name, type);
      }
   }

   public AttributeBinding getAttribute(QName qName)
   {
      return attrs == null ? null : attrs.get(qName);
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
      if(this.attrs == null)
         return attrs;

      // Map<QName, AttributeBinding>
      HashMap<QName, AttributeBinding> attrsNotSeen = new HashMap<QName, AttributeBinding>(this.attrs);
      for(int n = 0; n < attrs.getLength(); n ++)
      {
         QName name = new QName(attrs.getURI(n), attrs.getLocalName(n));
         attrsNotSeen.remove(name);
      }

      Attributes expandedAttrs = attrs;
      if( attrsNotSeen.size() > 0 )
      {
         AttributesImpl tmp = new AttributesImpl(attrs);
         Iterator<Map.Entry<QName, AttributeBinding>> iter = attrsNotSeen.entrySet().iterator();
         while( iter.hasNext() )
         {
            Map.Entry<QName, AttributeBinding> entry = (Map.Entry<QName, AttributeBinding>) iter.next();
            QName name = entry.getKey();
            AttributeBinding binding = entry.getValue();
            String constraint = binding.getDefaultConstraint();
            if( constraint != null )
            {
               // the Javadoc for Attributes.getType(i) says:
               // "The attribute type is one of the strings
               // "CDATA", "ID", "IDREF", "IDREFS", "NMTOKEN", "NMTOKENS", "ENTITY", "ENTITIES",
               // or "NOTATION" (always in upper case)."
               tmp.addAttribute(name.getNamespaceURI(), name.getLocalPart(), name.toString(), "CDATA", constraint);
            }
         }
         expandedAttrs = tmp;
      }

      return expandedAttrs;
   }
   
   public AttributeBinding addAttribute(QName name, TypeBinding type, AttributeHandler handler)
   {
      AttributeBinding attr = new AttributeBinding(schemaBinding, name, type, handler);
      addAttribute(attr);
      return attr;
   }

   public void addAttribute(AttributeBinding attr)
   {
      if(attrs == null)
      {
         attrs = Collections.singletonMap(attr.getQName(), attr);
         return;
      }
      
      if(attrs.size() == 1)
         attrs = new HashMap<QName, AttributeBinding>(attrs);         
      attrs.put(attr.getQName(), attr);
   }

   public Collection<AttributeBinding> getAttributes()
   {
      return attrs == null ? Collections.<AttributeBinding>emptyList() : attrs.values();
   }

   public CharactersHandler getCharactersHandler()
   {
      return charactersHandler;
   }

   public void setCharactersHandler(CharactersHandler charactersHandler)
   {
      this.charactersHandler = charactersHandler;
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

   /**
    * Pushes a new interceptor for the specified element.
    * If the element has a global scope in the schema,
    * this interceptor will invoked only when the element is found to be a child
    * of this type. This is the difference between the local interceptors
    * added with this method and the interceptors added directly to the
    * element binding.
    * When element is started, local interceptors are invoked before the interceptors
    * from the element binding. In the endElement the order is reversed.
    * 
    * @param qName
    * @param interceptor
    */
   public void pushInterceptor(QName qName, ElementInterceptor interceptor)
   {
      ElementBinding el = getElement(qName);
      if(el == null)
         el = addElement(qName, new TypeBinding());
      
      if(interceptors == null)
      {
         interceptors = Collections.singletonMap(qName, Collections.singletonList(interceptor));
         return;
      }
      
      List<ElementInterceptor> intList = (List<ElementInterceptor>) interceptors.get(qName);
      if(intList == null)
      {
         intList = Collections.singletonList(interceptor);
         if(interceptors.size() == 1)
            interceptors = new HashMap<QName, List<ElementInterceptor>>(interceptors);            
         interceptors.put(qName, intList);
      }
      else
      {
         if(intList.size() == 1)
         {
            intList = new ArrayList<ElementInterceptor>(intList);
            interceptors.put(qName, intList);
         }
         intList.add(interceptor);
      }
   }

   /**
    * Returns a list of local interceptors for the element.
    * If there are no local interceptors for the element then
    * an empty list is returned.
    * 
    * @param qName
    * @return
    */
   public List<ElementInterceptor> getInterceptors(QName qName)
   {
      if(interceptors == null)
         return EMPTY_ICEPTOR_LIST;
      List<ElementInterceptor> list = interceptors.get(qName);
      return list == null ? EMPTY_ICEPTOR_LIST : list;
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
      // actually, a type can be complex when the particle is null and
      // there are no attributes. But the XsdBinder will set the value of simple
      // to false. This check is for schema bindings created programmatically
      return simple == Constants.NOT_SET ? particle == null && attrs == null : simple == Constants.TRUE;
   }

   public void setSimple(boolean simple)
   {
      this.simple = simple ? Constants.TRUE : Constants.FALSE;
   }

   public boolean isTextContentAllowed()
   {
      return simpleType != null || isSimple();
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

   /**
    * Whether the ParticleHandler should return a non-null object from its
    * startParticle method.
    * This should be true for any type that has child elements and/or attributes,
    * i.e. complex types. If the type is simple or it's a complex type that should
    * be treated as a simple type then this value should be false.
    * 
    * @return
    */
   public boolean isStartElementCreatesObject()
   {
      return startElementCreatesObject == Constants.NOT_SET ?
         particle != null || attrs != null : startElementCreatesObject == Constants.TRUE;
   }

   /**
    * Whether the ParticleHandler should return a non-null object from its
    * startParticle method.
    * This should be true for any type that has child elements and/or attributes,
    * i.e. complex types. If the type is simple or it's a complex type that should
    * be treated as a simple type then this value should be false.
    * 
    * @param startElementCreatesObject
    */
   public void setStartElementCreatesObject(boolean startElementCreatesObject)
   {
      this.startElementCreatesObject = startElementCreatesObject ? Constants.TRUE : Constants.FALSE;
   }

   private boolean initializedWildcard;
   public WildcardBinding getWildcard()
   {
      if(initializedWildcard)
         return wildcard;
      
      if(particle != null)
      {
         wildcard = Util.getWildcard(particle.getTerm());
         initializedWildcard = true;
      }
      
      return wildcard;
   }

   public ParticleBinding getParticle()
   {
      return particle;
   }

   public void setParticle(ParticleBinding particle)
   {
      this.particle = particle;
   }

   public List<String> getLexicalPattern()
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
            patternValues = new ArrayList<String>(patternValues);
         }
         patternValues.add(patternValue);
      }
   }

   public List<String> getLexicalEnumeration()
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
            enumValues = new ArrayList<String>(enumValues);
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
      if(attrs == null)
         return false;

      Iterator<QName> iter = attrs.keySet().iterator();
      while(iter.hasNext())
      {
         QName qName = iter.next();
         if(!Constants.NS_XML_MIME.equals(qName.getNamespaceURI()))
            return false;
      }
      return true;
   }

   public void setBeforeMarshallingCallback(TermBeforeMarshallingCallback marshallingHandler)
   {
      this.beforeMarshallingCallback = marshallingHandler;
   }

   public TermBeforeMarshallingCallback getBeforeMarshallingCallback()
   {
      return beforeMarshallingCallback;
   }

   public void setBeforeSetParentCallback(TermBeforeSetParentCallback beforeSetParent)
   {
      this.beforeSetParentCallback = beforeSetParent;
   }

   public TermBeforeSetParentCallback getBeforeSetParentCallback()
   {
      return beforeSetParentCallback;
   }

   public boolean isIgnoreEmptyString()
   {
      return ignoreEmptyString == Constants.NOT_SET ? !isSimple() : ignoreEmptyString == Constants.TRUE;
   }
   
   public void setIgnoreEmptyString(boolean value)
   {
      this.ignoreEmptyString = value ? Constants.TRUE : Constants.FALSE;
   }
   
   public AnyAttributeBinding getAnyAttribute()
   {
      return anyAttribute;
   }
   
   public void setAnyAttribute(AnyAttributeBinding anyAttribute)
   {
      this.anyAttribute = anyAttribute;
   }
   
   public String toString()
   {
      return super.toString() + "[" + qName + "]";
   }
}
