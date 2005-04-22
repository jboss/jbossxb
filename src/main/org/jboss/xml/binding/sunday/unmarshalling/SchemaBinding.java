/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.sunday.unmarshalling;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import javax.xml.namespace.QName;
import org.jboss.xml.binding.metadata.unmarshalling.BindingCursor;
import org.jboss.xml.binding.JBossXBRuntimeException;
import org.jboss.xml.binding.Constants;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class SchemaBinding
   implements BindingCursor
{
   private static final Map SIMPLE_TYPES = new HashMap();

   // populate SIMPLE_TYPES
   {
      addSimpleType(new TypeBinding(Constants.QNAME_ANYSIMPLETYPE));
      addSimpleType(new TypeBinding(Constants.QNAME_STRING));
      addSimpleType(new TypeBinding(Constants.QNAME_BOOLEAN));
      addSimpleType(new TypeBinding(Constants.QNAME_DECIMAL));
      addSimpleType(new TypeBinding(Constants.QNAME_FLOAT));
      addSimpleType(new TypeBinding(Constants.QNAME_DOUBLE));
      addSimpleType(new TypeBinding(Constants.QNAME_DURATION));
      addSimpleType(new TypeBinding(Constants.QNAME_DATETIME));
      addSimpleType(new TypeBinding(Constants.QNAME_TIME));
      addSimpleType(new TypeBinding(Constants.QNAME_DATE));
      addSimpleType(new TypeBinding(Constants.QNAME_GYEARMONTH));
      addSimpleType(new TypeBinding(Constants.QNAME_GYEAR));
      addSimpleType(new TypeBinding(Constants.QNAME_GMONTHDAY));
      addSimpleType(new TypeBinding(Constants.QNAME_GDAY));
      addSimpleType(new TypeBinding(Constants.QNAME_GMONTH));
      addSimpleType(new TypeBinding(Constants.QNAME_HEXBINARY));
      addSimpleType(new TypeBinding(Constants.QNAME_BASE64BINARY));
      addSimpleType(new TypeBinding(Constants.QNAME_ANYURI));
      addSimpleType(new TypeBinding(Constants.QNAME_QNAME));
      addSimpleType(new TypeBinding(Constants.QNAME_NOTATION));
      addSimpleType(new TypeBinding(Constants.QNAME_NORMALIZEDSTRING));
      addSimpleType(new TypeBinding(Constants.QNAME_TOKEN));
      addSimpleType(new TypeBinding(Constants.QNAME_LANGUAGE));
      addSimpleType(new TypeBinding(Constants.QNAME_NMTOKEN));
      addSimpleType(new TypeBinding(Constants.QNAME_NMTOKENS));
      addSimpleType(new TypeBinding(Constants.QNAME_NAME));
      addSimpleType(new TypeBinding(Constants.QNAME_NCNAME));
      addSimpleType(new TypeBinding(Constants.QNAME_ID));
      addSimpleType(new TypeBinding(Constants.QNAME_IDREF));
      addSimpleType(new TypeBinding(Constants.QNAME_IDREFS));
      addSimpleType(new TypeBinding(Constants.QNAME_ENTITY));
      addSimpleType(new TypeBinding(Constants.QNAME_ENTITIES));
      addSimpleType(new TypeBinding(Constants.QNAME_INTEGER));
      addSimpleType(new TypeBinding(Constants.QNAME_NONPOSITIVEINTEGER));
      addSimpleType(new TypeBinding(Constants.QNAME_NEGATIVEINTEGER));
      addSimpleType(new TypeBinding(Constants.QNAME_LONG));
      addSimpleType(new TypeBinding(Constants.QNAME_INT));
      addSimpleType(new TypeBinding(Constants.QNAME_SHORT));
      addSimpleType(new TypeBinding(Constants.QNAME_BYTE));
      addSimpleType(new TypeBinding(Constants.QNAME_NONNEGATIVEINTEGER));
      addSimpleType(new TypeBinding(Constants.QNAME_UNSIGNEDLONG));
      addSimpleType(new TypeBinding(Constants.QNAME_UNSIGNEDINT));
      addSimpleType(new TypeBinding(Constants.QNAME_UNSIGNEDSHORT));
      addSimpleType(new TypeBinding(Constants.QNAME_UNSIGNEDBYTE));
      addSimpleType(new TypeBinding(Constants.QNAME_POSITIVEINTEGER));
   }

   private void addSimpleType(TypeBinding type)
   {
      SIMPLE_TYPES.put(type.getQName(), type);
   }

   private Map types = new HashMap(SIMPLE_TYPES);
   private Map elements = new HashMap();
   private LinkedList stack = new LinkedList();

   public TypeBinding getType(QName qName)
   {
      return (TypeBinding)types.get(qName);
   }

   public void addType(TypeBinding type)
   {
      QName qName = type.getQName();
      if(qName == null)
      {
         throw new JBossXBRuntimeException("Global type must have a name.");
      }
      types.put(qName, type);
   }

   public ElementBinding getElement(QName name)
   {
      return (ElementBinding)elements.get(name);
   }

   public void addElement(QName qName, ElementBinding element)
   {
      elements.put(qName, element);
   }

   public ElementBinding addElement(QName name, TypeBinding type)
   {
      ElementBinding element = new ElementBinding(type);
      addElement(name, element);
      return element;
   }

   public void startElement(String namespaceURI, String localName)
   {
      QName qName = new QName(namespaceURI, localName);
      ElementBinding element;
      if(stack.isEmpty())
      {
         element = (ElementBinding)elements.get(qName);
      }
      else
      {
         TypeBinding parentType = ((ElementBinding)stack.getLast()).getType();
         element = parentType.getElement(qName);
      }

      if(element == null)
      {
         throw new JBossXBRuntimeException("Element is not bound: " + qName);
      }

      stack.addLast(element);
   }

   public void endElement(String namespaceURI, String localName)
   {
      stack.removeLast();
   }

   public Object getElementBinding()
   {
      return stack.getLast();
   }

   public Object getParentElementBinding()
   {
      return stack.size() > 1 ? stack.get(stack.size() - 2) : null;
   }
}
