/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding.sunday.unmarshalling;

// $Id$

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.jboss.xb.binding.Constants;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.metadata.PackageMetaData;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @author Thomas.Diesler@jboss.org
 * @version <tt>$Revision$</tt>
 */
public class SchemaBinding
{
   private static final Map SIMPLE_TYPES = new HashMap();

   // populate SIMPLE_TYPES
   {
      ValueAdapter dateAdapter = new ValueAdapter()
      {
         public Object cast(Object o, Class c)
         {
            if(o != null && java.util.Date.class.isAssignableFrom(c))
            {
               o = ((java.util.Calendar)o).getTime();
            }
            return o;
         }
      };

      addSimpleType(new SimpleTypeBinding(Constants.QNAME_ANYSIMPLETYPE));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_STRING));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_BOOLEAN));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_DECIMAL));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_FLOAT));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_DOUBLE));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_DURATION));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_DATETIME, dateAdapter));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_TIME, dateAdapter));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_DATE, dateAdapter));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_GYEARMONTH));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_GYEAR));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_GMONTHDAY));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_GDAY));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_GMONTH));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_HEXBINARY));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_BASE64BINARY));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_ANYURI));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_QNAME));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_NOTATION));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_NORMALIZEDSTRING));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_TOKEN));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_LANGUAGE));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_NMTOKEN));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_NMTOKENS));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_NAME));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_NCNAME));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_ID));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_IDREF));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_IDREFS));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_ENTITY));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_ENTITIES));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_INTEGER));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_NONPOSITIVEINTEGER));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_NEGATIVEINTEGER));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_LONG));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_INT));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_SHORT));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_BYTE));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_NONNEGATIVEINTEGER));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_UNSIGNEDLONG));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_UNSIGNEDINT));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_UNSIGNEDSHORT));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_UNSIGNEDBYTE));
      addSimpleType(new SimpleTypeBinding(Constants.QNAME_POSITIVEINTEGER));
   }

   private void addSimpleType(TypeBinding type)
   {
      SIMPLE_TYPES.put(type.getQName(), type);
   }

   private Map types = new LinkedHashMap(SIMPLE_TYPES);
   private Map elements = new LinkedHashMap();
   private PackageMetaData packageMetaData;
   private SchemaBindingResolver schemaResolver;
   private boolean strictSchema = true;
   private boolean ignoreUnresolvedFieldOrClass = true;
   private boolean ignoreLowLine = true;
   private boolean replacePropertyRefs = true;

   public TypeBinding getType(QName qName)
   {
      return (TypeBinding)types.get(qName);
   }

   public QName[] getTypeQNames()
   {
      QName[] qnArr = new QName[types.size()];
      types.keySet().toArray(qnArr);
      return qnArr;
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
      return (ElementBinding)elements.get(name);
   }

   public QName[] getElementQNames()
   {
      QName[] qnArr = new QName[elements.size()];
      elements.keySet().toArray(qnArr);
      return qnArr;
   }

   public void addElement(ElementBinding element)
   {
      elements.put(element.getQName(), element);
   }

   public ElementBinding addElement(QName name, TypeBinding type)
   {
      ElementBinding element = new ElementBinding(this, name, type);
      addElement(element);
      return element;
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
   
   public String toString()
   {
      StringBuffer buffer = new StringBuffer("SchemaBindig:");

      QName[] typeQNames = getTypeQNames();
      for (int i = 0; i < typeQNames.length; i++)
      {
         TypeBinding type = getType(typeQNames[i]);
         buffer.append("\n " + type);
      }

      QName[] elQNames = getElementQNames();
      for (int i = 0; i < elQNames.length; i++)
      {
         ElementBinding element = getElement(elQNames[i]);
         buffer.append("\n " + element);
      }

      return buffer.toString();
   }
}
