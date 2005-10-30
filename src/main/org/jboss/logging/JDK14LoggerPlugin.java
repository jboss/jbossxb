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

import java.util.logging.Logger;
import java.util.logging.Level;

/** An example LoggerPlugin which uses the JDK java.util.logging framework.
 * 
 * @author Scott.Stark@jboss.org
 * @version $Revison:$
 */
public class JDK14LoggerPlugin implements LoggerPlugin
{
   private Logger log;

   public void init(String name)
   {
      log = Logger.getLogger(name);
   }

   public boolean isTraceEnabled()
   {
      return log.isLoggable(Level.FINER);
   }

   public void trace(Object message)
   {
      log.finer(message.toString());
   }

   public void trace(Object message, Throwable t)
   {
      log.log(Level.FINER, message.toString(), t);
   }

   public boolean isDebugEnabled()
   {
      return log.isLoggable(Level.FINE);
   }

   public void debug(Object message)
   {
      log.fine(message.toString());
   }

   public void debug(Object message, Throwable t)
   {
      log.log(Level.FINE, message.toString(), t);
   }

   public boolean isInfoEnabled()
   {
      return log.isLoggable(Level.INFO);
   }

   public void info(Object message)
   {
      log.info(message.toString());
   }

   public void info(Object message, Throwable t)
   {
      log.log(Level.INFO, message.toString(), t);
   }

   public void warn(Object message)
   {
      log.warning(message.toString());
   }

   public void warn(Object message, Throwable t)
   {
      log.log(Level.WARNING, message.toString(), t);
   }

   public void error(Object message)
   {
      log.severe(message.toString());
   }

   public void error(Object message, Throwable t)
   {
      log.log(Level.SEVERE, message.toString(), t);
   }

   public void fatal(Object message)
   {
      log.severe(message.toString());
   }

   public void fatal(Object message, Throwable t)
   {
      log.log(Level.SEVERE, message.toString(), t);
   }
}
