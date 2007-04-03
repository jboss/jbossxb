package org.jboss.xb.binding.sunday.xop;

/**
 * @author Heiko Braun <heiko.braun@jboss.com>
 * @since Jun 28, 2006
 */
public class XOPObject {

   private Object content;
   private String contentType;

   public XOPObject(Object content) {
      this.content = content;
   }

   public Object getContent() {
      return content;
   }

   public String getContentType() {
      return contentType;
   }

   public void setContentType(String contentType) {
      this.contentType = contentType;
   }
}
