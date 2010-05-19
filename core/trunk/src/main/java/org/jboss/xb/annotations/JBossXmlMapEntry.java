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
package org.jboss.xb.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * A JBossXmlMapEntry. Binds Java maps to XSD structures.
 * The annotation can be used on a type which implements java.util.Map
 * or property of type that implements java.util.Map.
 *
 * Some of the possible bindings:
 * 
 * <h1>sequence of key and value elements</h1>
 * <pre>
 * &lt;key>key1&lt;/key>&lt;value>value1&lt;/value>
 * &lt;key>key2&lt;/key>&lt;value>value2&lt;/value>
 * 
 * <tt>@JBossXmlMapKeyElement(name="key")</tt>
 * <tt>@JBossXmlMapValueElement(name="value")</tt>
 * public Map getMap(){ return map; }
 * </pre>
 *   
 * <h1>sequence of key and value elements wrapped inside entry element</h1>
 * <pre>
 * &lt;entry>
 *   &lt;key>key1&lt;/key>
 *   &lt;value>value1&lt;/value>
 * &lt;/entry>
 * &lt;entry>
 *   &lt;key>key2&lt;/key>
 *   &lt;value>value2&lt;/value>
 * &lt;/entry>
 * 
 * <tt>@JBossXmlMapEntry(name="entry")</tt>
 * <tt>@JBossXmlMapKeyElemenet(name="key")</tt>
 * <tt>@JBossXmlMapValueElement(name="value")</tt>
 * public Map getMap(){ return map; }
 * </pre>
 * 
 * <h1>key and value attributes in entry element</h1>
 * <pre>
 * &lt;entry key='key1' value='value1'/>
 * &lt;entry key='key2' value='value2'/>
 * 
 * <tt>@JBossXmlMapEntry(name="entry")</tt>
 * <tt>@JBossXmlMapKeyAttribute(name="key")</tt>
 * <tt>@JBossXmlMapValueAttribute(name="value")</tt>
 * public Map getMap(){ return map; }
 * </pre>
 * 
 * <h1>key is an attribute and the value is the value of entry element</h1>
 * <pre>
 * &lt;entry key='key1'>value1&lt;/entry>
 * &lt;entry key='key2'>value2&lt;/entry>
 * 
 * &#47;**
 *  * value binding is not specified, it's supposed to be the value of entry element
 *  *&#47;
 * <tt>@JBossXmlMapEntry(name="entry")</tt>
 * <tt>@JBossXmlMapKeyAttribute(name="key")</tt>
 * public Map getMap(){ return map; }
 * </pre>
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JBossXmlMapEntry
{
   String name() default JBossXmlConstants.DEFAULT;
   
   String namespace() default JBossXmlConstants.DEFAULT;
   
   Class type() default DEFAULT.class;
   
   class DEFAULT {};
}
