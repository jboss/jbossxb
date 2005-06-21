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
import org.jboss.logging.Logger;
import org.xml.sax.Attributes;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class XsdAnnotation
   extends XsdElement
{
   private static final Logger log = Logger.getLogger(XsdAnnotation.class);

   public XsdAnnotation(QName qName)
   {
      super(qName);
   }

   public static final XsdAnnotation unmarshal(String annotation)
   {
      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      unmarshaller.mapFactoryToNamespace(JaxbObjectModelFactory.INSTANCE, Constants.NS_JAXB);
      unmarshaller.mapFactoryToNamespace(JbxbObjectModelFactory.INSTANCE, Constants.NS_JBXB);

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
         if(parent instanceof SchemaMetaData)
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
            setAttributes(element, attrs, new AttributeSetter()
            {
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
            }
            );
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
   }

   private static final class JbxbObjectModelFactory
      implements GenericObjectModelFactory
   {
      public static final JbxbObjectModelFactory INSTANCE = new JbxbObjectModelFactory();

      public Object newChild(Object parent,
                             UnmarshallingContext ctx,
                             String namespaceURI,
                             String localName,
                             Attributes attrs)
      {
         Object child = null;
         if("package".equals(localName))
         {
            child = new PackageMetaData();
            setAttributes(child, attrs, new AttributeSetter()
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
         else if("value".equals(localName))
         {
            child = new ValueMetaData();
            setAttributes(child, attrs, new AttributeSetter()
            {
               public void setAttribute(Object o, String nsUri, String localName, String value)
               {
                  if("marshalMethod".equals(localName))
                  {
                     ((ValueMetaData)o).setMarshalMethod(value);
                  }
                  else if("unmarshalMethod".equals(localName))
                  {
                     ((ValueMetaData)o).setUnmarshalMethod(value);
                  }
               }
            }
            );
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
            XsdAppInfo appInfo = (XsdAppInfo)parent;
            appInfo.setPropertyMetaData(property);
         }
         else if("mapEntryKey".equals(localName))
         {
            XsdAppInfo appInfo = (XsdAppInfo)parent;
            appInfo.setMapEntryKey(true);
         }
         else if("mapEntryValue".equals(localName))
         {
            XsdAppInfo appInfo = (XsdAppInfo)parent;
            appInfo.setMapEntryValue(true);
         }
         else
         {
            log.warn("newChild: " + localName);
         }
         return child;
      }

      public void addChild(Object parent,
                           Object child,
                           UnmarshallingContext ctx,
                           String namespaceURI,
                           String localName)
      {
         if(child instanceof PackageMetaData)
         {
            SchemaMetaData schema = (SchemaMetaData)parent;
            schema.setPackage((PackageMetaData)child);
         }
         else if(child instanceof ValueMetaData)
         {
            XsdAppInfo appInfo = (XsdAppInfo)parent;
            ValueMetaData valueMetaData = (ValueMetaData)child;
            appInfo.setValueMetaData(valueMetaData);
         }
         else
         {
            log.warn("addChild: " + localName + "=" + child);
         }
      }

      public void setValue(Object o, UnmarshallingContext ctx, String namespaceURI, String localName, String value)
      {
         log.warn("setValue: " + localName + "=" + value);
      }

      public Object newRoot(Object root,
                            UnmarshallingContext ctx,
                            String namespaceURI,
                            String localName,
                            Attributes attrs)
      {
         Object element = null;
         if("schema".equals(localName))
         {
            element = new SchemaMetaData();
         }
         else if("value".equals(localName))
         {
            element = new ValueMetaData();
            setAttributes(element, attrs, new AttributeSetter()
            {
               public void setAttribute(Object o, String nsUri, String localName, String value)
               {
                  if("marshalMethod".equals(localName))
                  {
                     ((ValueMetaData)o).setMarshalMethod(value);
                  }
                  else if("unmarshalMethod".equals(localName))
                  {
                     ((ValueMetaData)o).setUnmarshalMethod(value);
                  }
               }
            }
            );
         }
         else if("class".equals(localName))
         {
            element = new ClassMetaData();
            setAttributes(element, attrs, new AttributeSetter()
            {
               public void setAttribute(Object o, String nsUri, String localName, String value)
               {
                  if("impl".equals(localName))
                  {
                     ((ClassMetaData)o).setImpl(value);
                  }
               }
            }
            );
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
            XsdAppInfo appInfo = (XsdAppInfo)root;
            appInfo.setPropertyMetaData(property);
         }
         else if("putMethod".equals(localName))
         {
            PutMethodMetaData putMethod = new PutMethodMetaData();
            setAttributes(putMethod, attrs, new AttributeSetter()
            {
               public void setAttribute(Object o, String nsUri, String localName, String value)
               {
                  if("name".equals(localName))
                  {
                     ((PutMethodMetaData)o).setName(value);
                  }
                  else if("keyType".equals(localName))
                  {
                     ((PutMethodMetaData)o).setKeyType(value);
                  }
                  else if("valueType".equals(localName))
                  {
                     ((PutMethodMetaData)o).setValueType(value);
                  }
               }
            }
            );
            XsdAppInfo appInfo = (XsdAppInfo)root;
            appInfo.setPutMethodMetaData(putMethod);
         }
         else if("addMethod".equals(localName))
         {
            AddMethodMetaData addMethod = new AddMethodMetaData();
            setAttributes(addMethod, attrs, new AttributeSetter()
            {
               public void setAttribute(Object o, String nsUri, String localName, String value)
               {
                  if("name".equals(localName))
                  {
                     ((AddMethodMetaData)o).setMethodName(value);
                  }
                  else if("valueType".equals(localName))
                  {
                     if("child".equals(value))
                     {
                        ((AddMethodMetaData)o).setChildType(true);
                     }
                     else
                     {
                        ((AddMethodMetaData)o).setValueType(value);
                     }
                  }
               }
            }
            );
            XsdAppInfo appInfo = (XsdAppInfo)root;
            appInfo.setAddMethodMetaData(addMethod);
         }
         else if("mapEntry".equals(localName))
         {
            MapEntryMetaData mapEntry = new MapEntryMetaData();
            setAttributes(mapEntry, attrs, new AttributeSetter()
            {
               public void setAttribute(Object o, String nsUri, String localName, String value)
               {
                  if("impl".equals(localName))
                  {
                     ((MapEntryMetaData)o).setImpl(value);
                  }
                  else if("getKeyMethod".equals(localName))
                  {
                     ((MapEntryMetaData)o).setGetKeyMethod(value);
                  }
                  else if("setKeyMethod".equals(localName))
                  {
                     ((MapEntryMetaData)o).setSetKeyMethod(value);
                  }
                  else if("getValueMethod".equals(localName))
                  {
                     ((MapEntryMetaData)o).setGetValueMethod(value);
                  }
                  else if("setValueMethod".equals(localName))
                  {
                     ((MapEntryMetaData)o).setSetValueMethod(value);
                  }
                  else if("valueType".equals(localName))
                  {
                     ((MapEntryMetaData)o).setValueType(value);
                  }
                  else if("nonNullValue".equals(localName))
                  {
                     boolean b = Boolean.valueOf(value).booleanValue();
                     ((MapEntryMetaData)o).setNonNullValue(b);
                  }
               }
            }
            );
            XsdAppInfo appInfo = (XsdAppInfo)root;
            appInfo.setMapEntryMetaData(mapEntry);
         }
         else if("mapEntryKey".equals(localName))
         {
            XsdAppInfo appInfo = (XsdAppInfo)root;
            appInfo.setMapEntryKey(true);
         }
         else if("mapEntryValue".equals(localName))
         {
            XsdAppInfo appInfo = (XsdAppInfo)root;
            appInfo.setMapEntryValue(true);
         }
         else if("characters".equals(localName))
         {
         }
         else
         {
            log.warn("Unexpected jbxb annotation: ns=" + namespaceURI + ", localName=" + localName);
         }
         return element;
      }

      public Object completeRoot(Object root, UnmarshallingContext ctx, String namespaceURI, String localName)
      {
         return root;
      }

      // Private

      private void setAttributes(Object o, Attributes attrs, AttributeSetter attrSetter)
      {
         for(int i = 0; i < attrs.getLength(); ++i)
         {
            attrSetter.setAttribute(o, attrs.getURI(i), attrs.getLocalName(i), attrs.getValue(i));
         }
      }
   }

   interface AttributeSetter
   {
      void setAttribute(Object o, String nsUri, String localName, String value);
   }
}
