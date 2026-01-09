package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

public class SnowBlock extends Block {
   public static final MapCodec CODEC = createCodec(SnowBlock::new);
   public static final int MAX_LAYERS = 8;
   public static final IntProperty LAYERS;
   private static final VoxelShape[] SHAPES_BY_LAYERS;
   public static final int field_31248 = 5;

   public MapCodec getCodec() {
      return CODEC;
   }

   public SnowBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(LAYERS, 1));
   }

   protected boolean canPathfindThrough(BlockState state, NavigationType type) {
      if (type == NavigationType.LAND) {
         return (Integer)state.get(LAYERS) < 5;
      } else {
         return false;
      }
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return SHAPES_BY_LAYERS[(Integer)state.get(LAYERS)];
   }

   protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return SHAPES_BY_LAYERS[(Integer)state.get(LAYERS) - 1];
   }

   protected VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
      return SHAPES_BY_LAYERS[(Integer)state.get(LAYERS)];
   }

   protected VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return SHAPES_BY_LAYERS[(Integer)state.get(LAYERS)];
   }

   protected boolean hasSidedTransparency(BlockState state) {
      return true;
   }

   protected float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
      return (Integer)state.get(LAYERS) == 8 ? 0.2F : 1.0F;
   }

   protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
      BlockState blockState = world.getBlockState(pos.down());
      if (blockState.isIn(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON)) {
         return false;
      } else if (blockState.isIn(BlockTags.SNOW_LAYER_CAN_SURVIVE_ON)) {
         return true;
      } else {
         return Block.isFaceFullSquare(blockState.getCollisionShape(world, pos.down()), Direction.UP) || blockState.isOf(this) && (Integer)blockState.get(LAYERS) == 8;
      }
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      return !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
   }

   protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if (world.getLightLevel(LightType.BLOCK, pos) > 11) {
         dropStacks(state, world, pos);
         world.removeBlock(pos, false);
      }

   }

   protected boolean canReplace(BlockState state, ItemPlacementContext context) {
      int i = (Integer)state.get(LAYERS);
      if (context.getStack().isOf(this.asItem()) && i < 8) {
         if (context.canReplaceExisting()) {
            return context.getSide() == Direction.UP;
         } else {
            return true;
         }
      } else {
         return i == 1;
      }
   }

   @Nullable
   public BlockState getPlacementState(ItemPlacementContext ctx) {
      BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
      if (blockState.isOf(this)) {
         int i = (Integer)blockState.get(LAYERS);
         return (BlockState)blockState.with(LAYERS, Math.min(8, i + 1));
      } else {
         return super.getPlacementState(ctx);
      }
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(LAYERS);
   }

   static {
      LAYERS = Properties.LAYERS;
      SHAPES_BY_LAYERS = Block.createShapeArray(8, (layers) -> {
         return Block.createColumnShape(16.0, 0.0, (double)(layers * 2));
      });
   }
}
