/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package org.jboss.logging;

import java.util.logging.LogManager;

/**
 * A simple extension to the default JDK LogManager that overrides the reset
 * call to a noop to avoid the current behavior of the LogManager installing
 * a shutdown hook which calls reset. The problem with this behavior is that
 * all usage of the logging api during shutdown produces no output.
 * 
 * The #doReset() method can be called after the jboss shutdown hook operates
 * to allow the logging layer to cleanup while still allowing jboss components
 * to use the jdk logging layer.
 * 
 * Install using -Djava.util.logging.manager=org.jboss.logging.JBossJDKLogManager
 * 
 * @see LogManager
 * 
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
public class JBossJDKLogManager extends LogManager
{
   /**
    * Ignore the reset operation
    * 
    * @see #doReset() to force a reset
    */
   public void reset()
   {
   }

   /**
    * Invokes the LogManager.reset() method.
    */
   public void doReset()
   {
      super.reset();
   }
}
