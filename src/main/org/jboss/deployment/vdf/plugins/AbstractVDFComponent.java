/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/
package org.jboss.deployment.vdf.plugins;

import java.net.URI;

import org.jboss.deployment.vdf.spi.VDFComponent;
import org.jboss.deployment.vdf.spi.VDFRuntimeException;

/**
 * Abstract base for Virtual Deployment Framework component implementations
 * 
 * Provides storage for the wrapped URI and a possible context object,
 * and bases all object operations (equals(), hashCode(), toString())
 * on the URI.
 * 
 * @author <a href="mailto:dimitris@jboss.org">Dimitris Andreadis</a>
 * @version $Revision$
 */
public abstract class AbstractVDFComponent implements VDFComponent
{
   // Protected -----------------------------------------------------
   
   /** the URI this component points at */
   protected URI uri;
   
   /** placeholder for arbitrary context data */
   protected Object ctx;
   
   // Constructors --------------------------------------------------
   
   public AbstractVDFComponent(URI uri)
   {
      if (uri == null)
      {
         throw new VDFRuntimeException("null URI");
      }
      this.uri = uri;
   }
   
   // Public Methods ------------------------------------------------
   
   public URI getURI()
   {
      return uri;
   }
   
   public String getScheme()
   {
      return uri.getScheme();
   }
   
   public void setContext(Object ctx)
   {
      this.ctx = ctx;
   }
   
   public Object getContext()
   {
      return ctx;
      
   }
   
   // Object overrides ----------------------------------------------
   
   public boolean equals(Object other)
   {
      if (other instanceof AbstractVDFComponent)
      {
         return ((AbstractVDFComponent)other).uri.equals(this.uri);
      }
      return false;
   }
   
   public int hashCode()
   {
      return uri.hashCode();
   }
   
   public String toString()
   {
      return uri.toString();
   }
}
