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
package org.jboss.test.xml.sandbox;

import java.util.Collection;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 37406 $</tt>
 */
public class Root
{
   private Collection choiceCollection1;
   private Collection choice2;
   private Collection choice3;

   public Collection getChoice3()
   {
      return choice3;
   }

   public void setChoice3(Collection choice3)
   {
      this.choice3 = choice3;
   }

   public Collection getChoiceCollection1()
   {
      return choiceCollection1;
   }

   public void setChoiceCollection1(Collection choiceCollection1)
   {
      this.choiceCollection1 = choiceCollection1;
   }

   public Collection getChoice2()
   {
      return choice2;
   }

   public void setChoice2(Collection choice2)
   {
      this.choice2 = choice2;
   }

   public boolean equals(Object o)
   {
      if(this == o)
      {
         return true;
      }
      if(!(o instanceof Root))
      {
         return false;
      }

      final Root root = (Root)o;

      if(choice2 != null ? !choice2.equals(root.choice2) : root.choice2 != null)
      {
         return false;
      }
      if(choice3 != null ? !choice3.equals(root.choice3) : root.choice3 != null)
      {
         return false;
      }
      if(choiceCollection1 != null ? !choiceCollection1.equals(root.choiceCollection1) : root.choiceCollection1 != null)
      {
         return false;
      }

      return true;
   }

   public int hashCode()
   {
      int result;
      result = (choiceCollection1 != null ? choiceCollection1.hashCode() : 0);
      result = 29 * result + (choice2 != null ? choice2.hashCode() : 0);
      result = 29 * result + (choice3 != null ? choice3.hashCode() : 0);
      return result;
   }

   public String toString()
   {
      return "[choiceCollection1=" + choiceCollection1 + ", choice2=" + choice2 + ", choice3=" + choice3 + "]";
   }
}
