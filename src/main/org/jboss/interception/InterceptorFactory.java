/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package org.jboss.interception;

/**
 * The interface <code>InterceptorFactory</code> is the base jboss
 * means of obtaining an interceptor for use in an interceptor chain.
 * The key can typically be an indication of which chain is being
 * constructed, such as a method.  The SimpleMetaData is the default
 * metdata supplied to each invocation using the returned interceptor.
 * The interceptor factory should store chain specific metadata in the
 * SimpleMetadata under the interceptor as group and possibly the key
 * as attr.
 *
 * @author <a href="mailto:d_jencks@users.sourceforge.net">David Jencks</a>
 * @version 1.0
 */
public interface InterceptorFactory
{
   public Interceptor createInterceptor(Object key, SimpleMetaData metadata);
}
