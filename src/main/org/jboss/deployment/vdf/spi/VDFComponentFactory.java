/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/
package org.jboss.deployment.vdf.spi;

import java.net.URI;

/**
 * Factory for Virtual Deployment Framework components
 * 
 * @author <a href="mailto:dimitris@jboss.org">Dimitris Andreadis</a>
 * @version $Revision$
 */
public interface VDFComponentFactory
{
   /** Create a VDFComponent for the provided URI
    */
   public VDFComponent createVDFComponent(URI uri) throws VDFException;
}
