/*
* JBoss, Home of Professional Open Source
* Copyright 2010, JBoss Inc., and individual contributors as indicated
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
package org.jboss.xb.util;

import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBindingInitializer;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 1958 $</tt>
 */
public class SchemaBindingPropertyInitializer implements SchemaBindingInitializer
{
   /** Must all content have a valid binding */
   private Boolean strictSchema;

   /** Should ${x} references be replaced with x system property */
   private Boolean replacePropertyRefs;
   
   /** if all the characters in the mixed content are whitespaces
    *  should they be considered indentation and ignored?
    *  the default is true for the backwards compatibility */
   private Boolean ignoreWhitespacesInMixedContent;

   /** whether to trim string values */
   private Boolean normalizeSpace;

   public Boolean getStrictSchema()
   {
      return strictSchema;
   }

   public void setStrictSchema(Boolean strictSchema)
   {
      this.strictSchema = strictSchema;
   }

   public Boolean isIgnoreWhitespacesInMixedContent()
   {
      return ignoreWhitespacesInMixedContent;
   }

   public void setIgnoreWhitespacesInMixedContent(Boolean ignoreWhitespacesInMixedContent)
   {
      this.ignoreWhitespacesInMixedContent = ignoreWhitespacesInMixedContent;
   }

   public Boolean isNormalizeSpace()
   {
      return normalizeSpace;
   }

   public void setNormalizeSpace(Boolean normalizeSpace)
   {
      this.normalizeSpace = normalizeSpace;
   }

   public Boolean isReplacePropertyRefs()
   {
      return replacePropertyRefs;
   }
   
   public void setReplacePropertyRefs(Boolean replacePropertyRefs)
   {
      this.replacePropertyRefs = replacePropertyRefs;
   }

   
   public SchemaBinding init(SchemaBinding schema)
   {
      if(strictSchema != null)
         schema.setStrictSchema(strictSchema);
      if(replacePropertyRefs != null)
         schema.setReplacePropertyRefs(replacePropertyRefs);
      if(ignoreWhitespacesInMixedContent != null)
         schema.setIgnoreWhitespacesInMixedContent(ignoreWhitespacesInMixedContent);
      if(normalizeSpace != null)
         schema.setNormalizeSpace(normalizeSpace);
      return schema;
   }
}
