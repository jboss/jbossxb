/*
 * JBoss, the OpenSource J2EE webOS
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.xml.binding.metadata.marshalling;

import org.jboss.xml.binding.JBossXBRuntimeException;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @version <tt>$Revision$</tt>
 */
public abstract class ObjectModelBindingFactory
{
   public static ObjectModelBindingFactory newInstance()
   {
      return new ObjectModelBindingFactoryImpl();
   }

   public abstract ObjectModelBinding newObjectModelBinding();

   public abstract FinalClassBinding bindFinalClass(ObjectModelBinding om,
                                                    Class cls,
                                                    String namespaceUri,
                                                    String elementName);

   public abstract FieldBinding bindFieldToAttribute(FinalClassBinding cls,
                                                     String fieldName,
                                                     String namespaceUri,
                                                     String attributeName);

   public abstract FieldBinding bindField(FinalClassBinding cls, String fieldName);

   public abstract FieldGroupSequenceBinding bindFieldGroupSequence(FinalClassBinding cls);

   public abstract FieldGroupChoiceBinding bindFieldGroupChoice(FinalClassBinding cls);

   public abstract FieldGroupSequenceBinding bindFieldGroupSequence(FieldGroupBinding group);

   public abstract FieldGroupChoiceBinding bindFieldGroupChoice(FieldGroupBinding group);

   public abstract FieldBinding bindField(FieldGroupBinding group, String fieldName);

   public abstract SimpleValueBinding bindSimpleValue(FieldBinding field, String namespaceUri, String elementName);

   public abstract FinalClassBinding bindFinalClassValue(FieldBinding field, String namespaceUri, String elementName);

   public abstract NonFinalClassBinding bindNonFinalClassValue(FieldBinding field);

   public abstract NonFinalClassBinding bindNonFinalClass(NonFinalClassBinding parentClass, Class subclass);

   public abstract FinalClassBinding bindFinalClass(NonFinalClassBinding parentClass,
                                                    Class subclass,
                                                    String namespaceUri,
                                                    String elementName);

   public abstract CollectionBinding bindCollectionValue(FieldBinding field, String namespaceUri, String elementName);

   public abstract NonFinalClassBinding bindNonFinalItem(CollectionBinding col, Class itemClass);

   public abstract FinalClassBinding bindFinalItem(CollectionBinding col,
                                                   Class itemClass,
                                                   String namespaceUri,
                                                   String elementName);

   public abstract SimpleValueBinding bindSimpleItem(CollectionBinding col,
                                                     Class itemClass,
                                                     String namespaceUri,
                                                     String elementName);

   public abstract NonFinalClassBinding bindNonFinalClass(ObjectModelBinding om, Class cls);
   
   // Inner
   
