/*
  * JBoss, Home of Professional Open Source
  * Copyright 2005, JBoss Inc., and individual contributors as indicated
  * by the @authors tag. See the copyright.txt in the distribution for a
  * full listing of individual contributors.
  *
  * This is free software; you can redistribute it and/or modify it
  * under the terms of the GNU Lesser General Public License as
  * published by the Free Software Foundation; either version 2.1 of
  * the License, or (at your option) any later version.
  *
  * This software is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  * Lesser General Public License for more details.
  *
  * You should have received a copy of the GNU Lesser General Public
  * License along with this software; if not, write to the Free
  * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */
package org.jboss.xb.binding.group;

import java.lang.reflect.Constructor;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jboss.util.Classes;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.sunday.unmarshalling.AttributeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.AttributeHandler;
import org.jboss.xb.binding.sunday.unmarshalling.CharactersHandler;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleHandler;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface ValueListHandler
{
   ValueListHandler IMMUTABLE = new ValueListHandler()
   {
      public Object newInstance(ParticleBinding particle, ValueList valueList)
      {
         Class cls = valueList.getTargetClass();
         Map map = valueList.getNonRequiredValues();

         Collection values = map.values();
         if(values.isEmpty())
         {
            throw new JBossXBRuntimeException("Value list does not contain non-required values.");
         }

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

            if(types.length == map.size())
            {
               ctor = ctors[i];

               int typeInd = 0;
               Iterator iter = values.iterator();
               while(iter.hasNext())
               {
                  Class type = types[typeInd++];
                  if(type.isPrimitive())
                  {
                     type = Classes.getPrimitiveWrapper(type);
                  }

                  if(!type.isAssignableFrom(iter.next().getClass()))
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
            StringBuffer buf = new StringBuffer();
            buf.append("There is no ctor in ")
               .append(cls)
               .append(" that would take the following arguments:\n");
            int cnt = 0;
            for(Iterator i = values.iterator(); i.hasNext();)
            {
               Object o = i.next();
               buf.append(' ').append(++cnt).append(") ").append(o.getClass()).append(": ").append(o).append('\n');
            }
            throw new IllegalStateException(buf.toString());
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
   };

   ValueListHandler NON_DEFAULT_CTOR = new ValueListHandler()
   {
      public Object newInstance(ParticleBinding particle, ValueList valueList)
      {
         Class cls = valueList.getTargetClass();
         int size = valueList.size();

         if(size == 0)
         {
            try
            {
               return newInstance(cls.getConstructor(null), null);
            }
            catch(NoSuchMethodException e)
            {
               throw new JBossXBRuntimeException(
                  "Value list does not contain non-required values and there is no no-arg ctor in " + cls
               );
            }
         }

         Constructor ctor = matchBestCtor(cls, valueList);

         if(ctor == null)
         {
            StringBuffer buf = new StringBuffer();
            buf.append("Failed to find no-arg ctor or best-match ctor in ")
               .append(cls)
               .append(", property values:\n");
            int cnt = 0;
            for(int i = 0; i < size; ++i)
            {
               Object o = valueList.getValue(i).value;
               buf.append(' ').append(++cnt).append(") ").append(o).append('\n');
            }
            throw new JBossXBRuntimeException(buf.toString());
         }

         Object o;
         int argsTotal = ctor.getParameterTypes().length;
         if(argsTotal == size)
         {
            Object[] args = getArgs(ctor, valueList);
            o = newInstance(ctor, args);
         }
         else
         {
            Object[] args = getArgs(ctor, valueList);
            o = newInstance(ctor, args);

            int i = argsTotal;
            while(i < size)
            {
               ValueList.NonRequiredValue valueEntry = valueList.getValue(i++);
               Object binding = valueEntry.binding;
               if(binding instanceof ParticleBinding)
               {
                  Object handler = valueEntry.handler;
                  ParticleBinding childParticle = (ParticleBinding)binding;
                  if(handler instanceof ParticleHandler)
                  {
                     ParticleHandler pHandler = (ParticleHandler)handler;
                     if(childParticle.isRepeatable())
                     {
                        List list = (List)valueEntry.value;
                        for(int listInd = 0; listInd < list.size(); ++listInd)
                        {
                           pHandler.setParent(o, list.get(listInd), valueEntry.qName, childParticle, particle);
                        }
                     }
                     else
                     {
                        pHandler.setParent(o, valueEntry.value, valueEntry.qName, childParticle, particle);
                     }
                  }
                  else
                  {
                     ((CharactersHandler)handler).setValue(valueEntry.qName,
                        (ElementBinding)childParticle.getTerm(),
                        o,
                        valueEntry.value
                     );
                  }
               }
               else if(binding instanceof AttributeBinding)
               {
                  AttributeBinding attr = (AttributeBinding)binding;
                  AttributeHandler handler = attr.getHandler();
                  if(handler != null)
                  {
                     handler.attribute(valueEntry.qName, attr.getQName(), attr, o, valueEntry.value);
                  }
                  else
                  {
                     throw new JBossXBRuntimeException("Attribute binding present but has no handler: element=" +
                        valueEntry.qName +
                        ", attrinute=" +
                        attr.getQName()
                     );
                  }
               }
               else
               {
                  throw new JBossXBRuntimeException("Unexpected binding type: " + binding);
               }
            }
         }

         return o;
      }

      private Constructor matchBestCtor(Class cls, ValueList valueList)
      {
         Constructor bestMatch = null;
         int bestMatchArgsTotal = 0;
         Constructor[] ctors = cls.getConstructors();
         int size = valueList.size();

         for(int i = 0; i < ctors.length; ++i)
         {
            Constructor ctor = ctors[i];
            Class[] types = ctor.getParameterTypes();

            if((types == null || types.length == 0) && bestMatch == null)
            {
               bestMatch = ctor;
               continue;
            }

            if(bestMatchArgsTotal <= types.length)
            {
               int typeInd = 0;
               for(int valueInd = 0; typeInd < types.length && valueInd < size; ++typeInd, ++valueInd)
               {
                  Class type = types[typeInd];
                  if(type.isPrimitive())
                  {
                     type = Classes.getPrimitiveWrapper(type);
                  }

                  ValueList.NonRequiredValue valueEntry = valueList.getValue(valueInd);
                  Object value = valueEntry.value;
                  if(value != null &&
                     !(type.isAssignableFrom(value.getClass()) ||
                     // if particle is repeatable and the type is array of a specific collection
                     // then we assume we can convert the arg later at creation time
                     // todo this code should be smarter
                     valueEntry.binding instanceof ParticleBinding &&
                     ((ParticleBinding)valueEntry.binding).isRepeatable() &&
                     type.isArray()
                     ))
                  {
                     break;
                  }

                  if(bestMatchArgsTotal == types.length &&
                     !bestMatch.getParameterTypes()[typeInd].isAssignableFrom(type))
                  {
                     break;
                  }
               }

               if(typeInd == types.length)
               {
                  bestMatch = ctor;
                  bestMatchArgsTotal = types.length;
               }
            }
         }
         return bestMatch;
      }

      private Object newInstance(Constructor bestMatch, Object[] args)
      {
         try
         {
            return bestMatch.newInstance(args);
         }
         catch(Exception e)
         {
            throw new JBossXBRuntimeException("Failed to create an instance of " +
               bestMatch.getDeclaringClass() +
               " using the following ctor arguments " +
               Arrays.asList(args), e
            );
         }
      }

      private Object[] getArgs(Constructor ctor, ValueList valueList)
      {
         Class[] types = ctor.getParameterTypes();
         Object[] args = new Object[types.length];
         for(int i = 0; i < types.length; ++i)
         {
            ValueList.NonRequiredValue valueEntry = valueList.getValue(i);
            Object arg = valueEntry.value;
            if(valueEntry.value != null && !types[i].isAssignableFrom(arg.getClass()))
            {
               // if type is array then convert collection to array
               // todo this part should be smarter about collections
               if(types[i].isArray() && Collection.class.isAssignableFrom(arg.getClass()))
               {
                  Collection col = (Collection)arg;
                  arg = Array.newInstance(types[i].getComponentType(), col.size());
                  int arrInd = 0;
                  for(Iterator iter = col.iterator(); iter.hasNext();)
                  {
                     Array.set(arg, arrInd++, iter.next());
                  }
               }
            }
            args[i] = arg;
         }
         return args;
      }
   };

   Object newInstance(ParticleBinding particle, ValueList valueList);
}
