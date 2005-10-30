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
package org.jboss.util.propertyeditor;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.jboss.util.NestedRuntimeException;

/**
 * A property editor for {@link java.util.Date}.
 *
 * @version <tt>$Revision$</tt>
 * @author  <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @author  <a href="mailto:d_jencks@users.sourceforge.net">David Jencks</a>
 * @author Scott.Stark@jboss.org
 */
public class DateEditor extends TextPropertyEditorSupport
{
   static DateFormat[] formats;

   static
   {
      PrivilegedAction action = new PrivilegedAction()
      {
         public Object run()
         {
            String defaultFormat = System.getProperty("org.jboss.util.propertyeditor.DateEditor.format",
               "MMM d, yyyy");
            formats = new DateFormat[] 
            {
               new SimpleDateFormat(defaultFormat),
               // Tue Jan 04 00:00:00 PST 2005
               new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy"),
               // Wed, 4 Jul 2001 12:08:56 -0700
               new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z")
            };
            return null;
         }
      };
      AccessController.doPrivileged(action);
   }

   /**
    * Returns a Date for the input object converted to a string. It tries
    * the default date format as specified by the following SimpleDateFormat
    * formats in order:
    * 
    * org.jboss.util.propertyeditor.DateEditor.format system property
    * (or MMM d, yyyy, if not specified),
    * EEE MMM d HH:mm:ss z yyyy
    * EEE, d MMM yyyy HH:mm:ss Z
    * @return a Date object
    *
    */
   public Object getValue()
   {
      int n = 0;
      ParseException ex = null;
      do
      {
         try
         {
            DateFormat df = formats[n];
            return df.parse(getAsText());
         }
         catch (ParseException e)
         {
            ex = e;
         }
         n ++;
      } while( n < formats.length );
      throw new NestedRuntimeException(ex);
   }
}
