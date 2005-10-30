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

import org.apache.log4j.helpers.PatternParser;

/** A subclass of the log4j PatternLayout that add the following conversion
characters:

   <p>
   <table border="1" CELLPADDING="8">
   <th>Conversion Character</th>
   <th>Effect</th>

   <tr>
     <td align=center><b>z</b></td>
     <td>Used to output current thread NDC value. This can be used to obtain
      an NDC to augment any NDC associated with the LoggingEvent. This might
      be necessary if the LoggingEvent has been serialized between VMs.
     </td>
   </tr>
   <tr>
     <td align=center><b>Z</b></td>
     <td>Used to output current thread MDC value. This can be used to obtain
      an MDC to augment any MDC associated with the LoggingEvent. This might
      be necessary if the LoggingEvent has been serialized between VMs.
      The Z conversion character must be followed by the key for the map placed
      between braces, as in %Z{theKey} where theKey is the key.
      The value in the MDC corresponding to the key will be output.
     </td>
   </tr>

 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
public class PatternLayout extends org.apache.log4j.PatternLayout
{

  protected PatternParser createPatternParser(String pattern)
  {
    return new PatternParserEx(pattern);
  }

}
