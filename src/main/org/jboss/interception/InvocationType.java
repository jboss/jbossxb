/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.interception;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Type safe enumeration used for to identify the invocation types.
 *
 * @author  <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @version $Revision$
 */
public final class InvocationType implements Serializable {
   // these fields are used for serialization
   private static int nextOrdinal = 0;
   private static final ArrayList values = new ArrayList(4);

   public static final InvocationType METHOD =
         new InvocationType("METHOD", false, false);
   public static final InvocationType FIELD_READ =
         new InvocationType("FIELD_READ", false, true);
   public static final InvocationType FIELD_WRITE =
         new InvocationType("FIELD_WRITE", true, false);
   public static final InvocationType CONSTRUCTOR =
         new InvocationType("CONSTRUCTOR", true, true);

   private final transient String name;
   private final transient boolean isLocal;
   private final transient boolean isHome;

   // this is the only value serialized
   private final int ordinal;

   private InvocationType(String name, boolean isHome, boolean isLocal) {
      this.name = name;
      this.isLocal = isLocal;
      this.isHome = isHome;
      this.ordinal = nextOrdinal++;
      values.add(this);
   }

   public boolean isLocal()
   {
      return isLocal;
   }

   public boolean isHome()
   {
      return isHome;
   }

   public String toString() {
      return name;
   }

   Object readResolve() throws ObjectStreamException {
      return values.get(ordinal);
   }
}


