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
package org.jboss.test.xml.unorderedsequence.support;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jboss.xb.annotations.JBossXmlConstants;
import org.jboss.xb.annotations.JBossXmlModelGroup;

/**
 * A RootWithMiscGroups.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
@XmlRootElement(name="root")
@XmlType(propOrder={"a", "b", "cde", "fg"})
public class RootWithMiscGroups
{
   private String a;
   private String b;
   private CDEChoice cde;
   private FGSequence fg;
   
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
   
   public CDEChoice getCde()
   {
      return cde;
   }
   
   public void setCde(CDEChoice cde)
   {
      this.cde = cde;
   }
   
   public FGSequence getFg()
   {
      return fg;
   }
   
   public void setFg(FGSequence fg)
   {
      this.fg = fg;
   }
   
   @JBossXmlModelGroup(name="cde", kind=JBossXmlConstants.MODEL_GROUP_CHOICE)
   public static class CDEChoice
   {
      private String c;
      private DESequence de;
      
      public String getC()
      {
         return c;
      }
      
      public void setC(String c)
      {
         this.c = c;
      }
      
      public DESequence getDe()
      {
         return de;
      }
      
      public void setDe(DESequence de)
      {
         this.de = de;
      }
   }
   
   @JBossXmlModelGroup(name="de", kind=JBossXmlConstants.MODEL_GROUP_SEQUENCE, propOrder={"d", "e"})
   public static class DESequence
   {
      private String d;
      private String e;
      
      public String getD()
      {
         return d;
      }
      
      public void setD(String d)
      {
         this.d = d;
      }
      
      public String getE()
      {
         return e;
      }
      
      public void setE(String e)
      {
         this.e = e;
      }
   }

   @JBossXmlModelGroup(name="fg", kind=JBossXmlConstants.MODEL_GROUP_SEQUENCE, propOrder={"f", "g"})
   public static class FGSequence
   {
      private String f;
      private String g;
      
      public String getF()
      {
         return f;
      }
      
      public void setF(String f)
      {
         this.f = f;
      }
      
      public String getG()
      {
         return g;
      }
      
      public void setG(String g)
      {
         this.g = g;
      }
   }
}
