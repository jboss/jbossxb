
/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 *
 */

package org.jboss.util.propertyeditor;

import java.beans.PropertyEditor;
import org.apache.log4j.Logger;
import org.dom4j.Element;



import junit.framework.*;



/**
 * PropertyEditorUnitTestCase.java
 *
 *
 * Created: Fri Feb  7 12:37:20 2003
 *
 * @author <a href="mailto:d_jencks@users.sourceforge.net">David Jencks</a>
 * @version
 */

public class PropertyEditorUnitTestCase extends TestCase {

   private static final Logger log = Logger.getLogger(PropertyEditorUnitTestCase.class);

   public PropertyEditorUnitTestCase(String name)
   {
      super(name);   
   }
   
   private static final String TEST_XML = "<element attribute=\"value\"><nested/></element>";
   public void testDOM4JElementEditor() throws Exception
   {
      PropertyEditor pe = new DOM4JElementEditor();
      pe.setAsText(TEST_XML);
      Element element = (Element)pe.getValue();
      pe.setValue(element);
      String result = pe.getAsText();
      log.info("DOM4JElementEditor returned: " + result);
      assertTrue("Result should be equal to original string: original: " + TEST_XML + ", result: " + result, TEST_XML.equals(result));
      boundaryTests(pe);
   }

   private final static String TEST_STRING_ARRAY = "first,second,third";
   public void testStringArrayEditor() throws Exception
   {
      PropertyEditor pe = new StringArrayEditor();
      pe.setAsText(TEST_STRING_ARRAY);
      String[] strings = (String[])pe.getValue();
      pe.setValue(strings);
      String result = pe.getAsText();
      log.info("StringArrayEditor returned: " + result);
      assertTrue("Result should be equal to original string: original: " + TEST_STRING_ARRAY + ", result: " + result, TEST_STRING_ARRAY.equals(result));
      boundaryTests(pe);
   }

   private void boundaryTests(PropertyEditor pe) throws Exception
   {
      pe.setAsText(null);
      assertTrue("value should be null, is: " + pe.getValue(), pe.getValue() == null);
      pe.setAsText("");
      assertTrue("value should be null, is: " + pe.getValue(), pe.getValue() == null);
      pe.setValue(null);
      assertTrue("getAsText should return null, is: " + pe.getAsText(), pe.getAsText() == null);
   }

}// PropertyEditorUnitTestCase
