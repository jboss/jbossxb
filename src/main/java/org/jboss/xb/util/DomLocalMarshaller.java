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
package org.jboss.xb.util;

import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.MarshallingContext;
import org.jboss.xb.binding.ObjectLocalMarshaller;
import org.w3c.dom.Element;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;


/**
 * ObjectLocalMarshaller that marshals org.w3c.dom.Element.
 * 
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 46112 $</tt>
 */
public class DomLocalMarshaller
   implements ObjectLocalMarshaller
{
   public static final DomLocalMarshaller INSTANCE = new DomLocalMarshaller();
   
   public void marshal(MarshallingContext ctx, Object o)
   {
      if(!(o instanceof Element))
      {
         throw new JBossXBRuntimeException("The argument must be an instance of " + Element.class + ": arg=" + o);
      }
      
      Element e = (Element)o;
      
      ContentHandler ch = ctx.getContentHandler();
      try
      {
         Dom2Sax.dom2sax(e, ch);
      }
      catch (SAXException e1)
      {
         throw new JBossXBRuntimeException("Failed to SAX the DOM");
      }
   }
}
