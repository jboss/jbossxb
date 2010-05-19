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
package org.jboss.test.xb.builder.object.type.jbossxmlmodelgroup.support;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * A ReusedPropertiesGroup.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
@XmlRootElement(name="main-root")
@XmlType(propOrder={"group1", "group2"})
public class RootWithTwoParticleGroups
{
   private GroupWrapper1 group1;
   private GroupWrapper2 group2;
   
   public GroupWrapper1 getGroup1()
   {
      return this.group1;
   }
   
   public void setGroup1(GroupWrapper1 group1)
   {
      this.group1 = group1;
   }

   public GroupWrapper2 getGroup2()
   {
      return this.group2;
   }
   
   public void setGroup2(GroupWrapper2 group2)
   {
      this.group2 = group2;
   }
   
   @XmlType(propOrder={"group", "e"})
   public static class GroupWrapper1
   {
      private AbstractChoice group;
      
      public AbstractChoice getGroup()
      {
         return group;
      }
      
      public void setGroup(AbstractChoice group)
      {
         this.group = group;
      }
      
      public String getE()
      {
         return "";
      }
      
      public void setE(String e)
      {
         
      }
   }

   @XmlType(propOrder={"group", "e"})
   public static class GroupWrapper2
   {
      private AbstractChoice group;
      
      public AbstractChoice getGroup()
      {
         return group;
      }
      
      public void setGroup(AbstractChoice group)
      {
         this.group = group;
      }

      public String getE()
      {
         return "";
      }
      
      public void setE(String e)
      {
         
      }
   }
}
