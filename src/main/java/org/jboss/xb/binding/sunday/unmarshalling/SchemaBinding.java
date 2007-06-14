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

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;

import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.sunday.xop.XOPUnmarshaller;
import org.jboss.xb.binding.sunday.xop.XOPMarshaller;
import org.jboss.xb.binding.metadata.PackageMetaData;
import org.jboss.xb.util.DomCharactersHandler;
import org.jboss.xb.util.DomLocalMarshaller;
import org.jboss.xb.util.DomParticleHandler;

/**
 * A SchemaBinding is a collection of binding objects (TypeBinding,
 * ChoiceBinding, ElementBinding, ModelGroupBinding, SequenceBinding, WildcardBinding)
 * for a single namespace keyed by the QNames of the schema components.
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class SchemaBinding
{
   private static final ValueAdapter DATE_ADAPTER = new ValueAdapter()
   {
      public Object cast(Object o, Class c)
      {
         if (o != null && java.util.Date.class.isAssignableFrom(c))
         {
            o = ((java.util.Calendar) o).getTime();
         }
         return o;
      }
   };
   
   /** The namespaces Set<String> */
   private Set namespaces = Collections.EMPTY_SET;
   /** Map<QName, TypeBinding> for simple/complex types */
   private Map types = new HashMap();
   /** Map<QName, ParticleBinding> for */
   private Map elements = new HashMap();
   /** Map<QName, ModelGroupBinding> for */
   private Map groups = new HashMap();
   /** The default package information */
   private PackageMetaData packageMetaData;
   /** Schema resolver to use for foreign namespaces */
   private SchemaBindingResolver schemaResolver;
   /** Must all content have a valid binding */
   private boolean strictSchema = true;
   /** Should child elements be ignored if they don't map to a parent field */
   private boolean ignoreUnresolvedFieldOrClass = true;
   /** Should '_' be considered as a word separator or part of Java identifier */
   private boolean ignoreLowLine = true;
   /** Should ${x} references be replaced with x system property */
   private boolean replacePropertyRefs = true;
   /** Should list xml types be unmarshalled as arrays */
   private boolean unmarshalListsToArrays;
   /** Should the default no-arg ctor be used to create the java instance */
   private boolean useNoArgCtorIfFound;
   /** The default property name to use for simple content bindings */
   private String simpleContentProperty = "value";
   
   /** if all the characters in the mixed content are whitespaces
    *  should they be considered indentation and ignored?
    *  the default is true for the backwards compatibility */
   private boolean ignoreWhitespacesInMixedContent = true;

   /** default XOP unmarshaller */
   private XOPUnmarshaller xopUnmarshaller;
   /** default XOP marshaller */
   private XOPMarshaller xopMarshaller;

   public SchemaBinding()
   {
      addType(new SimpleTypeBinding(Constants.QNAME_ANYSIMPLETYPE));
      addType(new SimpleTypeBinding(Constants.QNAME_STRING));
      addType(new SimpleTypeBinding(Constants.QNAME_BOOLEAN));
      addType(new SimpleTypeBinding(Constants.QNAME_DECIMAL));
      addType(new SimpleTypeBinding(Constants.QNAME_FLOAT));
      addType(new SimpleTypeBinding(Constants.QNAME_DOUBLE));
      addType(new SimpleTypeBinding(Constants.QNAME_DURATION));
      addType(new SimpleTypeBinding(Constants.QNAME_DATETIME, DATE_ADAPTER));
      addType(new SimpleTypeBinding(Constants.QNAME_TIME, DATE_ADAPTER));
      addType(new SimpleTypeBinding(Constants.QNAME_DATE, DATE_ADAPTER));
      addType(new SimpleTypeBinding(Constants.QNAME_GYEARMONTH));
      addType(new SimpleTypeBinding(Constants.QNAME_GYEAR));
      addType(new SimpleTypeBinding(Constants.QNAME_GMONTHDAY));
      addType(new SimpleTypeBinding(Constants.QNAME_GDAY));
      addType(new SimpleTypeBinding(Constants.QNAME_GMONTH));
      addType(new SimpleTypeBinding(Constants.QNAME_HEXBINARY));
      addType(new SimpleTypeBinding(Constants.QNAME_BASE64BINARY));
      addType(new SimpleTypeBinding(Constants.QNAME_ANYURI));
      addType(new SimpleTypeBinding(Constants.QNAME_QNAME));
      addType(new SimpleTypeBinding(Constants.QNAME_NOTATION));
      addType(new SimpleTypeBinding(Constants.QNAME_NORMALIZEDSTRING));
      addType(new SimpleTypeBinding(Constants.QNAME_TOKEN));
      addType(new SimpleTypeBinding(Constants.QNAME_LANGUAGE));
      addType(new SimpleTypeBinding(Constants.QNAME_NMTOKEN));
      addType(new SimpleTypeBinding(Constants.QNAME_NMTOKENS));
      addType(new SimpleTypeBinding(Constants.QNAME_NAME));
      addType(new SimpleTypeBinding(Constants.QNAME_NCNAME));
      addType(new SimpleTypeBinding(Constants.QNAME_ID));
      addType(new SimpleTypeBinding(Constants.QNAME_IDREF));
      addType(new SimpleTypeBinding(Constants.QNAME_IDREFS));
      addType(new SimpleTypeBinding(Constants.QNAME_ENTITY));
      addType(new SimpleTypeBinding(Constants.QNAME_ENTITIES));
      addType(new SimpleTypeBinding(Constants.QNAME_INTEGER));
      addType(new SimpleTypeBinding(Constants.QNAME_NONPOSITIVEINTEGER));
      addType(new SimpleTypeBinding(Constants.QNAME_NEGATIVEINTEGER));
      addType(new SimpleTypeBinding(Constants.QNAME_LONG));
      addType(new SimpleTypeBinding(Constants.QNAME_INT));
      addType(new SimpleTypeBinding(Constants.QNAME_SHORT));
      addType(new SimpleTypeBinding(Constants.QNAME_BYTE));
      addType(new SimpleTypeBinding(Constants.QNAME_NONNEGATIVEINTEGER));
      addType(new SimpleTypeBinding(Constants.QNAME_UNSIGNEDLONG));
      addType(new SimpleTypeBinding(Constants.QNAME_UNSIGNEDINT));
      addType(new SimpleTypeBinding(Constants.QNAME_UNSIGNEDSHORT));
      addType(new SimpleTypeBinding(Constants.QNAME_UNSIGNEDBYTE));
      addType(new SimpleTypeBinding(Constants.QNAME_POSITIVEINTEGER));
   }
   
   /**
    * Get the namespaces.
    * 
    * @return the namespaces.
    */
   public Set getNamespaces()
   {
      return namespaces;
   }

   /**
    * Set the namespaces.
    * 
    * @param namespaces the namespaces.
    * @throws IllegalArgumentException for null spaces
    */
   public void setNamespaces(Set namespaces)
   {
      if (namespaces == null)
         throw new IllegalArgumentException("Null namespaces");
      this.namespaces = namespaces;
   }

   public TypeBinding getType(QName qName)
   {
      return (TypeBinding)types.get(qName);
   }

   public void addType(TypeBinding type)
   {
      QName qName = type.getQName();
      if(qName == null)
      {
         throw new JBossXBRuntimeException("Global type must have a name.");
      }
      types.put(qName, type);
   }

   public ElementBinding getElement(QName name)
   {
      ParticleBinding particle = (ParticleBinding)elements.get(name);
      ElementBinding element = (ElementBinding)(particle == null ? null : particle.getTerm());
      return element;
   }

   public ParticleBinding getElementParticle(QName name)
   {
      return (ParticleBinding)elements.get(name);
   }

   public void addElement(ElementBinding element)
   {
      ParticleBinding particle = new ParticleBinding(element);
      elements.put(element.getQName(), particle);
   }

   public ElementBinding addElement(QName name, TypeBinding type)
   {
      ElementBinding element = new ElementBinding(this, name, type);
      addElement(element);
      return element;
   }

   public Iterator getElements()
   {
      return new Iterator()
      {
         private Iterator particleIterator = elements.values().iterator();

         public boolean hasNext()
         {
            return particleIterator.hasNext();
         }

         public Object next()
         {
            ParticleBinding particle = (ParticleBinding)particleIterator.next();
            return particle.getTerm();
         }

         public void remove()
         {
            throw new UnsupportedOperationException("remove is not implemented.");
         }
      };
   }

   public Iterator getElementParticles()
   {
      return elements.values().iterator();
   }

   public Iterator getTypes()
   {
      return Collections.unmodifiableCollection(types.values()).iterator();
   }

   public ModelGroupBinding getGroup(QName name)
   {
      return (ModelGroupBinding) groups.get(name);
   }

   public void addGroup(QName name, ModelGroupBinding group)
   {
      groups.put(name, group);
   }

   public Iterator getGroups()
   {
      return groups.values().iterator();
   }

   public PackageMetaData getPackageMetaData()
   {
      return packageMetaData;
   }

   public void setPackageMetaData(PackageMetaData packageMetaData)
   {
      this.packageMetaData = packageMetaData;
   }

   public SchemaBindingResolver getSchemaResolver()
   {
      return schemaResolver;
   }

   public void setSchemaResolver(SchemaBindingResolver schemaResolver)
   {
      this.schemaResolver = schemaResolver;
   }

   public boolean isStrictSchema()
   {
      return strictSchema;
   }

   /**
    * If strict-schema is true then all the elements and attributes in XML content being parsed must be bound
    * in this instance of SchemaBinding (except attributes from xmlns and xsi namespaces),
    * otherwise a runtime exception is thrown. The default value for this property is true.
    */
   public void setStrictSchema(boolean strictSchema)
   {
      this.strictSchema = strictSchema;
   }

   public boolean isIgnoreUnresolvedFieldOrClass()
   {
      return ignoreUnresolvedFieldOrClass;
   }

   /**
    * If a field is not found in the parent class to set child value on or
    * a class an element is bound to
    * an exception will be thrown if this property is false. Otherwise,
    * the process will just go on (the default for now).
    */
   public void setIgnoreUnresolvedFieldOrClass(boolean ignoreUnresolvedFieldOrClass)
   {
      this.ignoreUnresolvedFieldOrClass = ignoreUnresolvedFieldOrClass;
   }

   public boolean isReplacePropertyRefs()
   {
      return replacePropertyRefs;
   }
   /**
    *
    * @param flag
    */
   public void setReplacePropertyRefs(boolean flag)
   {
      this.replacePropertyRefs = flag;
   }

   public boolean isIgnoreLowLine()
   {
      return ignoreLowLine;
   }

   /**
    * Where '_' should be considered as a word separator or a part of the Java identifier
    * when mapping XML names to Java identifiers.
    */
   public void setIgnoreLowLine(boolean ignoreLowLine)
   {
      this.ignoreLowLine = ignoreLowLine;
   }

   public boolean isUnmarshalListsToArrays()
   {
      return unmarshalListsToArrays;
   }

   /**
    * Should list xml types be unmarshalled as arrays
    * @param unmarshalListsToArrays
    */
   public void setUnmarshalListsToArrays(boolean unmarshalListsToArrays)
   {
      this.unmarshalListsToArrays = unmarshalListsToArrays;
   }

   public boolean isUseNoArgCtorIfFound()
   {
      return useNoArgCtorIfFound;
   }

   /**
    * Should the default no-arg ctor be used to create the java instance
    * @param useNoArgCtorIfFound
    */
   public void setUseNoArgCtorIfFound(boolean useNoArgCtorIfFound)
   {
      this.useNoArgCtorIfFound = useNoArgCtorIfFound;
   }

   public String getSimpleContentProperty()
   {
      return simpleContentProperty;
   }

   /**
    * Set the default property name to use for simple content bindings
    * @param simpleContentProperty
    */
   public void setSimpleContentProperty(String simpleContentProperty)
   {
      this.simpleContentProperty = simpleContentProperty;
   }

   /**
    * @return  schema default XOP unmarshaller
    */
   public XOPUnmarshaller getXopUnmarshaller()
   {
      return xopUnmarshaller;
   }

   /**
    * @param xopUnmarshaller  schema default XOP unmarshaller
    */
   public void setXopUnmarshaller(XOPUnmarshaller xopUnmarshaller)
   {
      this.xopUnmarshaller = xopUnmarshaller;
   }

   /**
    * @return schema default XOP marshaller
    */
   public XOPMarshaller getXopMarshaller()
   {
      return xopMarshaller;
   }

   /**
    * @param xopMarshaller  schema default XOP marshaller
    */
   public void setXopMarshaller(XOPMarshaller xopMarshaller)
   {
      this.xopMarshaller = xopMarshaller;
   }

   public void setUnresolvedContentBoundToDOM(boolean toDom)
   {
      TypeBinding type = getType(Constants.QNAME_ANYTYPE);
      if(type == null)
      {
         // ignore, there is no use of the anyType in the schema
         return;
         //throw new JBossXBRuntimeException("anyType is not bound.");
      }

      WildcardBinding wildcard = type.getWildcard();
      if(toDom)
      {
         wildcard.setUnresolvedCharactersHandler(DomCharactersHandler.INSTANCE);
         wildcard.setUnresolvedElementHandler(DomParticleHandler.INSTANCE);
         wildcard.setUnresolvedMarshaller(DomLocalMarshaller.INSTANCE);
      }
      else
      {
         wildcard.setUnresolvedCharactersHandler(null);
         wildcard.setUnresolvedElementHandler(null);
         wildcard.setUnresolvedMarshaller(null);
      }
   }
   
   public boolean isUnresolvedContentBoundToDOM()
   {
      TypeBinding type = getType(Constants.QNAME_ANYTYPE);
      if(type == null)
      {
         // there is no use of the anyType in the schema
         return false;
         //throw new JBossXBRuntimeException("anyType is not bound.");
      }

      WildcardBinding wildcard = type.getWildcard();
      return wildcard.getUnresolvedCharactersHandler() instanceof DomCharactersHandler
      && wildcard.getUnresolvedElementHandler() instanceof DomParticleHandler
      && wildcard.getUnresolvedMarshaller() instanceof DomLocalMarshaller;
   }
   
   public boolean isIgnoreWhitespacesInMixedContent()
   {
      return ignoreWhitespacesInMixedContent;
   }
   
   public void setIgnoreWhitespacesInMixedContent(boolean value)
   {
      this.ignoreWhitespacesInMixedContent = value;
   }
   
   void addElementParticle(ParticleBinding particle)
   {
      ElementBinding element = (ElementBinding)particle.getTerm();
      elements.put(element.getQName(), particle);
   }
}
