/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.util.property.jmx;

/**
 * MBean interface.
 */
public interface SystemPropertyClassValueMBean {

   /**
    * The system property value
    */
  java.lang.String getProperty() ;

   /**
    * The system property value
    */
  void setProperty(java.lang.String property) ;

   /**
    * The class name to use a value for the system property when it is available
    */
  java.lang.String getClassName() ;

   /**
    * The class name to use a value for the system property when it is available
    */
  void setClassName(java.lang.String className) ;

   /**
    * JBoss lifecycle
    */
  void create() ;

}
