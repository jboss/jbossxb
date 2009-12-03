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

import org.jboss.xb.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xb.binding.sunday.unmarshalling.ParticleBinding;

/**
 * A ElementPosition.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class ElementPosition extends AbstractPosition
{
   private ParticleBinding nonXsiParticle;
   private boolean ignoreCharacters;
   private StringBuffer textContent;
   private Boolean indentation;
   private boolean ignorableCharacters = true;

   public ElementPosition(QName qName, ParticleBinding particle)
   {
      super(qName, particle);
   }

   public boolean isElement()
   {
      return true;
   }

   public void flushIgnorableCharacters()
   {
      if(textContent == null)
         return;

      if(indentation == Boolean.TRUE || ignorableCharacters)
      {
         if(trace)
            log.trace("ignored characters: " + ((ElementBinding) particle.getTerm()).getQName() + " '" + textContent + "'");
         textContent = null;
         indentation = null;
      }
   }

   public void reset()
   {
      super.reset();
      
      if(textContent != null)
         textContent.setLength(0);
      
      indentation = null;
      ignorableCharacters = true;
      
      if(nonXsiParticle != null)
         particle = nonXsiParticle;
   }
   
   public ParticleBinding getNonXsiParticle()
   {
      return nonXsiParticle;
   }
   
   public void setNonXsiParticle(ParticleBinding nonXsiParticle)
   {
      this.nonXsiParticle = nonXsiParticle;
   }
   
   public boolean isIgnoreCharacters()
   {
      return ignoreCharacters;
   }
   
   public void setIgnoreCharacters(boolean ignoreCharacters)
   {
      this.ignoreCharacters = ignoreCharacters;
   }
   
   public StringBuffer getTextContent()
   {
      return this.textContent;
   }
   
   public void characters(char[] ch, int start, int length)
   {
      ElementBinding e = (ElementBinding) particle.getTerm();

      // collect characters only if they are allowed content
      if(e.getType().isTextContentAllowed())
      {
         if(indentation != Boolean.FALSE)
         {
            if(e.getType().isSimple())
            {
               // simple content is not analyzed
               indentation = Boolean.FALSE;
               ignorableCharacters = false;
            }
            else if(e.getSchema() != null && !e.getSchema().isIgnoreWhitespacesInMixedContent())
            {
               indentation = Boolean.FALSE;
               ignorableCharacters = false;
            }
            else
            {
               // the indentation is currently defined as whitespaces with next line characters
               // this should probably be externalized in the form of a filter or something
               for (int i = start; i < start + length; ++i)
               {
                  if(ch[i] == 0x0a)
                  {
                     indentation = Boolean.TRUE;
                  }
                  else if (!Character.isWhitespace(ch[i]))
                  {
                     indentation = Boolean.FALSE;
                     ignorableCharacters = false;
                     break;
                  }
               }
            }
         }
         
         if (textContent == null)
            textContent = new StringBuffer();
         textContent.append(ch, start, length);
      }
   }
}