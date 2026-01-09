package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.PacketCodec;

public record AssetInfo(Identifier id, Identifier texturePath) {
   public static final Codec CODEC;
   public static final MapCodec MAP_CODEC;
   public static final PacketCodec PACKET_CODEC;

   public AssetInfo(Identifier id) {
      this(id, id.withPath((path) -> {
         return "textures/" + path + ".png";
      }));
   }

   public AssetInfo(Identifier identifier, Identifier identifier2) {
      this.id = identifier;
      this.texturePath = identifier2;
   }

   public Identifier id() {
      return this.id;
   }

   public Identifier texturePath() {
      return this.texturePath;
   }

   static {
      CODEC = Identifier.CODEC.xmap(AssetInfo::new, AssetInfo::id);
      MAP_CODEC = CODEC.fieldOf("asset_id");
      PACKET_CODEC = PacketCodec.tuple(Identifier.PACKET_CODEC, AssetInfo::id, AssetInfo::new);
   }
}
