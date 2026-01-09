package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.IntProvider;

public record SculkPatchFeatureConfig(int chargeCount, int amountPerCharge, int spreadAttempts, int growthRounds, int spreadRounds, IntProvider extraRareGrowths, float catalystChance) implements FeatureConfig {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Codec.intRange(1, 32).fieldOf("charge_count").forGetter(SculkPatchFeatureConfig::chargeCount), Codec.intRange(1, 500).fieldOf("amount_per_charge").forGetter(SculkPatchFeatureConfig::amountPerCharge), Codec.intRange(1, 64).fieldOf("spread_attempts").forGetter(SculkPatchFeatureConfig::spreadAttempts), Codec.intRange(0, 8).fieldOf("growth_rounds").forGetter(SculkPatchFeatureConfig::growthRounds), Codec.intRange(0, 8).fieldOf("spread_rounds").forGetter(SculkPatchFeatureConfig::spreadRounds), IntProvider.VALUE_CODEC.fieldOf("extra_rare_growths").forGetter(SculkPatchFeatureConfig::extraRareGrowths), Codec.floatRange(0.0F, 1.0F).fieldOf("catalyst_chance").forGetter(SculkPatchFeatureConfig::catalystChance)).apply(instance, SculkPatchFeatureConfig::new);
   });

   public SculkPatchFeatureConfig(int i, int j, int k, int l, int m, IntProvider intProvider, float f) {
      this.chargeCount = i;
      this.amountPerCharge = j;
      this.spreadAttempts = k;
      this.growthRounds = l;
      this.spreadRounds = m;
      this.extraRareGrowths = intProvider;
      this.catalystChance = f;
   }

   public int chargeCount() {
      return this.chargeCount;
   }

   public int amountPerCharge() {
      return this.amountPerCharge;
   }

   public int spreadAttempts() {
      return this.spreadAttempts;
   }

   public int growthRounds() {
      return this.growthRounds;
   }

   public int spreadRounds() {
      return this.spreadRounds;
   }

   public IntProvider extraRareGrowths() {
      return this.extraRareGrowths;
   }

   public float catalystChance() {
      return this.catalystChance;
   }
}
