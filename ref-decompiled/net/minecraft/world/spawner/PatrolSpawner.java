package net.minecraft.world.spawner;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;

public class PatrolSpawner implements SpecialSpawner {
   private int cooldown;

   public void spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
      if (spawnMonsters) {
         if (world.getGameRules().getBoolean(GameRules.DO_PATROL_SPAWNING)) {
            Random random = world.random;
            --this.cooldown;
            if (this.cooldown <= 0) {
               this.cooldown += 12000 + random.nextInt(1200);
               long l = world.getTimeOfDay() / 24000L;
               if (l >= 5L && world.isDay()) {
                  if (random.nextInt(5) == 0) {
                     int i = world.getPlayers().size();
                     if (i >= 1) {
                        PlayerEntity playerEntity = (PlayerEntity)world.getPlayers().get(random.nextInt(i));
                        if (!playerEntity.isSpectator()) {
                           if (!world.isNearOccupiedPointOfInterest(playerEntity.getBlockPos(), 2)) {
                              int j = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                              int k = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                              BlockPos.Mutable mutable = playerEntity.getBlockPos().mutableCopy().move(j, 0, k);
                              int m = true;
                              if (world.isRegionLoaded(mutable.getX() - 10, mutable.getZ() - 10, mutable.getX() + 10, mutable.getZ() + 10)) {
                                 RegistryEntry registryEntry = world.getBiome(mutable);
                                 if (!registryEntry.isIn(BiomeTags.WITHOUT_PATROL_SPAWNS)) {
                                    int n = (int)Math.ceil((double)world.getLocalDifficulty(mutable).getLocalDifficulty()) + 1;

                                    for(int o = 0; o < n; ++o) {
                                       mutable.setY(world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, mutable).getY());
                                       if (o == 0) {
                                          if (!this.spawnPillager(world, mutable, random, true)) {
                                             break;
                                          }
                                       } else {
                                          this.spawnPillager(world, mutable, random, false);
                                       }

                                       mutable.setX(mutable.getX() + random.nextInt(5) - random.nextInt(5));
                                       mutable.setZ(mutable.getZ() + random.nextInt(5) - random.nextInt(5));
                                    }

                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean spawnPillager(ServerWorld world, BlockPos pos, Random random, boolean captain) {
      BlockState blockState = world.getBlockState(pos);
      if (!SpawnHelper.isClearForSpawn(world, pos, blockState, blockState.getFluidState(), EntityType.PILLAGER)) {
         return false;
      } else if (!PatrolEntity.canSpawn(EntityType.PILLAGER, world, SpawnReason.PATROL, pos, random)) {
         return false;
      } else {
         PatrolEntity patrolEntity = (PatrolEntity)EntityType.PILLAGER.create(world, SpawnReason.PATROL);
         if (patrolEntity != null) {
            if (captain) {
               patrolEntity.setPatrolLeader(true);
               patrolEntity.setRandomPatrolTarget();
            }

            patrolEntity.setPosition((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
            patrolEntity.initialize(world, world.getLocalDifficulty(pos), SpawnReason.PATROL, (EntityData)null);
            world.spawnEntityAndPassengers(patrolEntity);
            return true;
         } else {
            return false;
         }
      }
   }
}
