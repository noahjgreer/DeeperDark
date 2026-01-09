package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class ChorusPlantBlock extends ConnectingBlock {
   public static final MapCodec CODEC = createCodec(ChorusPlantBlock::new);

   public MapCodec getCodec() {
      return CODEC;
   }

   public ChorusPlantBlock(AbstractBlock.Settings settings) {
      super(10.0F, settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(NORTH, false)).with(EAST, false)).with(SOUTH, false)).with(WEST, false)).with(UP, false)).with(DOWN, false));
   }

   public BlockState getPlacementState(ItemPlacementContext ctx) {
      return withConnectionProperties(ctx.getWorld(), ctx.getBlockPos(), this.getDefaultState());
   }

   public static BlockState withConnectionProperties(BlockView world, BlockPos pos, BlockState state) {
      BlockState blockState = world.getBlockState(pos.down());
      BlockState blockState2 = world.getBlockState(pos.up());
      BlockState blockState3 = world.getBlockState(pos.north());
      BlockState blockState4 = world.getBlockState(pos.east());
      BlockState blockState5 = world.getBlockState(pos.south());
      BlockState blockState6 = world.getBlockState(pos.west());
      Block block = state.getBlock();
      return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)state.withIfExists(DOWN, blockState.isOf(block) || blockState.isOf(Blocks.CHORUS_FLOWER) || blockState.isOf(Blocks.END_STONE))).withIfExists(UP, blockState2.isOf(block) || blockState2.isOf(Blocks.CHORUS_FLOWER))).withIfExists(NORTH, blockState3.isOf(block) || blockState3.isOf(Blocks.CHORUS_FLOWER))).withIfExists(EAST, blockState4.isOf(block) || blockState4.isOf(Blocks.CHORUS_FLOWER))).withIfExists(SOUTH, blockState5.isOf(block) || blockState5.isOf(Blocks.CHORUS_FLOWER))).withIfExists(WEST, blockState6.isOf(block) || blockState6.isOf(Blocks.CHORUS_FLOWER));
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      if (!state.canPlaceAt(world, pos)) {
         tickView.scheduleBlockTick(pos, this, 1);
         return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
      } else {
         boolean bl = neighborState.isOf(this) || neighborState.isOf(Blocks.CHORUS_FLOWER) || direction == Direction.DOWN && neighborState.isOf(Blocks.END_STONE);
         return (BlockState)state.with((Property)FACING_PROPERTIES.get(direction), bl);
      }
   }

   protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if (!state.canPlaceAt(world, pos)) {
         world.breakBlock(pos, true);
      }

   }

   protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
      BlockState blockState = world.getBlockState(pos.down());
      boolean bl = !world.getBlockState(pos.up()).isAir() && !blockState.isAir();
      Iterator var6 = Direction.Type.HORIZONTAL.iterator();

      BlockState blockState3;
      do {
         BlockPos blockPos;
         BlockState blockState2;
         do {
            if (!var6.hasNext()) {
               return blockState.isOf(this) || blockState.isOf(Blocks.END_STONE);
            }

            Direction direction = (Direction)var6.next();
            blockPos = pos.offset(direction);
            blockState2 = world.getBlockState(blockPos);
         } while(!blockState2.isOf(this));

         if (bl) {
            return false;
         }

         blockState3 = world.getBlockState(blockPos.down());
      } while(!blockState3.isOf(this) && !blockState3.isOf(Blocks.END_STONE));

      return true;
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
   }

   protected boolean canPathfindThrough(BlockState state, NavigationType type) {
      return false;
   }
}
