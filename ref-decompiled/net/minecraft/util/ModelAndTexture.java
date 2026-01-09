package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.PacketCodec;

public record ModelAndTexture(Object model, AssetInfo asset) {
   public ModelAndTexture(Object model, Identifier assetId) {
      this(model, new AssetInfo(assetId));
   }

   public ModelAndTexture(Object object, AssetInfo assetInfo) {
      this.model = object;
      this.asset = assetInfo;
   }

   public static MapCodec createMapCodec(Codec modelCodec, Object model) {
      return RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(modelCodec.optionalFieldOf("model", model).forGetter(ModelAndTexture::model), AssetInfo.MAP_CODEC.forGetter(ModelAndTexture::asset)).apply(instance, ModelAndTexture::new);
      });
   }

   public static PacketCodec createPacketCodec(PacketCodec modelPacketCodec) {
      return PacketCodec.tuple(modelPacketCodec, ModelAndTexture::model, AssetInfo.PACKET_CODEC, ModelAndTexture::asset, ModelAndTexture::new);
   }

   public Object model() {
      return this.model;
   }

   public AssetInfo asset() {
      return this.asset;
   }
}
