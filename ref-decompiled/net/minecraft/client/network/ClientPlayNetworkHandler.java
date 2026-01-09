package net.minecraft.client.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.hash.HashCode;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.lang.ref.WeakReference;
import java.time.Instant;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.DemoScreen;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.ReconfiguringScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.gui.screen.dialog.DialogNetworkAccess;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.screen.ingame.CommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.HorseScreen;
import net.minecraft.client.gui.screen.ingame.TestInstanceBlockScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.ClientRecipeManager;
import net.minecraft.client.render.debug.VillageDebugRenderer;
import net.minecraft.client.render.debug.VillageSectionsDebugRenderer;
import net.minecraft.client.render.debug.WorldGenAttemptDebugRenderer;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.sound.AggressiveBeeSoundInstance;
import net.minecraft.client.sound.GuardianAttackSoundInstance;
import net.minecraft.client.sound.MovingMinecartSoundInstance;
import net.minecraft.client.sound.PassiveBeeSoundInstance;
import net.minecraft.client.sound.SnifferDigSoundInstance;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.client.toast.RecipeToast;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.world.ClientWaypointHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.world.DataCache;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.SignedArgumentList;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.TrackedPosition;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerPosition;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.ExperimentalMinecartController;
import net.minecraft.entity.vehicle.MinecartController;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.FuelRegistry;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.encryption.ClientPlayerSession;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.encryption.PlayerKeyPair;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.encryption.PublicPlayerSession;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.message.ArgumentSignatureDataMap;
import net.minecraft.network.message.LastSeenMessagesCollector;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageChain;
import net.minecraft.network.message.MessageLink;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.MessageSignatureStorage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket;
import net.minecraft.network.packet.c2s.common.CustomClickActionC2SPacket;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.network.packet.c2s.play.AcknowledgeChunksC2SPacket;
import net.minecraft.network.packet.c2s.play.AcknowledgeReconfigurationC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatCommandSignedC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.c2s.play.MessageAcknowledgmentC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerLoadedC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerSessionC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.network.packet.s2c.common.SynchronizeTagsS2CPacket;
import net.minecraft.network.packet.s2c.custom.DebugBeeCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugBrainCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugBreezeCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugGameEventCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugGameEventListenersCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugGameTestAddMarkerCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugGameTestClearCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugGoalSelectorCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugHiveCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugNeighborsUpdateCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugPathCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugPoiAddedCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugPoiRemovedCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugPoiTicketCountCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugRaidsCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugRedstoneUpdateOrderCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugStructuresCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugVillageSectionsCustomPayload;
import net.minecraft.network.packet.s2c.custom.DebugWorldgenAttemptCustomPayload;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ChatSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkBiomeDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkData;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkLoadDistanceS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkRenderDistanceCenterS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkSentS2CPacket;
import net.minecraft.network.packet.s2c.play.ClearTitleS2CPacket;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.network.packet.s2c.play.CommonPlayerSpawnInfo;
import net.minecraft.network.packet.s2c.play.CooldownUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.CraftFailedResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.DamageTiltS2CPacket;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.DebugSampleS2CPacket;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.EndCombatS2CPacket;
import net.minecraft.network.packet.s2c.play.EnterCombatS2CPacket;
import net.minecraft.network.packet.s2c.play.EnterReconfigurationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionSyncS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.LightData;
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.LookAtS2CPacket;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.MoveMinecartAlongTrackS2CPacket;
import net.minecraft.network.packet.s2c.play.NbtQueryResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenHorseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenWrittenBookS2CPacket;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRotationS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.network.packet.s2c.play.ProfilelessChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ProjectilePowerS2CPacket;
import net.minecraft.network.packet.s2c.play.RecipeBookAddS2CPacket;
import net.minecraft.network.packet.s2c.play.RecipeBookRemoveS2CPacket;
import net.minecraft.network.packet.s2c.play.RecipeBookSettingsS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveEntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardDisplayS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreResetS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerPropertyUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SelectAdvancementTabS2CPacket;
import net.minecraft.network.packet.s2c.play.ServerMetadataS2CPacket;
import net.minecraft.network.packet.s2c.play.SetCameraEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.SetCursorItemS2CPacket;
import net.minecraft.network.packet.s2c.play.SetPlayerInventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.network.packet.s2c.play.SignEditorOpenS2CPacket;
import net.minecraft.network.packet.s2c.play.SimulationDistanceS2CPacket;
import net.minecraft.network.packet.s2c.play.StartChunkSendS2CPacket;
import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.network.packet.s2c.play.TestInstanceBlockStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.TickStepS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.network.packet.s2c.play.UnloadChunkS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateTickRateS2CPacket;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;
import net.minecraft.network.packet.s2c.play.WaypointS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderCenterChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderInitializeS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderInterpolateSizeS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderSizeChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderWarningBlocksChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderWarningTimeChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import net.minecraft.network.state.ConfigurationStates;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.display.CuttingRecipeDisplay;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.SerializableRegistries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagPacketSerializer;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.scoreboard.ScoreAccess;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.sync.ComponentChangesHash;
import net.minecraft.server.ServerLinks;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.storage.NbtReadView;
import net.minecraft.text.Text;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.HashCodeOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.tick.TickManager;
import net.minecraft.world.waypoint.TrackedWaypointHandler;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ClientPlayNetworkHandler extends ClientCommonNetworkHandler implements ClientPlayPacketListener, TickablePacketListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Text UNSECURE_SERVER_TOAST_TITLE = Text.translatable("multiplayer.unsecureserver.toast.title");
   private static final Text UNSECURE_SERVER_TOAST_TEXT = Text.translatable("multiplayer.unsecureserver.toast");
   private static final Text INVALID_PACKET_TEXT = Text.translatable("multiplayer.disconnect.invalid_packet");
   private static final Text RECONFIGURING_TEXT = Text.translatable("connect.reconfiguring");
   private static final Text BAD_CHAT_INDEX_TEXT = Text.translatable("multiplayer.disconnect.bad_chat_index");
   private static final Text CONFIRM_COMMAND_TITLE_TEXT = Text.translatable("multiplayer.confirm_command.title");
   private static final int ACKNOWLEDGMENT_BATCH_SIZE = 64;
   public static final int field_54852 = 64;
   private static final CommandTreeS2CPacket.NodeFactory COMMAND_NODE_FACTORY = new CommandTreeS2CPacket.NodeFactory() {
      public ArgumentBuilder literal(String name) {
         return LiteralArgumentBuilder.literal(name);
      }

      public ArgumentBuilder argument(String name, ArgumentType type, @Nullable Identifier suggestionProviderId) {
         RequiredArgumentBuilder requiredArgumentBuilder = RequiredArgumentBuilder.argument(name, type);
         if (suggestionProviderId != null) {
            requiredArgumentBuilder.suggests(SuggestionProviders.byId(suggestionProviderId));
         }

         return requiredArgumentBuilder;
      }

      public ArgumentBuilder modifyNode(ArgumentBuilder arg, boolean disableExecution, boolean requireTrusted) {
         if (disableExecution) {
            arg.executes((context) -> {
               return 0;
            });
         }

         if (requireTrusted) {
            arg.requires(ClientCommandSource::isTrusted);
         }

         return arg;
      }
   };
   private final GameProfile profile;
   private ClientWorld world;
   private ClientWorld.Properties worldProperties;
   private final Map playerListEntries = Maps.newHashMap();
   private final Set listedPlayerListEntries = new ReferenceOpenHashSet();
   private final ClientAdvancementManager advancementHandler;
   private final ClientCommandSource commandSource;
   private final ClientCommandSource restrictedCommandSource;
   private final DataQueryHandler dataQueryHandler = new DataQueryHandler(this);
   private int chunkLoadDistance = 3;
   private int simulationDistance = 3;
   private final Random random = Random.createThreadSafe();
   private CommandDispatcher commandDispatcher = new CommandDispatcher();
   private ClientRecipeManager recipeManager = new ClientRecipeManager(Map.of(), CuttingRecipeDisplay.Grouping.empty());
   private final UUID sessionId = UUID.randomUUID();
   private Set worldKeys;
   private final DynamicRegistryManager.Immutable combinedDynamicRegistries;
   private final FeatureSet enabledFeatures;
   private final BrewingRecipeRegistry brewingRecipeRegistry;
   private FuelRegistry fuelRegistry;
   private final ComponentChangesHash.ComponentHasher componentHasher;
   private OptionalInt removedPlayerVehicleId = OptionalInt.empty();
   @Nullable
   private ClientPlayerSession session;
   private MessageChain.Packer messagePacker;
   private int globalChatMessageIndex;
   private LastSeenMessagesCollector lastSeenMessagesCollector;
   private MessageSignatureStorage signatureStorage;
   @Nullable
   private CompletableFuture profileKeyPairFuture;
   @Nullable
   private SyncedClientOptions syncedOptions;
   private final ChunkBatchSizeCalculator chunkBatchSizeCalculator;
   private final PingMeasurer pingMeasurer;
   private final DebugSampleSubscriber debugSampleSubscriber;
   @Nullable
   private WorldLoadingState worldLoadingState;
   private boolean secureChatEnforced;
   private boolean displayedUnsecureChatWarning;
   private volatile boolean worldCleared;
   private final Scoreboard scoreboard;
   private final ClientWaypointHandler waypointHandler;
   private final SearchManager searchManager;
   private final List cachedData;

   public ClientPlayNetworkHandler(MinecraftClient client, ClientConnection clientConnection, ClientConnectionState clientConnectionState) {
      super(client, clientConnection, clientConnectionState);
      this.messagePacker = MessageChain.Packer.NONE;
      this.lastSeenMessagesCollector = new LastSeenMessagesCollector(20);
      this.signatureStorage = MessageSignatureStorage.create();
      this.chunkBatchSizeCalculator = new ChunkBatchSizeCalculator();
      this.displayedUnsecureChatWarning = false;
      this.scoreboard = new Scoreboard();
      this.waypointHandler = new ClientWaypointHandler();
      this.searchManager = new SearchManager();
      this.cachedData = new ArrayList();
      this.profile = clientConnectionState.localGameProfile();
      this.combinedDynamicRegistries = clientConnectionState.receivedRegistries();
      RegistryOps registryOps = this.combinedDynamicRegistries.getOps(HashCodeOps.INSTANCE);
      this.componentHasher = (component) -> {
         return ((HashCode)component.encode(registryOps).getOrThrow((error) -> {
            String var10002 = String.valueOf(component);
            return new IllegalArgumentException("Failed to hash " + var10002 + ": " + error);
         })).asInt();
      };
      this.enabledFeatures = clientConnectionState.enabledFeatures();
      this.advancementHandler = new ClientAdvancementManager(client, this.worldSession);
      this.commandSource = new ClientCommandSource(this, client, true);
      this.restrictedCommandSource = new ClientCommandSource(this, client, false);
      this.pingMeasurer = new PingMeasurer(this, client.getDebugHud().getPingLog());
      this.debugSampleSubscriber = new DebugSampleSubscriber(this, client.getDebugHud());
      if (clientConnectionState.chatState() != null) {
         client.inGameHud.getChatHud().restoreChatState(clientConnectionState.chatState());
      }

      this.brewingRecipeRegistry = BrewingRecipeRegistry.create(this.enabledFeatures);
      this.fuelRegistry = FuelRegistry.createDefault(clientConnectionState.receivedRegistries(), this.enabledFeatures);
   }

   public ClientCommandSource getCommandSource() {
      return this.commandSource;
   }

   public void unloadWorld() {
      this.worldCleared = true;
      this.clearWorld();
      this.worldSession.onUnload();
   }

   public void clearWorld() {
      this.clearCachedData();
      this.world = null;
      this.worldLoadingState = null;
   }

   private void clearCachedData() {
      Iterator var1 = this.cachedData.iterator();

      while(var1.hasNext()) {
         WeakReference weakReference = (WeakReference)var1.next();
         DataCache dataCache = (DataCache)weakReference.get();
         if (dataCache != null) {
            dataCache.clean();
         }
      }

      this.cachedData.clear();
   }

   public RecipeManager getRecipeManager() {
      return this.recipeManager;
   }

   public void onGameJoin(GameJoinS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.client.interactionManager = new ClientPlayerInteractionManager(this.client, this);
      CommonPlayerSpawnInfo commonPlayerSpawnInfo = packet.commonPlayerSpawnInfo();
      List list = Lists.newArrayList(packet.dimensionIds());
      Collections.shuffle(list);
      this.worldKeys = Sets.newLinkedHashSet(list);
      RegistryKey registryKey = commonPlayerSpawnInfo.dimension();
      RegistryEntry registryEntry = commonPlayerSpawnInfo.dimensionType();
      this.chunkLoadDistance = packet.viewDistance();
      this.simulationDistance = packet.simulationDistance();
      boolean bl = commonPlayerSpawnInfo.isDebug();
      boolean bl2 = commonPlayerSpawnInfo.isFlat();
      int i = commonPlayerSpawnInfo.seaLevel();
      ClientWorld.Properties properties = new ClientWorld.Properties(Difficulty.NORMAL, packet.hardcore(), bl2);
      this.worldProperties = properties;
      this.world = new ClientWorld(this, properties, registryKey, registryEntry, this.chunkLoadDistance, this.simulationDistance, this.client.worldRenderer, bl, commonPlayerSpawnInfo.seed(), i);
      this.client.joinWorld(this.world, DownloadingTerrainScreen.WorldEntryReason.OTHER);
      if (this.client.player == null) {
         this.client.player = this.client.interactionManager.createPlayer(this.world, new StatHandler(), new ClientRecipeBook());
         this.client.player.setYaw(-180.0F);
         if (this.client.getServer() != null) {
            this.client.getServer().setLocalPlayerUuid(this.client.player.getUuid());
         }
      }

      this.client.debugRenderer.reset();
      this.client.player.init();
      this.client.player.setId(packet.playerEntityId());
      this.world.addEntity(this.client.player);
      this.client.player.input = new KeyboardInput(this.client.options);
      this.client.interactionManager.copyAbilities(this.client.player);
      this.client.cameraEntity = this.client.player;
      this.startWorldLoading(this.client.player, this.world, DownloadingTerrainScreen.WorldEntryReason.OTHER);
      this.client.player.setReducedDebugInfo(packet.reducedDebugInfo());
      this.client.player.setShowsDeathScreen(packet.showDeathScreen());
      this.client.player.setLimitedCraftingEnabled(packet.doLimitedCrafting());
      this.client.player.setLastDeathPos(commonPlayerSpawnInfo.lastDeathLocation());
      this.client.player.setPortalCooldown(commonPlayerSpawnInfo.portalCooldown());
      this.client.interactionManager.setGameModes(commonPlayerSpawnInfo.gameMode(), commonPlayerSpawnInfo.lastGameMode());
      this.client.options.setServerViewDistance(packet.viewDistance());
      this.session = null;
      this.messagePacker = MessageChain.Packer.NONE;
      this.globalChatMessageIndex = 0;
      this.lastSeenMessagesCollector = new LastSeenMessagesCollector(20);
      this.signatureStorage = MessageSignatureStorage.create();
      if (this.connection.isEncrypted()) {
         this.fetchProfileKey();
      }

      this.worldSession.setGameMode(commonPlayerSpawnInfo.gameMode(), packet.hardcore());
      this.client.getQuickPlayLogger().save(this.client);
      this.secureChatEnforced = packet.enforcesSecureChat();
      if (this.serverInfo != null && !this.displayedUnsecureChatWarning && !this.isSecureChatEnforced()) {
         SystemToast systemToast = SystemToast.create(this.client, SystemToast.Type.UNSECURE_SERVER_WARNING, UNSECURE_SERVER_TOAST_TITLE, UNSECURE_SERVER_TOAST_TEXT);
         this.client.getToastManager().add(systemToast);
         this.displayedUnsecureChatWarning = true;
      }

   }

   public void onEntitySpawn(EntitySpawnS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      if (this.removedPlayerVehicleId.isPresent() && this.removedPlayerVehicleId.getAsInt() == packet.getEntityId()) {
         this.removedPlayerVehicleId = OptionalInt.empty();
      }

      Entity entity = this.createEntity(packet);
      if (entity != null) {
         entity.onSpawnPacket(packet);
         this.world.addEntity(entity);
         this.playSpawnSound(entity);
      } else {
         LOGGER.warn("Skipping Entity with id {}", packet.getEntityType());
      }

   }

   @Nullable
   private Entity createEntity(EntitySpawnS2CPacket packet) {
      EntityType entityType = packet.getEntityType();
      if (entityType == EntityType.PLAYER) {
         PlayerListEntry playerListEntry = this.getPlayerListEntry(packet.getUuid());
         if (playerListEntry == null) {
            LOGGER.warn("Server attempted to add player prior to sending player info (Player id {})", packet.getUuid());
            return null;
         } else {
            return new OtherClientPlayerEntity(this.world, playerListEntry.getProfile());
         }
      } else {
         return entityType.create(this.world, SpawnReason.LOAD);
      }
   }

   private void playSpawnSound(Entity entity) {
      if (entity instanceof AbstractMinecartEntity abstractMinecartEntity) {
         this.client.getSoundManager().play(new MovingMinecartSoundInstance(abstractMinecartEntity));
      } else if (entity instanceof BeeEntity beeEntity) {
         boolean bl = beeEntity.hasAngerTime();
         Object abstractBeeSoundInstance;
         if (bl) {
            abstractBeeSoundInstance = new AggressiveBeeSoundInstance(beeEntity);
         } else {
            abstractBeeSoundInstance = new PassiveBeeSoundInstance(beeEntity);
         }

         this.client.getSoundManager().playNextTick((TickableSoundInstance)abstractBeeSoundInstance);
      }

   }

   public void onEntityVelocityUpdate(EntityVelocityUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      Entity entity = this.world.getEntityById(packet.getEntityId());
      if (entity != null) {
         entity.setVelocityClient(packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityZ());
      }
   }

   public void onEntityTrackerUpdate(EntityTrackerUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      Entity entity = this.world.getEntityById(packet.id());
      if (entity != null) {
         entity.getDataTracker().writeUpdatedEntries(packet.trackedValues());
      }

   }

   public void onEntityPositionSync(EntityPositionSyncS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      Entity entity = this.world.getEntityById(packet.id());
      if (entity != null) {
         Vec3d vec3d = packet.values().position();
         entity.getTrackedPosition().setPos(vec3d);
         if (!entity.isLogicalSideForUpdatingMovement()) {
            float f = packet.values().yaw();
            float g = packet.values().pitch();
            boolean bl = entity.getPos().squaredDistanceTo(vec3d) > 4096.0;
            if (this.world.hasEntity(entity) && !bl) {
               entity.updateTrackedPositionAndAngles(vec3d, f, g);
            } else {
               entity.refreshPositionAndAngles(vec3d, f, g);
            }

            if (!entity.isInterpolating() && entity.hasPassengerDeep(this.client.player)) {
               entity.updatePassengerPosition(this.client.player);
               this.client.player.resetPosition();
            }

            entity.setOnGround(packet.onGround());
         }
      }
   }

   public void onEntityPosition(EntityPositionS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      Entity entity = this.world.getEntityById(packet.entityId());
      if (entity == null) {
         if (this.removedPlayerVehicleId.isPresent() && this.removedPlayerVehicleId.getAsInt() == packet.entityId()) {
            LOGGER.debug("Trying to teleport entity with id {}, that was formerly player vehicle, applying teleport to player instead", packet.entityId());
            setPosition(packet.change(), packet.relatives(), this.client.player, false);
            this.connection.send(new PlayerMoveC2SPacket.Full(this.client.player.getX(), this.client.player.getY(), this.client.player.getZ(), this.client.player.getYaw(), this.client.player.getPitch(), false, false));
         }

      } else {
         boolean bl = packet.relatives().contains(PositionFlag.X) || packet.relatives().contains(PositionFlag.Y) || packet.relatives().contains(PositionFlag.Z);
         boolean bl2 = this.world.hasEntity(entity) || !entity.isLogicalSideForUpdatingMovement() || bl;
         boolean bl3 = setPosition(packet.change(), packet.relatives(), entity, bl2);
         entity.setOnGround(packet.onGround());
         if (!bl3 && entity.hasPassengerDeep(this.client.player)) {
            entity.updatePassengerPosition(this.client.player);
            this.client.player.resetPosition();
            if (entity.isLogicalSideForUpdatingMovement()) {
               this.connection.send(VehicleMoveC2SPacket.fromVehicle(entity));
            }
         }

      }
   }

   public void onUpdateTickRate(UpdateTickRateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      if (this.client.world != null) {
         TickManager tickManager = this.client.world.getTickManager();
         tickManager.setTickRate(packet.tickRate());
         tickManager.setFrozen(packet.isFrozen());
      }
   }

   public void onTickStep(TickStepS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      if (this.client.world != null) {
         TickManager tickManager = this.client.world.getTickManager();
         tickManager.setStepTicks(packet.tickSteps());
      }
   }

   public void onUpdateSelectedSlot(UpdateSelectedSlotS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      if (PlayerInventory.isValidHotbarIndex(packet.slot())) {
         this.client.player.getInventory().setSelectedSlot(packet.slot());
      }

   }

   public void onEntity(EntityS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      Entity entity = packet.getEntity(this.world);
      if (entity != null) {
         TrackedPosition trackedPosition;
         Vec3d vec3d;
         if (entity.isLogicalSideForUpdatingMovement()) {
            trackedPosition = entity.getTrackedPosition();
            vec3d = trackedPosition.withDelta((long)packet.getDeltaX(), (long)packet.getDeltaY(), (long)packet.getDeltaZ());
            trackedPosition.setPos(vec3d);
         } else {
            if (packet.isPositionChanged()) {
               trackedPosition = entity.getTrackedPosition();
               vec3d = trackedPosition.withDelta((long)packet.getDeltaX(), (long)packet.getDeltaY(), (long)packet.getDeltaZ());
               trackedPosition.setPos(vec3d);
               if (packet.hasRotation()) {
                  entity.updateTrackedPositionAndAngles(vec3d, packet.getYaw(), packet.getPitch());
               } else {
                  entity.updateTrackedPositionAndAngles(vec3d, entity.getYaw(), entity.getPitch());
               }
            } else if (packet.hasRotation()) {
               entity.updateTrackedPositionAndAngles(entity.getPos(), packet.getYaw(), packet.getPitch());
            }

            entity.setOnGround(packet.isOnGround());
         }
      }
   }

   public void onMoveMinecartAlongTrack(MoveMinecartAlongTrackS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      Entity entity = packet.getEntity(this.world);
      if (entity instanceof AbstractMinecartEntity abstractMinecartEntity) {
         MinecartController var5 = abstractMinecartEntity.getController();
         if (var5 instanceof ExperimentalMinecartController experimentalMinecartController) {
            experimentalMinecartController.stagingLerpSteps.addAll(packet.lerpSteps());
         }

      }
   }

   public void onEntitySetHeadYaw(EntitySetHeadYawS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      Entity entity = packet.getEntity(this.world);
      if (entity != null) {
         entity.updateTrackedHeadRotation(packet.getHeadYaw(), 3);
      }
   }

   public void onEntitiesDestroy(EntitiesDestroyS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      packet.getEntityIds().forEach((id) -> {
         Entity entity = this.world.getEntityById(id);
         if (entity != null) {
            if (entity.hasPassengerDeep(this.client.player)) {
               LOGGER.debug("Remove entity {}:{} that has player as passenger", entity.getType(), id);
               this.removedPlayerVehicleId = OptionalInt.of(id);
            }

            this.world.removeEntity(id, Entity.RemovalReason.DISCARDED);
         }
      });
   }

   public void onPlayerPositionLook(PlayerPositionLookS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      PlayerEntity playerEntity = this.client.player;
      if (!playerEntity.hasVehicle()) {
         setPosition(packet.change(), packet.relatives(), playerEntity, false);
      }

      this.connection.send(new TeleportConfirmC2SPacket(packet.teleportId()));
      this.connection.send(new PlayerMoveC2SPacket.Full(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), playerEntity.getYaw(), playerEntity.getPitch(), false, false));
   }

   private static boolean setPosition(PlayerPosition pos, Set flags, Entity entity, boolean bl) {
      PlayerPosition playerPosition = PlayerPosition.fromEntity(entity);
      PlayerPosition playerPosition2 = PlayerPosition.apply(playerPosition, pos, flags);
      boolean bl2 = playerPosition.position().squaredDistanceTo(playerPosition2.position()) > 4096.0;
      if (bl && !bl2) {
         entity.updateTrackedPositionAndAngles(playerPosition2.position(), playerPosition2.yaw(), playerPosition2.pitch());
         entity.setVelocity(playerPosition2.deltaMovement());
         return true;
      } else {
         entity.setPosition(playerPosition2.position());
         entity.setVelocity(playerPosition2.deltaMovement());
         entity.setYaw(playerPosition2.yaw());
         entity.setPitch(playerPosition2.pitch());
         PlayerPosition playerPosition3 = new PlayerPosition(entity.getLastRenderPos(), Vec3d.ZERO, entity.lastYaw, entity.lastPitch);
         PlayerPosition playerPosition4 = PlayerPosition.apply(playerPosition3, pos, flags);
         entity.setLastPositionAndAngles(playerPosition4.position(), playerPosition4.yaw(), playerPosition4.pitch());
         return false;
      }
   }

   public void onPlayerRotation(PlayerRotationS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      PlayerEntity playerEntity = this.client.player;
      playerEntity.setYaw(packet.yRot());
      playerEntity.setPitch(packet.xRot());
      playerEntity.updateLastAngles();
      this.connection.send(new PlayerMoveC2SPacket.LookAndOnGround(playerEntity.getYaw(), playerEntity.getPitch(), false, false));
   }

   public void onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      packet.visitUpdates((pos, state) -> {
         this.world.handleBlockUpdate(pos, state, 19);
      });
   }

   public void onChunkData(ChunkDataS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      int i = packet.getChunkX();
      int j = packet.getChunkZ();
      this.loadChunk(i, j, packet.getChunkData());
      LightData lightData = packet.getLightData();
      this.world.enqueueChunkUpdate(() -> {
         this.readLightData(i, j, lightData, false);
         WorldChunk worldChunk = this.world.getChunkManager().getWorldChunk(i, j, false);
         if (worldChunk != null) {
            this.scheduleRenderChunk(worldChunk, i, j);
            this.client.worldRenderer.scheduleNeighborUpdates(worldChunk.getPos());
         }

      });
   }

   public void onChunkBiomeData(ChunkBiomeDataS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      Iterator var2 = packet.chunkBiomeData().iterator();

      ChunkBiomeDataS2CPacket.Serialized serialized;
      while(var2.hasNext()) {
         serialized = (ChunkBiomeDataS2CPacket.Serialized)var2.next();
         this.world.getChunkManager().onChunkBiomeData(serialized.pos().x, serialized.pos().z, serialized.toReadingBuf());
      }

      var2 = packet.chunkBiomeData().iterator();

      while(var2.hasNext()) {
         serialized = (ChunkBiomeDataS2CPacket.Serialized)var2.next();
         this.world.resetChunkColor(new ChunkPos(serialized.pos().x, serialized.pos().z));
      }

      var2 = packet.chunkBiomeData().iterator();

      while(var2.hasNext()) {
         serialized = (ChunkBiomeDataS2CPacket.Serialized)var2.next();

         for(int i = -1; i <= 1; ++i) {
            for(int j = -1; j <= 1; ++j) {
               for(int k = this.world.getBottomSectionCoord(); k <= this.world.getTopSectionCoord(); ++k) {
                  this.client.worldRenderer.scheduleChunkRender(serialized.pos().x + i, k, serialized.pos().z + j);
               }
            }
         }
      }

   }

   private void loadChunk(int x, int z, ChunkData chunkData) {
      this.world.getChunkManager().loadChunkFromPacket(x, z, chunkData.getSectionsDataBuf(), chunkData.getHeightmap(), chunkData.getBlockEntities(x, z));
   }

   private void scheduleRenderChunk(WorldChunk chunk, int x, int z) {
      LightingProvider lightingProvider = this.world.getChunkManager().getLightingProvider();
      ChunkSection[] chunkSections = chunk.getSectionArray();
      ChunkPos chunkPos = chunk.getPos();

      for(int i = 0; i < chunkSections.length; ++i) {
         ChunkSection chunkSection = chunkSections[i];
         int j = this.world.sectionIndexToCoord(i);
         lightingProvider.setSectionStatus(ChunkSectionPos.from(chunkPos, j), chunkSection.isEmpty());
      }

      this.world.scheduleChunkRenders(x - 1, this.world.getBottomSectionCoord(), z - 1, x + 1, this.world.getTopSectionCoord(), z + 1);
   }

   public void onUnloadChunk(UnloadChunkS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.world.getChunkManager().unload(packet.pos());
      this.unloadChunk(packet);
   }

   private void unloadChunk(UnloadChunkS2CPacket packet) {
      ChunkPos chunkPos = packet.pos();
      this.world.enqueueChunkUpdate(() -> {
         LightingProvider lightingProvider = this.world.getLightingProvider();
         lightingProvider.setColumnEnabled(chunkPos, false);

         int i;
         for(i = lightingProvider.getBottomY(); i < lightingProvider.getTopY(); ++i) {
            ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunkPos, i);
            lightingProvider.enqueueSectionData(LightType.BLOCK, chunkSectionPos, (ChunkNibbleArray)null);
            lightingProvider.enqueueSectionData(LightType.SKY, chunkSectionPos, (ChunkNibbleArray)null);
         }

         for(i = this.world.getBottomSectionCoord(); i <= this.world.getTopSectionCoord(); ++i) {
            lightingProvider.setSectionStatus(ChunkSectionPos.from(chunkPos, i), true);
         }

      });
   }

   public void onBlockUpdate(BlockUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.world.handleBlockUpdate(packet.getPos(), packet.getState(), 19);
   }

   public void onEnterReconfiguration(EnterReconfigurationS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.client.getMessageHandler().processAll();
      this.sendAcknowledgment();
      ChatHud.ChatState chatState = this.client.inGameHud.getChatHud().toChatState();
      this.client.enterReconfiguration(new ReconfiguringScreen(RECONFIGURING_TEXT, this.connection));
      this.connection.transitionInbound(ConfigurationStates.S2C, new ClientConfigurationNetworkHandler(this.client, this.connection, new ClientConnectionState(this.profile, this.worldSession, this.combinedDynamicRegistries, this.enabledFeatures, this.brand, this.serverInfo, this.postDisconnectScreen, this.serverCookies, chatState, this.customReportDetails, this.getServerLinks())));
      this.sendPacket(AcknowledgeReconfigurationC2SPacket.INSTANCE);
      this.connection.transitionOutbound(ConfigurationStates.C2S);
   }

   public void onItemPickupAnimation(ItemPickupAnimationS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      Entity entity = this.world.getEntityById(packet.getEntityId());
      LivingEntity livingEntity = (LivingEntity)this.world.getEntityById(packet.getCollectorEntityId());
      if (livingEntity == null) {
         livingEntity = this.client.player;
      }

      if (entity != null) {
         if (entity instanceof ExperienceOrbEntity) {
            this.world.playSoundClient(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, (this.random.nextFloat() - this.random.nextFloat()) * 0.35F + 0.9F, false);
         } else {
            this.world.playSoundClient(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, (this.random.nextFloat() - this.random.nextFloat()) * 1.4F + 2.0F, false);
         }

         this.client.particleManager.addParticle(new ItemPickupParticle(this.client.getEntityRenderDispatcher(), this.world, entity, (Entity)livingEntity));
         if (entity instanceof ItemEntity) {
            ItemEntity itemEntity = (ItemEntity)entity;
            ItemStack itemStack = itemEntity.getStack();
            if (!itemStack.isEmpty()) {
               itemStack.decrement(packet.getStackAmount());
            }

            if (itemStack.isEmpty()) {
               this.world.removeEntity(packet.getEntityId(), Entity.RemovalReason.DISCARDED);
            }
         } else if (!(entity instanceof ExperienceOrbEntity)) {
            this.world.removeEntity(packet.getEntityId(), Entity.RemovalReason.DISCARDED);
         }
      }

   }

   public void onGameMessage(GameMessageS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.client.getMessageHandler().onGameMessage(packet.content(), packet.overlay());
   }

   public void onChatMessage(ChatMessageS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      int i = this.globalChatMessageIndex++;
      if (packet.globalIndex() != i) {
         LOGGER.error("Missing or out-of-order chat message from server, expected index {} but got {}", i, packet.globalIndex());
         this.connection.disconnect(BAD_CHAT_INDEX_TEXT);
      } else {
         Optional optional = packet.body().toBody(this.signatureStorage);
         if (optional.isEmpty()) {
            LOGGER.error("Message from player with ID {} referenced unrecognized signature id", packet.sender());
            this.connection.disconnect(INVALID_PACKET_TEXT);
         } else {
            this.signatureStorage.add((MessageBody)optional.get(), packet.signature());
            UUID uUID = packet.sender();
            PlayerListEntry playerListEntry = this.getPlayerListEntry(uUID);
            if (playerListEntry == null) {
               LOGGER.error("Received player chat packet for unknown player with ID: {}", uUID);
               this.client.getMessageHandler().onUnverifiedMessage(uUID, packet.signature(), packet.serializedParameters());
            } else {
               PublicPlayerSession publicPlayerSession = playerListEntry.getSession();
               MessageLink messageLink;
               if (publicPlayerSession != null) {
                  messageLink = new MessageLink(packet.index(), uUID, publicPlayerSession.sessionId());
               } else {
                  messageLink = MessageLink.of(uUID);
               }

               SignedMessage signedMessage = new SignedMessage(messageLink, packet.signature(), (MessageBody)optional.get(), packet.unsignedContent(), packet.filterMask());
               signedMessage = playerListEntry.getMessageVerifier().ensureVerified(signedMessage);
               if (signedMessage != null) {
                  this.client.getMessageHandler().onChatMessage(signedMessage, playerListEntry.getProfile(), packet.serializedParameters());
               } else {
                  this.client.getMessageHandler().onUnverifiedMessage(uUID, packet.signature(), packet.serializedParameters());
               }

            }
         }
      }
   }

   public void onProfilelessChatMessage(ProfilelessChatMessageS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.client.getMessageHandler().onProfilelessMessage(packet.message(), packet.chatType());
   }

   public void onRemoveMessage(RemoveMessageS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      Optional optional = packet.messageSignature().getSignature(this.signatureStorage);
      if (optional.isEmpty()) {
         this.connection.disconnect(INVALID_PACKET_TEXT);
      } else {
         this.lastSeenMessagesCollector.remove((MessageSignatureData)optional.get());
         if (!this.client.getMessageHandler().removeDelayedMessage((MessageSignatureData)optional.get())) {
            this.client.inGameHud.getChatHud().removeMessage((MessageSignatureData)optional.get());
         }

      }
   }

   public void onEntityAnimation(EntityAnimationS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      Entity entity = this.world.getEntityById(packet.getEntityId());
      if (entity != null) {
         LivingEntity livingEntity;
         if (packet.getAnimationId() == 0) {
            livingEntity = (LivingEntity)entity;
            livingEntity.swingHand(Hand.MAIN_HAND);
         } else if (packet.getAnimationId() == 3) {
            livingEntity = (LivingEntity)entity;
            livingEntity.swingHand(Hand.OFF_HAND);
         } else if (packet.getAnimationId() == 2) {
            PlayerEntity playerEntity = (PlayerEntity)entity;
            playerEntity.wakeUp(false, false);
         } else if (packet.getAnimationId() == 4) {
            this.client.particleManager.addEmitter(entity, ParticleTypes.CRIT);
         } else if (packet.getAnimationId() == 5) {
            this.client.particleManager.addEmitter(entity, ParticleTypes.ENCHANTED_HIT);
         }

      }
   }

   public void onDamageTilt(DamageTiltS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      Entity entity = this.world.getEntityById(packet.id());
      if (entity != null) {
         entity.animateDamage(packet.yaw());
      }
   }

   public void onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.world.setTime(packet.time(), packet.timeOfDay(), packet.tickDayTime());
      this.worldSession.setTick(packet.time());
   }

   public void onPlayerSpawnPosition(PlayerSpawnPositionS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.client.world.setSpawnPos(packet.getPos(), packet.getAngle());
   }

   public void onEntityPassengersSet(EntityPassengersSetS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      Entity entity = this.world.getEntityById(packet.getEntityId());
      if (entity == null) {
         LOGGER.warn("Received passengers for unknown entity");
      } else {
         boolean bl = entity.hasPassengerDeep(this.client.player);
         entity.removeAllPassengers();
         int[] var4 = packet.getPassengerIds();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            int i = var4[var6];
            Entity entity2 = this.world.getEntityById(i);
            if (entity2 != null) {
               entity2.startRiding(entity, true);
               if (entity2 == this.client.player) {
                  this.removedPlayerVehicleId = OptionalInt.empty();
                  if (!bl) {
                     if (entity instanceof AbstractBoatEntity) {
                        this.client.player.lastYaw = entity.getYaw();
                        this.client.player.setYaw(entity.getYaw());
                        this.client.player.setHeadYaw(entity.getYaw());
                     }

                     Text text = Text.translatable("mount.onboard", this.client.options.sneakKey.getBoundKeyLocalizedText());
                     this.client.inGameHud.setOverlayMessage(text, false);
                     this.client.getNarratorManager().narrateSystemImmediately((Text)text);
                  }
               }
            }
         }

      }
   }

   public void onEntityAttach(EntityAttachS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      Entity entity = this.world.getEntityById(packet.getAttachedEntityId());
      if (entity instanceof Leashable leashable) {
         leashable.setUnresolvedLeashHolderId(packet.getHoldingEntityId());
      }

   }

   private static ItemStack getActiveDeathProtector(PlayerEntity player) {
      Hand[] var1 = Hand.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Hand hand = var1[var3];
         ItemStack itemStack = player.getStackInHand(hand);
         if (itemStack.contains(DataComponentTypes.DEATH_PROTECTION)) {
            return itemStack;
         }
      }

      return new ItemStack(Items.TOTEM_OF_UNDYING);
   }

   public void onEntityStatus(EntityStatusS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      Entity entity = packet.getEntity(this.world);
      if (entity != null) {
         switch (packet.getStatus()) {
            case 21:
               this.client.getSoundManager().play(new GuardianAttackSoundInstance((GuardianEntity)entity));
               break;
            case 35:
               int i = true;
               this.client.particleManager.addEmitter(entity, ParticleTypes.TOTEM_OF_UNDYING, 30);
               this.world.playSoundClient(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_TOTEM_USE, entity.getSoundCategory(), 1.0F, 1.0F, false);
               if (entity == this.client.player) {
                  this.client.gameRenderer.showFloatingItem(getActiveDeathProtector(this.client.player));
               }
               break;
            case 63:
               this.client.getSoundManager().play(new SnifferDigSoundInstance((SnifferEntity)entity));
               break;
            default:
               entity.handleStatus(packet.getStatus());
         }
      }

   }

   public void onEntityDamage(EntityDamageS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      Entity entity = this.world.getEntityById(packet.entityId());
      if (entity != null) {
         entity.onDamaged(packet.createDamageSource(this.world));
      }
   }

   public void onHealthUpdate(HealthUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.client.player.updateHealth(packet.getHealth());
      this.client.player.getHungerManager().setFoodLevel(packet.getFood());
      this.client.player.getHungerManager().setSaturationLevel(packet.getSaturation());
   }

   public void onExperienceBarUpdate(ExperienceBarUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.client.player.setExperience(packet.getBarProgress(), packet.getExperienceLevel(), packet.getExperience());
   }

   public void onPlayerRespawn(PlayerRespawnS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      CommonPlayerSpawnInfo commonPlayerSpawnInfo = packet.commonPlayerSpawnInfo();
      RegistryKey registryKey = commonPlayerSpawnInfo.dimension();
      RegistryEntry registryEntry = commonPlayerSpawnInfo.dimensionType();
      ClientPlayerEntity clientPlayerEntity = this.client.player;
      RegistryKey registryKey2 = clientPlayerEntity.getWorld().getRegistryKey();
      boolean bl = registryKey != registryKey2;
      DownloadingTerrainScreen.WorldEntryReason worldEntryReason = this.getWorldEntryReason(clientPlayerEntity.isDead(), registryKey, registryKey2);
      if (bl) {
         Map map = this.world.getMapStates();
         boolean bl2 = commonPlayerSpawnInfo.isDebug();
         boolean bl3 = commonPlayerSpawnInfo.isFlat();
         int i = commonPlayerSpawnInfo.seaLevel();
         ClientWorld.Properties properties = new ClientWorld.Properties(this.worldProperties.getDifficulty(), this.worldProperties.isHardcore(), bl3);
         this.worldProperties = properties;
         this.world = new ClientWorld(this, properties, registryKey, registryEntry, this.chunkLoadDistance, this.simulationDistance, this.client.worldRenderer, bl2, commonPlayerSpawnInfo.seed(), i);
         this.world.putMapStates(map);
         this.client.joinWorld(this.world, worldEntryReason);
      }

      this.client.cameraEntity = null;
      if (clientPlayerEntity.shouldCloseHandledScreenOnRespawn()) {
         clientPlayerEntity.closeHandledScreen();
      }

      ClientPlayerEntity clientPlayerEntity2;
      if (packet.hasFlag((byte)2)) {
         clientPlayerEntity2 = this.client.interactionManager.createPlayer(this.world, clientPlayerEntity.getStatHandler(), clientPlayerEntity.getRecipeBook(), clientPlayerEntity.getLastPlayerInput(), clientPlayerEntity.isSprinting());
      } else {
         clientPlayerEntity2 = this.client.interactionManager.createPlayer(this.world, clientPlayerEntity.getStatHandler(), clientPlayerEntity.getRecipeBook());
      }

      this.startWorldLoading(clientPlayerEntity2, this.world, worldEntryReason);
      clientPlayerEntity2.setId(clientPlayerEntity.getId());
      this.client.player = clientPlayerEntity2;
      if (bl) {
         this.client.getMusicTracker().stop();
      }

      this.client.cameraEntity = clientPlayerEntity2;
      if (packet.hasFlag((byte)2)) {
         List list = clientPlayerEntity.getDataTracker().getChangedEntries();
         if (list != null) {
            clientPlayerEntity2.getDataTracker().writeUpdatedEntries(list);
         }

         clientPlayerEntity2.setVelocity(clientPlayerEntity.getVelocity());
         clientPlayerEntity2.setYaw(clientPlayerEntity.getYaw());
         clientPlayerEntity2.setPitch(clientPlayerEntity.getPitch());
      } else {
         clientPlayerEntity2.init();
         clientPlayerEntity2.setYaw(-180.0F);
      }

      if (packet.hasFlag((byte)1)) {
         clientPlayerEntity2.getAttributes().setFrom(clientPlayerEntity.getAttributes());
      } else {
         clientPlayerEntity2.getAttributes().setBaseFrom(clientPlayerEntity.getAttributes());
      }

      this.world.addEntity(clientPlayerEntity2);
      clientPlayerEntity2.input = new KeyboardInput(this.client.options);
      this.client.interactionManager.copyAbilities(clientPlayerEntity2);
      clientPlayerEntity2.setReducedDebugInfo(clientPlayerEntity.hasReducedDebugInfo());
      clientPlayerEntity2.setShowsDeathScreen(clientPlayerEntity.showsDeathScreen());
      clientPlayerEntity2.setLastDeathPos(commonPlayerSpawnInfo.lastDeathLocation());
      clientPlayerEntity2.setPortalCooldown(commonPlayerSpawnInfo.portalCooldown());
      clientPlayerEntity2.nauseaIntensity = clientPlayerEntity.nauseaIntensity;
      clientPlayerEntity2.lastNauseaIntensity = clientPlayerEntity.lastNauseaIntensity;
      if (this.client.currentScreen instanceof DeathScreen || this.client.currentScreen instanceof DeathScreen.TitleScreenConfirmScreen) {
         this.client.setScreen((Screen)null);
      }

      this.client.interactionManager.setGameModes(commonPlayerSpawnInfo.gameMode(), commonPlayerSpawnInfo.lastGameMode());
   }

   private DownloadingTerrainScreen.WorldEntryReason getWorldEntryReason(boolean dead, RegistryKey from, RegistryKey to) {
      DownloadingTerrainScreen.WorldEntryReason worldEntryReason = DownloadingTerrainScreen.WorldEntryReason.OTHER;
      if (!dead) {
         if (from != World.NETHER && to != World.NETHER) {
            if (from == World.END || to == World.END) {
               worldEntryReason = DownloadingTerrainScreen.WorldEntryReason.END_PORTAL;
            }
         } else {
            worldEntryReason = DownloadingTerrainScreen.WorldEntryReason.NETHER_PORTAL;
         }
      }

      return worldEntryReason;
   }

   public void onExplosion(ExplosionS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      Vec3d vec3d = packet.center();
      this.client.world.playSoundClient(vec3d.getX(), vec3d.getY(), vec3d.getZ(), (SoundEvent)packet.explosionSound().value(), SoundCategory.BLOCKS, 4.0F, (1.0F + (this.client.world.random.nextFloat() - this.client.world.random.nextFloat()) * 0.2F) * 0.7F, false);
      this.client.world.addParticleClient(packet.explosionParticle(), vec3d.getX(), vec3d.getY(), vec3d.getZ(), 1.0, 0.0, 0.0);
      Optional var10000 = packet.playerKnockback();
      ClientPlayerEntity var10001 = this.client.player;
      Objects.requireNonNull(var10001);
      var10000.ifPresent(var10001::addVelocityInternal);
   }

   public void onOpenHorseScreen(OpenHorseScreenS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      Entity entity = this.world.getEntityById(packet.getHorseId());
      if (entity instanceof AbstractHorseEntity abstractHorseEntity) {
         ClientPlayerEntity clientPlayerEntity = this.client.player;
         int i = packet.getSlotColumnCount();
         SimpleInventory simpleInventory = new SimpleInventory(AbstractHorseEntity.getInventorySize(i));
         HorseScreenHandler horseScreenHandler = new HorseScreenHandler(packet.getSyncId(), clientPlayerEntity.getInventory(), simpleInventory, abstractHorseEntity, i);
         clientPlayerEntity.currentScreenHandler = horseScreenHandler;
         this.client.setScreen(new HorseScreen(horseScreenHandler, clientPlayerEntity.getInventory(), abstractHorseEntity, i));
      }

   }

   public void onOpenScreen(OpenScreenS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      HandledScreens.open(packet.getScreenHandlerType(), this.client, packet.getSyncId(), packet.getName());
   }

   public void onScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      PlayerEntity playerEntity = this.client.player;
      ItemStack itemStack = packet.getStack();
      int i = packet.getSlot();
      this.client.getTutorialManager().onSlotUpdate(itemStack);
      Screen var7 = this.client.currentScreen;
      boolean bl;
      if (var7 instanceof CreativeInventoryScreen creativeInventoryScreen) {
         bl = !creativeInventoryScreen.isInventoryTabSelected();
      } else {
         bl = false;
      }

      if (packet.getSyncId() == 0) {
         if (PlayerScreenHandler.isInHotbar(i) && !itemStack.isEmpty()) {
            ItemStack itemStack2 = playerEntity.playerScreenHandler.getSlot(i).getStack();
            if (itemStack2.isEmpty() || itemStack2.getCount() < itemStack.getCount()) {
               itemStack.setBobbingAnimationTime(5);
            }
         }

         playerEntity.playerScreenHandler.setStackInSlot(i, packet.getRevision(), itemStack);
      } else if (packet.getSyncId() == playerEntity.currentScreenHandler.syncId && (packet.getSyncId() != 0 || !bl)) {
         playerEntity.currentScreenHandler.setStackInSlot(i, packet.getRevision(), itemStack);
      }

      if (this.client.currentScreen instanceof CreativeInventoryScreen) {
         playerEntity.playerScreenHandler.setReceivedStack(i, itemStack);
         playerEntity.playerScreenHandler.sendContentUpdates();
      }

   }

   public void onSetCursorItem(SetCursorItemS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.client.getTutorialManager().onSlotUpdate(packet.contents());
      if (!(this.client.currentScreen instanceof CreativeInventoryScreen)) {
         this.client.player.currentScreenHandler.setCursorStack(packet.contents());
      }

   }

   public void onSetPlayerInventory(SetPlayerInventoryS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.client.getTutorialManager().onSlotUpdate(packet.contents());
      this.client.player.getInventory().setStack(packet.slot(), packet.contents());
   }

   public void onInventory(InventoryS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      PlayerEntity playerEntity = this.client.player;
      if (packet.syncId() == 0) {
         playerEntity.playerScreenHandler.updateSlotStacks(packet.revision(), packet.contents(), packet.cursorStack());
      } else if (packet.syncId() == playerEntity.currentScreenHandler.syncId) {
         playerEntity.currentScreenHandler.updateSlotStacks(packet.revision(), packet.contents(), packet.cursorStack());
      }

   }

   public void onSignEditorOpen(SignEditorOpenS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      BlockPos blockPos = packet.getPos();
      BlockEntity var4 = this.world.getBlockEntity(blockPos);
      if (var4 instanceof SignBlockEntity signBlockEntity) {
         this.client.player.openEditSignScreen(signBlockEntity, packet.isFront());
      } else {
         LOGGER.warn("Ignoring openTextEdit on an invalid entity: {} at pos {}", this.world.getBlockEntity(blockPos), blockPos);
      }

   }

   public void onBlockEntityUpdate(BlockEntityUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      BlockPos blockPos = packet.getPos();
      this.client.world.getBlockEntity(blockPos, packet.getBlockEntityType()).ifPresent((blockEntity) -> {
         ErrorReporter.Logging logging = new ErrorReporter.Logging(blockEntity.getReporterContext(), LOGGER);

         try {
            blockEntity.read(NbtReadView.create(logging, this.combinedDynamicRegistries, packet.getNbt()));
         } catch (Throwable var7) {
            try {
               logging.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }

            throw var7;
         }

         logging.close();
         if (blockEntity instanceof CommandBlockBlockEntity && this.client.currentScreen instanceof CommandBlockScreen) {
            ((CommandBlockScreen)this.client.currentScreen).updateCommandBlock();
         }

      });
   }

   public void onScreenHandlerPropertyUpdate(ScreenHandlerPropertyUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      PlayerEntity playerEntity = this.client.player;
      if (playerEntity.currentScreenHandler != null && playerEntity.currentScreenHandler.syncId == packet.getSyncId()) {
         playerEntity.currentScreenHandler.setProperty(packet.getPropertyId(), packet.getValue());
      }

   }

   public void onEntityEquipmentUpdate(EntityEquipmentUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      Entity entity = this.world.getEntityById(packet.getEntityId());
      if (entity instanceof LivingEntity livingEntity) {
         packet.getEquipmentList().forEach((pair) -> {
            livingEntity.equipStack((EquipmentSlot)pair.getFirst(), (ItemStack)pair.getSecond());
         });
      }

   }

   public void onCloseScreen(CloseScreenS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.client.player.closeScreen();
   }

   public void onBlockEvent(BlockEventS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.client.world.addSyncedBlockEvent(packet.getPos(), packet.getBlock(), packet.getType(), packet.getData());
   }

   public void onBlockBreakingProgress(BlockBreakingProgressS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.client.world.setBlockBreakingInfo(packet.getEntityId(), packet.getPos(), packet.getProgress());
   }

   public void onGameStateChange(GameStateChangeS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      PlayerEntity playerEntity = this.client.player;
      GameStateChangeS2CPacket.Reason reason = packet.getReason();
      float f = packet.getValue();
      int i = MathHelper.floor(f + 0.5F);
      if (reason == GameStateChangeS2CPacket.NO_RESPAWN_BLOCK) {
         playerEntity.sendMessage(Text.translatable("block.minecraft.spawn.not_valid"), false);
      } else if (reason == GameStateChangeS2CPacket.RAIN_STARTED) {
         this.world.getLevelProperties().setRaining(true);
         this.world.setRainGradient(0.0F);
      } else if (reason == GameStateChangeS2CPacket.RAIN_STOPPED) {
         this.world.getLevelProperties().setRaining(false);
         this.world.setRainGradient(1.0F);
      } else if (reason == GameStateChangeS2CPacket.GAME_MODE_CHANGED) {
         this.client.interactionManager.setGameMode(GameMode.byIndex(i));
      } else if (reason == GameStateChangeS2CPacket.GAME_WON) {
         this.client.setScreen(new CreditsScreen(true, () -> {
            this.client.player.networkHandler.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.PERFORM_RESPAWN));
            this.client.setScreen((Screen)null);
         }));
      } else if (reason == GameStateChangeS2CPacket.DEMO_MESSAGE_SHOWN) {
         GameOptions gameOptions = this.client.options;
         Text text = null;
         if (f == 0.0F) {
            this.client.setScreen(new DemoScreen());
         } else if (f == 101.0F) {
            text = Text.translatable("demo.help.movement", gameOptions.forwardKey.getBoundKeyLocalizedText(), gameOptions.leftKey.getBoundKeyLocalizedText(), gameOptions.backKey.getBoundKeyLocalizedText(), gameOptions.rightKey.getBoundKeyLocalizedText());
         } else if (f == 102.0F) {
            text = Text.translatable("demo.help.jump", gameOptions.jumpKey.getBoundKeyLocalizedText());
         } else if (f == 103.0F) {
            text = Text.translatable("demo.help.inventory", gameOptions.inventoryKey.getBoundKeyLocalizedText());
         } else if (f == 104.0F) {
            text = Text.translatable("demo.day.6", gameOptions.screenshotKey.getBoundKeyLocalizedText());
         }

         if (text != null) {
            this.client.inGameHud.getChatHud().addMessage((Text)text);
            this.client.getNarratorManager().narrateSystemMessage(text);
         }
      } else if (reason == GameStateChangeS2CPacket.PROJECTILE_HIT_PLAYER) {
         this.world.playSound(playerEntity, playerEntity.getX(), playerEntity.getEyeY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_HIT_PLAYER, SoundCategory.PLAYERS, 0.18F, 0.45F);
      } else if (reason == GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED) {
         this.world.setRainGradient(f);
      } else if (reason == GameStateChangeS2CPacket.THUNDER_GRADIENT_CHANGED) {
         this.world.setThunderGradient(f);
      } else if (reason == GameStateChangeS2CPacket.PUFFERFISH_STING) {
         this.world.playSound(playerEntity, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_PUFFER_FISH_STING, SoundCategory.NEUTRAL, 1.0F, 1.0F);
      } else if (reason == GameStateChangeS2CPacket.ELDER_GUARDIAN_EFFECT) {
         this.world.addParticleClient(ParticleTypes.ELDER_GUARDIAN, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), 0.0, 0.0, 0.0);
         if (i == 1) {
            this.world.playSound(playerEntity, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.HOSTILE, 1.0F, 1.0F);
         }
      } else if (reason == GameStateChangeS2CPacket.IMMEDIATE_RESPAWN) {
         this.client.player.setShowsDeathScreen(f == 0.0F);
      } else if (reason == GameStateChangeS2CPacket.LIMITED_CRAFTING_TOGGLED) {
         this.client.player.setLimitedCraftingEnabled(f == 1.0F);
      } else if (reason == GameStateChangeS2CPacket.INITIAL_CHUNKS_COMING && this.worldLoadingState != null) {
         this.worldLoadingState.handleChunksComingPacket();
      }

   }

   private void startWorldLoading(ClientPlayerEntity player, ClientWorld world, DownloadingTerrainScreen.WorldEntryReason worldEntryReason) {
      this.worldLoadingState = new WorldLoadingState(player, world, this.client.worldRenderer);
      MinecraftClient var10000 = this.client;
      WorldLoadingState var10003 = this.worldLoadingState;
      Objects.requireNonNull(var10003);
      var10000.setScreen(new DownloadingTerrainScreen(var10003::isReady, worldEntryReason));
   }

   public void onMapUpdate(MapUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      MapIdComponent mapIdComponent = packet.mapId();
      MapState mapState = this.client.world.getMapState(mapIdComponent);
      if (mapState == null) {
         mapState = MapState.of(packet.scale(), packet.locked(), this.client.world.getRegistryKey());
         this.client.world.putClientsideMapState(mapIdComponent, mapState);
      }

      packet.apply(mapState);
      this.client.getMapTextureManager().setNeedsUpdate(mapIdComponent, mapState);
   }

   public void onWorldEvent(WorldEventS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      if (packet.isGlobal()) {
         this.client.world.syncGlobalEvent(packet.getEventId(), packet.getPos(), packet.getData());
      } else {
         this.client.world.syncWorldEvent(packet.getEventId(), packet.getPos(), packet.getData());
      }

   }

   public void onAdvancements(AdvancementUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.advancementHandler.onAdvancements(packet);
   }

   public void onSelectAdvancementTab(SelectAdvancementTabS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      Identifier identifier = packet.getTabId();
      if (identifier == null) {
         this.advancementHandler.selectTab((AdvancementEntry)null, false);
      } else {
         AdvancementEntry advancementEntry = this.advancementHandler.get(identifier);
         this.advancementHandler.selectTab(advancementEntry, false);
      }

   }

   public void onCommandTree(CommandTreeS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.commandDispatcher = new CommandDispatcher(packet.getCommandTree(CommandRegistryAccess.of(this.combinedDynamicRegistries, this.enabledFeatures), COMMAND_NODE_FACTORY));
   }

   public void onStopSound(StopSoundS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.client.getSoundManager().stopSounds(packet.getSoundId(), packet.getCategory());
   }

   public void onCommandSuggestions(CommandSuggestionsS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.commandSource.onCommandSuggestions(packet.id(), packet.getSuggestions());
   }

   public void onSynchronizeRecipes(SynchronizeRecipesS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.recipeManager = new ClientRecipeManager(packet.itemSets(), packet.stonecutterRecipes());
   }

   public void onLookAt(LookAtS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      Vec3d vec3d = packet.getTargetPosition(this.world);
      if (vec3d != null) {
         this.client.player.lookAt(packet.getSelfAnchor(), vec3d);
      }

   }

   public void onNbtQueryResponse(NbtQueryResponseS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      if (!this.dataQueryHandler.handleQueryResponse(packet.getTransactionId(), packet.getNbt())) {
         LOGGER.debug("Got unhandled response to tag query {}", packet.getTransactionId());
      }

   }

   public void onStatistics(StatisticsS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      ObjectIterator var2 = packet.stats().object2IntEntrySet().iterator();

      while(var2.hasNext()) {
         Object2IntMap.Entry entry = (Object2IntMap.Entry)var2.next();
         Stat stat = (Stat)entry.getKey();
         int i = entry.getIntValue();
         this.client.player.getStatHandler().setStat(this.client.player, stat, i);
      }

      Screen var7 = this.client.currentScreen;
      if (var7 instanceof StatsScreen statsScreen) {
         statsScreen.onStatsReady();
      }

   }

   public void onRecipeBookAdd(RecipeBookAddS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      ClientRecipeBook clientRecipeBook = this.client.player.getRecipeBook();
      if (packet.replace()) {
         clientRecipeBook.clear();
      }

      Iterator var3 = packet.entries().iterator();

      while(var3.hasNext()) {
         RecipeBookAddS2CPacket.Entry entry = (RecipeBookAddS2CPacket.Entry)var3.next();
         clientRecipeBook.add(entry.contents());
         if (entry.isHighlighted()) {
            clientRecipeBook.markHighlighted(entry.contents().id());
         }

         if (entry.shouldShowNotification()) {
            RecipeToast.show(this.client.getToastManager(), entry.contents().display());
         }
      }

      this.refreshRecipeBook(clientRecipeBook);
   }

   public void onRecipeBookRemove(RecipeBookRemoveS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      ClientRecipeBook clientRecipeBook = this.client.player.getRecipeBook();
      Iterator var3 = packet.recipes().iterator();

      while(var3.hasNext()) {
         NetworkRecipeId networkRecipeId = (NetworkRecipeId)var3.next();
         clientRecipeBook.remove(networkRecipeId);
      }

      this.refreshRecipeBook(clientRecipeBook);
   }

   public void onRecipeBookSettings(RecipeBookSettingsS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      ClientRecipeBook clientRecipeBook = this.client.player.getRecipeBook();
      clientRecipeBook.setOptions(packet.bookSettings());
      this.refreshRecipeBook(clientRecipeBook);
   }

   private void refreshRecipeBook(ClientRecipeBook recipeBook) {
      recipeBook.refresh();
      this.searchManager.addRecipeOutputReloader(recipeBook, this.world);
      Screen var3 = this.client.currentScreen;
      if (var3 instanceof RecipeBookProvider recipeBookProvider) {
         recipeBookProvider.refreshRecipeBook();
      }

   }

   public void onEntityStatusEffect(EntityStatusEffectS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      Entity entity = this.world.getEntityById(packet.getEntityId());
      if (entity instanceof LivingEntity) {
         RegistryEntry registryEntry = packet.getEffectId();
         StatusEffectInstance statusEffectInstance = new StatusEffectInstance(registryEntry, packet.getDuration(), packet.getAmplifier(), packet.isAmbient(), packet.shouldShowParticles(), packet.shouldShowIcon(), (StatusEffectInstance)null);
         if (!packet.keepFading()) {
            statusEffectInstance.skipFading();
         }

         ((LivingEntity)entity).setStatusEffect(statusEffectInstance, (Entity)null);
      }
   }

   private Registry.PendingTagLoad startTagReload(RegistryKey registryRef, TagPacketSerializer.Serialized serialized) {
      Registry registry = this.combinedDynamicRegistries.getOrThrow(registryRef);
      return registry.startTagReload(serialized.toRegistryTags(registry));
   }

   public void onSynchronizeTags(SynchronizeTagsS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      List list = new ArrayList(packet.getGroups().size());
      boolean bl = this.connection.isLocal();
      packet.getGroups().forEach((registryRef, serialized) -> {
         if (!bl || SerializableRegistries.isSynced(registryRef)) {
            list.add(this.startTagReload(registryRef, serialized));
         }

      });
      list.forEach(Registry.PendingTagLoad::apply);
      this.fuelRegistry = FuelRegistry.createDefault(this.combinedDynamicRegistries, this.enabledFeatures);
      List list2 = List.copyOf(ItemGroups.getSearchGroup().getDisplayStacks());
      this.searchManager.addItemTagReloader(list2);
   }

   public void onEndCombat(EndCombatS2CPacket packet) {
   }

   public void onEnterCombat(EnterCombatS2CPacket packet) {
   }

   public void onDeathMessage(DeathMessageS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      Entity entity = this.world.getEntityById(packet.playerId());
      if (entity == this.client.player) {
         if (this.client.player.showsDeathScreen()) {
            this.client.setScreen(new DeathScreen(packet.message(), this.world.getLevelProperties().isHardcore()));
         } else {
            this.client.player.requestRespawn();
         }
      }

   }

   public void onDifficulty(DifficultyS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.worldProperties.setDifficulty(packet.difficulty());
      this.worldProperties.setDifficultyLocked(packet.difficultyLocked());
   }

   public void onSetCameraEntity(SetCameraEntityS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      Entity entity = packet.getEntity(this.world);
      if (entity != null) {
         this.client.setCameraEntity(entity);
      }

   }

   public void onWorldBorderInitialize(WorldBorderInitializeS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      WorldBorder worldBorder = this.world.getWorldBorder();
      worldBorder.setCenter(packet.getCenterX(), packet.getCenterZ());
      long l = packet.getSizeLerpTime();
      if (l > 0L) {
         worldBorder.interpolateSize(packet.getSize(), packet.getSizeLerpTarget(), l);
      } else {
         worldBorder.setSize(packet.getSizeLerpTarget());
      }

      worldBorder.setMaxRadius(packet.getMaxRadius());
      worldBorder.setWarningBlocks(packet.getWarningBlocks());
      worldBorder.setWarningTime(packet.getWarningTime());
   }

   public void onWorldBorderCenterChanged(WorldBorderCenterChangedS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.world.getWorldBorder().setCenter(packet.getCenterX(), packet.getCenterZ());
   }

   public void onWorldBorderInterpolateSize(WorldBorderInterpolateSizeS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.world.getWorldBorder().interpolateSize(packet.getSize(), packet.getSizeLerpTarget(), packet.getSizeLerpTime());
   }

   public void onWorldBorderSizeChanged(WorldBorderSizeChangedS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.world.getWorldBorder().setSize(packet.getSizeLerpTarget());
   }

   public void onWorldBorderWarningBlocksChanged(WorldBorderWarningBlocksChangedS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.world.getWorldBorder().setWarningBlocks(packet.getWarningBlocks());
   }

   public void onWorldBorderWarningTimeChanged(WorldBorderWarningTimeChangedS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.world.getWorldBorder().setWarningTime(packet.getWarningTime());
   }

   public void onTitleClear(ClearTitleS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.client.inGameHud.clearTitle();
      if (packet.shouldReset()) {
         this.client.inGameHud.setDefaultTitleFade();
      }

   }

   public void onServerMetadata(ServerMetadataS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      if (this.serverInfo != null) {
         this.serverInfo.label = packet.description();
         Optional var10000 = packet.favicon().map(ServerInfo::validateFavicon);
         ServerInfo var10001 = this.serverInfo;
         Objects.requireNonNull(var10001);
         var10000.ifPresent(var10001::setFavicon);
         ServerList.updateServerListEntry(this.serverInfo);
      }
   }

   public void onChatSuggestions(ChatSuggestionsS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.commandSource.onChatSuggestions(packet.action(), packet.entries());
   }

   public void onOverlayMessage(OverlayMessageS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.client.inGameHud.setOverlayMessage(packet.text(), false);
   }

   public void onTitle(TitleS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.client.inGameHud.setTitle(packet.text());
   }

   public void onSubtitle(SubtitleS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.client.inGameHud.setSubtitle(packet.text());
   }

   public void onTitleFade(TitleFadeS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.client.inGameHud.setTitleTicks(packet.getFadeInTicks(), packet.getStayTicks(), packet.getFadeOutTicks());
   }

   public void onPlayerListHeader(PlayerListHeaderS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.client.inGameHud.getPlayerListHud().setHeader(packet.header().getString().isEmpty() ? null : packet.header());
      this.client.inGameHud.getPlayerListHud().setFooter(packet.footer().getString().isEmpty() ? null : packet.footer());
   }

   public void onRemoveEntityStatusEffect(RemoveEntityStatusEffectS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      Entity var3 = packet.getEntity(this.world);
      if (var3 instanceof LivingEntity livingEntity) {
         livingEntity.removeStatusEffectInternal(packet.effect());
      }

   }

   public void onPlayerRemove(PlayerRemoveS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      Iterator var2 = packet.profileIds().iterator();

      while(var2.hasNext()) {
         UUID uUID = (UUID)var2.next();
         this.client.getSocialInteractionsManager().setPlayerOffline(uUID);
         PlayerListEntry playerListEntry = (PlayerListEntry)this.playerListEntries.remove(uUID);
         if (playerListEntry != null) {
            this.listedPlayerListEntries.remove(playerListEntry);
         }
      }

   }

   public void onPlayerList(PlayerListS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      Iterator var2 = packet.getPlayerAdditionEntries().iterator();

      PlayerListS2CPacket.Entry entry;
      PlayerListEntry playerListEntry;
      while(var2.hasNext()) {
         entry = (PlayerListS2CPacket.Entry)var2.next();
         playerListEntry = new PlayerListEntry((GameProfile)Objects.requireNonNull(entry.profile()), this.isSecureChatEnforced());
         if (this.playerListEntries.putIfAbsent(entry.profileId(), playerListEntry) == null) {
            this.client.getSocialInteractionsManager().setPlayerOnline(playerListEntry);
         }
      }

      var2 = packet.getEntries().iterator();

      while(true) {
         while(var2.hasNext()) {
            entry = (PlayerListS2CPacket.Entry)var2.next();
            playerListEntry = (PlayerListEntry)this.playerListEntries.get(entry.profileId());
            if (playerListEntry == null) {
               LOGGER.warn("Ignoring player info update for unknown player {} ({})", entry.profileId(), packet.getActions());
            } else {
               Iterator var5 = packet.getActions().iterator();

               while(var5.hasNext()) {
                  PlayerListS2CPacket.Action action = (PlayerListS2CPacket.Action)var5.next();
                  this.handlePlayerListAction(action, entry, playerListEntry);
               }
            }
         }

         return;
      }
   }

   private void handlePlayerListAction(PlayerListS2CPacket.Action action, PlayerListS2CPacket.Entry receivedEntry, PlayerListEntry currentEntry) {
      switch (action) {
         case INITIALIZE_CHAT:
            this.setPublicSession(receivedEntry, currentEntry);
            break;
         case UPDATE_GAME_MODE:
            if (currentEntry.getGameMode() != receivedEntry.gameMode() && this.client.player != null && this.client.player.getUuid().equals(receivedEntry.profileId())) {
               this.client.player.onGameModeChanged(receivedEntry.gameMode());
            }

            currentEntry.setGameMode(receivedEntry.gameMode());
            break;
         case UPDATE_LISTED:
            if (receivedEntry.listed()) {
               this.listedPlayerListEntries.add(currentEntry);
            } else {
               this.listedPlayerListEntries.remove(currentEntry);
            }
            break;
         case UPDATE_LATENCY:
            currentEntry.setLatency(receivedEntry.latency());
            break;
         case UPDATE_DISPLAY_NAME:
            currentEntry.setDisplayName(receivedEntry.displayName());
            break;
         case UPDATE_HAT:
            currentEntry.setShowHat(receivedEntry.showHat());
            break;
         case UPDATE_LIST_ORDER:
            currentEntry.setListOrder(receivedEntry.listOrder());
      }

   }

   private void setPublicSession(PlayerListS2CPacket.Entry receivedEntry, PlayerListEntry currentEntry) {
      GameProfile gameProfile = currentEntry.getProfile();
      SignatureVerifier signatureVerifier = this.client.getServicesSignatureVerifier();
      if (signatureVerifier == null) {
         LOGGER.warn("Ignoring chat session from {} due to missing Services public key", gameProfile.getName());
         currentEntry.resetSession(this.isSecureChatEnforced());
      } else {
         PublicPlayerSession.Serialized serialized = receivedEntry.chatSession();
         if (serialized != null) {
            try {
               PublicPlayerSession publicPlayerSession = serialized.toSession(gameProfile, signatureVerifier);
               currentEntry.setSession(publicPlayerSession);
            } catch (PlayerPublicKey.PublicKeyException var7) {
               LOGGER.error("Failed to validate profile key for player: '{}'", gameProfile.getName(), var7);
               currentEntry.resetSession(this.isSecureChatEnforced());
            }
         } else {
            currentEntry.resetSession(this.isSecureChatEnforced());
         }

      }
   }

   private boolean isSecureChatEnforced() {
      return this.client.providesProfileKeys() && this.secureChatEnforced;
   }

   public void onPlayerAbilities(PlayerAbilitiesS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      PlayerEntity playerEntity = this.client.player;
      playerEntity.getAbilities().flying = packet.isFlying();
      playerEntity.getAbilities().creativeMode = packet.isCreativeMode();
      playerEntity.getAbilities().invulnerable = packet.isInvulnerable();
      playerEntity.getAbilities().allowFlying = packet.allowFlying();
      playerEntity.getAbilities().setFlySpeed(packet.getFlySpeed());
      playerEntity.getAbilities().setWalkSpeed(packet.getWalkSpeed());
   }

   public void onPlaySound(PlaySoundS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.client.world.playSound(this.client.player, packet.getX(), packet.getY(), packet.getZ(), packet.getSound(), packet.getCategory(), packet.getVolume(), packet.getPitch(), packet.getSeed());
   }

   public void onPlaySoundFromEntity(PlaySoundFromEntityS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      Entity entity = this.world.getEntityById(packet.getEntityId());
      if (entity != null) {
         this.client.world.playSoundFromEntity(this.client.player, entity, packet.getSound(), packet.getCategory(), packet.getVolume(), packet.getPitch(), packet.getSeed());
      }
   }

   public void onBossBar(BossBarS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.client.inGameHud.getBossBarHud().handlePacket(packet);
   }

   public void onCooldownUpdate(CooldownUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      if (packet.cooldown() == 0) {
         this.client.player.getItemCooldownManager().remove(packet.cooldownGroup());
      } else {
         this.client.player.getItemCooldownManager().set(packet.cooldownGroup(), packet.cooldown());
      }

   }

   public void onVehicleMove(VehicleMoveS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      Entity entity = this.client.player.getRootVehicle();
      if (entity != this.client.player && entity.isLogicalSideForUpdatingMovement()) {
         Vec3d vec3d = packet.position();
         Vec3d vec3d2;
         if (entity.isInterpolating()) {
            vec3d2 = entity.getInterpolator().getLerpedPos();
         } else {
            vec3d2 = entity.getPos();
         }

         if (vec3d.distanceTo(vec3d2) > 9.999999747378752E-6) {
            if (entity.isInterpolating()) {
               entity.getInterpolator().clear();
            }

            entity.updatePositionAndAngles(vec3d.getX(), vec3d.getY(), vec3d.getZ(), packet.yaw(), packet.pitch());
         }

         this.connection.send(VehicleMoveC2SPacket.fromVehicle(entity));
      }

   }

   public void onOpenWrittenBook(OpenWrittenBookS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      ItemStack itemStack = this.client.player.getStackInHand(packet.getHand());
      BookScreen.Contents contents = BookScreen.Contents.create(itemStack);
      if (contents != null) {
         this.client.setScreen(new BookScreen(contents));
      }

   }

   public void onCustomPayload(CustomPayload payload) {
      if (payload instanceof DebugPathCustomPayload debugPathCustomPayload) {
         this.client.debugRenderer.pathfindingDebugRenderer.addPath(debugPathCustomPayload.entityId(), debugPathCustomPayload.path(), debugPathCustomPayload.maxNodeDistance());
      } else if (payload instanceof DebugNeighborsUpdateCustomPayload debugNeighborsUpdateCustomPayload) {
         this.client.debugRenderer.neighborUpdateDebugRenderer.addNeighborUpdate(debugNeighborsUpdateCustomPayload.time(), debugNeighborsUpdateCustomPayload.pos());
      } else if (payload instanceof DebugRedstoneUpdateOrderCustomPayload debugRedstoneUpdateOrderCustomPayload) {
         this.client.debugRenderer.redstoneUpdateOrderDebugRenderer.addUpdateOrder(debugRedstoneUpdateOrderCustomPayload);
      } else if (payload instanceof DebugStructuresCustomPayload debugStructuresCustomPayload) {
         this.client.debugRenderer.structureDebugRenderer.addStructure(debugStructuresCustomPayload.mainBB(), debugStructuresCustomPayload.pieces(), debugStructuresCustomPayload.dimension());
      } else if (payload instanceof DebugWorldgenAttemptCustomPayload debugWorldgenAttemptCustomPayload) {
         ((WorldGenAttemptDebugRenderer)this.client.debugRenderer.worldGenAttemptDebugRenderer).addBox(debugWorldgenAttemptCustomPayload.pos(), debugWorldgenAttemptCustomPayload.scale(), debugWorldgenAttemptCustomPayload.red(), debugWorldgenAttemptCustomPayload.green(), debugWorldgenAttemptCustomPayload.blue(), debugWorldgenAttemptCustomPayload.alpha());
      } else if (payload instanceof DebugPoiTicketCountCustomPayload debugPoiTicketCountCustomPayload) {
         this.client.debugRenderer.villageDebugRenderer.setFreeTicketCount(debugPoiTicketCountCustomPayload.pos(), debugPoiTicketCountCustomPayload.freeTicketCount());
      } else if (payload instanceof DebugPoiAddedCustomPayload debugPoiAddedCustomPayload) {
         VillageDebugRenderer.PointOfInterest pointOfInterest = new VillageDebugRenderer.PointOfInterest(debugPoiAddedCustomPayload.pos(), debugPoiAddedCustomPayload.poiType(), debugPoiAddedCustomPayload.freeTicketCount());
         this.client.debugRenderer.villageDebugRenderer.addPointOfInterest(pointOfInterest);
      } else if (payload instanceof DebugPoiRemovedCustomPayload debugPoiRemovedCustomPayload) {
         this.client.debugRenderer.villageDebugRenderer.removePointOfInterest(debugPoiRemovedCustomPayload.pos());
      } else if (payload instanceof DebugVillageSectionsCustomPayload debugVillageSectionsCustomPayload) {
         VillageSectionsDebugRenderer villageSectionsDebugRenderer = this.client.debugRenderer.villageSectionsDebugRenderer;
         Set var10000 = debugVillageSectionsCustomPayload.villageChunks();
         Objects.requireNonNull(villageSectionsDebugRenderer);
         var10000.forEach(villageSectionsDebugRenderer::addSection);
         var10000 = debugVillageSectionsCustomPayload.notVillageChunks();
         Objects.requireNonNull(villageSectionsDebugRenderer);
         var10000.forEach(villageSectionsDebugRenderer::removeSection);
      } else if (payload instanceof DebugGoalSelectorCustomPayload debugGoalSelectorCustomPayload) {
         this.client.debugRenderer.goalSelectorDebugRenderer.setGoalSelectorList(debugGoalSelectorCustomPayload.entityId(), debugGoalSelectorCustomPayload.pos(), debugGoalSelectorCustomPayload.goals());
      } else if (payload instanceof DebugBrainCustomPayload debugBrainCustomPayload) {
         this.client.debugRenderer.villageDebugRenderer.addBrain(debugBrainCustomPayload.brainDump());
      } else if (payload instanceof DebugBeeCustomPayload debugBeeCustomPayload) {
         this.client.debugRenderer.beeDebugRenderer.addBee(debugBeeCustomPayload.beeInfo());
      } else if (payload instanceof DebugHiveCustomPayload debugHiveCustomPayload) {
         this.client.debugRenderer.beeDebugRenderer.addHive(debugHiveCustomPayload.hiveInfo(), this.world.getTime());
      } else if (payload instanceof DebugGameTestAddMarkerCustomPayload debugGameTestAddMarkerCustomPayload) {
         this.client.debugRenderer.gameTestDebugRenderer.addMarker(debugGameTestAddMarkerCustomPayload.pos(), debugGameTestAddMarkerCustomPayload.color(), debugGameTestAddMarkerCustomPayload.text(), debugGameTestAddMarkerCustomPayload.durationMs());
      } else if (payload instanceof DebugGameTestClearCustomPayload) {
         this.client.debugRenderer.gameTestDebugRenderer.clear();
      } else if (payload instanceof DebugRaidsCustomPayload) {
         DebugRaidsCustomPayload debugRaidsCustomPayload = (DebugRaidsCustomPayload)payload;
         this.client.debugRenderer.raidCenterDebugRenderer.setRaidCenters(debugRaidsCustomPayload.raidCenters());
      } else if (payload instanceof DebugGameEventCustomPayload) {
         DebugGameEventCustomPayload debugGameEventCustomPayload = (DebugGameEventCustomPayload)payload;
         this.client.debugRenderer.gameEventDebugRenderer.addEvent(debugGameEventCustomPayload.gameEventType(), debugGameEventCustomPayload.pos());
      } else if (payload instanceof DebugGameEventListenersCustomPayload) {
         DebugGameEventListenersCustomPayload debugGameEventListenersCustomPayload = (DebugGameEventListenersCustomPayload)payload;
         this.client.debugRenderer.gameEventDebugRenderer.addListener(debugGameEventListenersCustomPayload.listenerPos(), debugGameEventListenersCustomPayload.listenerRange());
      } else if (payload instanceof DebugBreezeCustomPayload) {
         DebugBreezeCustomPayload debugBreezeCustomPayload = (DebugBreezeCustomPayload)payload;
         this.client.debugRenderer.breezeDebugRenderer.addBreezeDebugInfo(debugBreezeCustomPayload.breezeInfo());
      } else {
         this.warnOnUnknownPayload(payload);
      }

   }

   private void warnOnUnknownPayload(CustomPayload payload) {
      LOGGER.warn("Unknown custom packet payload: {}", payload.getId().id());
   }

   public void onScoreboardObjectiveUpdate(ScoreboardObjectiveUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      String string = packet.getName();
      if (packet.getMode() == 0) {
         this.scoreboard.addObjective(string, ScoreboardCriterion.DUMMY, packet.getDisplayName(), packet.getType(), false, (NumberFormat)packet.getNumberFormat().orElse((Object)null));
      } else {
         ScoreboardObjective scoreboardObjective = this.scoreboard.getNullableObjective(string);
         if (scoreboardObjective != null) {
            if (packet.getMode() == 1) {
               this.scoreboard.removeObjective(scoreboardObjective);
            } else if (packet.getMode() == 2) {
               scoreboardObjective.setRenderType(packet.getType());
               scoreboardObjective.setDisplayName(packet.getDisplayName());
               scoreboardObjective.setNumberFormat((NumberFormat)packet.getNumberFormat().orElse((Object)null));
            }
         }
      }

   }

   public void onScoreboardScoreUpdate(ScoreboardScoreUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      String string = packet.objectiveName();
      ScoreHolder scoreHolder = ScoreHolder.fromName(packet.scoreHolderName());
      ScoreboardObjective scoreboardObjective = this.scoreboard.getNullableObjective(string);
      if (scoreboardObjective != null) {
         ScoreAccess scoreAccess = this.scoreboard.getOrCreateScore(scoreHolder, scoreboardObjective, true);
         scoreAccess.setScore(packet.score());
         scoreAccess.setDisplayText((Text)packet.display().orElse((Object)null));
         scoreAccess.setNumberFormat((NumberFormat)packet.numberFormat().orElse((Object)null));
      } else {
         LOGGER.warn("Received packet for unknown scoreboard objective: {}", string);
      }

   }

   public void onScoreboardScoreReset(ScoreboardScoreResetS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      String string = packet.objectiveName();
      ScoreHolder scoreHolder = ScoreHolder.fromName(packet.scoreHolderName());
      if (string == null) {
         this.scoreboard.removeScores(scoreHolder);
      } else {
         ScoreboardObjective scoreboardObjective = this.scoreboard.getNullableObjective(string);
         if (scoreboardObjective != null) {
            this.scoreboard.removeScore(scoreHolder, scoreboardObjective);
         } else {
            LOGGER.warn("Received packet for unknown scoreboard objective: {}", string);
         }
      }

   }

   public void onScoreboardDisplay(ScoreboardDisplayS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      String string = packet.getName();
      ScoreboardObjective scoreboardObjective = string == null ? null : this.scoreboard.getNullableObjective(string);
      this.scoreboard.setObjectiveSlot(packet.getSlot(), scoreboardObjective);
   }

   public void onTeam(TeamS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      TeamS2CPacket.Operation operation = packet.getTeamOperation();
      Team team;
      if (operation == TeamS2CPacket.Operation.ADD) {
         team = this.scoreboard.addTeam(packet.getTeamName());
      } else {
         team = this.scoreboard.getTeam(packet.getTeamName());
         if (team == null) {
            LOGGER.warn("Received packet for unknown team {}: team action: {}, player action: {}", new Object[]{packet.getTeamName(), packet.getTeamOperation(), packet.getPlayerListOperation()});
            return;
         }
      }

      Optional optional = packet.getTeam();
      optional.ifPresent((teamx) -> {
         team.setDisplayName(teamx.getDisplayName());
         team.setColor(teamx.getColor());
         team.setFriendlyFlagsBitwise(teamx.getFriendlyFlagsBitwise());
         team.setNameTagVisibilityRule(teamx.getNameTagVisibilityRule());
         team.setCollisionRule(teamx.getCollisionRule());
         team.setPrefix(teamx.getPrefix());
         team.setSuffix(teamx.getSuffix());
      });
      TeamS2CPacket.Operation operation2 = packet.getPlayerListOperation();
      Iterator var6;
      String string;
      if (operation2 == TeamS2CPacket.Operation.ADD) {
         var6 = packet.getPlayerNames().iterator();

         while(var6.hasNext()) {
            string = (String)var6.next();
            this.scoreboard.addScoreHolderToTeam(string, team);
         }
      } else if (operation2 == TeamS2CPacket.Operation.REMOVE) {
         var6 = packet.getPlayerNames().iterator();

         while(var6.hasNext()) {
            string = (String)var6.next();
            this.scoreboard.removeScoreHolderFromTeam(string, team);
         }
      }

      if (operation == TeamS2CPacket.Operation.REMOVE) {
         this.scoreboard.removeTeam(team);
      }

   }

   public void onParticle(ParticleS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      if (packet.getCount() == 0) {
         double d = (double)(packet.getSpeed() * packet.getOffsetX());
         double e = (double)(packet.getSpeed() * packet.getOffsetY());
         double f = (double)(packet.getSpeed() * packet.getOffsetZ());

         try {
            this.world.addParticleClient(packet.getParameters(), packet.shouldForceSpawn(), packet.isImportant(), packet.getX(), packet.getY(), packet.getZ(), d, e, f);
         } catch (Throwable var17) {
            LOGGER.warn("Could not spawn particle effect {}", packet.getParameters());
         }
      } else {
         for(int i = 0; i < packet.getCount(); ++i) {
            double g = this.random.nextGaussian() * (double)packet.getOffsetX();
            double h = this.random.nextGaussian() * (double)packet.getOffsetY();
            double j = this.random.nextGaussian() * (double)packet.getOffsetZ();
            double k = this.random.nextGaussian() * (double)packet.getSpeed();
            double l = this.random.nextGaussian() * (double)packet.getSpeed();
            double m = this.random.nextGaussian() * (double)packet.getSpeed();

            try {
               this.world.addParticleClient(packet.getParameters(), packet.shouldForceSpawn(), packet.isImportant(), packet.getX() + g, packet.getY() + h, packet.getZ() + j, k, l, m);
            } catch (Throwable var16) {
               LOGGER.warn("Could not spawn particle effect {}", packet.getParameters());
               return;
            }
         }
      }

   }

   public void onEntityAttributes(EntityAttributesS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      Entity entity = this.world.getEntityById(packet.getEntityId());
      if (entity != null) {
         if (!(entity instanceof LivingEntity)) {
            throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + String.valueOf(entity) + ")");
         } else {
            AttributeContainer attributeContainer = ((LivingEntity)entity).getAttributes();
            Iterator var4 = packet.getEntries().iterator();

            while(true) {
               while(var4.hasNext()) {
                  EntityAttributesS2CPacket.Entry entry = (EntityAttributesS2CPacket.Entry)var4.next();
                  EntityAttributeInstance entityAttributeInstance = attributeContainer.getCustomInstance(entry.attribute());
                  if (entityAttributeInstance == null) {
                     LOGGER.warn("Entity {} does not have attribute {}", entity, entry.attribute().getIdAsString());
                  } else {
                     entityAttributeInstance.setBaseValue(entry.base());
                     entityAttributeInstance.clearModifiers();
                     Iterator var7 = entry.modifiers().iterator();

                     while(var7.hasNext()) {
                        EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)var7.next();
                        entityAttributeInstance.addTemporaryModifier(entityAttributeModifier);
                     }
                  }
               }

               return;
            }
         }
      }
   }

   public void onCraftFailedResponse(CraftFailedResponseS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      ScreenHandler screenHandler = this.client.player.currentScreenHandler;
      if (screenHandler.syncId == packet.syncId()) {
         Screen var4 = this.client.currentScreen;
         if (var4 instanceof RecipeBookProvider) {
            RecipeBookProvider recipeBookProvider = (RecipeBookProvider)var4;
            recipeBookProvider.onCraftFailed(packet.recipeDisplay());
         }

      }
   }

   public void onLightUpdate(LightUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      int i = packet.getChunkX();
      int j = packet.getChunkZ();
      LightData lightData = packet.getData();
      this.world.enqueueChunkUpdate(() -> {
         this.readLightData(i, j, lightData, true);
      });
   }

   private void readLightData(int x, int z, LightData data, boolean scheduleBlockRenders) {
      LightingProvider lightingProvider = this.world.getChunkManager().getLightingProvider();
      BitSet bitSet = data.getInitedSky();
      BitSet bitSet2 = data.getUninitedSky();
      Iterator iterator = data.getSkyNibbles().iterator();
      this.updateLighting(x, z, lightingProvider, LightType.SKY, bitSet, bitSet2, iterator, scheduleBlockRenders);
      BitSet bitSet3 = data.getInitedBlock();
      BitSet bitSet4 = data.getUninitedBlock();
      Iterator iterator2 = data.getBlockNibbles().iterator();
      this.updateLighting(x, z, lightingProvider, LightType.BLOCK, bitSet3, bitSet4, iterator2, scheduleBlockRenders);
      lightingProvider.setColumnEnabled(new ChunkPos(x, z), true);
   }

   public void onSetTradeOffers(SetTradeOffersS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      ScreenHandler screenHandler = this.client.player.currentScreenHandler;
      if (packet.getSyncId() == screenHandler.syncId && screenHandler instanceof MerchantScreenHandler merchantScreenHandler) {
         merchantScreenHandler.setOffers(packet.getOffers());
         merchantScreenHandler.setExperienceFromServer(packet.getExperience());
         merchantScreenHandler.setLevelProgress(packet.getLevelProgress());
         merchantScreenHandler.setLeveled(packet.isLeveled());
         merchantScreenHandler.setCanRefreshTrades(packet.isRefreshable());
      }

   }

   public void onChunkLoadDistance(ChunkLoadDistanceS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.chunkLoadDistance = packet.getDistance();
      this.client.options.setServerViewDistance(this.chunkLoadDistance);
      this.world.getChunkManager().updateLoadDistance(packet.getDistance());
   }

   public void onSimulationDistance(SimulationDistanceS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.simulationDistance = packet.simulationDistance();
      this.world.setSimulationDistance(this.simulationDistance);
   }

   public void onChunkRenderDistanceCenter(ChunkRenderDistanceCenterS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.world.getChunkManager().setChunkMapCenter(packet.getChunkX(), packet.getChunkZ());
   }

   public void onPlayerActionResponse(PlayerActionResponseS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.world.handlePlayerActionResponse(packet.sequence());
   }

   public void onBundle(BundleS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      Iterator var2 = packet.getPackets().iterator();

      while(var2.hasNext()) {
         Packet packet2 = (Packet)var2.next();
         packet2.apply(this);
      }

   }

   public void onProjectilePower(ProjectilePowerS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      Entity entity = this.world.getEntityById(packet.getEntityId());
      if (entity instanceof ExplosiveProjectileEntity explosiveProjectileEntity) {
         explosiveProjectileEntity.accelerationPower = packet.getAccelerationPower();
      }

   }

   public void onStartChunkSend(StartChunkSendS2CPacket packet) {
      this.chunkBatchSizeCalculator.onStartChunkSend();
   }

   public void onChunkSent(ChunkSentS2CPacket packet) {
      this.chunkBatchSizeCalculator.onChunkSent(packet.batchSize());
      this.sendPacket(new AcknowledgeChunksC2SPacket(this.chunkBatchSizeCalculator.getDesiredChunksPerTick()));
   }

   public void onDebugSample(DebugSampleS2CPacket packet) {
      this.client.getDebugHud().set(packet.sample(), packet.debugSampleType());
   }

   public void onPingResult(PingResultS2CPacket packet) {
      this.pingMeasurer.onPingResult(packet);
   }

   public void onTestInstanceBlockStatus(TestInstanceBlockStatusS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      Screen var3 = this.client.currentScreen;
      if (var3 instanceof TestInstanceBlockScreen testInstanceBlockScreen) {
         testInstanceBlockScreen.handleStatus(packet.status(), packet.size());
      }

   }

   public void onWaypoint(WaypointS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      packet.apply((TrackedWaypointHandler)this.waypointHandler);
   }

   private void updateLighting(int chunkX, int chunkZ, LightingProvider provider, LightType type, BitSet inited, BitSet uninited, Iterator nibbles, boolean scheduleBlockRenders) {
      for(int i = 0; i < provider.getHeight(); ++i) {
         int j = provider.getBottomY() + i;
         boolean bl = inited.get(i);
         boolean bl2 = uninited.get(i);
         if (bl || bl2) {
            provider.enqueueSectionData(type, ChunkSectionPos.from(chunkX, j, chunkZ), bl ? new ChunkNibbleArray((byte[])((byte[])nibbles.next()).clone()) : new ChunkNibbleArray());
            if (scheduleBlockRenders) {
               this.world.scheduleBlockRenders(chunkX, j, chunkZ);
            }
         }
      }

   }

   public ClientConnection getConnection() {
      return this.connection;
   }

   public boolean isConnectionOpen() {
      return this.connection.isOpen() && !this.worldCleared;
   }

   public Collection getListedPlayerListEntries() {
      return this.listedPlayerListEntries;
   }

   public Collection getPlayerList() {
      return this.playerListEntries.values();
   }

   public Collection getPlayerUuids() {
      return this.playerListEntries.keySet();
   }

   @Nullable
   public PlayerListEntry getPlayerListEntry(UUID uuid) {
      return (PlayerListEntry)this.playerListEntries.get(uuid);
   }

   @Nullable
   public PlayerListEntry getPlayerListEntry(String profileName) {
      Iterator var2 = this.playerListEntries.values().iterator();

      PlayerListEntry playerListEntry;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         playerListEntry = (PlayerListEntry)var2.next();
      } while(!playerListEntry.getProfile().getName().equals(profileName));

      return playerListEntry;
   }

   public GameProfile getProfile() {
      return this.profile;
   }

   public ClientAdvancementManager getAdvancementHandler() {
      return this.advancementHandler;
   }

   public CommandDispatcher getCommandDispatcher() {
      return this.commandDispatcher;
   }

   public ClientWorld getWorld() {
      return this.world;
   }

   public DataQueryHandler getDataQueryHandler() {
      return this.dataQueryHandler;
   }

   public UUID getSessionId() {
      return this.sessionId;
   }

   public Set getWorldKeys() {
      return this.worldKeys;
   }

   public DynamicRegistryManager.Immutable getRegistryManager() {
      return this.combinedDynamicRegistries;
   }

   public void acknowledge(MessageSignatureData signature, boolean displayed) {
      if (this.lastSeenMessagesCollector.add(signature, displayed) && this.lastSeenMessagesCollector.getMessageCount() > 64) {
         this.sendAcknowledgment();
      }

   }

   private void sendAcknowledgment() {
      int i = this.lastSeenMessagesCollector.resetMessageCount();
      if (i > 0) {
         this.sendPacket(new MessageAcknowledgmentC2SPacket(i));
      }

   }

   public void sendChatMessage(String content) {
      Instant instant = Instant.now();
      long l = NetworkEncryptionUtils.SecureRandomUtil.nextLong();
      LastSeenMessagesCollector.LastSeenMessages lastSeenMessages = this.lastSeenMessagesCollector.collect();
      MessageSignatureData messageSignatureData = this.messagePacker.pack(new MessageBody(content, instant, l, lastSeenMessages.lastSeen()));
      this.sendPacket(new ChatMessageC2SPacket(content, instant, l, messageSignatureData, lastSeenMessages.update()));
   }

   public void sendChatCommand(String command) {
      SignedArgumentList signedArgumentList = SignedArgumentList.of(this.commandDispatcher.parse(command, this.commandSource));
      if (signedArgumentList.arguments().isEmpty()) {
         this.sendPacket(new CommandExecutionC2SPacket(command));
      } else {
         Instant instant = Instant.now();
         long l = NetworkEncryptionUtils.SecureRandomUtil.nextLong();
         LastSeenMessagesCollector.LastSeenMessages lastSeenMessages = this.lastSeenMessagesCollector.collect();
         ArgumentSignatureDataMap argumentSignatureDataMap = ArgumentSignatureDataMap.sign(signedArgumentList, (value) -> {
            MessageBody messageBody = new MessageBody(value, instant, l, lastSeenMessages.lastSeen());
            return this.messagePacker.pack(messageBody);
         });
         this.sendPacket(new ChatCommandSignedC2SPacket(command, instant, l, argumentSignatureDataMap, lastSeenMessages.update()));
      }
   }

   public void runClickEventCommand(String command, @Nullable Screen afterActionScreen) {
      switch (this.parseCommand(command).ordinal()) {
         case 0:
            this.sendPacket(new CommandExecutionC2SPacket(command));
            this.client.setScreen(afterActionScreen);
            break;
         case 1:
            this.openConfirmCommandScreen(command, "multiplayer.confirm_command.parse_errors", afterActionScreen);
            break;
         case 2:
            LOGGER.error("Not allowed to run command with signed argument from click event: '{}'", command);
            break;
         case 3:
            this.openConfirmCommandScreen(command, "multiplayer.confirm_command.permissions_required", afterActionScreen);
      }

   }

   private CommandRunResult parseCommand(String command) {
      ParseResults parseResults = this.commandDispatcher.parse(command, this.commandSource);
      if (!validate(parseResults)) {
         return ClientPlayNetworkHandler.CommandRunResult.PARSE_ERRORS;
      } else if (SignedArgumentList.isNotEmpty(parseResults)) {
         return ClientPlayNetworkHandler.CommandRunResult.SIGNATURE_REQUIRED;
      } else {
         ParseResults parseResults2 = this.commandDispatcher.parse(command, this.restrictedCommandSource);
         return !validate(parseResults2) ? ClientPlayNetworkHandler.CommandRunResult.PERMISSIONS_REQUIRED : ClientPlayNetworkHandler.CommandRunResult.NO_ISSUES;
      }
   }

   private static boolean validate(ParseResults parseResults) {
      return !parseResults.getReader().canRead() && parseResults.getExceptions().isEmpty() && parseResults.getContext().getLastChild().getCommand() != null;
   }

   private void openConfirmCommandScreen(String command, String message, @Nullable Screen screenAfterRun) {
      Screen screen = this.client.currentScreen;
      this.client.setScreen(new ConfirmScreen((confirmed) -> {
         if (confirmed) {
            this.sendPacket(new CommandExecutionC2SPacket(command));
         }

         if (confirmed) {
            this.client.setScreen(screenAfterRun);
         } else {
            this.client.setScreen(screen);
         }

      }, CONFIRM_COMMAND_TITLE_TEXT, Text.translatable(message, Text.literal(command).formatted(Formatting.YELLOW))));
   }

   public void syncOptions(SyncedClientOptions syncedOptions) {
      if (!syncedOptions.equals(this.syncedOptions)) {
         this.sendPacket(new ClientOptionsC2SPacket(syncedOptions));
         this.syncedOptions = syncedOptions;
      }

   }

   public void tick() {
      if (this.session != null && this.client.getProfileKeys().isExpired()) {
         this.fetchProfileKey();
      }

      if (this.profileKeyPairFuture != null && this.profileKeyPairFuture.isDone()) {
         ((Optional)this.profileKeyPairFuture.join()).ifPresent(this::updateKeyPair);
         this.profileKeyPairFuture = null;
      }

      this.sendQueuedPackets();
      if (this.client.getDebugHud().shouldShowPacketSizeAndPingCharts()) {
         this.pingMeasurer.ping();
      }

      this.debugSampleSubscriber.tick();
      this.worldSession.tick();
      if (this.worldLoadingState != null) {
         this.worldLoadingState.tick();
         if (this.worldLoadingState.isReady() && !this.client.player.isLoaded()) {
            this.connection.send(new PlayerLoadedC2SPacket());
            this.client.player.setLoaded(true);
         }
      }

   }

   public void fetchProfileKey() {
      this.profileKeyPairFuture = this.client.getProfileKeys().fetchKeyPair();
   }

   private void updateKeyPair(PlayerKeyPair keyPair) {
      if (this.client.uuidEquals(this.profile.getId())) {
         if (this.session == null || !this.session.keyPair().equals(keyPair)) {
            this.session = ClientPlayerSession.create(keyPair);
            this.messagePacker = this.session.createPacker(this.profile.getId());
            this.sendPacket(new PlayerSessionC2SPacket(this.session.toPublicSession().toSerialized()));
         }
      }
   }

   protected DialogNetworkAccess createDialogNetworkAccess() {
      return new DialogNetworkAccess() {
         public void disconnect(Text reason) {
            ClientPlayNetworkHandler.this.getConnection().disconnect(reason);
         }

         public void runClickEventCommand(String command, @Nullable Screen afterActionScreen) {
            ClientPlayNetworkHandler.this.runClickEventCommand(command, afterActionScreen);
         }

         public void showDialog(RegistryEntry dialog, @Nullable Screen afterActionScreen) {
            ClientPlayNetworkHandler.this.showDialog(dialog, this, afterActionScreen);
         }

         public void sendCustomClickActionPacket(Identifier id, Optional payload) {
            ClientPlayNetworkHandler.this.sendPacket(new CustomClickActionC2SPacket(id, payload));
         }

         public ServerLinks getServerLinks() {
            return ClientPlayNetworkHandler.this.getServerLinks();
         }
      };
   }

   @Nullable
   public ServerInfo getServerInfo() {
      return this.serverInfo;
   }

   public FeatureSet getEnabledFeatures() {
      return this.enabledFeatures;
   }

   public boolean hasFeature(FeatureSet feature) {
      return feature.isSubsetOf(this.getEnabledFeatures());
   }

   public Scoreboard getScoreboard() {
      return this.scoreboard;
   }

   public BrewingRecipeRegistry getBrewingRecipeRegistry() {
      return this.brewingRecipeRegistry;
   }

   public FuelRegistry getFuelRegistry() {
      return this.fuelRegistry;
   }

   public void refreshSearchManager() {
      this.searchManager.refresh();
   }

   public SearchManager getSearchManager() {
      return this.searchManager;
   }

   public void registerForCleaning(DataCache dataCache) {
      this.cachedData.add(new WeakReference(dataCache));
   }

   public ComponentChangesHash.ComponentHasher getComponentHasher() {
      return this.componentHasher;
   }

   public ClientWaypointHandler getWaypointHandler() {
      return this.waypointHandler;
   }

   @Environment(EnvType.CLIENT)
   private static enum CommandRunResult {
      NO_ISSUES,
      PARSE_ERRORS,
      SIGNATURE_REQUIRED,
      PERMISSIONS_REQUIRED;

      // $FF: synthetic method
      private static CommandRunResult[] method_71931() {
         return new CommandRunResult[]{NO_ISSUES, PARSE_ERRORS, SIGNATURE_REQUIRED, PERMISSIONS_REQUIRED};
      }
   }
}
