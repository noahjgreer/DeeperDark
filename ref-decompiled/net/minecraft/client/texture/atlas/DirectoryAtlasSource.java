package net.minecraft.client.texture.atlas;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public record DirectoryAtlasSource(String sourcePath, String idPrefix) implements AtlasSource {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codec.STRING.fieldOf("source").forGetter(DirectoryAtlasSource::sourcePath), Codec.STRING.fieldOf("prefix").forGetter(DirectoryAtlasSource::idPrefix)).apply(instance, DirectoryAtlasSource::new);
   });

   public DirectoryAtlasSource(String source, String prefix) {
      this.sourcePath = source;
      this.idPrefix = prefix;
   }

   public void load(ResourceManager resourceManager, AtlasSource.SpriteRegions regions) {
      ResourceFinder resourceFinder = new ResourceFinder("textures/" + this.sourcePath, ".png");
      resourceFinder.findResources(resourceManager).forEach((id, resource) -> {
         Identifier identifier = resourceFinder.toResourceId(id).withPrefixedPath(this.idPrefix);
         regions.add(identifier, resource);
      });
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public String sourcePath() {
      return this.sourcePath;
   }

   public String idPrefix() {
      return this.idPrefix;
   }
}
