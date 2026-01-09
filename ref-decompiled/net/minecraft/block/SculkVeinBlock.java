package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.block.entity.SculkSpreadManager;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class SculkVeinBlock extends MultifaceGrowthBlock implements SculkSpreadable {
   public static final MapCodec CODEC = createCodec(SculkVeinBlock::new);
   private final MultifaceGrower allGrowTypeGrower;
   private final MultifaceGrower samePositionOnlyGrower;

   public MapCodec getCodec() {
      return CODEC;
   }

   public SculkVeinBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.allGrowTypeGrower = new MultifaceGrower(new SculkVeinGrowChecker(this, MultifaceGrower.GROW_TYPES));
      this.samePositionOnlyGrower = new MultifaceGrower(new SculkVeinGrowChecker(this, new MultifaceGrower.GrowType[]{MultifaceGrower.GrowType.SAME_POSITION}));
   }

   public MultifaceGrower getGrower() {
      return this.allGrowTypeGrower;
   }

   public MultifaceGrower getSamePositionOnlyGrower() {
      return this.samePositionOnlyGrower;
   }

   public static boolean place(WorldAccess world, BlockPos pos, BlockState state, Collection directions) {
      boolean bl = false;
      BlockState blockState = Blocks.SCULK_VEIN.getDefaultState();
      Iterator var6 = directions.iterator();

      while(var6.hasNext()) {
         Direction direction = (Direction)var6.next();
         if (canGrowOn(world, pos, direction)) {
            blockState = (BlockState)blockState.with(getProperty(direction), true);
            bl = true;
         }
      }

      if (!bl) {
         return false;
      } else {
         if (!state.getFluidState().isEmpty()) {
            blockState = (BlockState)blockState.with(MultifaceBlock.WATERLOGGED, true);
         }

         world.setBlockState(pos, blockState, 3);
         return true;
      }
   }

   public void spreadAtSamePosition(WorldAccess world, BlockState state, BlockPos pos, Random random) {
      if (state.isOf(this)) {
         Direction[] var5 = DIRECTIONS;
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Direction direction = var5[var7];
            BooleanProperty booleanProperty = getProperty(direction);
            if ((Boolean)state.get(booleanProperty) && world.getBlockState(pos.offset(direction)).isOf(Blocks.SCULK)) {
               state = (BlockState)state.with(booleanProperty, false);
            }
         }

         if (!hasAnyDirection(state)) {
            FluidState fluidState = world.getFluidState(pos);
            state = (fluidState.isEmpty() ? Blocks.AIR : Blocks.WATER).getDefaultState();
         }

         world.setBlockState(pos, state, 3);
         SculkSpreadable.super.spreadAtSamePosition(world, state, pos, random);
      }
   }

   public int spread(SculkSpreadManager.Cursor cursor, WorldAccess world, BlockPos catalystPos, Random random, SculkSpreadManager spreadManager, boolean shouldConvertToBlock) {
      if (shouldConvertToBlock && this.convertToBlock(spreadManager, world, cursor.getPos(), random)) {
         return cursor.getCharge() - 1;
      } else {
         return random.nextInt(spreadManager.getSpreadChance()) == 0 ? MathHelper.floor((float)cursor.getCharge() * 0.5F) : cursor.getCharge();
      }
   }

   private boolean convertToBlock(SculkSpreadManager spreadManager, WorldAccess world, BlockPos pos, Random random) {
      BlockState blockState = world.getBlockState(pos);
      TagKey tagKey = spreadManager.getReplaceableTag();
      Iterator var7 = Direction.shuffle(random).iterator();

      while(var7.hasNext()) {
         Direction direction = (Direction)var7.next();
         if (hasDirection(blockState, direction)) {
            BlockPos blockPos = pos.offset(direction);
            BlockState blockState2 = world.getBlockState(blockPos);
            if (blockState2.isIn(tagKey)) {
               BlockState blockState3 = Blocks.SCULK.getDefaultState();
               world.setBlockState(blockPos, blockState3, 3);
               Block.pushEntitiesUpBeforeBlockChange(blockState2, blockState3, world, blockPos);
               world.playSound((Entity)null, blockPos, SoundEvents.BLOCK_SCULK_SPREAD, SoundCategory.BLOCKS, 1.0F, 1.0F);
               this.allGrowTypeGrower.grow(blockState3, world, blockPos, spreadManager.isWorldGen());
               Direction direction2 = direction.getOpposite();
               Direction[] var13 = DIRECTIONS;
               int var14 = var13.length;

               for(int var15 = 0; var15 < var14; ++var15) {
                  Direction direction3 = var13[var15];
                  if (direction3 != direction2) {
                     BlockPos blockPos2 = blockPos.offset(direction3);
                     BlockState blockState4 = world.getBlockState(blockPos2);
                     if (blockState4.isOf(this)) {
                        this.spreadAtSamePosition(world, blockState4, blockPos2, random);
                     }
                  }
               }

               return true;
            }
         }
      }

      return false;
   }

   public static boolean veinCoversSculkReplaceable(WorldAccess world, BlockState state, BlockPos pos) {
      if (!state.isOf(Blocks.SCULK_VEIN)) {
         return false;
      } else {
         Direction[] var3 = DIRECTIONS;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Direction direction = var3[var5];
            if (hasDirection(state, direction) && world.getBlockState(pos.offset(direction)).isIn(BlockTags.SCULK_REPLACEABLE)) {
               return true;
            }
         }

         return false;
      }
   }

   class SculkVeinGrowChecker extends MultifaceGrower.LichenGrowChecker {
      private final MultifaceGrower.GrowType[] growTypes;

      public SculkVeinGrowChecker(final SculkVeinBlock block, final MultifaceGrower.GrowType... growTypes) {
         super(block);
         this.growTypes = growTypes;
      }

      public boolean canGrow(BlockView world, BlockPos pos, BlockPos growPos, Direction direction, BlockState state) {
         BlockState blockState = world.getBlockState(growPos.offset(direction));
         if (!blockState.isOf(Blocks.SCULK) && !blockState.isOf(Blocks.SCULK_CATALYST) && !blockState.isOf(Blocks.MOVING_PISTON)) {
            if (pos.getManhattanDistance(growPos) == 2) {
               BlockPos blockPos = pos.offset(direction.getOpposite());
               if (world.getBlockState(blockPos).isSideSolidFullSquare(world, blockPos, direction)) {
                  return false;
               }
            }

            FluidState fluidState = state.getFluidState();
            if (!fluidState.isEmpty() && !fluidState.isOf(Fluids.WATER)) {
               return false;
            } else if (state.isIn(BlockTags.FIRE)) {
               return false;
            } else {
               return state.isReplaceable() || super.canGrow(world, pos, growPos, direction, state);
            }
         } else {
            return false;
         }
      }

      public MultifaceGrower.GrowType[] getGrowTypes() {
         return this.growTypes;
      }

      public boolean canGrow(BlockState state) {
         return !state.isOf(Blocks.SCULK_VEIN);
      }
   }
}
