package net.minecraft.entity.effect;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Set;
import java.util.function.ToIntFunction;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;

class WeavingStatusEffect extends StatusEffect {
   private final ToIntFunction cobwebChanceFunction;

   protected WeavingStatusEffect(StatusEffectCategory category, int color, ToIntFunction cobwebChanceFunction) {
      super(category, color, ParticleTypes.ITEM_COBWEB);
      this.cobwebChanceFunction = cobwebChanceFunction;
   }

   public void onEntityRemoval(ServerWorld world, LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
      if (reason == Entity.RemovalReason.KILLED && (entity instanceof PlayerEntity || world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING))) {
         this.tryPlaceCobweb(world, entity.getRandom(), entity.getBlockPos());
      }

   }

   private void tryPlaceCobweb(ServerWorld world, Random random, BlockPos pos) {
      Set set = Sets.newHashSet();
      int i = this.cobwebChanceFunction.applyAsInt(random);
      Iterator var6 = BlockPos.iterateRandomly(random, 15, pos, 1).iterator();

      BlockPos blockPos;
      while(var6.hasNext()) {
         blockPos = (BlockPos)var6.next();
         BlockPos blockPos2 = blockPos.down();
         if (!set.contains(blockPos) && world.getBlockState(blockPos).isReplaceable() && world.getBlockState(blockPos2).isSideSolidFullSquare(world, blockPos2, Direction.UP)) {
            set.add(blockPos.toImmutable());
            if (set.size() >= i) {
               break;
            }
         }
      }

      var6 = set.iterator();

      while(var6.hasNext()) {
         blockPos = (BlockPos)var6.next();
         world.setBlockState(blockPos, Blocks.COBWEB.getDefaultState(), 3);
         world.syncWorldEvent(3018, blockPos, 0);
      }

   }
}
