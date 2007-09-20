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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jboss.test.xb.builder.object.mc.support.model.factory.GenericBeanFactoryMetaData;
import org.jboss.util.JBossObject;
import org.jboss.util.JBossStringBuilder;
import org.jboss.xb.annotations.JBossXmlSchema;

/**
 * An abstract kernel deployment.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 60497 $
 */
@JBossXmlSchema(namespace="urn:jboss:bean-deployer:2.0", elementFormDefault=XmlNsForm.QUALIFIED)
@XmlRootElement(name="deployment")
@XmlType(propOrder={"classLoader", "beanFactories"})
public class AbstractKernelDeployment extends JBossObject
   implements KernelDeployment, Serializable
{
   private static final long serialVersionUID = 1;

   /** The name of the deployment */
   protected String name;

   /** Whether it is installed */
   protected boolean installed;

   /** Is deployment scoped */
   protected Boolean scoped;

   /** The annotations */
   protected Set<AnnotationMetaData> annotations;

   /** The beans List<BeanMetaDataFactory> */
   protected List<BeanMetaDataFactory> beanFactories;

   /** The ClassLoader */
   protected ClassLoaderMetaData classLoader;

   /**
    * Create a new kernel deployment
    */
   public AbstractKernelDeployment()
   {
   }

   /**
    * Set the bean factories.
    *
    * @deprecated use setBeanFactories
    * @param beans a List<BeanMetaData>.
    */
   @SuppressWarnings("unchecked")
   public void setBeans(List beans)
   {
      this.beanFactories = beans;
      flushJBossObjectCache();
   }

   /**
    * Set the bean factories.
    * 
    * @param beanFactories a List<BeanMetaDataFactory>.
    */
   @XmlElements
   ({
      @XmlElement(name="bean", type=AbstractBeanMetaData.class),
      @XmlElement(name="beanfactory", type=GenericBeanFactoryMetaData.class)
   })
   @XmlAnyElement
   public void setBeanFactories(List<BeanMetaDataFactory> beanFactories)
   {
      this.beanFactories = beanFactories;
      flushJBossObjectCache();
   }

   public String getName()
   {
      return name;
   }

   @XmlAttribute
   public void setName(String name)
   {
      this.name = name;
      flushJBossObjectCache();
   }

   public boolean isInstalled()
   {
      return installed;
   }

   public void setInstalled(boolean installed)
   {
      this.installed = installed;
      flushJBossObjectCache();
   }

   public List<BeanMetaData> getBeans()
   {
      if (beanFactories == null || beanFactories.size() == 0)
         return null;
      List<BeanMetaData> result = new ArrayList<BeanMetaData>(beanFactories.size());
      for (BeanMetaDataFactory factory : beanFactories)
      {
         List<BeanMetaData> beans = factory.getBeans();
         // add all deployment annotations to bean's annotations
         if (annotations != null && annotations.isEmpty() == false)
         {
            for (BeanMetaData bmd : beans)
            {
               Set<AnnotationMetaData> annotationsBMD = bmd.getAnnotations();
               if (annotationsBMD == null)
               {
                  annotationsBMD = new HashSet<AnnotationMetaData>();
                  bmd.setAnnotations(annotationsBMD);
               }
               annotationsBMD.addAll(annotations);
            }
         }
         result.addAll(beans);
      }
      return result;
   }

   public Boolean isScoped()
   {
      return scoped;
   }

   public void setScoped(Boolean scoped)
   {
      this.scoped = scoped;
   }

   public Set<AnnotationMetaData> getAnnotations()
   {
      return annotations;
   }

   public void setAnnotations(Set<AnnotationMetaData> annotations)
   {
      this.annotations = annotations;
   }

   public List<BeanMetaDataFactory> getBeanFactories()
   {
      return beanFactories;
   }

   public ClassLoaderMetaData getClassLoader()
   {
      return classLoader;
   }

   /**
    * Set the classloader
    * 
    * @param classLoader the classloader metadata
    */
   @XmlElement(name="classloader", type=AbstractClassLoaderMetaData.class)
   public void setClassLoader(ClassLoaderMetaData classLoader)
   {
      this.classLoader = classLoader;
   }

   public void toString(JBossStringBuilder buffer)
   {
      buffer.append("name=").append(name);
      buffer.append(" installed=").append(installed);
      if (classLoader != null)
         buffer.append(" classLoader=").append(classLoader);
      if (beanFactories != null)
         buffer.append(" beanFactories=").append(beanFactories);
   }

   public void toShortString(JBossStringBuilder buffer)
   {
      buffer.append(name);
   }
}