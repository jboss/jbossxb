/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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
package org.jboss.ejb.metadata.jboss.cmp;

import java.sql.Types;

import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
 * A JDBCTypeAdapter.
 * 
 * @author <a href="alex@jboss.com">Alexey Loubyansky</a>
 * @version $Revision: 1.1 $
 */
public class JDBCTypeAdapter extends XmlAdapter<String, Integer>
{
   @Override
   public String marshal(Integer arg0) throws Exception
   {
      String result = null;
      switch(arg0)
      {
         case Types.ARRAY:
            result = "ARRAY";
            break;
         case Types.BIGINT:
            result = "BIGINT";
            break;
         case Types.BINARY:
            result = "BINARY";
            break;
         case Types.BIT:
            result = "BIT";
            break;
         case Types.BLOB:
            result = "BLOB";
            break;
         case Types.BOOLEAN:
            result = "BOOLEAN";
            break;
         case Types.CHAR:
            result = "CHAR";
            break;
         case Types.CLOB:
            result = "CLOB";
            break;
         case Types.DATALINK:
            result = "DATALINK";
            break;
         case Types.DATE:
            result = "DATE";
            break;
         case Types.DECIMAL:
            result = "DECIMAL";
            break;
         case Types.DISTINCT:
            result = "DISTINCT";
            break;
         case Types.DOUBLE:
            result = "DOUBLE";
            break;
         case Types.FLOAT:
            result = "FLOAT";
            break;
         case Types.INTEGER:
            result = "INTEGER";
            break;
         case Types.JAVA_OBJECT:
            result = "JAVA_OBJECT";
            break;
         case Types.LONGVARBINARY:
            result = "LONGVARBINARY";
            break;
         case Types.LONGVARCHAR:
            result = "LONGVARCHAR";
            break;
         case Types.NULL:
            result = "NULL";
            break;
         case Types.NUMERIC:
            result = "NUMERIC";
            break;
         case Types.OTHER:
            result = "OTHER";
            break;
         case Types.REAL:
            result = "REAL";
            break;
         case Types.REF:
            result = "REF";
            break;
         case Types.SMALLINT:
            result = "SMALLINT";
            break;
         case Types.STRUCT:
            result = "STRUCT";
            break;
         case Types.TIME:
            result = "TIME";
            break;
         case Types.TIMESTAMP:
            result = "TIMESTAMP";
            break;
         case Types.TINYINT:
            result = "TINYINT";
            break;
         case Types.VARBINARY:
            result = "VARBINARY";
            break;
         case Types.VARCHAR:
            result = "VARCHAR";
            break;
         default:
            throw new IllegalArgumentException("Unexpected jdbc type: " + arg0);
      }
      return result;
   }

   @Override
   public Integer unmarshal(String arg0) throws Exception
   {
      Integer result;
      if("ARRAY".equals(arg0))
         result = Types.ARRAY;
      else if("BIGINT".equals(arg0))
         result = Types.BIGINT;
      else if("BINARY".equals(arg0))
         result = Types.BINARY;
      else if("BIT".equals(arg0))
         result = Types.BIT;
      else if("BLOB".equals(arg0))
         result = Types.BLOB;
      else if("BOOLEAN".equals(arg0))
         result = Types.BOOLEAN;
      else if("CHAR".equals(arg0))
         result = Types.CHAR;
      else if("CLOB".equals(arg0))
         result = Types.CLOB;
      else if("DATALINK".equals(arg0))
         result = Types.DATALINK;
      else if("DATE".equals(arg0))
         result = Types.DATE;
      else if("DECIMAL".equals(arg0))
         result = Types.DECIMAL;
      else if("DISTINCT".equals(arg0))
         result = Types.DISTINCT;
      else if("DOUBLE".equals(arg0))
         result = Types.DOUBLE;
      else if("FLOAT".equals(arg0))
         result = Types.FLOAT;
      else if("INTEGER".equals(arg0))
         result = Types.INTEGER;
      else if("JAVA_OBJECT".equals(arg0))
         result = Types.JAVA_OBJECT;
      else if("LONGVARBINARY".equals(arg0))
         result = Types.LONGVARBINARY;
      else if("LONGVARCHAR".equals(arg0))
         result = Types.LONGVARCHAR;
      else if("NULL".equals(arg0))
         result = Types.NULL;
      else if("NUMERIC".equals(arg0))
         result = Types.NUMERIC;
      else if("OTHER".equals(arg0))
         result = Types.OTHER;
      else if("REAL".equals(arg0))
         result = Types.REAL;
      else if("REF".equals(arg0))
         result = Types.REF;
      else if("SMALLINT".equals(arg0))
         result = Types.SMALLINT;
      else if("STRUCT".equals(arg0))
         result = Types.STRUCT;
      else if("TIME".equals(arg0))
         result = Types.TIME;
      else if("TIMESTAMP".equals(arg0))
         result = Types.TIMESTAMP;
      else if("TINYINT".equals(arg0))
         result = Types.TINYINT;
      else if("VARBINARY".equals(arg0))
         result = Types.VARBINARY;
      else if("VARCHAR".equals(arg0))
         result = Types.VARCHAR;
      else
         throw new IllegalArgumentException("Unexpected JDBC type: " + arg0);
      return result;
   }
}
