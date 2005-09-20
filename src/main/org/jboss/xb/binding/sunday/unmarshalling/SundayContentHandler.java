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

   private StringBuffer textContent = new StringBuffer();
   private final StackImpl stack = new StackImpl();

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
      // todo: textContent should be an instvar of StackItem
      if(((StackItem)stack.peek()).particle != null)
      {
         textContent.append(ch, start, length);
      }
   }

   public void endElement(String namespaceURI, String localName, String qName)
   {
      StackItem item = pop();
      if(item.particle == null)
      {
         return;
      }

      ElementBinding elementBinding = null;
      if(item.particle instanceof ElementBinding)
      {
         elementBinding = (ElementBinding)item.particle;
      }
      else
      {
         while(!stack.isEmpty())
         {
            item = pop();
            if(item.particle instanceof ElementBinding)
            {
               elementBinding = (ElementBinding)item.particle;
               break;
            }
         }

         if(elementBinding == null)
         {
            throw new JBossXBRuntimeException("Failed to endElement " + qName + ": binding not found");
         }
      }

      QName endName = localName.length() == 0 ? new QName(qName) : new QName(namespaceURI, localName);
      Object o = item.o;

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
               interceptor.characters(((StackItem)stack.peek(elementHandlers.size() - 1 - i)).o,
                  endName, typeBinding, nsRegistry, dataContent);
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

      Object parent = stack.isEmpty() ? null : ((StackItem)stack.peek()).o;
      o = typeBinding.getHandler().endElement(o, endName, elementBinding);

      int i = elementHandlers.size();
      while(i-- > 0)
      {
         ElementInterceptor interceptor = (ElementInterceptor)elementHandlers.get(i);
         interceptor.endElement(((StackItem)stack.peek(elementHandlers.size() - 1 - i)).o, endName, typeBinding);
      }

      //
      // setParent
      //

      i = elementHandlers.size();
      // todo yack...
      if(i == 0)
      {
         ElementBinding parentElement = null;
         for(int j = 0; j < stack.size(); ++j)
         {
            Object peeked = ((StackItem)stack.peek(j)).particle;
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
         else if(parentElement != null && parentElement.getType().hasWildcard() && !stack.isEmpty()/*!objectStack.isEmpty()*/)
         {
            // todo: review this> the parent has anyType, so it gets the value of its child
            for(int j = 0; j < stack.size(); ++j)
            {
               StackItem peeked = (StackItem)stack.peek(j);
               peeked.o = o;
               if(peeked.particle instanceof ElementBinding)
               {
                  //System.out.println("Value of " + endName + " " + o + " is promoted as the value of its parent " +
                  //   ((ElementBinding)peeked.particle).getQName());
                  break;
               }
            }

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
            parent = ((StackItem)stack.pop()).o;
            interceptor.add(parent, o, endName);
            o = parent;
         }
      }

      if(stack.isEmpty())
      {
         root = o;
      }

      if(!stack.isEmpty())
      {
         StackItem peeked = (StackItem)stack.peek();
         if(peeked.particle instanceof ElementBinding)
         {
            throw new JBossXBRuntimeException("Expected model group for " + endName);
         }
         ModelGroupBinding.Cursor cursor = peeked.cursor;
         if(cursor.isElementFinished())
         {
            pop();
            if(stack.isEmpty())
            {
               throw new JBossXBRuntimeException("There is no cursor to end element: " + endName);
            }
            cursor = ((StackItem)stack.peek()).cursor;
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
      SchemaBinding schemaBinding = schema;

      if(stack.isEmpty())
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
         StackItem item = (StackItem)stack.peek();
         if(item.particle != null)
         {
            ModelGroupBinding.Cursor cursor;
            if(item.particle instanceof ElementBinding)
            {
               ElementBinding element = (ElementBinding)item.particle;
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
                  log.warn(startName + " not found as a child of " + element.getQName());
               }
            }
            else
            {
               while(!stack.isEmpty())
               {
                  item = (StackItem)stack.peek();
                  if(item.particle instanceof ElementBinding)
                  {
                     ElementBinding element = (ElementBinding)item.particle;
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

                  cursor = item.cursor;
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

      Object parent = null;
      if(binding != null)
      {
         TypeBinding typeBinding = binding.getType();
         parent = stack.isEmpty() ? null : ((StackItem)stack.peek()).o;

         List elementHandlers = binding.getInterceptors();
         for(int i = 0; i < elementHandlers.size(); ++i)
         {
            ElementInterceptor interceptor = (ElementInterceptor)elementHandlers.get(i);
            parent = interceptor.startElement(parent, startName, typeBinding);
            push(startName, binding, parent);
            interceptor.attributes(parent, startName, typeBinding, atts, nsRegistry);
         }

         // todo xsi:nil handling
         String nil = atts.getValue("xsi:nil");
         if(nil == null || !("1".equals(nil) || "true".equals(nil)))
         {
            parent = typeBinding.getHandler().startElement(parent, startName, binding);
         }
         else
         {
            parent = NIL;
         }

         if(parent != null && parent != NIL)
         {
            // Expand the attributes list with any missing attrs with defaults
            atts = typeBinding.expandWithDefaultAttributes(atts);
            typeBinding.getHandler().attributes(parent, startName, binding, atts, nsRegistry);
         }
      }
      else
      {
         ElementBinding parentBinding = stack.isEmpty() ? null : (ElementBinding)((StackItem)stack.peek()).particle;
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

      push(startName, binding, parent);
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

   private void push(QName qName, ElementBinding element, Object o)
   {
      stack.push(new StackItem(null, element, o));
      if(log.isTraceEnabled())
      {
         log.trace("pushed " + qName + "=" + o + ", binding=" + element);
      }
   }

   private void push(ModelGroupBinding.Cursor cursor)
   {
      stack.push(new StackItem(cursor, cursor.getModelGroup(), stack.isEmpty() ? null : ((StackItem)stack.peek()).o));
      if(log.isTraceEnabled())
      {
         log.trace("pushed cursor " + cursor);
      }
   }

   private StackItem pop()
   {
      StackItem item = (StackItem)stack.pop();
      if(log.isTraceEnabled())
      {
         if(item.particle instanceof ElementBinding)
         {
            log.trace("poped " + ((ElementBinding)item.particle).getQName() + "=" + item.particle);
         }
         else
         {
            log.trace("poped " + item.particle);
         }
      }
      return item;
   }

   // Inner

   private static class StackItem
   {
      final ModelGroupBinding.Cursor cursor;
      final ParticleBinding particle;
      Object o;

      public StackItem(ModelGroupBinding.Cursor cursor, ParticleBinding particle, Object o)
      {
         this.cursor = cursor;
         this.particle = particle;
         this.o = o;
      }
   }

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
