/***************************************
 *                                     *
 *  JBoss: The OpenSource J2EE WebOS   *
 *                                     *
 *  Distributable under LGPL license.  *
 *  See terms of license at gnu.org.   *
 *                                     *
 ***************************************/

package org.jboss.interception;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.jboss.util.NestedRuntimeException;
/**
 * SimpleMetaData provides hashmap based metadata storage and manages
 * serializtion of the contents based on the flags supplied.
 *
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @author <a href="mailto:d_jencks@users.sourceforge.net">David Jencks</a>
 * @version $Revision$
 *
 */
public class SimpleMetaData implements MetaDataResolver
{
   public static final byte TRANSIENT = 0x01;
   public static final byte AS_IS = 0x02;
   public static final byte MARSHALLED = 0x04;

   protected final Map metaData;

   /**
    * Creates an empty <code>SimpleMetaData</code>.
    *
    */
   public SimpleMetaData()
   {
      metaData = new HashMap();
   }


   public void addMetaData(Object group, Object attr, Object value)
   {
      addMetaData(group, attr, value, MARSHALLED);
   }

   public synchronized void addMetaData(Object group, Object attr, Object value, byte type)
   {
      HashMap groupData = (HashMap)metaData.get(group);
      if (groupData == null)
      {
         groupData = new HashMap();
         metaData.put(group, groupData);
      }
      MetaDataValue val = new MetaDataValue(type, value);
      groupData.put(attr, val);
   }

   public synchronized Object getMetaData(Object group, Object attr)
   {
      try
      {
         HashMap groupData = (HashMap)metaData.get(group);
         if (groupData == null) return null;
         MetaDataValue val = (MetaDataValue)groupData.get(attr);
         return val.get();
      }
      catch (IOException ioex)
      {
         throw new NestedRuntimeException("failed on MarshalledValue", ioex);
      }
      catch (ClassNotFoundException ex)
      {
         throw new NestedRuntimeException("failed on MarshalledValue", ex);
      }
   }

   public synchronized void removeMetaData(Object group, Object attr)
   {
      HashMap groupData = (HashMap)metaData.get(group);
      if (groupData != null)
      {
         groupData.remove(attr);
      }
   }

   public synchronized void removeGroupData(Object group)
   {
      metaData.remove(group);
   }

   public synchronized void clear()
   {
      metaData.clear();
   }

   public synchronized void mergeIn(SimpleMetaData data)
   {
      Iterator it = data.metaData.keySet().iterator();
      while (it.hasNext())
      {
         Object group = (String)it.next();
         HashMap attrs = (HashMap)data.metaData.get(group);
         HashMap map = (HashMap)metaData.get(group);
         if (map == null)
         {
            map = new HashMap();
            this.metaData.put(group, map);
         }
         map.putAll(attrs);
      }
   }

   public synchronized Object resolve(Invocation invocation, Object group, Object attr)
   {
      return getMetaData(group, attr);
   }

   public synchronized void writeExternal(java.io.ObjectOutput out)
      throws IOException
   {
      for (Iterator it = metaData.keySet().iterator(); it.hasNext(); )
      {
         Object group = it.next();
         HashMap map = (HashMap)metaData.get(group);
         if (map != null && map.size() > 0)
         {
            boolean groupWritten = false;
            for (Iterator attrs = map.keySet().iterator(); it.hasNext(); )
            {
               Object attr = attrs.next();
               MetaDataValue value = (MetaDataValue)map.get(attr);
               if (value.type == TRANSIENT) continue;
               if (!groupWritten)
               {
                  groupWritten = true;
                  out.writeObject(group);
               }
               out.writeObject(attr);
               if (value.type == AS_IS)
               {
                  out.writeObject(value.value);
               }
               else
               {
                  out.writeObject(new MarshalledValue(value.value));
               }
            }
            if (groupWritten) out.writeObject(null); // placeholder for end of attributes
         }
      }
      out.writeObject(null); // place holder for end of marshall
   }

   public synchronized void readExternal(java.io.ObjectInput in)
      throws IOException, ClassNotFoundException
   {
      Object group;
      while ((group = (String)in.readObject()) != null)
      {
         HashMap map = new HashMap();
         metaData.put(group, map);
         Object attr;
         while ((attr = (String)in.readObject()) != null)
         {
            Object obj = in.readObject();
            if (obj instanceof MarshalledValue)
            {
               map.put(attr, new MetaDataValue(MARSHALLED, obj));
            }
            else
            {
               map.put(attr, new MetaDataValue(AS_IS, obj));
            }
         }
      }
   }

   //Inner classes----------------------
   protected class MetaDataValue
   {

      public final byte type;
      public final Object value;

      public MetaDataValue(byte type, Object value)
      {
         this.type = type;
         this.value = value;
      }

      public Object get()
         throws java.io.IOException, ClassNotFoundException
      {
         if (value instanceof MarshalledValue)
         {
            return ((MarshalledValue)value).get();
         }
         return value;
      }

   }



}
