/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.util.file;

/**
 * ArchiveBrowser filter to find .class files
 *
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @version $Revision$
 */
public class ClassFileFilter implements ArchiveBrowser.Filter
{
   public boolean accept(String filename)
   {
      return filename.endsWith(".class");
   }
}