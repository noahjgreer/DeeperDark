package net.minecraft.registry.tag;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;

public record TagFile(List entries, boolean replace) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(TagEntry.CODEC.listOf().fieldOf("values").forGetter(TagFile::entries), Codec.BOOL.optionalFieldOf("replace", false).forGetter(TagFile::replace)).apply(instance, TagFile::new);
   });

   public TagFile(List list, boolean bl) {
      this.entries = list;
      this.replace = bl;
   }

   public List entries() {
      return this.entries;
   }

   public boolean replace() {
      return this.replace;
   }
}
