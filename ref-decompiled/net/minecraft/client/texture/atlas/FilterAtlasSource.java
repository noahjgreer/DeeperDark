package net.minecraft.client.texture.atlas;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.metadata.BlockEntry;

@Environment(EnvType.CLIENT)
public record FilterAtlasSource(BlockEntry pattern) implements AtlasSource {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(BlockEntry.CODEC.fieldOf("pattern").forGetter(FilterAtlasSource::pattern)).apply(instance, FilterAtlasSource::new);
   });

   public FilterAtlasSource(final BlockEntry pattern) {
      this.pattern = pattern;
   }

   public void load(ResourceManager resourceManager, AtlasSource.SpriteRegions regions) {
      regions.removeIf(this.pattern.getIdentifierPredicate());
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public BlockEntry pattern() {
      return this.pattern;
   }
}
