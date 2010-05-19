/*
* JBoss, Home of Professional Open Source
* Copyright 2009, JBoss Inc., and individual contributors as indicated
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
package org.jboss.test.xml.unorderedsequence.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jboss.xb.annotations.JBossXmlConstants;
import org.jboss.xb.annotations.JBossXmlModelGroup;
import org.jboss.xb.annotations.JBossXmlType;

/**
 * A UnorderedOrderedMix.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
@XmlRootElement(name="root")
@JBossXmlType(modelGroup=JBossXmlConstants.MODEL_GROUP_UNORDERED_SEQUENCE)
public class UnorderedOrderedMix
{
   private List<String> strings = new ArrayList<String>();
   private List<SomeSequence> someSequences = new ArrayList<SomeSequence>();
   private List<String> someChoices = new ArrayList<String>();
   private List<SomeBean> someBeans = new ArrayList<SomeBean>();   
   
   @XmlElement(name="string")
   public List<String> getStrings()
   {
      return strings;
   }
   
   public void setStrings(List<String> strings)
   {
      throw new UnsupportedOperationException();
   }
   
   public List<SomeSequence> getSequences()
   {
      return this.someSequences;
   }
   
   public void setSequences(List<SomeSequence> sequences)
   {
      throw new UnsupportedOperationException();
   }
   
   @XmlElements({
      @XmlElement(name="choice1", type=String.class),
      @XmlElement(name="choice2", type=String.class)})
   public List<String> getChoices()
   {
      return someChoices;
   }

   public void setChoices(List<String> choices)
   {
      throw new UnsupportedOperationException();
   }
   
   @XmlElement(name="bean")
   public List<SomeBean> getBeans()
   {
      return someBeans;
   }
   
   public void setBeans(List<SomeBean> beans)
   {
      throw new UnsupportedOperationException();
   }
   
   @JBossXmlModelGroup(name="some-sequence", propOrder={"a", "b", "c"}, kind=JBossXmlConstants.MODEL_GROUP_SEQUENCE)
   public static class SomeSequence
   {
      private String a;
      private String b;
      private List<String> c = new ArrayList<String>();
      
      public SomeSequence()
      {
      }
      
      public SomeSequence(String a, String b)
      {
         this.a = a;
         this.b = b;
      }
      
      public String getA()
      {
         return a;
      }
      
      public void setA(String a)
      {
         this.a = a;
      }
      
      public String getB()
      {
         return b;
      }
      
      public void setB(String b)
      {
         this.b = b;
      }

      public List<String> getC()
      {
         return Collections.unmodifiableList(c);
      }
      
      public void setC(List<String> c)
      {
         this.c = c;
      }
   }
   
   @XmlType()
   @JBossXmlType(modelGroup=JBossXmlConstants.MODEL_GROUP_SEQUENCE)
   public static class SomeBean
   {
      private List<String> strings;
   
      @XmlElement(name="string")
      public List<String> getStrings()
      {
         return Collections.unmodifiableList(strings);
      }
      
      public void setStrings(List<String> strings)
      {
         this.strings = strings;
      }
   }
}
