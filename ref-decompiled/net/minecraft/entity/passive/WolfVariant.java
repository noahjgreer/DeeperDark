package net.minecraft.entity.passive;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.entity.VariantSelectorProvider;
import net.minecraft.entity.spawn.SpawnConditionSelectors;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.util.AssetInfo;

public record WolfVariant(WolfAssetInfo assetInfo, SpawnConditionSelectors spawnConditions) implements VariantSelectorProvider {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(WolfVariant.WolfAssetInfo.CODEC.fieldOf("assets").forGetter(WolfVariant::assetInfo), SpawnConditionSelectors.CODEC.fieldOf("spawn_conditions").forGetter(WolfVariant::spawnConditions)).apply(instance, WolfVariant::new);
   });
   public static final Codec NETWORK_CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(WolfVariant.WolfAssetInfo.CODEC.fieldOf("assets").forGetter(WolfVariant::assetInfo)).apply(instance, WolfVariant::new);
   });
   public static final Codec ENTRY_CODEC;
   public static final PacketCodec ENTRY_PACKET_CODEC;

   private WolfVariant(WolfAssetInfo assetInfo) {
      this(assetInfo, SpawnConditionSelectors.EMPTY);
   }

   public WolfVariant(WolfAssetInfo wolfAssetInfo, SpawnConditionSelectors spawnConditionSelectors) {
      this.assetInfo = wolfAssetInfo;
      this.spawnConditions = spawnConditionSelectors;
   }

   public List getSelectors() {
      return this.spawnConditions.selectors();
   }

   public WolfAssetInfo assetInfo() {
      return this.assetInfo;
   }

   public SpawnConditionSelectors spawnConditions() {
      return this.spawnConditions;
   }

   static {
      ENTRY_CODEC = RegistryFixedCodec.of(RegistryKeys.WOLF_VARIANT);
      ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(RegistryKeys.WOLF_VARIANT);
   }

   public static record WolfAssetInfo(AssetInfo wild, AssetInfo tame, AssetInfo angry) {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(AssetInfo.CODEC.fieldOf("wild").forGetter(WolfAssetInfo::wild), AssetInfo.CODEC.fieldOf("tame").forGetter(WolfAssetInfo::tame), AssetInfo.CODEC.fieldOf("angry").forGetter(WolfAssetInfo::angry)).apply(instance, WolfAssetInfo::new);
      });

      public WolfAssetInfo(AssetInfo assetInfo, AssetInfo assetInfo2, AssetInfo assetInfo3) {
         this.wild = assetInfo;
         this.tame = assetInfo2;
         this.angry = assetInfo3;
      }

      public AssetInfo wild() {
         return this.wild;
      }

      public AssetInfo tame() {
         return this.tame;
      }

      public AssetInfo angry() {
         return this.angry;
      }
   }
}
