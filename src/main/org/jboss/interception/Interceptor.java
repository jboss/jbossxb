/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package org.jboss.interception;

/**
 * The interface <code>Interceptor</code> is the basic jboss
 * interceptor interface for use in mbeans, aop, ejb, and transport
 * layers.  David Jencks is by no means the original author.
 *
 * @author <a href="mailto:d_jencks@users.sourceforge.net">David Jencks</a>
 * @version 1.0
 */
public interface Interceptor
{
   public String getName();
   public InvocationResponse invoke(Invocation invocation) throws Throwable;
}
