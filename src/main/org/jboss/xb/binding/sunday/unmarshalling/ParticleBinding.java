/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
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
   private int maxOccurs = -1;
   private boolean maxOccursUnbounded;

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
}
