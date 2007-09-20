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
package org.jboss.test.xb.builder.object.mc.support.model;

import java.io.Serializable;
import java.lang.annotation.Annotation;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import org.jboss.annotation.factory.AnnotationCreator;
import org.jboss.annotation.factory.ast.TokenMgrError;
import org.jboss.util.JBossObject;
import org.jboss.util.JBossStringBuilder;
import org.jboss.util.StringPropertyReplacer;

/**
 * Metadata for an annotation.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 60497 $
 */
@XmlType(propOrder={"annotation"})
public class AbstractAnnotationMetaData extends JBossObject
   implements AnnotationMetaData, Serializable
{
   private static final long serialVersionUID = 1L;

   public String annotation;

   protected Annotation ann;

   protected boolean replace = true;

   /**
    * Create a new annotation meta data
    */
   public AbstractAnnotationMetaData()
   {
      super();
   }

   public String getAnnotation()
   {
      return annotation;
   }

   @XmlValue
   public void setAnnotation(String annotation)
   {
      this.annotation = annotation;
   }

   public boolean isReplace()
   {
      return replace;
   }

   public void setReplace(boolean replace)
   {
      this.replace = replace;
   }

   @XmlTransient
   public Annotation getAnnotationInstance()
   {
      try
      {
         String annString = annotation;
         // TODO - JBMICROCONT-143 + any better way?
         if (replace)
         {
            annString = StringPropertyReplacer.replaceProperties(annString);
         }
         //FIXME [JBMICROCONT-99] [JBAOP-278] Use the loader for the bean?
         ann = (Annotation)AnnotationCreator.createAnnotation(annString, Thread.currentThread().getContextClassLoader());
      }
      catch (Exception e)
      {
         throw new RuntimeException("Error creating annotation for " + annotation, e);
      }
      catch(TokenMgrError e)
      {
         throw new RuntimeException("Error creating annotation for " + annotation, e);
      }

      return ann;
   }

   public void toString(JBossStringBuilder buffer)
   {
      if (ann == null)
         buffer.append("expr=").append(annotation);
      else
         buffer.append("expr=").append(ann);
   }

   public void toShortString(JBossStringBuilder buffer)
   {
      if (ann == null)
         buffer.append(annotation);
      else
         buffer.append(ann);
   }

   protected int getHashCode()
   {
      return annotation.hashCode();
   }

   public boolean equals(Object object)
   {
      if (object == null || object instanceof AbstractAnnotationMetaData == false)
         return false;

      AbstractAnnotationMetaData amd = (AbstractAnnotationMetaData)object;
      // this is what we probably want? - never saw duplicate annotation on a bean/prop/...
      return (replace == amd.replace) && annotation.equals(amd.annotation);
   }

}
