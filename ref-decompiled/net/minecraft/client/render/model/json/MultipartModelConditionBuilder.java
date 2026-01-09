package net.minecraft.client.render.model.json;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.state.property.Property;

@Environment(EnvType.CLIENT)
public class MultipartModelConditionBuilder {
   private final ImmutableMap.Builder values = ImmutableMap.builder();

   private void putTerms(Property property, SimpleMultipartModelSelector.Terms terms) {
      this.values.put(property.getName(), terms);
   }

   public final MultipartModelConditionBuilder put(Property property, Comparable value) {
      this.putTerms(property, new SimpleMultipartModelSelector.Terms(List.of(new SimpleMultipartModelSelector.Term(property.name(value), false))));
      return this;
   }

   @SafeVarargs
   public final MultipartModelConditionBuilder put(Property property, Comparable value, Comparable... values) {
      Stream var10000 = Stream.concat(Stream.of(value), Stream.of(values));
      Objects.requireNonNull(property);
      List list = var10000.map(property::name).sorted().distinct().map((valuex) -> {
         return new SimpleMultipartModelSelector.Term(valuex, false);
      }).toList();
      this.putTerms(property, new SimpleMultipartModelSelector.Terms(list));
      return this;
   }

   public final MultipartModelConditionBuilder replace(Property property, Comparable value) {
      this.putTerms(property, new SimpleMultipartModelSelector.Terms(List.of(new SimpleMultipartModelSelector.Term(property.name(value), true))));
      return this;
   }

   public MultipartModelCondition build() {
      return new SimpleMultipartModelSelector(this.values.buildOrThrow());
   }
}
