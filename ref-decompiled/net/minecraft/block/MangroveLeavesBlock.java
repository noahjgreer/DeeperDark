package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class MangroveLeavesBlock extends TintedParticleLeavesBlock implements Fertilizable {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codecs.rangedInclusiveFloat(0.0F, 1.0F).fieldOf("leaf_particle_chance").forGetter((block) -> {
         return block.leafParticleChance;
      }), createSettingsCodec()).apply(instance, MangroveLeavesBlock::new);
   });

   public MapCodec getCodec() {
      return CODEC;
   }

   public MangroveLeavesBlock(float f, AbstractBlock.Settings settings) {
      super(f, settings);
   }

   public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
      return world.getBlockState(pos.down()).isAir();
   }

   public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
      return true;
   }

   public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
      world.setBlockState(pos.down(), PropaguleBlock.getDefaultHangingState(), 2);
   }

   public BlockPos getFertilizeParticlePos(BlockPos pos) {
      return pos.down();
   }
}
