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
   extends XsdElement
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

      protected void setAttributes(XsdElement element, Attributes attrs)
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
         XsdElement element = null;
         if("appinfo".equals(localName))
         {
            element = new XsdAppInfo();
         }

         setAttributes(element, attrs);

         return element;
      }

      public void addChild(Object parent,
                           Object child,
                           UnmarshallingContext ctx,
                           String namespaceURI,
                           String localName)
      {
         if(parent instanceof XsdAppInfo)
         {
            XsdAppInfo appInfo = (XsdAppInfo)parent;
            if(child instanceof ClassMetaData)
            {
               appInfo.setClassMetaData((ClassMetaData)child);
            }
            else if(child instanceof PropertyMetaData)
            {
               appInfo.setPropertyMetaData((PropertyMetaData)child);
            }
            else if(child instanceof SchemaMetaData)
            {
               appInfo.setSchemaMetaData((SchemaMetaData)child);
            }
            else if(child instanceof ValueMetaData)
            {
               appInfo.setValueMetaData((ValueMetaData)child);
            }
         }
         else
         {
            super.addChild(parent, child, ctx, namespaceURI, localName);
         }
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
      implements GenericObjectModelFactory
   {
      public static final GenericObjectModelFactory INSTANCE = new JaxbObjectModelFactory();

      public Object newChild(Object parent,
                             UnmarshallingContext ctx,
                             String namespaceURI,
                             String localName,
                             Attributes attrs)
      {
         Object element = null;
         if("package".equals(localName))
         {
            element = new PackageMetaData();
            setAttributes(element, attrs, new AttributeSetter()
            {
               public void setAttribute(Object o, String nsUri, String localName, String value)
               {
                  if("name".equals(localName))
                  {
                     ((PackageMetaData)o).setName(value);
                  }
               }
            }
            );
         }
         else if("javaType".equals(localName))
         {
            ValueMetaData valueMetaData = new ValueMetaData();
            setAttributes(valueMetaData, attrs, new AttributeSetter()
            {
               public void setAttribute(Object o, String nsUri, String localName, String value)
               {
                  if("parseMethod".equals(localName))
                  {
                     ((ValueMetaData)o).setUnmarshalMethod(value);
                  }
                  else if("printMethod".equals(localName))
                  {
                     ((ValueMetaData)o).setMarshalMethod(value);
                  }
               }
            }
            );

            // todo review this...
            XsdAppInfo appInfo = (XsdAppInfo)parent;
            appInfo.setValueMetaData(valueMetaData);
         }

         return element;
      }

      public void addChild(Object parent,
                           Object child,
                           UnmarshallingContext ctx,
                           String namespaceURI,
                           String localName)
      {
         if(parent instanceof CharactersMetaData)
         {
            CharactersMetaData charMetaData = (CharactersMetaData)parent;
            if(child instanceof PropertyMetaData)
            {
               charMetaData.setProperty((PropertyMetaData)child);
            }
            else
            {
               charMetaData.setValue((ValueMetaData)child);
            }
         }
         else if(parent instanceof SchemaMetaData)
         {
            SchemaMetaData schemaMetaData = (SchemaMetaData)parent;
            if(child instanceof PackageMetaData)
            {
               schemaMetaData.setPackage((PackageMetaData)child);
            }
            else
            {
               schemaMetaData.addValue((ValueMetaData)child);
            }
         }
      }

      public void setValue(Object o, UnmarshallingContext ctx, String namespaceURI, String localName, String value)
      {
      }

      public Object newRoot(Object root,
                            UnmarshallingContext ctx,
                            String namespaceURI,
                            String localName,
                            Attributes attrs)
      {
         Object element = null;
         if("schemaBindings".equals(localName))
         {
            element = new SchemaMetaData();
         }
         else if("property".equals(localName))
         {
            PropertyMetaData property = new PropertyMetaData();
            setAttributes(property, attrs, new AttributeSetter()
            {
               public void setAttribute(Object o, String nsUri, String localName, String value)
               {
                  if("name".equals(localName))
                  {
                     ((PropertyMetaData)o).setName(value);
                  }
                  else if("collectionType".equals(localName))
                  {
                     ((PropertyMetaData)o).setCollectionType(value);
                  }
               }
            }
            );
            //element = property;
            XsdAppInfo appInfo = (XsdAppInfo)root;
            appInfo.setPropertyMetaData(property);
            // return null;
         }
         else if("class".equals(localName))
         {
            element = new ClassMetaData();
            setAttributes(element, attrs, new AttributeSetter()
            {
               public void setAttribute(Object o, String nsUri, String localName, String value)
               {
                  if("implClass".equals(localName))
                  {
                     ((ClassMetaData)o).setImpl(value);
                  }
               }
            }
            );
         }
         else if("javaType".equals(localName))
         {
            element = new ValueMetaData();
            setAttributes(element, attrs, new AttributeSetter(){
               public void setAttribute(Object o, String nsUri, String localName, String value)
               {
                  if("printMethod".equals(localName))
                  {
                     ((ValueMetaData)o).setMarshalMethod(value);
                  }
                  else if("parseMethod".equals(localName))
                  {
                     ((ValueMetaData)o).setUnmarshalMethod(value);
                  }
               }
            });
         }

         return element;
      }

      public Object completeRoot(Object root, UnmarshallingContext ctx, String namespaceURI, String localName)
      {
         return root;
      }

      private void setAttributes(Object o, Attributes attrs, AttributeSetter attrSetter)
      {
         for(int i = 0; i < attrs.getLength(); ++i)
         {
            attrSetter.setAttribute(o, attrs.getURI(i), attrs.getLocalName(i), attrs.getValue(i));
         }
      }

      // Inner

      interface AttributeSetter
      {
         void setAttribute(Object o, String nsUri, String localName, String value);
      }
   }
}
