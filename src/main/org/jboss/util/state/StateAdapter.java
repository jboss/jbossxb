/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.util.state;

/**
 * ???
 *
 * @version <tt>$Revision$</tt>
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class StateAdapter
   extends State
   implements StateMachine.Acceptable, StateMachine.ChangeListener
{
   public StateAdapter(final int value, final String name)
   {
      super(value, name);
   }

   public StateAdapter(final int value)
   {
      super(value);
   }

   public boolean accept(final State state) { return false; }

   public void stateChanged(final StateMachine.ChangeEvent event) {}
}
