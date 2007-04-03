/**
 * 
 */
package org.jboss.xb.binding.sunday.unmarshalling;

import org.w3c.dom.DOMError;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.DOMLocator;

public class XsdBinderLoggingErrorHandler implements DOMErrorHandler
{
   private static XsdBinderLoggingErrorHandler errorHandler;

   // Hide constructor
   private XsdBinderLoggingErrorHandler()
   {
   }

   public static XsdBinderLoggingErrorHandler newInstance()
   {
      if (errorHandler == null)
      {
         errorHandler = new XsdBinderLoggingErrorHandler();
      }
      return errorHandler;
   }

   public boolean handleError(DOMError error)
   {
      // todo: i do throw exceptions here instead of returning false to stop parsing immediately
      // since returning false seems to be no different from true (a bug in the parser?)
      // Although, throwing an exception reports the same error twice but the second time with
      // location -1:-1
      switch (error.getSeverity())
      {
         case DOMError.SEVERITY_ERROR:
            XsdBinder.log.error(formatMessage(error));
         case DOMError.SEVERITY_FATAL_ERROR:
            XsdBinder.log.fatal(formatMessage(error));
         case DOMError.SEVERITY_WARNING:
            XsdBinder.log.warn(formatMessage(error));
            break;
      }
      return false;
   }

   String formatMessage(DOMError error)
   {
      StringBuffer buf = new StringBuffer();
      DOMLocator location = error.getLocation();
      if (location != null)
      {
         buf.append(location.getLineNumber()).append(':').append(location.getColumnNumber());
      }
      else
      {
         buf.append("[location unavailable]");
      }

      buf.append(' ').append(error.getMessage());
      return buf.toString();
   }
}