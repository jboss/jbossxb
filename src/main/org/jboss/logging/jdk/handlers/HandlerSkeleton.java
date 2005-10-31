/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.jboss.logging.jdk.handlers;

import java.util.logging.Handler;

/**
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
public abstract class HandlerSkeleton extends Handler
{
   protected String name;

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public void activateOptions()
   {
   }

   protected void debug(String msg)
   {
      System.out.println(msg);
   }
}
