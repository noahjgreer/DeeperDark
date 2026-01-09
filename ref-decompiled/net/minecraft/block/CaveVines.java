package net.minecraft.block;

import java.util.function.ToIntFunction;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public interface CaveVines {
   VoxelShape SHAPE = Block.createColumnShape(14.0, 0.0, 16.0);
   BooleanProperty BERRIES = Properties.BERRIES;

   static ActionResult pickBerries(@Nullable Entity picker, BlockState state, World world, BlockPos pos) {
      if ((Boolean)state.get(BERRIES)) {
         Block.dropStack(world, pos, new ItemStack(Items.GLOW_BERRIES, 1));
         float f = MathHelper.nextBetween(world.random, 0.8F, 1.2F);
         world.playSound((Entity)null, pos, SoundEvents.BLOCK_CAVE_VINES_PICK_BERRIES, SoundCategory.BLOCKS, 1.0F, f);
         BlockState blockState = (BlockState)state.with(BERRIES, false);
         world.setBlockState(pos, blockState, 2);
         world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(picker, blockState));
         return ActionResult.SUCCESS;
      } else {
         return ActionResult.PASS;
      }
   }

   static boolean hasBerries(BlockState state) {
      return state.contains(BERRIES) && (Boolean)state.get(BERRIES);
   }

   static ToIntFunction getLuminanceSupplier(int luminance) {
      return (state) -> {
         return (Boolean)state.get(Properties.BERRIES) ? luminance : 0;
      };
   }
}
