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
package javax.xml.namespace;

import java.util.Iterator;

/** Interface for read only XML Namespace context processing.
 * 
 * An XML Namespace has the properties:
 * Namespace URI: Namespace name expressed as a URI to which the prefix is bound
 * prefix: syntactically, this is the part of the attribute name following the
 * XMLConstants.XMLNS_ATTRIBUTE ("xmlns") in the Namespace declaration
 * example: <element xmlns:prefix="http://Namespace-name-URI">
 * All get*(*) methods operate in the current scope for Namespace URI and prefix
 * resolution.
 * 
 * Note that a Namespace URI can be bound to multiple prefixes in the current
 * scope. This can occur when multiple XMLConstants.XMLNS_ATTRIBUTE ("xmlns")
 * Namespace declarations occur in the same Start-Tag and refer to the same
 * Namespace URI. e.g.
 * <element xmlns:prefix1="http://Namespace-name-URI"
 *    xmlns:prefix2="http://Namespace-name-URI">
 * 
 * This can also occur when the same Namespace URI is used in multiple
 * XMLConstants.XMLNS_ATTRIBUTE ("xmlns") Namespace declarations in the
 * logical parent element hierarchy. e.g.
 * <parent xmlns:prefix1="http://Namespace-name-URI">
 *    <child xmlns:prefix2="http://Namespace-name-URI">
 *    ...
 *    </child>
 * </parent>
 * 
 * A prefix can only be bound to a single Namespace URI in the current scope.
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
public interface NamespaceContext
{
	public String getNamespaceURI(String prefix);
	public String getPrefix(String namespaceURI);
	public Iterator getPrefixes(String namespaceURI);
}
