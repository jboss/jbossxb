/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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
package org.jboss.test.xb.builder.object.jbossxmlcollection.support;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.xb.annotations.JBossXmlCollection;


/**
 * A Root.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
@XmlRootElement
public class Root
{
   private List<String> defaultList;
   private List<?> unparameterizedList;
   private List<String> jbossXmlList;
   private List<?> unparameterizedJbossXmlList;
   
   public List<String> getDefaultList()
   {
      return defaultList;
   }
   
   public void setDefaultList(List<String> list)
   {
      this.defaultList = list;
   }
   
   @XmlElement(type=Integer.class)
   public List<?> getUnparameterizedList()
   {
      return unparameterizedList;
   }
   
   public void setUnparameterizedList(List<?> unparameterizedList)
   {
      this.unparameterizedList = unparameterizedList;
   }
   
   public List<String> getJbossXmlList()
   {
      return jbossXmlList;
   }
   
   @JBossXmlCollection(type=java.util.LinkedList.class)
   public void setJbossXmlList(List<String> list)
   {
      this.jbossXmlList = list;
   }

   @JBossXmlCollection(type=java.util.LinkedList.class)
   @XmlElement(type=Integer.class)
   public List<?> getUnparameterizedJbossXmlList()
   {
      return unparameterizedJbossXmlList;
   }
   
   public void setUnparameterizedJbossXmlList(List<?> unparameterizedList)
   {
      this.unparameterizedJbossXmlList = unparameterizedList;
   }
}
