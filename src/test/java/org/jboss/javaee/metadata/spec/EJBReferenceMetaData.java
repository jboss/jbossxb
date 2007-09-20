/*
* JBoss, Home of Professional Open Source
* Copyright 2006, JBoss Inc., and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jboss.javaee.metadata.spec;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlType;

import org.jboss.javaee.metadata.support.MergeableMappedMetaData;

/**
 * EJBReferenceMetaData.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
@XmlType(name="ejb-refType")
public class EJBReferenceMetaData extends AbstractEJBReferenceMetaData implements MergeableMappedMetaData<EJBReferenceMetaData>
{
   /** The serialVersionUID */
   private static final long serialVersionUID = -3828756360112709340L;
   
   /** The home type */
   private String home;
   
   /** The remote type */
   private String remote;
   
   /** The invoker bindings */
   private Map<String, String> invokerBindings;
   
   /**
    * Create a new EJBReferenceMetaData.
    */
   public EJBReferenceMetaData()
   {
      // For serialization
   }

   /**
    * Get the home.
    * 
    * @return the home.
    */
   public String getHome()
   {
      return home;
   }

   /**
    * Set the home.
    * 
    * @param home the home.
    * @throws IllegalArgumentException for a null home
    */
   public void setHome(String home)
   {
      if (home == null)
         throw new IllegalArgumentException("Null home");
      this.home = home;
   }

   /**
    * Get the remote.
    * 
    * @return the remote.
    */
   public String getRemote()
   {
      return remote;
   }

   /**
    * Set the remote.
    * 
    * @param remote the remote.
    * @throws IllegalArgumentException for a null remote
    */
   public void setRemote(String remote)
   {
      if (remote == null)
         throw new IllegalArgumentException("Null remote");
      this.remote = remote;
   }

   /**
    * Get an invoker proxy binding name
    * 
    * @param invokerProxyBindingName
    * @return the jndi name override
    */
   @Deprecated // This is in the wrong place
   public  String getInvokerBinding(String invokerProxyBindingName)
   {
      if (invokerBindings == null)
         return null;
      return invokerBindings.get(invokerProxyBindingName);
   }

   /**
    * Add an invoker binding
    * 
    * @param invokerProxyBindingName the invoker proxy binding name
    * @param jndiName the jndi name
    */
   @Deprecated // This is in the wrong place
   public void addInvokerBinding(String invokerProxyBindingName, String jndiName)
   {
      if (invokerBindings == null)
         invokerBindings = new HashMap<String, String>();
      invokerBindings.put(invokerProxyBindingName, jndiName);
   }
   
   public EJBReferenceMetaData merge(EJBReferenceMetaData original)
   {
      EJBReferenceMetaData merged = new EJBReferenceMetaData();
      merge(merged, original);
      return merged;
   }
   
   /**
    * Merge
    * 
    * @param merged the data to merge into
    * @param original the original data
    */
   public void merge(EJBReferenceMetaData merged, EJBReferenceMetaData original)
   {
      super.merge(merged, original);
      if (home != null)
         merged.setHome(home);
      else if (original.home != null)
         merged.setHome(original.home);
      if (remote != null)
         merged.setRemote(remote);
      else if (original.remote != null)
         merged.setRemote(original.remote);
   }
}
