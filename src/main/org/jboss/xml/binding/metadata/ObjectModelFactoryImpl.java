/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata;

import org.jboss.xml.binding.GenericObjectModelFactory;
import org.jboss.xml.binding.UnmarshallingContext;
import org.jboss.xml.binding.Immutable;
import org.jboss.logging.Logger;
import org.xml.sax.Attributes;


/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class ObjectModelFactoryImpl
   implements GenericObjectModelFactory
{
   private static final Logger log = Logger.getLogger(ObjectModelFactoryImpl.class);

   public Object newChild(Object parent,
                          UnmarshallingContext ctx,
                          String namespaceURI,
                          String localName,
                          Attributes attrs)
   {
      Object child = null;

      XmlElement childElement = (XmlElement)ctx.getMetadata();
      if(childElement != null)
      {
         JavaValue javaValue = childElement.getJavaValue();
         if(javaValue.isBound())
         {
            child = javaValue.get(parent, localName);
            if(child == null)
            {
               child = javaValue.newInstance();
               if(log.isTraceEnabled())
               {
                  log.trace(
                     "newChild for " +
                     localName +
                     ": " +
                     (child == null ? "" : child.getClass().getName() + ":") +
                     child
                  );
               }
            }
            else if(log.isTraceEnabled())
            {
               log.trace(
                  "newChild for " + localName + ": using already created child " + child.getClass() + ":" + child
               );
            }

            if(child != null && attrs != null && attrs.getLength() > 0)
            {
               for(int i = 0; i < attrs.getLength(); ++i)
               {
                  XmlAttribute attr = childElement.getType().getAttribute(attrs.getURI(i), attrs.getLocalName(i));
                  if(attr != null)
                  {
                     javaValue = attr.getJavaValue();
                     if(javaValue.isBound())
                     {
                        javaValue.set(child, attrs.getValue(i), attrs.getLocalName(i));
                     }
                     else if(log.isTraceEnabled())
                     {
                        log.trace("Java value for attribute " +
                           attrs.getURI(i) +
                           ":" +
                           attrs.getLocalName(i) +
                           " is not bound."
                        );
                     }
                  }
                  else
                  {
                     log.warn("Metadata is not available for attribute " + namespaceURI + ":" + localName);
                  }
               }
            }
         }
         else
         {
            if(log.isTraceEnabled())
            {
               log.trace("Java value for element " + namespaceURI + ":" + localName + " is not bound.");
            }
         }
      }
      else
      {
         log.warn("Metadata is not available for element " + namespaceURI + ":" + localName);
      }

      return child;
   }

   public void addChild(Object parent, Object child, UnmarshallingContext ctx, String namespaceURI, String localName)
   {
      XmlElement childElement = (XmlElement)ctx.getMetadata();
      if(log.isTraceEnabled())
      {
         log.trace("addChild for " + localName + ": child=" + child);
      }
      childElement.getJavaValue().set(parent, child, localName);
   }

   public void setValue(Object o, UnmarshallingContext ctx, String namespaceURI, String localName, String value)
   {
      if(log.isTraceEnabled())
      {
         log.trace("setValue for " + localName + ": o=" + o + ", value=" + value);
      }

      XmlElement element = (XmlElement)ctx.getMetadata();
      if(element != null)
      {
         element.getType().getDataContent().getJavaValue().set(o, value, localName);
      }
      else
      {
         log.warn("Metadata is not available for " + namespaceURI + ":" + localName);
      }
   }

   public Object newRoot(Object root,
                         UnmarshallingContext ctx,
                         String namespaceURI,
                         String localName,
                         Attributes attrs)
   {
      if(root == null)
      {
         XmlElement element = (XmlElement)ctx.getMetadata();
         root = element.getJavaValue().newInstance();
         if(log.isTraceEnabled())
         {
            log.trace("created new root: " + root.getClass() + ":" + root);
         }
      }
      return root;
   }

   public Object completeRoot(Object root, UnmarshallingContext ctx, String namespaceURI, String localName)
   {
      if(root instanceof Immutable)
      {
         root = ((Immutable)root).newInstance();
      }
      return root;
   }
}
