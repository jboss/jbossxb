/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.test.xb.builder.object.type.map.support;

import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlNsForm;

import org.jboss.xb.annotations.JBossXmlSchema;
import org.jboss.xb.annotations.JBossXmlMapKeyElement;
import org.jboss.xb.annotations.JBossXmlMapValueElement;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@XmlRootElement
@JBossXmlSchema(namespace="ns", elementFormDefault= XmlNsForm.QUALIFIED)
public class RootWrongValue
{
   private Map<KeyIface, ValueIface> tester;

   public Map<KeyIface, ValueIface> getTester()
   {
      return tester;
   }

   @JBossXmlMapKeyElement(name = "key")
   @JBossXmlMapValueElement(name = "value")
   public void setTester(Map<KeyIface, ValueIface> tester)
   {
      this.tester = tester;
   }
}