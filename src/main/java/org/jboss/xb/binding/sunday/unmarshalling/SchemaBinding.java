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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.JBossXBValueFormatException;
import org.jboss.xb.binding.SimpleTypeBindings;
import org.jboss.xb.binding.sunday.xop.XOPUnmarshaller;
import org.jboss.xb.binding.sunday.xop.XOPMarshaller;
import org.jboss.xb.binding.metadata.PackageMetaData;
import org.jboss.xb.binding.metadata.ValueMetaData;
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
      public Object cast(Object o, Class<?> c)
      {
         if (c != null && o != null && java.util.Date.class.isAssignableFrom(c))
         {
            o = ((java.util.Calendar) o).getTime();
         }
         return o;
      }
   };
   
   /** The namespaces Set<String> */
   private Set<String> namespaces = Collections.emptySet();
   /** namespace to prefix map, used in xb builder during binding */
   private Map<String, String> nsByPrefix = Collections.emptyMap();
   /** Map<QName, TypeBinding> for simple/complex types */
   private Map<QName, TypeBinding> types = new HashMap<QName, TypeBinding>();
   /** Map<QName, ParticleBinding> for */
   private Map<QName, ParticleBinding> elements = new HashMap<QName, ParticleBinding>();
   /** Map<QName, ModelGroupBinding> for */
   private Map<QName, ModelGroupBinding> groups = new HashMap<QName, ModelGroupBinding>();
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

   /** whether to trim string values */
   private boolean normalizeSpace;
   
   /** default XOP unmarshaller */
   private XOPUnmarshaller xopUnmarshaller;
   /** default XOP marshaller */
   private XOPMarshaller xopMarshaller;

   public SchemaBinding()
   {
      addType(new SimpleTypeBinding(Constants.QNAME_ANYSIMPLETYPE, CharactersHandler.NOOP_UNMARSHAL_HANDLER));
      addType(new SimpleTypeBinding(Constants.QNAME_STRING, CharactersHandler.NOOP_UNMARSHAL_HANDLER));
      addType(new SimpleTypeBinding(Constants.QNAME_BOOLEAN, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            if(value.length() == 1)
            {
               char c = value.charAt(0);
               if(c == '1')
                  return Boolean.TRUE;
               if(c == '0')
                  return Boolean.FALSE;
               throw new JBossXBValueFormatException("An instance of a datatype that is defined as ?boolean? can have the following legal literals" +
                  " {true, false, 1, 0}. But got: " + value);
            }
            else
               return Boolean.valueOf(value);
         }
      }));
      
      addType(new SimpleTypeBinding(Constants.QNAME_DECIMAL, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            return new BigDecimal(value);
         }
      }));
      
      addType(new SimpleTypeBinding(Constants.QNAME_FLOAT, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            if("INF".equals(value))
               return new Float(Float.POSITIVE_INFINITY);
            else if("-INF".equals(value))
               return new Float(Float.NEGATIVE_INFINITY);
            else
               return Float.valueOf(value);
         }
      }));
      
      addType(new SimpleTypeBinding(Constants.QNAME_DOUBLE, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            if("INF".equals(value))
               return new Double(Double.POSITIVE_INFINITY);
            else if("-INF".equals(value))
               return new Double(Double.NEGATIVE_INFINITY);
            else
               return Double.valueOf(value);
         }
      }));
      
      addType(new SimpleTypeBinding(Constants.QNAME_DURATION, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {         
            // todo XS_DURATION
            throw new IllegalStateException("Recognized but not supported xsdType: " + Constants.QNAME_DURATION);
         }
      }));
      
      addType(new SimpleTypeBinding(Constants.QNAME_DATETIME, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            return SimpleTypeBindings.unmarshalDateTime(value);
         }
      }, DATE_ADAPTER));
      
      addType(new SimpleTypeBinding(Constants.QNAME_TIME, new CharactersHandler.UnmarshalCharactersHandler(){
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            return SimpleTypeBindings.unmarshalTime(value);
         }         
      }, DATE_ADAPTER));
      
      addType(new SimpleTypeBinding(Constants.QNAME_DATE, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            return SimpleTypeBindings.unmarshalDate(value);
         }         
      }, DATE_ADAPTER));
      
      addType(new SimpleTypeBinding(Constants.QNAME_GYEARMONTH, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            return SimpleTypeBindings.unmarshalGYearMonth(value);
         }
      }));
      
      addType(new SimpleTypeBinding(Constants.QNAME_GYEAR, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            return SimpleTypeBindings.unmarshalGYear(value);
         }
      }));
      
      addType(new SimpleTypeBinding(Constants.QNAME_GMONTHDAY, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            return SimpleTypeBindings.unmarshalGMonthDay(value);
         }
      }));
      
      addType(new SimpleTypeBinding(Constants.QNAME_GDAY, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            return SimpleTypeBindings.unmarshalGDay(value);
         }
      }));
      
      addType(new SimpleTypeBinding(Constants.QNAME_GMONTH, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            return SimpleTypeBindings.unmarshalGMonth(value);
         }
      }));
      
      addType(new SimpleTypeBinding(Constants.QNAME_HEXBINARY, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            return SimpleTypeBindings.unmarshalHexBinary(value);
         }
      }));
      
      addType(new SimpleTypeBinding(Constants.QNAME_BASE64BINARY, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            return SimpleTypeBindings.unmarshalBase64(value);
         }         
      }));
      
      addType(new SimpleTypeBinding(Constants.QNAME_ANYURI, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            // anyUri is by default bound to java.net.URI for now. The following is the warning from JAXB2.0:
            //
            // Design Note � xs:anyURI is not bound to java.net.URI by default since not all
            // possible values of xs:anyURI can be passed to the java.net.URI constructor. Using
            // a global JAXB customization described in Section 7.9, �<javaType>
            // Declaration", a JAXB user can override the default mapping to map xs:anyURI to
            // java.net.URI.
            //
            try
            {
               return new java.net.URI(value);
            }
            catch(URISyntaxException e)
            {
               throw new JBossXBValueFormatException("Failed to unmarshal anyURI value " + value, e);
            }
         }
      }));
      
      addType(new SimpleTypeBinding(Constants.QNAME_QNAME, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            return SimpleTypeBindings.unmarshalQName(value, nsCtx);
         }
      }));
      
      addType(new SimpleTypeBinding(Constants.QNAME_NOTATION, CharactersHandler.NOOP));
      addType(new SimpleTypeBinding(Constants.QNAME_NORMALIZEDSTRING, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            if(SimpleTypeBindings.isNormalizedString(value))
               return value;
            else
               throw new JBossXBValueFormatException("Invalid normalizedString value: " + value);
         }
      }));
      
      addType(new SimpleTypeBinding(Constants.QNAME_TOKEN, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            if(SimpleTypeBindings.isValidToken(value))
               return value;
            else
               throw new JBossXBValueFormatException("Invalid token value: " + value);
         }
      }));
      
      addType(new SimpleTypeBinding(Constants.QNAME_LANGUAGE, CharactersHandler.NOOP));
      addType(new SimpleTypeBinding(Constants.QNAME_NMTOKEN, CharactersHandler.NOOP));
      addType(new SimpleTypeBinding(Constants.QNAME_NMTOKENS, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            return SimpleTypeBindings.unmarshalNMTokens(value);
         }
      }));
      
      addType(new SimpleTypeBinding(Constants.QNAME_NAME, CharactersHandler.NOOP));
      addType(new SimpleTypeBinding(Constants.QNAME_NCNAME, CharactersHandler.NOOP));
      addType(new SimpleTypeBinding(Constants.QNAME_ID, CharactersHandler.NOOP));
      addType(new SimpleTypeBinding(Constants.QNAME_IDREF, CharactersHandler.NOOP));
      addType(new SimpleTypeBinding(Constants.QNAME_IDREFS, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            return SimpleTypeBindings.unmarshalIdRefs(value);
         }
      }));
      
      addType(new SimpleTypeBinding(Constants.QNAME_ENTITY, CharactersHandler.NOOP));
      addType(new SimpleTypeBinding(Constants.QNAME_ENTITIES, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            return SimpleTypeBindings.unmarshalIdRefs(value);
         }
      }));
      
      addType(new SimpleTypeBinding(Constants.QNAME_INTEGER, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            return new BigInteger(value);
         }
      }));
      
      addType(new SimpleTypeBinding(Constants.QNAME_NONPOSITIVEINTEGER, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            BigInteger result = new BigInteger(value);
            if(BigInteger.ZERO.compareTo(result) < 0)
               throw new JBossXBValueFormatException("Invalid nonPositiveInteger value: " + value);
            return result;
         }
      }));
      
      addType(new SimpleTypeBinding(Constants.QNAME_NEGATIVEINTEGER, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            BigInteger result = new BigInteger(value);
            if(BigInteger.ZERO.compareTo(result) <= 0)
               throw new JBossXBValueFormatException("Invalid negativeInteger value: " + value);
            return result;
         }
      }));
      
      addType(new SimpleTypeBinding(Constants.QNAME_LONG, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            return Long.valueOf(value);
         }
      }));
      
      addType(new SimpleTypeBinding(Constants.QNAME_INT, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            return Integer.valueOf(value);
         }
      }));
      
      addType(new SimpleTypeBinding(Constants.QNAME_SHORT, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            return Short.valueOf(value);
         }
      }));
      
      addType(new SimpleTypeBinding(Constants.QNAME_BYTE, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            return Byte.valueOf(value);
         }
      }));
      
      addType(new SimpleTypeBinding(Constants.QNAME_NONNEGATIVEINTEGER, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            BigInteger result = new BigInteger(value);
            if(BigInteger.ZERO.compareTo(result) > 0)
               throw new JBossXBValueFormatException("Invalid nonNegativeInteger value: " + value);
            return result;
         }
      }));
      
      addType(new SimpleTypeBinding(Constants.QNAME_UNSIGNEDLONG, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            BigInteger d = new BigInteger(value);
            if(d.doubleValue() < 0 || d.doubleValue() > 18446744073709551615D)
               throw new JBossXBValueFormatException("Invalid unsignedLong value: " + value);
            return d;
         }
      }));
      
      addType(new SimpleTypeBinding(Constants.QNAME_UNSIGNEDINT, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            long l = Long.parseLong(value);
            if(l < 0 || l > 4294967295L)
               throw new JBossXBValueFormatException("Invalid unsignedInt value: " + value);
            return new Long(l);
         }
      }));
      
      addType(new SimpleTypeBinding(Constants.QNAME_UNSIGNEDSHORT, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            int i = Integer.parseInt(value);
            if(i < 0 || i > 65535)
               throw new JBossXBValueFormatException("Invalid unsignedShort value: " + value);
            return new Integer(i);
         }
      }));
      
      addType(new SimpleTypeBinding(Constants.QNAME_UNSIGNEDBYTE, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            short s = Short.parseShort(value);
            if(s < 0 || s > 255)
               throw new JBossXBValueFormatException("Invalid unsignedByte value: " + value);
            return new Short(s);
         }
      }));
      
      addType(new SimpleTypeBinding(Constants.QNAME_POSITIVEINTEGER, new CharactersHandler.UnmarshalCharactersHandler()
      {
         public Object unmarshal(QName qName, TypeBinding typeBinding, NamespaceContext nsCtx,
               ValueMetaData valueMetaData, String value)
         {
            if (value == null)
               throw new IllegalArgumentException("Value string cannot be null");
            BigInteger result = new BigInteger(value);
            if(BigInteger.ZERO.compareTo((BigInteger)result) >= 0)
               throw new JBossXBValueFormatException("Invalid positiveInteger value: " + value);
            return result;
         }
      }));
   }
   
   public void addPrefixMapping(String prefix, String ns)
   {
      if(nsByPrefix.isEmpty())
      {
         nsByPrefix = Collections.singletonMap(prefix, ns);
      }
      else
      {
         if(nsByPrefix.size() == 1)
         {
            nsByPrefix = new HashMap<String, String>(nsByPrefix);
         }
         nsByPrefix.put(prefix, ns);
      }
   }
   
   public String getNamespace(String prefix)
   {
      return nsByPrefix.get(prefix);
   }
   
   /**
    * Get the namespaces.
    * 
    * @return the namespaces.
    */
   public Set<String> getNamespaces()
   {
      return namespaces;
   }

   /**
    * Set the namespaces.
    * 
    * @param namespaces the namespaces.
    * @throws IllegalArgumentException for null spaces
    */
   public void setNamespaces(Set<String> namespaces)
   {
      if (namespaces == null)
         throw new IllegalArgumentException("Null namespaces");
      this.namespaces = namespaces;
   }

   public TypeBinding getType(QName qName)
   {
      return types.get(qName);
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
      ParticleBinding particle = elements.get(name);
      ElementBinding element = (ElementBinding)(particle == null ? null : particle.getTerm());
      return element;
   }

   public ParticleBinding getElementParticle(QName name)
   {
      return elements.get(name);
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

   public Iterator<ElementBinding> getElements()
   {
      return new Iterator<ElementBinding>()
      {
         private Iterator<ParticleBinding> particleIterator = elements.values().iterator();

         public boolean hasNext()
         {
            return particleIterator.hasNext();
         }

         public ElementBinding next()
         {
            ParticleBinding particle = particleIterator.next();
            return (ElementBinding) particle.getTerm();
         }

         public void remove()
         {
            throw new UnsupportedOperationException("remove is not implemented.");
         }
      };
   }

   public Iterator<ParticleBinding> getElementParticles()
   {
      return elements.values().iterator();
   }

   public Iterator<TypeBinding> getTypes()
   {
      return Collections.unmodifiableCollection(types.values()).iterator();
   }

   public ModelGroupBinding getGroup(QName name)
   {
      return groups.get(name);
   }

   public void addGroup(QName name, ModelGroupBinding group)
   {
      groups.put(name, group);
   }

   public Iterator<ModelGroupBinding> getGroups()
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
   
   public boolean isNormalizeSpace()
   {
      return normalizeSpace;
   }
   
   public void setNormalizeSpace(boolean value)
   {
      this.normalizeSpace = value;
   }
   
   void addElementParticle(ParticleBinding particle)
   {
      ElementBinding element = (ElementBinding)particle.getTerm();
      elements.put(element.getQName(), particle);
   }
}
