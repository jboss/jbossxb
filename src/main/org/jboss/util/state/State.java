/*
 * JBoss, the OpenSource WebOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.util.state;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

/** The respresentation of a state in a state machine.
 * 
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
public class State
{
   /** The name of the state */
   private String name;
   /** HashMap<String, Transition> */
   private HashMap allowedTransitions = new HashMap();
   /** Arbitrary state data */
   private Object data;

   public State(String name)
   {
      this(name, null);
   }
   public State(String name, Map transitions)
   {
      this.name = name;
      if( transitions != null )
      {
         allowedTransitions.putAll(transitions);
      }
   }

   /** Get the state name.
    * @return the name of the state.
    */ 
   public String getName()
   {
      return name;
   }

   public Object getData()
   {
      return data;
   }
   public void setData(Object data)
   {
      this.data = data;
   }

   /** An accept state is indicated by no transitions
    * @return true if this is an accept state, false otherwise.
    */ 
   public boolean isAcceptState()
   {
      return allowedTransitions.size() == 0;
   }

   /** Add a transition to the allowed transition map.
    * 
    * @param transition
    */ 
   public void addTransition(Transition transition)
   {
      allowedTransitions.put(transition.getName(), transition);
   }
   /** Lookup an allowed transition given its name.
    * 
    * @param name - the name of a valid transition from this state.
    * @return the valid transition if it exists, null otherwise.
    */ 
   public Transition getTransition(String name)
   {
      Transition t = (Transition) allowedTransitions.get(name);
      return t;
   }

   /** Get the Map<String, Transition> of allowed transitions for this state.
    * @return the allowed transitions map.
    */ 
   public Map getTransitions()
   {
      return allowedTransitions;
   }

   public String toString()
   {
      StringBuffer tmp = new StringBuffer("State(name=");
      tmp.append(name);
      tmp.append("\n");
      Iterator i = allowedTransitions.entrySet().iterator();
      while( i.hasNext() )
      {
         Map.Entry e = (Map.Entry) i.next();
         tmp.append("\t on: ");
         tmp.append(e.getKey());
         Transition t = (Transition) e.getValue();
         tmp.append(" go to: ");
         tmp.append(t.getTarget().getName());
         tmp.append('\n');
      }
      tmp.append(')');
      return tmp.toString();
   }
}
