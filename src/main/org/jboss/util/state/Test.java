/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.util.state;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * ???
 *      
 * @version <tt>$Revision$</tt>
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class Test
{
   static StateMachine machine = null;
   static StateMachine.Model originalModel = null;

   static State NEW = new State(0, "NEW");
   static State INITIALIZING = new State(1, "INITIALIZING");
   static State INITIALIZED = new State(2, "INITIALIZED");
   static State STARTING = new State(3, "STARTING");
   static State STARTED = new StateAdapter(4, "STARTED") {
         public void stateChanged(StateMachine.ChangeEvent event) {
            startedGotEvent = true;
         }
      };
   static State FAILED = new StateAdapter(100, "FAILED") {
         public void stateChanged(StateMachine.ChangeEvent event) {
            failedGotEvent = true;
         }
      };
   static State FINAL = new AcceptableState(101, "FINAL") {
         public boolean isAcceptable(State state) {
            finalChecking = true;
            return false;
         }
      };

   static boolean startedGotEvent = false;
   static boolean failedGotEvent = false;
   static boolean finalChecking = false;

   public static StateMachine.Model makeClone()
   {
      StateMachine.Model model = (StateMachine.Model)originalModel.clone();
      Assert.assertTrue(model.equals(originalModel), "Clone was mutated");
      return model;
   }

   private static class Assert
   {
      public static int failed = 0;
      public static int total = 0;
      
      public static void assertTrue(boolean rv)
      {
         assertTrue(rv, null);
      }

      public static void assertTrue(boolean rv, String msg)
      {
         total++;
         if (!rv) failed++;
         
         if (!rv && msg != null) {
            System.out.println(rv + ": " + msg);
         }
         else if (!rv) {
            System.out.println(rv);
         }
      }
   }

   public static boolean canSerialize(java.io.Serializable obj)
   {
      try {
         org.jboss.util.Objects.copy(obj);
         return true;
      }
      catch (Exception e) {
         return false;
      }
   }
   
   public static void main(String[] args)
      throws Exception
   {
      try {
         doit();
      }
      catch (Exception e) {
         e.printStackTrace();
      }

      System.out.println("\n\nTotal: " + Assert.total);
      System.out.println("Failed: " + Assert.failed);
   }

   private static void doit() throws Exception
   {
      System.out.println("\nTesting data structure equality...");
      Assert.assertTrue(new DefaultStateMachineModel().equals(new DefaultStateMachineModel()));
      
      Set set;

      Set setA = new HashSet();
      setA.add(FAILED);
      
      Set setB = new HashSet();
      setB.add(FAILED);
      
      Set setC = new HashSet();
      setC.add(FAILED);
      setC.add(NEW);

      Set setD = new HashSet();
      setD.add(FAILED);
      setD.add(STARTED);
      setD.add(NEW);
      
      DefaultStateMachineModel modelA = new DefaultStateMachineModel();
      modelA.addState(NEW);
      DefaultStateMachineModel modelB = new DefaultStateMachineModel();
      modelB.addState(NEW);
      DefaultStateMachineModel modelC = new DefaultStateMachineModel();
      modelC.addState(FINAL);
      DefaultStateMachineModel modelD = new DefaultStateMachineModel();
      modelD.addState(FINAL);
      modelD.addState(NEW);
      modelD.setInitialState(FINAL);

      DefaultStateMachineModel modelA1 = new DefaultStateMachineModel();
      modelA1.addState(NEW, setA);
      DefaultStateMachineModel modelB1 = new DefaultStateMachineModel();
      modelB1.addState(NEW, setB);
      DefaultStateMachineModel modelC1 = new DefaultStateMachineModel();
      modelC1.addState(FINAL, setC);
      DefaultStateMachineModel modelD1 = new DefaultStateMachineModel();
      modelD1.addState(FINAL, setD);
      modelD1.addState(NEW);
      modelD1.setInitialState(FINAL);

      DefaultStateMachineModel modelA2 = (DefaultStateMachineModel)modelA1.clone();
      DefaultStateMachineModel modelB2 = (DefaultStateMachineModel)modelB1.clone();
      DefaultStateMachineModel modelC2 = (DefaultStateMachineModel)modelC1.clone();
      DefaultStateMachineModel modelD2 = (DefaultStateMachineModel)modelD1.clone();
      
      Assert.assertTrue(modelA.equals(modelA) == true, "Equality is broken 1");
      Assert.assertTrue(modelA.equals(modelB) == true, "Equality is broken 2");
      Assert.assertTrue(modelB.equals(modelA) == true, "Equality is broken 3");
      Assert.assertTrue(modelA.equals(modelC) != true, "Equality is broken 4");

      Assert.assertTrue(modelA1.equals(modelA1) == true, "Equality is broken 5");
      Assert.assertTrue(modelA1.equals(modelB1) == true, "Equality is broken 6");
      Assert.assertTrue(modelB1.equals(modelA1) == true, "Equality is broken 7");
      Assert.assertTrue(modelA1.equals(modelC1) != true, "Equality is broken 8");
      Assert.assertTrue(modelD1.equals(modelD1) == true, "Equality is broken 9");
      Assert.assertTrue(modelD1.equals(modelA1) != true, "Equality is broken a");

      Assert.assertTrue(modelA.equals(modelA1) != true, "Equality is broken b");
      Assert.assertTrue(modelB.equals(modelB1) != true, "Equality is broken c");
      Assert.assertTrue(modelC.equals(modelC1) != true, "Equality is broken d");
      Assert.assertTrue(modelD.equals(modelD1) != true, "Equality is broken e");

      Assert.assertTrue(modelA1.equals(modelA2) == true, "Equality is broken f");
      Assert.assertTrue(modelB1.equals(modelB2) == true, "Equality is broken g");
      Assert.assertTrue(modelC1.equals(modelC2) == true, "Equality is broken h");
      Assert.assertTrue(modelD1.equals(modelD2) == true, "Equality is broken h");

      modelD.removeState(NEW);

      System.out.println("\nTesting serializaion...");
      Assert.assertTrue(canSerialize(new State(0, "")), "State is not serializable");
      Assert.assertTrue(canSerialize(new StateAdapter(0, "")), "StateAdapter is not serializable");
      Assert.assertTrue(canSerialize(new AcceptableState(0, "") { public boolean isAcceptable(State state) { return false; } }), "AcceptableState is not serializable");
      Assert.assertTrue(canSerialize(new DefaultStateMachineModel()), "DefaultStateMachineModel is not serializable");
      
      System.out.println("\nSetting up model for tests...");

      DefaultStateMachineModel model = new DefaultStateMachineModel();
      
      Assert.assertTrue(model.equals(new DefaultStateMachineModel()) == true, "Equality is broken");
      Assert.assertTrue(model.equals((StateMachine.Model)model.clone()) == true, "Equality is broken");
   
      set = model.addState(NEW, INITIALIZING);
      Assert.assertTrue(((set == null) == true), "Should have returned a null replacement set");

      Assert.assertTrue(model.equals(new DefaultStateMachineModel()) != true, "Equality is broken");
      Assert.assertTrue(model.equals((StateMachine.Model)model.clone()) == true, "Equality is broken");
      
      model.addState(INITIALIZING, new State[] { INITIALIZED, FAILED });
      model.addState(INITIALIZED, new State[] { STARTING, FAILED });
      model.addState(STARTING, INITIALIZED);
      
      Assert.assertTrue(model.equals((StateMachine.Model)model.clone()) == true, "Cloned equality is broken 1");

      // test set replacement returns
      model.addState(STARTED, INITIALIZED); // invalid state
      set = model.addState(STARTED, STARTING); // this is what we want
      Assert.assertTrue(set.size() == 1 && set.contains(INITIALIZED), "State replacement is broken 2");

      Assert.assertTrue(model.equals((StateMachine.Model)model.clone()) == true, "Cloned equality is broken 3");
      
      model.addState(FINAL);

      Assert.assertTrue(model.equals((StateMachine.Model)model.clone()) == true, "Cloned equality is broken 4");
      
      Set mostStates = new HashSet(model.states());
      mostStates.remove(NEW); // new can only transition to INITIALIZED, not FAILED
      mostStates.remove(FAILED); // can not accept outselves
      model.addState(FAILED, mostStates);
      
      model.setInitialState(NEW);

      originalModel = (DefaultStateMachineModel)org.jboss.util.Objects.copy(model);
      System.out.println("Original model: " + originalModel);

      System.out.println("\nTesting clonability of model...");

      StateMachine.Model aModel;

      Assert.assertTrue(model.equals(makeClone()) == true, "Cloned equality is broken 5");

      aModel = (StateMachine.Model)model.clone();
      Assert.assertTrue(model.equals(aModel) == true);
      Assert.assertTrue(aModel.equals(model) == true);

      aModel.clear();

      Assert.assertTrue(model.equals(aModel) != true, "Cloned equality is broken 6");

      aModel = (StateMachine.Model)model.clone();
      Assert.assertTrue(model.equals(aModel) == true, "Cloned equality is broken 7");
      
      aModel.removeState(FINAL);
      Assert.assertTrue(model.equals(aModel) != true, "Cloned equality is broken 8");
      
      aModel = (StateMachine.Model)model.clone();
      Assert.assertTrue(model.equals(aModel) == true, "Cloned equality is broken 9");
      
      aModel.addState(new State(FINAL.getValue(), "NEW FINAL"));

      Assert.assertTrue(model.equals(aModel) == true, "Cloned equality is broken a");

      machine = new StateMachine(makeClone());
      System.out.println(machine);
      System.out.println();
      
      test("new machine");

      Assert.assertTrue(machine.isStateFinal(FINAL), "State FINAL should be final");
      Assert.assertTrue(!machine.isStateFinal(NEW), "State NEW should not be final");
      
      Assert.assertTrue(finalChecking, "Acceptable State broken");
      Assert.assertTrue(startedGotEvent, "ChangeListener broken");
      Assert.assertTrue(failedGotEvent, "ChangeListener broken");
      
      machine.reset();
      test("reset");

      /*
      machine = new StateMachine(makeClone(), true);
      test("chainable");

      System.out.println("\nTesting isAcceptable w/chaining...");
      
      machine = new StateMachine(makeClone(), true);
      Assert.assertTrue(machine.isAcceptable(INITIALIZED), "Chaining to valid state is broken");
      machine.transition(INITIALIZED);
      Assert.assertTrue(machine.getCurrentState().equals(INITIALIZED));
      Assert.assertTrue(!machine.isAcceptable(NEW), "Chaining to invalid state is broken");
      try {
         machine.transition(NEW);
         System.out.println(false);
      }
      catch (Exception e) {
         System.out.println(true);
      }
      Assert.assertTrue(machine.getCurrentState().equals(INITIALIZED), "State should have been INITIALIZED");
      Assert.assertTrue(!machine.isAcceptable(INITIALIZED), "Can transition to current state");
      try {
         machine.transition(INITIALIZED);
         Assert.assertTrue(false, "Invalid state change allowed");
      }
      catch (Exception e) {
         Assert.assertTrue(true);
      }
      */
      
      machine = new StateMachine(makeClone());
      // System.out.println("Prototype model: " + model);
      // System.out.println("Machine model: " + machine.getModel());
      test("model cloning");

      aModel = makeClone();
      aModel.removeState(FAILED);
      // System.out.println("Prototype model: " + model);

      machine = new StateMachine(aModel);
      try {
         test("model cloning with removal");
      }
      catch (IllegalStateException e) {
         Assert.assertTrue(e.getMessage().equals("State must be STARTING; cannot accept state: FAILED; state=INITIALIZED"));
      }

      machine = new StateMachine(makeClone());
      // System.out.println("Prototype model states: " + model.states());
      test("model cloning after removal");
      
      // test exception handling
      machine = new StateMachine(makeClone());

      // change listener
      machine.addChangeListener(new StateMachine.ChangeListener() {
            public void stateChanged(StateMachine.ChangeEvent event) {
               throw new RuntimeException("ChangeListener");
            }
         });

      try {
         machine.transition(INITIALIZING);
         Assert.assertTrue(false);
         // should not make it here
      }
      catch (RuntimeException e) {
         Assert.assertTrue(e.getMessage().equals("ChangeListener"), "Invalid message from change listener");
         Assert.assertTrue(machine.getCurrentState().equals(INITIALIZING), "State should be INITAILIZING");
      }

      machine = new StateMachine(makeClone());
      
      // acceptable state
      State state = new AcceptableState(100, "FAILED") {
            public boolean isAcceptable(State state) {
               throw new RuntimeException("Accetable");
            }
         };

      // System.out.println("New FAILED state: " + state + "(" + state.toIdentityString() + ")");
      
      aModel = machine.getModel();
      // System.out.println("Most states: " + mostStates);
      
      set = aModel.addState(state, mostStates); // will replace previous state with same value
      // System.out.println("Removed states: " + set);
      // System.out.println("new states: " + aModel.states());

      machine.transition(INITIALIZING);
      Assert.assertTrue(machine.getCurrentState().equals(INITIALIZING), "State should be INITALIAING");
      machine.transition(FAILED);
      Assert.assertTrue(machine.getCurrentState().equals(FAILED), "State should be FAILED");
      
      try {
         machine.transition(FINAL);
         // should not make it here
      }
      catch (Exception e) {
         Assert.assertTrue(e.getMessage().equals("Accetable"), "Invalid message from Acceptable");
         Assert.assertTrue(machine.getCurrentState().equals(FAILED), "State should be FAILED");
      }

      System.out.println("\nDone.");
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
      System.out.println("\nTesting " + name + "...");

      StateMachine.Model model = machine.getModel();
      // System.out.println("Using model: " + model);

      Assert.assertTrue(model.getInitialState().equals(NEW), "Initial state should have been NEW");
      Assert.assertTrue(machine.getCurrentState().equals(NEW), "State should have been NEW");
      
      // dumpStates(machine.getModel().states());

      // dumpState();

      // try some valid state changes
      machine.transition(INITIALIZING);
      Assert.assertTrue(machine.getCurrentState().equals(INITIALIZING), "State should have been INITIALIZING");
      machine.transition(INITIALIZED);
      Assert.assertTrue(machine.getCurrentState().equals(INITIALIZED), "State should have been INITIALIZED");

      // now for an invalid state change
      try {
         machine.transition(NEW);
         Assert.assertTrue(false, "Invalid state change allowed; can not trans from INITIALIZED to NEW");
      }
      catch (IllegalStateException e) {
         Assert.assertTrue(machine.getCurrentState().equals(INITIALIZED), "State should be INITIALIZED");
      }

      // now for an invalid when we are in a final state
      machine.transition(FAILED);
      Assert.assertTrue(machine.getCurrentState().equals(FAILED), "State should be FAILED");

      machine.transition(FINAL);
      Assert.assertTrue(machine.getCurrentState().equals(FINAL), "State should be FINAL");
      
      try {
         machine.transition(NEW);
         Assert.assertTrue(false, "Invalid state change allowed; can not trans from FINAL to NEW");
      }
      catch (IllegalStateException e) {
         Assert.assertTrue(machine.getCurrentState().equals(FINAL), "State should be FINAL");
      }
   }
   
   public static void dumpState()
   {
      System.out.print("Current state: ");
      dumpState(machine.getCurrentState());
   }
}
