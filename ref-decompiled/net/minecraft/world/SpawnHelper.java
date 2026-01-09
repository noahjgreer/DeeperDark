package net.minecraft.world;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.GravityField;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.NetherFortressStructure;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureKeys;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public final class SpawnHelper {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MIN_SPAWN_DISTANCE = 24;
   public static final int field_30972 = 8;
   public static final int field_30973 = 128;
   public static final int field_56560;
   static final int CHUNK_AREA;
   private static final SpawnGroup[] SPAWNABLE_GROUPS;

   private SpawnHelper() {
   }

   public static Info setupSpawn(int spawningChunkCount, Iterable entities, ChunkSource chunkSource, SpawnDensityCapper densityCapper) {
      GravityField gravityField = new GravityField();
      Object2IntOpenHashMap object2IntOpenHashMap = new Object2IntOpenHashMap();
      Iterator var6 = entities.iterator();

      while(true) {
         Entity entity;
         MobEntity mobEntity;
         do {
            if (!var6.hasNext()) {
               return new Info(spawningChunkCount, object2IntOpenHashMap, gravityField, densityCapper);
            }

            entity = (Entity)var6.next();
            if (!(entity instanceof MobEntity)) {
               break;
            }

            mobEntity = (MobEntity)entity;
         } while(mobEntity.isPersistent() || mobEntity.cannotDespawn());

         SpawnGroup spawnGroup = entity.getType().getSpawnGroup();
         if (spawnGroup != SpawnGroup.MISC) {
            BlockPos blockPos = entity.getBlockPos();
            chunkSource.query(ChunkPos.toLong(blockPos), (chunk) -> {
               SpawnSettings.SpawnDensity spawnDensity = getBiomeDirectly(blockPos, chunk).getSpawnSettings().getSpawnDensity(entity.getType());
               if (spawnDensity != null) {
                  gravityField.addPoint(entity.getBlockPos(), spawnDensity.mass());
               }

               if (entity instanceof MobEntity) {
                  densityCapper.increaseDensity(chunk.getPos(), spawnGroup);
               }

               object2IntOpenHashMap.addTo(spawnGroup, 1);
            });
         }
      }
   }

   static Biome getBiomeDirectly(BlockPos pos, Chunk chunk) {
      return (Biome)chunk.getBiomeForNoiseGen(BiomeCoords.fromBlock(pos.getX()), BiomeCoords.fromBlock(pos.getY()), BiomeCoords.fromBlock(pos.getZ())).value();
   }

   public static List collectSpawnableGroups(Info info, boolean spawnAnimals, boolean spawnMonsters, boolean rare) {
      List list = new ArrayList(SPAWNABLE_GROUPS.length);
      SpawnGroup[] var5 = SPAWNABLE_GROUPS;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         SpawnGroup spawnGroup = var5[var7];
         if ((spawnAnimals || !spawnGroup.isPeaceful()) && (spawnMonsters || spawnGroup.isPeaceful()) && (rare || !spawnGroup.isRare()) && info.isBelowCap(spawnGroup)) {
            list.add(spawnGroup);
         }
      }

      return list;
   }

   public static void spawn(ServerWorld world, WorldChunk chunk, Info info, List spawnableGroups) {
      Profiler profiler = Profilers.get();
      profiler.push("spawner");
      Iterator var5 = spawnableGroups.iterator();

      while(var5.hasNext()) {
         SpawnGroup spawnGroup = (SpawnGroup)var5.next();
         if (info.canSpawn(spawnGroup, chunk.getPos())) {
            Objects.requireNonNull(info);
            Checker var10003 = info::test;
            Objects.requireNonNull(info);
            spawnEntitiesInChunk(spawnGroup, world, chunk, var10003, info::run);
         }
      }

      profiler.pop();
   }

   public static void spawnEntitiesInChunk(SpawnGroup group, ServerWorld world, WorldChunk chunk, Checker checker, Runner runner) {
      BlockPos blockPos = getRandomPosInChunkSection(world, chunk);
      if (blockPos.getY() >= world.getBottomY() + 1) {
         spawnEntitiesInChunk(group, world, chunk, blockPos, checker, runner);
      }
   }

   @Debug
   public static void spawnEntitiesInChunk(SpawnGroup group, ServerWorld world, BlockPos pos) {
      spawnEntitiesInChunk(group, world, world.getChunk(pos), pos, (type, posx, chunk) -> {
         return true;
      }, (entity, chunk) -> {
      });
   }

   public static void spawnEntitiesInChunk(SpawnGroup group, ServerWorld world, Chunk chunk, BlockPos pos, Checker checker, Runner runner) {
      StructureAccessor structureAccessor = world.getStructureAccessor();
      ChunkGenerator chunkGenerator = world.getChunkManager().getChunkGenerator();
      int i = pos.getY();
      BlockState blockState = chunk.getBlockState(pos);
      if (!blockState.isSolidBlock(chunk, pos)) {
         BlockPos.Mutable mutable = new BlockPos.Mutable();
         int j = 0;

         for(int k = 0; k < 3; ++k) {
            int l = pos.getX();
            int m = pos.getZ();
            int n = true;
            SpawnSettings.SpawnEntry spawnEntry = null;
            EntityData entityData = null;
            int o = MathHelper.ceil(world.random.nextFloat() * 4.0F);
            int p = 0;

            for(int q = 0; q < o; ++q) {
               l += world.random.nextInt(6) - world.random.nextInt(6);
               m += world.random.nextInt(6) - world.random.nextInt(6);
               mutable.set(l, i, m);
               double d = (double)l + 0.5;
               double e = (double)m + 0.5;
               PlayerEntity playerEntity = world.getClosestPlayer(d, (double)i, e, -1.0, false);
               if (playerEntity != null) {
                  double f = playerEntity.squaredDistanceTo(d, (double)i, e);
                  if (isAcceptableSpawnPosition(world, chunk, mutable, f)) {
                     if (spawnEntry == null) {
                        Optional optional = pickRandomSpawnEntry(world, structureAccessor, chunkGenerator, group, world.random, mutable);
                        if (optional.isEmpty()) {
                           break;
                        }

                        spawnEntry = (SpawnSettings.SpawnEntry)optional.get();
                        o = spawnEntry.minGroupSize() + world.random.nextInt(1 + spawnEntry.maxGroupSize() - spawnEntry.minGroupSize());
                     }

                     if (canSpawn(world, group, structureAccessor, chunkGenerator, spawnEntry, mutable, f) && checker.test(spawnEntry.type(), mutable, chunk)) {
                        MobEntity mobEntity = createMob(world, spawnEntry.type());
                        if (mobEntity == null) {
                           return;
                        }

                        mobEntity.refreshPositionAndAngles(d, (double)i, e, world.random.nextFloat() * 360.0F, 0.0F);
                        if (isValidSpawn(world, mobEntity, f)) {
                           entityData = mobEntity.initialize(world, world.getLocalDifficulty(mobEntity.getBlockPos()), SpawnReason.NATURAL, entityData);
                           ++j;
                           ++p;
                           world.spawnEntityAndPassengers(mobEntity);
                           runner.run(mobEntity, chunk);
                           if (j >= mobEntity.getLimitPerChunk()) {
                              return;
                           }

                           if (mobEntity.spawnsTooManyForEachTry(p)) {
                              break;
                           }
                        }
                     }
                  }
               }
            }
         }

      }
   }

   private static boolean isAcceptableSpawnPosition(ServerWorld world, Chunk chunk, BlockPos.Mutable pos, double squaredDistance) {
      if (squaredDistance <= 576.0) {
         return false;
      } else if (world.getSpawnPos().isWithinDistance(new Vec3d((double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5), 24.0)) {
         return false;
      } else {
         ChunkPos chunkPos = new ChunkPos(pos);
         return Objects.equals(chunkPos, chunk.getPos()) || world.canSpawnEntitiesAt(chunkPos);
      }
   }

   private static boolean canSpawn(ServerWorld world, SpawnGroup group, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, SpawnSettings.SpawnEntry spawnEntry, BlockPos.Mutable pos, double squaredDistance) {
      EntityType entityType = spawnEntry.type();
      if (entityType.getSpawnGroup() == SpawnGroup.MISC) {
         return false;
      } else if (!entityType.isSpawnableFarFromPlayer() && squaredDistance > (double)(entityType.getSpawnGroup().getImmediateDespawnRange() * entityType.getSpawnGroup().getImmediateDespawnRange())) {
         return false;
      } else if (entityType.isSummonable() && containsSpawnEntry(world, structureAccessor, chunkGenerator, group, spawnEntry, pos)) {
         if (!SpawnRestriction.isSpawnPosAllowed(entityType, world, pos)) {
            return false;
         } else if (!SpawnRestriction.canSpawn(entityType, world, SpawnReason.NATURAL, pos, world.random)) {
            return false;
         } else {
            return world.isSpaceEmpty(entityType.getSpawnBox((double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5));
         }
      } else {
         return false;
      }
   }

   @Nullable
   private static MobEntity createMob(ServerWorld world, EntityType type) {
      try {
         Entity var3 = type.create(world, SpawnReason.NATURAL);
         if (var3 instanceof MobEntity mobEntity) {
            return mobEntity;
         }

         LOGGER.warn("Can't spawn entity of type: {}", Registries.ENTITY_TYPE.getId(type));
      } catch (Exception var4) {
         LOGGER.warn("Failed to create mob", var4);
      }

      return null;
   }

   private static boolean isValidSpawn(ServerWorld world, MobEntity entity, double squaredDistance) {
      if (squaredDistance > (double)(entity.getType().getSpawnGroup().getImmediateDespawnRange() * entity.getType().getSpawnGroup().getImmediateDespawnRange()) && entity.canImmediatelyDespawn(squaredDistance)) {
         return false;
      } else {
         return entity.canSpawn(world, SpawnReason.NATURAL) && entity.canSpawn(world);
      }
   }

   private static Optional pickRandomSpawnEntry(ServerWorld world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, SpawnGroup spawnGroup, Random random, BlockPos pos) {
      RegistryEntry registryEntry = world.getBiome(pos);
      return spawnGroup == SpawnGroup.WATER_AMBIENT && registryEntry.isIn(BiomeTags.REDUCE_WATER_AMBIENT_SPAWNS) && random.nextFloat() < 0.98F ? Optional.empty() : getSpawnEntries(world, structureAccessor, chunkGenerator, spawnGroup, pos, registryEntry).getOrEmpty(random);
   }

   private static boolean containsSpawnEntry(ServerWorld world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, SpawnGroup spawnGroup, SpawnSettings.SpawnEntry spawnEntry, BlockPos pos) {
      return getSpawnEntries(world, structureAccessor, chunkGenerator, spawnGroup, pos, (RegistryEntry)null).contains(spawnEntry);
   }

   private static Pool getSpawnEntries(ServerWorld world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, SpawnGroup spawnGroup, BlockPos pos, @Nullable RegistryEntry biomeEntry) {
      return shouldUseNetherFortressSpawns(pos, world, spawnGroup, structureAccessor) ? NetherFortressStructure.MONSTER_SPAWNS : chunkGenerator.getEntitySpawnList(biomeEntry != null ? biomeEntry : world.getBiome(pos), structureAccessor, spawnGroup, pos);
   }

   public static boolean shouldUseNetherFortressSpawns(BlockPos pos, ServerWorld world, SpawnGroup spawnGroup, StructureAccessor structureAccessor) {
      if (spawnGroup == SpawnGroup.MONSTER && world.getBlockState(pos.down()).isOf(Blocks.NETHER_BRICKS)) {
         Structure structure = (Structure)structureAccessor.getRegistryManager().getOrThrow(RegistryKeys.STRUCTURE).get(StructureKeys.FORTRESS);
         return structure == null ? false : structureAccessor.getStructureAt(pos, structure).hasChildren();
      } else {
         return false;
      }
   }

   private static BlockPos getRandomPosInChunkSection(World world, WorldChunk chunk) {
      ChunkPos chunkPos = chunk.getPos();
      int i = chunkPos.getStartX() + world.random.nextInt(16);
      int j = chunkPos.getStartZ() + world.random.nextInt(16);
      int k = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, i, j) + 1;
      int l = MathHelper.nextBetween(world.random, world.getBottomY(), k);
      return new BlockPos(i, l, j);
   }

   public static boolean isClearForSpawn(BlockView blockView, BlockPos pos, BlockState state, FluidState fluidState, EntityType entityType) {
      if (state.isFullCube(blockView, pos)) {
         return false;
      } else if (state.emitsRedstonePower()) {
         return false;
      } else if (!fluidState.isEmpty()) {
         return false;
      } else if (state.isIn(BlockTags.PREVENT_MOB_SPAWNING_INSIDE)) {
         return false;
      } else {
         return !entityType.isInvalidSpawn(state);
      }
   }

   public static void populateEntities(ServerWorldAccess world, RegistryEntry biomeEntry, ChunkPos chunkPos, Random random) {
      SpawnSettings spawnSettings = ((Biome)biomeEntry.value()).getSpawnSettings();
      Pool pool = spawnSettings.getSpawnEntries(SpawnGroup.CREATURE);
      if (!pool.isEmpty()) {
         int i = chunkPos.getStartX();
         int j = chunkPos.getStartZ();

         while(true) {
            Optional optional;
            do {
               if (!(random.nextFloat() < spawnSettings.getCreatureSpawnProbability())) {
                  return;
               }

               optional = pool.getOrEmpty(random);
            } while(optional.isEmpty());

            SpawnSettings.SpawnEntry spawnEntry = (SpawnSettings.SpawnEntry)optional.get();
            int k = spawnEntry.minGroupSize() + random.nextInt(1 + spawnEntry.maxGroupSize() - spawnEntry.minGroupSize());
            EntityData entityData = null;
            int l = i + random.nextInt(16);
            int m = j + random.nextInt(16);
            int n = l;
            int o = m;

            for(int p = 0; p < k; ++p) {
               boolean bl = false;

               for(int q = 0; !bl && q < 4; ++q) {
                  BlockPos blockPos = getEntitySpawnPos(world, spawnEntry.type(), l, m);
                  if (spawnEntry.type().isSummonable() && SpawnRestriction.isSpawnPosAllowed(spawnEntry.type(), world, blockPos)) {
                     float f = spawnEntry.type().getWidth();
                     double d = MathHelper.clamp((double)l, (double)i + (double)f, (double)i + 16.0 - (double)f);
                     double e = MathHelper.clamp((double)m, (double)j + (double)f, (double)j + 16.0 - (double)f);
                     if (!world.isSpaceEmpty(spawnEntry.type().getSpawnBox(d, (double)blockPos.getY(), e)) || !SpawnRestriction.canSpawn(spawnEntry.type(), world, SpawnReason.CHUNK_GENERATION, BlockPos.ofFloored(d, (double)blockPos.getY(), e), world.getRandom())) {
                        continue;
                     }

                     Entity entity;
                     try {
                        entity = spawnEntry.type().create(world.toServerWorld(), SpawnReason.NATURAL);
                     } catch (Exception var27) {
                        LOGGER.warn("Failed to create mob", var27);
                        continue;
                     }

                     if (entity == null) {
                        continue;
                     }

                     entity.refreshPositionAndAngles(d, (double)blockPos.getY(), e, random.nextFloat() * 360.0F, 0.0F);
                     if (entity instanceof MobEntity) {
                        MobEntity mobEntity = (MobEntity)entity;
                        if (mobEntity.canSpawn(world, SpawnReason.CHUNK_GENERATION) && mobEntity.canSpawn(world)) {
                           entityData = mobEntity.initialize(world, world.getLocalDifficulty(mobEntity.getBlockPos()), SpawnReason.CHUNK_GENERATION, entityData);
                           world.spawnEntityAndPassengers(mobEntity);
                           bl = true;
                        }
                     }
                  }

                  l += random.nextInt(5) - random.nextInt(5);

                  for(m += random.nextInt(5) - random.nextInt(5); l < i || l >= i + 16 || m < j || m >= j + 16; m = o + random.nextInt(5) - random.nextInt(5)) {
                     l = n + random.nextInt(5) - random.nextInt(5);
                  }
               }
            }
         }
      }
   }

   private static BlockPos getEntitySpawnPos(WorldView world, EntityType entityType, int x, int z) {
      int i = world.getTopY(SpawnRestriction.getHeightmapType(entityType), x, z);
      BlockPos.Mutable mutable = new BlockPos.Mutable(x, i, z);
      if (world.getDimension().hasCeiling()) {
         do {
            mutable.move(Direction.DOWN);
         } while(!world.getBlockState(mutable).isAir());

         do {
            mutable.move(Direction.DOWN);
         } while(world.getBlockState(mutable).isAir() && mutable.getY() > world.getBottomY());
      }

      return SpawnRestriction.getLocation(entityType).adjustPosition(world, mutable.toImmutable());
   }

   static {
      field_56560 = MathHelper.floor(8.0F / MathHelper.SQUARE_ROOT_OF_TWO);
      CHUNK_AREA = (int)Math.pow(17.0, 2.0);
      SPAWNABLE_GROUPS = (SpawnGroup[])Stream.of(SpawnGroup.values()).filter((spawnGroup) -> {
         return spawnGroup != SpawnGroup.MISC;
      }).toArray((i) -> {
         return new SpawnGroup[i];
      });
   }

   @FunctionalInterface
   public interface ChunkSource {
      void query(long pos, Consumer chunkConsumer);
   }

   public static class Info {
      private final int spawningChunkCount;
      private final Object2IntOpenHashMap groupToCount;
      private final GravityField densityField;
      private final Object2IntMap groupToCountView;
      private final SpawnDensityCapper densityCapper;
      @Nullable
      private BlockPos cachedPos;
      @Nullable
      private EntityType cachedEntityType;
      private double cachedDensityMass;

      Info(int spawningChunkCount, Object2IntOpenHashMap groupToCount, GravityField densityField, SpawnDensityCapper densityCapper) {
         this.spawningChunkCount = spawningChunkCount;
         this.groupToCount = groupToCount;
         this.densityField = densityField;
         this.densityCapper = densityCapper;
         this.groupToCountView = Object2IntMaps.unmodifiable(groupToCount);
      }

      private boolean test(EntityType type, BlockPos pos, Chunk chunk) {
         this.cachedPos = pos;
         this.cachedEntityType = type;
         SpawnSettings.SpawnDensity spawnDensity = SpawnHelper.getBiomeDirectly(pos, chunk).getSpawnSettings().getSpawnDensity(type);
         if (spawnDensity == null) {
            this.cachedDensityMass = 0.0;
            return true;
         } else {
            double d = spawnDensity.mass();
            this.cachedDensityMass = d;
            double e = this.densityField.calculate(pos, d);
            return e <= spawnDensity.gravityLimit();
         }
      }

      private void run(MobEntity entity, Chunk chunk) {
         EntityType entityType = entity.getType();
         BlockPos blockPos = entity.getBlockPos();
         double d;
         if (blockPos.equals(this.cachedPos) && entityType == this.cachedEntityType) {
            d = this.cachedDensityMass;
         } else {
            SpawnSettings.SpawnDensity spawnDensity = SpawnHelper.getBiomeDirectly(blockPos, chunk).getSpawnSettings().getSpawnDensity(entityType);
            if (spawnDensity != null) {
               d = spawnDensity.mass();
            } else {
               d = 0.0;
            }
         }

         this.densityField.addPoint(blockPos, d);
         SpawnGroup spawnGroup = entityType.getSpawnGroup();
         this.groupToCount.addTo(spawnGroup, 1);
         this.densityCapper.increaseDensity(new ChunkPos(blockPos), spawnGroup);
      }

      public int getSpawningChunkCount() {
         return this.spawningChunkCount;
      }

      public Object2IntMap getGroupToCount() {
         return this.groupToCountView;
      }

      boolean isBelowCap(SpawnGroup group) {
         int i = group.getCapacity() * this.spawningChunkCount / SpawnHelper.CHUNK_AREA;
         return this.groupToCount.getInt(group) < i;
      }

      boolean canSpawn(SpawnGroup group, ChunkPos chunkPos) {
         return this.densityCapper.canSpawn(group, chunkPos);
      }
   }

   @FunctionalInterface
   public interface Checker {
      boolean test(EntityType type, BlockPos pos, Chunk chunk);
   }

   @FunctionalInterface
   public interface Runner {
      void run(MobEntity entity, Chunk chunk);
   }
}