   static final class ObjectModelBindingFactoryImpl
      extends ObjectModelBindingFactory
   {
      public ObjectModelBinding newObjectModelBinding()
      {
         return new ObjectModelBindingImpl();
      }

      public FinalClassBinding bindFinalClass(ObjectModelBinding om,
                                              Class cls,
                                              String namespaceUri,
                                              String elementName)
      {
         FinalClassBindingImpl clsBinding = new FinalClassBindingImpl(cls, namespaceUri, elementName);
         ((ObjectModelBindingImpl)om).bindTop(clsBinding);
         return clsBinding;
      }

      public FieldBinding bindFieldToAttribute(FinalClassBinding cls,
                                               String fieldName,
                                               String namespaceUri,
                                               String attributeName)
      {
         FieldBinding field = new FieldBindingImpl(cls, fieldName);
         this.bindSimpleValue(field, namespaceUri, attributeName);
         ((FinalClassBindingImpl)cls).bindFieldToAttribute(field);
         return field;
      }

      public FieldBinding bindField(FinalClassBinding cls, String fieldName)
      {
         FieldBinding field = new FieldBindingImpl(cls, fieldName);
         ((FinalClassBindingImpl)cls).bindFieldGroup(field);
         return field;
      }

      public FieldGroupSequenceBinding bindFieldGroupSequence(FinalClassBinding cls)
      {
         FieldGroupSequenceBindingImpl seq = new FieldGroupSequenceBindingImpl(cls);
         ((FinalClassBindingImpl)cls).bindFieldGroup(seq);
         return seq;
      }

      public FieldGroupChoiceBinding bindFieldGroupChoice(FinalClassBinding cls)
      {
         FieldGroupChoiceBindingImpl choice = new FieldGroupChoiceBindingImpl(cls);
         ((FinalClassBindingImpl)cls).bindFieldGroup(choice);
         return choice;
      }

      public FieldGroupSequenceBinding bindFieldGroupSequence(FieldGroupBinding group)
      {
         FieldGroupSequenceBindingImpl seq = new FieldGroupSequenceBindingImpl(group.getFinalClassBinding());
         bindFieldGroup(group, seq);
         return seq;
      }

      public FieldGroupChoiceBinding bindFieldGroupChoice(FieldGroupBinding group)
      {
         FieldGroupChoiceBindingImpl choice = new FieldGroupChoiceBindingImpl(group.getFinalClassBinding());
         bindFieldGroup(group, choice);
         return choice;
      }

      public FieldBinding bindField(FieldGroupBinding group, String fieldName)
      {
         FieldBinding field = new FieldBindingImpl(group.getFinalClassBinding(), fieldName);
         bindFieldGroup(group, field);
         return field;
      }

      public SimpleValueBinding bindSimpleValue(FieldBinding field, String namespaceUri, String elementName)
      {
         SimpleValueBinding value = new SimpleValueBindingImpl(field.getFieldType(), namespaceUri, elementName);
         ((FieldBindingImpl)field).bindValue(value);
         return value;
      }

      public FinalClassBinding bindFinalClassValue(FieldBinding field, String namespaceUri, String elementName)
      {
         FinalClassBinding value = new FinalClassBindingImpl(field.getFieldType(), namespaceUri, elementName);
         ((FieldBindingImpl)field).bindValue(value);
         return value;
      }

      public NonFinalClassBinding bindNonFinalClassValue(FieldBinding field)
      {
         NonFinalClassBinding value = new NonFinalClassBindingImpl(field.getFieldType());
         ((FieldBindingImpl)field).bindValue(value);
         return value;
      }

      public NonFinalClassBinding bindNonFinalClass(NonFinalClassBinding parentClass, Class subclass)
      {
         NonFinalClassBindingImpl parent = ((NonFinalClassBindingImpl)parentClass);
         NonFinalClassBinding subclassBinding = new NonFinalClassBindingImpl(subclass,
            parent.fieldGroups,
            parent.fieldToAttribute
         );
         parent.bindSubclass(subclassBinding);
         return subclassBinding;
      }

      public FinalClassBinding bindFinalClass(NonFinalClassBinding parentClass,
                                              Class subclass,
                                              String namespaceUri,
                                              String elementName)
      {
         NonFinalClassBindingImpl parent = ((NonFinalClassBindingImpl)parentClass);
         FinalClassBinding subclassBinding = new FinalClassBindingImpl(subclass,
            namespaceUri,
            elementName,
            parent.fieldGroups,
            parent.fieldToAttribute
         );
         parent.bindSubclass(subclassBinding);
         return subclassBinding;
      }

      public CollectionBinding bindCollectionValue(FieldBinding field, String namespaceUri, String elementName)
      {
         CollectionBinding value = new CollectionBindingImpl(field.getFieldType(), namespaceUri, elementName);
         ((FieldBindingImpl)field).bindValue(value);
         return value;
      }

      public NonFinalClassBinding bindNonFinalItem(CollectionBinding col, Class itemClass)
      {
         NonFinalClassBinding item = new NonFinalClassBindingImpl(itemClass);
         ((CollectionBindingImpl)col).bindItem(item);
         return item;
      }

      public FinalClassBinding bindFinalItem(CollectionBinding col,
                                             Class itemClass,
                                             String namespaceUri,
                                             String elementName)
      {
         FinalClassBinding item = new FinalClassBindingImpl(itemClass, namespaceUri, elementName);
         ((CollectionBindingImpl)col).bindItem(item);
         return item;
      }

      public SimpleValueBinding bindSimpleItem(CollectionBinding col,
                                               Class itemClass,
                                               String namespaceUri,
                                               String elementName)
      {
         SimpleValueBinding item = new SimpleValueBindingImpl(itemClass, namespaceUri, elementName);
         ((CollectionBindingImpl)col).bindItem(item);
         return item;
      }

      public NonFinalClassBinding bindNonFinalClass(ObjectModelBinding om, Class cls)
      {
         NonFinalClassBinding clsBinding = new NonFinalClassBindingImpl(cls);
         ((ObjectModelBindingImpl)om).bindTop(clsBinding);
         return clsBinding;
      }
      
      // Private
      
      private void bindFieldGroup(FieldGroupBinding group, FieldGroupBinding subgroup)
      {
         switch(group.getCategory())
         {
            case FieldGroupBinding.SEQUENCE:
               ((FieldGroupSequenceBindingImpl)group).bindGroup(subgroup);
               break;
            case FieldGroupBinding.CHOICE:
               ((FieldGroupChoiceBindingImpl)group).bindGroup(subgroup);
               break;
            case FieldGroupBinding.FIELD:
               throw new JBossXBRuntimeException("Field binding can't contain nested field groups!");
            default:
               throw new JBossXBRuntimeException("Unexpected field group category: " + group.getCategory());
         }
      }
   }

