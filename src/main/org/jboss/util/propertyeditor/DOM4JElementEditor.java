
/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 *
 */

package org.jboss.util.propertyeditor;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import org.jboss.util.NestedRuntimeException;



/**
 * DOM4JElementEditor.java
 *
 *
 * Created: Fri Feb  7 08:36:34 2003
 *
 * @author <a href="mailto:d_jencks@users.sourceforge.net">David Jencks</a>
 * @version
 */

public class DOM4JElementEditor extends PropertyEditorSupport {
   public DOM4JElementEditor() {
      
   }
   
   public void setAsText(String text)
   {
      if (text == null || text.length() == 0)
      {
	 setValue(null); 
      } // end of if ()
      else
      {
	 try {
	    Reader reader = new StringReader(text);
	    SAXReader saxReader = new SAXReader();
	    Document doc = saxReader.read(reader);
	    setValue(doc.getRootElement());
	 }
	 catch (DocumentException e)
	 {
	    throw new NestedRuntimeException(e);
	 }
      }
   }

   public String getAsText()
   {
      if (getValue() == null)
      {
	 return null; 
      } // end of if ()
      
      try {
	 Element element = (Element)getValue();
	 Writer writer = new StringWriter();
	 XMLWriter xmlWriter = new XMLWriter(writer);
	 xmlWriter.write(element);
	 return writer.toString();
      }
      catch (IOException e)
      {
         throw new NestedRuntimeException(e);
      }
   }
}// DOM4JElementEditor
