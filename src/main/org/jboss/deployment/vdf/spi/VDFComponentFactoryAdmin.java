/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/
package org.jboss.deployment.vdf.spi;

import org.w3c.dom.Element;

/**
 * Factory for Virtual Deployment Framework components
 * 
 * @author <a href="mailto:dimitris@jboss.org">Dimitris Andreadis</a>
 * @version $Revision$
 */
public interface VDFComponentFactoryAdmin extends VDFComponentFactory
{
   /**
    * Initializes the VDFComponentFactory using the supplied
    * configuration element whose content will be probably
    * different for each particular implementation.
    * 
    * Once created, the configuration cannot change.
    * 
    * Calling any other method before create() is executed
    * should result in a IllegalStateException
    * 
    * Finally, the implementation should be prepared to
    * receive multiple concurrent calls.
    * 
    * @param  config    XML Element to load arbitrary config
    * @throws VDFException when any error occurs during create
    */
   public void create(Element config) throws VDFException;

   /**
    * Releases resources and destroys the VDFComponentFactory.
    * The object is unusable after destroy() has been called.
    */
   public void destroy();
}
