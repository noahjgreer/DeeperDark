package net.minecraft.item.equipment.trim;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

public record ArmorTrimAssets(AssetId base, Map overrides) {
   public static final String field_56322 = "_";
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(ArmorTrimAssets.AssetId.CODEC.fieldOf("asset_name").forGetter(ArmorTrimAssets::base), Codec.unboundedMap(RegistryKey.createCodec(EquipmentAssetKeys.REGISTRY_KEY), ArmorTrimAssets.AssetId.CODEC).optionalFieldOf("override_armor_assets", Map.of()).forGetter(ArmorTrimAssets::overrides)).apply(instance, ArmorTrimAssets::new);
   });
   public static final PacketCodec PACKET_CODEC;
   public static final ArmorTrimAssets QUARTZ;
   public static final ArmorTrimAssets IRON;
   public static final ArmorTrimAssets NETHERITE;
   public static final ArmorTrimAssets REDSTONE;
   public static final ArmorTrimAssets COPPER;
   public static final ArmorTrimAssets GOLD;
   public static final ArmorTrimAssets EMERALD;
   public static final ArmorTrimAssets DIAMOND;
   public static final ArmorTrimAssets LAPIS;
   public static final ArmorTrimAssets AMETHYST;
   public static final ArmorTrimAssets RESIN;

   public ArmorTrimAssets(AssetId assetId, Map map) {
      this.base = assetId;
      this.overrides = map;
   }

   public static ArmorTrimAssets of(String suffix) {
      return new ArmorTrimAssets(new AssetId(suffix), Map.of());
   }

   public static ArmorTrimAssets of(String suffix, Map overrides) {
      return new ArmorTrimAssets(new AssetId(suffix), Map.copyOf(Maps.transformValues(overrides, AssetId::new)));
   }

   public AssetId getAssetId(RegistryKey equipmentAsset) {
      return (AssetId)this.overrides.getOrDefault(equipmentAsset, this.base);
   }

   public AssetId base() {
      return this.base;
   }

   public Map overrides() {
      return this.overrides;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(ArmorTrimAssets.AssetId.PACKET_CODEC, ArmorTrimAssets::base, PacketCodecs.map(Object2ObjectOpenHashMap::new, RegistryKey.createPacketCodec(EquipmentAssetKeys.REGISTRY_KEY), ArmorTrimAssets.AssetId.PACKET_CODEC), ArmorTrimAssets::overrides, ArmorTrimAssets::new);
      QUARTZ = of("quartz");
      IRON = of("iron", Map.of(EquipmentAssetKeys.IRON, "iron_darker"));
      NETHERITE = of("netherite", Map.of(EquipmentAssetKeys.NETHERITE, "netherite_darker"));
      REDSTONE = of("redstone");
      COPPER = of("copper");
      GOLD = of("gold", Map.of(EquipmentAssetKeys.GOLD, "gold_darker"));
      EMERALD = of("emerald");
      DIAMOND = of("diamond", Map.of(EquipmentAssetKeys.DIAMOND, "diamond_darker"));
      LAPIS = of("lapis");
      AMETHYST = of("amethyst");
      RESIN = of("resin");
   }

   public static record AssetId(String suffix) {
      public static final Codec CODEC;
      public static final PacketCodec PACKET_CODEC;

      public AssetId(String string) {
         if (!Identifier.isPathValid(string)) {
            throw new IllegalArgumentException("Invalid string to use as a resource path element: " + string);
         } else {
            this.suffix = string;
         }
      }

      public String suffix() {
         return this.suffix;
      }

      static {
         CODEC = Codecs.IDENTIFIER_PATH.xmap(AssetId::new, AssetId::suffix);
         PACKET_CODEC = PacketCodecs.STRING.xmap(AssetId::new, AssetId::suffix);
      }
   }
}
