/*
* JBoss, Home of Professional Open Source
* Copyright 2009, JBoss Inc., and individual contributors as indicated
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
package org.jboss.xb.binding.sunday.unmarshalling.position;

import javax.xml.namespace.QName;

import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.PositionStack;
import org.jboss.xb.binding.sunday.unmarshalling.RepeatableParticleHandler;
import org.jboss.xb.binding.sunday.unmarshalling.TypeBinding;
import org.xml.sax.Attributes;

/**
 * A Position.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public interface Position
{
   void setStack(PositionStack stack);

   ParticleBinding getParticle();

   void setParentType(TypeBinding parentType);
   
   Object getValue();
   
   void setValue(Object value);
   
   Object getRepeatableParticleValue();

   RepeatableParticleHandler getRepeatableHandler();
   
   boolean isEnded();
   
   Position getNext();
   
   boolean isElement();
   
   void reset();
   
   Position nextPosition(QName qName, Attributes attrs);

   void characters(char[] ch, int start, int length);
   
   Object initValue(Object parent, Attributes atts);
   
   ElementPosition startParticle(QName startName, Attributes atts);
   
   void endParticle();
   
   void startRepeatableParticle(Object parent);
   
   void endRepeatableParticle(Position parentPosition);
}
