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
import org.jboss.xml.binding.metadata.unmarshalling.DocumentBinding;

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
   protected final DelegatingDocumentBinding doc;
   private final QName elementName;
   private final Map children = new HashMap();
   protected final List delegates = new ArrayList();

   public DelegatingBasicElementBinding(DelegatingDocumentBinding doc, BasicElementBinding delegate)
   {
      this.elementName = delegate.getElementName();
      delegates.add(delegate);
      this.doc = doc;
   }

   void addDelegate(BasicElementBinding delegate)
   {
      delegates.add(delegate);
   }

   DelegatingElementBinding bindElement(QName elementName,
                                        String fieldName,
                                        Class javaType)
   {
      AbstractElementBinding child = new ElementBindingImpl(elementName,
         javaType,
         getJavaType(),
         fieldName,
         this
      );
      DelegatingElementBinding delegatingChild = (DelegatingElementBinding)children.get(elementName);
      if(delegatingChild == null)
      {
         delegatingChild = new DelegatingElementBinding(doc, child);
         children.put(delegatingChild.getElementName(), delegatingChild);
      }
      else
      {
         delegatingChild.addDelegate(child);
      }
      return delegatingChild;
   }

   public DocumentBinding getDocument()
   {
      return doc;
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
                  cachedChild = new DelegatingElementBinding(doc, child);
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
            break;
         }
      }

      if(i < 0)
      {
         BasicElementBindingImpl newBinding = cloneLastBinding();
         newBinding.addAttribute(attribute);
      }
      return attribute;
   }

   abstract BasicElementBindingImpl cloneLastBinding();
}
