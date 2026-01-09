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

public record FrogVariant(AssetInfo assetInfo, SpawnConditionSelectors spawnConditions) implements VariantSelectorProvider {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(AssetInfo.MAP_CODEC.forGetter(FrogVariant::assetInfo), SpawnConditionSelectors.CODEC.fieldOf("spawn_conditions").forGetter(FrogVariant::spawnConditions)).apply(instance, FrogVariant::new);
   });
   public static final Codec NETWORK_CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(AssetInfo.MAP_CODEC.forGetter(FrogVariant::assetInfo)).apply(instance, FrogVariant::new);
   });
   public static final Codec ENTRY_CODEC;
   public static final PacketCodec PACKET_CODEC;

   private FrogVariant(AssetInfo assetInfo) {
      this(assetInfo, SpawnConditionSelectors.EMPTY);
   }

   public FrogVariant(AssetInfo assetInfo, SpawnConditionSelectors spawnConditionSelectors) {
      this.assetInfo = assetInfo;
      this.spawnConditions = spawnConditionSelectors;
   }

   public List getSelectors() {
      return this.spawnConditions.selectors();
   }

   public AssetInfo assetInfo() {
      return this.assetInfo;
   }

   public SpawnConditionSelectors spawnConditions() {
      return this.spawnConditions;
   }

   static {
      ENTRY_CODEC = RegistryFixedCodec.of(RegistryKeys.FROG_VARIANT);
      PACKET_CODEC = PacketCodecs.registryEntry(RegistryKeys.FROG_VARIANT);
   }
}
