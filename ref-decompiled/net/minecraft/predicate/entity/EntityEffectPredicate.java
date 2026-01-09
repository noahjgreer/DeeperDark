package net.minecraft.predicate.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.predicate.NumberRange;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.Nullable;

public record EntityEffectPredicate(Map effects) {
   public static final Codec CODEC;

   public EntityEffectPredicate(Map effects) {
      this.effects = effects;
   }

   public boolean test(Entity entity) {
      boolean var10000;
      if (entity instanceof LivingEntity livingEntity) {
         if (this.test(livingEntity.getActiveStatusEffects())) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   public boolean test(LivingEntity livingEntity) {
      return this.test(livingEntity.getActiveStatusEffects());
   }

   public boolean test(Map effects) {
      Iterator var2 = this.effects.entrySet().iterator();

      Map.Entry entry;
      StatusEffectInstance statusEffectInstance;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         entry = (Map.Entry)var2.next();
         statusEffectInstance = (StatusEffectInstance)effects.get(entry.getKey());
      } while(((EffectData)entry.getValue()).test(statusEffectInstance));

      return false;
   }

   public Map effects() {
      return this.effects;
   }

   static {
      CODEC = Codec.unboundedMap(StatusEffect.ENTRY_CODEC, EntityEffectPredicate.EffectData.CODEC).xmap(EntityEffectPredicate::new, EntityEffectPredicate::effects);
   }

   public static record EffectData(NumberRange.IntRange amplifier, NumberRange.IntRange duration, Optional ambient, Optional visible) {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(NumberRange.IntRange.CODEC.optionalFieldOf("amplifier", NumberRange.IntRange.ANY).forGetter(EffectData::amplifier), NumberRange.IntRange.CODEC.optionalFieldOf("duration", NumberRange.IntRange.ANY).forGetter(EffectData::duration), Codec.BOOL.optionalFieldOf("ambient").forGetter(EffectData::ambient), Codec.BOOL.optionalFieldOf("visible").forGetter(EffectData::visible)).apply(instance, EffectData::new);
      });

      public EffectData() {
         this(NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, Optional.empty(), Optional.empty());
      }

      public EffectData(NumberRange.IntRange amplifier, NumberRange.IntRange duration, Optional optional, Optional optional2) {
         this.amplifier = amplifier;
         this.duration = duration;
         this.ambient = optional;
         this.visible = optional2;
      }

      public boolean test(@Nullable StatusEffectInstance statusEffectInstance) {
         if (statusEffectInstance == null) {
            return false;
         } else if (!this.amplifier.test(statusEffectInstance.getAmplifier())) {
            return false;
         } else if (!this.duration.test(statusEffectInstance.getDuration())) {
            return false;
         } else if (this.ambient.isPresent() && (Boolean)this.ambient.get() != statusEffectInstance.isAmbient()) {
            return false;
         } else {
            return !this.visible.isPresent() || (Boolean)this.visible.get() == statusEffectInstance.shouldShowParticles();
         }
      }

      public NumberRange.IntRange amplifier() {
         return this.amplifier;
      }

      public NumberRange.IntRange duration() {
         return this.duration;
      }

      public Optional ambient() {
         return this.ambient;
      }

      public Optional visible() {
         return this.visible;
      }
   }

   public static class Builder {
      private final ImmutableMap.Builder effects = ImmutableMap.builder();

      public static Builder create() {
         return new Builder();
      }

      public Builder addEffect(RegistryEntry effect) {
         this.effects.put(effect, new EffectData());
         return this;
      }

      public Builder addEffect(RegistryEntry effect, EffectData effectData) {
         this.effects.put(effect, effectData);
         return this;
      }

      public Optional build() {
         return Optional.of(new EntityEffectPredicate(this.effects.build()));
      }
   }
}
