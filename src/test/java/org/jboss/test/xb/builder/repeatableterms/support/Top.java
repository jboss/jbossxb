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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * A Top.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
@XmlRootElement
@XmlType(propOrder={"items", "sequences", "choices"})
public class Top
{
   public String[] items;
   public Sequence[] sequences;
   public Choice[] choices;

   @XmlElement(name="item")
   public String[] getItems()
   {
      return items;
   }
   
   public void setItems(String[] items)
   {
      this.items = items;
   }
   
   public Sequence[] getSequences()
   {
      return sequences;
   }
   
   public void setSequences(Sequence[] sequences)
   {
      this.sequences = sequences;
   }

   public Choice[] getChoices()
   {
      return choices;
   }
   
   public void setChoices(Choice[] choices)
   {
      this.choices = choices;
   }
}
