/*
 * JBoss, the OpenSource webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata;

import javax.xml.namespace.QName;
import org.jboss.xml.binding.Constants;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class XsdAppInfo
   extends XsdElement
{
   static final QName QNAME = new QName(Constants.NS_XML_SCHEMA, "appinfo");

   private SchemaMetaData schemaMetaData;
   private ClassMetaData classMetaData;
   private PropertyMetaData propertyMetaData;
   private ValueMetaData valueMetaData;

   public XsdAppInfo()
   {
      super(QNAME);
   }

   public SchemaMetaData getSchemaMetaData()
   {
      return schemaMetaData;
   }

   public void setSchemaMetaData(SchemaMetaData schema)
   {
      this.schemaMetaData = schema;
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

   public ValueMetaData getValueMetaData()
   {
      return valueMetaData;
   }

   public void setValueMetaData(ValueMetaData valueMetaData)
   {
      this.valueMetaData = valueMetaData;
   }
}
