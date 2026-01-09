package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class SnowyBlock extends Block {
   public static final MapCodec CODEC = createCodec(SnowyBlock::new);
   public static final BooleanProperty SNOWY;

   protected MapCodec getCodec() {
      return CODEC;
   }

   public SnowyBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(SNOWY, false));
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      return direction == Direction.UP ? (BlockState)state.with(SNOWY, isSnow(neighborState)) : super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
   }

   public BlockState getPlacementState(ItemPlacementContext ctx) {
      BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos().up());
      return (BlockState)this.getDefaultState().with(SNOWY, isSnow(blockState));
   }

   protected static boolean isSnow(BlockState state) {
      return state.isIn(BlockTags.SNOW);
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(SNOWY);
   }

   static {
      SNOWY = Properties.SNOWY;
   }
}
