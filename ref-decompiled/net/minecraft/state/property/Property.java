package net.minecraft.state.property;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.state.State;
import org.jetbrains.annotations.Nullable;

public abstract class Property {
   private final Class type;
   private final String name;
   @Nullable
   private Integer hashCodeCache;
   private final Codec codec;
   private final Codec valueCodec;

   protected Property(String name, Class type) {
      this.codec = Codec.STRING.comapFlatMap((value) -> {
         return (DataResult)this.parse(value).map(DataResult::success).orElseGet(() -> {
            return DataResult.error(() -> {
               String var10000 = String.valueOf(this);
               return "Unable to read property: " + var10000 + " with value: " + value;
            });
         });
      }, this::name);
      this.valueCodec = this.codec.xmap(this::createValue, Value::value);
      this.type = type;
      this.name = name;
   }

   public Value createValue(Comparable value) {
      return new Value(this, value);
   }

   public Value createValue(State state) {
      return new Value(this, state.get(this));
   }

   public Stream stream() {
      return this.getValues().stream().map(this::createValue);
   }

   public Codec getCodec() {
      return this.codec;
   }

   public Codec getValueCodec() {
      return this.valueCodec;
   }

   public String getName() {
      return this.name;
   }

   public Class getType() {
      return this.type;
   }

   public abstract List getValues();

   public abstract String name(Comparable value);

   public abstract Optional parse(String name);

   public abstract int ordinal(Comparable value);

   public String toString() {
      return MoreObjects.toStringHelper(this).add("name", this.name).add("clazz", this.type).add("values", this.getValues()).toString();
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof Property)) {
         return false;
      } else {
         Property property = (Property)o;
         return this.type.equals(property.type) && this.name.equals(property.name);
      }
   }

   public final int hashCode() {
      if (this.hashCodeCache == null) {
         this.hashCodeCache = this.computeHashCode();
      }

      return this.hashCodeCache;
   }

   public int computeHashCode() {
      return 31 * this.type.hashCode() + this.name.hashCode();
   }

   public DataResult parse(DynamicOps ops, State state, Object input) {
      DataResult dataResult = this.codec.parse(ops, input);
      return dataResult.map((property) -> {
         return (State)state.with(this, property);
      }).setPartial(state);
   }

   public static record Value(Property property, Comparable value) {
      public Value(Property property, Comparable value) {
         if (!property.getValues().contains(value)) {
            String var10002 = String.valueOf(value);
            throw new IllegalArgumentException("Value " + var10002 + " does not belong to property " + String.valueOf(property));
         } else {
            this.property = property;
            this.value = value;
         }
      }

      public String toString() {
         String var10000 = this.property.getName();
         return var10000 + "=" + this.property.name(this.value);
      }

      public Property property() {
         return this.property;
      }

      public Comparable value() {
         return this.value;
      }
   }
}
