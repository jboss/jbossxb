/*
 * JBoss, the OpenSource WebOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.util.state;

/** A representation of a transition from a state to another state.
 * 
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
public class Transition
{
   private String name;
   private State target;

   public Transition(String name, State target)
   {
      this.name = name;
      this.target = target;
   }

   public String getName()
   {
      return name;
   }
   public State getTarget()
   {
      return target;
   }
}
