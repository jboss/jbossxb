/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package org.jboss.interception;

import java.util.Iterator;
import java.util.List;

/**
 * The class <code>Invocation</code> is the jboss basic invocation
 * object.  It carries all information in the metaDataResolvers and
 * uses the interceptors interator to determine the sequence of
 * Interceptors to go through.  The interceptors iterator can be
 * replaced to redirect to another chain, transfer to another vm, etc.
 *
 * The metadata specific to this instance (method name, arguments,
 * security info, etc) are held in the metadata instance variable.
 * The "container specific" metadata that is normally set up by
 * Interceptor factories on deployment is in the final metadata in the
 * metaDataResolvers list so it may be overridden by other metadata.
 *
 * @author  <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @author <a href="mailto:d_jencks@users.sourceforge.net">David Jencks</a>
 * @version $Revision$
 *
 */
public class Invocation
{
   protected InvocationType type;

   protected transient Iterator interceptors;
   protected transient List metaDataResolvers = null;

   protected SimpleMetaData metadata = new SimpleMetaData();

   public Invocation(InvocationType type, Iterator interceptors, List metaDataResolvers)
   {
      // We expect a copy of the interceptor chain so that it can't change
      // in the middle of an invocation.  This is so that
      // we can redeploy interceptor chains, yet not effect
      // currently running invocations.
      this.interceptors = interceptors;
      this.type = type;

      //
      // We expect a copy
      //
      this.metaDataResolvers = metaDataResolvers;
      this.metaDataResolvers.add(0, metadata);
   }



   public InvocationResponse invokeNext() throws Throwable
   {
      if (interceptors.hasNext())
      {
         Interceptor next = (Interceptor)interceptors.next();
         return next.invoke(this);
      }
      throw new RuntimeException("End of interceptor chain reached!");
   }

   public InvocationType getType()
   {
      return type;
   }

   public void setInterceptors(Iterator interceptors)
   {
      this.interceptors = interceptors;
   }

   public SimpleMetaData getInstanceMetaData()
   {
      return metadata;
   }

   public Object getMetaData(String group, String attr)
   {
      for (Iterator i =  metaDataResolvers.iterator(); i.hasNext(); )
      {
         MetaDataResolver resolver = (MetaDataResolver)i.next();
         Object val = resolver.resolve(this, group, attr);
         if (val != null)
         {
            return val;
         }
      }
      return null;
   }
}
