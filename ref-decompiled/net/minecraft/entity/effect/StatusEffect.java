package net.minecraft.entity.effect;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.TintedParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.featuretoggle.ToggleableFeature;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class StatusEffect implements ToggleableFeature {
   public static final Codec ENTRY_CODEC;
   public static final PacketCodec ENTRY_PACKET_CODEC;
   private static final int AMBIENT_PARTICLE_ALPHA;
   private final Map attributeModifiers = new Object2ObjectOpenHashMap();
   private final StatusEffectCategory category;
   private final int color;
   private final Function particleFactory;
   @Nullable
   private String translationKey;
   private int fadeInTicks;
   private int fadeOutTicks;
   private int fadeOutThresholdTicks;
   private Optional applySound = Optional.empty();
   private FeatureSet requiredFeatures;

   protected StatusEffect(StatusEffectCategory category, int color) {
      this.requiredFeatures = FeatureFlags.VANILLA_FEATURES;
      this.category = category;
      this.color = color;
      this.particleFactory = (effect) -> {
         int j = effect.isAmbient() ? AMBIENT_PARTICLE_ALPHA : 255;
         return TintedParticleEffect.create(ParticleTypes.ENTITY_EFFECT, ColorHelper.withAlpha(j, color));
      };
   }

   protected StatusEffect(StatusEffectCategory category, int color, ParticleEffect particleEffect) {
      this.requiredFeatures = FeatureFlags.VANILLA_FEATURES;
      this.category = category;
      this.color = color;
      this.particleFactory = (effect) -> {
         return particleEffect;
      };
   }

   public int getFadeInTicks() {
      return this.fadeInTicks;
   }

   public int getFadeOutTicks() {
      return this.fadeOutTicks;
   }

   public int getFadeOutThresholdTicks() {
      return this.fadeOutThresholdTicks;
   }

   public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
      return true;
   }

   public void applyInstantEffect(ServerWorld world, @Nullable Entity effectEntity, @Nullable Entity attacker, LivingEntity target, int amplifier, double proximity) {
      this.applyUpdateEffect(world, target, amplifier);
   }

   public boolean canApplyUpdateEffect(int duration, int amplifier) {
      return false;
   }

   public void onApplied(LivingEntity entity, int amplifier) {
   }

   public void playApplySound(LivingEntity entity, int amplifier) {
      this.applySound.ifPresent((sound) -> {
         entity.getWorld().playSound((Entity)null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)sound, entity.getSoundCategory(), 1.0F, 1.0F);
      });
   }

   public void onEntityRemoval(ServerWorld world, LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
   }

   public void onEntityDamage(ServerWorld world, LivingEntity entity, int amplifier, DamageSource source, float amount) {
   }

   public boolean isInstant() {
      return false;
   }

   protected String loadTranslationKey() {
      if (this.translationKey == null) {
         this.translationKey = Util.createTranslationKey("effect", Registries.STATUS_EFFECT.getId(this));
      }

      return this.translationKey;
   }

   public String getTranslationKey() {
      return this.loadTranslationKey();
   }

   public Text getName() {
      return Text.translatable(this.getTranslationKey());
   }

   public StatusEffectCategory getCategory() {
      return this.category;
   }

   public int getColor() {
      return this.color;
   }

   public StatusEffect addAttributeModifier(RegistryEntry attribute, Identifier id, double amount, EntityAttributeModifier.Operation operation) {
      this.attributeModifiers.put(attribute, new EffectAttributeModifierCreator(id, amount, operation));
      return this;
   }

   public StatusEffect fadeTicks(int fadeTicks) {
      return this.fadeTicks(fadeTicks, fadeTicks, fadeTicks);
   }

   public StatusEffect fadeTicks(int fadeInTicks, int fadeOutTicks, int fadeOutThresholdTicks) {
      this.fadeInTicks = fadeInTicks;
      this.fadeOutTicks = fadeOutTicks;
      this.fadeOutThresholdTicks = fadeOutThresholdTicks;
      return this;
   }

   public void forEachAttributeModifier(int amplifier, BiConsumer consumer) {
      this.attributeModifiers.forEach((attribute, attributeModifierCreator) -> {
         consumer.accept(attribute, attributeModifierCreator.createAttributeModifier(amplifier));
      });
   }

   public void onRemoved(AttributeContainer attributeContainer) {
      Iterator var2 = this.attributeModifiers.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry entry = (Map.Entry)var2.next();
         EntityAttributeInstance entityAttributeInstance = attributeContainer.getCustomInstance((RegistryEntry)entry.getKey());
         if (entityAttributeInstance != null) {
            entityAttributeInstance.removeModifier(((EffectAttributeModifierCreator)entry.getValue()).id());
         }
      }

   }

   public void onApplied(AttributeContainer attributeContainer, int amplifier) {
      Iterator var3 = this.attributeModifiers.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry entry = (Map.Entry)var3.next();
         EntityAttributeInstance entityAttributeInstance = attributeContainer.getCustomInstance((RegistryEntry)entry.getKey());
         if (entityAttributeInstance != null) {
            entityAttributeInstance.removeModifier(((EffectAttributeModifierCreator)entry.getValue()).id());
            entityAttributeInstance.addPersistentModifier(((EffectAttributeModifierCreator)entry.getValue()).createAttributeModifier(amplifier));
         }
      }

   }

   public boolean isBeneficial() {
      return this.category == StatusEffectCategory.BENEFICIAL;
   }

   public ParticleEffect createParticle(StatusEffectInstance effect) {
      return (ParticleEffect)this.particleFactory.apply(effect);
   }

   public StatusEffect applySound(SoundEvent sound) {
      this.applySound = Optional.of(sound);
      return this;
   }

   public StatusEffect requires(FeatureFlag... requiredFeatures) {
      this.requiredFeatures = FeatureFlags.FEATURE_MANAGER.featureSetOf(requiredFeatures);
      return this;
   }

   public FeatureSet getRequiredFeatures() {
      return this.requiredFeatures;
   }

   static {
      ENTRY_CODEC = Registries.STATUS_EFFECT.getEntryCodec();
      ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(RegistryKeys.STATUS_EFFECT);
      AMBIENT_PARTICLE_ALPHA = MathHelper.floor(38.25F);
   }

   private static record EffectAttributeModifierCreator(Identifier id, double baseValue, EntityAttributeModifier.Operation operation) {
      EffectAttributeModifierCreator(Identifier identifier, double d, EntityAttributeModifier.Operation operation) {
         this.id = identifier;
         this.baseValue = d;
         this.operation = operation;
      }

      public EntityAttributeModifier createAttributeModifier(int amplifier) {
         return new EntityAttributeModifier(this.id, this.baseValue * (double)(amplifier + 1), this.operation);
      }

      public Identifier id() {
         return this.id;
      }

      public double baseValue() {
         return this.baseValue;
      }

      public EntityAttributeModifier.Operation operation() {
         return this.operation;
      }
   }
}
