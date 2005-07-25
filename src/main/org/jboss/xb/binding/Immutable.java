/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding;

import org.jboss.logging.Logger;

import java.util.List;
import java.lang.reflect.Constructor;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class Immutable
{
   private static final Logger log = Logger.getLogger(Immutable.class);

   public final Class cls;

   final List names = new java.util.ArrayList();

   final List values = new java.util.ArrayList();

   public Immutable(Class cls)
   {
      this.cls = cls;
      if(log.isTraceEnabled())
      {
         log.trace("created immutable container for " + cls);
      }
   }

   public void addChild(String localName, Object child)
   {
      if(!names.isEmpty() && names.get(names.size() - 1).equals(localName))
      {
         throw new IllegalStateException("Attempt to add duplicate element " +
            localName +
            ": prev value=" +
            values.get(values.size() - 1) +
            ", new value=" +
            child
         );
      }
      names.add(localName);
      values.add(child);

      if(log.isTraceEnabled())
      {
         log.trace("added child " + localName + " for " + cls + ": " + child);
      }
   }

   public Object getChild(String localName)
   {
      return names.isEmpty() ?
         null :
         (names.get(names.size() - 1).equals(localName) ? values.get(values.size() - 1) : null);
   }

   public Object[] getValues()
   {
      return values.toArray();
   }

   public Class[] getValueTypes()
   {
      Class[] types = new Class[values.size()];
      for(int i = 0; i < values.size(); ++i)
      {
         types[i] = values.get(i).getClass();
      }
      return types;
   }

   public Object newInstance()
   {
      Constructor ctor = null;
      Constructor[] ctors = cls.getConstructors();

      if(ctors == null || ctors.length == 0)
      {
         throw new JBossXBRuntimeException("The class has no declared constructors: " + cls);
      }

      for(int i = 0; i < ctors.length; ++i)
      {
         Class[] types = ctors[i].getParameterTypes();

         if(types == null || types.length == 0)
         {
            throw new IllegalStateException("Found no-arg constructor for immutable " + cls);
         }

         if(types.length == values.size())
         {
            ctor = ctors[i];

            int typeInd = 0;
            while(typeInd < types.length)
            {
               if(!types[typeInd].isAssignableFrom(values.get(typeInd++).getClass()))
               {
                  ctor = null;
                  break;
               }
            }

            if(ctor != null)
            {
               break;
            }
         }
      }

      if(ctor == null)
      {
         throw new IllegalStateException("No constructor in " + cls + " that would take arguments " + values);
      }

      try
      {
         return ctor.newInstance(values.toArray());
      }
      catch(Exception e)
      {
         throw new IllegalStateException("Failed to create immutable instance of " +
            cls +
            " using arguments: "
            + values + ": " + e.getMessage()
         );
      }
   }
}
