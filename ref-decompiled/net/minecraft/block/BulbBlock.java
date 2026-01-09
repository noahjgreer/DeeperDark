package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

public class BulbBlock extends Block {
   public static final MapCodec CODEC = createCodec(BulbBlock::new);
   public static final BooleanProperty POWERED;
   public static final BooleanProperty LIT;

   protected MapCodec getCodec() {
      return CODEC;
   }

   public BulbBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)this.getDefaultState().with(LIT, false)).with(POWERED, false));
   }

   protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
      if (oldState.getBlock() != state.getBlock() && world instanceof ServerWorld serverWorld) {
         this.update(state, serverWorld, pos);
      }

   }

   protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
      if (world instanceof ServerWorld serverWorld) {
         this.update(state, serverWorld, pos);
      }

   }

   public void update(BlockState state, ServerWorld world, BlockPos pos) {
      boolean bl = world.isReceivingRedstonePower(pos);
      if (bl != (Boolean)state.get(POWERED)) {
         BlockState blockState = state;
         if (!(Boolean)state.get(POWERED)) {
            blockState = (BlockState)state.cycle(LIT);
            world.playSound((Entity)null, pos, (Boolean)blockState.get(LIT) ? SoundEvents.BLOCK_COPPER_BULB_TURN_ON : SoundEvents.BLOCK_COPPER_BULB_TURN_OFF, SoundCategory.BLOCKS);
         }

         world.setBlockState(pos, (BlockState)blockState.with(POWERED, bl), 3);
      }
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(LIT, POWERED);
   }

   protected boolean hasComparatorOutput(BlockState state) {
      return true;
   }

   protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
      return (Boolean)world.getBlockState(pos).get(LIT) ? 15 : 0;
   }

   static {
      POWERED = Properties.POWERED;
      LIT = Properties.LIT;
   }
}
