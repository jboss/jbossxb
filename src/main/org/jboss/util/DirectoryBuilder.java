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
package org.jboss.util;

import java.io.File;

/**
 * A simple utility to make it easier to build File objects for nested
 * directories based on the command line 'cd' pattern.
 *      
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @version $Revision$
 */
public class DirectoryBuilder
{
   protected File root;

   public DirectoryBuilder() {
      // empty
   } 

   public DirectoryBuilder(final File root) {
      this.root = root;
   } 

   public DirectoryBuilder(final File root, final File child) {
      this(root);
      cd(child);
   }

   public DirectoryBuilder(final String rootname) {
      this(new File(rootname));
   }

   public DirectoryBuilder(final String rootname, final String childname) {
      this(new File(rootname), new File(childname));
   }

   public DirectoryBuilder cd(final File child) {
      if (child.isAbsolute()) {
	 root = child;
      }
      else {
	 root = new File(root, child.getPath());
      }
      return this;
   }

   public DirectoryBuilder cd(final String childname) {
      return cd(new File(childname));
   }

   public File get() {
      return root;
   }

   public String toString() {
      return root.toString();
   }
}
