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
package org.jboss.test.xb.builder.object.element.jbossxmlnsprefix.support;

import javax.xml.bind.annotation.XmlElement;

import org.jboss.xb.annotations.JBossXmlModelGroup;
import org.jboss.xb.annotations.JBossXmlNsPrefix;

/**
 * A GroupWithJBossXmlNsPrefixProperty.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
@JBossXmlModelGroup(name="group", propOrder={"applyToGroupTrueApplyToContentFalse", "applyToGroupFalseApplyToContentFalse",
      "applyToGroupTrueApplyToContentTrue", "applyToGroupFalseApplyToContentTrue"})
public class GroupWithJBossXmlNsPrefixProperty
{
   private Child1 applyToGroupTrueApplyToContentFalse;
   private Child2 applyToGroupFalseApplyToContentFalse;
   private Child3 applyToGroupTrueApplyToContentTrue;
   private Child4 applyToGroupFalseApplyToContentTrue;
   
   @XmlElement(name="group-true-content-false")
   @JBossXmlNsPrefix(prefix="child", applyToComponentQName=true, applyToComponentContent=false)
   public Child1 getApplyToGroupTrueApplyToContentFalse()
   {
      return applyToGroupTrueApplyToContentFalse;
   }
   public void setApplyToGroupTrueApplyToContentFalse(Child1 ApplyToGroupTrueApplyToContentFalse)
   {
      this.applyToGroupTrueApplyToContentFalse = ApplyToGroupTrueApplyToContentFalse;
   }
   
   @XmlElement(name="group-false-content-false")
   @JBossXmlNsPrefix(prefix="child", applyToComponentQName=false, applyToComponentContent=false)
   public Child2 getApplyToGroupFalseApplyToContentFalse()
   {
      return applyToGroupFalseApplyToContentFalse;
   }
   
   public void setApplyToGroupFalseApplyToContentFalse(Child2 applyToElementFalseApplyToTypeFalse)
   {
      this.applyToGroupFalseApplyToContentFalse = applyToElementFalseApplyToTypeFalse;
   }

   @XmlElement(name="group-true-content-true")
   @JBossXmlNsPrefix(prefix="child", applyToComponentQName=true, applyToComponentContent=true)
   public Child3 getApplyToGroupTrueApplyToContentTrue()
   {
      return applyToGroupTrueApplyToContentTrue;
   }
   
   public void setApplyToGroupTrueApplyToContentTrue(Child3 ApplyToGroupTrueApplyToContentTrue)
   {
      this.applyToGroupTrueApplyToContentTrue = ApplyToGroupTrueApplyToContentTrue;
   }

   @XmlElement(name="group-false-content-true")
   @JBossXmlNsPrefix(prefix="child", applyToComponentQName=false, applyToComponentContent=true)
   public Child4 getApplyToGroupFalseApplyToContentTrue()
   {
      return applyToGroupFalseApplyToContentTrue;
   }
   
   public void setApplyToGroupFalseApplyToContentTrue(Child4 ApplyToGroupFalseApplyToContentTrue)
   {
      this.applyToGroupFalseApplyToContentTrue = ApplyToGroupFalseApplyToContentTrue;
   }
   
   public static final class Child1 extends Child {}
   public static final class Child2 extends Child {}
   public static final class Child3 extends Child {}
   public static final class Child4 extends Child {}
}
