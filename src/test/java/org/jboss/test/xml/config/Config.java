/*
  * JBoss, Home of Professional Open Source
  * Copyright 2005, JBoss Inc., and individual contributors as indicated
  * by the @authors tag. See the copyright.txt in the distribution for a
  * full listing of individual contributors.
  *
  * This is free software; you can redistribute it and/or modify it
  * under the terms of the GNU Lesser General Public License as
  * published by the Free Software Foundation; either version 2.1 of
  * the License, or (at your option) any later version.
  *
  * This software is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  * Lesser General Public License for more details.
  *
  * You should have received a copy of the GNU Lesser General Public
  * License along with this software; if not, write to the Free
  * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */
package org.jboss.test.xml.config;

import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision: 37406 $</tt>
 */
public class Config
{
   public static Config getInstance()
   {
      Config config = new Config();

      Config.ConfigAttr.ConfigAttrDataValue defValue = new Config.ConfigAttr.ConfigAttrDataValue();
      defValue.setData("default value");
      config.getAttrs().add(new Config.ConfigAttr(defValue));

      Config.ConfigAttr.ConfigAttrValue1 value1 = new Config.ConfigAttr.ConfigAttrValue1();
      value1.setProperty("value1");
      config.getAttrs().add(new Config.ConfigAttr(value1));

      Config.ConfigAttr.ConfigAttrValue2 value2 = new Config.ConfigAttr.ConfigAttrValue2();
      value2.setValue2("value2");
      config.getAttrs().add(new Config.ConfigAttr(value2));

      config.beans = new ArrayList();
      config.beans.add(new Config.Bean("A"));
      config.beans.add(new Config.Bean("B"));

      config.list = new LinkedList();
      config.list.add(new Config.ListValue("some.type", "foo"));
      config.list.add(new Config.Depends("SomeBean"));

      List sublist = new LinkedList();
      sublist.add(new Config.ListValue("another.type", "bar"));
      sublist.add(new Config.Depends("AnotherBean"));
      config.list.add(sublist);

      config.map = new HashMap();
      config.map.put("key1", "value1");
      config.map.put("key2", "value2");
      config.map.put("key3", "value3");
      config.map.put("key4", "value4");

      Map submap = new HashMap();
      submap.put("submapKey3", "submapValue3");
      config.map.put("submap", submap);

      return config;
   }

   private Collection attrs = new ArrayList();
   public Collection beans;
   public Collection list;
   public Map map;

   public Collection getAttrs()
   {
      return attrs;
   }

   public void setAttrs(Collection attrs)
   {
      this.attrs = attrs;
   }

   public String toString()
   {
      return "[config attrs=" +
         attrs +
         (beans == null ? ", no beans" : ", beans{" + beans.getClass() + "}=" + beans) +
         (list == null ? ", no list" : ", list{" + list.getClass() + "}=" + list) +
         (map == null ? ", no map" : ", map{" + map.getClass() + "}=" + map) +
         "]";
   }

   public boolean equals(Object o)
   {
      if(this == o)
      {
         return true;
      }
      if(!(o instanceof Config))
      {
         return false;
      }

      final Config config = (Config)o;

      if(attrs != null ? !attrs.equals(config.attrs) : config.attrs != null)
      {
         return false;
      }
      if(beans != null ? !beans.equals(config.beans) : config.beans != null)
      {
         return false;
      }
      if(list != null ? !list.equals(config.list) : config.list != null)
      {
         return false;
      }
      if(map != null ? !map.equals(config.map) : config.map != null)
      {
         return false;
      }

      return true;
   }

   public int hashCode()
   {
      int result;
      result = (attrs != null ? attrs.hashCode() : 0);
      result = 29 * result + (beans != null ? beans.hashCode() : 0);
      result = 29 * result + (list != null ? list.hashCode() : 0);
      result = 29 * result + (map != null ? map.hashCode() : 0);
      return result;
   }

   // Inner

   public static class ListValue
   {
      public String type;
      public String value;

      public ListValue(String type, String value)
      {
         this.type = type;
         this.value = value;
      }

      public String toString()
      {
         return "[list-value type=" + type + ", value=" + value + "]";
      }

