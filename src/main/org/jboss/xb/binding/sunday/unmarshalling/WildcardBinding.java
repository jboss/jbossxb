/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding.sunday.unmarshalling;

import javax.xml.namespace.QName;
import org.xml.sax.Attributes;
import org.jboss.xb.binding.Util;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class WildcardBinding
   extends TermBinding
{
   private SchemaBindingResolver schemaResolver;

   public WildcardBinding(SchemaBinding schema)
   {
      super(schema);
   }

   public SchemaBindingResolver getSchemaResolver()
   {
      return schemaResolver;
   }

   public void setSchemaResolver(SchemaBindingResolver schemaResolver)
   {
      this.schemaResolver = schemaResolver;
   }

   public ElementBinding getElement(QName qName, Attributes attrs)
   {
      SchemaBindingResolver resolver = schemaResolver;
      if(resolver == null && schema != null)
      {
         resolver = schema.getSchemaResolver();
      }

      ElementBinding element = null;
      if(resolver != null)
      {
         // this is wildcard handling
         String schemaLocation = attrs == null ? null : Util.getSchemaLocation(attrs, qName.getNamespaceURI());
         SchemaBinding schema = resolver.resolve(qName.getNamespaceURI(), null, schemaLocation);
         if(schema != null)
         {
            element = schema.getElement(qName);
         }
      }

      return element;
   }

   public boolean isSkip()
   {
      return skip == null ? false : skip.booleanValue();
   }

   public boolean isModelGroup()
   {
      return false;
   }
}
