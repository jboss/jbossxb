/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.util.loading;

import java.net.URL;

/** An interface representing class loader like senamics used in the aop
 * layer. Its only purpose was to remove the explicit dependency on the
 * JBoss UCL class loader api, but its existence seems to be a hack that
 * should be removed.
 * 
 * @version $Revision$
 */ 
public interface Translatable
{
   public URL getResourceLocally(String name);
}
