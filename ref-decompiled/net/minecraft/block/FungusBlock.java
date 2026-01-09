package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class FungusBlock extends PlantBlock implements Fertilizable {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(RegistryKey.createCodec(RegistryKeys.CONFIGURED_FEATURE).fieldOf("feature").forGetter((block) -> {
         return block.featureKey;
      }), Registries.BLOCK.getCodec().fieldOf("grows_on").forGetter((block) -> {
         return block.nylium;
      }), createSettingsCodec()).apply(instance, FungusBlock::new);
   });
   private static final double GROW_CHANCE = 0.4;
   private static final VoxelShape SHAPE = Block.createColumnShape(8.0, 0.0, 9.0);
   private final Block nylium;
   private final RegistryKey featureKey;

   public MapCodec getCodec() {
      return CODEC;
   }

   public FungusBlock(RegistryKey featureKey, Block nylium, AbstractBlock.Settings settings) {
      super(settings);
      this.featureKey = featureKey;
      this.nylium = nylium;
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return SHAPE;
   }

   protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
      return floor.isIn(BlockTags.NYLIUM) || floor.isOf(Blocks.MYCELIUM) || floor.isOf(Blocks.SOUL_SOIL) || super.canPlantOnTop(floor, world, pos);
   }

   private Optional getFeatureEntry(WorldView world) {
      return world.getRegistryManager().getOrThrow(RegistryKeys.CONFIGURED_FEATURE).getOptional(this.featureKey);
   }

   public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
      BlockState blockState = world.getBlockState(pos.down());
      return blockState.isOf(this.nylium);
   }

   public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
      return (double)random.nextFloat() < 0.4;
   }

   public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
      this.getFeatureEntry(world).ifPresent((featureEntry) -> {
         ((ConfiguredFeature)featureEntry.value()).generate(world, world.getChunkManager().getChunkGenerator(), random, pos);
      });
   }
}
