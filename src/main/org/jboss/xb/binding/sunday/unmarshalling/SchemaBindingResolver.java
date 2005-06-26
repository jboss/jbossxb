/*
 * JBoss, the OpenSource webOS
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
    * @param nsUri  namespace URI
    * @param localName  the local name of the root element
    * @return  an instance of SchemaBinding correspnding to the namespace URI or null
    * if the namespace URI is not recognized (though, in this case it could also throw an exception)
    */
   SchemaBinding resolve(String nsUri, String localName);
}
