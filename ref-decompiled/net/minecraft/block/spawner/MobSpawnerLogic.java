package net.minecraft.block.spawner;

import com.mojang.logging.LogUtils;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public abstract class MobSpawnerLogic {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String SPAWN_DATA_KEY = "SpawnData";
   private static final int field_30951 = 1;
   private static final int field_57757 = 20;
   private static final int DEFAULT_MIN_SPAWN_DELAY = 200;
   private static final int DEFAULT_MAX_SPAWN_DELAY = 800;
   private static final int DEFAULT_SPAWN_COUNT = 4;
   private static final int DEFAULT_MAX_NEARBY_ENTITIES = 6;
   private static final int DEFAULT_REQUIRED_PLAYER_RANGE = 16;
   private static final int DEFAULT_SPAWN_RANGE = 4;
   private int spawnDelay = 20;
   private Pool spawnPotentials = Pool.empty();
   @Nullable
   private MobSpawnerEntry spawnEntry;
   private double rotation;
   private double lastRotation;
   private int minSpawnDelay = 200;
   private int maxSpawnDelay = 800;
   private int spawnCount = 4;
   @Nullable
   private Entity renderedEntity;
   private int maxNearbyEntities = 6;
   private int requiredPlayerRange = 16;
   private int spawnRange = 4;

   public void setEntityId(EntityType type, @Nullable World world, Random random, BlockPos pos) {
      this.getSpawnEntry(world, random, pos).getNbt().putString("id", Registries.ENTITY_TYPE.getId(type).toString());
   }

   private boolean isPlayerInRange(World world, BlockPos pos) {
      return world.isPlayerInRange((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, (double)this.requiredPlayerRange);
   }

   public void clientTick(World world, BlockPos pos) {
      if (!this.isPlayerInRange(world, pos)) {
         this.lastRotation = this.rotation;
      } else if (this.renderedEntity != null) {
         Random random = world.getRandom();
         double d = (double)pos.getX() + random.nextDouble();
         double e = (double)pos.getY() + random.nextDouble();
         double f = (double)pos.getZ() + random.nextDouble();
         world.addParticleClient(ParticleTypes.SMOKE, d, e, f, 0.0, 0.0, 0.0);
         world.addParticleClient(ParticleTypes.FLAME, d, e, f, 0.0, 0.0, 0.0);
         if (this.spawnDelay > 0) {
            --this.spawnDelay;
         }

         this.lastRotation = this.rotation;
         this.rotation = (this.rotation + (double)(1000.0F / ((float)this.spawnDelay + 200.0F))) % 360.0;
      }

   }

   public void serverTick(ServerWorld world, BlockPos pos) {
      if (this.isPlayerInRange(world, pos)) {
         if (this.spawnDelay == -1) {
            this.updateSpawns(world, pos);
         }

         if (this.spawnDelay > 0) {
            --this.spawnDelay;
         } else {
            boolean bl = false;
            Random random = world.getRandom();
            MobSpawnerEntry mobSpawnerEntry = this.getSpawnEntry(world, random, pos);

            for(int i = 0; i < this.spawnCount; ++i) {
               ErrorReporter.Logging logging = new ErrorReporter.Logging(this::toString, LOGGER);

               label144: {
                  label143: {
                     label142: {
                        label141: {
                           label140: {
                              label139: {
                                 label138: {
                                    label137: {
                                       label136: {
                                          label135: {
                                             try {
                                                label155: {
                                                   ReadView readView = NbtReadView.create(logging, world.getRegistryManager(), mobSpawnerEntry.getNbt());
                                                   Optional optional = EntityType.fromData(readView);
                                                   if (optional.isEmpty()) {
                                                      this.updateSpawns(world, pos);
                                                      break label143;
                                                   }

                                                   Vec3d vec3d = (Vec3d)readView.read("Pos", Vec3d.CODEC).orElseGet(() -> {
                                                      return new Vec3d((double)pos.getX() + (random.nextDouble() - random.nextDouble()) * (double)this.spawnRange + 0.5, (double)(pos.getY() + random.nextInt(3) - 1), (double)pos.getZ() + (random.nextDouble() - random.nextDouble()) * (double)this.spawnRange + 0.5);
                                                   });
                                                   if (!world.isSpaceEmpty(((EntityType)optional.get()).getSpawnBox(vec3d.x, vec3d.y, vec3d.z))) {
                                                      break label138;
                                                   }

                                                   BlockPos blockPos = BlockPos.ofFloored(vec3d);
                                                   if (mobSpawnerEntry.getCustomSpawnRules().isPresent()) {
                                                      if (!((EntityType)optional.get()).getSpawnGroup().isPeaceful() && world.getDifficulty() == Difficulty.PEACEFUL) {
                                                         break label136;
                                                      }

                                                      MobSpawnerEntry.CustomSpawnRules customSpawnRules = (MobSpawnerEntry.CustomSpawnRules)mobSpawnerEntry.getCustomSpawnRules().get();
                                                      if (!customSpawnRules.canSpawn(blockPos, world)) {
                                                         break label135;
                                                      }
                                                   } else if (!SpawnRestriction.canSpawn((EntityType)optional.get(), world, SpawnReason.SPAWNER, blockPos, world.getRandom())) {
                                                      break label155;
                                                   }

                                                   Entity entity = EntityType.loadEntityWithPassengers((ReadView)readView, world, SpawnReason.SPAWNER, (entityx) -> {
                                                      entityx.refreshPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, entityx.getYaw(), entityx.getPitch());
                                                      return entityx;
                                                   });
                                                   if (entity == null) {
                                                      this.updateSpawns(world, pos);
                                                      break label144;
                                                   }

                                                   int j = world.getEntitiesByType(TypeFilter.equals(entity.getClass()), (new Box((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1))).expand((double)this.spawnRange), EntityPredicates.EXCEPT_SPECTATOR).size();
                                                   if (j >= this.maxNearbyEntities) {
                                                      this.updateSpawns(world, pos);
                                                      break label142;
                                                   }

                                                   entity.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), random.nextFloat() * 360.0F, 0.0F);
                                                   if (entity instanceof MobEntity) {
                                                      MobEntity mobEntity = (MobEntity)entity;
                                                      if (mobSpawnerEntry.getCustomSpawnRules().isEmpty() && !mobEntity.canSpawn(world, SpawnReason.SPAWNER)) {
                                                         break label140;
                                                      }

                                                      if (!mobEntity.canSpawn(world)) {
                                                         break label139;
                                                      }

                                                      boolean bl2 = mobSpawnerEntry.getNbt().getSize() == 1 && mobSpawnerEntry.getNbt().getString("id").isPresent();
                                                      if (bl2) {
                                                         ((MobEntity)entity).initialize(world, world.getLocalDifficulty(entity.getBlockPos()), SpawnReason.SPAWNER, (EntityData)null);
                                                      }

                                                      Optional var10000 = mobSpawnerEntry.getEquipment();
                                                      Objects.requireNonNull(mobEntity);
                                                      var10000.ifPresent(mobEntity::setEquipmentFromTable);
                                                   }

                                                   if (!world.spawnNewEntityAndPassengers(entity)) {
                                                      this.updateSpawns(world, pos);
                                                      break label141;
                                                   }

                                                   world.syncWorldEvent(2004, pos, 0);
                                                   world.emitGameEvent(entity, GameEvent.ENTITY_PLACE, blockPos);
                                                   if (entity instanceof MobEntity) {
                                                      ((MobEntity)entity).playSpawnEffects();
                                                   }

                                                   bl = true;
                                                   break label137;
                                                }
                                             } catch (Throwable var17) {
                                                try {
                                                   logging.close();
                                                } catch (Throwable var16) {
                                                   var17.addSuppressed(var16);
                                                }

                                                throw var17;
                                             }

                                             logging.close();
                                             continue;
                                          }

                                          logging.close();
                                          continue;
                                       }

                                       logging.close();
                                       continue;
                                    }

                                    logging.close();
                                    continue;
                                 }

                                 logging.close();
                                 continue;
                              }

                              logging.close();
                              continue;
                           }

                           logging.close();
                           continue;
                        }

                        logging.close();
                        return;
                     }

                     logging.close();
                     return;
                  }

                  logging.close();
                  return;
               }

               logging.close();
               return;
            }

            if (bl) {
               this.updateSpawns(world, pos);
            }

         }
      }
   }

   private void updateSpawns(World world, BlockPos pos) {
      Random random = world.random;
      if (this.maxSpawnDelay <= this.minSpawnDelay) {
         this.spawnDelay = this.minSpawnDelay;
      } else {
         this.spawnDelay = this.minSpawnDelay + random.nextInt(this.maxSpawnDelay - this.minSpawnDelay);
      }

      this.spawnPotentials.getOrEmpty(random).ifPresent((spawnPotential) -> {
         this.setSpawnEntry(world, pos, spawnPotential);
      });
      this.sendStatus(world, pos, 1);
   }

   public void readData(@Nullable World world, BlockPos pos, ReadView view) {
      this.spawnDelay = view.getShort("Delay", (short)20);
      view.read("SpawnData", MobSpawnerEntry.CODEC).ifPresent((mobSpawnerEntry) -> {
         this.setSpawnEntry(world, pos, mobSpawnerEntry);
      });
      this.spawnPotentials = (Pool)view.read("SpawnPotentials", MobSpawnerEntry.DATA_POOL_CODEC).orElseGet(() -> {
         return Pool.of((Object)(this.spawnEntry != null ? this.spawnEntry : new MobSpawnerEntry()));
      });
      this.minSpawnDelay = view.getInt("MinSpawnDelay", 200);
      this.maxSpawnDelay = view.getInt("MaxSpawnDelay", 800);
      this.spawnCount = view.getInt("SpawnCount", 4);
      this.maxNearbyEntities = view.getInt("MaxNearbyEntities", 6);
      this.requiredPlayerRange = view.getInt("RequiredPlayerRange", 16);
      this.spawnRange = view.getInt("SpawnRange", 4);
      this.renderedEntity = null;
   }

   public void writeData(WriteView view) {
      view.putShort("Delay", (short)this.spawnDelay);
      view.putShort("MinSpawnDelay", (short)this.minSpawnDelay);
      view.putShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
      view.putShort("SpawnCount", (short)this.spawnCount);
      view.putShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
      view.putShort("RequiredPlayerRange", (short)this.requiredPlayerRange);
      view.putShort("SpawnRange", (short)this.spawnRange);
      view.putNullable("SpawnData", MobSpawnerEntry.CODEC, this.spawnEntry);
      view.put("SpawnPotentials", MobSpawnerEntry.DATA_POOL_CODEC, this.spawnPotentials);
   }

   @Nullable
   public Entity getRenderedEntity(World world, BlockPos pos) {
      if (this.renderedEntity == null) {
         NbtCompound nbtCompound = this.getSpawnEntry(world, world.getRandom(), pos).getNbt();
         if (nbtCompound.getString("id").isEmpty()) {
            return null;
         }

         this.renderedEntity = EntityType.loadEntityWithPassengers(nbtCompound, world, SpawnReason.SPAWNER, Function.identity());
         if (nbtCompound.getSize() == 1 && this.renderedEntity instanceof MobEntity) {
         }
      }

      return this.renderedEntity;
   }

   public boolean handleStatus(World world, int status) {
      if (status == 1) {
         if (world.isClient) {
            this.spawnDelay = this.minSpawnDelay;
         }

         return true;
      } else {
         return false;
      }
   }

   protected void setSpawnEntry(@Nullable World world, BlockPos pos, MobSpawnerEntry spawnEntry) {
      this.spawnEntry = spawnEntry;
   }

   private MobSpawnerEntry getSpawnEntry(@Nullable World world, Random random, BlockPos pos) {
      if (this.spawnEntry != null) {
         return this.spawnEntry;
      } else {
         this.setSpawnEntry(world, pos, (MobSpawnerEntry)this.spawnPotentials.getOrEmpty(random).orElseGet(MobSpawnerEntry::new));
         return this.spawnEntry;
      }
   }

   public abstract void sendStatus(World world, BlockPos pos, int status);

   public double getRotation() {
      return this.rotation;
   }

   public double getLastRotation() {
      return this.lastRotation;
   }
}
