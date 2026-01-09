package net.minecraft.entity.ai.brain.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class BiasedLongJumpTask extends LongJumpTask {
   private final TagKey favoredBlocks;
   private final float biasChance;
   private final List unfavoredTargets = new ArrayList();
   private boolean useBias;

   public BiasedLongJumpTask(UniformIntProvider cooldownRange, int verticalRange, int horizontalRange, float maxRange, Function entityToSound, TagKey favoredBlocks, float biasChance, BiPredicate jumpToPredicate) {
      super(cooldownRange, verticalRange, horizontalRange, maxRange, entityToSound, jumpToPredicate);
      this.favoredBlocks = favoredBlocks;
      this.biasChance = biasChance;
   }

   protected void run(ServerWorld serverWorld, MobEntity mobEntity, long l) {
      super.run(serverWorld, mobEntity, l);
      this.unfavoredTargets.clear();
      this.useBias = mobEntity.getRandom().nextFloat() < this.biasChance;
   }

   protected Optional removeRandomTarget(ServerWorld world) {
      if (!this.useBias) {
         return super.removeRandomTarget(world);
      } else {
         BlockPos.Mutable mutable = new BlockPos.Mutable();

         while(!this.potentialTargets.isEmpty()) {
            Optional optional = super.removeRandomTarget(world);
            if (optional.isPresent()) {
               LongJumpTask.Target target = (LongJumpTask.Target)optional.get();
               if (world.getBlockState(mutable.set(target.pos(), (Direction)Direction.DOWN)).isIn(this.favoredBlocks)) {
                  return optional;
               }

               this.unfavoredTargets.add(target);
            }
         }

         if (!this.unfavoredTargets.isEmpty()) {
            return Optional.of((LongJumpTask.Target)this.unfavoredTargets.remove(0));
         } else {
            return Optional.empty();
         }
      }
   }

   // $FF: synthetic method
   protected void run(final ServerWorld world, final LivingEntity entity, final long time) {
      this.run(world, (MobEntity)entity, time);
   }
}
