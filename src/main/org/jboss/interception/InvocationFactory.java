/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.interception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The class <code>InvocationFactory</code> is the generic factory for
 * constructing invocation objects using particular chains of
 * interceptors.  This handles multiple chains selected based on a key
 * such as the method being invoked.  Interceptor factories are
 * expected to store instance specific metadata in the supplied
 * SimpleMetaData object when a chain is being constructed.
 *
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @author <a href="mailto:d_jencks@users.sourceforge.net">David Jencks</a>
 * @version $Revision$
 *
 */
public abstract class InvocationFactory
{

   //This synchronization barrier should be sufficient to assure that,
   //with copy-on-write, we always get a consistent view of a chain.
   private final Map chains = Collections.synchronizedMap(new HashMap());

   /**
    * The field <code>defaultMetaData</code> contains the default
    * metadata set up by the interceptor chains.  It is added last to
    * the metadatas in each Invocation so it may be overridden.
    *
    */
   protected SimpleMetaData defaultMetaData = new SimpleMetaData();

   protected String name;

   public InvocationFactory(String name)
   {
      this.name = name;
   }


   public SimpleMetaData getDefaultMetaData()
   {
      return defaultMetaData;
   }


   public String getName()
   {
      return name;
   }

   /**
    * The <code>newInvocation</code> method constructs a new Invocation
    * object of the specified type.  It uses the key to obtain the
    * correct chain for constructing the interceptor iterator. It uses
    * the supplied metadatas for metadata, after inserting the default
    * metadata as the first element.
    *
    * @param type an <code>InvocationType</code> value
    * @param key an <code>Object</code> value
    * @param metadatas a <code>List</code> value
    * @return an <code>Invocation</code> value
    */
   protected Invocation newInvocation(InvocationType type, Object key, List metadatas)
   {
      List chain = (List)chains.get(key);
      if (chain == null)
      {
         throw new IllegalArgumentException("No chain found for key: " + key);
      } // end of if ()
      if (metadatas == null)
      {
         metadatas = new LinkedList();
      } // end of if ()
      //Add the default metadata last so it may be overridden.
      //synchronization is necessary to assure we have an accurate
      //view of the default metadata in case it was constructed or
      //modified in a different thread.  See discussions of
      //double-checked locking is broken.
      synchronized (defaultMetaData)
      {
         metadatas.add(metadatas.size(), defaultMetaData);
      }
      Invocation invocation = new Invocation(type, chain.iterator(), metadatas);
      return invocation;
   }

   /**
    * The <code>setChain</code> method maps the supplied key to the
    * supplied interceptor chain.
    *
    * @param key an <code>Object</code> value
    * @param chain a <code>List</code> value
    */
   public void setChain(Object key, List chain)
   {
      chains.put(key, chain);
   }

   /**
    * The <code>createChain</code> method constructs an interceptor
    * chain using the supplied list of InterceptorFactories by asking
    * each factory in order to supply an interceptor for the key and
    * metadata.  If the factory returns a non-null result, it is added
    * to the chain.  The factory may also add appropriately processed
    * metadata to the metadata map.
    *
    * @param key an <code>Object</code> value
    * @param metadata a <code>MetaDataResolver</code> value
    * @param factories a <code>List</code> value
    */
   public void createChain(Object key, List factories)
   {
      List chain = new ArrayList(factories.size());
      for (Iterator i = factories.iterator(); i.hasNext(); )
      {
         Interceptor interceptor =  ((InterceptorFactory)i.next()).createInterceptor(key, defaultMetaData);
         if (interceptor != null)
         {
            chain.add(interceptor);
         } // end of if ()

      } // end of for ()
      setChain(key, chain);
   }


}
