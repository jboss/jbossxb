/*
 * JBoss, the OpenSource webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.net.ssl;

import javax.net.ssl.SSLSocketFactory;


/** A simple builder for creating SSLSocketFactory instances.
 *
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
public interface SSLSocketFactoryBuilder
{
   SSLSocketFactory getSocketFactory() throws Exception;
}
