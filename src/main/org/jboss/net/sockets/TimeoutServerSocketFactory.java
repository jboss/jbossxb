/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.net.sockets;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.rmi.server.RMIServerSocketFactory;

/**
 * A RMIServerSocketFactory that installs a InterruptableInputStream to be
 * responsive to thead interruption events.
 *
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
public class TimeoutServerSocketFactory
   implements RMIServerSocketFactory, Serializable
{
   /** @since 3.2.6 */
   static final long serialVersionUID = 7006964274840965634L;
   protected transient InetAddress bindAddress;
   protected int backlog = 200;

   /**
    * Create a socket factory that binds on any address with a default
    * backlog of 200
    */
   public TimeoutServerSocketFactory()
   {
      this(null, 200);
   }

   /**
    * Create a socket factory with the given bind address
    */
   public TimeoutServerSocketFactory(InetAddress bindAddress)
   {
      this(bindAddress, 200);
   }

   /**
    * Create a socket factory with the given backlog
    */
   public TimeoutServerSocketFactory(int backlog)
   {
      this(null, backlog);
   }

   /**
    * Create a socket factory with the given bind address and backlog
    */
   public TimeoutServerSocketFactory(InetAddress bindAddress, int backlog)
   {
      this.bindAddress = bindAddress;
      this.backlog = backlog;
   }

   public String getBindAddress()
   {
      String address = null;
      if (bindAddress != null)
         address = bindAddress.getHostAddress();
      return address;
   }

   public void setBindAddress(String host) throws UnknownHostException
   {
      bindAddress = InetAddress.getByName(host);
   }
   public void setBindAddress(InetAddress bindAddress)
   {
      this.bindAddress = bindAddress;
   }

   /**
    * Create a server socket on the specified port (port 0 indicates
    * an anonymous port).
    *
    * @param port the port number
    * @return the server socket on the specified port
    * @throws java.io.IOException if an I/O error occurs during server socket
    *    creation
    * @since 1.2
    */
   public ServerSocket createServerSocket(int port) throws IOException
   {
      ServerSocket activeSocket = new TimeoutServerSocket(port, backlog, bindAddress);
      return activeSocket;
   }

   public boolean equals(Object obj)
   {
      return obj instanceof TimeoutServerSocketFactory;
   }

   public int hashCode()
   {
      return getClass().getName().hashCode();
   }
}