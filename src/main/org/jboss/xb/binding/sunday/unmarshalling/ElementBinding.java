/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding.sunday.unmarshalling;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

import org.jboss.xb.binding.metadata.AddMethodMetaData;
import org.jboss.xb.binding.metadata.ClassMetaData;
import org.jboss.xb.binding.metadata.MapEntryMetaData;
import org.jboss.xb.binding.metadata.PropertyMetaData;
import org.jboss.xb.binding.metadata.PutMethodMetaData;
import org.jboss.xb.binding.metadata.ValueMetaData;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class ElementBinding
{
   private List interceptors = Collections.EMPTY_LIST;

   private final SchemaBinding schema;
   private final TypeBinding typeBinding;
   private ClassMetaData classMetaData;
   private PropertyMetaData propertyMetaData;
   private MapEntryMetaData mapEntryMetaData;
   private PutMethodMetaData putMethodMetaData;
   private AddMethodMetaData addMethodMetaData;
   private ValueMetaData valueMetaData;
   private boolean mapEntryKey;
   private boolean mapEntryValue;
   private boolean multiOccurs;
   private Boolean skip;

   public ElementBinding(SchemaBinding schema, TypeBinding typeBinding)
   {
      this.schema = schema;
      this.typeBinding = typeBinding;
   }

   public List getInterceptors()
   {
      return interceptors;
   }

   public TypeBinding getType()
   {
      return typeBinding;
   }

   public void pushInterceptor(ElementInterceptor interceptor)
   {
      switch(interceptors.size())
      {
         case 0:
            interceptors = Collections.singletonList(interceptor);
            break;
         case 1:
            interceptors = new ArrayList(interceptors);
         default:
            interceptors.add(interceptor);

      }
   }

   public ClassMetaData getClassMetaData()
   {
      ClassMetaData result = classMetaData;
      if(result == null && mapEntryMetaData == null)
      {
            result = typeBinding.getClassMetaData();
      }
      return result;
   }

   public void setClassMetaData(ClassMetaData classMetaData)
   {
      this.classMetaData = classMetaData;
   }

   public PropertyMetaData getPropertyMetaData()
   {
      // todo: this method doesn't check property metadata on its type because the rules to use property
      // metadata on a type should be clarified.
      return propertyMetaData;
   }

   public void setPropertyMetaData(PropertyMetaData propertyMetaData)
   {
      this.propertyMetaData = propertyMetaData;
   }

   public MapEntryMetaData getMapEntryMetaData()
   {
      MapEntryMetaData result = mapEntryMetaData;
      if(result == null && classMetaData == null)
      {
         result = typeBinding.getMapEntryMetaData();
      }
      return result;
   }

   public void setMapEntryMetaData(MapEntryMetaData mapEntryMetaData)
   {
      this.mapEntryMetaData = mapEntryMetaData;
   }

   public ValueMetaData getValueMetaData()
   {
      return valueMetaData != null ? valueMetaData : typeBinding.getValueMetaData();
   }

   public void setValueMetaData(ValueMetaData valueMetaData)
   {
      this.valueMetaData = valueMetaData;
   }

   public boolean isMultiOccurs()
   {
      return multiOccurs;
   }

   public void setMultiOccurs(boolean multiOccurs)
   {
      this.multiOccurs = multiOccurs;
   }

   public void setMapEntryKey(boolean mapEntryKey)
   {
      this.mapEntryKey = mapEntryKey;
   }

   public boolean isMapEntryKey()
   {
      return mapEntryKey;
   }

   public boolean isMapEntryValue()
   {
      return mapEntryValue;
   }

   public void setMapEntryValue(boolean mapEntryValue)
   {
      this.mapEntryValue = mapEntryValue;
   }

   public PutMethodMetaData getPutMethodMetaData()
   {
      // todo should types be allowed to have putMethod metadata
      return putMethodMetaData;
   }

   public void setPutMethodMetaData(PutMethodMetaData putMethodMetaData)
   {
      this.putMethodMetaData = putMethodMetaData;
   }

   public AddMethodMetaData getAddMethodMetaData()
   {
      AddMethodMetaData result =  addMethodMetaData;
      if(result == null && putMethodMetaData == null && propertyMetaData == null)
      {
         result = typeBinding.getAddMethodMetaData();
      }
      return result;
   }

   public void setAddMethodMetaData(AddMethodMetaData addMethodMetaData)
   {
      this.addMethodMetaData = addMethodMetaData;
   }

   public SchemaBinding getSchema()
   {
      return schema;
   }

   public void setSkip(boolean skip)
   {
      this.skip = skip ? Boolean.TRUE : Boolean.FALSE;
   }

   public boolean isSkip()
   {
      return skip == null ? typeBinding.isSkip() : skip.booleanValue();
   }
}
