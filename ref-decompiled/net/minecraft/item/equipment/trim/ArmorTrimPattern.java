package net.minecraft.item.equipment.trim;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

public record ArmorTrimPattern(Identifier assetId, Text description, boolean decal) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Identifier.CODEC.fieldOf("asset_id").forGetter(ArmorTrimPattern::assetId), TextCodecs.CODEC.fieldOf("description").forGetter(ArmorTrimPattern::description), Codec.BOOL.fieldOf("decal").orElse(false).forGetter(ArmorTrimPattern::decal)).apply(instance, ArmorTrimPattern::new);
   });
   public static final PacketCodec PACKET_CODEC;
   public static final Codec ENTRY_CODEC;
   public static final PacketCodec ENTRY_PACKET_CODEC;

   public ArmorTrimPattern(Identifier identifier, Text text, boolean bl) {
      this.assetId = identifier;
      this.description = text;
      this.decal = bl;
   }

   public Text getDescription(RegistryEntry material) {
      return this.description.copy().fillStyle(((ArmorTrimMaterial)material.value()).description().getStyle());
   }

   public Identifier assetId() {
      return this.assetId;
   }

   public Text description() {
      return this.description;
   }

   public boolean decal() {
      return this.decal;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(Identifier.PACKET_CODEC, ArmorTrimPattern::assetId, TextCodecs.REGISTRY_PACKET_CODEC, ArmorTrimPattern::description, PacketCodecs.BOOLEAN, ArmorTrimPattern::decal, ArmorTrimPattern::new);
      ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.TRIM_PATTERN, CODEC);
      ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(RegistryKeys.TRIM_PATTERN, PACKET_CODEC);
   }
}
