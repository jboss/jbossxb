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

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jboss.xb.annotations.JBossXmlNsPrefix;
import org.jboss.xb.annotations.JBossXmlSchema;

/**
 * A Root.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
@XmlRootElement(name="root")
@JBossXmlSchema(namespace = "ns.root",
      xmlns={@XmlNs(namespaceURI = "ns.child", prefix = "child")},
      elementFormDefault=XmlNsForm.QUALIFIED)
@XmlType(propOrder={"child", "rootName"})
public class ApplyToChildrenFalse
{
   private String rootName;
   private Child child;
   
   public String getRootName()
   {
      return rootName;
   }
   
   public void setRootName(String rootName)
   {
      this.rootName = rootName;
   }
   
   @JBossXmlNsPrefix(prefix = "child", applyToChildren=false)
   public Child getChild()
   {
      return child;
   }
   
   public void setChild(Child child)
   {
      this.child = child;
   }
}
