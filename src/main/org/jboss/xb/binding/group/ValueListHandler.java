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
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.jboss.util.Classes;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.sunday.unmarshalling.AttributeBinding;
import org.jboss.xb.binding.sunday.unmarshalling.AttributeHandler;
import org.jboss.xb.binding.sunday.unmarshalling.DefaultHandlers;
import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ModelGroupBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleHandler;
import org.jboss.xb.binding.sunday.unmarshalling.TermBinding;

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
         Map map = valueList.getNonRequiredValues();

         Collection values = map.values();
         if(values.isEmpty())
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

         Constructor bestMatch = null;
         int bestMatchArgsTotal = 0;
         Constructor[] ctors = cls.getConstructors();

         for(int i = 0; i < ctors.length; ++i)
         {
            Constructor ctor = ctors[i];
            Class[] types = ctor.getParameterTypes();

            if((types == null || types.length == 0) && bestMatch == null)
            {
               bestMatch = ctor;
               continue;
            }

            if(bestMatch != null && bestMatchArgsTotal < types.length)
            {
               int typeInd = 0;
               Iterator iter = values.iterator();
               while(typeInd < types.length && iter.hasNext())
               {
                  Class type = types[typeInd++];
                  if(type.isPrimitive())
                  {
                     type = Classes.getPrimitiveWrapper(type);
                  }

                  if(!type.isAssignableFrom(iter.next().getClass()))
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

         if(bestMatch == null)
         {
            StringBuffer buf = new StringBuffer();
            buf.append("Failed to find no-arg ctor or best-match ctor in ")
               .append(cls)
               .append(", property values:\n");
            int cnt = 0;
            for(Iterator i = values.iterator(); i.hasNext();)
            {
               Object o = i.next();
               buf.append(' ').append(++cnt).append(") ").append(o.getClass()).append(": ").append(o).append('\n');
            }
            throw new IllegalStateException(buf.toString());
         }

         Object o;
         if(bestMatchArgsTotal == values.size())
         {
            o = newInstance(bestMatch, values.toArray());
         }
         else
         {
            QName elementName = null;
            if(particle.getTerm() instanceof ElementBinding)
            {
               elementName = ((ElementBinding)particle.getTerm()).getQName();
            }

            Object[] args = new Object[bestMatchArgsTotal];
            int i = 0;
            Iterator iter = values.iterator();
            while(i < bestMatchArgsTotal)
            {
               args[i++] = iter.next();
            }

            o = newInstance(bestMatch, args);

            List bindings = valueList.getNonRequiredBindings();
            while(iter.hasNext())
            {
               Object value = iter.next();
               Object binding = bindings.get(i++);
               QName qName = null;
               if(binding instanceof ParticleBinding)
               {
                  ParticleBinding childParticle = (ParticleBinding)binding;
                  TermBinding term = childParticle.getTerm();
                  ParticleHandler handler;
                  if(term instanceof ElementBinding)
                  {
                     ElementBinding e = (ElementBinding)term;
                     qName = e.getQName();
                     handler = e.getType().getHandler();
                     if(handler == null)
                     {
                        handler = DefaultHandlers.ELEMENT_HANDLER;
                     }
                  }
                  else
                  {
                     handler = ((ModelGroupBinding)term).getHandler();
                  }

                  handler.setParent(o, value, qName, childParticle, particle);
               }
               else if(binding instanceof AttributeBinding)
               {
                  AttributeBinding attr = (AttributeBinding)binding;
                  AttributeHandler handler = attr.getHandler();
                  if(handler != null)
                  {
                     handler.attribute(elementName, attr.getQName(), attr, o, value);
                  }
                  else
                  {
                     throw new JBossXBRuntimeException("Attribute binding present but has no handler: element=" +
                        elementName +
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
               Arrays.asList(args)
            );
         }
      }
   };

   Object newInstance(ParticleBinding particle, ValueList valueList);
}
