/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.util.state;

import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;

/**
 * ???
 *      
 * @version <tt>$Revision$</tt>
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class Test
{
   static StateMachine machine = null;

   static State NEW = new State(0, "NEW");
   static State INITIALIZING = new State(1, "INITIALIZING");
   static State INITIALIZED = new State(2, "INITIALIZED");
   static State STARTING = new State(3, "STARTING");
   static State STARTED = new StateAdapter(4, "STARTED") {
         public void stateChanged(StateMachine.ChangeEvent event) {
            System.out.println(this + " got event: " + event);
         }
      };
   static State FAILED = new StateAdapter(100, "FAILED") {
         public void stateChanged(StateMachine.ChangeEvent event) {
            System.out.println(this + " got event: " + event);
         }
      };
   static State FINAL = new AcceptableState(101, "FINAL") {
         public boolean accept(State state) {
            System.out.println("Checking acceptance for state: " + state);
            return false;
         }
      };

   static StateMachine.Model model = null;
   
   public static void main(String[] args)
   {
      DefaultStateMachineModel model = new DefaultStateMachineModel();
      Test.model = model;

      Set set;
      
      set = model.addState(NEW, INITIALIZING);
      System.out.println("replaced set: " + set);
      
      model.addState(INITIALIZING, new State[] { INITIALIZED, FAILED });
      model.addState(INITIALIZED, new State[] { STARTING, FAILED });
      model.addState(STARTING, INITIALIZED);

      // test set replacement returns
      model.addState(STARTED, INITIALIZED); // invalid state
      set = model.addState(STARTED, STARTING); // this is what we want
      System.out.println("replaced set: " + set); // should have returned INITIALIZED
      
      model.addState(FINAL);
      
      Set mostStates = new HashSet(model.states());
      mostStates.remove(NEW); // new can only transition to INITIALIZED, not FAILED
      mostStates.remove(FAILED); // can not accept outselves
      System.out.println("Most states: " + mostStates);
      model.addState(FAILED, mostStates);
      
      model.setInitialState(NEW);

      System.out.println("New state: " + NEW);
      System.out.println("Failed state: " + FAILED);

      machine = new StateMachine((StateMachine.Model)model.clone());
      test("new machine");
      
      machine.reset();
      test("reset");

      machine = new StateMachine((StateMachine.Model)model.clone());
      test("model cloning");

      StateMachine.Model aModel;
      /*
      aModel = (StateMachine.Model)model.clone();
      aModel.removeState(FAILED);
      System.out.println("Prototype model states: " + model.states());

      machine = new StateMachine(aModel);
      try {
         test("model cloning with removal");
      }
      catch (Exception e) {
         System.out.println("This is okay: " + e);
         System.out.println();
      }

      machine = new StateMachine((StateMachine.Model)model.clone());
      System.out.println("Prototype model states: " + model.states());
      test("model cloning after removal");
      */
      
      // test exception handling
      machine = new StateMachine((StateMachine.Model)model.clone());

      // change listener
      machine.addChangeListener(new StateMachine.ChangeListener() {
            public void stateChanged(StateMachine.ChangeEvent event) {
               throw new RuntimeException("ChangeListener");
            }
         });

      try {
         machine.transition(INITIALIZING);
         // should not make it here
      }
      catch (Exception e) {
         System.out.println("*** Caught: " + e);
         dumpState();
         // state should be INITIALIZING
      }

      machine = new StateMachine((StateMachine.Model)model.clone());
      
      // acceptable state
      State state = new AcceptableState(100, "FAILED") {
            public boolean accept(State state) {
               throw new RuntimeException("Accetable");
            }
         };

      System.out.println("New FAILED state: " + state + "(" + state.toIdentityString() + ")");
      
      aModel = machine.getModel();
      System.out.println("Most states: " + mostStates);
      
      set = aModel.addState(state, mostStates); // will replace previous state with same value
      System.out.println("Removed states: " + set);
      System.out.println("new states: " + aModel.states());

      machine.transition(INITIALIZING);
      machine.transition(FAILED);
      dumpState();
      try {
         machine.transition(FINAL);
         // should not make it here
      }
      catch (Exception e) {
         System.out.println("*** Caught: " + e);
         // state should be FAILED
         dumpState();
      }
   }

   public static void dumpState(State state)
   {
      Set acceptable = machine.getModel().states();
      System.out.println(state + " accepts " + acceptable);
   }
   
   public static void dumpStates(Set set)
   {
      Iterator iter = set.iterator();
      while (iter.hasNext()) {
         dumpState((State)iter.next());
      }
   }
   
   public static void test(String name)
   {
      System.out.println("Testing " + name + "...");

      dumpStates(machine.getModel().states());

      // add some listeners
      machine.addChangeListener(new StateMachine.ChangeListener() {
            public void stateChanged(StateMachine.ChangeEvent event) {
               System.out.println("Event: " + event);
            }
         });
      
      dumpState();

      // try some valid state changes
      machine.transition(INITIALIZING);
      dumpState();
      machine.transition(INITIALIZED);
      dumpState();

      // now for an invalid state change
      try {
         machine.transition(NEW);
      }
      catch (IllegalStateException e) {
         System.out.println(e);
      }

      // now for an invalid when we are in a final state
      machine.transition(FAILED);
      dumpState();
      machine.transition(FINAL);
      dumpState();
      
      try {
         machine.transition(NEW);
      }
      catch (IllegalStateException e) {
         System.out.println(e);
      }

      System.out.println();
      System.out.println();
   }
   
   public static void dumpState()
   {
      System.out.print("Current state: ");
      dumpState(machine.getCurrentState());
   }
}
