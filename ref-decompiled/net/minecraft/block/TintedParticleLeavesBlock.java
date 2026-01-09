package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ParticleUtil;
import net.minecraft.particle.TintedParticleEffect;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class TintedParticleLeavesBlock extends LeavesBlock {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codecs.rangedInclusiveFloat(0.0F, 1.0F).fieldOf("leaf_particle_chance").forGetter((tintedParticleLeavesBlock) -> {
         return tintedParticleLeavesBlock.leafParticleChance;
      }), createSettingsCodec()).apply(instance, TintedParticleLeavesBlock::new);
   });

   public TintedParticleLeavesBlock(float f, AbstractBlock.Settings settings) {
      super(f, settings);
   }

   protected void spawnLeafParticle(World world, BlockPos pos, Random random) {
      TintedParticleEffect tintedParticleEffect = TintedParticleEffect.create(ParticleTypes.TINTED_LEAVES, world.getBlockColor(pos));
      ParticleUtil.spawnParticle(world, pos, (Random)random, (ParticleEffect)tintedParticleEffect);
   }

   public MapCodec getCodec() {
      return CODEC;
   }
}
