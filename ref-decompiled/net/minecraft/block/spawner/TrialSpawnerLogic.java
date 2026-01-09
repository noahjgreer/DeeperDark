package net.minecraft.block.spawner;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TrialSpawnerBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.enums.TrialSpawnerState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.slf4j.Logger;

public final class TrialSpawnerLogic {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int field_47358 = 40;
   private static final int DEFAULT_COOLDOWN_LENGTH = 36000;
   private static final int DEFAULT_ENTITY_DETECTION_RANGE = 14;
   private static final int MAX_ENTITY_DISTANCE = 47;
   private static final int MAX_ENTITY_DISTANCE_SQUARED = MathHelper.square(47);
   private static final float SOUND_RATE_PER_TICK = 0.02F;
   private final TrialSpawnerData data = new TrialSpawnerData();
   private FullConfig fullConfig;
   private final TrialSpawner trialSpawner;
   private EntityDetector entityDetector;
   private final EntityDetector.Selector entitySelector;
   private boolean forceActivate;
   private boolean ominous;

   public TrialSpawnerLogic(FullConfig fullConfig, TrialSpawner trialSpawner, EntityDetector entityDetector, EntityDetector.Selector entitySelector) {
      this.fullConfig = fullConfig;
      this.trialSpawner = trialSpawner;
      this.entityDetector = entityDetector;
      this.entitySelector = entitySelector;
   }

   public TrialSpawnerConfig getConfig() {
      return this.ominous ? (TrialSpawnerConfig)this.fullConfig.ominous().value() : (TrialSpawnerConfig)this.fullConfig.normal.value();
   }

   public TrialSpawnerConfig getNormalConfig() {
      return (TrialSpawnerConfig)this.fullConfig.normal.value();
   }

   public TrialSpawnerConfig getOminousConfig() {
      return (TrialSpawnerConfig)this.fullConfig.ominous.value();
   }

   public void readData(ReadView view) {
      Optional var10000 = view.read(TrialSpawnerData.Packed.CODEC);
      TrialSpawnerData var10001 = this.data;
      Objects.requireNonNull(var10001);
      var10000.ifPresent(var10001::unpack);
      this.fullConfig = (FullConfig)view.read(TrialSpawnerLogic.FullConfig.CODEC).orElse(TrialSpawnerLogic.FullConfig.DEFAULT);
   }

   public void writeData(WriteView view) {
      view.put(TrialSpawnerData.Packed.CODEC, this.data.pack());
      view.put(TrialSpawnerLogic.FullConfig.CODEC, this.fullConfig);
   }

   public void setOminous(ServerWorld world, BlockPos pos) {
      world.setBlockState(pos, (BlockState)world.getBlockState(pos).with(TrialSpawnerBlock.OMINOUS, true), 3);
      world.syncWorldEvent(3020, pos, 1);
      this.ominous = true;
      this.data.resetAndClearMobs(this, world);
   }

   public void setNotOminous(ServerWorld world, BlockPos pos) {
      world.setBlockState(pos, (BlockState)world.getBlockState(pos).with(TrialSpawnerBlock.OMINOUS, false), 3);
      this.ominous = false;
   }

   public boolean isOminous() {
      return this.ominous;
   }

   public int getCooldownLength() {
      return this.fullConfig.targetCooldownLength;
   }

   public int getDetectionRadius() {
      return this.fullConfig.requiredPlayerRange;
   }

   public TrialSpawnerState getSpawnerState() {
      return this.trialSpawner.getSpawnerState();
   }

   public TrialSpawnerData getData() {
      return this.data;
   }

   public void setSpawnerState(World world, TrialSpawnerState spawnerState) {
      this.trialSpawner.setSpawnerState(world, spawnerState);
   }

   public void updateListeners() {
      this.trialSpawner.updateListeners();
   }

   public EntityDetector getEntityDetector() {
      return this.entityDetector;
   }

   public EntityDetector.Selector getEntitySelector() {
      return this.entitySelector;
   }