   static final class ObjectModelBindingImpl
      implements ObjectModelBinding
   {
      private final Map tops = new HashMap();

      void bindTop(FieldValueBinding fieldValue)
      {
         tops.put(fieldValue.getJavaClass(), fieldValue);
      }

      public FieldValueBinding getTopClasse(Class cls)
      {
         return (FieldValueBinding)tops.get(cls);
      }
   }

   static abstract class AbstractFieldValueBinding
      implements FieldValueBinding
   {
      private final Class javaClass;

      protected AbstractFieldValueBinding(Class javaClass)
      {
         this.javaClass = javaClass;
      }

      public Class getJavaClass()
      {
         return javaClass;
      }
   }

   static abstract class AbstractBaseClassBinding
      extends AbstractFieldValueBinding
      implements BaseClassBinding
   {
      final Set fieldGroups;
      final Set fieldToAttribute;

      public AbstractBaseClassBinding(Class javaClass,
                                      Collection inheritedFieldGroups,
                                      Collection inheritedFieldToAttribute)
      {
         super(javaClass);
         this.fieldGroups = inheritedFieldGroups == null ? new HashSet() : new HashSet(inheritedFieldGroups);
         this.fieldToAttribute = inheritedFieldToAttribute == null ?
            new HashSet() :
            new HashSet(inheritedFieldToAttribute);
      }

      void bindFieldGroup(FieldGroupBinding group)
      {
         fieldGroups.add(group);
      }

      void bindFieldToAttribute(FieldBinding field)
      {
         fieldToAttribute.add(field);
      }

      public FieldBinding[] getFieldToAttributeBindings()
      {
         return (FieldBinding[])fieldToAttribute.toArray(new FieldBinding[fieldToAttribute.size()]);
      }

      public FieldGroupBinding[] getFieldGroups()
      {
         return (FieldGroupBinding[])fieldGroups.toArray(new FieldGroupBinding[fieldGroups.size()]);
      }
   }

   static final class SimpleValueBindingImpl
      extends AbstractFieldValueBinding
      implements SimpleValueBinding
   {
      private final String namespaceUri;
      private final String elementName;

      public SimpleValueBindingImpl(Class javaClass, String namespaceUri, String elementName)
      {
         super(javaClass);
         this.namespaceUri = namespaceUri;
         this.elementName = elementName;
      }

      public int getCategory()
      {
         return FieldValueBinding.SIMPLE_VALUE;
      }

      public String getNamespaceUri()
      {
         return namespaceUri;
      }

      public String getElementName()
      {
         return elementName;
      }
   }

   static final class FinalClassBindingImpl
      extends AbstractBaseClassBinding
      implements FinalClassBinding
   {
      private final String namespaceUri;
      private final String elementName;

      public FinalClassBindingImpl(Class javaClass, String namespaceUri, String elementName)
      {
         this(javaClass, namespaceUri, elementName, null, null);
      }

      public FinalClassBindingImpl(Class javaClass,
                                   String namespaceUri,
                                   String elementName,
                                   Collection inheritedFieldGroups,
                                   Collection inheritedFieldToAttribute)
      {
         super(javaClass, inheritedFieldGroups, inheritedFieldToAttribute);
         this.namespaceUri = namespaceUri;
         this.elementName = elementName;
      }

      public int getCategory()
      {
         return FieldValueBinding.FINAL_CLASS;
      }

      public String getNamespaceUri()
      {
         return namespaceUri;
      }

      public String getElementName()
      {
         return elementName;
      }
   }

   static final class NonFinalClassBindingImpl
      extends AbstractBaseClassBinding
      implements NonFinalClassBinding
   {
      private final Map subclasses = new HashMap();

      public NonFinalClassBindingImpl(Class javaClass)
      {
         this(javaClass, null, null);
      }

      public NonFinalClassBindingImpl(Class javaClass,
                                      Collection inheritedFieldGroups,
                                      Collection inheritedFieldToAttribute)
      {
         super(javaClass, inheritedFieldGroups, inheritedFieldToAttribute);
      }

      void bindSubclass(FieldValueBinding subclass)
      {
         subclasses.put(subclass.getJavaClass(), subclass);
      }

      public int getCategory()
      {
         return FieldValueBinding.NON_FINAL_CLASS;
      }

      public FieldValueBinding getSubclassBinding(Class type)
      {
         return (FieldValueBinding)subclasses.get(type);
      }
   }

   static final class CollectionBindingImpl
      extends AbstractFieldValueBinding
      implements CollectionBinding
   {
      private final String namespaceUri;
      private final String elementName;
      private final Map items = new HashMap();

      public CollectionBindingImpl(Class javaClass, String namespaceUri, String elementName)
      {
         super(javaClass);
         this.namespaceUri = namespaceUri;
         this.elementName = elementName;
      }

      void bindItem(FieldValueBinding item)
      {
         items.put(item.getJavaClass(), item);
      }

      public int getCategory()
      {
         return FieldValueBinding.COLLECTION;
      }

      public String getNamespaceUri()
      {
         return namespaceUri;
      }

      public String getElementName()
      {
         return elementName;
      }

      public FieldValueBinding getItemBinding(Class type)
      {
         return (FieldValueBinding)items.get(type);
      }
   }

