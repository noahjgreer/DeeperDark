package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public record DamageResistantComponent(TagKey types) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(TagKey.codec(RegistryKeys.DAMAGE_TYPE).fieldOf("types").forGetter(DamageResistantComponent::types)).apply(instance, DamageResistantComponent::new);
   });
   public static final PacketCodec PACKET_CODEC;

   public DamageResistantComponent(TagKey tagKey) {
      this.types = tagKey;
   }

   public boolean resists(DamageSource damageSource) {
      return damageSource.isIn(this.types);
   }

   public TagKey types() {
      return this.types;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(TagKey.packetCodec(RegistryKeys.DAMAGE_TYPE), DamageResistantComponent::types, DamageResistantComponent::new);
   }
}
