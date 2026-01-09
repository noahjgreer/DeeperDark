package net.minecraft.entity.effect;

import com.google.common.collect.ComparisonChain;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class StatusEffectInstance implements Comparable {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int INFINITE = -1;
   public static final int MIN_AMPLIFIER = 0;
   public static final int MAX_AMPLIFIER = 255;
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(StatusEffect.ENTRY_CODEC.fieldOf("id").forGetter(StatusEffectInstance::getEffectType), StatusEffectInstance.Parameters.CODEC.forGetter(StatusEffectInstance::asParameters)).apply(instance, StatusEffectInstance::new);
   });
   public static final PacketCodec PACKET_CODEC;
   private final RegistryEntry type;
   private int duration;
   private int amplifier;
   private boolean ambient;
   private boolean showParticles;
   private boolean showIcon;
   @Nullable
   private StatusEffectInstance hiddenEffect;
   private final Fading fading;

   public StatusEffectInstance(RegistryEntry effect) {
      this(effect, 0, 0);
   }

   public StatusEffectInstance(RegistryEntry effect, int duration) {
      this(effect, duration, 0);
   }

   public StatusEffectInstance(RegistryEntry effect, int duration, int amplifier) {
      this(effect, duration, amplifier, false, true);
   }

   public StatusEffectInstance(RegistryEntry effect, int duration, int amplifier, boolean ambient, boolean visible) {
      this(effect, duration, amplifier, ambient, visible, visible);
   }

   public StatusEffectInstance(RegistryEntry effect, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon) {
      this(effect, duration, amplifier, ambient, showParticles, showIcon, (StatusEffectInstance)null);
   }

   public StatusEffectInstance(RegistryEntry effect, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon, @Nullable StatusEffectInstance hiddenEffect) {
      this.fading = new Fading();
      this.type = effect;
      this.duration = duration;
      this.amplifier = MathHelper.clamp(amplifier, 0, 255);
      this.ambient = ambient;
      this.showParticles = showParticles;
      this.showIcon = showIcon;
      this.hiddenEffect = hiddenEffect;
   }

   public StatusEffectInstance(StatusEffectInstance instance) {
      this.fading = new Fading();
      this.type = instance.type;
      this.copyFrom(instance);
   }

   private StatusEffectInstance(RegistryEntry effect, Parameters parameters) {
      this(effect, parameters.duration(), parameters.amplifier(), parameters.ambient(), parameters.showParticles(), parameters.showIcon(), (StatusEffectInstance)parameters.hiddenEffect().map((parametersx) -> {
         return new StatusEffectInstance(effect, parametersx);
      }).orElse((Object)null));
   }

   private Parameters asParameters() {
      return new Parameters(this.getAmplifier(), this.getDuration(), this.isAmbient(), this.shouldShowParticles(), this.shouldShowIcon(), Optional.ofNullable(this.hiddenEffect).map(StatusEffectInstance::asParameters));
   }

   public float getFadeFactor(LivingEntity entity, float tickProgress) {
      return this.fading.calculate(entity, tickProgress);
   }

   public ParticleEffect createParticle() {
      return ((StatusEffect)this.type.value()).createParticle(this);
   }

   void copyFrom(StatusEffectInstance that) {
      this.duration = that.duration;
      this.amplifier = that.amplifier;
      this.ambient = that.ambient;
      this.showParticles = that.showParticles;
      this.showIcon = that.showIcon;
   }

   public boolean upgrade(StatusEffectInstance that) {
      if (!this.type.equals(that.type)) {
         LOGGER.warn("This method should only be called for matching effects!");
      }

      boolean bl = false;
      if (that.amplifier > this.amplifier) {
         if (that.lastsShorterThan(this)) {
            StatusEffectInstance statusEffectInstance = this.hiddenEffect;
            this.hiddenEffect = new StatusEffectInstance(this);
            this.hiddenEffect.hiddenEffect = statusEffectInstance;
         }

         this.amplifier = that.amplifier;
         this.duration = that.duration;
         bl = true;
      } else if (this.lastsShorterThan(that)) {
         if (that.amplifier == this.amplifier) {
            this.duration = that.duration;
            bl = true;
         } else if (this.hiddenEffect == null) {
            this.hiddenEffect = new StatusEffectInstance(that);
         } else {
            this.hiddenEffect.upgrade(that);
         }
      }

      if (!that.ambient && this.ambient || bl) {
         this.ambient = that.ambient;
         bl = true;
      }

      if (that.showParticles != this.showParticles) {
         this.showParticles = that.showParticles;
         bl = true;
      }

      if (that.showIcon != this.showIcon) {
         this.showIcon = that.showIcon;
         bl = true;
      }

      return bl;
   }

   private boolean lastsShorterThan(StatusEffectInstance effect) {
      return !this.isInfinite() && (this.duration < effect.duration || effect.isInfinite());
   }

   public boolean isInfinite() {
      return this.duration == -1;
   }

   public boolean isDurationBelow(int duration) {
      return !this.isInfinite() && this.duration <= duration;
   }

   public StatusEffectInstance withScaledDuration(float durationMultiplier) {
      StatusEffectInstance statusEffectInstance = new StatusEffectInstance(this);
      statusEffectInstance.duration = statusEffectInstance.mapDuration((duration) -> {
         return Math.max(MathHelper.floor((float)duration * durationMultiplier), 1);
      });
      return statusEffectInstance;
   }

   public int mapDuration(Int2IntFunction mapper) {
      return !this.isInfinite() && this.duration != 0 ? mapper.applyAsInt(this.duration) : this.duration;
   }

   public RegistryEntry getEffectType() {
      return this.type;
   }

   public int getDuration() {
      return this.duration;
   }

   public int getAmplifier() {
      return this.amplifier;
   }

   public boolean isAmbient() {
      return this.ambient;
   }

   public boolean shouldShowParticles() {
      return this.showParticles;
   }

   public boolean shouldShowIcon() {
      return this.showIcon;
   }

   public boolean update(ServerWorld world, LivingEntity entity, Runnable hiddenEffectCallback) {
      if (!this.isActive()) {
         return false;
      } else {
         int i = this.isInfinite() ? entity.age : this.duration;
         if (((StatusEffect)this.type.value()).canApplyUpdateEffect(i, this.amplifier) && !((StatusEffect)this.type.value()).applyUpdateEffect(world, entity, this.amplifier)) {
            return false;
         } else {
            this.updateDuration();
            if (this.tickHiddenEffect()) {
               hiddenEffectCallback.run();
            }

            return this.isActive();
         }
      }
   }

   public void tickClient() {
      if (this.isActive()) {
         this.updateDuration();
         this.tickHiddenEffect();
      }

      this.fading.update(this);
   }

   private boolean isActive() {
      return this.isInfinite() || this.duration > 0;
   }

   private void updateDuration() {
      if (this.hiddenEffect != null) {
         this.hiddenEffect.updateDuration();
      }

      this.duration = this.mapDuration((duration) -> {
         return duration - 1;
      });
   }

   private boolean tickHiddenEffect() {
      if (this.duration == 0 && this.hiddenEffect != null) {
         this.copyFrom(this.hiddenEffect);
         this.hiddenEffect = this.hiddenEffect.hiddenEffect;
         return true;
      } else {
         return false;
      }
   }

   public void onApplied(LivingEntity entity) {
      ((StatusEffect)this.type.value()).onApplied(entity, this.amplifier);
   }

   public void onEntityRemoval(ServerWorld world, LivingEntity entity, Entity.RemovalReason reason) {
      ((StatusEffect)this.type.value()).onEntityRemoval(world, entity, this.amplifier, reason);
   }

   public void onEntityDamage(ServerWorld world, LivingEntity entity, DamageSource source, float amount) {
      ((StatusEffect)this.type.value()).onEntityDamage(world, entity, this.amplifier, source, amount);
   }

   public String getTranslationKey() {
      return ((StatusEffect)this.type.value()).getTranslationKey();
   }

   public String toString() {
      String var10000;
      String string;
      if (this.amplifier > 0) {
         var10000 = this.getTranslationKey();
         string = var10000 + " x " + (this.amplifier + 1) + ", Duration: " + this.getDurationString();
      } else {
         var10000 = this.getTranslationKey();
         string = var10000 + ", Duration: " + this.getDurationString();
      }

      if (!this.showParticles) {
         string = string + ", Particles: false";
      }

      if (!this.showIcon) {
         string = string + ", Show Icon: false";
      }

      return string;
   }

   private String getDurationString() {
      return this.isInfinite() ? "infinite" : Integer.toString(this.duration);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof StatusEffectInstance)) {
         return false;
      } else {
         StatusEffectInstance statusEffectInstance = (StatusEffectInstance)o;
         return this.duration == statusEffectInstance.duration && this.amplifier == statusEffectInstance.amplifier && this.ambient == statusEffectInstance.ambient && this.showParticles == statusEffectInstance.showParticles && this.showIcon == statusEffectInstance.showIcon && this.type.equals(statusEffectInstance.type);
      }
   }

   public int hashCode() {
      int i = this.type.hashCode();
      i = 31 * i + this.duration;
      i = 31 * i + this.amplifier;
      i = 31 * i + (this.ambient ? 1 : 0);
      i = 31 * i + (this.showParticles ? 1 : 0);
      i = 31 * i + (this.showIcon ? 1 : 0);
      return i;
   }

   public int compareTo(StatusEffectInstance statusEffectInstance) {
      int i = true;
      return (this.getDuration() <= 32147 || statusEffectInstance.getDuration() <= 32147) && (!this.isAmbient() || !statusEffectInstance.isAmbient()) ? ComparisonChain.start().compareFalseFirst(this.isAmbient(), statusEffectInstance.isAmbient()).compareFalseFirst(this.isInfinite(), statusEffectInstance.isInfinite()).compare(this.getDuration(), statusEffectInstance.getDuration()).compare(((StatusEffect)this.getEffectType().value()).getColor(), ((StatusEffect)statusEffectInstance.getEffectType().value()).getColor()).result() : ComparisonChain.start().compare(this.isAmbient(), statusEffectInstance.isAmbient()).compare(((StatusEffect)this.getEffectType().value()).getColor(), ((StatusEffect)statusEffectInstance.getEffectType().value()).getColor()).result();
   }

   public void playApplySound(LivingEntity entity) {
      ((StatusEffect)this.type.value()).playApplySound(entity, this.amplifier);
   }

   public boolean equals(RegistryEntry effect) {
      return this.type.equals(effect);
   }

   public void copyFadingFrom(StatusEffectInstance effect) {
      this.fading.copyFrom(effect.fading);
   }

   public void skipFading() {
      this.fading.skipFading(this);
   }

   // $FF: synthetic method
   public int compareTo(final Object that) {
      return this.compareTo((StatusEffectInstance)that);
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(StatusEffect.ENTRY_PACKET_CODEC, StatusEffectInstance::getEffectType, StatusEffectInstance.Parameters.PACKET_CODEC, StatusEffectInstance::asParameters, StatusEffectInstance::new);
   }

   static class Fading {
      private float factor;
      private float lastFactor;

      public void skipFading(StatusEffectInstance effect) {
         this.factor = shouldFadeIn(effect) ? 1.0F : 0.0F;
         this.lastFactor = this.factor;
      }

      public void copyFrom(Fading fading) {
         this.factor = fading.factor;
         this.lastFactor = fading.lastFactor;
      }

      public void update(StatusEffectInstance effect) {
         this.lastFactor = this.factor;
         boolean bl = shouldFadeIn(effect);
         float f = bl ? 1.0F : 0.0F;
         if (this.factor != f) {
            StatusEffect statusEffect = (StatusEffect)effect.getEffectType().value();
            int i = bl ? statusEffect.getFadeInTicks() : statusEffect.getFadeOutTicks();
            if (i == 0) {
               this.factor = f;
            } else {
               float g = 1.0F / (float)i;
               this.factor += MathHelper.clamp(f - this.factor, -g, g);
            }

         }
      }

      private static boolean shouldFadeIn(StatusEffectInstance effect) {
         return !effect.isDurationBelow(((StatusEffect)effect.getEffectType().value()).getFadeOutThresholdTicks());
      }

      public float calculate(LivingEntity entity, float tickProgress) {
         if (entity.isRemoved()) {
            this.lastFactor = this.factor;
         }

         return MathHelper.lerp(tickProgress, this.lastFactor, this.factor);
      }
   }

   private static record Parameters(int amplifier, int duration, boolean ambient, boolean showParticles, boolean showIcon, Optional hiddenEffect) {
      public static final MapCodec CODEC = MapCodec.recursive("MobEffectInstance.Details", (codec) -> {
         return RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(Codecs.UNSIGNED_BYTE.optionalFieldOf("amplifier", 0).forGetter(Parameters::amplifier), Codec.INT.optionalFieldOf("duration", 0).forGetter(Parameters::duration), Codec.BOOL.optionalFieldOf("ambient", false).forGetter(Parameters::ambient), Codec.BOOL.optionalFieldOf("show_particles", true).forGetter(Parameters::showParticles), Codec.BOOL.optionalFieldOf("show_icon").forGetter((parameters) -> {
               return Optional.of(parameters.showIcon());
            }), codec.optionalFieldOf("hidden_effect").forGetter(Parameters::hiddenEffect)).apply(instance, Parameters::create);
         });
      });
      public static final PacketCodec PACKET_CODEC = PacketCodec.recursive((packetCodec) -> {
         return PacketCodec.tuple(PacketCodecs.VAR_INT, Parameters::amplifier, PacketCodecs.VAR_INT, Parameters::duration, PacketCodecs.BOOLEAN, Parameters::ambient, PacketCodecs.BOOLEAN, Parameters::showParticles, PacketCodecs.BOOLEAN, Parameters::showIcon, packetCodec.collect(PacketCodecs::optional), Parameters::hiddenEffect, Parameters::new);
      });

      Parameters(int i, int j, boolean bl, boolean bl2, boolean bl3, Optional optional) {
         this.amplifier = i;
         this.duration = j;
         this.ambient = bl;
         this.showParticles = bl2;
         this.showIcon = bl3;
         this.hiddenEffect = optional;
      }

      private static Parameters create(int amplifier, int duration, boolean ambient, boolean showParticles, Optional showIcon, Optional hiddenEffect) {
         return new Parameters(amplifier, duration, ambient, showParticles, (Boolean)showIcon.orElse(showParticles), hiddenEffect);
      }

      public int amplifier() {
         return this.amplifier;
      }

      public int duration() {
         return this.duration;
      }

      public boolean ambient() {
         return this.ambient;
      }

      public boolean showParticles() {
         return this.showParticles;
      }

      public boolean showIcon() {
         return this.showIcon;
      }

      public Optional hiddenEffect() {
         return this.hiddenEffect;
      }
   }
}
