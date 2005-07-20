/*
 * JBoss, the OpenSource J2EE webOS
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
import javax.xml.namespace.NamespaceContext;
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

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class TypeBinding
{
   private final QName qName;
   private Map elements = Collections.EMPTY_MAP;
   private QName arrayItemQName;
   private ElementBinding arrayItem;
   /** Map<QName, AttributeBinding>  */
   private Map attrs = Collections.EMPTY_MAP;
   private ElementHandler handler = RtElementHandler.INSTANCE;
   private CharactersHandler simpleType;
   private ClassMetaData classMetaData;
   private ValueMetaData valueMetaData;
   private PropertyMetaData propertyMetaData;
   private MapEntryMetaData mapEntryMetaData;
   private boolean mapEntryKey;
   private boolean mapEntryValue;
   private SchemaBinding schemaBinding; // todo it's optional for now...
   private SchemaBindingResolver schemaResolver;
   private TypeBinding baseType;
   private boolean skip;
   private CharactersMetaData charMetaData;
   private PropertyMetaData wildcardPropertyMetaData;
   private AddMethodMetaData addMethodMetaData;

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
      this.elements = new HashMap(baseType.elements);
      this.arrayItemQName = baseType.arrayItemQName;
      this.arrayItem = baseType.arrayItem;
      this.attrs = new HashMap(baseType.attrs);
      this.classMetaData = baseType.classMetaData;
      this.valueMetaData = baseType.valueMetaData;
      this.propertyMetaData = baseType.propertyMetaData;
      this.mapEntryMetaData = baseType.mapEntryMetaData;
      this.mapEntryKey = baseType.mapEntryKey;
      this.mapEntryValue = baseType.mapEntryValue;
      this.schemaBinding = baseType.schemaBinding;
      this.schemaResolver = baseType.schemaResolver;
      this.baseType = baseType;

      if(!baseType.isSimple())
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
      ElementBinding element = (ElementBinding)elements.get(name);
      if(element == null && schemaResolver != null)
      {
         // this is wildcard handling
         SchemaBinding schema = schemaResolver.resolve(name.getNamespaceURI(), name.getLocalPart());
         if(schema != null)
         {
            element = schema.getElement(name);
         }
      }
      return element;
   }

   public void addElement(QName qName, ElementBinding binding)
   {
      switch(elements.size())
      {
         case 0:
            elements = Collections.singletonMap(qName, binding);
            if(binding.isMultiOccurs())
            {
               arrayItem = binding;
               arrayItemQName = qName;
            }
            break;
         case 1:
            elements = new HashMap(elements);
            arrayItem = null;
            arrayItemQName = null;
         default:
            elements.put(qName, binding);
      }
   }

   public ElementBinding addElement(QName name, TypeBinding type)
   {
      ElementBinding el = new ElementBinding(schemaBinding, type);
      addElement(name, el);
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
      if( this.attrs.size() == 0 )
         return attrs;

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
      AttributeBinding attr = new AttributeBinding(schemaBinding, type, handler);
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

   public Object startElement(Object parent, QName qName, ElementBinding element)
   {
      return handler.startElement(parent, qName, element);
   }

   public void attributes(Object o,
                          QName elementName,
                          ElementBinding element,
                          Attributes attrs,
                          NamespaceContext nsCtx)
   {
      handler.attributes(o, elementName, element, attrs, nsCtx);
   }

   public Object endElement(Object parent, Object o, ElementBinding element, QName qName)
   {
      return handler.endElement(o, qName, element);
   }

   public void setHandler(ElementHandler handler)
   {
      this.handler = handler;
   }

   public ElementHandler getHandler()
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
      return elements.isEmpty() && attrs.isEmpty();
   }

   public boolean hasSimpleContent()
   {
      return elements.isEmpty();
   }

   public boolean isArrayWrapper()
   {
      return arrayItem != null;
   }

   public ElementBinding getArrayItem()
   {
      return arrayItem;
   }

   public QName getArrayItemQName()
   {
      return arrayItemQName;
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

   public SchemaBindingResolver getSchemaResolver()
   {
      return schemaResolver;
   }

   public void setSchemaResolver(SchemaBindingResolver schemaResolver)
   {
      this.schemaResolver = schemaResolver;
   }

   public boolean isMapEntryKey()
   {
      return mapEntryKey;
   }

   public void setMapEntryKey(boolean mapEntryKey)
   {
      this.mapEntryKey = mapEntryKey;
   }

   public boolean isMapEntryValue()
   {
      return mapEntryValue;
   }

   public void setMapEntryValue(boolean mapEntryValue)
   {
      this.mapEntryValue = mapEntryValue;
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

   public boolean isWildcardElement(QName qName)
   {
      return !elements.containsKey(qName);
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
}
