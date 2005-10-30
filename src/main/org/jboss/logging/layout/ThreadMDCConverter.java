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
package org.jboss.logging.layout;

import org.apache.log4j.MDC;
import org.apache.log4j.helpers.FormattingInfo;
import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.spi.LoggingEvent;

/** A PatternConverter that uses the current thread MDC rather than the
 * LoggingEvent MDC value.
 *
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
public class ThreadMDCConverter extends PatternConverter
{
   private String key;
   /** Creates a new instance of ThreadMDCPatternConverter */
   public ThreadMDCConverter(FormattingInfo formattingInfo, String key)
   {
      super(formattingInfo);
      this.key = key;
   }

   protected String convert(LoggingEvent loggingEvent)
   {
      Object val = MDC.get(key);
      String strVal = null;
      if( val != null )
         strVal = val.toString();
      return strVal;
   }

}
