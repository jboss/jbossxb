/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata.unmarshalling.impl;

import org.jboss.xml.binding.metadata.unmarshalling.BasicElementBinding;
import org.jboss.xml.binding.metadata.unmarshalling.ElementBinding;
import org.jboss.xml.binding.metadata.unmarshalling.AttributeBinding;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public abstract class DelegatingBasicElementBinding
   implements BasicElementBinding
{
   private final QName elementName;
   private final Map children = new HashMap();
   protected final List delegates = new ArrayList();

   public DelegatingBasicElementBinding(BasicElementBinding delegate)
   {
      this.elementName = delegate.getElementName();
      delegates.add(delegate);
   }

   void addDelegate(BasicElementBinding delegate)
   {
      delegates.add(delegate);
   }

   DelegatingElementBinding bindElement(QName elementName, String fieldName, Class javaType)
   {
      ElementBinding child = new ElementBindingImpl(elementName, javaType, getJavaType(), fieldName);
      DelegatingElementBinding delegatingChild = (DelegatingElementBinding)children.get(elementName);
      if(delegatingChild == null)
      {
         delegatingChild = new DelegatingElementBinding(child);
         children.put(delegatingChild.getElementName(), delegatingChild);
      }
      else
      {
         delegatingChild.addDelegate(child);
      }
      return delegatingChild;
   }

   public QName getElementQName()
   {
      return elementName;
   }

   public QName getElementName()
   {
      return elementName;
   }

   public Class getJavaType()
   {
      return ((BasicElementBinding)delegates.get(delegates.size() - 1)).getJavaType();
   }

   public ElementBinding getElement(QName elementName)
   {
      DelegatingElementBinding cachedChild = (DelegatingElementBinding)children.get(elementName);
      if(cachedChild == null)
      {
         for(int i = delegates.size() - 1; i >= 0; --i)
         {
            BasicElementBinding parent = (BasicElementBinding)delegates.get(i);
            ElementBinding child = parent.getElement(elementName);
            if(child != null)
            {
               if(child instanceof DelegatingElementBinding)
               {
                  cachedChild = (DelegatingElementBinding)child;
               }
               else
               {
                  cachedChild = new DelegatingElementBinding(child);
               }
               children.put(elementName, cachedChild);
               break;
            }
         }
      }
      return cachedChild;
   }

   public AttributeBinding getAttribute(QName attributeName)
   {
      AttributeBinding attr = null;
      for(int i = delegates.size() - 1; i >= 0; --i)
      {
         BasicElementBinding parent = (BasicElementBinding)delegates.get(i);
         attr = parent.getAttribute(elementName);
         if(attr != null)
         {
            break;
         }
      }
      return attr;
   }

   AttributeBinding bindAttribute(QName attributeName, String fieldName, Class javaType)
   {
      AttributeBinding attribute = new AttributeBindingImpl(attributeName, javaType, getJavaType(), fieldName);
      int i = delegates.size() - 1;
      while(i >= 0)
      {
         BasicElementBinding element = (BasicElementBinding)delegates.get(i--);
         if(element instanceof BasicElementBindingImpl)
         {
            ((BasicElementBindingImpl)element).addAttribute(attribute);
            break;
         }
         else if(element.getAttribute(attributeName) != null)
         {
            BasicElementBindingImpl newBinding = cloneLastBinding();
            newBinding.addAttribute(attribute);
            addDelegate(newBinding);
            break;
         }
      }

      if(i < 0)
      {
         BasicElementBindingImpl newBinding = cloneLastBinding();
         newBinding.addAttribute(attribute);
         addDelegate(newBinding);
      }
      return attribute;
   }

   abstract BasicElementBindingImpl cloneLastBinding();
}
