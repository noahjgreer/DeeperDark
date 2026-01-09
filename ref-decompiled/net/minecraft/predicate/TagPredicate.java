package net.minecraft.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;

public record TagPredicate(TagKey tag, boolean expected) {
   public TagPredicate(TagKey tag, boolean expected) {
      this.tag = tag;
      this.expected = expected;
   }

   public static Codec createCodec(RegistryKey registryRef) {
      return RecordCodecBuilder.create((instance) -> {
         return instance.group(TagKey.unprefixedCodec(registryRef).fieldOf("id").forGetter(TagPredicate::tag), Codec.BOOL.fieldOf("expected").forGetter(TagPredicate::expected)).apply(instance, TagPredicate::new);
      });
   }

   public static TagPredicate expected(TagKey tag) {
      return new TagPredicate(tag, true);
   }

   public static TagPredicate unexpected(TagKey tag) {
      return new TagPredicate(tag, false);
   }

   public boolean test(RegistryEntry registryEntry) {
      return registryEntry.isIn(this.tag) == this.expected;
   }

   public TagKey tag() {
      return this.tag;
   }

   public boolean expected() {
      return this.expected;
   }
}
