/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding.sunday.unmarshalling;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface SchemaBindingResolver
{
   /**
    * Returns an instance of SchemaBinding corresponding to the namespace URI.
    *
    * @param nsUri - namespace URI of the element with the schema reference
    * @param localName - the local name of the element
    * @param baseURI - an optional baseURI for resolving the schemaLocation.
    * @param schemaLocation - the option schema location uri that matches
    *    nsUri if one exists
    * @return an instance of SchemaBinding correspnding to the namespace URI
    * or null if the namespace URI is not recognized (though, in this case it
    * could also throw an exception)
    */
   SchemaBinding resolve(String nsUri, String localName,
      String baseURI, String schemaLocation);
}
