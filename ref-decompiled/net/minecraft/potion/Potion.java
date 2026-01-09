package net.minecraft.potion;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.featuretoggle.ToggleableFeature;

public class Potion implements ToggleableFeature {
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;
   private final String baseName;
   private final List effects;
   private FeatureSet requiredFeatures;

   public Potion(String baseName, StatusEffectInstance... effects) {
      this.requiredFeatures = FeatureFlags.VANILLA_FEATURES;
      this.baseName = baseName;
      this.effects = List.of(effects);
   }

   public Potion requires(FeatureFlag... requiredFeatures) {
      this.requiredFeatures = FeatureFlags.FEATURE_MANAGER.featureSetOf(requiredFeatures);
      return this;
   }

   public FeatureSet getRequiredFeatures() {
      return this.requiredFeatures;
   }

   public List getEffects() {
      return this.effects;
   }

   public String getBaseName() {
      return this.baseName;
   }

   public boolean hasInstantEffect() {
      Iterator var1 = this.effects.iterator();

      StatusEffectInstance statusEffectInstance;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         statusEffectInstance = (StatusEffectInstance)var1.next();
      } while(!((StatusEffect)statusEffectInstance.getEffectType().value()).isInstant());

      return true;
   }

   static {
      CODEC = Registries.POTION.getEntryCodec();
      PACKET_CODEC = PacketCodecs.registryEntry(RegistryKeys.POTION);
   }
}
