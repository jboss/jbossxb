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
package org.jboss.xb.binding;

import org.apache.xerces.xs.XSTypeDefinition;

import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;

/**
 * An interface for content navigation. At the moment it has only one method to get child's content.
 * But it could also implement XPath navigation.
 *
 * @version <tt>$Revision$</tt>
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 */
public interface UnmarshallingContext
{
   /** Construct a QName from a value
    * @param value A value that is of the form [prefix:]localpart
    */
   QName resolveQName(String value);

   /**
    * @return  all the known namespace URIs
    */
   Iterator getNamespaceURIs();

   /**
    * @return  NamespaceContext instance
    */
   NamespaceContext getNamespaceContext();

   /** 
    * @return true if the text content passed to the setValue(...) method
    * is automatically trimmed (the default).
    */
   boolean isTrimTextContent();
   
   /**
    * Should the text content be automatically trimmed before setValue(...) is called.
    * @param trimTextContent
    */
   void setTrimTextContent(boolean trimTextContent);

   /**
    * Returns child's content.
    * todo consider deprecating this method
    * @param namespaceURI
    * @param qName
    * @return
    */
   String getChildContent(String namespaceURI, String qName);

   /**
    * @return current element's type definition or null if this info is not available
    */
   XSTypeDefinition getType();
}