   public boolean canActivate(ServerWorld world) {
      if (this.forceActivate) {
         return true;
      } else {
         return world.getDifficulty() == Difficulty.PEACEFUL ? false : world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING);
      }
   }

   public Optional trySpawnMob(ServerWorld world, BlockPos pos) {
      Random random = world.getRandom();
      MobSpawnerEntry mobSpawnerEntry = this.data.getSpawnData(this, world.getRandom());
      ErrorReporter.Logging logging = new ErrorReporter.Logging(() -> {
         return "spawner@" + String.valueOf(pos);
      }, LOGGER);

      Optional var15;
      label110: {
         Optional var20;
         label109: {
            Optional var16;
            label108: {
               Optional var21;
               label107: {
                  label106: {
                     Optional var18;
                     label105: {
                        label104: {
                           label103: {
                              try {
                                 label124: {
                                    ReadView readView = NbtReadView.create(logging, world.getRegistryManager(), mobSpawnerEntry.entity());
                                    Optional optional = EntityType.fromData(readView);
                                    if (optional.isEmpty()) {
                                       var15 = Optional.empty();
                                       break label110;
                                    }

                                    Vec3d vec3d = (Vec3d)readView.read("Pos", Vec3d.CODEC).orElseGet(() -> {
                                       TrialSpawnerConfig trialSpawnerConfig = this.getConfig();
                                       return new Vec3d((double)pos.getX() + (random.nextDouble() - random.nextDouble()) * (double)trialSpawnerConfig.spawnRange() + 0.5, (double)(pos.getY() + random.nextInt(3) - 1), (double)pos.getZ() + (random.nextDouble() - random.nextDouble()) * (double)trialSpawnerConfig.spawnRange() + 0.5);
                                    });
                                    if (!world.isSpaceEmpty(((EntityType)optional.get()).getSpawnBox(vec3d.x, vec3d.y, vec3d.z))) {
                                       var16 = Optional.empty();
                                       break label108;
                                    }

                                    if (!hasLineOfSight(world, pos.toCenterPos(), vec3d)) {
                                       var16 = Optional.empty();
                                       break label106;
                                    }

                                    BlockPos blockPos = BlockPos.ofFloored(vec3d);
                                    if (!SpawnRestriction.canSpawn((EntityType)optional.get(), world, SpawnReason.TRIAL_SPAWNER, blockPos, world.getRandom())) {
                                       var18 = Optional.empty();
                                       break label105;
                                    }

                                    if (mobSpawnerEntry.getCustomSpawnRules().isPresent()) {
                                       MobSpawnerEntry.CustomSpawnRules customSpawnRules = (MobSpawnerEntry.CustomSpawnRules)mobSpawnerEntry.getCustomSpawnRules().get();
                                       if (!customSpawnRules.canSpawn(blockPos, world)) {
                                          var20 = Optional.empty();
                                          break label104;
                                       }
                                    }

                                    Entity entity = EntityType.loadEntityWithPassengers((ReadView)readView, world, SpawnReason.TRIAL_SPAWNER, (entityx) -> {
                                       entityx.refreshPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, random.nextFloat() * 360.0F, 0.0F);
                                       return entityx;
                                    });
                                    if (entity == null) {
                                       var20 = Optional.empty();
                                       break label103;
                                    }

                                    if (entity instanceof MobEntity mobEntity) {
                                       if (!mobEntity.canSpawn(world)) {
                                          var21 = Optional.empty();
                                          break label124;
                                       }

                                       boolean bl = mobSpawnerEntry.getNbt().getSize() == 1 && mobSpawnerEntry.getNbt().getString("id").isPresent();
                                       if (bl) {
                                          mobEntity.initialize(world, world.getLocalDifficulty(mobEntity.getBlockPos()), SpawnReason.TRIAL_SPAWNER, (EntityData)null);
                                       }

                                       mobEntity.setPersistent();
                                       Optional var10000 = mobSpawnerEntry.getEquipment();
                                       Objects.requireNonNull(mobEntity);
                                       var10000.ifPresent(mobEntity::setEquipmentFromTable);
                                    }

                                    if (!world.spawnNewEntityAndPassengers(entity)) {
                                       var20 = Optional.empty();
                                       break label109;
                                    }

                                    Type type = this.ominous ? TrialSpawnerLogic.Type.OMINOUS : TrialSpawnerLogic.Type.NORMAL;
                                    world.syncWorldEvent(3011, pos, type.getIndex());
                                    world.syncWorldEvent(3012, blockPos, type.getIndex());
                                    world.emitGameEvent(entity, GameEvent.ENTITY_PLACE, blockPos);
                                    var21 = Optional.of(entity.getUuid());
                                    break label107;
                                 }
                              } catch (Throwable var14) {
                                 try {
                                    logging.close();
                                 } catch (Throwable var13) {
                                    var14.addSuppressed(var13);
                                 }

                                 throw var14;
                              }

                              logging.close();
                              return var21;
                           }

                           logging.close();
                           return var20;
                        }

                        logging.close();
                        return var20;
                     }

                     logging.close();
                     return var18;
                  }

                  logging.close();
                  return var16;
               }

               logging.close();
               return var21;
            }

            logging.close();
            return var16;
         }

         logging.close();
         return var20;
      }

      logging.close();
      return var15;
   }

   public void ejectLootTable(ServerWorld world, BlockPos pos, RegistryKey lootTable) {
      LootTable lootTable2 = world.getServer().getReloadableRegistries().getLootTable(lootTable);
      LootWorldContext lootWorldContext = (new LootWorldContext.Builder(world)).build(LootContextTypes.EMPTY);
      ObjectArrayList objectArrayList = lootTable2.generateLoot(lootWorldContext);
      if (!objectArrayList.isEmpty()) {
         ObjectListIterator var7 = objectArrayList.iterator();

         while(var7.hasNext()) {
            ItemStack itemStack = (ItemStack)var7.next();
            ItemDispenserBehavior.spawnItem(world, itemStack, 2, Direction.UP, Vec3d.ofBottomCenter(pos).offset(Direction.UP, 1.2));
         }

         world.syncWorldEvent(3014, pos, 0);
      }

   }

   public void tickClient(World world, BlockPos pos, boolean ominous) {
      TrialSpawnerState trialSpawnerState = this.getSpawnerState();
      trialSpawnerState.emitParticles(world, pos, ominous);
      if (trialSpawnerState.doesDisplayRotate()) {
         double d = (double)Math.max(0L, this.data.nextMobSpawnsAt - world.getTime());
         this.data.lastDisplayEntityRotation = this.data.displayEntityRotation;
         this.data.displayEntityRotation = (this.data.displayEntityRotation + trialSpawnerState.getDisplayRotationSpeed() / (d + 200.0)) % 360.0;
      }

      if (trialSpawnerState.playsSound()) {
         Random random = world.getRandom();
         if (random.nextFloat() <= 0.02F) {
            SoundEvent soundEvent = ominous ? SoundEvents.BLOCK_TRIAL_SPAWNER_AMBIENT_OMINOUS : SoundEvents.BLOCK_TRIAL_SPAWNER_AMBIENT;
            world.playSoundAtBlockCenterClient(pos, soundEvent, SoundCategory.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false);
         }
      }

   }

   public void tickServer(ServerWorld world, BlockPos pos, boolean ominous) {
      this.ominous = ominous;
      TrialSpawnerState trialSpawnerState = this.getSpawnerState();
      if (this.data.spawnedMobsAlive.removeIf((uuid) -> {
         return shouldRemoveMobFromData(world, pos, uuid);
      })) {
         this.data.nextMobSpawnsAt = world.getTime() + (long)this.getConfig().ticksBetweenSpawn();
      }

      TrialSpawnerState trialSpawnerState2 = trialSpawnerState.tick(pos, this, world);
      if (trialSpawnerState2 != trialSpawnerState) {
         this.setSpawnerState(world, trialSpawnerState2);
      }

   }

   private static boolean shouldRemoveMobFromData(ServerWorld world, BlockPos pos, UUID uuid) {
      Entity entity = world.getEntity(uuid);
      return entity == null || !entity.isAlive() || !entity.getWorld().getRegistryKey().equals(world.getRegistryKey()) || entity.getBlockPos().getSquaredDistance(pos) > (double)MAX_ENTITY_DISTANCE_SQUARED;
   }

   private static boolean hasLineOfSight(World world, Vec3d spawnerPos, Vec3d spawnPos) {
      BlockHitResult blockHitResult = world.raycast(new RaycastContext(spawnPos, spawnerPos, RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, ShapeContext.absent()));
      return blockHitResult.getBlockPos().equals(BlockPos.ofFloored(spawnerPos)) || blockHitResult.getType() == HitResult.Type.MISS;
   }

   public static void addMobSpawnParticles(World world, BlockPos pos, Random random, SimpleParticleType particle) {
      for(int i = 0; i < 20; ++i) {
         double d = (double)pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
         double e = (double)pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
         double f = (double)pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
         world.addParticleClient(ParticleTypes.SMOKE, d, e, f, 0.0, 0.0, 0.0);
         world.addParticleClient(particle, d, e, f, 0.0, 0.0, 0.0);
      }

   }

   public static void addTrialOmenParticles(World world, BlockPos pos, Random random) {
      for(int i = 0; i < 20; ++i) {
         double d = (double)pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
         double e = (double)pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
         double f = (double)pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 2.0;
         double g = random.nextGaussian() * 0.02;
         double h = random.nextGaussian() * 0.02;
         double j = random.nextGaussian() * 0.02;
         world.addParticleClient(ParticleTypes.TRIAL_OMEN, d, e, f, g, h, j);
         world.addParticleClient(ParticleTypes.SOUL_FIRE_FLAME, d, e, f, g, h, j);
      }

   }

   public static void addDetectionParticles(World world, BlockPos pos, Random random, int playerCount, ParticleEffect particle) {
      for(int i = 0; i < 30 + Math.min(playerCount, 10) * 5; ++i) {
         double d = (double)(2.0F * random.nextFloat() - 1.0F) * 0.65;
         double e = (double)(2.0F * random.nextFloat() - 1.0F) * 0.65;
         double f = (double)pos.getX() + 0.5 + d;
         double g = (double)pos.getY() + 0.1 + (double)random.nextFloat() * 0.8;
         double h = (double)pos.getZ() + 0.5 + e;
         world.addParticleClient(particle, f, g, h, 0.0, 0.0, 0.0);
      }

   }

   public static void addEjectItemParticles(World world, BlockPos pos, Random random) {
      for(int i = 0; i < 20; ++i) {
         double d = (double)pos.getX() + 0.4 + random.nextDouble() * 0.2;
         double e = (double)pos.getY() + 0.4 + random.nextDouble() * 0.2;
         double f = (double)pos.getZ() + 0.4 + random.nextDouble() * 0.2;
         double g = random.nextGaussian() * 0.02;
         double h = random.nextGaussian() * 0.02;
         double j = random.nextGaussian() * 0.02;
         world.addParticleClient(ParticleTypes.SMALL_FLAME, d, e, f, g, h, j * 0.25);
         world.addParticleClient(ParticleTypes.SMOKE, d, e, f, g, h, j);
      }

   }

   public void setEntityType(EntityType entityType, World world) {
      this.data.reset();
      this.fullConfig = this.fullConfig.withEntityType(entityType);
      this.setSpawnerState(world, TrialSpawnerState.INACTIVE);
   }

   /** @deprecated */
   @Deprecated(
      forRemoval = true
   )
   @VisibleForTesting
   public void setEntityDetector(EntityDetector detector) {
      this.entityDetector = detector;
   }

   /** @deprecated */
   @Deprecated(
      forRemoval = true
   )
   @VisibleForTesting
   public void forceActivate() {
      this.forceActivate = true;
   }

   public static record FullConfig(RegistryEntry normal, RegistryEntry ominous, int targetCooldownLength, int requiredPlayerRange) {
      final RegistryEntry normal;
      final RegistryEntry ominous;
      final int targetCooldownLength;
      final int requiredPlayerRange;
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(TrialSpawnerConfig.ENTRY_CODEC.optionalFieldOf("normal_config", RegistryEntry.of(TrialSpawnerConfig.DEFAULT)).forGetter(FullConfig::normal), TrialSpawnerConfig.ENTRY_CODEC.optionalFieldOf("ominous_config", RegistryEntry.of(TrialSpawnerConfig.DEFAULT)).forGetter(FullConfig::ominous), Codecs.NON_NEGATIVE_INT.optionalFieldOf("target_cooldown_length", 36000).forGetter(FullConfig::targetCooldownLength), Codec.intRange(1, 128).optionalFieldOf("required_player_range", 14).forGetter(FullConfig::requiredPlayerRange)).apply(instance, FullConfig::new);
      });
      public static final FullConfig DEFAULT;

      public FullConfig(RegistryEntry registryEntry, RegistryEntry registryEntry2, int i, int j) {
         this.normal = registryEntry;
         this.ominous = registryEntry2;
         this.targetCooldownLength = i;
         this.requiredPlayerRange = j;
      }

      public FullConfig withEntityType(EntityType entityType) {
         return new FullConfig(RegistryEntry.of(((TrialSpawnerConfig)this.normal.value()).withSpawnPotential(entityType)), RegistryEntry.of(((TrialSpawnerConfig)this.ominous.value()).withSpawnPotential(entityType)), this.targetCooldownLength, this.requiredPlayerRange);
      }

      public RegistryEntry normal() {
         return this.normal;
      }

      public RegistryEntry ominous() {
         return this.ominous;
      }

      public int targetCooldownLength() {
         return this.targetCooldownLength;
      }

      public int requiredPlayerRange() {
         return this.requiredPlayerRange;
      }

      static {
         DEFAULT = new FullConfig(RegistryEntry.of(TrialSpawnerConfig.DEFAULT), RegistryEntry.of(TrialSpawnerConfig.DEFAULT), 36000, 14);
      }
   }

   public interface TrialSpawner {
      void setSpawnerState(World world, TrialSpawnerState spawnerState);

      TrialSpawnerState getSpawnerState();

      void updateListeners();
   }

   public static enum Type {
      NORMAL(ParticleTypes.FLAME),
      OMINOUS(ParticleTypes.SOUL_FIRE_FLAME);

      public final SimpleParticleType particle;

      private Type(final SimpleParticleType particle) {
         this.particle = particle;
      }

      public static Type fromIndex(int index) {
         Type[] types = values();
         return index <= types.length && index >= 0 ? types[index] : NORMAL;
      }

      public int getIndex() {
         return this.ordinal();
      }

      // $FF: synthetic method
      private static Type[] method_58711() {
         return new Type[]{NORMAL, OMINOUS};
      }
   }
}
