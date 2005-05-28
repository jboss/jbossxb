/*
 * JBoss, the OpenSource webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata;


/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class CharactersMetaData
{
   private PropertyMetaData property;
   private ValueMetaData value;

   public PropertyMetaData getProperty()
   {
      return property;
   }

   public void setProperty(PropertyMetaData property)
   {
      this.property = property;
   }

   public ValueMetaData getValue()
   {
      return value;
   }

   public void setValue(ValueMetaData value)
   {
      this.value = value;
   }

   public boolean equals(Object o)
   {
      if(this == o)
      {
         return true;
      }
      if(!(o instanceof CharactersMetaData))
      {
         return false;
      }

      final CharactersMetaData charactersMetaData = (CharactersMetaData)o;

      if(property != null ? !property.equals(charactersMetaData.property) : charactersMetaData.property != null)
      {
         return false;
      }
      if(value != null ? !value.equals(charactersMetaData.value) : charactersMetaData.value != null)
      {
         return false;
      }

      return true;
   }

   public int hashCode()
   {
      int result;
      result = (property != null ? property.hashCode() : 0);
      result = 29 * result + (value != null ? value.hashCode() : 0);
      return result;
   }
}
