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
import java.util.HashSet;
import java.util.Set;

import org.jboss.util.CloneableObject;
import org.jboss.util.NullArgumentException;

/**
 * A container for a state mapping, which maps an accepting state
 * to it's acceptable states.
 * 
 * @version <tt>$Revision$</tt>
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class StateMapping
   extends CloneableObject
   implements Serializable
{
   protected State state;
   protected Set acceptable;

   public StateMapping(final State state, final Set acceptable)
   {
      construct(state, acceptable);
   }

   public StateMapping(final State state, final State[] acceptable)
   {
      if (acceptable == null)
         throw new NullArgumentException("acceptable");

      if (acceptable.length == 0) {
         construct(state, (Set)null);
         return;
      }
      
      HashSet set = new HashSet(acceptable.length);
      
      for (int i=0; i<acceptable.length; i++) {
         if (acceptable[i] == null)
            throw new NullArgumentException("acceptable", i);
         
         set.add(acceptable[i]);
      }

      construct(state, set);
   }

   public StateMapping(final State state)
   {
      construct(state, null);
   }
   
   protected void construct(final State state, final Set acceptable)
   {
      if (state == null)
         throw new NullArgumentException("state");

      this.state = state;
      this.acceptable = acceptable;
   }
   
   public boolean equals(final Object obj)
   {
      if (obj == this) return true;
      
      if (obj != null && obj.getClass() == getClass()) {
         StateMapping mapping = (StateMapping)obj;
         
         return
            ((state == mapping.state) ||
             (state != null && state.equals(mapping.state))) &&
            ((acceptable == mapping.acceptable) ||
             (acceptable != null && acceptable.equals(mapping.acceptable)));
      }

      return false;
   }

   public String toString()
   {
      return
         state + (isFinal()
                  ? " is final"
                  : " accepts: " + acceptable);
   }

   public Object clone()
   {
      StateMapping mapping = (StateMapping)super.clone();
      if (mapping.acceptable != null) {
         mapping.acceptable = new HashSet(acceptable);
      }
      
      return mapping;
   }

   public State getAcceptingState()
   {
      return state;
   }

   public Set getAcceptableStates()
   {
      return acceptable;
   }
   
   public boolean isFinal()
   {
      return acceptable == null;
   }
}
