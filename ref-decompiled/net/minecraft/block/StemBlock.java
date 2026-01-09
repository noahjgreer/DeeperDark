package net.minecraft.block;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class StemBlock extends PlantBlock implements Fertilizable {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(RegistryKey.createCodec(RegistryKeys.BLOCK).fieldOf("fruit").forGetter((block) -> {
         return block.gourdBlock;
      }), RegistryKey.createCodec(RegistryKeys.BLOCK).fieldOf("attached_stem").forGetter((block) -> {
         return block.attachedStemBlock;
      }), RegistryKey.createCodec(RegistryKeys.ITEM).fieldOf("seed").forGetter((block) -> {
         return block.pickBlockItem;
      }), createSettingsCodec()).apply(instance, StemBlock::new);
   });
   public static final int MAX_AGE = 7;
   public static final IntProperty AGE;
   private static final VoxelShape[] SHAPES_BY_AGE;
   private final RegistryKey gourdBlock;
   private final RegistryKey attachedStemBlock;
   private final RegistryKey pickBlockItem;

   public MapCodec getCodec() {
      return CODEC;
   }

   public StemBlock(RegistryKey gourdBlock, RegistryKey attachedStemBlock, RegistryKey pickBlockItem, AbstractBlock.Settings settings) {
      super(settings);
      this.gourdBlock = gourdBlock;
      this.attachedStemBlock = attachedStemBlock;
      this.pickBlockItem = pickBlockItem;
      this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(AGE, 0));
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return SHAPES_BY_AGE[(Integer)state.get(AGE)];
   }

   protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
      return floor.isOf(Blocks.FARMLAND);
   }

   protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if (world.getBaseLightLevel(pos, 0) >= 9) {
         float f = CropBlock.getAvailableMoisture(this, world, pos);
         if (random.nextInt((int)(25.0F / f) + 1) == 0) {
            int i = (Integer)state.get(AGE);
            if (i < 7) {
               state = (BlockState)state.with(AGE, i + 1);
               world.setBlockState(pos, state, 2);
            } else {
               Direction direction = Direction.Type.HORIZONTAL.random(random);
               BlockPos blockPos = pos.offset(direction);
               BlockState blockState = world.getBlockState(blockPos.down());
               if (world.getBlockState(blockPos).isAir() && (blockState.isOf(Blocks.FARMLAND) || blockState.isIn(BlockTags.DIRT))) {
                  Registry registry = world.getRegistryManager().getOrThrow(RegistryKeys.BLOCK);
                  Optional optional = registry.getOptionalValue(this.gourdBlock);
                  Optional optional2 = registry.getOptionalValue(this.attachedStemBlock);
                  if (optional.isPresent() && optional2.isPresent()) {
                     world.setBlockState(blockPos, ((Block)optional.get()).getDefaultState());
                     world.setBlockState(pos, (BlockState)((Block)optional2.get()).getDefaultState().with(HorizontalFacingBlock.FACING, direction));
                  }
               }
            }
         }

      }
   }

   protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
      return new ItemStack((ItemConvertible)DataFixUtils.orElse(world.getRegistryManager().getOrThrow(RegistryKeys.ITEM).getOptionalValue(this.pickBlockItem), this));
   }

   public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
      return (Integer)state.get(AGE) != 7;
   }

   public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
      return true;
   }

   public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
      int i = Math.min(7, (Integer)state.get(AGE) + MathHelper.nextInt(world.random, 2, 5));
      BlockState blockState = (BlockState)state.with(AGE, i);
      world.setBlockState(pos, blockState, 2);
      if (i == 7) {
         blockState.randomTick(world, pos, world.random);
      }

   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(AGE);
   }

   static {
      AGE = Properties.AGE_7;
      SHAPES_BY_AGE = Block.createShapeArray(7, (age) -> {
         return Block.createColumnShape(2.0, 0.0, (double)(2 + age * 2));
      });
   }
}
