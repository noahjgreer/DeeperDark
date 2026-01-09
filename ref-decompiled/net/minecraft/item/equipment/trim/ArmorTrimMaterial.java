package net.minecraft.item.equipment.trim;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public record ArmorTrimMaterial(ArmorTrimAssets assets, Text description) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(ArmorTrimAssets.CODEC.forGetter(ArmorTrimMaterial::assets), TextCodecs.CODEC.fieldOf("description").forGetter(ArmorTrimMaterial::description)).apply(instance, ArmorTrimMaterial::new);
   });
   public static final PacketCodec PACKET_CODEC;
   public static final Codec ENTRY_CODEC;
   public static final PacketCodec ENTRY_PACKET_CODEC;

   public ArmorTrimMaterial(ArmorTrimAssets armorTrimAssets, Text text) {
      this.assets = armorTrimAssets;
      this.description = text;
   }

   public ArmorTrimAssets assets() {
      return this.assets;
   }

   public Text description() {
      return this.description;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(ArmorTrimAssets.PACKET_CODEC, ArmorTrimMaterial::assets, TextCodecs.REGISTRY_PACKET_CODEC, ArmorTrimMaterial::description, ArmorTrimMaterial::new);
      ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.TRIM_MATERIAL, CODEC);
      ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(RegistryKeys.TRIM_MATERIAL, PACKET_CODEC);
   }
}
