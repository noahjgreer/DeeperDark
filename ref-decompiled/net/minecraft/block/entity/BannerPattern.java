package net.minecraft.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.util.Identifier;

public record BannerPattern(Identifier assetId, String translationKey) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Identifier.CODEC.fieldOf("asset_id").forGetter(BannerPattern::assetId), Codec.STRING.fieldOf("translation_key").forGetter(BannerPattern::translationKey)).apply(instance, BannerPattern::new);
   });
   public static final PacketCodec PACKET_CODEC;
   public static final Codec ENTRY_CODEC;
   public static final PacketCodec ENTRY_PACKET_CODEC;

   public BannerPattern(Identifier identifier, String string) {
      this.assetId = identifier;
      this.translationKey = string;
   }

   public Identifier assetId() {
      return this.assetId;
   }

   public String translationKey() {
      return this.translationKey;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(Identifier.PACKET_CODEC, BannerPattern::assetId, PacketCodecs.STRING, BannerPattern::translationKey, BannerPattern::new);
      ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.BANNER_PATTERN, CODEC);
      ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(RegistryKeys.BANNER_PATTERN, PACKET_CODEC);
   }
}
