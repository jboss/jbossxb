/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling.impl;

import org.jboss.xml.binding.sunday.unmarshalling.ElementTypeBinding;
import org.jboss.xml.binding.sunday.unmarshalling.AttributeBinding;
import org.jboss.xml.binding.sunday.unmarshalling.AttributeHandler;
import org.jboss.xml.binding.sunday.unmarshalling.TextContentBinding;
import org.jboss.xml.binding.sunday.unmarshalling.TextContentHandler;
import org.jboss.xml.binding.sunday.unmarshalling.ElementBinding;

import javax.xml.namespace.QName;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class ElementTypeBindingImpl
   implements ElementTypeBinding
{
   private Map attrBindings = Collections.EMPTY_MAP;
   private Map childBindings = Collections.EMPTY_MAP;
   protected TextContentBinding contentBinding;
   private List parents = Collections.EMPTY_LIST;

   public AttributeBinding addAttribute(QName name)
   {
      AttributeBinding attr = new AttributeBindingImpl();
      addAttribute(name, attr);
      return attr;
   }

   public void addAttribute(QName name, AttributeBinding binding)
   {
      switch(attrBindings.size())
      {
         case 0:
            attrBindings = Collections.singletonMap(name, binding);
            break;
         case 1:
            attrBindings = new HashMap(attrBindings);
         default:
            attrBindings.put(name, binding);
      }
   }

   public AttributeBinding getAttribute(QName name)
   {
      AttributeBinding attr = (AttributeBinding)attrBindings.get(name);
      if(attr == null && parents.size() > 0)
      {
         for(int i = 0; i < parents.size(); ++i)
         {
            ElementTypeBinding parent = (ElementTypeBinding)parents.get(i);
            attr = parent.getAttribute(name);
            if(attr != null)
            {
               addAttribute(name, attr);
               break;
            }
         }
      }
      return attr;
   }

   public boolean hasAttributes()
   {
      boolean has = !attrBindings.isEmpty();
      if(!has && parents.size() > 0)
      {
         for(int i = 0; i < parents.size(); ++i)
         {
            ElementTypeBinding parent = (ElementTypeBinding)parents.get(i);
            has = parent.hasAttributes();
            if(has)
            {
               break;
            }
         }
      }
      return has;
   }

   public void pushAttributeHandler(QName name, AttributeHandler handler)
   {
      AttributeBinding binding = (AttributeBinding)attrBindings.get(name);
      if(binding == null)
      {
         binding = new AttributeBindingImpl();
         addAttribute(name, binding);
      }
      binding.pushHandler(handler);
   }

   public void addElement(QName name, ElementBinding binding)
   {
      switch(childBindings.size())
      {
         case 0:
            childBindings = Collections.singletonMap(name, binding);
            break;
         case 1:
            childBindings = new HashMap(childBindings);
         default:
            childBindings.put(name, binding);
      }
   }

   public void setTextContent(TextContentBinding contentBindingBinding)
   {
      this.contentBinding = contentBindingBinding;
   }

   public TextContentBinding getTextContent()
   {
      if(contentBinding == null && parents.size() > 0)
      {
         for(int i = 0; i < parents.size(); ++i)
         {
            ElementTypeBinding parent = (ElementTypeBinding)parents.get(i);
            contentBinding = parent.getTextContent();
            if(contentBinding != null)
            {
               break;
            }
         }
      }
      return contentBinding;
   }

   public void pushTextContentHandler(TextContentHandler handler)
   {
      if(contentBinding == null)
      {
         contentBinding = new TextContentBindingImpl();
      }
      contentBinding.pushHandler(handler);
   }

   public ElementBinding addElement(QName name)
   {
      ElementBinding childBinding = new ElementBindingImpl();
      addElement(name, childBinding);
      return childBinding;
   }

   public ElementBinding getElement(QName name)
   {
      ElementBinding element = (ElementBinding)childBindings.get(name);
      if(element == null && parents.size() > 0)
      {
         for(int i = 0; i <  parents.size(); ++i)
         {
            ElementTypeBinding parent = (ElementTypeBinding)parents.get(i);
            element = parent.getElement(name);
            if(element != null)
            {
               addElement(name, element);
               break;
            }
         }
      }
      return element;
   }

   public void addParent(ElementTypeBinding type)
   {
      switch(parents.size())
      {
         case 0:
            parents = Collections.singletonList(type);
            break;
         case 1:
            parents = new ArrayList(parents);
         default:
            parents.add(type);
      }
   }
}
