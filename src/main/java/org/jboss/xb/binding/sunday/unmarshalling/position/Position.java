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

import org.jboss.xb.binding.NamespaceRegistry;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleHandler;
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
   QName getQName();
   
   ParticleBinding getParticle();
   
   void setParticle(ParticleBinding particle);
   
   ParticleHandler getHandler();
   
   void setHandler(ParticleHandler handler);
   
   TypeBinding getParentType();
   
   void setParentType(TypeBinding parentType);
   
   Object getValue();
   
   void setValue(Object value);
   
   Object getRepeatableParticleValue();
   
   void setRepeatableParticleValue(Object repeatableParticleValue);
   
   RepeatableParticleHandler getRepeatableHandler();
   
   void setRepeatableHandler(RepeatableParticleHandler repeatableHandler);
   
   boolean isEnded();
   
   void setEnded(boolean ended);
   
   Position getNext();
   
   void setNext(Position next);
   
   boolean isElement();
   
   boolean isModelGroup();
   
   void reset();
   
   Position startElement(QName qName, Attributes attrs);

   Object startParticle(Object parent, Attributes atts, NamespaceRegistry nsRegistry);
   
   void endParticle();
   
   boolean repeatTerm(QName qName, Attributes atts);
   
   ParticleBinding getCurrentParticle();
   
   void setCurrentParticle(ParticleBinding currentParticle);
   
   void flushIgnorableCharacters();
   
   void characters(char[] ch, int start, int length);
}
