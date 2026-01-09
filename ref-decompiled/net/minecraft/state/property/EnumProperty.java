package net.minecraft.state.property;

import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.StringIdentifiable;

public final class EnumProperty extends Property {
   private final List values;
   private final Map byName;
   private final int[] enumOrdinalToPropertyOrdinal;

   private EnumProperty(String name, Class type, List values) {
      super(name, type);
      if (values.isEmpty()) {
         throw new IllegalArgumentException("Trying to make empty EnumProperty '" + name + "'");
      } else {
         this.values = List.copyOf(values);
         Enum[] enums = (Enum[])type.getEnumConstants();
         this.enumOrdinalToPropertyOrdinal = new int[enums.length];
         Enum[] var5 = enums;
         int var6 = enums.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Enum enum_ = var5[var7];
            this.enumOrdinalToPropertyOrdinal[enum_.ordinal()] = values.indexOf(enum_);
         }

         ImmutableMap.Builder builder = ImmutableMap.builder();
         Iterator var10 = values.iterator();

         while(var10.hasNext()) {
            Enum enum2 = (Enum)var10.next();
            String string = ((StringIdentifiable)enum2).asString();
            builder.put(string, enum2);
         }

         this.byName = builder.buildOrThrow();
      }
   }

   public List getValues() {
      return this.values;
   }

   public Optional parse(String name) {
      return Optional.ofNullable((Enum)this.byName.get(name));
   }

   public String name(Enum enum_) {
      return ((StringIdentifiable)enum_).asString();
   }

   public int ordinal(Enum enum_) {
      return this.enumOrdinalToPropertyOrdinal[enum_.ordinal()];
   }

   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else {
         if (object instanceof EnumProperty) {
            EnumProperty enumProperty = (EnumProperty)object;
            if (super.equals(object)) {
               return this.values.equals(enumProperty.values);
            }
         }

         return false;
      }
   }

   public int computeHashCode() {
      int i = super.computeHashCode();
      i = 31 * i + this.values.hashCode();
      return i;
   }

   public static EnumProperty of(String name, Class type) {
      return of(name, type, (enum_) -> {
         return true;
      });
   }

   public static EnumProperty of(String name, Class type, Predicate filter) {
      return of(name, type, (List)Arrays.stream((Enum[])type.getEnumConstants()).filter(filter).collect(Collectors.toList()));
   }

   @SafeVarargs
   public static EnumProperty of(String name, Class type, Enum... values) {
      return of(name, type, List.of(values));
   }

   public static EnumProperty of(String name, Class type, List values) {
      return new EnumProperty(name, type, values);
   }

   // $FF: synthetic method
   public int ordinal(final Comparable value) {
      return this.ordinal((Enum)value);
   }

   // $FF: synthetic method
   public String name(final Comparable value) {
      return this.name((Enum)value);
   }
}
