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

import javax.xml.namespace.QName;
import org.jboss.xb.binding.metadata.AddMethodMetaData;
import org.jboss.xb.binding.metadata.ClassMetaData;
import org.jboss.xb.binding.metadata.MapEntryMetaData;
import org.jboss.xb.binding.metadata.PropertyMetaData;
import org.jboss.xb.binding.metadata.PutMethodMetaData;
import org.jboss.xb.binding.metadata.ValueMetaData;
import org.jboss.xb.binding.JBossXBRuntimeException;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class ElementBinding
   implements ParticleBinding
{
   private List interceptors = Collections.EMPTY_LIST;

   private final SchemaBinding schema;
   private final QName qName;
   private final TypeBinding typeBinding;
   private boolean nillable;
   private ClassMetaData classMetaData;
   private PropertyMetaData propertyMetaData;
   private MapEntryMetaData mapEntryMetaData;
   private PutMethodMetaData putMethodMetaData;
   private AddMethodMetaData addMethodMetaData;
   private ValueMetaData valueMetaData;
   private boolean mapEntryKey;
   private boolean mapEntryValue;

   private int minOccurs;
   private int maxOccurs;
   private boolean maxOccursUnbounded;

   private Boolean skip;
   private ValueAdapter valueAdapter;

   public ElementBinding(SchemaBinding schema, QName qName, TypeBinding typeBinding)
   {
      this.schema = schema;
      this.qName = qName;
      this.typeBinding = typeBinding;

      if(qName == null)
      {
         throw new JBossXBRuntimeException("Each element must have a non-null QName!");
      }
   }

   public QName getQName()
   {
      return qName;
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

   public int getMinOccurs()
   {
      return minOccurs;
   }

   public void setMinOccurs(int minOccurs)
   {
      this.minOccurs = minOccurs;
   }

   public int getMaxOccurs()
   {
      return maxOccurs;
   }

   public void setMaxOccurs(int maxOccurs)
   {
      this.maxOccurs = maxOccurs;
   }

   public boolean getMaxOccursUnbounded()
   {
      return maxOccursUnbounded;
   }

   public void setMaxOccursUnbounded(boolean maxOccursUnbounded)
   {
      this.maxOccursUnbounded = maxOccursUnbounded;
   }

   public boolean isMultiOccurs()
   {
      return maxOccursUnbounded || maxOccurs > 1 || minOccurs > 1;
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

   public ValueAdapter getValueAdapter()
   {
      return valueAdapter == null ? typeBinding.getValueAdapter() : valueAdapter;
   }

   public void setValueAdapter(ValueAdapter valueAdapter)
   {
      this.valueAdapter = valueAdapter;
   }

   public boolean isNillable()
   {
      return nillable;
   }

   public void setNillable(boolean nillable)
   {
      this.nillable = nillable;
   }
   
   public String toString()
   {
      StringBuffer buffer = new StringBuffer("ElementBindig: " + getQName());
      return buffer.toString();
   }
}
