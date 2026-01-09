package net.minecraft.data.tag;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricProvidedTagBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagBuilder;
import net.minecraft.registry.tag.TagKey;

public interface ProvidedTagBuilder extends FabricProvidedTagBuilder {
   ProvidedTagBuilder add(Object value);

   default ProvidedTagBuilder add(Object... values) {
      return this.add(Arrays.stream(values));
   }

   default ProvidedTagBuilder add(Collection values) {
      values.forEach(this::add);
      return this;
   }

   default ProvidedTagBuilder add(Stream values) {
      values.forEach(this::add);
      return this;
   }

   ProvidedTagBuilder addOptional(Object value);

   ProvidedTagBuilder addTag(TagKey tag);

   ProvidedTagBuilder addOptionalTag(TagKey tag);

   static ProvidedTagBuilder of(final TagBuilder builder) {
      return new ProvidedTagBuilder() {
         public ProvidedTagBuilder add(RegistryKey registryKey) {
            builder.add(registryKey.getValue());
            return this;
         }

         public ProvidedTagBuilder addOptional(RegistryKey registryKey) {
            builder.addOptional(registryKey.getValue());
            return this;
         }

         public ProvidedTagBuilder addTag(TagKey tag) {
            builder.addTag(tag.id());
            return this;
         }

         public ProvidedTagBuilder addOptionalTag(TagKey tag) {
            builder.addOptionalTag(tag.id());
            return this;
         }
      };
   }

   default ProvidedTagBuilder mapped(final Function mapper) {
      return new ProvidedTagBuilder(this) {
         public ProvidedTagBuilder add(Object value) {
            ProvidedTagBuilder.this.add(mapper.apply(value));
            return this;
         }

         public ProvidedTagBuilder addOptional(Object value) {
            ProvidedTagBuilder.this.add(mapper.apply(value));
            return this;
         }

         public ProvidedTagBuilder addTag(TagKey tag) {
            ProvidedTagBuilder.this.addTag(tag);
            return this;
         }

         public ProvidedTagBuilder addOptionalTag(TagKey tag) {
            ProvidedTagBuilder.this.addOptionalTag(tag);
            return this;
         }
      };
   }
}
