package net.minecraft.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

public interface BlockEntityProvider {
   @Nullable
   BlockEntity createBlockEntity(BlockPos pos, BlockState state);

   @Nullable
   default BlockEntityTicker getTicker(World world, BlockState state, BlockEntityType type) {
      return null;
   }

   @Nullable
   default GameEventListener getGameEventListener(ServerWorld world, BlockEntity blockEntity) {
      if (blockEntity instanceof GameEventListener.Holder holder) {
         return holder.getEventListener();
      } else {
         return null;
      }
   }
}
