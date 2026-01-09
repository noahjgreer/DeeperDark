package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.SynchronizeTagsS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkLoadDistanceS2CPacket;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.SimulationDistanceS2CPacket;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderCenterChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderInitializeS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderInterpolateSizeS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderSizeChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderWarningBlocksChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderWarningTimeChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.network.state.PlayStateFactories;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagPacketSerializer;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stats;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.ReadView;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Formatting;
import net.minecraft.util.UserCache;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.path.PathUtil;
import net.minecraft.world.GameRules;
import net.minecraft.world.PlayerSaveHandler;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.border.WorldBorderListener;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public abstract class PlayerManager {
   public static final File BANNED_PLAYERS_FILE = new File("banned-players.json");
   public static final File BANNED_IPS_FILE = new File("banned-ips.json");
   public static final File OPERATORS_FILE = new File("ops.json");
   public static final File WHITELIST_FILE = new File("whitelist.json");
   public static final Text FILTERED_FULL_TEXT = Text.translatable("chat.filtered_full");
   public static final Text DUPLICATE_LOGIN_TEXT = Text.translatable("multiplayer.disconnect.duplicate_login");
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int LATENCY_UPDATE_INTERVAL = 600;
   private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
   private final MinecraftServer server;
   private final List players = Lists.newArrayList();
   private final Map playerMap = Maps.newHashMap();
   private final BannedPlayerList bannedProfiles;
   private final BannedIpList bannedIps;
   private final OperatorList ops;
   private final Whitelist whitelist;
   private final Map statisticsMap;
   private final Map advancementTrackers;
   private final PlayerSaveHandler saveHandler;
   private boolean whitelistEnabled;
   private final CombinedDynamicRegistries registryManager;
   protected final int maxPlayers;
   private int viewDistance;
   private int simulationDistance;
   private boolean cheatsAllowed;
   private static final boolean field_29791 = false;
   private int latencyUpdateTimer;

   public PlayerManager(MinecraftServer server, CombinedDynamicRegistries registryManager, PlayerSaveHandler saveHandler, int maxPlayers) {
      this.bannedProfiles = new BannedPlayerList(BANNED_PLAYERS_FILE);
      this.bannedIps = new BannedIpList(BANNED_IPS_FILE);
      this.ops = new OperatorList(OPERATORS_FILE);
      this.whitelist = new Whitelist(WHITELIST_FILE);
      this.statisticsMap = Maps.newHashMap();
      this.advancementTrackers = Maps.newHashMap();
      this.server = server;
      this.registryManager = registryManager;
      this.maxPlayers = maxPlayers;
      this.saveHandler = saveHandler;
   }

   public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData) {
      GameProfile gameProfile = player.getGameProfile();
      UserCache userCache = this.server.getUserCache();
      String string;
      if (userCache != null) {
         Optional optional = userCache.getByUuid(gameProfile.getId());
         string = (String)optional.map(GameProfile::getName).orElse(gameProfile.getName());
         userCache.add(gameProfile);
      } else {
         string = gameProfile.getName();
      }

      ErrorReporter.Logging logging = new ErrorReporter.Logging(player.getErrorReporterContext(), LOGGER);

      try {
         Optional optional2 = this.loadPlayerData(player, logging);
         RegistryKey registryKey = (RegistryKey)optional2.flatMap((view) -> {
            return view.read("Dimension", World.CODEC);
         }).orElse(World.OVERWORLD);
         ServerWorld serverWorld = this.server.getWorld(registryKey);
         ServerWorld serverWorld2;
         if (serverWorld == null) {
            LOGGER.warn("Unknown respawn dimension {}, defaulting to overworld", registryKey);
            serverWorld2 = this.server.getOverworld();
         } else {
            serverWorld2 = serverWorld;
         }

         player.setServerWorld(serverWorld2);
         if (optional2.isEmpty()) {
            player.refreshPositionAndAngles(player.getWorldSpawnPos(serverWorld2, serverWorld2.getSpawnPos()).toBottomCenterPos(), serverWorld2.getSpawnAngle(), 0.0F);
         }

         serverWorld2.method_72079(player.getChunkPos(), 1);
         String string2 = connection.getAddressAsString(this.server.shouldLogIps());
         LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", new Object[]{player.getName().getString(), string2, player.getId(), player.getX(), player.getY(), player.getZ()});
         WorldProperties worldProperties = serverWorld2.getLevelProperties();
         player.readGameModeData((ReadView)optional2.orElse((Object)null));
         ServerPlayNetworkHandler serverPlayNetworkHandler = new ServerPlayNetworkHandler(this.server, connection, player, clientData);
         connection.transitionInbound(PlayStateFactories.C2S.bind(RegistryByteBuf.makeFactory(this.server.getRegistryManager()), serverPlayNetworkHandler), serverPlayNetworkHandler);
         GameRules gameRules = serverWorld2.getGameRules();
         boolean bl = gameRules.getBoolean(GameRules.DO_IMMEDIATE_RESPAWN);
         boolean bl2 = gameRules.getBoolean(GameRules.REDUCED_DEBUG_INFO);
         boolean bl3 = gameRules.getBoolean(GameRules.DO_LIMITED_CRAFTING);
         serverPlayNetworkHandler.sendPacket(new GameJoinS2CPacket(player.getId(), worldProperties.isHardcore(), this.server.getWorldRegistryKeys(), this.getMaxPlayerCount(), this.viewDistance, this.simulationDistance, bl2, !bl, bl3, player.createCommonPlayerSpawnInfo(serverWorld2), this.server.shouldEnforceSecureProfile()));
         serverPlayNetworkHandler.sendPacket(new DifficultyS2CPacket(worldProperties.getDifficulty(), worldProperties.isDifficultyLocked()));
         serverPlayNetworkHandler.sendPacket(new PlayerAbilitiesS2CPacket(player.getAbilities()));
         serverPlayNetworkHandler.sendPacket(new UpdateSelectedSlotS2CPacket(player.getInventory().getSelectedSlot()));
         ServerRecipeManager serverRecipeManager = this.server.getRecipeManager();
         serverPlayNetworkHandler.sendPacket(new SynchronizeRecipesS2CPacket(serverRecipeManager.getPropertySets(), serverRecipeManager.getStonecutterRecipeForSync()));
         this.sendCommandTree(player);
         player.getStatHandler().updateStatSet();
         player.getRecipeBook().sendInitRecipesPacket(player);
         this.sendScoreboard(serverWorld2.getScoreboard(), player);
         this.server.forcePlayerSampleUpdate();
         MutableText mutableText;
         if (player.getGameProfile().getName().equalsIgnoreCase(string)) {
            mutableText = Text.translatable("multiplayer.player.joined", player.getDisplayName());
         } else {
            mutableText = Text.translatable("multiplayer.player.joined.renamed", player.getDisplayName(), string);
         }

         this.broadcast(mutableText.formatted(Formatting.YELLOW), false);
         serverPlayNetworkHandler.requestTeleport(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
         ServerMetadata serverMetadata = this.server.getServerMetadata();
         if (serverMetadata != null && !clientData.transferred()) {
            player.sendServerMetadata(serverMetadata);
         }

         player.networkHandler.sendPacket(PlayerListS2CPacket.entryFromPlayer(this.players));
         this.players.add(player);
         this.playerMap.put(player.getUuid(), player);
         this.sendToAll(PlayerListS2CPacket.entryFromPlayer(List.of(player)));
         this.sendWorldInfo(player, serverWorld2);
         serverWorld2.onPlayerConnected(player);
         this.server.getBossBarManager().onPlayerConnect(player);
         this.sendStatusEffects(player);
         optional2.ifPresent((view) -> {
            player.readEnderPearls(view);
            player.readRootVehicle(view);
         });
         player.onSpawn();
      } catch (Throwable var23) {
         try {
            logging.close();
         } catch (Throwable var22) {
            var23.addSuppressed(var22);
         }

         throw var23;
      }

      logging.close();
   }

   protected void sendScoreboard(ServerScoreboard scoreboard, ServerPlayerEntity player) {
      Set set = Sets.newHashSet();
      Iterator var4 = scoreboard.getTeams().iterator();

      while(var4.hasNext()) {
         Team team = (Team)var4.next();
         player.networkHandler.sendPacket(TeamS2CPacket.updateTeam(team, true));
      }

      ScoreboardDisplaySlot[] var12 = ScoreboardDisplaySlot.values();
      int var13 = var12.length;

      for(int var6 = 0; var6 < var13; ++var6) {
         ScoreboardDisplaySlot scoreboardDisplaySlot = var12[var6];
         ScoreboardObjective scoreboardObjective = scoreboard.getObjectiveForSlot(scoreboardDisplaySlot);
         if (scoreboardObjective != null && !set.contains(scoreboardObjective)) {
            List list = scoreboard.createChangePackets(scoreboardObjective);
            Iterator var10 = list.iterator();

            while(var10.hasNext()) {
               Packet packet = (Packet)var10.next();
               player.networkHandler.sendPacket(packet);
            }

            set.add(scoreboardObjective);
         }
      }

   }

   public void setMainWorld(ServerWorld world) {
      world.getWorldBorder().addListener(new WorldBorderListener() {
         public void onSizeChange(WorldBorder border, double size) {
            PlayerManager.this.sendToAll(new WorldBorderSizeChangedS2CPacket(border));
         }

         public void onInterpolateSize(WorldBorder border, double fromSize, double toSize, long time) {
            PlayerManager.this.sendToAll(new WorldBorderInterpolateSizeS2CPacket(border));
         }

         public void onCenterChanged(WorldBorder border, double centerX, double centerZ) {
            PlayerManager.this.sendToAll(new WorldBorderCenterChangedS2CPacket(border));
         }

         public void onWarningTimeChanged(WorldBorder border, int warningTime) {
            PlayerManager.this.sendToAll(new WorldBorderWarningTimeChangedS2CPacket(border));
         }

         public void onWarningBlocksChanged(WorldBorder border, int warningBlockDistance) {
            PlayerManager.this.sendToAll(new WorldBorderWarningBlocksChangedS2CPacket(border));
         }

         public void onDamagePerBlockChanged(WorldBorder border, double damagePerBlock) {
         }

         public void onSafeZoneChanged(WorldBorder border, double safeZoneRadius) {
         }
      });
   }

   public Optional loadPlayerData(ServerPlayerEntity player, ErrorReporter errorReporter) {
      NbtCompound nbtCompound = this.server.getSaveProperties().getPlayerData();
      Optional optional;
      if (this.server.isHost(player.getGameProfile()) && nbtCompound != null) {
         ReadView readView = NbtReadView.create(errorReporter, player.getRegistryManager(), nbtCompound);
         optional = Optional.of(readView);
         player.readData(readView);
         LOGGER.debug("loading single player");
      } else {
         optional = this.saveHandler.loadPlayerData(player, (ErrorReporter)errorReporter);
      }

      return optional;
   }

   protected void savePlayerData(ServerPlayerEntity player) {
      this.saveHandler.savePlayerData(player);
      ServerStatHandler serverStatHandler = (ServerStatHandler)this.statisticsMap.get(player.getUuid());
      if (serverStatHandler != null) {
         serverStatHandler.save();
      }

      PlayerAdvancementTracker playerAdvancementTracker = (PlayerAdvancementTracker)this.advancementTrackers.get(player.getUuid());
      if (playerAdvancementTracker != null) {
         playerAdvancementTracker.save();
      }

   }

   public void remove(ServerPlayerEntity player) {
      ServerWorld serverWorld = player.getWorld();
      player.incrementStat(Stats.LEAVE_GAME);
      this.savePlayerData(player);
      if (player.hasVehicle()) {
         Entity entity = player.getRootVehicle();
         if (entity.hasPlayerRider()) {
            LOGGER.debug("Removing player mount");
            player.stopRiding();
            entity.streamPassengersAndSelf().forEach((entityx) -> {
               entityx.setRemoved(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
            });
         }
      }

      player.detach();
      Iterator var5 = player.getEnderPearls().iterator();

      while(var5.hasNext()) {
         EnderPearlEntity enderPearlEntity = (EnderPearlEntity)var5.next();
         enderPearlEntity.setRemoved(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
      }

      serverWorld.removePlayer(player, Entity.RemovalReason.UNLOADED_WITH_PLAYER);
      player.getAdvancementTracker().clearCriteria();
      this.players.remove(player);
      this.server.getBossBarManager().onPlayerDisconnect(player);
      UUID uUID = player.getUuid();
      ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.playerMap.get(uUID);
      if (serverPlayerEntity == player) {
         this.playerMap.remove(uUID);
         this.statisticsMap.remove(uUID);
         this.advancementTrackers.remove(uUID);
      }

      this.sendToAll(new PlayerRemoveS2CPacket(List.of(player.getUuid())));
   }

   @Nullable
   public Text checkCanJoin(SocketAddress address, GameProfile profile) {
      MutableText mutableText;
      if (this.bannedProfiles.contains(profile)) {
         BannedPlayerEntry bannedPlayerEntry = (BannedPlayerEntry)this.bannedProfiles.get(profile);
         mutableText = Text.translatable("multiplayer.disconnect.banned.reason", bannedPlayerEntry.getReason());
         if (bannedPlayerEntry.getExpiryDate() != null) {
            mutableText.append((Text)Text.translatable("multiplayer.disconnect.banned.expiration", DATE_FORMATTER.format(bannedPlayerEntry.getExpiryDate())));
         }

         return mutableText;
      } else if (!this.isWhitelisted(profile)) {
         return Text.translatable("multiplayer.disconnect.not_whitelisted");
      } else if (this.bannedIps.isBanned(address)) {
         BannedIpEntry bannedIpEntry = this.bannedIps.get(address);
         mutableText = Text.translatable("multiplayer.disconnect.banned_ip.reason", bannedIpEntry.getReason());
         if (bannedIpEntry.getExpiryDate() != null) {
            mutableText.append((Text)Text.translatable("multiplayer.disconnect.banned_ip.expiration", DATE_FORMATTER.format(bannedIpEntry.getExpiryDate())));
         }

         return mutableText;
      } else {
         return this.players.size() >= this.maxPlayers && !this.canBypassPlayerLimit(profile) ? Text.translatable("multiplayer.disconnect.server_full") : null;
      }
   }

   public boolean disconnectDuplicateLogins(GameProfile profile) {
      UUID uUID = profile.getId();
      Set set = Sets.newIdentityHashSet();
      Iterator var4 = this.players.iterator();

      while(var4.hasNext()) {
         ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var4.next();
         if (serverPlayerEntity.getUuid().equals(uUID)) {
            set.add(serverPlayerEntity);
         }
      }

      ServerPlayerEntity serverPlayerEntity2 = (ServerPlayerEntity)this.playerMap.get(profile.getId());
      if (serverPlayerEntity2 != null) {
         set.add(serverPlayerEntity2);
      }

      Iterator var8 = set.iterator();

      while(var8.hasNext()) {
         ServerPlayerEntity serverPlayerEntity3 = (ServerPlayerEntity)var8.next();
         serverPlayerEntity3.networkHandler.disconnect(DUPLICATE_LOGIN_TEXT);
      }

      return !set.isEmpty();
   }

   public ServerPlayerEntity respawnPlayer(ServerPlayerEntity player, boolean alive, Entity.RemovalReason removalReason) {
      this.players.remove(player);
      player.getWorld().removePlayer(player, removalReason);
      TeleportTarget teleportTarget = player.getRespawnTarget(!alive, TeleportTarget.NO_OP);
      ServerWorld serverWorld = teleportTarget.world();
      ServerPlayerEntity serverPlayerEntity = new ServerPlayerEntity(this.server, serverWorld, player.getGameProfile(), player.getClientOptions());
      serverPlayerEntity.networkHandler = player.networkHandler;
      serverPlayerEntity.copyFrom(player, alive);
      serverPlayerEntity.setId(player.getId());
      serverPlayerEntity.setMainArm(player.getMainArm());
      if (!teleportTarget.missingRespawnBlock()) {
         serverPlayerEntity.setSpawnPointFrom(player);
      }

      Iterator var7 = player.getCommandTags().iterator();

      while(var7.hasNext()) {
         String string = (String)var7.next();
         serverPlayerEntity.addCommandTag(string);
      }

      Vec3d vec3d = teleportTarget.position();
      serverPlayerEntity.refreshPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, teleportTarget.yaw(), teleportTarget.pitch());
      if (teleportTarget.missingRespawnBlock()) {
         serverPlayerEntity.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.NO_RESPAWN_BLOCK, 0.0F));
      }

      byte b = alive ? 1 : 0;
      ServerWorld serverWorld2 = serverPlayerEntity.getWorld();
      WorldProperties worldProperties = serverWorld2.getLevelProperties();
      serverPlayerEntity.networkHandler.sendPacket(new PlayerRespawnS2CPacket(serverPlayerEntity.createCommonPlayerSpawnInfo(serverWorld2), (byte)b));
      serverPlayerEntity.networkHandler.requestTeleport(serverPlayerEntity.getX(), serverPlayerEntity.getY(), serverPlayerEntity.getZ(), serverPlayerEntity.getYaw(), serverPlayerEntity.getPitch());
      serverPlayerEntity.networkHandler.sendPacket(new PlayerSpawnPositionS2CPacket(serverWorld.getSpawnPos(), serverWorld.getSpawnAngle()));
      serverPlayerEntity.networkHandler.sendPacket(new DifficultyS2CPacket(worldProperties.getDifficulty(), worldProperties.isDifficultyLocked()));
      serverPlayerEntity.networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(serverPlayerEntity.experienceProgress, serverPlayerEntity.totalExperience, serverPlayerEntity.experienceLevel));
      this.sendStatusEffects(serverPlayerEntity);
      this.sendWorldInfo(serverPlayerEntity, serverWorld);
      this.sendCommandTree(serverPlayerEntity);
      serverWorld.onPlayerRespawned(serverPlayerEntity);
      this.players.add(serverPlayerEntity);
      this.playerMap.put(serverPlayerEntity.getUuid(), serverPlayerEntity);
      serverPlayerEntity.onSpawn();
      serverPlayerEntity.setHealth(serverPlayerEntity.getHealth());
      ServerPlayerEntity.Respawn respawn = serverPlayerEntity.getRespawn();
      if (!alive && respawn != null) {
         ServerWorld serverWorld3 = this.server.getWorld(respawn.dimension());
         if (serverWorld3 != null) {
            BlockPos blockPos = respawn.pos();
            BlockState blockState = serverWorld3.getBlockState(blockPos);
            if (blockState.isOf(Blocks.RESPAWN_ANCHOR)) {
               serverPlayerEntity.networkHandler.sendPacket(new PlaySoundS2CPacket(SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE, SoundCategory.BLOCKS, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), 1.0F, 1.0F, serverWorld.getRandom().nextLong()));
            }
         }
      }

      return serverPlayerEntity;
   }

   public void sendStatusEffects(ServerPlayerEntity player) {
      this.sendStatusEffects(player, player.networkHandler);
   }

   public void sendStatusEffects(LivingEntity entity, ServerPlayNetworkHandler networkHandler) {
      Iterator var3 = entity.getStatusEffects().iterator();

      while(var3.hasNext()) {
         StatusEffectInstance statusEffectInstance = (StatusEffectInstance)var3.next();
         networkHandler.sendPacket(new EntityStatusEffectS2CPacket(entity.getId(), statusEffectInstance, false));
      }

   }

   public void sendCommandTree(ServerPlayerEntity player) {
      GameProfile gameProfile = player.getGameProfile();
      int i = this.server.getPermissionLevel(gameProfile);
      this.sendCommandTree(player, i);
   }

   public void updatePlayerLatency() {
      if (++this.latencyUpdateTimer > 600) {
         this.sendToAll(new PlayerListS2CPacket(EnumSet.of(PlayerListS2CPacket.Action.UPDATE_LATENCY), this.players));
         this.latencyUpdateTimer = 0;
      }

   }

   public void sendToAll(Packet packet) {
      Iterator var2 = this.players.iterator();

      while(var2.hasNext()) {
         ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var2.next();
         serverPlayerEntity.networkHandler.sendPacket(packet);
      }

   }

   public void sendToDimension(Packet packet, RegistryKey dimension) {
      Iterator var3 = this.players.iterator();

      while(var3.hasNext()) {
         ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var3.next();
         if (serverPlayerEntity.getWorld().getRegistryKey() == dimension) {
            serverPlayerEntity.networkHandler.sendPacket(packet);
         }
      }

   }

   public void sendToTeam(PlayerEntity source, Text message) {
      AbstractTeam abstractTeam = source.getScoreboardTeam();
      if (abstractTeam != null) {
         Collection collection = abstractTeam.getPlayerList();
         Iterator var5 = collection.iterator();

         while(var5.hasNext()) {
            String string = (String)var5.next();
            ServerPlayerEntity serverPlayerEntity = this.getPlayer(string);
            if (serverPlayerEntity != null && serverPlayerEntity != source) {
               serverPlayerEntity.sendMessage(message);
            }
         }

      }
   }

   public void sendToOtherTeams(PlayerEntity source, Text message) {
      AbstractTeam abstractTeam = source.getScoreboardTeam();
      if (abstractTeam == null) {
         this.broadcast(message, false);
      } else {
         for(int i = 0; i < this.players.size(); ++i) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.players.get(i);
            if (serverPlayerEntity.getScoreboardTeam() != abstractTeam) {
               serverPlayerEntity.sendMessage(message);
            }
         }

      }
   }

   public String[] getPlayerNames() {
      String[] strings = new String[this.players.size()];

      for(int i = 0; i < this.players.size(); ++i) {
         strings[i] = ((ServerPlayerEntity)this.players.get(i)).getGameProfile().getName();
      }

      return strings;
   }

   public BannedPlayerList getUserBanList() {
      return this.bannedProfiles;
   }

   public BannedIpList getIpBanList() {
      return this.bannedIps;
   }

   public void addToOperators(GameProfile profile) {
      this.ops.add(new OperatorEntry(profile, this.server.getOpPermissionLevel(), this.ops.canBypassPlayerLimit(profile)));
      ServerPlayerEntity serverPlayerEntity = this.getPlayer(profile.getId());
      if (serverPlayerEntity != null) {
         this.sendCommandTree(serverPlayerEntity);
      }

   }

   public void removeFromOperators(GameProfile profile) {
      this.ops.remove(profile);
      ServerPlayerEntity serverPlayerEntity = this.getPlayer(profile.getId());
      if (serverPlayerEntity != null) {
         this.sendCommandTree(serverPlayerEntity);
      }

   }

   private void sendCommandTree(ServerPlayerEntity player, int permissionLevel) {
      if (player.networkHandler != null) {
         byte b;
         if (permissionLevel <= 0) {
            b = 24;
         } else if (permissionLevel >= 4) {
            b = 28;
         } else {
            b = (byte)(24 + permissionLevel);
         }

         player.networkHandler.sendPacket(new EntityStatusS2CPacket(player, b));
      }

      this.server.getCommandManager().sendCommandTree(player);
   }

   public boolean isWhitelisted(GameProfile profile) {
      return !this.whitelistEnabled || this.ops.contains(profile) || this.whitelist.contains(profile);
   }

   public boolean isOperator(GameProfile profile) {
      return this.ops.contains(profile) || this.server.isHost(profile) && this.server.getSaveProperties().areCommandsAllowed() || this.cheatsAllowed;
   }

   @Nullable
   public ServerPlayerEntity getPlayer(String name) {
      int i = this.players.size();

      for(int j = 0; j < i; ++j) {
         ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.players.get(j);
         if (serverPlayerEntity.getGameProfile().getName().equalsIgnoreCase(name)) {
            return serverPlayerEntity;
         }
      }

      return null;
   }

   public void sendToAround(@Nullable PlayerEntity player, double x, double y, double z, double distance, RegistryKey worldKey, Packet packet) {
      for(int i = 0; i < this.players.size(); ++i) {
         ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.players.get(i);
         if (serverPlayerEntity != player && serverPlayerEntity.getWorld().getRegistryKey() == worldKey) {
            double d = x - serverPlayerEntity.getX();
            double e = y - serverPlayerEntity.getY();
            double f = z - serverPlayerEntity.getZ();
            if (d * d + e * e + f * f < distance * distance) {
               serverPlayerEntity.networkHandler.sendPacket(packet);
            }
         }
      }

   }

   public void saveAllPlayerData() {
      for(int i = 0; i < this.players.size(); ++i) {
         this.savePlayerData((ServerPlayerEntity)this.players.get(i));
      }

   }

   public Whitelist getWhitelist() {
      return this.whitelist;
   }

   public String[] getWhitelistedNames() {
      return this.whitelist.getNames();
   }

   public OperatorList getOpList() {
      return this.ops;
   }

   public String[] getOpNames() {
      return this.ops.getNames();
   }

   public void reloadWhitelist() {
   }

   public void sendWorldInfo(ServerPlayerEntity player, ServerWorld world) {
      WorldBorder worldBorder = this.server.getOverworld().getWorldBorder();
      player.networkHandler.sendPacket(new WorldBorderInitializeS2CPacket(worldBorder));
      player.networkHandler.sendPacket(new WorldTimeUpdateS2CPacket(world.getTime(), world.getTimeOfDay(), world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)));
      player.networkHandler.sendPacket(new PlayerSpawnPositionS2CPacket(world.getSpawnPos(), world.getSpawnAngle()));
      if (world.isRaining()) {
         player.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_STARTED, 0.0F));
         player.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED, world.getRainGradient(1.0F)));
         player.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.THUNDER_GRADIENT_CHANGED, world.getThunderGradient(1.0F)));
      }

      player.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.INITIAL_CHUNKS_COMING, 0.0F));
      this.server.getTickManager().sendPackets(player);
   }

   public void sendPlayerStatus(ServerPlayerEntity player) {
      player.playerScreenHandler.syncState();
      player.markHealthDirty();
      player.networkHandler.sendPacket(new UpdateSelectedSlotS2CPacket(player.getInventory().getSelectedSlot()));
   }

   public int getCurrentPlayerCount() {
      return this.players.size();
   }

   public int getMaxPlayerCount() {
      return this.maxPlayers;
   }

   public boolean isWhitelistEnabled() {
      return this.whitelistEnabled;
   }

   public void setWhitelistEnabled(boolean whitelistEnabled) {
      this.whitelistEnabled = whitelistEnabled;
   }

   public List getPlayersByIp(String ip) {
      List list = Lists.newArrayList();
      Iterator var3 = this.players.iterator();

      while(var3.hasNext()) {
         ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var3.next();
         if (serverPlayerEntity.getIp().equals(ip)) {
            list.add(serverPlayerEntity);
         }
      }

      return list;
   }

   public int getViewDistance() {
      return this.viewDistance;
   }

   public int getSimulationDistance() {
      return this.simulationDistance;
   }

   public MinecraftServer getServer() {
      return this.server;
   }

   @Nullable
   public NbtCompound getUserData() {
      return null;
   }

   public void setCheatsAllowed(boolean cheatsAllowed) {
      this.cheatsAllowed = cheatsAllowed;
   }

   public void disconnectAllPlayers() {
      for(int i = 0; i < this.players.size(); ++i) {
         ((ServerPlayerEntity)this.players.get(i)).networkHandler.disconnect(Text.translatable("multiplayer.disconnect.server_shutdown"));
      }

   }

   public void broadcast(Text message, boolean overlay) {
      this.broadcast(message, (player) -> {
         return message;
      }, overlay);
   }

   public void broadcast(Text message, Function playerMessageFactory, boolean overlay) {
      this.server.sendMessage(message);
      Iterator var4 = this.players.iterator();

      while(var4.hasNext()) {
         ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var4.next();
         Text text = (Text)playerMessageFactory.apply(serverPlayerEntity);
         if (text != null) {
            serverPlayerEntity.sendMessageToClient(text, overlay);
         }
      }

   }

   public void broadcast(SignedMessage message, ServerCommandSource source, MessageType.Parameters params) {
      Objects.requireNonNull(source);
      this.broadcast(message, source::shouldFilterText, source.getPlayer(), params);
   }

   public void broadcast(SignedMessage message, ServerPlayerEntity sender, MessageType.Parameters params) {
      Objects.requireNonNull(sender);
      this.broadcast(message, sender::shouldFilterMessagesSentTo, sender, params);
   }

   private void broadcast(SignedMessage message, Predicate shouldSendFiltered, @Nullable ServerPlayerEntity sender, MessageType.Parameters params) {
      boolean bl = this.verify(message);
      this.server.logChatMessage(message.getContent(), params, bl ? null : "Not Secure");
      SentMessage sentMessage = SentMessage.of(message);
      boolean bl2 = false;

      boolean bl3;
      for(Iterator var8 = this.players.iterator(); var8.hasNext(); bl2 |= bl3 && message.isFullyFiltered()) {
         ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var8.next();
         bl3 = shouldSendFiltered.test(serverPlayerEntity);
         serverPlayerEntity.sendChatMessage(sentMessage, bl3, params);
      }

      if (bl2 && sender != null) {
         sender.sendMessage(FILTERED_FULL_TEXT);
      }

   }

   private boolean verify(SignedMessage message) {
      return message.hasSignature() && !message.isExpiredOnServer(Instant.now());
   }

   public ServerStatHandler createStatHandler(PlayerEntity player) {
      UUID uUID = player.getUuid();
      ServerStatHandler serverStatHandler = (ServerStatHandler)this.statisticsMap.get(uUID);
      if (serverStatHandler == null) {
         File file = this.server.getSavePath(WorldSavePath.STATS).toFile();
         File file2 = new File(file, String.valueOf(uUID) + ".json");
         if (!file2.exists()) {
            File file3 = new File(file, player.getName().getString() + ".json");
            Path path = file3.toPath();
            if (PathUtil.isNormal(path) && PathUtil.isAllowedName(path) && path.startsWith(file.getPath()) && file3.isFile()) {
               file3.renameTo(file2);
            }
         }

         serverStatHandler = new ServerStatHandler(this.server, file2);
         this.statisticsMap.put(uUID, serverStatHandler);
      }

      return serverStatHandler;
   }

   public PlayerAdvancementTracker getAdvancementTracker(ServerPlayerEntity player) {
      UUID uUID = player.getUuid();
      PlayerAdvancementTracker playerAdvancementTracker = (PlayerAdvancementTracker)this.advancementTrackers.get(uUID);
      if (playerAdvancementTracker == null) {
         Path path = this.server.getSavePath(WorldSavePath.ADVANCEMENTS).resolve(String.valueOf(uUID) + ".json");
         playerAdvancementTracker = new PlayerAdvancementTracker(this.server.getDataFixer(), this, this.server.getAdvancementLoader(), path, player);
         this.advancementTrackers.put(uUID, playerAdvancementTracker);
      }

      playerAdvancementTracker.setOwner(player);
      return playerAdvancementTracker;
   }

   public void setViewDistance(int viewDistance) {
      this.viewDistance = viewDistance;
      this.sendToAll(new ChunkLoadDistanceS2CPacket(viewDistance));
      Iterator var2 = this.server.getWorlds().iterator();

      while(var2.hasNext()) {
         ServerWorld serverWorld = (ServerWorld)var2.next();
         if (serverWorld != null) {
            serverWorld.getChunkManager().applyViewDistance(viewDistance);
         }
      }

   }

   public void setSimulationDistance(int simulationDistance) {
      this.simulationDistance = simulationDistance;
      this.sendToAll(new SimulationDistanceS2CPacket(simulationDistance));
      Iterator var2 = this.server.getWorlds().iterator();

      while(var2.hasNext()) {
         ServerWorld serverWorld = (ServerWorld)var2.next();
         if (serverWorld != null) {
            serverWorld.getChunkManager().applySimulationDistance(simulationDistance);
         }
      }

   }

   public List getPlayerList() {
      return this.players;
   }

   @Nullable
   public ServerPlayerEntity getPlayer(UUID uuid) {
      return (ServerPlayerEntity)this.playerMap.get(uuid);
   }

   public boolean canBypassPlayerLimit(GameProfile profile) {
      return false;
   }

   public void onDataPacksReloaded() {
      Iterator var1 = this.advancementTrackers.values().iterator();

      while(var1.hasNext()) {
         PlayerAdvancementTracker playerAdvancementTracker = (PlayerAdvancementTracker)var1.next();
         playerAdvancementTracker.reload(this.server.getAdvancementLoader());
      }

      this.sendToAll(new SynchronizeTagsS2CPacket(TagPacketSerializer.serializeTags(this.registryManager)));
      ServerRecipeManager serverRecipeManager = this.server.getRecipeManager();
      SynchronizeRecipesS2CPacket synchronizeRecipesS2CPacket = new SynchronizeRecipesS2CPacket(serverRecipeManager.getPropertySets(), serverRecipeManager.getStonecutterRecipeForSync());
      Iterator var3 = this.players.iterator();

      while(var3.hasNext()) {
         ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var3.next();
         serverPlayerEntity.networkHandler.sendPacket(synchronizeRecipesS2CPacket);
         serverPlayerEntity.getRecipeBook().sendInitRecipesPacket(serverPlayerEntity);
      }

   }

   public boolean areCheatsAllowed() {
      return this.cheatsAllowed;
   }
}
