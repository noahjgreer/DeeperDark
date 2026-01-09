package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.item.consume.ClearAllEffectsConsumeEffect;
import net.minecraft.item.consume.ConsumeEffect;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record DeathProtectionComponent(List deathEffects) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(ConsumeEffect.CODEC.listOf().optionalFieldOf("death_effects", List.of()).forGetter(DeathProtectionComponent::deathEffects)).apply(instance, DeathProtectionComponent::new);
   });
   public static final PacketCodec PACKET_CODEC;
   public static final DeathProtectionComponent TOTEM_OF_UNDYING;

   public DeathProtectionComponent(List list) {
      this.deathEffects = list;
   }

   public void applyDeathEffects(ItemStack stack, LivingEntity entity) {
      Iterator var3 = this.deathEffects.iterator();

      while(var3.hasNext()) {
         ConsumeEffect consumeEffect = (ConsumeEffect)var3.next();
         consumeEffect.onConsume(entity.getWorld(), stack, entity);
      }

   }

   public List deathEffects() {
      return this.deathEffects;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(ConsumeEffect.PACKET_CODEC.collect(PacketCodecs.toList()), DeathProtectionComponent::deathEffects, DeathProtectionComponent::new);
      TOTEM_OF_UNDYING = new DeathProtectionComponent(List.of(new ClearAllEffectsConsumeEffect(), new ApplyEffectsConsumeEffect(List.of(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1), new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1), new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0)))));
   }
}
