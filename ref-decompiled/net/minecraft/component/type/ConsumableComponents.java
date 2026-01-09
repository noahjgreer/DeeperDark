package net.minecraft.component.type;

import java.util.List;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.item.consume.ClearAllEffectsConsumeEffect;
import net.minecraft.item.consume.RemoveEffectsConsumeEffect;
import net.minecraft.item.consume.TeleportRandomlyConsumeEffect;
import net.minecraft.item.consume.UseAction;
import net.minecraft.sound.SoundEvents;

public class ConsumableComponents {
   public static final ConsumableComponent FOOD = food().build();
   public static final ConsumableComponent DRINK = drink().build();
   public static final ConsumableComponent HONEY_BOTTLE;
   public static final ConsumableComponent OMINOUS_BOTTLE;
   public static final ConsumableComponent DRIED_KELP;
   public static final ConsumableComponent RAW_CHICKEN;
   public static final ConsumableComponent ENCHANTED_GOLDEN_APPLE;
   public static final ConsumableComponent GOLDEN_APPLE;
   public static final ConsumableComponent POISONOUS_POTATO;
   public static final ConsumableComponent PUFFERFISH;
   public static final ConsumableComponent ROTTEN_FLESH;
   public static final ConsumableComponent SPIDER_EYE;
   public static final ConsumableComponent MILK_BUCKET;
   public static final ConsumableComponent CHORUS_FRUIT;

   public static ConsumableComponent.Builder food() {
      return ConsumableComponent.builder().consumeSeconds(1.6F).useAction(UseAction.EAT).sound(SoundEvents.ENTITY_GENERIC_EAT).consumeParticles(true);
   }

   public static ConsumableComponent.Builder drink() {
      return ConsumableComponent.builder().consumeSeconds(1.6F).useAction(UseAction.DRINK).sound(SoundEvents.ENTITY_GENERIC_DRINK).consumeParticles(false);
   }

   static {
      HONEY_BOTTLE = drink().consumeSeconds(2.0F).sound(SoundEvents.ITEM_HONEY_BOTTLE_DRINK).consumeEffect(new RemoveEffectsConsumeEffect(StatusEffects.POISON)).build();
      OMINOUS_BOTTLE = drink().finishSound(SoundEvents.ITEM_OMINOUS_BOTTLE_DISPOSE).build();
      DRIED_KELP = food().consumeSeconds(0.8F).build();
      RAW_CHICKEN = food().consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.HUNGER, 600, 0), 0.3F)).build();
      ENCHANTED_GOLDEN_APPLE = food().consumeEffect(new ApplyEffectsConsumeEffect(List.of(new StatusEffectInstance(StatusEffects.REGENERATION, 400, 1), new StatusEffectInstance(StatusEffects.RESISTANCE, 6000, 0), new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 6000, 0), new StatusEffectInstance(StatusEffects.ABSORPTION, 2400, 3)))).build();
      GOLDEN_APPLE = food().consumeEffect(new ApplyEffectsConsumeEffect(List.of(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 1), new StatusEffectInstance(StatusEffects.ABSORPTION, 2400, 0)))).build();
      POISONOUS_POTATO = food().consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 0), 0.6F)).build();
      PUFFERFISH = food().consumeEffect(new ApplyEffectsConsumeEffect(List.of(new StatusEffectInstance(StatusEffects.POISON, 1200, 1), new StatusEffectInstance(StatusEffects.HUNGER, 300, 2), new StatusEffectInstance(StatusEffects.NAUSEA, 300, 0)))).build();
      ROTTEN_FLESH = food().consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.HUNGER, 600, 0), 0.8F)).build();
      SPIDER_EYE = food().consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 0))).build();
      MILK_BUCKET = drink().consumeEffect(ClearAllEffectsConsumeEffect.INSTANCE).build();
      CHORUS_FRUIT = food().consumeEffect(new TeleportRandomlyConsumeEffect()).build();
   }
}
