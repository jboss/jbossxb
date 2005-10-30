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
package org.jboss.logging.appender;

import java.util.HashMap;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.TriggeringEventEvaluator;
import gnu.regexp.RE;
import gnu.regexp.REException;

/** An implementation of the log4j TriggeringEventEvaluator that matches the
 * LoggingEvent message against the MDB{RegexEventEvaluator} regular
 * expression.
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
public class RegexEventEvaluator implements TriggeringEventEvaluator
{
   /** A cache HashMap<String, RE> of previously compiled REs */
   private HashMap regexMap = new HashMap();

   /** Lookup the current MDC 'RegexEventEvaluator' to determine the regular
    * expression context that should be applied to determine if the logging
    * event should be considered a triggering event. If there is no value
    * for the 'RegexEventEvaluator' key then no comparision is made.
    *
    * @param event the logging event to check
    * @return true if MDC{RegexEventEvaluator} is a regex expression that
    * matches the event.getRenderedMessage(), false otherwise.
    */
   public boolean isTriggeringEvent(LoggingEvent event)
   {
      String regex = (String) event.getMDC("RegexEventEvaluator");
      boolean isTriggeringEvent = false;
      if( regex != null )
      {
         // Look for a cached regex pattern
         RE re = (RE) regexMap.get(regex);
         if( re == null )
         {
            try
            {
               re = new RE(regex);
               regexMap.put(regex, re);
            }
            catch (REException e)
            {
            }
         }

         if( re != null )
         {
            String msg = event.getRenderedMessage();
            if( msg != null )
               isTriggeringEvent = re.isMatch(msg);
         }
      }
      return isTriggeringEvent;
   }
}

