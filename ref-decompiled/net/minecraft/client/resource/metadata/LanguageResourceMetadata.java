package net.minecraft.client.resource.metadata;

import com.mojang.serialization.Codec;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;

@Environment(EnvType.CLIENT)
public record LanguageResourceMetadata(Map definitions) {
   public static final Codec LANGUAGE_CODE_CODEC = Codec.string(1, 16);
   public static final Codec CODEC;
   public static final ResourceMetadataSerializer SERIALIZER;

   public LanguageResourceMetadata(Map map) {
      this.definitions = map;
   }

   public Map definitions() {
      return this.definitions;
   }

   static {
      CODEC = Codec.unboundedMap(LANGUAGE_CODE_CODEC, LanguageDefinition.CODEC).xmap(LanguageResourceMetadata::new, LanguageResourceMetadata::definitions);
      SERIALIZER = new ResourceMetadataSerializer("language", CODEC);
   }
}
