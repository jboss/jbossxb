/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding.sunday.unmarshalling;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import javax.xml.namespace.QName;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;
import org.jboss.xb.binding.metadata.AddMethodMetaData;
import org.jboss.xb.binding.metadata.CharactersMetaData;
import org.jboss.xb.binding.metadata.ClassMetaData;
import org.jboss.xb.binding.metadata.MapEntryMetaData;
import org.jboss.xb.binding.metadata.PropertyMetaData;
import org.jboss.xb.binding.metadata.ValueMetaData;
import org.jboss.xb.binding.sunday.unmarshalling.impl.runtime.RtCharactersHandler;
import org.jboss.xb.binding.sunday.unmarshalling.impl.runtime.RtElementHandler;
import org.jboss.xb.binding.JBossXBRuntimeException;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class TypeBinding
{
   private final QName qName;
   private ElementBinding arrayItem;
   /** Map<QName, AttributeBinding>  */
   private Map attrs = Collections.EMPTY_MAP;
   private ParticleHandler handler = RtElementHandler.INSTANCE;
   private CharactersHandler simpleType;
   private ClassMetaData classMetaData;
   private ValueMetaData valueMetaData;
   private PropertyMetaData propertyMetaData;
   private MapEntryMetaData mapEntryMetaData;
   private SchemaBinding schemaBinding; // todo it's optional for now...
   private TypeBinding baseType;
   private boolean skip;
   private CharactersMetaData charMetaData;
   private PropertyMetaData wildcardPropertyMetaData;
   private AddMethodMetaData addMethodMetaData;
   private ValueAdapter valueAdapter = ValueAdapter.NOOP;
   private Boolean startElementCreatesObject;

   private WildcardBinding wildcard;
   private ModelGroupBinding modelGroup;

   public TypeBinding()
   {
      this(null);
   }

   public TypeBinding(QName qName)
   {
      this(qName, RtCharactersHandler.INSTANCE);
   }

   public TypeBinding(QName qName, CharactersHandler simple)
   {
      this.qName = qName;
      this.simpleType = simple;
   }

   public TypeBinding(QName qName, TypeBinding baseType)
   {
      this(qName, baseType.simpleType);

      if(baseType.modelGroup != null)
      {
         try
         {
            this.modelGroup = (ModelGroupBinding)baseType.modelGroup.clone();
         }
         catch(CloneNotSupportedException e)
         {
            throw new JBossXBRuntimeException(e.getMessage());
         }
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
      return getElement(name, null);
   }

   public ElementBinding getElement(QName name, Attributes atts)
   {
      ElementBinding element = null;
      if(modelGroup != null)
      {
         element = modelGroup.newCursor().getElement(name, atts);
      }

      if(element == null && wildcard != null)
      {
         element = wildcard.getElement(name, atts);
      }
      return element;
   }

   public void addElement(ElementBinding binding)
   {
      if(modelGroup == null)
      {
         modelGroup = new AllBinding();
         if(binding.isMultiOccurs())
         {
            arrayItem = binding;
         }
      }
      else
      {
         arrayItem = null;
      }
      modelGroup.addElement(binding);
   }

   public ElementBinding addElement(QName name, TypeBinding type)
   {
      ElementBinding el = new ElementBinding(schemaBinding, name, type);
      addElement(el);
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
               QName typeName = binding.getType().getQName();
               tmp.addAttribute(name.getNamespaceURI(), name.getLocalPart(),
                  name.toString(), typeName.toString(), constraint);
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

   public CharactersHandler getSimpleType()
   {
      return simpleType;
   }

   public void setSimpleType(CharactersHandler simpleType)
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

   public boolean isSimple()
   {
      return modelGroup == null && attrs.isEmpty();
   }

   public boolean isArrayWrapper()
   {
      return arrayItem != null || modelGroup != null && modelGroup.getArrayItem() != null;
   }

   public ElementBinding getArrayItem()
   {
      return arrayItem == null ? (modelGroup == null ? null : modelGroup.getArrayItem()) : arrayItem;
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

   public PropertyMetaData getWildcardPropertyMetaData()
   {
      return wildcardPropertyMetaData;
   }

   public void setWildcardPropertyMetaData(PropertyMetaData wildcardPropertyMetaData)
   {
      this.wildcardPropertyMetaData = wildcardPropertyMetaData;
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
               modelGroup != null || !attrs.isEmpty() : startElementCreatesObject.booleanValue();
   }

   public void setStartElementCreatesObject(boolean startElementCreatesObject)
   {
      this.startElementCreatesObject = startElementCreatesObject ? Boolean.TRUE : Boolean.FALSE;
   }

   public void setWildcard(WildcardBinding wildcard)
   {
      this.wildcard = wildcard;
   }

   public boolean hasWildcard()
   {
      return wildcard != null;
   }

   public ModelGroupBinding getModelGroup()
   {
      return modelGroup;
   }

   public void setModelGroup(ModelGroupBinding modelGroup)
   {
      this.modelGroup = modelGroup;
   }
}
