package net.minecraft.client.font;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public interface FontLoader {
   MapCodec CODEC = FontType.CODEC.dispatchMap(FontLoader::getType, FontType::getLoaderCodec);

   FontType getType();

   Either build();

   @Environment(EnvType.CLIENT)
   public static record Provider(FontLoader definition, FontFilterType.FilterMap filter) {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(FontLoader.CODEC.forGetter(Provider::definition), FontFilterType.FilterMap.CODEC.optionalFieldOf("filter", FontFilterType.FilterMap.NO_FILTER).forGetter(Provider::filter)).apply(instance, Provider::new);
      });

      public Provider(FontLoader fontLoader, FontFilterType.FilterMap filterMap) {
         this.definition = fontLoader;
         this.filter = filterMap;
      }

      public FontLoader definition() {
         return this.definition;
      }

      public FontFilterType.FilterMap filter() {
         return this.filter;
      }
   }

   @Environment(EnvType.CLIENT)
   public static record Reference(Identifier id) {
      public Reference(Identifier identifier) {
         this.id = identifier;
      }

      public Identifier id() {
         return this.id;
      }
   }

   @Environment(EnvType.CLIENT)
   public interface Loadable {
      Font load(ResourceManager resourceManager) throws IOException;
   }
}
