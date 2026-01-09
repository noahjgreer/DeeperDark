package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class EndSpikeFeatureConfig implements FeatureConfig {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Codec.BOOL.fieldOf("crystal_invulnerable").orElse(false).forGetter((config) -> {
         return config.crystalInvulnerable;
      }), EndSpikeFeature.Spike.CODEC.listOf().fieldOf("spikes").forGetter((config) -> {
         return config.spikes;
      }), BlockPos.CODEC.optionalFieldOf("crystal_beam_target").forGetter((config) -> {
         return Optional.ofNullable(config.crystalBeamTarget);
      })).apply(instance, EndSpikeFeatureConfig::new);
   });
   private final boolean crystalInvulnerable;
   private final List spikes;
   @Nullable
   private final BlockPos crystalBeamTarget;

   public EndSpikeFeatureConfig(boolean crystalInvulnerable, List spikes, @Nullable BlockPos crystalBeamTarget) {
      this(crystalInvulnerable, spikes, Optional.ofNullable(crystalBeamTarget));
   }

   private EndSpikeFeatureConfig(boolean crystalInvulnerable, List spikes, Optional crystalBeamTarget) {
      this.crystalInvulnerable = crystalInvulnerable;
      this.spikes = spikes;
      this.crystalBeamTarget = (BlockPos)crystalBeamTarget.orElse((Object)null);
   }

   public boolean isCrystalInvulnerable() {
      return this.crystalInvulnerable;
   }

   public List getSpikes() {
      return this.spikes;
   }

   @Nullable
   public BlockPos getPos() {
      return this.crystalBeamTarget;
   }
}
