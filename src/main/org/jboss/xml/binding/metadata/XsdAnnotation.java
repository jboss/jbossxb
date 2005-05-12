/*
 * JBoss, the OpenSource webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata;

import java.io.StringReader;
import javax.xml.namespace.QName;
import org.jboss.xml.binding.UnmarshallerFactory;
import org.jboss.xml.binding.Unmarshaller;
import org.jboss.xml.binding.GenericObjectModelFactory;
import org.jboss.xml.binding.UnmarshallingContext;
import org.jboss.xml.binding.JBossXBException;
import org.jboss.xml.binding.JBossXBRuntimeException;
import org.jboss.xml.binding.Constants;
import org.xml.sax.Attributes;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class XsdAnnotation
   extends BindingElement
{
   public XsdAnnotation(QName qName)
   {
      super(qName);
   }

   public static final XsdAnnotation unmarshal(String annotation)
   {
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      unmarshaller.mapFactoryToNamespace(JaxbObjectModelFactory.INSTANCE, Constants.NS_JAXB);

      try
      {
         return (XsdAnnotation)unmarshaller.unmarshal(new StringReader(annotation),
            XsdObjectModelFactory.INSTANCE,
            null
         );
      }
      catch(JBossXBException e)
      {
         throw new JBossXBRuntimeException("Failed to parse annotation string: " + annotation + ": " + e.getMessage(),
            e
         );
      }
   }

   public XsdAppInfo getAppInfo()
   {
      return (XsdAppInfo)getChild(XsdAppInfo.QNAME);
   }

   // Inner

   private static abstract class AbstractGOMF
      implements GenericObjectModelFactory
   {
      public Object newChild(Object parent,
                             UnmarshallingContext ctx,
                             String namespaceURI,
                             String localName,
                             Attributes attrs)
      {
         return null;
      }

      public void addChild(Object parent,
                           Object child,
                           UnmarshallingContext ctx,
                           String namespaceURI,
                           String localName)
      {
         XsdElement p = (XsdElement)parent;
         XsdElement c = (XsdElement)child;
         p.addChild(c);
      }

      public void setValue(Object o, UnmarshallingContext ctx, String namespaceURI, String localName, String value)
      {
         XsdElement e = (XsdElement)o;
         e.setData(value);
      }

      public Object completeRoot(Object root, UnmarshallingContext ctx, String namespaceURI, String localName)
      {
         return root;
      }

      protected void setAttributes(BindingElement element, Attributes attrs)
      {
         if(element != null)
         {
            for(int i = 0; i < attrs.getLength(); ++i)
            {
               element.addAttribute(new QName(attrs.getURI(i), attrs.getLocalName(i)), attrs.getValue(i));
            }
         }
      }
   }

   private static final class XsdObjectModelFactory
      extends AbstractGOMF
   {
      public static final GenericObjectModelFactory INSTANCE = new XsdObjectModelFactory();

      public Object newChild(Object parent,
                             UnmarshallingContext ctx,
                             String namespaceURI,
                             String localName,
                             Attributes attrs)
      {
         BindingElement element = null;
         if("appinfo".equals(localName))
         {
            element = new XsdAppInfo();
         }

         setAttributes(element, attrs);

         return element;
      }

      public Object newRoot(Object root,
                            UnmarshallingContext ctx,
                            String namespaceURI,
                            String localName,
                            Attributes attrs)
      {
         return new XsdAnnotation(new QName(namespaceURI, localName));
      }
   }

   private static final class JaxbObjectModelFactory
      extends AbstractGOMF
   {
      public static final GenericObjectModelFactory INSTANCE = new JaxbObjectModelFactory();

      public Object newChild(Object parent,
                             UnmarshallingContext ctx,
                             String namespaceURI,
                             String localName,
                             Attributes attrs)
      {
         BindingElement element = null;
         if("package".equals(localName))
         {
            element = new JaxbPackage();
         }

         setAttributes(element, attrs);

         return element;
      }

      public Object newRoot(Object root,
                            UnmarshallingContext ctx,
                            String namespaceURI,
                            String localName,
                            Attributes attrs)
      {
         BindingElement element = null;
         if("schemaBindings".equals(localName))
         {
            element = new JaxbSchemaBindings();
         }
         else if("property".equals(localName))
         {
            element = new JaxbProperty();
         }
         else if("class".equals(localName))
         {
            element = new JaxbClass();
         }

         setAttributes(element, attrs);

         return element;
      }
   }
}
