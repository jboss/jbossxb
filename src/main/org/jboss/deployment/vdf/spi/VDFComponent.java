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
 * Interface implemented by Virtual Deployment Framework components.
 * 
 * VDFComponents are meant to hide all details of accessing
 * filesystems and resources from other deployment subsystems,
 * thus avoiding direct URL access.
 * 
 * Methods of this interface may throw unchecked VDFRuntimeExceptions
 * 
 * @author <a href="mailto:dimitris@jboss.org">Dimitris Andreadis</a>
 * @version $Revision$
 */
public interface VDFComponent
{
   /** The URI wrapped by the VDFComponent.
    */
   public URI getURI();

   /** The URI's scheme.
    */
   public String getScheme();
   
   /** The BaseName of the component (last part of its pathname).
    */
   public String getBaseName();
   
   /** The last modification time of the pointed resource.
    */
   public long getLastModified();

   /** Checks if the pointed resource exists.
    */
   public boolean exists();

   /** Checks if the pointed resource is a file abstraction.
    */
   public boolean isFile();

   /** Checks if the pointed resource corresponds to a directory abstraction.
    */
   public boolean isDirectory();

   /** Returns the children components of this component, if the component corresponds
    * to a directory abstraction. The returned array will be empty if there are no children.
    * If the method is called on a non-directory resource, a VDFRuntimeException will be thrown.
    */ 
   public VDFComponent[] getChildren();

   /** Convenience method to store any context object with this component and retrieve it later
    */
   public void setContext(Object ctx);   

   /** Convenience method to get any previously stored context object
    */
   public Object getContext();

}
