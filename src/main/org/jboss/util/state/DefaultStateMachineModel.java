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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jboss.util.CloneableObject;
import org.jboss.util.NullArgumentException;
import org.jboss.util.PrettyString;

/**
 * A default implementation of a state machine model.
 *
 * <p>Accepting to acceptable state mappings are backed up
 *    by a HashMap and HashSets.
 *
 * <p>Implements clonable so that the model can be used as a
 *    prototype.  Nested containers are cloned, so that changes
 *    will not effect the master or other clones.
 *      
 * @version <tt>$Revision$</tt>
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class DefaultStateMachineModel
   extends CloneableObject
   implements StateMachine.Model, Serializable, PrettyString.Appendable
{
   /** The State to StateMapping table. */
   protected Map acceptingMap = new HashMap();

   /** The mapping for the initial state. */
   protected StateMapping initial;

   /** The mapping for the current state. */
   protected StateMapping current;

   /**
    * Construct a new <tt>DefaultStateMachineModel</tt>.
    */
   public DefaultStateMachineModel()
   {
      super();
   }

   public StringBuffer appendPrettyString(StringBuffer buff, String prefix)
   {
      buff.append(prefix).append(super.toString()).append(" {").append("\n");
      buff.append(prefix).append("  Accepting state mappings:\n");
      Iterator iter = acceptingMap.keySet().iterator();
      while (iter.hasNext()) {
         buff.append(prefix).append("    ").append(acceptingMap.get((State)iter.next()));
         buff.append("\n");
      }
      buff.append(prefix).append("  Initial state: ")
         .append(initial == null ? null : initial.state)
         .append("\n");
      
      buff.append(prefix).append("  Current state: ")
         .append(current == null ? null : current.state)
         .append("\n");
      
      buff.append(prefix).append("}");

      return buff;
   }
   
   public String toString()
   {
      return appendPrettyString(new StringBuffer(), "").toString();
   }

   public boolean equals(final Object obj)
   {
      if (obj == this) return true;

      if (obj != null && obj.getClass() == getClass()) {
         DefaultStateMachineModel model = (DefaultStateMachineModel)obj;

         return
            ((current == model.current) ||
             (current != null && current.equals(model.current))) &&
            ((initial == model.initial) ||
             (initial != null && initial.equals(model.initial))) &&
            ((acceptingMap == model.acceptingMap) ||
             (acceptingMap != null && acceptingMap.equals(model.acceptingMap)));
      }

      return false;
   }

   /**
    * Get the mapping for the given state.
    *
    * @param state   The state of the mapping; must not be null.
    */
   protected StateMapping getStateMapping(final State state)
   {
      if (state == null)
         throw new NullArgumentException("state");
      
      return (StateMapping)acceptingMap.get(state);
   }

   /**
    * Put a new mapping into the map.
    *
    * @return   The previous mapping for the state or null if none.
    */
   protected StateMapping putStateMapping(final State state, final Set acceptable)
   {
      return (StateMapping)acceptingMap.put(state, new StateMapping(state, acceptable));
   }

   public boolean isMappedState(State state)
   {
      if (state == null) return false;
      
      return getStateMapping(state) != null;
   }
   
   public State getMappedState(final State state)
   {
      StateMapping mapping = getStateMapping(state);
      if (mapping != null)
         return mapping.getAcceptingState();

      throw new IllegalArgumentException("State not mapped: " + state);
   }

   public Set addState(final State state, final Set acceptable)
   {
      StateMapping prevStateMapping = getStateMapping(state);
      
      // If we will replace the state, do some clean up before
      if (containsState(state)) {
         // update mapping to reflect new state value
         updateAcceptableMapping(state, false);
      }
      
      // Now replace it
      putStateMapping(state, acceptable);

      if (acceptable != null) {
         // Sanity check acceptable states
         Iterator iter = acceptable.iterator();
         while (iter.hasNext()) {
            State temp = (State)iter.next();
            if (temp == null)
               throw new NullArgumentException("acceptable", "Set Element");
         
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

      return prevStateMapping == null ? null : prevStateMapping.getAcceptableStates();
   }

   public Set addState(final State state, final State[] acceptable)
   {
      if (acceptable == null)
         throw new NullArgumentException("acceptable");

      if (acceptable.length == 0) {
         return addState(state, (Set)null);
      }
      
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
      HashSet set = new HashSet(1);
      set.add(acceptable);
      return addState(state, set);
   }
   
   public Set addState(final State state)
   {
      return addState(state, (Set)null);
   }

   public void setInitialState(final State state)
   {
      StateMapping mapping = getStateMapping(state);
      if (mapping == null)
         throw new IllegalArgumentException("State not mapped: " + state);

      this.initial = mapping;
   }

   public State getInitialState()
   {
      return initial != null ? initial.state : null;
   }

   public void setCurrentState(final State state)
   {
      StateMapping mapping = getStateMapping(state);
      if (mapping == null)
         throw new IllegalArgumentException("State not mapped: " + state);
      
      this.current = mapping;
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
      if (current != null && state.equals(current.state))
         throw new IllegalArgumentException
            ("Can not remove current state: " + state);

      StateMapping prevStateMapping = getStateMapping(state);

      // remove the mappings for this state
      updateAcceptableMapping(state, true);

      // Finally remove it
      acceptingMap.remove(state);
      
      return prevStateMapping.getAcceptableStates();
   }

   /**
    * Update acceptable mappings.
    *
    * @param state   The state value to update or remove
    * @param remove  True to remove the state from all mappings.
    */
   protected void updateAcceptableMapping(final State state, final boolean remove)
   {
      Iterator iter = acceptingMap.entrySet().iterator();

      while (iter.hasNext()) {
         StateMapping mapping = (StateMapping)((Map.Entry)iter.next()).getValue();

         // only attempt to update non-final states
         if (mapping.getAcceptableStates() != null &&
             mapping.getAcceptableStates().contains(state))
         {
            mapping.getAcceptableStates().remove(state);

            if (!remove) {
               mapping.getAcceptableStates().add(state);
            }
         }
      }
   }
   
   public Set states()
   {
      return Collections.unmodifiableSet(acceptingMap.keySet());
   }

   public Set acceptableStates(final State state)
   {
      StateMapping mapping = getStateMapping(state);

      if (mapping.getAcceptableStates() != null)
         return Collections.unmodifiableSet(mapping.getAcceptableStates());
      return null;
   }

   public void clear()
   {
      acceptingMap.clear();
      initial = null;
      current = null;
   }

   public Object clone()
   {
      // clone one level deeper so that the model can be used as a prototype.
      
      DefaultStateMachineModel model = (DefaultStateMachineModel)super.clone();
      model.acceptingMap = new HashMap();

      // Need to make sure that value entries are cloned too
      Iterator iter = acceptingMap.entrySet().iterator();
      while (iter.hasNext()) {
         Map.Entry entry = (Map.Entry)iter.next();
         model.acceptingMap.put(entry.getKey(),
                                ((StateMapping)entry.getValue()).clone());
      }
       
      if (model.current != null)
         model.current = (StateMapping)current.clone();

      if (model.initial != null)
         model.initial = (StateMapping)initial.clone();

      return model;
   }
}
