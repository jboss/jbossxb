/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.util.xml;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import org.jboss.logging.Logger;
import org.jboss.util.JBossStringBuilder;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

public class JBossErrorHandler implements ErrorHandler, ErrorListener
{
   private static final Logger log =Logger.getLogger(JBossErrorHandler.class);
   
   // The xml file being parsed
   private String fileName;
   private JBossEntityResolver resolver;
   private boolean error;
   
   public JBossErrorHandler(String fileName, JBossEntityResolver resolver)
   {
      this.fileName = fileName;
      this.resolver = resolver;
      this.error = false;
   }
   
   public void error(SAXParseException e)
   {
      if (resolver == null || resolver.isEntityResolved())
      {
         error = true;
         log.error(formatError("error", e));
      }
   }
   
   public void fatalError(SAXParseException e)
   {
      if (resolver == null || resolver.isEntityResolved())
      {
         error = true;
         log.error(formatError("fatal", e));
      }
   }
   
   public void warning(SAXParseException e)
   {
      if (resolver == null || resolver.isEntityResolved())
      {
         error = true;
         log.error(formatError("warning", e));
      }
   }
   
   public void error(TransformerException e)
   {
      if (resolver == null || resolver.isEntityResolved())
      {
         error = true;
         log.error(formatError("error", e));
      }
   }
   
   public void fatalError(TransformerException e)
   {
      if (resolver == null || resolver.isEntityResolved())
      {
         error = true;
         log.error(formatError("fatal", e));
      }
   }
   
   public void warning(TransformerException e)
   {
      if (resolver == null || resolver.isEntityResolved())
      {
         error = true;
         log.error(formatError("warning", e));
      }
   }

   protected String formatError(String context, SAXParseException e)
   {
      JBossStringBuilder buffer = new JBossStringBuilder();
      buffer.append("File ").append(fileName);
      buffer.append(" process ").append(context);
      buffer.append(". Line: ").append(e.getLineNumber());
      buffer.append(". Error message: ").append(e.getMessage());
      return buffer.toString();
   }

   protected String formatError(String context, TransformerException e)
   {
      JBossStringBuilder buffer = new JBossStringBuilder();
      buffer.append("File ").append(fileName);
      buffer.append(" process ").append(context);
      buffer.append(". Location: ").append(e.getLocationAsString());
      buffer.append(". Error message: ").append(e.getMessage());
      return buffer.toString();
   }
   
   public boolean hadError()
   {
      return error;
   }
}
