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
package org.jboss.test.xml;

import java.util.Arrays;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBindingResolver;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: $</tt>
 */
public class RepeatableTermsUnitTestCase
   extends AbstractJBossXBTest
{  
   public RepeatableTermsUnitTestCase(String name)
   {
      super(name);
   }

   protected void configureLogging()
   {
      //enableTrace("org.jboss.xb.binding.sunday");
      //enableTrace("org.jboss.xb.binding.sunday.unmarshalling.SequenceBinding");
      //enableTrace("org.jboss.xb.binding.sunday.unmarshalling.ChoiceBinding");
   }

   public void testUnmarshal1() throws Exception
   {
      Object o = unmarshal();

      assertNotNull(o);
      assertTrue(o instanceof Top);
      Top top = (Top)o;

      assertNotNull(top.item);
      assertEquals(3, top.item.length);
      assertEquals(new String[]{"item1", "item2", "item3"}, top.item);

      assertNotNull(top.sequence);
      //assertEquals(5, top.sequence.length);
      assertEquals(
         new Sequence[]
         {
            new Sequence("sequenceChoice1_1", null),
            new Sequence(null, "sequenceChoice2_1"),
            new Sequence("sequenceChoice1_2", null),
            new Sequence("sequenceChoice1_3", null),
            new Sequence(null, "sequenceChoice2_2")
         },
         top.sequence
      );

      assertNotNull(top.choice);
      assertEquals(3, top.choice.length);
      assertEquals(
         new Choice[]
         {
            new Choice(new String[]{"choiceChoice1_1", "choiceChoice1_2"}, null),
            new Choice(null, new String[]{"choiceChoice2_1", "choiceChoice2_2"}),
            new Choice(new String[]{"choiceChoice1_3", "choiceChoice1_4"}, null),
         },
         top.choice
      );
   }

   public void testUnmarshal2() throws Exception
   {
      Object o = unmarshal();
      assertNotNull(o);
      assertTrue(o instanceof Top);
      Top top = (Top)o;

      assertNull(top.item);
      assertNull(top.choice);
      assertNull(top.sequence);
      
      assertEquals("item1", top.item1);
      assertEquals("item2", top.item2);
   }
   
   private Object unmarshal() throws Exception
   {
      String testXsd = findXML(rootName + "_" + getName() + ".xsd");
      SchemaBinding schema = XsdBinder.bind(testXsd, (SchemaBindingResolver)null);
      schema.setIgnoreUnresolvedFieldOrClass(false);

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      return unmarshaller.unmarshal(findXML(rootName + "_" + getName() + ".xml"), schema);
   }

   // Inner

   public static final class Top
   {
      public String[] item;
      public Sequence[] sequence;
      public Choice[] choice;

      public String item1;
      public String item2;
      
      public String toString()
      {
         return "[top item=" + (item == null ? null : Arrays.asList(item)) +
            " sequence=" + (sequence == null ? null : Arrays.asList(sequence)) +
            " choice=" + (choice == null ? null : Arrays.asList(choice)) +
            " item1=" + item1 + " item2=" + item2 + "]";
      }
   }

   public static final class Sequence
   {
      public String sequenceChoice1;
      public String sequenceChoice2;

      public Sequence()
      {
      }

      public Sequence(String sequenceChoice1, String sequenceChoice2)
      {
         this.sequenceChoice1 = sequenceChoice1;
         this.sequenceChoice2 = sequenceChoice2;
      }

      public String toString()
      {
         return "[" + sequenceChoice1 + " " + sequenceChoice2 + "]";
      }

      public boolean equals(Object o)
      {
         if(this == o)
         {
            return true;
         }
         if(!(o instanceof Sequence))
         {
            return false;
         }

         final Sequence sequence = (Sequence)o;

         if(sequenceChoice1 != null ? !sequenceChoice1.equals(sequence.sequenceChoice1) : sequence.sequenceChoice1 != null)
         {
            return false;
         }
         if(sequenceChoice2 != null ? !sequenceChoice2.equals(sequence.sequenceChoice2) : sequence.sequenceChoice2 != null)
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         int result;
         result = (sequenceChoice1 != null ? sequenceChoice1.hashCode() : 0);
         result = 29 * result + (sequenceChoice2 != null ? sequenceChoice2.hashCode() : 0);
         return result;
      }
   }

   public static final class Choice
   {
      public String[] choiceChoice1;
      public String[] choiceChoice2;

      public Choice()
      {
      }

      public Choice(String[] choiceChoice1, String[] choiceChoice2)
      {
         this.choiceChoice1 = choiceChoice1;
         this.choiceChoice2 = choiceChoice2;
      }

      public String toString()
      {
         return "[" +
            (choiceChoice1 == null ? null : Arrays.asList(choiceChoice1)) +
            " " + (choiceChoice2 == null ? null : Arrays.asList(choiceChoice2)) + "]";
      }

      public boolean equals(Object o)
      {
         if(this == o)
         {
            return true;
         }
         if(!(o instanceof Choice))
         {
            return false;
         }

         final Choice choice = (Choice)o;

         if(!Arrays.equals(choiceChoice1, choice.choiceChoice1))
         {
            return false;
         }
         if(!Arrays.equals(choiceChoice2, choice.choiceChoice2))
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         return 0;
      }
   }
}
