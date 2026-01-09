package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

public class RedstoneLampBlock extends Block {
   public static final MapCodec CODEC = createCodec(RedstoneLampBlock::new);
   public static final BooleanProperty LIT;

   public MapCodec getCodec() {
      return CODEC;
   }

   public RedstoneLampBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)this.getDefaultState().with(LIT, false));
   }

   @Nullable
   public BlockState getPlacementState(ItemPlacementContext ctx) {
      return (BlockState)this.getDefaultState().with(LIT, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
   }

   protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
      if (!world.isClient) {
         boolean bl = (Boolean)state.get(LIT);
         if (bl != world.isReceivingRedstonePower(pos)) {
            if (bl) {
               world.scheduleBlockTick(pos, this, 4);
            } else {
               world.setBlockState(pos, (BlockState)state.cycle(LIT), 2);
            }
         }

      }
   }

   protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if ((Boolean)state.get(LIT) && !world.isReceivingRedstonePower(pos)) {
         world.setBlockState(pos, (BlockState)state.cycle(LIT), 2);
      }

   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(LIT);
   }

   static {
      LIT = RedstoneTorchBlock.LIT;
   }
}
