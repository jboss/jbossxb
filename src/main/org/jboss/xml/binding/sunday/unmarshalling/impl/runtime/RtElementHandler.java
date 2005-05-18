/*
 * JBoss, the OpenSource webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling.impl.runtime;

import java.lang.reflect.Constructor;
import javax.xml.namespace.QName;
import javax.xml.namespace.NamespaceContext;
import org.jboss.xml.binding.sunday.unmarshalling.ElementHandler;
import org.jboss.xml.binding.sunday.unmarshalling.TypeBinding;
import org.jboss.xml.binding.sunday.unmarshalling.AttributeBinding;
import org.jboss.xml.binding.sunday.unmarshalling.AttributeHandler;
import org.jboss.xml.binding.sunday.unmarshalling.CharactersHandler;
import org.jboss.xml.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xml.binding.sunday.unmarshalling.ElementBinding;
import org.jboss.xml.binding.Util;
import org.jboss.xml.binding.JBossXBRuntimeException;
import org.jboss.xml.binding.Constants;
import org.jboss.xml.binding.metadata.JaxbPackage;
import org.jboss.xml.binding.metadata.JaxbProperty;
import org.xml.sax.Attributes;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class RtElementHandler
   implements ElementHandler
{
   public static final RtElementHandler INSTANCE = new RtElementHandler();

   public Object startElement(Object parent, QName elementName, TypeBinding type)
   {
      Object o = null;
      if(!type.isSimple())
      {
         QName qName = type.getQName();
         if(qName == null)
         {
            qName = elementName;
         }

         String className = type.getJaxbClass() == null ? null : type.getJaxbClass().getImplClass();
         if(className == null)
         {
            SchemaBinding schemaBinding = type.getSchemaBinding();
            JaxbPackage jaxbPackage = schemaBinding == null ? null : schemaBinding.getJaxbPackage();
            String pkg = jaxbPackage == null ?
               Util.xmlNamespaceToJavaPackage(qName.getNamespaceURI()) :
               jaxbPackage.getName();
            className = Util.xmlNameToClassName(qName.getLocalPart(), true);
            if(pkg != null && pkg.length() > 0)
            {
               className = pkg + '.' + className;
            }
         }

         Class cls = null;
         try
         {
            cls = Thread.currentThread().getContextClassLoader().loadClass(className);
         }
         catch(ClassNotFoundException e)
         {
            // todo complex element may contain just data content...
            /*
            throw new JBossXBRuntimeException("Failed to resolve class name for " +
               elementName +
               " of type " +
               type.getQName() +
               ": " +
               e.getMessage()
            );
            */
         }

         if(cls != null)
         {
            try
            {
               Constructor ctor = cls.getConstructor(null);

               try
               {
                  o = ctor.newInstance(null);
               }
               catch(Exception e)
               {
                  throw new JBossXBRuntimeException("Failed to create an instance of " +
                     cls +
                     " using default constructor for element " +
                     elementName +
                     " of type " +
                     type.getQName()
                  );
               }
            }
            catch(NoSuchMethodException e)
            {
               throw new JBossXBRuntimeException(
                  "" +
                  cls +
                  " doesn't declare no-arg constructor: element=" +
                  elementName +
                  ", type=" +
                  type.getQName()
               );
            }
         }
      }
      return o;
   }

   public void attributes(Object o, QName elementName, TypeBinding type, Attributes attrs, NamespaceContext nsCtx)
   {
      for(int i = 0; i < attrs.getLength(); ++i)
      {
         QName attrName = new QName(attrs.getURI(i), attrs.getLocalName(i));
         AttributeBinding binding = type.getAttribute(attrName);
         if(binding != null)
         {
            AttributeHandler handler = binding.getHandler();
            if(handler != null)
            {
               Object value = handler.unmarshal(elementName, attrName, binding, nsCtx, attrs.getValue(i));
               handler.attribute(elementName, attrName, o, value);
            }
            else
            {
               throw new JBossXBRuntimeException(
                  "Attribute binding present but has no handler: element=" + elementName + ", attrinute=" + attrName
               );
            }
         }
         else
         {
            if(!Constants.NS_XML_SCHEMA_INSTANCE.equals(attrs.getURI(i)))
            {
               CharactersHandler simpleType = type.getSimpleType();
               Object value = simpleType == null ?
                  attrs.getValue(i) :
                  simpleType.unmarshal(elementName, type, nsCtx, binding.getJaxbProperty(), attrs.getValue(i));
               RtUtil.set(o, attrName, type, value);
            }
         }
      }
   }

   public Object endElement(Object o, QName elementName, TypeBinding type)
   {
      // todo: immutables
      return o;
   }

   public void setParent(Object parent, Object o, QName qName, ElementBinding element)
   {
      if(parent != null)
      {
         JaxbProperty jaxbProperty = element.getJaxbProperty();

         String propName = jaxbProperty == null ? null : jaxbProperty.getName();
         if(propName == null)
         {
            propName = Util.xmlNameToFieldName(qName.getLocalPart(), true);
         }

         String colType = jaxbProperty == null ? null : jaxbProperty.getCollectionType();
         RtUtil.set(parent, o, propName, colType, true);
      }
   }

   // Private

}
