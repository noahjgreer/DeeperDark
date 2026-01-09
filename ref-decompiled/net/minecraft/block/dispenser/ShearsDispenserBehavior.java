package net.minecraft.block.dispenser;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.event.GameEvent;

public class ShearsDispenserBehavior extends FallibleItemDispenserBehavior {
   protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
      ServerWorld serverWorld = pointer.world();
      if (!serverWorld.isClient()) {
         BlockPos blockPos = pointer.pos().offset((Direction)pointer.state().get(DispenserBlock.FACING));
         this.setSuccess(tryShearBlock(serverWorld, blockPos) || tryShearEntity(serverWorld, blockPos, stack));
         if (this.isSuccess()) {
            stack.damage(1, (ServerWorld)serverWorld, (ServerPlayerEntity)null, (Consumer)((item) -> {
            }));
         }
      }

      return stack;
   }

   private static boolean tryShearBlock(ServerWorld world, BlockPos pos) {
      BlockState blockState = world.getBlockState(pos);
      if (blockState.isIn(BlockTags.BEEHIVES, (state) -> {
         return state.contains(BeehiveBlock.HONEY_LEVEL) && state.getBlock() instanceof BeehiveBlock;
      })) {
         int i = (Integer)blockState.get(BeehiveBlock.HONEY_LEVEL);
         if (i >= 5) {
            world.playSound((Entity)null, pos, SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.BLOCKS, 1.0F, 1.0F);
            BeehiveBlock.dropHoneycomb(world, pos);
            ((BeehiveBlock)blockState.getBlock()).takeHoney(world, blockState, pos, (PlayerEntity)null, BeehiveBlockEntity.BeeState.BEE_RELEASED);
            world.emitGameEvent((Entity)null, GameEvent.SHEAR, pos);
            return true;
         }
      }

      return false;
   }

   private static boolean tryShearEntity(ServerWorld world, BlockPos pos, ItemStack shears) {
      List list = world.getEntitiesByClass(Entity.class, new Box(pos), EntityPredicates.EXCEPT_SPECTATOR);
      Iterator var4 = list.iterator();

      while(var4.hasNext()) {
         Entity entity = (Entity)var4.next();
         if (entity.snipAllHeldLeashes((PlayerEntity)null)) {
            return true;
         }

         if (entity instanceof Shearable shearable) {
            if (shearable.isShearable()) {
               shearable.sheared(world, SoundCategory.BLOCKS, shears);
               world.emitGameEvent((Entity)null, GameEvent.SHEAR, pos);
               return true;
            }
         }
      }

      return false;
   }
}
