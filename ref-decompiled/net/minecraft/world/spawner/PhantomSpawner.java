package net.minecraft.world.spawner;

import java.util.Iterator;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.SpawnHelper;

public class PhantomSpawner implements SpecialSpawner {
   private int cooldown;

   public void spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
      if (spawnMonsters) {
         if (world.getGameRules().getBoolean(GameRules.DO_INSOMNIA)) {
            Random random = world.random;
            --this.cooldown;
            if (this.cooldown <= 0) {
               this.cooldown += (60 + random.nextInt(60)) * 20;
               if (world.getAmbientDarkness() >= 5 || !world.getDimension().hasSkyLight()) {
                  Iterator var5 = world.getPlayers().iterator();

                  while(true) {
                     LocalDifficulty localDifficulty;
                     BlockPos blockPos2;
                     BlockState blockState;
                     FluidState fluidState;
                     do {
                        BlockPos blockPos;
                        int i;
                        do {
                           ServerPlayerEntity serverPlayerEntity;
                           do {
                              do {
                                 do {
                                    if (!var5.hasNext()) {
                                       return;
                                    }

                                    serverPlayerEntity = (ServerPlayerEntity)var5.next();
                                 } while(serverPlayerEntity.isSpectator());

                                 blockPos = serverPlayerEntity.getBlockPos();
                              } while(world.getDimension().hasSkyLight() && (blockPos.getY() < world.getSeaLevel() || !world.isSkyVisible(blockPos)));

                              localDifficulty = world.getLocalDifficulty(blockPos);
                           } while(!localDifficulty.isHarderThan(random.nextFloat() * 3.0F));

                           ServerStatHandler serverStatHandler = serverPlayerEntity.getStatHandler();
                           i = MathHelper.clamp(serverStatHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST)), 1, Integer.MAX_VALUE);
                           int j = true;
                        } while(random.nextInt(i) < 72000);

                        blockPos2 = blockPos.up(20 + random.nextInt(15)).east(-10 + random.nextInt(21)).south(-10 + random.nextInt(21));
                        blockState = world.getBlockState(blockPos2);
                        fluidState = world.getFluidState(blockPos2);
                     } while(!SpawnHelper.isClearForSpawn(world, blockPos2, blockState, fluidState, EntityType.PHANTOM));

                     EntityData entityData = null;
                     int k = 1 + random.nextInt(localDifficulty.getGlobalDifficulty().getId() + 1);

                     for(int l = 0; l < k; ++l) {
                        PhantomEntity phantomEntity = (PhantomEntity)EntityType.PHANTOM.create(world, SpawnReason.NATURAL);
                        if (phantomEntity != null) {
                           phantomEntity.refreshPositionAndAngles(blockPos2, 0.0F, 0.0F);
                           entityData = phantomEntity.initialize(world, localDifficulty, SpawnReason.NATURAL, entityData);
                           world.spawnEntityAndPassengers(phantomEntity);
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
