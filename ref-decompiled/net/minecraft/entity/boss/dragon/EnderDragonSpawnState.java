package net.minecraft.entity.boss.dragon;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import net.minecraft.world.gen.feature.EndSpikeFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public enum EnderDragonSpawnState {
   START {
      public void run(ServerWorld world, EnderDragonFight fight, List crystals, int tick, BlockPos pos) {
         BlockPos blockPos = new BlockPos(0, 128, 0);
         Iterator var7 = crystals.iterator();

         while(var7.hasNext()) {
            EndCrystalEntity endCrystalEntity = (EndCrystalEntity)var7.next();
            endCrystalEntity.setBeamTarget(blockPos);
         }

         fight.setSpawnState(PREPARING_TO_SUMMON_PILLARS);
      }
   },
   PREPARING_TO_SUMMON_PILLARS {
      public void run(ServerWorld world, EnderDragonFight fight, List crystals, int tick, BlockPos pos) {
         if (tick < 100) {
            if (tick == 0 || tick == 50 || tick == 51 || tick == 52 || tick >= 95) {
               world.syncWorldEvent(3001, new BlockPos(0, 128, 0), 0);
            }
         } else {
            fight.setSpawnState(SUMMONING_PILLARS);
         }

      }
   },
   SUMMONING_PILLARS {
      public void run(ServerWorld world, EnderDragonFight fight, List crystals, int tick, BlockPos pos) {
         int i = true;
         boolean bl = tick % 40 == 0;
         boolean bl2 = tick % 40 == 39;
         if (bl || bl2) {
            List list = EndSpikeFeature.getSpikes(world);
            int j = tick / 40;
            if (j < list.size()) {
               EndSpikeFeature.Spike spike = (EndSpikeFeature.Spike)list.get(j);
               if (bl) {
                  Iterator var12 = crystals.iterator();

                  while(var12.hasNext()) {
                     EndCrystalEntity endCrystalEntity = (EndCrystalEntity)var12.next();
                     endCrystalEntity.setBeamTarget(new BlockPos(spike.getCenterX(), spike.getHeight() + 1, spike.getCenterZ()));
                  }
               } else {
                  int k = true;
                  Iterator var16 = BlockPos.iterate(new BlockPos(spike.getCenterX() - 10, spike.getHeight() - 10, spike.getCenterZ() - 10), new BlockPos(spike.getCenterX() + 10, spike.getHeight() + 10, spike.getCenterZ() + 10)).iterator();

                  while(var16.hasNext()) {
                     BlockPos blockPos = (BlockPos)var16.next();
                     world.removeBlock(blockPos, false);
                  }

                  world.createExplosion((Entity)null, (double)((float)spike.getCenterX() + 0.5F), (double)spike.getHeight(), (double)((float)spike.getCenterZ() + 0.5F), 5.0F, World.ExplosionSourceType.BLOCK);
                  EndSpikeFeatureConfig endSpikeFeatureConfig = new EndSpikeFeatureConfig(true, ImmutableList.of(spike), new BlockPos(0, 128, 0));
                  Feature.END_SPIKE.generateIfValid(endSpikeFeatureConfig, world, world.getChunkManager().getChunkGenerator(), Random.create(), new BlockPos(spike.getCenterX(), 45, spike.getCenterZ()));
               }
            } else if (bl) {
               fight.setSpawnState(SUMMONING_DRAGON);
            }
         }

      }
   },
   SUMMONING_DRAGON {
      public void run(ServerWorld world, EnderDragonFight fight, List crystals, int tick, BlockPos pos) {
         Iterator var6;
         EndCrystalEntity endCrystalEntity;
         if (tick >= 100) {
            fight.setSpawnState(END);
            fight.resetEndCrystals();
            var6 = crystals.iterator();

            while(var6.hasNext()) {
               endCrystalEntity = (EndCrystalEntity)var6.next();
               endCrystalEntity.setBeamTarget((BlockPos)null);
               world.createExplosion(endCrystalEntity, endCrystalEntity.getX(), endCrystalEntity.getY(), endCrystalEntity.getZ(), 6.0F, World.ExplosionSourceType.NONE);
               endCrystalEntity.discard();
            }
         } else if (tick >= 80) {
            world.syncWorldEvent(3001, new BlockPos(0, 128, 0), 0);
         } else if (tick == 0) {
            var6 = crystals.iterator();

            while(var6.hasNext()) {
               endCrystalEntity = (EndCrystalEntity)var6.next();
               endCrystalEntity.setBeamTarget(new BlockPos(0, 128, 0));
            }
         } else if (tick < 5) {
            world.syncWorldEvent(3001, new BlockPos(0, 128, 0), 0);
         }

      }
   },
   END {
      public void run(ServerWorld world, EnderDragonFight fight, List crystals, int tick, BlockPos pos) {
      }
   };

   public abstract void run(ServerWorld world, EnderDragonFight fight, List crystals, int tick, BlockPos pos);

   // $FF: synthetic method
   private static EnderDragonSpawnState[] method_36745() {
      return new EnderDragonSpawnState[]{START, PREPARING_TO_SUMMON_PILLARS, SUMMONING_PILLARS, SUMMONING_DRAGON, END};
   }
}
