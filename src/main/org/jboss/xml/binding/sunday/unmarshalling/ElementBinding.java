/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import org.jboss.xml.binding.metadata.PropertyMetaData;
import org.jboss.xml.binding.metadata.ValueMetaData;
import org.jboss.xml.binding.metadata.ClassMetaData;
import org.jboss.xml.binding.metadata.MapEntryMetaData;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class ElementBinding
{
   private List interceptors = Collections.EMPTY_LIST;

   private final TypeBinding typeBinding;
   private ClassMetaData classMetaData;
   private PropertyMetaData propertyMetaData;
   private MapEntryMetaData mapEntryMetaData;
   private ValueMetaData valueMetaData;
   private boolean multiOccurs;

   public ElementBinding(TypeBinding typeBinding)
   {
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
      return classMetaData;
   }

   public void setClassMetaData(ClassMetaData classMetaData)
   {
      this.classMetaData = classMetaData;
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

   public ValueMetaData getValueMetaData()
   {
      return valueMetaData;
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
}
