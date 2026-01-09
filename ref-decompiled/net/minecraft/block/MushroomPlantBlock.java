package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.Optional;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class MushroomPlantBlock extends PlantBlock implements Fertilizable {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(RegistryKey.createCodec(RegistryKeys.CONFIGURED_FEATURE).fieldOf("feature").forGetter((block) -> {
         return block.featureKey;
      }), createSettingsCodec()).apply(instance, MushroomPlantBlock::new);
   });
   private static final VoxelShape SHAPE = Block.createColumnShape(6.0, 0.0, 6.0);
   private final RegistryKey featureKey;

   public MapCodec getCodec() {
      return CODEC;
   }

   public MushroomPlantBlock(RegistryKey featureKey, AbstractBlock.Settings settings) {
      super(settings);
      this.featureKey = featureKey;
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return SHAPE;
   }

   protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if (random.nextInt(25) == 0) {
         int i = 5;
         int j = true;
         Iterator var7 = BlockPos.iterate(pos.add(-4, -1, -4), pos.add(4, 1, 4)).iterator();

         while(var7.hasNext()) {
            BlockPos blockPos = (BlockPos)var7.next();
            if (world.getBlockState(blockPos).isOf(this)) {
               --i;
               if (i <= 0) {
                  return;
               }
            }
         }

         BlockPos blockPos2 = pos.add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);

         for(int k = 0; k < 4; ++k) {
            if (world.isAir(blockPos2) && state.canPlaceAt(world, blockPos2)) {
               pos = blockPos2;
            }

            blockPos2 = pos.add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
         }

         if (world.isAir(blockPos2) && state.canPlaceAt(world, blockPos2)) {
            world.setBlockState(blockPos2, state, 2);
         }
      }

   }

   protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
      return floor.isOpaqueFullCube();
   }

   protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
      BlockPos blockPos = pos.down();
      BlockState blockState = world.getBlockState(blockPos);
      if (blockState.isIn(BlockTags.MUSHROOM_GROW_BLOCK)) {
         return true;
      } else {
         return world.getBaseLightLevel(pos, 0) < 13 && this.canPlantOnTop(blockState, world, blockPos);
      }
   }

   public boolean trySpawningBigMushroom(ServerWorld world, BlockPos pos, BlockState state, Random random) {
      Optional optional = world.getRegistryManager().getOrThrow(RegistryKeys.CONFIGURED_FEATURE).getOptional(this.featureKey);
      if (optional.isEmpty()) {
         return false;
      } else {
         world.removeBlock(pos, false);
         if (((ConfiguredFeature)((RegistryEntry)optional.get()).value()).generate(world, world.getChunkManager().getChunkGenerator(), random, pos)) {
            return true;
         } else {
            world.setBlockState(pos, state, 3);
            return false;
         }
      }
   }

   public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
      return true;
   }

   public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
      return (double)random.nextFloat() < 0.4;
   }

   public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
      this.trySpawningBigMushroom(world, pos, state, random);
   }
}
