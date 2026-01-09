package net.minecraft.client.data;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.state.property.Property;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public record PropertiesMap(List values) {
   public static final PropertiesMap EMPTY = new PropertiesMap(List.of());
   private static final Comparator COMPARATOR = Comparator.comparing((value) -> {
      return value.property().getName();
   });

   public PropertiesMap(List values) {
      this.values = values;
   }

   public PropertiesMap withValue(Property.Value value) {
      return new PropertiesMap(Util.withAppended(this.values, value));
   }

   public PropertiesMap copyOf(PropertiesMap propertiesMap) {
      return new PropertiesMap(ImmutableList.builder().addAll(this.values).addAll(propertiesMap.values).build());
   }

   public static PropertiesMap withValues(Property.Value... values) {
      return new PropertiesMap(List.of(values));
   }

   public String asString() {
      return (String)this.values.stream().sorted(COMPARATOR).map(Property.Value::toString).collect(Collectors.joining(","));
   }

   public String toString() {
      return this.asString();
   }

   public List values() {
      return this.values;
   }
}
