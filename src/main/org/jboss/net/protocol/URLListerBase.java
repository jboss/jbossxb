/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/


package org.jboss.net.protocol;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Collection;
import java.util.StringTokenizer;
import java.net.URL;
import java.io.IOException;

/**
 * Support class for URLLister's providing protocol independent functionality.
 */
public abstract class URLListerBase implements URLLister {
   public Collection listMembers(URL baseUrl, String patterns) throws IOException {
      StringTokenizer tokens = new StringTokenizer(patterns, ",");
      String[] members = new String[tokens.countTokens()];
      for (int i=0; tokens.hasMoreTokens(); i++) {
         members[i] = tokens.nextToken();
      }
      return listMembers(baseUrl, members);
   }

   /**
    * Inner class representing Filter criteria to be applied to the members
    * of the returned Collection
    */
   public static class URLFilter {
      protected boolean allowAll;
      protected HashSet constants;

      public URLFilter(String[] patterns) {
         constants = new HashSet(Arrays.asList(patterns));
         allowAll = constants.contains("*");
      }

      public boolean accept(String name) {
         if (allowAll) {
            return true;
         }
         if (constants.contains(name)) {
            return true;
         }
         return false;
      }
   }

   protected static final URLFilter acceptAllFilter = new URLFilter(new String[] {"*"});
}
