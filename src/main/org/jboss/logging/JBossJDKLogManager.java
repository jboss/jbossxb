/*
  * JBoss, Home of Professional Open Source
  * Copyright 2005, JBoss Inc., and individual contributors as indicated
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
