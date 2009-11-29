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
import org.jboss.xb.binding.sunday.marshalling.TermBeforeMarshallingCallback;
import org.jboss.xb.binding.sunday.unmarshalling.SundayContentHandler.Position;
import org.jboss.xb.binding.sunday.xop.XOPUnmarshaller;
import org.xml.sax.Attributes;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class ElementBinding
   extends TermBinding
{
   protected List<ElementInterceptor> interceptors = Collections.emptyList();

   protected TypeBinding typeBinding;
   protected boolean nillable;
   protected Boolean normalizeSpace;

   protected XOPUnmarshaller xopUnmarshaller;

   public ElementBinding(SchemaBinding schema, QName qName, TypeBinding typeBinding)
   {
      super(schema);
      this.typeBinding = typeBinding;
      setQName(qName);
   }

   protected ElementBinding()
   {
   }

   public void setQName(QName qName)
   {
      if(qName == null)
         throw new JBossXBRuntimeException("Each element must have a non-null QName!");
      this.qName = qName;
   }
   public List<ElementInterceptor> getInterceptors()
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
            interceptors = new ArrayList<ElementInterceptor>(interceptors);
         default:
         {
            if( interceptors.contains(interceptor) == false )
               interceptors.add(interceptor);
         }

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

   public TermBeforeMarshallingCallback getBeforeMarshallingCallback()
   {
      return beforeMarshallingCallback == null ? typeBinding.getBeforeMarshallingCallback() : beforeMarshallingCallback;
   }

   public TermBeforeSetParentCallback getBeforeSetParentCallback()
   {
      return beforeSetParentCallback == null ? typeBinding.getBeforeSetParentCallback() : beforeSetParentCallback;
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

   public boolean isElement()
   {
      return true;
   }

   public XOPUnmarshaller getXopUnmarshaller()
   {
      return xopUnmarshaller == null ? typeBinding.getXopUnmarshaller() : xopUnmarshaller;
   }

   public void setXopUnmarshaller(XOPUnmarshaller xopUnmarshaller)
   {
      this.xopUnmarshaller = xopUnmarshaller;
   }

   public void setNormalizeSpace(Boolean value)
   {
      this.normalizeSpace = value;
   }
   
   public boolean isNormalizeSpace()
   {
      if(normalizeSpace != null)
         return normalizeSpace.booleanValue();      
      return schema == null ? true : schema.isNormalizeSpace();
   }
   
   public String toString()
   {
      return super.toString() + "(" + qName + ", type=" + typeBinding.getQName() + ")";
   }

   public Position newPosition(QName name, Attributes attrs, ParticleBinding particle)
   {
      return null;
   }
}
