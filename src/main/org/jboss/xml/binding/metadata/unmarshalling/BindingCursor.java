/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata.unmarshalling;

import java.util.LinkedList;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public interface BindingCursor
{
   void startElement(String namespaceURI, String localName);

   void endElement(String namespaceURI, String localName);

   BasicElementBinding getElementBinding();

   class Factory
   {
      private Factory()
      {
      }

      public static BindingCursor newCursor(DocumentBinding doc)
      {
         return doc == null ? NoopBindingCursor.INSTANCE : new BindingCursorImpl(doc);
      }

      private static class BindingCursorImpl implements BindingCursor
      {
         private final DocumentBinding docBinding;
         private final LinkedList stack = new LinkedList();

         public BindingCursorImpl(DocumentBinding docBinding)
         {
            this.docBinding = docBinding;
         }

         public void startElement(String namespaceURI, String localName)
         {
            BasicElementBinding elBinding;
            if(stack.isEmpty())
            {
               NamespaceBinding nsBinding = docBinding.getNamespace(namespaceURI);
               elBinding = nsBinding.getTopElement(localName);
            }
            else
            {
               elBinding = (BasicElementBinding)stack.getLast();
               elBinding = elBinding.getChildElement(localName);
            }

            if(elBinding == null)
            {
               System.out.println("Cursor> startElement: no binding for " + localName);
            }
            else
            {
               System.out.println("Cursor> startElement: " + elBinding.getElementName() + " -> " + elBinding.getJavaType());
            }

            stack.addLast(elBinding);
         }

         public void endElement(String namespaceURI, String localName)
         {
            stack.removeLast();
         }

         public BasicElementBinding getElementBinding()
         {
            return (BasicElementBinding)stack.getLast();
         }
      }

      private static class NoopBindingCursor implements BindingCursor
      {
         static final BindingCursor INSTANCE = new NoopBindingCursor();

         private NoopBindingCursor()
         {
         }

         public void startElement(String namespaceURI, String localName)
         {
         }

         public void endElement(String namespaceURI, String localName)
         {
         }

         public BasicElementBinding getElementBinding()
         {
            return null;
         }
      }
   }
}
