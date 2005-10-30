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

/**
 * Defines a "pluggable" login module. In fact, this is only used to split between 
 * log4j and /dev/null. Choice is made in org.jboss.logging.Logger
 *
 * @see org.jboss.logging.Logger
 * @see org.jboss.logging.Log4jLoggerPlugin
 * @see org.jboss.logging.NullLoggerPlugin
 *
 * @author  <a href="mailto:sacha.labourey@cogito-info.ch">Sacha Labourey</a>.
 * @version $Revision$
 *
 * <p><b>Revisions:</b>
 *
 * <p><b>30 mai 2002 Sacha Labourey:</b>
 * <ul>
 * <li> First implementation </li>
 * </ul>
 */

public interface LoggerPlugin
{
   // must be called first
   //
   public void init (String name);
   
   public boolean isTraceEnabled();
   public void trace(Object message);
   public void trace(Object message, Throwable t);

   public boolean isDebugEnabled();
   public void debug(Object message);
   public void debug(Object message, Throwable t);

   public boolean isInfoEnabled();
   public void info(Object message);
   public void info(Object message, Throwable t);

   public void warn(Object message);
   public void warn(Object message, Throwable t);

   public void error(Object message);
   public void error(Object message, Throwable t);

   public void fatal(Object message);
   public void fatal(Object message, Throwable t);
   
}
