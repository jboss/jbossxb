/*
 * JBoss, the OpenSource WebOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.util.propertyeditor;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.jboss.util.NestedRuntimeException;
import org.jboss.util.StringPropertyReplacer;
import org.jboss.util.threadpool.BlockingMode;

/**
 * A property editor for {@link org.jboss.util.threadpool.BlockingMode} enum.
 *
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
public class BlockingModeEditor
   extends TextPropertyEditorSupport
{
   /**
    * Returns a BlockingMode for the input object converted to a string.
    *
    * @return a BlockingMode enum instance
    *
    */
   public Object getValue()
   {
      String text = getAsText();
      BlockingMode mode = BlockingMode.toBlockingMode(text);
      return mode;
   }
}
