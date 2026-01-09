package net.minecraft.client.world;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FuelRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.network.packet.Packet;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.profiler.ScopedProfiler;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.Difficulty;
import net.minecraft.world.EntityList;
import net.minecraft.world.GameMode;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.ColorResolver;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.entity.ClientEntityManager;
import net.minecraft.world.entity.EntityHandler;
import net.minecraft.world.entity.EntityLookup;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.world.tick.EmptyTickSchedulers;
import net.minecraft.world.tick.QueryableTickScheduler;
import net.minecraft.world.tick.TickManager;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ClientWorld extends World implements DataCache.CacheContext {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Text QUITTING_MULTIPLAYER_TEXT = Text.translatable("multiplayer.status.quitting");
   private static final double PARTICLE_Y_OFFSET = 0.05;
   private static final int field_34805 = 10;
   private static final int field_34806 = 1000;
   final EntityList entityList = new EntityList();
   private final ClientEntityManager entityManager = new ClientEntityManager(Entity.class, new ClientEntityHandler());
   private final ClientPlayNetworkHandler networkHandler;
   private final WorldRenderer worldRenderer;
   private final WorldEventHandler worldEventHandler;
   private final Properties clientWorldProperties;
   private final DimensionEffects dimensionEffects;
   private final TickManager tickManager;
   private final MinecraftClient client = MinecraftClient.getInstance();
   final List players = Lists.newArrayList();
   final List enderDragonParts = Lists.newArrayList();
   private final Map mapStates = Maps.newHashMap();
   private static final int field_32640 = -1;
   private int lightningTicksLeft;
   private final Object2ObjectArrayMap colorCache = (Object2ObjectArrayMap)Util.make(new Object2ObjectArrayMap(3), (map) -> {
      map.put(BiomeColors.GRASS_COLOR, new BiomeColorCache((pos) -> {
         return this.calculateColor(pos, BiomeColors.GRASS_COLOR);
      }));
      map.put(BiomeColors.FOLIAGE_COLOR, new BiomeColorCache((pos) -> {
         return this.calculateColor(pos, BiomeColors.FOLIAGE_COLOR);
      }));
      map.put(BiomeColors.DRY_FOLIAGE_COLOR, new BiomeColorCache((pos) -> {
         return this.calculateColor(pos, BiomeColors.DRY_FOLIAGE_COLOR);
      }));
      map.put(BiomeColors.WATER_COLOR, new BiomeColorCache((pos) -> {
         return this.calculateColor(pos, BiomeColors.WATER_COLOR);
      }));
   });
   private final ClientChunkManager chunkManager;
   private final Deque chunkUpdaters = Queues.newArrayDeque();
   private int simulationDistance;
   private final PendingUpdateManager pendingUpdateManager = new PendingUpdateManager();
   private final Set blockEntities = new ReferenceOpenHashSet();
   private final int seaLevel;
   private boolean shouldTickTimeOfDay;
   private static final Set BLOCK_MARKER_ITEMS;

   public void handlePlayerActionResponse(int sequence) {
      this.pendingUpdateManager.processPendingUpdates(sequence, this);
   }

   public void loadBlockEntity(BlockEntity blockEntity) {
      BlockEntityRenderer blockEntityRenderer = this.client.getBlockEntityRenderDispatcher().get(blockEntity);
      if (blockEntityRenderer != null && blockEntityRenderer.rendersOutsideBoundingBox()) {
         this.blockEntities.add(blockEntity);
      }

   }

   public Set getBlockEntities() {
      return this.blockEntities;
   }

   public void handleBlockUpdate(BlockPos pos, BlockState state, int flags) {
      if (!this.pendingUpdateManager.hasPendingUpdate(pos, state)) {
         super.setBlockState(pos, state, flags, 512);
      }

   }

   public void processPendingUpdate(BlockPos pos, BlockState state, Vec3d playerPos) {
      BlockState blockState = this.getBlockState(pos);
      if (blockState != state) {
         this.setBlockState(pos, state, 19);
         PlayerEntity playerEntity = this.client.player;
         if (this == playerEntity.getWorld() && playerEntity.collidesWithStateAtPos(pos, state)) {
            playerEntity.updatePosition(playerPos.x, playerPos.y, playerPos.z);
         }
      }

   }

   PendingUpdateManager getPendingUpdateManager() {
      return this.pendingUpdateManager;
   }

   public boolean setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
      if (this.pendingUpdateManager.hasPendingSequence()) {
         BlockState blockState = this.getBlockState(pos);
         boolean bl = super.setBlockState(pos, state, flags, maxUpdateDepth);
         if (bl) {
            this.pendingUpdateManager.addPendingUpdate(pos, blockState, this.client.player);
         }

         return bl;
      } else {
         return super.setBlockState(pos, state, flags, maxUpdateDepth);
      }
   }

   public ClientWorld(ClientPlayNetworkHandler networkHandler, Properties properties, RegistryKey registryRef, RegistryEntry dimensionType, int loadDistance, int simulationDistance, WorldRenderer worldRenderer, boolean debugWorld, long seed, int seaLevel) {
      super(properties, registryRef, networkHandler.getRegistryManager(), dimensionType, true, debugWorld, seed, 1000000);
      this.networkHandler = networkHandler;
      this.chunkManager = new ClientChunkManager(this, loadDistance);
      this.tickManager = new TickManager();
      this.clientWorldProperties = properties;
      this.worldRenderer = worldRenderer;
      this.seaLevel = seaLevel;
      this.worldEventHandler = new WorldEventHandler(this.client, this, worldRenderer);
      this.dimensionEffects = DimensionEffects.byDimensionType((DimensionType)dimensionType.value());
      this.setSpawnPos(new BlockPos(8, 64, 8), 0.0F);
      this.simulationDistance = simulationDistance;
      this.calculateAmbientDarkness();
      this.initWeatherGradients();
   }

   public void enqueueChunkUpdate(Runnable updater) {
      this.chunkUpdaters.add(updater);
   }

   public void runQueuedChunkUpdates() {
      int i = this.chunkUpdaters.size();
      int j = i < 1000 ? Math.max(10, i / 10) : i;

      for(int k = 0; k < j; ++k) {
         Runnable runnable = (Runnable)this.chunkUpdaters.poll();
         if (runnable == null) {
            break;
         }

         runnable.run();
      }

   }

   public DimensionEffects getDimensionEffects() {
      return this.dimensionEffects;
   }

   public void tick(BooleanSupplier shouldKeepTicking) {
      this.getWorldBorder().tick();
      this.calculateAmbientDarkness();
      if (this.getTickManager().shouldTick()) {
         this.tickTime();
      }

      if (this.lightningTicksLeft > 0) {
         this.setLightningTicksLeft(this.lightningTicksLeft - 1);
      }

      ScopedProfiler scopedProfiler = Profilers.get().scoped("blocks");

      try {
         this.chunkManager.tick(shouldKeepTicking, true);
      } catch (Throwable var6) {
         if (scopedProfiler != null) {
            try {
               scopedProfiler.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (scopedProfiler != null) {
         scopedProfiler.close();
      }

   }

   private void tickTime() {
      this.clientWorldProperties.setTime(this.clientWorldProperties.getTime() + 1L);
      if (this.shouldTickTimeOfDay) {
         this.clientWorldProperties.setTimeOfDay(this.clientWorldProperties.getTimeOfDay() + 1L);
      }

   }

   public void setTime(long time, long timeOfDay, boolean shouldTickTimeOfDay) {
      this.clientWorldProperties.setTime(time);
      this.clientWorldProperties.setTimeOfDay(timeOfDay);
      this.shouldTickTimeOfDay = shouldTickTimeOfDay;
   }

   public Iterable getEntities() {
      return this.getEntityLookup().iterate();
   }

   public void tickEntities() {
      Profiler profiler = Profilers.get();
      profiler.push("entities");
      this.entityList.forEach((entity) -> {
         if (!entity.isRemoved() && !entity.hasVehicle() && !this.tickManager.shouldSkipTick(entity)) {
            this.tickEntity(this::tickEntity, entity);
         }
      });
      profiler.pop();
      this.tickBlockEntities();
   }

   public boolean hasEntity(Entity entity) {
      return this.entityList.has(entity);
   }

   public boolean shouldUpdatePostDeath(Entity entity) {
      return entity.getChunkPos().getChebyshevDistance(this.client.player.getChunkPos()) <= this.simulationDistance;
   }

   public void tickEntity(Entity entity) {
      entity.resetPosition();
      ++entity.age;
      Profilers.get().push(() -> {
         return Registries.ENTITY_TYPE.getId(entity.getType()).toString();
      });
      entity.tick();
      Profilers.get().pop();
      Iterator var2 = entity.getPassengerList().iterator();

      while(var2.hasNext()) {
         Entity entity2 = (Entity)var2.next();
         this.tickPassenger(entity, entity2);
      }

   }

   private void tickPassenger(Entity entity, Entity passenger) {
      if (!passenger.isRemoved() && passenger.getVehicle() == entity) {
         if (passenger instanceof PlayerEntity || this.entityList.has(passenger)) {
            passenger.resetPosition();
            ++passenger.age;
            passenger.tickRiding();
            Iterator var3 = passenger.getPassengerList().iterator();

            while(var3.hasNext()) {
               Entity entity2 = (Entity)var3.next();
               this.tickPassenger(passenger, entity2);
            }

         }
      } else {
         passenger.stopRiding();
      }
   }

   public void unloadBlockEntities(WorldChunk chunk) {
      chunk.clear();
      this.chunkManager.getLightingProvider().setColumnEnabled(chunk.getPos(), false);
      this.entityManager.stopTicking(chunk.getPos());
   }

   public void resetChunkColor(ChunkPos chunkPos) {
      this.colorCache.forEach((resolver, cache) -> {
         cache.reset(chunkPos.x, chunkPos.z);
      });
      this.entityManager.startTicking(chunkPos);
   }

   public void onChunkUnload(long sectionPos) {
      this.worldRenderer.onChunkUnload(sectionPos);
   }

   public void reloadColor() {
      this.colorCache.forEach((resolver, cache) -> {
         cache.reset();
      });
   }

   public boolean isChunkLoaded(int chunkX, int chunkZ) {
      return true;
   }

   public int getRegularEntityCount() {
      return this.entityManager.getEntityCount();
   }

   public void addEntity(Entity entity) {
      this.removeEntity(entity.getId(), Entity.RemovalReason.DISCARDED);
      this.entityManager.addEntity(entity);
   }

   public void removeEntity(int entityId, Entity.RemovalReason removalReason) {
      Entity entity = (Entity)this.getEntityLookup().get(entityId);
      if (entity != null) {
         entity.setRemoved(removalReason);
         entity.onRemoved();
      }

   }

   public List getCrammedEntities(Entity entity, Box box) {
      ClientPlayerEntity clientPlayerEntity = this.client.player;
      return clientPlayerEntity != null && clientPlayerEntity != entity && clientPlayerEntity.getBoundingBox().intersects(box) && EntityPredicates.canBePushedBy(entity).test(clientPlayerEntity) ? List.of(clientPlayerEntity) : List.of();
   }

   @Nullable
   public Entity getEntityById(int id) {
      return (Entity)this.getEntityLookup().get(id);
   }

   public void disconnect(Text reasonText) {
      this.networkHandler.getConnection().disconnect(reasonText);
   }

   public void doRandomBlockDisplayTicks(int centerX, int centerY, int centerZ) {
      int i = true;
      Random random = Random.create();
      Block block = this.getBlockParticle();
      BlockPos.Mutable mutable = new BlockPos.Mutable();

      for(int j = 0; j < 667; ++j) {
         this.randomBlockDisplayTick(centerX, centerY, centerZ, 16, random, block, mutable);
         this.randomBlockDisplayTick(centerX, centerY, centerZ, 32, random, block, mutable);
      }

   }

   @Nullable
   private Block getBlockParticle() {
      if (this.client.interactionManager.getCurrentGameMode() == GameMode.CREATIVE) {
         ItemStack itemStack = this.client.player.getMainHandStack();
         Item item = itemStack.getItem();
         if (BLOCK_MARKER_ITEMS.contains(item) && item instanceof BlockItem) {
            BlockItem blockItem = (BlockItem)item;
            return blockItem.getBlock();
         }
      }

      return null;
   }

   public void randomBlockDisplayTick(int centerX, int centerY, int centerZ, int radius, Random random, @Nullable Block block, BlockPos.Mutable pos) {
      int i = centerX + this.random.nextInt(radius) - this.random.nextInt(radius);
      int j = centerY + this.random.nextInt(radius) - this.random.nextInt(radius);
      int k = centerZ + this.random.nextInt(radius) - this.random.nextInt(radius);
      pos.set(i, j, k);
      BlockState blockState = this.getBlockState(pos);
      blockState.getBlock().randomDisplayTick(blockState, this, pos, random);
      FluidState fluidState = this.getFluidState(pos);
      if (!fluidState.isEmpty()) {
         fluidState.randomDisplayTick(this, pos, random);
         ParticleEffect particleEffect = fluidState.getParticle();
         if (particleEffect != null && this.random.nextInt(10) == 0) {
            boolean bl = blockState.isSideSolidFullSquare(this, pos, Direction.DOWN);
            BlockPos blockPos = pos.down();
            this.addParticle(blockPos, this.getBlockState(blockPos), particleEffect, bl);
         }
      }

      if (block == blockState.getBlock()) {
         this.addParticleClient(new BlockStateParticleEffect(ParticleTypes.BLOCK_MARKER, blockState), (double)i + 0.5, (double)j + 0.5, (double)k + 0.5, 0.0, 0.0, 0.0);
      }

      if (!blockState.isFullCube(this, pos)) {
         ((Biome)this.getBiome(pos).value()).getParticleConfig().ifPresent((config) -> {
            if (config.shouldAddParticle(this.random)) {
               this.addParticleClient(config.getParticle(), (double)pos.getX() + this.random.nextDouble(), (double)pos.getY() + this.random.nextDouble(), (double)pos.getZ() + this.random.nextDouble(), 0.0, 0.0, 0.0);
            }

         });
      }

   }

   private void addParticle(BlockPos pos, BlockState state, ParticleEffect parameters, boolean solidBelow) {
      if (state.getFluidState().isEmpty()) {
         VoxelShape voxelShape = state.getCollisionShape(this, pos);
         double d = voxelShape.getMax(Direction.Axis.Y);
         if (d < 1.0) {
            if (solidBelow) {
               this.addParticle((double)pos.getX(), (double)(pos.getX() + 1), (double)pos.getZ(), (double)(pos.getZ() + 1), (double)(pos.getY() + 1) - 0.05, parameters);
            }
         } else if (!state.isIn(BlockTags.IMPERMEABLE)) {
            double e = voxelShape.getMin(Direction.Axis.Y);
            if (e > 0.0) {
               this.addParticle(pos, parameters, voxelShape, (double)pos.getY() + e - 0.05);
            } else {
               BlockPos blockPos = pos.down();
               BlockState blockState = this.getBlockState(blockPos);
               VoxelShape voxelShape2 = blockState.getCollisionShape(this, blockPos);
               double f = voxelShape2.getMax(Direction.Axis.Y);
               if (f < 1.0 && blockState.getFluidState().isEmpty()) {
                  this.addParticle(pos, parameters, voxelShape, (double)pos.getY() - 0.05);
               }
            }
         }

      }
   }

   private void addParticle(BlockPos pos, ParticleEffect parameters, VoxelShape shape, double y) {
      this.addParticle((double)pos.getX() + shape.getMin(Direction.Axis.X), (double)pos.getX() + shape.getMax(Direction.Axis.X), (double)pos.getZ() + shape.getMin(Direction.Axis.Z), (double)pos.getZ() + shape.getMax(Direction.Axis.Z), y, parameters);
   }

   private void addParticle(double minX, double maxX, double minZ, double maxZ, double y, ParticleEffect parameters) {
      this.addParticleClient(parameters, MathHelper.lerp(this.random.nextDouble(), minX, maxX), y, MathHelper.lerp(this.random.nextDouble(), minZ, maxZ), 0.0, 0.0, 0.0);
   }

   public CrashReportSection addDetailsToCrashReport(CrashReport report) {
      CrashReportSection crashReportSection = super.addDetailsToCrashReport(report);
      crashReportSection.add("Server brand", () -> {
         return this.client.player.networkHandler.getBrand();
      });
      crashReportSection.add("Server type", () -> {
         return this.client.getServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server";
      });
      crashReportSection.add("Tracked entity count", () -> {
         return String.valueOf(this.getRegularEntityCount());
      });
      return crashReportSection;
   }

   public void playSound(@Nullable Entity source, double x, double y, double z, RegistryEntry sound, SoundCategory category, float volume, float pitch, long seed) {
      if (source == this.client.player) {
         this.playSound(x, y, z, (SoundEvent)sound.value(), category, volume, pitch, false, seed);
      }

   }

   public void playSoundFromEntity(@Nullable Entity source, Entity entity, RegistryEntry sound, SoundCategory category, float volume, float pitch, long seed) {
      if (source == this.client.player) {
         this.client.getSoundManager().play(new EntityTrackingSoundInstance((SoundEvent)sound.value(), category, volume, pitch, entity, seed));
      }

   }

   public void playSoundFromEntityClient(Entity entity, SoundEvent sound, SoundCategory category, float volume, float pitch) {
      this.client.getSoundManager().play(new EntityTrackingSoundInstance(sound, category, volume, pitch, entity, this.random.nextLong()));
   }

   public void playSoundClient(SoundEvent sound, SoundCategory category, float volume, float pitch) {
      if (this.client.player != null) {
         this.client.getSoundManager().play(new EntityTrackingSoundInstance(sound, category, volume, pitch, this.client.player, this.random.nextLong()));
      }

   }

   public void playSoundClient(double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean useDistance) {
      this.playSound(x, y, z, sound, category, volume, pitch, useDistance, this.random.nextLong());
   }

   private void playSound(double x, double y, double z, SoundEvent event, SoundCategory category, float volume, float pitch, boolean useDistance, long seed) {
      double d = this.client.gameRenderer.getCamera().getPos().squaredDistanceTo(x, y, z);
      PositionedSoundInstance positionedSoundInstance = new PositionedSoundInstance(event, category, volume, pitch, Random.create(seed), x, y, z);
      if (useDistance && d > 100.0) {
         double e = Math.sqrt(d) / 40.0;
         this.client.getSoundManager().play(positionedSoundInstance, (int)(e * 20.0));
      } else {
         this.client.getSoundManager().play(positionedSoundInstance);
      }

   }

   public void addFireworkParticle(double x, double y, double z, double velocityX, double velocityY, double velocityZ, List explosions) {
      if (explosions.isEmpty()) {
         for(int i = 0; i < this.random.nextInt(3) + 2; ++i) {
            this.addParticleClient(ParticleTypes.POOF, x, y, z, this.random.nextGaussian() * 0.05, 0.005, this.random.nextGaussian() * 0.05);
         }
      } else {
         this.client.particleManager.addParticle(new FireworksSparkParticle.FireworkParticle(this, x, y, z, velocityX, velocityY, velocityZ, this.client.particleManager, explosions));
      }

   }

   public void sendPacket(Packet packet) {
      this.networkHandler.sendPacket(packet);
   }

   public RecipeManager getRecipeManager() {
      return this.networkHandler.getRecipeManager();
   }

   public TickManager getTickManager() {
      return this.tickManager;
   }

   public QueryableTickScheduler getBlockTickScheduler() {
      return EmptyTickSchedulers.getClientTickScheduler();
   }

   public QueryableTickScheduler getFluidTickScheduler() {
      return EmptyTickSchedulers.getClientTickScheduler();
   }

   public ClientChunkManager getChunkManager() {
      return this.chunkManager;
   }

   @Nullable
   public MapState getMapState(MapIdComponent id) {
      return (MapState)this.mapStates.get(id);
   }

   public void putClientsideMapState(MapIdComponent id, MapState state) {
      this.mapStates.put(id, state);
   }

   public Scoreboard getScoreboard() {
      return this.networkHandler.getScoreboard();
   }

   public void updateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
      this.worldRenderer.updateBlock(this, pos, oldState, newState, flags);
   }

   public void scheduleBlockRerenderIfNeeded(BlockPos pos, BlockState old, BlockState updated) {
      this.worldRenderer.scheduleBlockRerenderIfNeeded(pos, old, updated);
   }

   public void scheduleBlockRenders(int x, int y, int z) {
      this.worldRenderer.scheduleChunkRenders3x3x3(x, y, z);
   }

   public void scheduleChunkRenders(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      this.worldRenderer.scheduleChunkRenders(minX, minY, minZ, maxX, maxY, maxZ);
   }

   public void setBlockBreakingInfo(int entityId, BlockPos pos, int progress) {
      this.worldRenderer.setBlockBreakingInfo(entityId, pos, progress);
   }

   public void syncGlobalEvent(int eventId, BlockPos pos, int data) {
      this.worldEventHandler.processGlobalEvent(eventId, pos, data);
   }

   public void syncWorldEvent(@Nullable Entity source, int eventId, BlockPos pos, int data) {
      try {
         this.worldEventHandler.processWorldEvent(eventId, pos, data);
      } catch (Throwable var8) {
         CrashReport crashReport = CrashReport.create(var8, "Playing level event");
         CrashReportSection crashReportSection = crashReport.addElement("Level event being played");
         crashReportSection.add("Block coordinates", (Object)CrashReportSection.createPositionString(this, pos));
         crashReportSection.add("Event source", (Object)source);
         crashReportSection.add("Event type", (Object)eventId);
         crashReportSection.add("Event data", (Object)data);
         throw new CrashException(crashReport);
      }
   }

   public void addParticleClient(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
      this.worldRenderer.addParticle(parameters, parameters.getType().shouldAlwaysSpawn(), x, y, z, velocityX, velocityY, velocityZ);
   }

   public void addParticleClient(ParticleEffect parameters, boolean force, boolean canSpawnOnMinimal, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
      this.worldRenderer.addParticle(parameters, parameters.getType().shouldAlwaysSpawn() || force, canSpawnOnMinimal, x, y, z, velocityX, velocityY, velocityZ);
   }

   public void addImportantParticleClient(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
      this.worldRenderer.addParticle(parameters, false, true, x, y, z, velocityX, velocityY, velocityZ);
   }

   public void addImportantParticleClient(ParticleEffect parameters, boolean force, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
      this.worldRenderer.addParticle(parameters, parameters.getType().shouldAlwaysSpawn() || force, true, x, y, z, velocityX, velocityY, velocityZ);
   }

   public List getPlayers() {
      return this.players;
   }

   public List getEnderDragonParts() {
      return this.enderDragonParts;
   }

   public RegistryEntry getGeneratorStoredBiome(int biomeX, int biomeY, int biomeZ) {
      return this.getRegistryManager().getOrThrow(RegistryKeys.BIOME).getOrThrow(BiomeKeys.PLAINS);
   }

   public float getSkyBrightness(float tickProgress) {
      float f = this.getSkyAngle(tickProgress);
      float g = 1.0F - (MathHelper.cos(f * 6.2831855F) * 2.0F + 0.2F);
      g = MathHelper.clamp(g, 0.0F, 1.0F);
      g = 1.0F - g;
      g *= 1.0F - this.getRainGradient(tickProgress) * 5.0F / 16.0F;
      g *= 1.0F - this.getThunderGradient(tickProgress) * 5.0F / 16.0F;
      return g * 0.8F + 0.2F;
   }

   public int getSkyColor(Vec3d cameraPos, float tickProgress) {
      float f = this.getSkyAngle(tickProgress);
      Vec3d vec3d = cameraPos.subtract(2.0, 2.0, 2.0).multiply(0.25);
      Vec3d vec3d2 = CubicSampler.sampleColor(vec3d, (x, y, z) -> {
         return Vec3d.unpackRgb(((Biome)this.getBiomeAccess().getBiomeForNoiseGen(x, y, z).value()).getSkyColor());
      });
      float g = MathHelper.cos(f * 6.2831855F) * 2.0F + 0.5F;
      g = MathHelper.clamp(g, 0.0F, 1.0F);
      vec3d2 = vec3d2.multiply((double)g);
      int i = ColorHelper.getArgb(vec3d2);
      float h = this.getRainGradient(tickProgress);
      float j;
      float k;
      if (h > 0.0F) {
         j = 0.6F;
         k = h * 0.75F;
         int l = ColorHelper.scaleRgb(ColorHelper.grayscale(i), 0.6F);
         i = ColorHelper.lerp(k, i, l);
      }

      j = this.getThunderGradient(tickProgress);
      float m;
      if (j > 0.0F) {
         k = 0.2F;
         m = j * 0.75F;
         int n = ColorHelper.scaleRgb(ColorHelper.grayscale(i), 0.2F);
         i = ColorHelper.lerp(m, i, n);
      }

      int o = this.getLightningTicksLeft();
      if (o > 0) {
         m = Math.min((float)o - tickProgress, 1.0F);
         m *= 0.45F;
         i = ColorHelper.lerp(m, i, ColorHelper.getArgb(204, 204, 255));
      }

      return i;
   }

   public int getCloudsColor(float tickProgress) {
      int i = -1;
      float f = this.getRainGradient(tickProgress);
      if (f > 0.0F) {
         int j = ColorHelper.scaleRgb(ColorHelper.grayscale(i), 0.6F);
         i = ColorHelper.lerp(f * 0.95F, i, j);
      }

      float g = this.getSkyAngle(tickProgress);
      float h = MathHelper.cos(g * 6.2831855F) * 2.0F + 0.5F;
      h = MathHelper.clamp(h, 0.0F, 1.0F);
      i = ColorHelper.mix(i, ColorHelper.fromFloats(1.0F, h * 0.9F + 0.1F, h * 0.9F + 0.1F, h * 0.85F + 0.15F));
      float k = this.getThunderGradient(tickProgress);
      if (k > 0.0F) {
         int l = ColorHelper.scaleRgb(ColorHelper.grayscale(i), 0.2F);
         i = ColorHelper.lerp(k * 0.95F, i, l);
      }

      return i;
   }

   public float getStarBrightness(float tickProgress) {
      float f = this.getSkyAngle(tickProgress);
      float g = 1.0F - (MathHelper.cos(f * 6.2831855F) * 2.0F + 0.25F);
      g = MathHelper.clamp(g, 0.0F, 1.0F);
      return g * g * 0.5F;
   }

   public int getLightningTicksLeft() {
      return (Boolean)this.client.options.getHideLightningFlashes().getValue() ? 0 : this.lightningTicksLeft;
   }

   public void setLightningTicksLeft(int lightningTicksLeft) {
      this.lightningTicksLeft = lightningTicksLeft;
   }

   public float getBrightness(Direction direction, boolean shaded) {
      boolean bl = this.getDimensionEffects().isDarkened();
      if (!shaded) {
         return bl ? 0.9F : 1.0F;
      } else {
         switch (direction) {
            case DOWN:
               return bl ? 0.9F : 0.5F;
            case UP:
               return bl ? 0.9F : 1.0F;
            case NORTH:
            case SOUTH:
               return 0.8F;
            case WEST:
            case EAST:
               return 0.6F;
            default:
               return 1.0F;
         }
      }
   }

   public int getColor(BlockPos pos, ColorResolver colorResolver) {
      BiomeColorCache biomeColorCache = (BiomeColorCache)this.colorCache.get(colorResolver);
      return biomeColorCache.getBiomeColor(pos);
   }

   public int calculateColor(BlockPos pos, ColorResolver colorResolver) {
      int i = (Integer)MinecraftClient.getInstance().options.getBiomeBlendRadius().getValue();
      if (i == 0) {
         return colorResolver.getColor((Biome)this.getBiome(pos).value(), (double)pos.getX(), (double)pos.getZ());
      } else {
         int j = (i * 2 + 1) * (i * 2 + 1);
         int k = 0;
         int l = 0;
         int m = 0;
         CuboidBlockIterator cuboidBlockIterator = new CuboidBlockIterator(pos.getX() - i, pos.getY(), pos.getZ() - i, pos.getX() + i, pos.getY(), pos.getZ() + i);

         int n;
         for(BlockPos.Mutable mutable = new BlockPos.Mutable(); cuboidBlockIterator.step(); m += n & 255) {
            mutable.set(cuboidBlockIterator.getX(), cuboidBlockIterator.getY(), cuboidBlockIterator.getZ());
            n = colorResolver.getColor((Biome)this.getBiome(mutable).value(), (double)mutable.getX(), (double)mutable.getZ());
            k += (n & 16711680) >> 16;
            l += (n & '\uff00') >> 8;
         }

         return (k / j & 255) << 16 | (l / j & 255) << 8 | m / j & 255;
      }
   }

   public void setSpawnPos(BlockPos pos, float angle) {
      this.properties.setSpawnPos(pos, angle);
   }

   public String toString() {
      return "ClientLevel";
   }

   public Properties getLevelProperties() {
      return this.clientWorldProperties;
   }

   public void emitGameEvent(RegistryEntry event, Vec3d emitterPos, GameEvent.Emitter emitter) {
   }

   protected Map getMapStates() {
      return ImmutableMap.copyOf(this.mapStates);
   }

   protected void putMapStates(Map mapStates) {
      this.mapStates.putAll(mapStates);
   }

   protected EntityLookup getEntityLookup() {
      return this.entityManager.getLookup();
   }

   public String asString() {
      String var10000 = this.chunkManager.getDebugString();
      return "Chunks[C] W: " + var10000 + " E: " + this.entityManager.getDebugString();
   }

   public void addBlockBreakParticles(BlockPos pos, BlockState state) {
      this.client.particleManager.addBlockBreakParticles(pos, state);
   }

   public void setSimulationDistance(int simulationDistance) {
      this.simulationDistance = simulationDistance;
   }

   public int getSimulationDistance() {
      return this.simulationDistance;
   }

   public FeatureSet getEnabledFeatures() {
      return this.networkHandler.getEnabledFeatures();
   }

   public BrewingRecipeRegistry getBrewingRecipeRegistry() {
      return this.networkHandler.getBrewingRecipeRegistry();
   }

   public FuelRegistry getFuelRegistry() {
      return this.networkHandler.getFuelRegistry();
   }

   public void createExplosion(@Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionBehavior behavior, double x, double y, double z, float power, boolean createFire, World.ExplosionSourceType explosionSourceType, ParticleEffect smallParticle, ParticleEffect largeParticle, RegistryEntry soundEvent) {
   }

   public int getSeaLevel() {
      return this.seaLevel;
   }

   public int getBlockColor(BlockPos pos) {
      return MinecraftClient.getInstance().getBlockColors().getColor(this.getBlockState(pos), this, pos, 0);
   }

   public void registerForCleaning(DataCache dataCache) {
      this.networkHandler.registerForCleaning(dataCache);
   }

   // $FF: synthetic method
   public WorldProperties getLevelProperties() {
      return this.getLevelProperties();
   }

   // $FF: synthetic method
   public Collection getEnderDragonParts() {
      return this.getEnderDragonParts();
   }

   // $FF: synthetic method
   public ChunkManager getChunkManager() {
      return this.getChunkManager();
   }

   static {
      BLOCK_MARKER_ITEMS = Set.of(Items.BARRIER, Items.LIGHT);
   }

   @Environment(EnvType.CLIENT)
   private final class ClientEntityHandler implements EntityHandler {
      ClientEntityHandler() {
      }

      public void create(Entity entity) {
      }

      public void destroy(Entity entity) {
      }

      public void startTicking(Entity entity) {
         ClientWorld.this.entityList.add(entity);
      }

      public void stopTicking(Entity entity) {
         ClientWorld.this.entityList.remove(entity);
      }

      public void startTracking(Entity entity) {
         Objects.requireNonNull(entity);
         byte var3 = 0;
         switch (entity.typeSwitch<invokedynamic>(entity, var3)) {
            case 0:
               AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)entity;
               ClientWorld.this.players.add(abstractClientPlayerEntity);
               break;
            case 1:
               EnderDragonEntity enderDragonEntity = (EnderDragonEntity)entity;
               ClientWorld.this.enderDragonParts.addAll(Arrays.asList(enderDragonEntity.getBodyParts()));
         }

      }

      public void stopTracking(Entity entity) {
         entity.detach();
         Objects.requireNonNull(entity);
         byte var3 = 0;
         switch (entity.typeSwitch<invokedynamic>(entity, var3)) {
            case 0:
               AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)entity;
               ClientWorld.this.players.remove(abstractClientPlayerEntity);
               break;
            case 1:
               EnderDragonEntity enderDragonEntity = (EnderDragonEntity)entity;
               ClientWorld.this.enderDragonParts.removeAll(Arrays.asList(enderDragonEntity.getBodyParts()));
         }

      }

      public void updateLoadStatus(Entity entity) {
      }

      // $FF: synthetic method
      public void updateLoadStatus(final Object entity) {
         this.updateLoadStatus((Entity)entity);
      }

      // $FF: synthetic method
      public void stopTracking(final Object entity) {
         this.stopTracking((Entity)entity);
      }

      // $FF: synthetic method
      public void startTracking(final Object entity) {
         this.startTracking((Entity)entity);
      }

      // $FF: synthetic method
      public void startTicking(final Object entity) {
         this.startTicking((Entity)entity);
      }

      // $FF: synthetic method
      public void destroy(final Object entity) {
         this.destroy((Entity)entity);
      }

      // $FF: synthetic method
      public void create(final Object entity) {
         this.create((Entity)entity);
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Properties implements MutableWorldProperties {
      private final boolean hardcore;
      private final boolean flatWorld;
      private BlockPos spawnPos;
      private float spawnAngle;
      private long time;
      private long timeOfDay;
      private boolean raining;
      private Difficulty difficulty;
      private boolean difficultyLocked;

      public Properties(Difficulty difficulty, boolean hardcore, boolean flatWorld) {
         this.difficulty = difficulty;
         this.hardcore = hardcore;
         this.flatWorld = flatWorld;
      }

      public BlockPos getSpawnPos() {
         return this.spawnPos;
      }

      public float getSpawnAngle() {
         return this.spawnAngle;
      }

      public long getTime() {
         return this.time;
      }

      public long getTimeOfDay() {
         return this.timeOfDay;
      }

      public void setTime(long time) {
         this.time = time;
      }

      public void setTimeOfDay(long timeOfDay) {
         this.timeOfDay = timeOfDay;
      }

      public void setSpawnPos(BlockPos pos, float angle) {
         this.spawnPos = pos.toImmutable();
         this.spawnAngle = angle;
      }

      public boolean isThundering() {
         return false;
      }

      public boolean isRaining() {
         return this.raining;
      }

      public void setRaining(boolean raining) {
         this.raining = raining;
      }

      public boolean isHardcore() {
         return this.hardcore;
      }

      public Difficulty getDifficulty() {
         return this.difficulty;
      }

      public boolean isDifficultyLocked() {
         return this.difficultyLocked;
      }

      public void populateCrashReport(CrashReportSection reportSection, HeightLimitView world) {
         MutableWorldProperties.super.populateCrashReport(reportSection, world);
      }

      public void setDifficulty(Difficulty difficulty) {
         this.difficulty = difficulty;
      }

      public void setDifficultyLocked(boolean difficultyLocked) {
         this.difficultyLocked = difficultyLocked;
      }

      public double getSkyDarknessHeight(HeightLimitView world) {
         return this.flatWorld ? (double)world.getBottomY() : 63.0;
      }

      public float getVoidDarknessRange() {
         return this.flatWorld ? 1.0F : 32.0F;
      }
   }
}
