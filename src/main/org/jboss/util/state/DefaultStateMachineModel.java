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

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Iterator;

import org.jboss.util.NullArgumentException;
import org.jboss.util.CloneableObject;

/**
 * A default implementation of a state machine model.
 *
 * <p>Accepting to acceptable state mappings are backed up
 *    by a HashMap and HashSets.
 *      
 * @version <tt>$Revision$</tt>
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class DefaultStateMachineModel
   extends CloneableObject
   implements StateMachine.Model, Serializable
{
   /**
    * A container for entiries in the state acceptable map.
    */
   protected class MappingEntry
   {
      public State state;
      public Set acceptableStates;

      public MappingEntry(final State state, Set acceptableStates)
      {
         this.state = state;
         this.acceptableStates = acceptableStates;
      }
   }

   /** The mapping from State to MappingEntry. */
   protected Map acceptingMap = new HashMap();

   /** The mapping entry for the initial state. */
   protected MappingEntry initial;

   /** The mapping entry for the current state. */
   protected MappingEntry current;

   /**
    * Construct a new <tt>DefaultStateMachineModel</tt>.
    */
   public DefaultStateMachineModel()
   {
      super();
   }

   /**
    * Get an entry from the map.
    *
    * @param state   The state of the entry; must not be null.
    */
   protected MappingEntry getEntry(final State state)
   {
      if (state == null)
         throw new NullArgumentException("state");
      
      return (MappingEntry)acceptingMap.get(state);
   }

   /**
    * Put a new entry into the map.
    *
    * @return   The previous entry for the state or null if none.
    */
   protected MappingEntry putEntry(final State state, final Set acceptable)
   {
      MappingEntry entry = new MappingEntry(state, acceptable);
      return (MappingEntry)acceptingMap.put(state, entry);
   }
   
   public State getState(final State state)
   {
      MappingEntry entry = getEntry(state);
      if (entry != null)
         return entry.state;
      return null;
   }
   
   public Set addState(final State state, final Set acceptable)
   {
      MappingEntry prevEntry = getEntry(state);
      
      // If we will replace the state, do some clean up before
      if (containsState(state)) {
         //
         // This looks odd, because of the typing by value and
         // not typing by value + class.  This will replace
         // the previous version of this state with the new
         // version.
         //
         
         // update mapping to reflect new state value
         updateAcceptableMapping(prevEntry.state, prevEntry.state);
      }
      
      // Now replace it
      putEntry(state, acceptable);

      if (acceptable != null) {
         // Sanity check acceptable states
         Iterator iter = acceptable.iterator();
         while (iter.hasNext()) {
            State temp = (State)iter.next();
            if (temp == null)
               throw new NullArgumentException("acceptable", "?");
         
            if (temp.equals(state)) {
               throw new IllegalArgumentException
                  ("Acceptable states must not include the accepting state: " + temp);
            }

            // Add final states for all non-existant states
            if (!containsState(temp)) {
               addState(temp);
            }
         }
      }

      return prevEntry == null ? null : prevEntry.acceptableStates;
   }

   public Set addState(final State state, final State[] acceptable)
   {
      if (acceptable == null)
         throw new NullArgumentException("acceptable");
      
      HashSet set = new HashSet(acceptable.length);
      
      for (int i=0; i<acceptable.length; i++) {
         if (acceptable[i] == null)
            throw new NullArgumentException("acceptable", i);
         
         set.add(acceptable[i]);
      }

      return addState(state, set);
   }

   public Set addState(final State state, final State acceptable)
   {
      return addState(state, new State[] { acceptable });
   }
   
   public Set addState(final State state)
   {
      return addState(state, (Set)null);
   }

   public void setInitialState(final State state)
   {
      MappingEntry entry = getEntry(state);
      if (entry == null)
         throw new IllegalArgumentException("State not mapped: " + state);

      this.initial = entry;
   }

   public State getInitialState()
   {
      return initial != null ? initial.state : null;
   }

   public void setCurrentState(final State state)
   {
      MappingEntry entry = getEntry(state);
      if (entry == null)
         throw new IllegalArgumentException("State not mapped: " + state);
      
      this.current = entry;
   }

   public State getCurrentState()
   {
      return current != null ? current.state : null;
   }
   
   public boolean containsState(final State state)
   {
      return acceptingMap.containsKey(state);
   }

   public Set removeState(final State state)
   {
      if (state == null)
         throw new NullArgumentException("state");
      if (state.equals(current.state))
         throw new IllegalArgumentException
            ("Can not remove current state: " + state);

      MappingEntry prevEntry = getEntry(state);

      // If state is non-final then update acceptable mappings
      if (prevEntry.acceptableStates != null) {
         // remove the mappings for this state
         updateAcceptableMapping(state, null);
      }
      
      return prevEntry.acceptableStates;
   }

   /**
    * Update acceptable mappings.
    *
    * @param prev   The previous state to update/remove.
    * @param next   The next value for the state or null to remove previous.
    */
   protected void updateAcceptableMapping(final State prev, final State next)
   {
      Iterator iter = acceptingMap.entrySet().iterator();

      while (iter.hasNext()) {
         MappingEntry entry = (MappingEntry)((Map.Entry)iter.next()).getValue();

         // only attempt to update non-final states
         if (entry.acceptableStates != null && entry.acceptableStates.contains(prev)) {
            entry.acceptableStates.remove(prev);
            entry.acceptableStates.add(next);
         }
      }
   }
   
   public Set states()
   {
      return Collections.unmodifiableSet(acceptingMap.keySet());
   }

   public Set acceptableStates(final State state)
   {
      MappingEntry entry = getEntry(state);
      return Collections.unmodifiableSet(entry.acceptableStates);
   }

   public void clear()
   {
      acceptingMap.clear();
      initial = null;
      current = null;
   }

   public Object clone()
   {
      // deeply (well sort of) clone the acceptingMap, else clones
      // will share the same map
      
      DefaultStateMachineModel model = (DefaultStateMachineModel)super.clone();
      model.acceptingMap = new HashMap(model.acceptingMap);

      return model;
   }
}
