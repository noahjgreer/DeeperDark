package net.minecraft.entity.boss.dragon;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.predicate.block.BlockPredicate;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkLevelType;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.EndConfiguredFeatures;
import net.minecraft.world.gen.feature.EndPortalFeature;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class EnderDragonFight {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int CHECK_DRAGON_SEEN_INTERVAL = 1200;
   private static final int CRYSTAL_COUNTING_INTERVAL = 100;
   public static final int field_31445 = 20;
   private static final int ISLAND_SIZE = 8;
   public static final int field_31441 = 9;
   private static final int PLAYER_COUNTING_INTERVAL = 20;
   private static final int field_31448 = 96;
   public static final int SPAWN_Y = 128;
   private final Predicate showBossBarPredicate;
   private final ServerBossBar bossBar;
   private final ServerWorld world;
   private final BlockPos origin;
   private final ObjectArrayList gateways;
   private final BlockPattern endPortalPattern;
   private int dragonSeenTimer;
   private int endCrystalsAlive;
   private int crystalCountTimer;
   private int playerUpdateTimer;
   private boolean dragonKilled;
   private boolean previouslyKilled;
   private boolean skipChunksLoadedCheck;
   @Nullable
   private UUID dragonUuid;
   private boolean doLegacyCheck;
   @Nullable
   private BlockPos exitPortalLocation;
   @Nullable
   private EnderDragonSpawnState dragonSpawnState;
   private int spawnStateTimer;
   @Nullable
   private List crystals;

   public EnderDragonFight(ServerWorld world, long gatewaysSeed, Data data) {
      this(world, gatewaysSeed, data, BlockPos.ORIGIN);
   }

   public EnderDragonFight(ServerWorld world, long gatewaysSeed, Data data, BlockPos origin) {
      this.bossBar = (ServerBossBar)(new ServerBossBar(Text.translatable("entity.minecraft.ender_dragon"), BossBar.Color.PINK, BossBar.Style.PROGRESS)).setDragonMusic(true).setThickenFog(true);
      this.gateways = new ObjectArrayList();
      this.playerUpdateTimer = 21;
      this.skipChunksLoadedCheck = false;
      this.doLegacyCheck = true;
      this.world = world;
      this.origin = origin;
      this.showBossBarPredicate = EntityPredicates.VALID_ENTITY.and(EntityPredicates.maxDistance((double)origin.getX(), (double)(128 + origin.getY()), (double)origin.getZ(), 192.0));
      this.doLegacyCheck = data.needsStateScanning;
      this.dragonUuid = (UUID)data.dragonUUID.orElse((Object)null);
      this.dragonKilled = data.dragonKilled;
      this.previouslyKilled = data.previouslyKilled;
      if (data.isRespawning) {
         this.dragonSpawnState = EnderDragonSpawnState.START;
      }

      this.exitPortalLocation = (BlockPos)data.exitPortalLocation.orElse((Object)null);
      this.gateways.addAll((Collection)data.gateways.orElseGet(() -> {
         ObjectArrayList objectArrayList = new ObjectArrayList(ContiguousSet.create(Range.closedOpen(0, 20), DiscreteDomain.integers()));
         Util.shuffle((List)objectArrayList, Random.create(gatewaysSeed));
         return objectArrayList;
      }));
      this.endPortalPattern = BlockPatternBuilder.start().aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").aisle("  ###  ", " #   # ", "#     #", "#  #  #", "#     #", " #   # ", "  ###  ").aisle("       ", "  ###  ", " ##### ", " ##### ", " ##### ", "  ###  ", "       ").where('#', CachedBlockPosition.matchesBlockState(BlockPredicate.make(Blocks.BEDROCK))).build();
   }

   /** @deprecated */
   @Deprecated
   @VisibleForTesting
   public void setSkipChunksLoadedCheck() {
      this.skipChunksLoadedCheck = true;
   }

   public Data toData() {
      return new Data(this.doLegacyCheck, this.dragonKilled, this.previouslyKilled, false, Optional.ofNullable(this.dragonUuid), Optional.ofNullable(this.exitPortalLocation), Optional.of(this.gateways));
   }

   public void tick() {
      this.bossBar.setVisible(!this.dragonKilled);
      if (++this.playerUpdateTimer >= 20) {
         this.updatePlayers();
         this.playerUpdateTimer = 0;
      }

      if (!this.bossBar.getPlayers().isEmpty()) {
         this.world.getChunkManager().addTicket(ChunkTicketType.DRAGON, new ChunkPos(0, 0), 9);
         boolean bl = this.areChunksLoaded();
         if (this.doLegacyCheck && bl) {
            this.convertFromLegacy();
            this.doLegacyCheck = false;
         }

         if (this.dragonSpawnState != null) {
            if (this.crystals == null && bl) {
               this.dragonSpawnState = null;
               this.respawnDragon();
            }

            this.dragonSpawnState.run(this.world, this, this.crystals, this.spawnStateTimer++, this.exitPortalLocation);
         }

         if (!this.dragonKilled) {
            if ((this.dragonUuid == null || ++this.dragonSeenTimer >= 1200) && bl) {
               this.checkDragonSeen();
               this.dragonSeenTimer = 0;
            }

            if (++this.crystalCountTimer >= 100 && bl) {
               this.countAliveCrystals();
               this.crystalCountTimer = 0;
            }
         }
      } else {
         this.world.getChunkManager().removeTicket(ChunkTicketType.DRAGON, new ChunkPos(0, 0), 9);
      }

   }

   private void convertFromLegacy() {
      LOGGER.info("Scanning for legacy world dragon fight...");
      boolean bl = this.worldContainsEndPortal();
      if (bl) {
         LOGGER.info("Found that the dragon has been killed in this world already.");
         this.previouslyKilled = true;
      } else {
         LOGGER.info("Found that the dragon has not yet been killed in this world.");
         this.previouslyKilled = false;
         if (this.findEndPortal() == null) {
            this.generateEndPortal(false);
         }
      }

      List list = this.world.getAliveEnderDragons();
      if (list.isEmpty()) {
         this.dragonKilled = true;
      } else {
         EnderDragonEntity enderDragonEntity = (EnderDragonEntity)list.get(0);
         this.dragonUuid = enderDragonEntity.getUuid();
         LOGGER.info("Found that there's a dragon still alive ({})", enderDragonEntity);
         this.dragonKilled = false;
         if (!bl) {
            LOGGER.info("But we didn't have a portal, let's remove it.");
            enderDragonEntity.discard();
            this.dragonUuid = null;
         }
      }

      if (!this.previouslyKilled && this.dragonKilled) {
         this.dragonKilled = false;
      }

   }

   private void checkDragonSeen() {
      List list = this.world.getAliveEnderDragons();
      if (list.isEmpty()) {
         LOGGER.debug("Haven't seen the dragon, respawning it");
         this.createDragon();
      } else {
         LOGGER.debug("Haven't seen our dragon, but found another one to use.");
         this.dragonUuid = ((EnderDragonEntity)list.get(0)).getUuid();
      }

   }

   protected void setSpawnState(EnderDragonSpawnState spawnState) {
      if (this.dragonSpawnState == null) {
         throw new IllegalStateException("Dragon respawn isn't in progress, can't skip ahead in the animation.");
      } else {
         this.spawnStateTimer = 0;
         if (spawnState == EnderDragonSpawnState.END) {
            this.dragonSpawnState = null;
            this.dragonKilled = false;
            EnderDragonEntity enderDragonEntity = this.createDragon();
            if (enderDragonEntity != null) {
               Iterator var3 = this.bossBar.getPlayers().iterator();

               while(var3.hasNext()) {
                  ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var3.next();
                  Criteria.SUMMONED_ENTITY.trigger(serverPlayerEntity, enderDragonEntity);
               }
            }
         } else {
            this.dragonSpawnState = spawnState;
         }

      }
   }

   private boolean worldContainsEndPortal() {
      for(int i = -8; i <= 8; ++i) {
         for(int j = -8; j <= 8; ++j) {
            WorldChunk worldChunk = this.world.getChunk(i, j);
            Iterator var4 = worldChunk.getBlockEntities().values().iterator();

            while(var4.hasNext()) {
               BlockEntity blockEntity = (BlockEntity)var4.next();
               if (blockEntity instanceof EndPortalBlockEntity) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   @Nullable
   private BlockPattern.Result findEndPortal() {
      ChunkPos chunkPos = new ChunkPos(this.origin);

      int j;
      for(int i = -8 + chunkPos.x; i <= 8 + chunkPos.x; ++i) {
         for(j = -8 + chunkPos.z; j <= 8 + chunkPos.z; ++j) {
            WorldChunk worldChunk = this.world.getChunk(i, j);
            Iterator var5 = worldChunk.getBlockEntities().values().iterator();

            while(var5.hasNext()) {
               BlockEntity blockEntity = (BlockEntity)var5.next();
               if (blockEntity instanceof EndPortalBlockEntity) {
                  BlockPattern.Result result = this.endPortalPattern.searchAround(this.world, blockEntity.getPos());
                  if (result != null) {
                     BlockPos blockPos = result.translate(3, 3, 3).getBlockPos();
                     if (this.exitPortalLocation == null) {
                        this.exitPortalLocation = blockPos;
                     }

                     return result;
                  }
               }
            }
         }
      }

      BlockPos blockPos2 = EndPortalFeature.offsetOrigin(this.origin);
      j = this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, blockPos2).getY();

      for(int k = j; k >= this.world.getBottomY(); --k) {
         BlockPattern.Result result2 = this.endPortalPattern.searchAround(this.world, new BlockPos(blockPos2.getX(), k, blockPos2.getZ()));
         if (result2 != null) {
            if (this.exitPortalLocation == null) {
               this.exitPortalLocation = result2.translate(3, 3, 3).getBlockPos();
            }

            return result2;
         }
      }

      return null;
   }

   private boolean areChunksLoaded() {
      if (this.skipChunksLoadedCheck) {
         return true;
      } else {
         ChunkPos chunkPos = new ChunkPos(this.origin);

         for(int i = -8 + chunkPos.x; i <= 8 + chunkPos.x; ++i) {
            for(int j = 8 + chunkPos.z; j <= 8 + chunkPos.z; ++j) {
               Chunk chunk = this.world.getChunk(i, j, ChunkStatus.FULL, false);
               if (!(chunk instanceof WorldChunk)) {
                  return false;
               }

               ChunkLevelType chunkLevelType = ((WorldChunk)chunk).getLevelType();
               if (!chunkLevelType.isAfter(ChunkLevelType.BLOCK_TICKING)) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   private void updatePlayers() {
      Set set = Sets.newHashSet();
      Iterator var2 = this.world.getPlayers(this.showBossBarPredicate).iterator();

      while(var2.hasNext()) {
         ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var2.next();
         this.bossBar.addPlayer(serverPlayerEntity);
         set.add(serverPlayerEntity);
      }

      Set set2 = Sets.newHashSet(this.bossBar.getPlayers());
      set2.removeAll(set);
      Iterator var6 = set2.iterator();

      while(var6.hasNext()) {
         ServerPlayerEntity serverPlayerEntity2 = (ServerPlayerEntity)var6.next();
         this.bossBar.removePlayer(serverPlayerEntity2);
      }

   }

   private void countAliveCrystals() {
      this.crystalCountTimer = 0;
      this.endCrystalsAlive = 0;

      EndSpikeFeature.Spike spike;
      for(Iterator var1 = EndSpikeFeature.getSpikes(this.world).iterator(); var1.hasNext(); this.endCrystalsAlive += this.world.getNonSpectatingEntities(EndCrystalEntity.class, spike.getBoundingBox()).size()) {
         spike = (EndSpikeFeature.Spike)var1.next();
      }

      LOGGER.debug("Found {} end crystals still alive", this.endCrystalsAlive);
   }

   public void dragonKilled(EnderDragonEntity dragon) {
      if (dragon.getUuid().equals(this.dragonUuid)) {
         this.bossBar.setPercent(0.0F);
         this.bossBar.setVisible(false);
         this.generateEndPortal(true);
         this.generateNewEndGateway();
         if (!this.previouslyKilled) {
            this.world.setBlockState(this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, EndPortalFeature.offsetOrigin(this.origin)), Blocks.DRAGON_EGG.getDefaultState());
         }

         this.previouslyKilled = true;
         this.dragonKilled = true;
      }

   }

   /** @deprecated */
   @Deprecated
   @VisibleForTesting
   public void clearGatewaysList() {
      this.gateways.clear();
   }

   private void generateNewEndGateway() {
      if (!this.gateways.isEmpty()) {
         int i = (Integer)this.gateways.remove(this.gateways.size() - 1);
         int j = MathHelper.floor(96.0 * Math.cos(2.0 * (-3.141592653589793 + 0.15707963267948966 * (double)i)));
         int k = MathHelper.floor(96.0 * Math.sin(2.0 * (-3.141592653589793 + 0.15707963267948966 * (double)i)));
         this.generateEndGateway(new BlockPos(j, 75, k));
      }
   }

   private void generateEndGateway(BlockPos pos) {
      this.world.syncWorldEvent(3000, pos, 0);
      this.world.getRegistryManager().getOptional(RegistryKeys.CONFIGURED_FEATURE).flatMap((registry) -> {
         return registry.getOptional(EndConfiguredFeatures.END_GATEWAY_DELAYED);
      }).ifPresent((reference) -> {
         ((ConfiguredFeature)reference.value()).generate(this.world, this.world.getChunkManager().getChunkGenerator(), Random.create(), pos);
      });
   }

   private void generateEndPortal(boolean previouslyKilled) {
      EndPortalFeature endPortalFeature = new EndPortalFeature(previouslyKilled);
      if (this.exitPortalLocation == null) {
         for(this.exitPortalLocation = this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPortalFeature.offsetOrigin(this.origin)).down(); this.world.getBlockState(this.exitPortalLocation).isOf(Blocks.BEDROCK) && this.exitPortalLocation.getY() > 63; this.exitPortalLocation = this.exitPortalLocation.down()) {
         }

         this.exitPortalLocation = this.exitPortalLocation.withY(Math.max(this.world.getBottomY() + 1, this.exitPortalLocation.getY()));
      }

      if (endPortalFeature.generateIfValid(FeatureConfig.DEFAULT, this.world, this.world.getChunkManager().getChunkGenerator(), Random.create(), this.exitPortalLocation)) {
         int i = MathHelper.ceilDiv(4, 16);
         this.world.getChunkManager().chunkLoadingManager.forceLighting(new ChunkPos(this.exitPortalLocation), i);
      }

   }

   @Nullable
   private EnderDragonEntity createDragon() {
      this.world.getWorldChunk(new BlockPos(this.origin.getX(), 128 + this.origin.getY(), this.origin.getZ()));
      EnderDragonEntity enderDragonEntity = (EnderDragonEntity)EntityType.ENDER_DRAGON.create(this.world, SpawnReason.EVENT);
      if (enderDragonEntity != null) {
         enderDragonEntity.setFight(this);
         enderDragonEntity.setFightOrigin(this.origin);
         enderDragonEntity.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
         enderDragonEntity.refreshPositionAndAngles((double)this.origin.getX(), (double)(128 + this.origin.getY()), (double)this.origin.getZ(), this.world.random.nextFloat() * 360.0F, 0.0F);
         this.world.spawnEntity(enderDragonEntity);
         this.dragonUuid = enderDragonEntity.getUuid();
      }

      return enderDragonEntity;
   }

   public void updateFight(EnderDragonEntity dragon) {
      if (dragon.getUuid().equals(this.dragonUuid)) {
         this.bossBar.setPercent(dragon.getHealth() / dragon.getMaxHealth());
         this.dragonSeenTimer = 0;
         if (dragon.hasCustomName()) {
            this.bossBar.setName(dragon.getDisplayName());
         }
      }

   }

   public int getAliveEndCrystals() {
      return this.endCrystalsAlive;
   }

   public void crystalDestroyed(EndCrystalEntity enderCrystal, DamageSource source) {
      if (this.dragonSpawnState != null && this.crystals.contains(enderCrystal)) {
         LOGGER.debug("Aborting respawn sequence");
         this.dragonSpawnState = null;
         this.spawnStateTimer = 0;
         this.resetEndCrystals();
         this.generateEndPortal(true);
      } else {
         this.countAliveCrystals();
         Entity entity = this.world.getEntity(this.dragonUuid);
         if (entity instanceof EnderDragonEntity) {
            EnderDragonEntity enderDragonEntity = (EnderDragonEntity)entity;
            enderDragonEntity.crystalDestroyed(this.world, enderCrystal, enderCrystal.getBlockPos(), source);
         }
      }

   }

   public boolean hasPreviouslyKilled() {
      return this.previouslyKilled;
   }

   public void respawnDragon() {
      if (this.dragonKilled && this.dragonSpawnState == null) {
         BlockPos blockPos = this.exitPortalLocation;
         if (blockPos == null) {
            LOGGER.debug("Tried to respawn, but need to find the portal first.");
            BlockPattern.Result result = this.findEndPortal();
            if (result == null) {
               LOGGER.debug("Couldn't find a portal, so we made one.");
               this.generateEndPortal(true);
            } else {
               LOGGER.debug("Found the exit portal & saved its location for next time.");
            }

            blockPos = this.exitPortalLocation;
         }

         List list = Lists.newArrayList();
         BlockPos blockPos2 = blockPos.up(1);
         Iterator var4 = Direction.Type.HORIZONTAL.iterator();

         while(var4.hasNext()) {
            Direction direction = (Direction)var4.next();
            List list2 = this.world.getNonSpectatingEntities(EndCrystalEntity.class, new Box(blockPos2.offset((Direction)direction, 2)));
            if (list2.isEmpty()) {
               return;
            }

            list.addAll(list2);
         }

         LOGGER.debug("Found all crystals, respawning dragon.");
         this.respawnDragon(list);
      }

   }

   private void respawnDragon(List crystals) {
      if (this.dragonKilled && this.dragonSpawnState == null) {
         for(BlockPattern.Result result = this.findEndPortal(); result != null; result = this.findEndPortal()) {
            for(int i = 0; i < this.endPortalPattern.getWidth(); ++i) {
               for(int j = 0; j < this.endPortalPattern.getHeight(); ++j) {
                  for(int k = 0; k < this.endPortalPattern.getDepth(); ++k) {
                     CachedBlockPosition cachedBlockPosition = result.translate(i, j, k);
                     if (cachedBlockPosition.getBlockState().isOf(Blocks.BEDROCK) || cachedBlockPosition.getBlockState().isOf(Blocks.END_PORTAL)) {
                        this.world.setBlockState(cachedBlockPosition.getBlockPos(), Blocks.END_STONE.getDefaultState());
                     }
                  }
               }
            }
         }

         this.dragonSpawnState = EnderDragonSpawnState.START;
         this.spawnStateTimer = 0;
         this.generateEndPortal(false);
         this.crystals = crystals;
      }

   }

   public void resetEndCrystals() {
      Iterator var1 = EndSpikeFeature.getSpikes(this.world).iterator();

      while(var1.hasNext()) {
         EndSpikeFeature.Spike spike = (EndSpikeFeature.Spike)var1.next();
         List list = this.world.getNonSpectatingEntities(EndCrystalEntity.class, spike.getBoundingBox());
         Iterator var4 = list.iterator();

         while(var4.hasNext()) {
            EndCrystalEntity endCrystalEntity = (EndCrystalEntity)var4.next();
            endCrystalEntity.setInvulnerable(false);
            endCrystalEntity.setBeamTarget((BlockPos)null);
         }
      }

   }

   @Nullable
   public UUID getDragonUuid() {
      return this.dragonUuid;
   }

   public static record Data(boolean needsStateScanning, boolean dragonKilled, boolean previouslyKilled, boolean isRespawning, Optional dragonUUID, Optional exitPortalLocation, Optional gateways) {
      final boolean needsStateScanning;
      final boolean dragonKilled;
      final boolean previouslyKilled;
      final boolean isRespawning;
      final Optional dragonUUID;
      final Optional exitPortalLocation;
      final Optional gateways;
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Codec.BOOL.fieldOf("NeedsStateScanning").orElse(true).forGetter(Data::needsStateScanning), Codec.BOOL.fieldOf("DragonKilled").orElse(false).forGetter(Data::dragonKilled), Codec.BOOL.fieldOf("PreviouslyKilled").orElse(false).forGetter(Data::previouslyKilled), Codec.BOOL.lenientOptionalFieldOf("IsRespawning", false).forGetter(Data::isRespawning), Uuids.INT_STREAM_CODEC.lenientOptionalFieldOf("Dragon").forGetter(Data::dragonUUID), BlockPos.CODEC.lenientOptionalFieldOf("ExitPortalLocation").forGetter(Data::exitPortalLocation), Codec.list(Codec.INT).lenientOptionalFieldOf("Gateways").forGetter(Data::gateways)).apply(instance, Data::new);
      });
      public static final Data DEFAULT = new Data(true, false, false, false, Optional.empty(), Optional.empty(), Optional.empty());

      public Data(boolean bl, boolean bl2, boolean bl3, boolean bl4, Optional optional, Optional optional2, Optional optional3) {
         this.needsStateScanning = bl;
         this.dragonKilled = bl2;
         this.previouslyKilled = bl3;
         this.isRespawning = bl4;
         this.dragonUUID = optional;
         this.exitPortalLocation = optional2;
         this.gateways = optional3;
      }

      public boolean needsStateScanning() {
         return this.needsStateScanning;
      }

      public boolean dragonKilled() {
         return this.dragonKilled;
      }

      public boolean previouslyKilled() {
         return this.previouslyKilled;
      }

      public boolean isRespawning() {
         return this.isRespawning;
      }

      public Optional dragonUUID() {
         return this.dragonUUID;
      }

      public Optional exitPortalLocation() {
         return this.exitPortalLocation;
      }

      public Optional gateways() {
         return this.gateways;
      }
   }
}
