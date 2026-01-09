package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;

public class TrappedChestBlock extends ChestBlock {
   public static final MapCodec CODEC = createCodec(TrappedChestBlock::new);

   public MapCodec getCodec() {
      return CODEC;
   }

   public TrappedChestBlock(AbstractBlock.Settings settings) {
      super(() -> {
         return BlockEntityType.TRAPPED_CHEST;
      }, settings);
   }

   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return new TrappedChestBlockEntity(pos, state);
   }

   protected Stat getOpenStat() {
      return Stats.CUSTOM.getOrCreateStat(Stats.TRIGGER_TRAPPED_CHEST);
   }

   protected boolean emitsRedstonePower(BlockState state) {
      return true;
   }

   protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
      return MathHelper.clamp(ChestBlockEntity.getPlayersLookingInChestCount(world, pos), 0, 15);
   }

   protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
      return direction == Direction.UP ? state.getWeakRedstonePower(world, pos, direction) : 0;
   }
}
