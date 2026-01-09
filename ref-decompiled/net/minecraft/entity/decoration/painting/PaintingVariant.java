package net.minecraft.entity.decoration.painting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

public record PaintingVariant(int width, int height, Identifier assetId, Optional title, Optional author) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Codecs.rangedInt(1, 16).fieldOf("width").forGetter(PaintingVariant::width), Codecs.rangedInt(1, 16).fieldOf("height").forGetter(PaintingVariant::height), Identifier.CODEC.fieldOf("asset_id").forGetter(PaintingVariant::assetId), TextCodecs.CODEC.optionalFieldOf("title").forGetter(PaintingVariant::title), TextCodecs.CODEC.optionalFieldOf("author").forGetter(PaintingVariant::author)).apply(instance, PaintingVariant::new);
   });
   public static final PacketCodec PACKET_CODEC;
   public static final Codec ENTRY_CODEC;
   public static final PacketCodec ENTRY_PACKET_CODEC;

   public PaintingVariant(int width, int height, Identifier identifier, Optional optional, Optional optional2) {
      this.width = width;
      this.height = height;
      this.assetId = identifier;
      this.title = optional;
      this.author = optional2;
   }

   public int getArea() {
      return this.width() * this.height();
   }

   public int width() {
      return this.width;
   }

   public int height() {
      return this.height;
   }

   public Identifier assetId() {
      return this.assetId;
   }

   public Optional title() {
      return this.title;
   }

   public Optional author() {
      return this.author;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT, PaintingVariant::width, PacketCodecs.VAR_INT, PaintingVariant::height, Identifier.PACKET_CODEC, PaintingVariant::assetId, TextCodecs.OPTIONAL_UNLIMITED_REGISTRY_PACKET_CODEC, PaintingVariant::title, TextCodecs.OPTIONAL_UNLIMITED_REGISTRY_PACKET_CODEC, PaintingVariant::author, PaintingVariant::new);
      ENTRY_CODEC = RegistryFixedCodec.of(RegistryKeys.PAINTING_VARIANT);
      ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(RegistryKeys.PAINTING_VARIANT, PACKET_CODEC);
   }
}
