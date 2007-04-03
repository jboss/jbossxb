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
package org.jboss.test.xml.pojoserver;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * A SimpleBean.
 * 
 * @author <a href="adrian@jboss.com">Adrian Brock</a>
 * @version $Revision: 37406 $
 */
public interface SimpleBean
{
   // Constants -----------------------------------------------------

   // Public --------------------------------------------------------

   String getConstructorUsed();

   BigDecimal getABigDecimal();

   void setABigDecimal(BigDecimal bigDecimal);

   BigInteger getABigInteger();

   void setABigInteger(BigInteger bigInteger);

   boolean isAboolean();

   void setAboolean(boolean aboolean);

   Boolean getABoolean();

   void setABoolean(Boolean boolean1);

   byte getAbyte();

   void setAbyte(byte abyte);

   Byte getAByte();

   void setAByte(Byte byte1);

   char getAchar();

   void setAchar(char achar);

   Character getACharacter();

   void setACharacter(Character character);

   Date getADate();

   void setADate(Date date);

   double getAdouble();

   void setAdouble(double adouble);

   Double getADouble();

   void setADouble(Double double1);

   float getAfloat();

   void setAfloat(float afloat);

   Float getAFloat();

   void setAFloat(Float float1);

   long getAlong();

   void setAlong(long along);

   Long getALong();

   void setALong(Long long1);

   int getAnint();

   void setAnint(int anint);

   Integer getAnInt();

   void setAnInt(Integer anInt);

   short getAshort();

   void setAshort(short ashort);

   Short getAShort();

   void setAShort(Short short1);

   String getAString();

   void setAString(String string);

   SimpleBean getOther();

   void setOther(SimpleBean other);

   // Inner classes -------------------------------------------------
}