package net.minecraft.data.tag;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.data.DataOutput;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagBuilder;
import net.minecraft.registry.tag.TagKey;

public abstract class ValueLookupTagProvider extends TagProvider {
   private final Function valueToKey;

   public ValueLookupTagProvider(DataOutput output, RegistryKey registryRef, CompletableFuture registriesFuture, Function valueToKey) {
      super(output, registryRef, registriesFuture);
      this.valueToKey = valueToKey;
   }

   public ValueLookupTagProvider(DataOutput output, RegistryKey registryRef, CompletableFuture registriesFuture, CompletableFuture parentTagLookupFuture, Function valueToKey) {
      super(output, registryRef, registriesFuture, parentTagLookupFuture);
      this.valueToKey = valueToKey;
   }

   protected ProvidedTagBuilder builder(TagKey tag) {
      TagBuilder tagBuilder = this.getTagBuilder(tag);
      return ProvidedTagBuilder.of(tagBuilder).mapped(this.valueToKey);
   }
}
