/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/


package org.jboss.net.protocol.http;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.httpclient.HttpException;
import org.apache.util.HttpURL;
import org.apache.webdav.lib.WebdavResource;
import org.jboss.net.protocol.URLListerBase;

public class DavURLLister extends URLListerBase {
   public Collection listMembers(URL baseUrl) throws IOException {
      return listMembers(baseUrl,  acceptAllFilter);
   }

   public Collection listMembers(URL baseUrl, String[] members) throws IOException {
      return listMembers(baseUrl,  new URLFilter(members));
   }

   private Collection listMembers(URL baseUrl, URLFilter filter) throws IOException {
      try {
         WebdavResource resource = new WebdavResource(baseUrl.toString());
         WebdavResource[] resources = resource.listWebdavResources();
         List urls = new ArrayList(resources.length);
         for (int i = 0; i < resources.length; i++) {
            WebdavResource member = resources[i];
            HttpURL httpURL = member.getHttpURL();
            if (filter.accept(httpURL.getName())) {
               String url = httpURL.getUnescapedHttpURL();
               if (member.isCollection() && url.endsWith("/") == false) {
                  url += "/";
               }
               urls.add(new URL(url));
            }
         }
         return urls;
      } catch (HttpException e) {
         throw new IOException(e.getMessage());
      } catch (MalformedURLException e) {
         // should not happen
         throw new IllegalStateException(e.getMessage());
      }
   }
}
