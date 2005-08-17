/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding.group;

import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import org.jboss.xb.binding.JBossXBRuntimeException;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class ValueList
{
   private final int requiredTotal;
   private final Object[] requiredValues;
   private int state;
   private Map nonRequiredValues = Collections.EMPTY_MAP;
   private List nonRequiredBindings;

   ValueList(int requiredTotal)
   {
      this.requiredTotal = requiredTotal;
      requiredValues = new Object[requiredTotal];
   }

   void setRequiredValue(int index, int stateIncrement, Object value)
   {
      if(index >= requiredTotal)
      {
         throw new JBossXBRuntimeException(
            "Maximum argument index for this value list is " + requiredTotal + " but got " + index
         );
      }
      requiredValues[index] = value;
      state += stateIncrement;
   }

   Object getRequiredValue(int index)
   {
      if(index >= requiredTotal)
      {
         throw new JBossXBRuntimeException(
            "Maximum argument index for this value list is " + requiredTotal + " but got " + index
         );
      }
      return requiredValues[index];
   }

   int getState()
   {
      return state;
   }

   void setNonRequiredValue(QName qName, Object binding, Object value)
   {
      switch(nonRequiredValues.size())
      {
         case 0:
            nonRequiredValues = Collections.singletonMap(qName, value);
            nonRequiredBindings = new ArrayList();
            nonRequiredBindings.add(binding);
            break;
         case 1:
            nonRequiredValues = new LinkedHashMap(nonRequiredValues);
         default:
            nonRequiredValues.put(qName, value);
            nonRequiredBindings.add(binding);
      }
   }

   Object getNonRequiredValue(QName qName)
   {
      return nonRequiredValues.get(qName);
   }

   public List getRequiredValues()
   {
      return Arrays.asList(requiredValues);
   }

   public Map getNonRequiredValues()
   {
      return nonRequiredValues;
   }

   public List getNonRequiredBindings()
   {
      return nonRequiredBindings;
   }
}
