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
import org.jboss.xb.binding.metadata.PutMethodMetaData;
import org.jboss.xb.binding.metadata.ValueMetaData;
import org.jboss.xb.binding.JBossXBRuntimeException;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class ElementBinding
   extends TermBinding
{
   private List interceptors = Collections.EMPTY_LIST;

   private final QName qName;
   private final TypeBinding typeBinding;
   private boolean nillable;

   public ElementBinding(SchemaBinding schema, QName qName, TypeBinding typeBinding)
   {
      super(schema);
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

   public MapEntryMetaData getMapEntryMetaData()
   {
      MapEntryMetaData result = mapEntryMetaData;
      if(result == null && classMetaData == null)
      {
         result = typeBinding.getMapEntryMetaData();
      }
      return result;
   }

   public ValueMetaData getValueMetaData()
   {
      return valueMetaData != null ? valueMetaData : typeBinding.getValueMetaData();
   }

   public PutMethodMetaData getPutMethodMetaData()
   {
      // todo should types be allowed to have putMethod metadata
      return putMethodMetaData;
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

   public boolean isSkip()
   {
      return skip == null ? typeBinding.isSkip() : skip.booleanValue();
   }

   public ValueAdapter getValueAdapter()
   {
      return valueAdapter == null ? typeBinding.getValueAdapter() : valueAdapter;
   }

   public boolean isNillable()
   {
      return nillable;
   }

   public void setNillable(boolean nillable)
   {
      this.nillable = nillable;
   }

   public boolean isModelGroup()
   {
      return false;
   }

   public boolean isWildcard()
   {
      return false;
   }

   public String toString()
   {
      return super.toString() + "[" + qName + "]";
   }
}