   static abstract class AbstractFieldGroupBindingImpl
      implements FieldGroupBinding
   {
      private final FinalClassBinding owner;

      protected AbstractFieldGroupBindingImpl(FinalClassBinding owner)
      {
         this.owner = owner;
      }

      public FinalClassBinding getFinalClassBinding()
      {
         return owner;
      }
   }

   static final class FieldGroupSequenceBindingImpl
      extends AbstractFieldGroupBindingImpl
      implements FieldGroupSequenceBinding
   {
      private final List groups = new ArrayList();

      public FieldGroupSequenceBindingImpl(FinalClassBinding owner)
      {
         super(owner);
      }

      void bindGroup(FieldGroupBinding group)
      {
         groups.add(group);
      }

      public FieldGroupBinding[] getFieldGroups()
      {
         return (FieldGroupBinding[])groups.toArray(new FieldGroupBinding[groups.size()]);
      }

      public int getCategory()
      {
         return FieldGroupBinding.SEQUENCE;
      }

      public boolean equals(Object o)
      {
         if(this == o)
         {
            return true;
         }
         if(!(o instanceof FieldGroupSequenceBindingImpl))
         {
            return false;
         }

         final FieldGroupSequenceBindingImpl fieldGroupSequenceBinding = (FieldGroupSequenceBindingImpl)o;

         if(!groups.equals(fieldGroupSequenceBinding.groups))
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         return groups.hashCode();
      }
   }

   static final class FieldGroupChoiceBindingImpl
      extends AbstractFieldGroupBindingImpl
      implements FieldGroupChoiceBinding
   {
      private final List groups = new ArrayList();

      public FieldGroupChoiceBindingImpl(FinalClassBinding owner)
      {
         super(owner);
      }

      void bindGroup(FieldGroupBinding group)
      {
         groups.add(group);
      }

      public FieldGroupBinding[] getFieldGroups()
      {
         return (FieldGroupBinding[])groups.toArray(new FieldGroupBinding[groups.size()]);
      }

      public int getCategory()
      {
         return FieldGroupBinding.CHOICE;
      }

      public boolean equals(Object o)
      {
         if(this == o)
         {
            return true;
         }
         if(!(o instanceof FieldGroupChoiceBindingImpl))
         {
            return false;
         }

         final FieldGroupChoiceBindingImpl fieldGroupChoiceBinding = (FieldGroupChoiceBindingImpl)o;

         if(!groups.equals(fieldGroupChoiceBinding.groups))
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         return groups.hashCode();
      }
   }

   static final class FieldBindingImpl
      extends AbstractFieldGroupBindingImpl
      implements FieldBinding
   {
      private final String fieldName;
      private final Field field;
      private final Method getter;
      private final Class fieldType;
      private FieldValueBinding value;

      public FieldBindingImpl(FinalClassBinding owner, String fieldName)
      {
         super(owner);

         Class ownerClass = owner.getJavaClass();

         this.fieldName = fieldName;

         Field field = null;
         Method getter = null;
         Class fieldType;

         String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
         try
         {
            getter = ownerClass.getMethod(getterName, null);
            fieldType = getter.getReturnType();
         }
         catch(NoSuchMethodException e1)
         {
            try
            {
               field = ownerClass.getField(fieldName);
               fieldType = field.getType();
            }
            catch(NoSuchFieldException e)
            {
               throw new JBossXBRuntimeException(
                  "Failed to bind field " + fieldName + " in " + owner + ": neither field nor getter were found."
               );
            }
         }

         this.field = field;
         this.getter = getter;
         this.fieldType = fieldType;
      }

      void bindValue(FieldValueBinding value)
      {
         this.value = value;
      }

      public String getFieldName()
      {
         return fieldName;
      }

      public Field getField()
      {
         return field;
      }

      public Method getGetter()
      {
         return getter;
      }

      public Class getFieldType()
      {
         return fieldType;
      }

      public FieldValueBinding getValueBinding()
      {
         return value;
      }

      public int getCategory()
      {
         return FieldGroupBinding.FIELD;
      }

      public boolean equals(Object o)
      {
         if(this == o)
         {
            return true;
         }
         if(!(o instanceof FieldBindingImpl))
         {
            return false;
         }

         final FieldBindingImpl fieldBinding = (FieldBindingImpl)o;

         if(!fieldName.equals(fieldBinding.fieldName))
         {
            return false;
         }

         return true;
      }

      public int hashCode()
      {
         return fieldName.hashCode();
      }
   }
}
