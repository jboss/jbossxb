/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.util.state;

import java.io.Serializable;

import org.jboss.util.CloneableObject;

/**
 * Provides the basic interface for states (both accepting, acceptable and final)
 * of a state machine.
 *
 * <p>Each state has a name and integer value.  States are considered equivilent
 *    if the integer values equal.
 *
 * <p>Note that the second opperand is an annonymous class, changing its
 *    equivilence from a vanilla Type instance.
 *
 * <p>State objects also can contain an optional opaque object.  This is provided
 *    for applications to make use of the state machine for managing data
 *    assocciated with the state.
 *      
 * @version <tt>$Revision$</tt>
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class State
   extends CloneableObject
   implements Serializable
{
   protected final int value;

   protected final String name;

   protected Object opaque;
   
   public State(final int value, final String name)
   {
      this.value = value;
      this.name = name;
   }

   public State(final int value)
   {
      this(value, null);
   }
   
   public int getValue()
   {
      return value;
   }

   public String getName()
   {
      return (name == null ? String.valueOf(value) : name);
   }

   public void setOpaque(final Object obj)
   {
      opaque = obj;
   }

   public Object getOpaque()
   {
      return opaque;
   }
   
   public String toString()
   {
      if (name == null) {
         return getName();
      }

      return name + " (" + value + ")";
   }

   public String toIdentityString()
   {
      return super.toString();
   }
   
   public int hashCode()
   {
      return value;
   }

   public boolean equals(final Object obj)
   {
      if (obj == this) return true;

      if (obj instanceof State) {
         return value == ((State)obj).getValue();
      }

      return false;
   }
}
