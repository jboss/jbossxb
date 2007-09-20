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
package org.jboss.ejb.metadata.jboss;

import org.jboss.javaee.metadata.spec.JavaEEMetaDataConstants;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jboss.xb.annotations.JBossXmlSchema;

/**
 * JBoss50MetaData.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 1.1 $
 */
@XmlRootElement(name="jboss", namespace=JavaEEMetaDataConstants.JBOSS_NS)
@JBossXmlSchema(
      xmlns={@XmlNs(namespaceURI = JavaEEMetaDataConstants.JAVAEE_NS, prefix = "jee")},
      ignoreUnresolvedFieldOrClass=false,
      namespace=JavaEEMetaDataConstants.JBOSS_NS,
      elementFormDefault=XmlNsForm.QUALIFIED)
@XmlType(name="jbossType", namespace=JavaEEMetaDataConstants.JBOSS_NS)
/*@XmlTypeImpls
({
   @XmlTypeImpl(name="true-falseType", startElementCreatesObject=false),
   @XmlTypeImpl(name="clusteredType", startElementCreatesObject=false),
   @XmlTypeImpl(name="string", namespace=JavaEEMetaDataConstants.JAVAEE_NS, startElementCreatesObject=false),
   @XmlTypeImpl(name="xsdStringType", startElementCreatesObject=false),
   @XmlTypeImpl(name="dependsType", startElementCreatesObject=false),
   @XmlTypeImpl(name="missing-method-permissions-excluded-modeType", startElementCreatesObject=false),
   @XmlTypeImpl(name="exception-on-rollbackType", startElementCreatesObject=false),
   @XmlTypeImpl(name="security-domainType", startElementCreatesObject=false),
   @XmlTypeImpl(name="jboss-security-roleType", impl=SecurityRoleMetaData.class),
   @XmlTypeImpl(name="jboss-message-destinationType", impl=MessageDestinationMetaData.class),
   @XmlTypeImpl(name="jboss-ejb-refType", impl=EJBReferenceMetaData.class),
   @XmlTypeImpl(name="jboss-ejb-local-refType", impl=EJBLocalReferenceMetaData.class),
   @XmlTypeImpl(name="jboss-resource-refType", impl=ResourceReferenceMetaData.class),
   @XmlTypeImpl(name="jboss-resource-env-refType", impl=ResourceEnvironmentReferenceMetaData.class),
   @XmlTypeImpl(name="jboss-message-destination-refType", impl=MessageDestinationReferenceMetaData.class),
   @XmlTypeImpl(name="jboss-security-identityType", impl=SecurityIdentityMetaData.class),
   @XmlTypeImpl(name="ejb-timeout-identityType", impl=SecurityIdentityMetaData.class),
   @XmlTypeImpl(name="ejb-linkType", startElementCreatesObject=false),
   @XmlTypeImpl(name="jndi-nameType", startElementCreatesObject=false),
   @XmlTypeImpl(name="mdb-passwdType", startElementCreatesObject=false),
   @XmlTypeImpl(name="mdb-subscription-idType", startElementCreatesObject=false),
   @XmlTypeImpl(name="concurrentType", startElementCreatesObject=false),
})
*/public class JBoss50MetaData extends JBossMetaData
{
   /** The serialVersionUID */
   private static final long serialVersionUID = 8741748087070507813L;

   /**
    * Create a new JBoss50MetaData.
    */
   public JBoss50MetaData()
   {
      // For serialization
   }
}
