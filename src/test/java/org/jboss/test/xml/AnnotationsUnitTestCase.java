/*
  * JBoss, Home of Professional Open Source
  * Copyright 2005, JBoss Inc., and individual contributors as indicated
  * by the @authors tag. See the copyright.txt in the distribution for a
  * full listing of individual contributors.
  *
  * This is free software; you can redistribute it and/or modify it
  * under the terms of the GNU Lesser General Public License as
  * published by the Free Software Foundation; either version 2.1 of
  * the License, or (at your option) any later version.
  *
  * This software is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  * Lesser General Public License for more details.
  *
  * You should have received a copy of the GNU Lesser General Public
  * License along with this software; if not, write to the Free
  * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */
package org.jboss.test.xml;

import org.jboss.test.xml.book.Book;
import org.jboss.test.BaseTestCase;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.sunday.unmarshalling.SchemaBinding;
import org.jboss.xb.binding.sunday.unmarshalling.XsdBinder;


/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 38201 $</tt>
 */
public class AnnotationsUnitTestCase
   extends BaseTestCase
{
   public AnnotationsUnitTestCase(String name)
   {
      super(name);
   }

/*
   public void configureLogging()
   {
      enableTrace("org.jboss.xb");
   }
*/

   public void testMain() throws Exception
   {
      String url = getPath("xml/book/annotated_books.xsd");
      SchemaBinding schemaBinding = XsdBinder.bind(url);

      Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      Book book = (Book)unmarshaller.unmarshal(getPath("xml/book/book-xs.xml"), schemaBinding);
      assertEquals(Book.getInstance(), book);
   }

   // Private

   public String getPath(String path)
   {
      java.net.URL url = Thread.currentThread().getContextClassLoader().getResource(path);
      if(url == null)
      {
         fail("URL not found: " + path);
      }
      return url.toString();
   }
}
