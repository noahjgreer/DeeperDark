package net.minecraft.client.texture.atlas;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

@Environment(EnvType.CLIENT)
public class AtlasSourceManager {
   private static final Codecs.IdMapper ID_MAPPER = new Codecs.IdMapper();
   public static final Codec TYPE_CODEC;
   public static final Codec LIST_CODEC;

   public static void bootstrap() {
      ID_MAPPER.put(Identifier.ofVanilla("single"), SingleAtlasSource.CODEC);
      ID_MAPPER.put(Identifier.ofVanilla("directory"), DirectoryAtlasSource.CODEC);
      ID_MAPPER.put(Identifier.ofVanilla("filter"), FilterAtlasSource.CODEC);
      ID_MAPPER.put(Identifier.ofVanilla("unstitch"), UnstitchAtlasSource.CODEC);
      ID_MAPPER.put(Identifier.ofVanilla("paletted_permutations"), PalettedPermutationsAtlasSource.CODEC);
   }

   static {
      TYPE_CODEC = ID_MAPPER.getCodec(Identifier.CODEC).dispatch(AtlasSource::getCodec, (codec) -> {
         return codec;
      });
      LIST_CODEC = TYPE_CODEC.listOf().fieldOf("sources").codec();
   }
}
