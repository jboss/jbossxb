/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.net.protocol;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

/**
 * Interface defining methods that can be used to list the contents of a URL
 * collection irrespective of the protocol.
 */
public interface URLLister {
   /**
    * List all the members of the given collection URL.
    * @param baseUrl the URL to list; must end in "/"
    * @return a Collection of URLs that are members of the baseURL
    * @throws IOException if there was a problem getting the list
    */
   Collection listMembers(URL baseUrl) throws IOException;

   /**
    * List the members of the given collection URL that match the patterns
    * supplied.
    * @param baseUrl the URL to list; must end in "/"
    * @param patterns the patterns to match
    * @return a Collection of URLs that match
    * @throws IOException if there was a problem getting the list
    */
   Collection listMembers(URL baseUrl, String[] patterns) throws IOException;

   /**
    * List the members of the given collection URL that match the patterns
    * supplied.
    * @param baseUrl the URL to list; must end in "/"
    * @param patterns the patterns to match (separated by ',')
    * @return a Collection of URLs that match
    * @throws IOException if there was a problem getting the list
    */
   Collection listMembers(URL baseUrl, String patterns) throws IOException;
}
