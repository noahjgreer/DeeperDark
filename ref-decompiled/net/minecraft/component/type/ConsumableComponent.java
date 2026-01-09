package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.ConsumeEffect;
import net.minecraft.item.consume.PlaySoundConsumeEffect;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public record ConsumableComponent(float consumeSeconds, UseAction useAction, RegistryEntry sound, boolean hasConsumeParticles, List onConsumeEffects) {
   public static final float DEFAULT_CONSUME_SECONDS = 1.6F;
   private static final int PARTICLES_AND_SOUND_TICK_INTERVAL = 4;
   private static final float PARTICLES_AND_SOUND_TICK_THRESHOLD = 0.21875F;
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Codecs.NON_NEGATIVE_FLOAT.optionalFieldOf("consume_seconds", 1.6F).forGetter(ConsumableComponent::consumeSeconds), UseAction.CODEC.optionalFieldOf("animation", UseAction.EAT).forGetter(ConsumableComponent::useAction), SoundEvent.ENTRY_CODEC.optionalFieldOf("sound", SoundEvents.ENTITY_GENERIC_EAT).forGetter(ConsumableComponent::sound), Codec.BOOL.optionalFieldOf("has_consume_particles", true).forGetter(ConsumableComponent::hasConsumeParticles), ConsumeEffect.CODEC.listOf().optionalFieldOf("on_consume_effects", List.of()).forGetter(ConsumableComponent::onConsumeEffects)).apply(instance, ConsumableComponent::new);
   });
   public static final PacketCodec PACKET_CODEC;

   public ConsumableComponent(float f, UseAction useAction, RegistryEntry registryEntry, boolean bl, List list) {
      this.consumeSeconds = f;
      this.useAction = useAction;
      this.sound = registryEntry;
      this.hasConsumeParticles = bl;
      this.onConsumeEffects = list;
   }

   public ActionResult consume(LivingEntity user, ItemStack stack, Hand hand) {
      if (!this.canConsume(user, stack)) {
         return ActionResult.FAIL;
      } else {
         boolean bl = this.getConsumeTicks() > 0;
         if (bl) {
            user.setCurrentHand(hand);
            return ActionResult.CONSUME;
         } else {
            ItemStack itemStack = this.finishConsumption(user.getWorld(), user, stack);
            return ActionResult.CONSUME.withNewHandStack(itemStack);
         }
      }
   }

   public ItemStack finishConsumption(World world, LivingEntity user, ItemStack stack) {
      Random random = user.getRandom();
      this.spawnParticlesAndPlaySound(random, user, stack, 16);
      if (user instanceof ServerPlayerEntity serverPlayerEntity) {
         serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
         Criteria.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
      }

      stack.streamAll(Consumable.class).forEach((consumable) -> {
         consumable.onConsume(world, user, stack, this);
      });
      if (!world.isClient) {
         this.onConsumeEffects.forEach((effect) -> {
            effect.onConsume(world, stack, user);
         });
      }

      user.emitGameEvent(this.useAction == UseAction.DRINK ? GameEvent.DRINK : GameEvent.EAT);
      stack.decrementUnlessCreative(1, user);
      return stack;
   }

   public boolean canConsume(LivingEntity user, ItemStack stack) {
      FoodComponent foodComponent = (FoodComponent)stack.get(DataComponentTypes.FOOD);
      if (foodComponent != null && user instanceof PlayerEntity playerEntity) {
         return playerEntity.canConsume(foodComponent.canAlwaysEat());
      } else {
         return true;
      }
   }

   public int getConsumeTicks() {
      return (int)(this.consumeSeconds * 20.0F);
   }

   public void spawnParticlesAndPlaySound(Random random, LivingEntity user, ItemStack stack, int particleCount) {
      float f = random.nextBoolean() ? 0.5F : 1.0F;
      float g = random.nextTriangular(1.0F, 0.2F);
      float h = 0.5F;
      float i = MathHelper.nextBetween(random, 0.9F, 1.0F);
      float j = this.useAction == UseAction.DRINK ? 0.5F : f;
      float k = this.useAction == UseAction.DRINK ? i : g;
      if (this.hasConsumeParticles) {
         user.spawnItemParticles(stack, particleCount);
      }

      SoundEvent var10000;
      if (user instanceof ConsumableSoundProvider consumableSoundProvider) {
         var10000 = consumableSoundProvider.getConsumeSound(stack);
      } else {
         var10000 = (SoundEvent)this.sound.value();
      }

      SoundEvent soundEvent = var10000;
      user.playSound(soundEvent, j, k);
   }

   public boolean shouldSpawnParticlesAndPlaySounds(int remainingUseTicks) {
      int i = this.getConsumeTicks() - remainingUseTicks;
      int j = (int)((float)this.getConsumeTicks() * 0.21875F);
      boolean bl = i > j;
      return bl && remainingUseTicks % 4 == 0;
   }

   public static Builder builder() {
      return new Builder();
   }

   public float consumeSeconds() {
      return this.consumeSeconds;
   }

   public UseAction useAction() {
      return this.useAction;
   }

   public RegistryEntry sound() {
      return this.sound;
   }

   public boolean hasConsumeParticles() {
      return this.hasConsumeParticles;
   }

   public List onConsumeEffects() {
      return this.onConsumeEffects;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(PacketCodecs.FLOAT, ConsumableComponent::consumeSeconds, UseAction.PACKET_CODEC, ConsumableComponent::useAction, SoundEvent.ENTRY_PACKET_CODEC, ConsumableComponent::sound, PacketCodecs.BOOLEAN, ConsumableComponent::hasConsumeParticles, ConsumeEffect.PACKET_CODEC.collect(PacketCodecs.toList()), ConsumableComponent::onConsumeEffects, ConsumableComponent::new);
   }

   public interface ConsumableSoundProvider {
      SoundEvent getConsumeSound(ItemStack stack);
   }

   public static class Builder {
      private float consumeSeconds = 1.6F;
      private UseAction useAction;
      private RegistryEntry sound;
      private boolean consumeParticles;
      private final List consumeEffects;

      Builder() {
         this.useAction = UseAction.EAT;
         this.sound = SoundEvents.ENTITY_GENERIC_EAT;
         this.consumeParticles = true;
         this.consumeEffects = new ArrayList();
      }

      public Builder consumeSeconds(float consumeSeconds) {
         this.consumeSeconds = consumeSeconds;
         return this;
      }

      public Builder useAction(UseAction useAction) {
         this.useAction = useAction;
         return this;
      }

      public Builder sound(RegistryEntry sound) {
         this.sound = sound;
         return this;
      }

      public Builder finishSound(RegistryEntry finishSound) {
         return this.consumeEffect(new PlaySoundConsumeEffect(finishSound));
      }

      public Builder consumeParticles(boolean consumeParticles) {
         this.consumeParticles = consumeParticles;
         return this;
      }

      public Builder consumeEffect(ConsumeEffect consumeEffect) {
         this.consumeEffects.add(consumeEffect);
         return this;
      }

      public ConsumableComponent build() {
         return new ConsumableComponent(this.consumeSeconds, this.useAction, this.sound, this.consumeParticles, this.consumeEffects);
      }
   }
}
