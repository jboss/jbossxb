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

import org.w3c.dom.ls.LSInput;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface SchemaBindingResolver
{
   String getBaseURI();

   void setBaseURI(String baseURI);

   /**
    * Returns an instance of SchemaBinding corresponding to the namespace URI.
    *
    * @param nsUri - namespace URI of the element with the schema reference
    * @param baseURI - an optional baseURI for resolving the schemaLocation.
    * @param schemaLocation - the option schema location uri that matches
    *    nsUri if one exists
    * @return an instance of SchemaBinding correspnding to the namespace URI
    * or null if the namespace URI is not recognized (though, in this case it
    * could also throw an exception)
    */
   SchemaBinding resolve(String nsUri, String baseURI, String schemaLocation);

   /**
    * This one is used to resolve imported schemas with <xsd:import>
    * @param nsUri
    * @param baseUri
    * @param schemaLocation
    * @return LIInput for the resolved namespace schema if found, null otherwise
    */
   LSInput resolveAsLSInput(String nsUri, String baseUri, String schemaLocation);
}
