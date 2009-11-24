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
package org.jboss.test.xb.builder.repeatableterms.support;

import javax.xml.bind.annotation.XmlElement;

import org.jboss.xb.annotations.JBossXmlConstants;
import org.jboss.xb.annotations.JBossXmlModelGroup;

/**
 * A Choice.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
@JBossXmlModelGroup(name="choiceChoice", kind=JBossXmlConstants.MODEL_GROUP_CHOICE)
public class Choice
{
   private String[] choiceChoice1;
   private String[] choiceChoice2;

   @XmlElement(name="choiceChoice1")
   public String[] getChoiceChoice1()
   {
      return choiceChoice1;
   }
   
   public void setChoiceChoice1(String[] choiceChoice1)
   {
      this.choiceChoice1 = choiceChoice1;
   }
   
   @XmlElement(name="choiceChoice2")
   public String[] getChoiceChoice2()
   {
      return choiceChoice2;
   }
   
   public void setChoiceChoice2(String[] choiceChoice2)
   {
      this.choiceChoice2 = choiceChoice2;
   }
}
