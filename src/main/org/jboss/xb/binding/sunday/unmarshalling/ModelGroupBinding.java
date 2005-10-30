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
package org.jboss.xb.binding.sunday.unmarshalling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Collection;
import javax.xml.namespace.QName;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.sunday.unmarshalling.impl.runtime.RtElementHandler;
import org.jboss.logging.Logger;
import org.xml.sax.Attributes;


/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public abstract class ModelGroupBinding
   extends TermBinding
   implements Cloneable
{
   protected static final Logger log = Logger.getLogger(ModelGroupBinding.class);

   protected boolean requiredParticle;
   protected ParticleHandler handler = RtElementHandler.INSTANCE;

   protected ModelGroupBinding(SchemaBinding schema)
   {
      super(schema);
   }

   public ParticleHandler getHandler()
   {
      return handler;
   }

   public void setHandler(ParticleHandler handler)
   {
      this.handler = handler;
   }

   public abstract ElementBinding getArrayItem();

   /**
    * Model group that is passed in as an argument to this method must be fully populated with
    * element, wildcard and child model group bindings.
    * @param particle
    */
   public void addParticle(ParticleBinding particle)
   {
      if(particle.isRequired())
      {
         requiredParticle = true;
      }
   }

   public abstract Collection getParticles();

   public boolean hasRequiredParticle()
   {
      return requiredParticle;
   }

   /**
    * This method is not actually used during parsing. It's here only for internal tests.
    *
    * @param qName an element name
    * @return true if the model group may start with the specified element
    */
   public boolean mayStartWith(QName qName)
   {
      return mayStartWith(qName, Collections.EMPTY_SET);
   }

   public abstract Cursor newCursor(ParticleBinding particle);

   public Object clone() throws CloneNotSupportedException
   {
      return super.clone();
   }

   // Protected

   protected abstract boolean mayStartWith(QName qName, Set set);

   public boolean isSkip()
   {
      return skip == null ? true : skip.booleanValue();
   }

   public boolean isModelGroup()
   {
      return true;
   }

   public boolean isWildcard()
   {
      return false;
   }

   // Inner

   public static abstract class Cursor
   {
      protected final ParticleBinding particle;

      protected Cursor(ParticleBinding particle)
      {
         this.particle = particle;
      }

      public ParticleBinding getParticle()
      {
         return particle;
      }

      public ModelGroupBinding getModelGroup()
      {
         return (ModelGroupBinding)particle.getTerm();
      }

      public abstract ParticleBinding getCurrentParticle();

      public abstract ElementBinding getElement();

      public List startElement(QName qName, Attributes attrs)
      {
         return startElement(qName, attrs, Collections.EMPTY_SET, Collections.EMPTY_LIST, true);
      }

      public ElementBinding getElement(QName qName, Attributes attrs)
      {
         return getElement(qName, attrs, Collections.EMPTY_SET);
      }

      public abstract void endElement(QName qName);

      public abstract int getOccurence();

      // Protected

      protected abstract List startElement(QName qName,
                                           Attributes atts,
                                           Set passedGroups,
                                           List groupStack,
                                           boolean required);

      protected abstract ElementBinding getElement(QName qName, Attributes atts, Set passedGroups);

      protected ElementBinding getElement(List group, QName qName, Attributes atts, Set passedGroups)
      {
         ElementBinding element = null;
         for(int i = 0; i < group.size(); ++i)
         {
            ParticleBinding nextParticle = (ParticleBinding)group.get(i);
            Object item = nextParticle.getTerm();
            if(item instanceof ElementBinding)
            {
               ElementBinding choice = (ElementBinding)item;
               if(qName.equals(choice.getQName()))
               {
                  element = choice;
                  break;
               }
            }
            else if(item instanceof ModelGroupBinding)
            {
               ModelGroupBinding modelGroup = (ModelGroupBinding)item;
               if(!passedGroups.contains(modelGroup))
               {
                  switch(passedGroups.size())
                  {
                     case 0:
                        passedGroups = Collections.singleton(this);
                        break;
                     case 1:
                        passedGroups = new HashSet(passedGroups);
                     default:
                        passedGroups.add(this);
                  }

                  ElementBinding e = modelGroup.newCursor(nextParticle).getElement(qName, atts, passedGroups);
                  if(e != null)
                  {
                     element = e;
                     if(!qName.equals(e.getQName()))
                     {
                        throw new JBossXBRuntimeException(
                           "There is a bug in ModelGroupBinding.Cursor.getElement(QName,Attributes) impl"
                        );
                     }
                     break;
                  }
               }
            }
            else if(item instanceof WildcardBinding)
            {
               WildcardBinding wildcard = (WildcardBinding)item;
               ElementBinding e = wildcard.getElement(qName, atts);
               if(e != null)
               {
                  element = e;
                  if(!qName.equals(e.getQName()))
                  {
                     throw new JBossXBRuntimeException(
                        "There is a bug in ModelGroupBinding.Cursor.getElement(QName,Attributes) impl"
                     );
                  }
                  break;
               }
            }
         }
         return element;
      }

      protected List addItem(List list, Object o)
      {
         switch(list.size())
         {
            case 0:
               list = Collections.singletonList(o);
               break;
            case 1:
               list = new ArrayList(list);
            default:
               list.add(o);
         }
         return list;
      }
   }
}
