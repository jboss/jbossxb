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
package org.jboss.xb.binding.metadata.unmarshalling;

import org.jboss.xb.binding.metadata.unmarshalling.impl.DocumentBindingFactoryImpl;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public abstract class DocumentBindingFactory
{
   public static DocumentBindingFactory newInstance()
   {
      return new DocumentBindingFactoryImpl();
   }

   public abstract DocumentBindingStack newDocumentBindingStack();

   public abstract DocumentBinding newDocumentBinding();

   public abstract NamespaceBinding bindNamespace(DocumentBinding doc, String namespaceUri, String javaPackage);

   public abstract TopElementBinding bindTopElement(NamespaceBinding ns, String elementName, Class javaClass);

   public abstract ElementBinding bindElement(BasicElementBinding parent,
                                              String namespaceUri,
                                              String elementName,
                                              String fieldName,
                                              Class javaType);

   public abstract AttributeBinding bindAttribute(BasicElementBinding parent,
                                                  String namespaceUri,
                                                  String attributeName,
                                                  String fieldName,
                                                  Class javaType);

   public abstract XmlValueBinding bindValue(XmlValueContainer container,
                                             String fieldName,
                                             Class javaType);
}
