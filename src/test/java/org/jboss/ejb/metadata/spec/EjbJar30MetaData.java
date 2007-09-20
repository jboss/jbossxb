/*
* JBoss, Home of Professional Open Source
* Copyright 2006, JBoss Inc., and individual contributors as indicated
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
package org.jboss.ejb.metadata.spec;

import org.jboss.javaee.metadata.spec.JavaEEMetaDataConstants;
import org.jboss.xb.annotations.JBossXmlSchema;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * EjbJar30MetaData.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
@XmlRootElement(name="ejb-jar", namespace=JavaEEMetaDataConstants.JAVAEE_NS)
@JBossXmlSchema(
      xmlns={@XmlNs(namespaceURI = JavaEEMetaDataConstants.JAVAEE_NS, prefix = "jee")},
      ignoreUnresolvedFieldOrClass=false,
      namespace=JavaEEMetaDataConstants.JAVAEE_NS,
      elementFormDefault=XmlNsForm.QUALIFIED)
@XmlType(name="ejb-jarType",
      namespace=JavaEEMetaDataConstants.JAVAEE_NS,
      propOrder={"descriptionGroup", "enterpriseBeans", "interceptors", "relationships", "assemblyDescriptor", "ejbClientJar"})
/*@XmlTypeImpls
({
   @XmlTypeImpl(name="fully-qualified-classType", startElementCreatesObject=false),
   @XmlTypeImpl(name="java-typeType", startElementCreatesObject=false),
   @XmlTypeImpl(name="java-identifierType", startElementCreatesObject=false),
   @XmlTypeImpl(name="true-falseType", startElementCreatesObject=false),
   @XmlTypeImpl(name="string", namespace=JavaEEMetaDataConstants.JAVAEE_NS, startElementCreatesObject=false),
   @XmlTypeImpl(name="xsdStringType", startElementCreatesObject=false),
   @XmlTypeImpl(name="env-entry-type-valuesType", startElementCreatesObject=false),
   @XmlTypeImpl(name="ejb-linkType", startElementCreatesObject=false),
   @XmlTypeImpl(name="jndi-nameType", startElementCreatesObject=false),
   @XmlTypeImpl(name="role-nameType", startElementCreatesObject=false),
   @XmlTypeImpl(name="message-destination-typeType", startElementCreatesObject=false),
   @XmlTypeImpl(name="message-destination-linkType", startElementCreatesObject=false),
})
*/public class EjbJar30MetaData extends EjbJar3xMetaData
{
   /** The serialVersionUID */
   private static final long serialVersionUID = 4822895045737616761L;

   /**
    * Create a new EjbJar30MetaData.
    */
   public EjbJar30MetaData()
   {
      // For serialization
   }
}
