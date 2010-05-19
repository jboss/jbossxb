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

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class ParticleBinding
{
   private TermBinding term;
   private int minOccurs = 1;
   private int maxOccurs = 1;
   private boolean maxOccursUnbounded;

   public ParticleBinding(TermBinding term, int minOccurs, int maxOccurs, boolean maxOccursUnbounded)
   {
      this.term = term;
      this.minOccurs = minOccurs;
      this.maxOccurs = maxOccurs;
      this.maxOccursUnbounded = maxOccursUnbounded;
   }

   public ParticleBinding(TermBinding term)
   {
      this.term = term;
   }

   public int getMinOccurs()
   {
      return minOccurs;
   }

   public int getMaxOccurs()
   {
      return maxOccurs;
   }

   public boolean getMaxOccursUnbounded()
   {
      return maxOccursUnbounded;
   }

   public void setMinOccurs(int minOccurs)
   {
      this.minOccurs = minOccurs;
   }

   public void setMaxOccurs(int maxOccurs)
   {
      this.maxOccurs = maxOccurs;
   }

   public void setMaxOccursUnbounded(boolean maxOccursUnbounded)
   {
      this.maxOccursUnbounded = maxOccursUnbounded;
   }

   public TermBinding getTerm()
   {
      return term;
   }

   public void setTerm(TermBinding term)
   {
      this.term = term;
   }

   public boolean isRepeatable()
   {
      return maxOccursUnbounded || maxOccurs > 1 || minOccurs > 1;
   }

   public boolean isRequired()
   {
      return minOccurs > 0 && (!term.isModelGroup() || ((ModelGroupBinding)term).hasRequiredParticle());
   }

   public boolean isOccurrenceAllowed(int occurrence)
   {
      return maxOccursUnbounded || occurrence <= maxOccurs;
   }
   
   public String toString()
   {
      return "[" + term.toString() + ", minOccurs=" + minOccurs +
      ", maxOccurs=" + (maxOccursUnbounded ? "unbounded" : String.valueOf(maxOccurs)) + "]";
   }
}