      public boolean equals(Object o)
      {
         if(this == o)
         {
            return true;
         }
         if(!(o instanceof ListValue))
         {
            return false;
         }

         final ListValue listValue = (ListValue)o;

         if(type != null ? !type.equals(listValue.type) : listValue.type != null)
         {
            return false;
         }
         if(value != null ? !value.equals(listValue.value) : listValue.value != null)
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         int result;
         result = (type != null ? type.hashCode() : 0);
         result = 29 * result + (value != null ? value.hashCode() : 0);
         return result;
      }
   }

   public static class Depends
   {
      public String value;

      public Depends(String value)
      {
         this.value = value;
      }

      public String toString()
      {
         return "[depends value=" + value + "]";
      }

      public boolean equals(Object o)
      {
         if(this == o)
         {
            return true;
         }
         if(!(o instanceof Depends))
         {
            return false;
         }

         final Depends depends = (Depends)o;

         if(value != null ? !value.equals(depends.value) : depends.value != null)
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         return (value != null ? value.hashCode() : 0);
      }
   }

   public static class Bean
   {
      public String name;

      public Bean(String name)
      {
         this.name = name;
      }

      public String toString()
      {
         return "[bean " + name + "]";
      }

      public boolean equals(Object o)
      {
         if(this == o)
         {
            return true;
         }
         if(!(o instanceof Bean))
         {
            return false;
         }

         final Bean bean = (Bean)o;

         if(name != null ? !name.equals(bean.name) : bean.name != null)
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         return (name != null ? name.hashCode() : 0);
      }
   }

   public static class ConfigAttr
   {
      private ConfigAttrValue value;

      public ConfigAttr(ConfigAttrValue value)
      {
         this.value = value;
      }

      public ConfigAttrValue getValue()
      {
         return value;
      }

      public String toString()
      {
         return "[config-attr " + value + "]";
      }

      public boolean equals(Object o)
      {
         if(this == o)
         {
            return true;
         }
         if(!(o instanceof ConfigAttr))
         {
            return false;
         }

         final ConfigAttr configAttr = (ConfigAttr)o;

         if(!value.equals(configAttr.value))
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         return value.hashCode();
      }

      // Inner

      public static abstract class ConfigAttrValue
      {
      }

      public static class ConfigAttrDataValue
         extends ConfigAttrValue
      {
         private String data;

         public String getData()
         {
            return data;
         }

         public void setData(String data)
         {
            this.data = data;
         }

         public String toString()
         {
            return "[" + getClass().getName() + ": " + data + "]";
         }

         public boolean equals(Object o)
         {
            if(this == o)
            {
               return true;
            }
            if(!(o instanceof ConfigAttrDataValue))
            {
               return false;
            }

            final ConfigAttrDataValue configAttrDefaultValue = (ConfigAttrDataValue)o;

            if(!data.equals(configAttrDefaultValue.data))
            {
               return false;
            }

            return true;
         }

         public int hashCode()
         {
            return data.hashCode();
         }
      }

      public static class ConfigAttrValue1
         extends ConfigAttrValue
      {
         private String property;

         public String getProperty()
         {
            return property;
         }

         public void setProperty(String property)
         {
            this.property = property;
         }

         public String toString()
         {
            return "[" + getClass().getName() + ": " + property + "]";
         }

         public boolean equals(Object o)
         {
            if(this == o)
            {
               return true;
            }
            if(!(o instanceof ConfigAttrValue1))
            {
               return false;
            }

            final ConfigAttrValue1 configAttrValue1 = (ConfigAttrValue1)o;

            if(!property.equals(configAttrValue1.property))
            {
               return false;
            }

            return true;
         }

         public int hashCode()
         {
            return property.hashCode();
         }
      }

      public static class ConfigAttrValue2
         extends ConfigAttrValue
      {
         private String value2;

         public String getValue2()
         {
            return value2;
         }

         public void setValue2(String value2)
         {
            this.value2 = value2;
         }

         public String toString()
         {
            return "[" + getClass().getName() + ": " + value2 + "]";
         }

         public boolean equals(Object o)
         {
            if(this == o)
            {
               return true;
            }
            if(!(o instanceof ConfigAttrValue2))
            {
               return false;
            }

            final ConfigAttrValue2 configAttrValue2 = (ConfigAttrValue2)o;

            if(!value2.equals(configAttrValue2.value2))
            {
               return false;
            }

            return true;
         }

         public int hashCode()
         {
            return value2.hashCode();
         }
      }
   }
}
