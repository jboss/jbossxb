package org.jboss.net.sockets;

import java.net.Socket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.SocketChannel;

/** A Socket that overrides the getInputStream to return a InterruptableInputStream
 *  
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
public class TimeoutSocket extends Socket
{
   private Socket s;

   public TimeoutSocket(Socket s)
   {
      this.s = s;
   }

   public InetAddress getInetAddress()
   {
      return s.getInetAddress();
   }

   public InetAddress getLocalAddress()
   {
      return s.getLocalAddress();
   }

   public int getPort()
   {
      return s.getPort();
   }

   public int getLocalPort()
   {
      return s.getLocalPort();
   }

   public SocketAddress getRemoteSocketAddress()
   {
      return s.getRemoteSocketAddress();
   }

   public SocketAddress getLocalSocketAddress()
   {
      return s.getLocalSocketAddress();
   }

   public SocketChannel getChannel()
   {
      return s.getChannel();
   }

   public InputStream getInputStream() throws IOException
   {
      InputStream is = s.getInputStream();
      InterruptableInputStream iis = new InterruptableInputStream(is);
      return iis;
   }

   public OutputStream getOutputStream() throws IOException
   {
      return s.getOutputStream();
   }

   public void setTcpNoDelay(boolean on) throws SocketException
   {
      s.setTcpNoDelay(on);
   }

   public boolean getTcpNoDelay() throws SocketException
   {
      return s.getTcpNoDelay();
   }

   public void setSoLinger(boolean on, int linger) throws SocketException
   {
      s.setSoLinger(on, linger);
   }

   public int getSoLinger() throws SocketException
   {
      return s.getSoLinger();
   }

   public void sendUrgentData(int data) throws IOException
   {
      s.sendUrgentData(data);
   }

   public void setOOBInline(boolean on) throws SocketException
   {
      s.setOOBInline(on);
   }

   public boolean getOOBInline() throws SocketException
   {
      return s.getOOBInline();
   }

   public synchronized void setSoTimeout(int timeout) throws SocketException
   {
      s.setSoTimeout(1000);
   }

   public synchronized int getSoTimeout() throws SocketException
   {
      return s.getSoTimeout();
   }

   public synchronized void setSendBufferSize(int size) throws SocketException
   {
      s.setSendBufferSize(size);
   }

   public synchronized int getSendBufferSize() throws SocketException
   {
      return s.getSendBufferSize();
   }

   public synchronized void setReceiveBufferSize(int size) throws SocketException
   {
      s.setReceiveBufferSize(size);
   }

   public synchronized int getReceiveBufferSize() throws SocketException
   {
      return s.getReceiveBufferSize();
   }

   public void setKeepAlive(boolean on) throws SocketException
   {
      s.setKeepAlive(on);
   }

   public boolean getKeepAlive() throws SocketException
   {
      return s.getKeepAlive();
   }

   public void setTrafficClass(int tc) throws SocketException
   {
      s.setTrafficClass(tc);
   }

   public int getTrafficClass() throws SocketException
   {
      return s.getTrafficClass();
   }

   public void setReuseAddress(boolean on) throws SocketException
   {
      s.setReuseAddress(on);
   }

   public boolean getReuseAddress() throws SocketException
   {
      return s.getReuseAddress();
   }

   public synchronized void close() throws IOException
   {
      s.close();
   }

   public void shutdownInput() throws IOException
   {
      s.shutdownInput();
   }

   public void shutdownOutput() throws IOException
   {
      s.shutdownOutput();
   }

   public String toString()
   {
      return s.toString();
   }

   public boolean isConnected()
   {
      return s.isConnected();
   }

   public boolean isBound()
   {
      return s.isBound();
   }

   public boolean isClosed()
   {
      return s.isClosed();
   }

   public boolean isInputShutdown()
   {
      return s.isInputShutdown();
   }

   public boolean isOutputShutdown()
   {
      return s.isOutputShutdown();
   }
}
