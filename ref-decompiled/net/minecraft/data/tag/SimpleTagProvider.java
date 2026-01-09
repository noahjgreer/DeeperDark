package net.minecraft.data.tag;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagBuilder;
import net.minecraft.registry.tag.TagKey;

public abstract class SimpleTagProvider extends TagProvider {
   protected SimpleTagProvider(DataOutput dataOutput, RegistryKey registryKey, CompletableFuture completableFuture) {
      super(dataOutput, registryKey, completableFuture);
   }

   protected ProvidedTagBuilder builder(TagKey tag) {
      TagBuilder tagBuilder = this.getTagBuilder(tag);
      return ProvidedTagBuilder.of(tagBuilder);
   }
}
