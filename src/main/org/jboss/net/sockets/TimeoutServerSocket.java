package org.jboss.net.sockets;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.io.IOException;

/** A ServerSocket that returns a TimeoutSocket from the overriden accept.
 * 
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
public class TimeoutServerSocket extends ServerSocket
{
   public TimeoutServerSocket(int port)
      throws IOException
   {
      this(port, 50);
   }
   public TimeoutServerSocket(int port, int backlog)
      throws IOException
   {
      this(port, backlog, null);
   }
   public TimeoutServerSocket(int port, int backlog, InetAddress bindAddr)
      throws IOException
   {
      super(port, backlog, bindAddr);
   }

   public Socket accept() throws IOException
   {
      Socket s = super.accept();
      s.setSoTimeout(1000);
      TimeoutSocket ts = new TimeoutSocket(s);
      return ts;
   }
}
