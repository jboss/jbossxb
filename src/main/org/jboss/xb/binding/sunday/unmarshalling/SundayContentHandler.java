/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xb.binding.sunday.unmarshalling;

import java.util.LinkedList;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xerces.xs.XSTypeDefinition;
import org.jboss.logging.Logger;
import org.jboss.util.StringPropertyReplacer;
import org.jboss.xb.binding.JBossXBRuntimeException;
import org.jboss.xb.binding.NamespaceRegistry;
import org.jboss.xb.binding.Util;
import org.jboss.xb.binding.metadata.CharactersMetaData;
import org.jboss.xb.binding.metadata.ValueMetaData;
import org.jboss.xb.binding.parser.JBossXBParser;
import org.xml.sax.Attributes;

/**
 * todo: to improve performance, consider gathering all the necessary binding metadata in startElement and
 * pushing it instead of ElementBinding into elementStack to free the endElement implementation from
 * re-gathering this same metadata again.
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public class SundayContentHandler
   implements JBossXBParser.ContentHandler
{
   private final static Logger log = Logger.getLogger(SundayContentHandler.class);

   private final static Object NIL = new Object();

   private final SchemaBinding schema;
   private final SchemaBindingResolver schemaResolver;
   private final StackImpl elementStack = new StackImpl();
   private final StackImpl objectStack = new StackImpl();
   private StringBuffer textContent = new StringBuffer();
   private Object root;
   private NamespaceRegistry nsRegistry = new NamespaceRegistry();

   public SundayContentHandler(SchemaBinding schema)
   {
      this.schema = schema;
      this.schemaResolver = null;
   }

   public SundayContentHandler(SchemaBindingResolver schemaResolver)
   {
      this.schemaResolver = schemaResolver;
      this.schema = null;
   }

   public void characters(char[] ch, int start, int length)
   {
      if(elementStack.peek() != null)
      {
         textContent.append(ch, start, length);
      }
   }

   public void endElement(String namespaceURI, String localName, String qName)
   {
      Object poped = pop();
      if(poped == null)
      {
         return;
      }

      ElementBinding elementBinding = null;
      if(poped instanceof ElementBinding)
      {
         elementBinding = (ElementBinding)poped;
      }
      else
      {
         while(!elementStack.isEmpty())
         {
            Object peeked = elementStack.pop();
            if(peeked instanceof ElementBinding)
            {
               elementBinding = (ElementBinding)peeked;
               break;
            }
         }

         if(elementBinding == null)
         {
            throw new JBossXBRuntimeException("Failed to endElement " + qName + ": binding not found");
         }
      }

      QName endName = localName.length() == 0 ? new QName(qName) : new QName(namespaceURI, localName);
      Object o = objectStack.pop();

      TypeBinding typeBinding = elementBinding.getType();
      List elementHandlers = elementBinding.getInterceptors();

      if(o != NIL)
      {
         //
         // characters
         //

         CharactersHandler simpleType = typeBinding.getSimpleType();
         if(textContent.length() > 0 || simpleType != null)
         {
            String dataContent;
            SchemaBinding schema = elementBinding.getSchema();
            if(textContent.length() == 0)
            {
               dataContent = null;
            }
            else
            {

               dataContent = textContent.toString();
               if(schema != null && schema.isReplacePropertyRefs())
               {
                  dataContent = StringPropertyReplacer.replaceProperties(dataContent);
               }
               textContent.delete(0, textContent.length());
            }

            Object unmarshalled;

            if(simpleType == null)
            {
               if(schema != null && schema.isStrictSchema())
               {
                  throw new JBossXBRuntimeException("Element " +
                     endName +
                     " type binding " +
                     typeBinding.getQName() +
                     " does not include text content binding ('" + dataContent
                  );
               }
               unmarshalled = dataContent;
            }
            else
            {
               ValueMetaData valueMetaData = elementBinding.getValueMetaData();
               if(valueMetaData == null)
               {
                  CharactersMetaData charactersMetaData = typeBinding.getCharactersMetaData();
                  if(charactersMetaData != null)
                  {
                     valueMetaData = charactersMetaData.getValue();
                  }
               }

               // todo valueMetaData is available from typeBinding
               unmarshalled = dataContent == null ?
                  simpleType.unmarshalEmpty(endName, typeBinding, nsRegistry, valueMetaData) :
                  simpleType.unmarshal(endName, typeBinding, nsRegistry, valueMetaData, dataContent);
            }

            if(unmarshalled != null)
            {
               // if startElement returned null, we use characters as the object for this element
               // todo subject to refactoring
               if(o == null)
               {
                  o = unmarshalled;
               }
               else if(simpleType != null)
               {
                  simpleType.setValue(endName, elementBinding, o, unmarshalled);
               }
            }

            // todo interceptors get dataContent?
            int i = elementHandlers.size();
            while(i-- > 0)
            {
               ElementInterceptor interceptor = (ElementInterceptor)elementHandlers.get(i);
               interceptor.characters(objectStack.peek(elementHandlers.size() - 1 - i),
                  endName, typeBinding, nsRegistry, dataContent
               );
            }
         }
      }
      else
      {
         o = null;
      }

      //
      // endElement
      //

      Object parent = objectStack.isEmpty() ? null : objectStack.peek();
      o = typeBinding.getHandler().endElement(o, endName, elementBinding);

      int i = elementHandlers.size();
      while(i-- > 0)
      {
         ElementInterceptor interceptor = (ElementInterceptor)elementHandlers.get(i);
         interceptor.endElement(objectStack.peek(elementHandlers.size() - 1 - i), endName, typeBinding);
      }

      //
      // setParent
      //

      i = elementHandlers.size();
      // todo yack...
      if(i == 0)
      {
         ElementBinding parentElement = null;
         for(int j = 0; j < elementStack.size(); ++j)
         {
            Object peeked = elementStack.peek(j);
            if(peeked instanceof ElementBinding)
            {
               parentElement = (ElementBinding)peeked;
               break;
            }
         }

         if(parent != null)
         {
            /*if(o == null)
            {
               throw new JBossXBRuntimeException(endName + " is null!");
            } */
            typeBinding.getHandler().setParent(parent, o, endName, elementBinding, parentElement);
         }
         else if(parentElement != null && parentElement.getType().hasWildcard() && !objectStack.isEmpty())
         {
            // todo: review this> the parent has anyType, so it gets the value of its child
            objectStack.pop();
            objectStack.push(o);
            if(log.isTraceEnabled())
            {
               log.trace("Value of " + endName + " " + o + " is promoted as the value of its parent element.");
            }
         }
      }
      else
      {
         while(i-- > 0)
         {
            ElementInterceptor interceptor = (ElementInterceptor)elementHandlers.get(i);
            parent = objectStack.pop();
            interceptor.add(parent, o, endName);
            o = parent;
         }
      }

      if(objectStack.isEmpty())
      {
         root = o;
      }

      if(!elementStack.isEmpty())
      {
         Object peeked = elementStack.peek();
         if(peeked instanceof ElementBinding)
         {
            throw new JBossXBRuntimeException("Expected model group for " + endName);
         }
         ModelGroupBinding.Cursor cursor = (ModelGroupBinding.Cursor)peeked;
         if(cursor.isElementFinished())
         {
            pop();
            if(elementStack.isEmpty())
            {
               throw new JBossXBRuntimeException("There is no cursor to end element: " + endName);
            }
            cursor = (ModelGroupBinding.Cursor)elementStack.peek();
         }
         cursor.endElement(endName);
      }
   }

   public void startElement(String namespaceURI,
                            String localName,
                            String qName,
                            Attributes atts,
                            XSTypeDefinition type)
   {
      QName startName = localName.length() == 0 ? new QName(qName) : new QName(namespaceURI, localName);
      ElementBinding binding = null;

      //ElementBinding parentBinding = null;
      SchemaBinding schemaBinding = schema;

      if(elementStack.isEmpty())
      {
         if(schemaBinding != null)
         {
            binding = schemaBinding.getElement(startName);
         }
         else if(schemaResolver != null)
         {
            String schemaLocation = atts == null ? null : Util.getSchemaLocation(atts, namespaceURI);
            schemaBinding = schemaResolver.resolve(namespaceURI, null, schemaLocation);
            if(schemaBinding != null)
            {
               binding = schemaBinding.getElement(startName);
            }
         }
         else
         {
            throw new JBossXBRuntimeException("Neither schema binding nor schema binding resolver is available!");
         }
      }
      else
      {
         Object peeked = elementStack.peek();
         if(peeked != null)
         {
            ModelGroupBinding.Cursor cursor;
            if(peeked instanceof ElementBinding)
            {
               ElementBinding element = (ElementBinding)peeked;
               ModelGroupBinding modelGroup = element.getType().getModelGroup();
               if(modelGroup == null)
               {
                  throw new JBossXBRuntimeException("Element " + element.getQName() + " should have a complex type!");
               }

               cursor = modelGroup.newCursor();
               List newCursors = cursor.startElement(startName, atts);
               if(!newCursors.isEmpty())
               {
                  // push all except the last one
                  for(int i = newCursors.size() - 1; i >= 0; --i)
                  {
                     cursor = (ModelGroupBinding.Cursor)newCursors.get(i);
                     //cursor.getModelGroup().startModelGroup();
                     push(cursor);
                  }

                  binding = (ElementBinding)cursor.getElement();
               }
               else
               {
                  System.out.println(startName + " not found as a child of " + element.getQName());
               }
            }
            else
            {
               while(!elementStack.isEmpty())
               {
                  peeked = elementStack.peek();
                  if(peeked instanceof ElementBinding)
                  {
                     ElementBinding element = (ElementBinding)peeked;
                     ModelGroupBinding modelGroup = element.getType().getModelGroup();
                     if(modelGroup == null)
                     {
                        throw new JBossXBRuntimeException(
                           "Element " + element.getQName() + " should have a complex type!"
                        );
                     }

                     if(modelGroup.isRepeatable())
                     {
                        cursor = modelGroup.newCursor();
                        List newCursors = cursor.startElement(startName, atts);
                        if(!newCursors.isEmpty())
                        {
                           // push all except the last one
                           for(int i = newCursors.size() - 1; i >= 0; --i)
                           {
                              cursor = (ModelGroupBinding.Cursor)newCursors.get(i);
                              //cursor.getModelGroup().startModelGroup();
                              push(cursor);
                           }

                           binding = (ElementBinding)cursor.getElement();
                        }
                     }

                     if(binding == null)
                     {
                        // todo: review this situation
                        log.warn("Element not found: " + startName);
                     }

                     break;
                  }

                  cursor = (ModelGroupBinding.Cursor)peeked;
                  List newCursors = cursor.startElement(startName, atts);
                  if(!newCursors.isEmpty())
                  {
                     // push all except the last one
                     for(int i = newCursors.size() - 2; i >= 0; --i)
                     {
                        cursor = (ModelGroupBinding.Cursor)newCursors.get(i);
                        //cursor.getModelGroup().startModelGroup();
                        push(cursor);
                     }

                     binding = (ElementBinding)cursor.getElement();
                     break;
                  }
                  else
                  {
                     pop();
                     //cursor.getModelGroup().endModelGroup();
                  }
               }
            }
         }
      }

      push(startName, binding);

      if(binding != null)
      {
         TypeBinding typeBinding = binding.getType();
         Object o = objectStack.isEmpty() ? null : objectStack.peek();

         List elementHandlers = binding.getInterceptors();
         for(int i = 0; i < elementHandlers.size(); ++i)
         {
            ElementInterceptor interceptor = (ElementInterceptor)elementHandlers.get(i);
            o = interceptor.startElement(o, startName, typeBinding);
            objectStack.push(o);
            interceptor.attributes(o, startName, typeBinding, atts, nsRegistry);
         }

         // todo xsi:nil handling
         String nil = atts.getValue("xsi:nil");
         if(nil == null || !("1".equals(nil) || "true".equals(nil)))
         {
            o = typeBinding.getHandler().startElement(o, startName, binding);
         }
         else
         {
            o = NIL;
         }
         objectStack.push(o);

         if(o != null && o != NIL)
         {
            // Expand the attributes list with any missing attrs with defaults
            atts = typeBinding.expandWithDefaultAttributes(atts);
            typeBinding.getHandler().attributes(o, startName, binding, atts, nsRegistry);
         }
      }
      else
      {
         ElementBinding parentBinding = elementStack.isEmpty() ? null : (ElementBinding)elementStack.peek();
         if(parentBinding != null && parentBinding.getSchema() != null)
         {
            schemaBinding = parentBinding.getSchema();
         }

         String msg = "Element " +
            startName +
            " is not bound " +
            (parentBinding == null ? "as a global element." : "in type " + parentBinding.getType().getQName());
         if(schemaBinding != null && schemaBinding.isStrictSchema())
         {
            throw new JBossXBRuntimeException(msg);
         }
         else if(log.isTraceEnabled())
         {
            log.trace(msg);
         }
      }
   }

   public void startPrefixMapping(String prefix, String uri)
   {
      nsRegistry.addPrefixMapping(prefix, uri);
   }

   public void endPrefixMapping(String prefix)
   {
      nsRegistry.removePrefixMapping(prefix);
   }

   public void processingInstruction(String target, String data)
   {
   }

   public Object getRoot()
   {
      return root;
   }

   // Private

   private void push(QName qName, ElementBinding element)
   {
      elementStack.push(element);
      if(log.isTraceEnabled())
      {
         log.trace("pushed binding " + qName + "=" + element);
      }
   }

   private void push(ModelGroupBinding.Cursor cursor)
   {
      elementStack.push(cursor);
      if(log.isTraceEnabled())
      {
         log.trace("pushed cursor " + cursor);
      }
   }

   private Object pop()
   {
      Object poped = elementStack.isEmpty() ? null : elementStack.pop();
      if(log.isTraceEnabled())
      {
         if(poped instanceof ElementBinding)
         {
            log.trace("poped " + ((ElementBinding)poped).getQName() + "=" + poped);
         }
         else
         {
            log.trace("poped " + poped);
         }
      }
      return poped;
   }

   // Inner

   static class StackImpl
   {
      private LinkedList list = new LinkedList();

      public void clear()
      {
         list.clear();
      }

      public void push(Object o)
      {
         list.addLast(o);
      }

      public Object pop()
      {
         return list.removeLast();
      }

      public Object peek()
      {
         return list.getLast();
      }

      public Object peek(int i)
      {
         return list.get(list.size() - 1 - i);
      }

      public boolean isEmpty()
      {
         return list.isEmpty();
      }

      public int size()
      {
         return list.size();
      }
   }
}
