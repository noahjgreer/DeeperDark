package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

public record UseCooldownComponent(float seconds, Optional cooldownGroup) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Codecs.POSITIVE_FLOAT.fieldOf("seconds").forGetter(UseCooldownComponent::seconds), Identifier.CODEC.optionalFieldOf("cooldown_group").forGetter(UseCooldownComponent::cooldownGroup)).apply(instance, UseCooldownComponent::new);
   });
   public static final PacketCodec PACKET_CODEC;

   public UseCooldownComponent(float seconds) {
      this(seconds, Optional.empty());
   }

   public UseCooldownComponent(float f, Optional optional) {
      this.seconds = f;
      this.cooldownGroup = optional;
   }

   public int getCooldownTicks() {
      return (int)(this.seconds * 20.0F);
   }

   public void set(ItemStack stack, LivingEntity user) {
      if (user instanceof PlayerEntity playerEntity) {
         playerEntity.getItemCooldownManager().set(stack, this.getCooldownTicks());
      }

   }

   public float seconds() {
      return this.seconds;
   }

   public Optional cooldownGroup() {
      return this.cooldownGroup;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(PacketCodecs.FLOAT, UseCooldownComponent::seconds, Identifier.PACKET_CODEC.collect(PacketCodecs::optional), UseCooldownComponent::cooldownGroup, UseCooldownComponent::new);
   }
}
