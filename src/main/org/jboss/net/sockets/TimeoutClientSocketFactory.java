package org.jboss.net.sockets;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.server.RMIClientSocketFactory;
import java.net.Socket;
import EDU.oswego.cs.dl.util.concurrent.FIFOSemaphore;

/**
 * A RMIClientSocketFactory that installs a InterruptableInputStream to be
 * responsive to thead interruption events.
 * 
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
public class TimeoutClientSocketFactory
   implements RMIClientSocketFactory, Serializable
{
   private static final long serialVersionUID = -920483051658660269L;

   public TimeoutClientSocketFactory()
   {
   }

   /**
    * Create a server socket on the specified port (port 0 indicates
    * an anonymous port).
    * @param  port the port number
    * @return the server socket on the specified port
    * @exception java.io.IOException if an I/O error occurs during server socket
    * creation
    * @since 1.2
    */
   public Socket createSocket(String host, int port) throws IOException
   {
      Socket s = new Socket(host, port);
      s.setSoTimeout(1000);
      TimeoutSocket ts = new TimeoutSocket(s);
      return ts;
   }
   
   public boolean equals(Object obj)
   {
      return obj instanceof TimeoutClientSocketFactory;
   }
   public int hashCode()
   {
      return getClass().getName().hashCode();
   }
   
}
