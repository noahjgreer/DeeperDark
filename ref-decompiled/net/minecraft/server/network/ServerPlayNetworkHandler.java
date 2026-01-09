package net.minecraft.server.network;

import com.google.common.collect.Lists;
import com.google.common.primitives.Floats;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.CrafterBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.entity.TestBlockEntity;
import net.minecraft.block.entity.TestInstanceBlockEntity;
import net.minecraft.command.argument.SignedArgumentList;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WritableBookContentComponent;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.RideableInventory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.entity.player.PlayerPosition;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.encryption.PublicPlayerSession;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.message.AcknowledgmentValidator;
import net.minecraft.network.message.ArgumentSignatureDataMap;
import net.minecraft.network.message.ChatVisibility;
import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageChain;
import net.minecraft.network.message.MessageChainTaskQueue;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.MessageSignatureStorage;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedCommandArguments;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.play.AcknowledgeChunksC2SPacket;
import net.minecraft.network.packet.c2s.play.AcknowledgeReconfigurationC2SPacket;
import net.minecraft.network.packet.c2s.play.AdvancementTabC2SPacket;
import net.minecraft.network.packet.c2s.play.BoatPaddleStateC2SPacket;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.BundleItemSelectedC2SPacket;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.network.packet.c2s.play.ChangeGameModeC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatCommandSignedC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientTickEndC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.c2s.play.CraftRequestC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.DebugSampleSubscriptionC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.JigsawGeneratingC2SPacket;
import net.minecraft.network.packet.c2s.play.MessageAcknowledgmentC2SPacket;
import net.minecraft.network.packet.c2s.play.PickItemFromBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PickItemFromEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerLoadedC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerSessionC2SPacket;
import net.minecraft.network.packet.c2s.play.QueryBlockNbtC2SPacket;
import net.minecraft.network.packet.c2s.play.QueryEntityNbtC2SPacket;
import net.minecraft.network.packet.c2s.play.RecipeBookDataC2SPacket;
import net.minecraft.network.packet.c2s.play.RecipeCategoryOptionsC2SPacket;
import net.minecraft.network.packet.c2s.play.RenameItemC2SPacket;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.network.packet.c2s.play.SetTestBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.SlotChangedStateC2SPacket;
import net.minecraft.network.packet.c2s.play.SpectatorTeleportC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.c2s.play.TestInstanceBlockActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateBeaconC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockMinecartC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyLockC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateJigsawC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateStructureBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.CraftFailedResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.EnterReconfigurationS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.NbtQueryResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.ProfilelessChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.TestInstanceBlockStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import net.minecraft.network.state.ConfigurationStates;
import net.minecraft.network.state.PlayStateFactories;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.CrafterScreenHandler;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.sync.ItemStackHash;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.GameModeCommand;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.test.TestInstance;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Cooldown;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Util;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ServerPlayNetworkHandler extends ServerCommonNetworkHandler implements PlayStateFactories.PacketCodecModifierContext, ServerPlayPacketListener, PlayerAssociatedNetworkHandler, TickablePacketListener {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final int DEFAULT_SEQUENCE = -1;
   private static final int MAX_PENDING_ACKNOWLEDGMENTS = 4096;
   private static final int field_49027 = 80;
   private static final Text CHAT_VALIDATION_FAILED_TEXT = Text.translatable("multiplayer.disconnect.chat_validation_failed");
   private static final Text INVALID_COMMAND_SIGNATURE_TEXT;
   private static final int field_49778 = 1000;
   public ServerPlayerEntity player;
   public final ChunkDataSender chunkDataSender;
   private int ticks;
   private int sequence = -1;
   private final Cooldown messageCooldown = new Cooldown(20, 200);
   private final Cooldown creativeItemDropCooldown = new Cooldown(20, 1480);
   private double lastTickX;
   private double lastTickY;
   private double lastTickZ;
   private double updatedX;
   private double updatedY;
   private double updatedZ;
   @Nullable
   private Entity topmostRiddenEntity;
   private double lastTickRiddenX;
   private double lastTickRiddenY;
   private double lastTickRiddenZ;
   private double updatedRiddenX;
   private double updatedRiddenY;
   private double updatedRiddenZ;
   @Nullable
   private Vec3d requestedTeleportPos;
   private int requestedTeleportId;
   private int lastTeleportCheckTicks;
   private boolean floating;
   private int floatingTicks;
   private boolean vehicleFloating;
   private int vehicleFloatingTicks;
   private int movePacketsCount;
   private int lastTickMovePacketsCount;
   private boolean movedThisTick;
   @Nullable
   private PublicPlayerSession session;
   private MessageChain.Unpacker messageUnpacker;
   private final AcknowledgmentValidator acknowledgmentValidator = new AcknowledgmentValidator(20);
   private int globalChatMessageIndex;
   private final MessageSignatureStorage signatureStorage = MessageSignatureStorage.create();
   private final MessageChainTaskQueue messageChainTaskQueue;
   private boolean requestedReconfiguration;

   public ServerPlayNetworkHandler(MinecraftServer server, ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData) {
      super(server, connection, clientData);
      this.chunkDataSender = new ChunkDataSender(connection.isLocal());
      this.player = player;
      player.networkHandler = this;
      player.getTextStream().onConnect();
      UUID var10001 = player.getUuid();
      Objects.requireNonNull(server);
      this.messageUnpacker = MessageChain.Unpacker.unsigned(var10001, server::shouldEnforceSecureProfile);
      this.messageChainTaskQueue = new MessageChainTaskQueue(server);
   }

   public void tick() {
      if (this.sequence > -1) {
         this.sendPacket(new PlayerActionResponseS2CPacket(this.sequence));
         this.sequence = -1;
      }

      this.syncWithPlayerPosition();
      this.player.lastX = this.player.getX();
      this.player.lastY = this.player.getY();
      this.player.lastZ = this.player.getZ();
      this.player.playerTick();
      this.player.updatePositionAndAngles(this.lastTickX, this.lastTickY, this.lastTickZ, this.player.getYaw(), this.player.getPitch());
      ++this.ticks;
      this.lastTickMovePacketsCount = this.movePacketsCount;
      if (this.floating && !this.player.isSleeping() && !this.player.hasVehicle() && !this.player.isDead()) {
         if (++this.floatingTicks > this.getMaxAllowedFloatingTicks(this.player)) {
            LOGGER.warn("{} was kicked for floating too long!", this.player.getName().getString());
            this.disconnect(Text.translatable("multiplayer.disconnect.flying"));
            return;
         }
      } else {
         this.floating = false;
         this.floatingTicks = 0;
      }

      this.topmostRiddenEntity = this.player.getRootVehicle();
      if (this.topmostRiddenEntity != this.player && this.topmostRiddenEntity.getControllingPassenger() == this.player) {
         this.lastTickRiddenX = this.topmostRiddenEntity.getX();
         this.lastTickRiddenY = this.topmostRiddenEntity.getY();
         this.lastTickRiddenZ = this.topmostRiddenEntity.getZ();
         this.updatedRiddenX = this.topmostRiddenEntity.getX();
         this.updatedRiddenY = this.topmostRiddenEntity.getY();
         this.updatedRiddenZ = this.topmostRiddenEntity.getZ();
         if (this.vehicleFloating && this.topmostRiddenEntity.getControllingPassenger() == this.player) {
            if (++this.vehicleFloatingTicks > this.getMaxAllowedFloatingTicks(this.topmostRiddenEntity)) {
               LOGGER.warn("{} was kicked for floating a vehicle too long!", this.player.getName().getString());
               this.disconnect(Text.translatable("multiplayer.disconnect.flying"));
               return;
            }
         } else {
            this.vehicleFloating = false;
            this.vehicleFloatingTicks = 0;
         }
      } else {
         this.topmostRiddenEntity = null;
         this.vehicleFloating = false;
         this.vehicleFloatingTicks = 0;
      }

      this.baseTick();
      this.messageCooldown.tick();
      this.creativeItemDropCooldown.tick();
      if (this.player.getLastActionTime() > 0L && this.server.getPlayerIdleTimeout() > 0 && Util.getMeasuringTimeMs() - this.player.getLastActionTime() > (long)this.server.getPlayerIdleTimeout() * 1000L * 60L) {
         this.disconnect(Text.translatable("multiplayer.disconnect.idling"));
      }

   }

   private int getMaxAllowedFloatingTicks(Entity vehicle) {
      double d = vehicle.getFinalGravity();
      if (d < 9.999999747378752E-6) {
         return Integer.MAX_VALUE;
      } else {
         double e = 0.08 / d;
         return MathHelper.ceil(80.0 * Math.max(e, 1.0));
      }
   }

   public void syncWithPlayerPosition() {
      this.lastTickX = this.player.getX();
      this.lastTickY = this.player.getY();
      this.lastTickZ = this.player.getZ();
      this.updatedX = this.player.getX();
      this.updatedY = this.player.getY();
      this.updatedZ = this.player.getZ();
   }

   public boolean isConnectionOpen() {
      return this.connection.isOpen() && !this.requestedReconfiguration;
   }

   public boolean accepts(Packet packet) {
      if (super.accepts(packet)) {
         return true;
      } else {
         return this.requestedReconfiguration && this.connection.isOpen() && packet instanceof AcknowledgeReconfigurationC2SPacket;
      }
   }

   protected GameProfile getProfile() {
      return this.player.getGameProfile();
   }

   private CompletableFuture filterText(Object text, BiFunction filterer) {
      return ((CompletableFuture)filterer.apply(this.player.getTextStream(), text)).thenApply((filtered) -> {
         if (!this.isConnectionOpen()) {
            LOGGER.debug("Ignoring packet due to disconnection");
            throw new CancellationException("disconnected");
         } else {
            return filtered;
         }
      });
   }

   private CompletableFuture filterText(String text) {
      return this.filterText(text, TextStream::filterText);
   }

   private CompletableFuture filterTexts(List texts) {
      return this.filterText(texts, TextStream::filterTexts);
   }

   public void onPlayerInput(PlayerInputC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      this.player.setPlayerInput(packet.input());
      if (this.player.isLoaded()) {
         this.player.updateLastActionTime();
         this.player.setSneaking(packet.input().sneak());
      }

   }

   private static boolean isMovementInvalid(double x, double y, double z, float yaw, float pitch) {
      return Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z) || !Floats.isFinite(pitch) || !Floats.isFinite(yaw);
   }

   private static double clampHorizontal(double d) {
      return MathHelper.clamp(d, -3.0E7, 3.0E7);
   }

   private static double clampVertical(double d) {
      return MathHelper.clamp(d, -2.0E7, 2.0E7);
   }

   public void onVehicleMove(VehicleMoveC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      if (isMovementInvalid(packet.position().getX(), packet.position().getY(), packet.position().getZ(), packet.yaw(), packet.pitch())) {
         this.disconnect(Text.translatable("multiplayer.disconnect.invalid_vehicle_movement"));
      } else if (!this.handlePendingTeleport() && this.player.isLoaded()) {
         Entity entity = this.player.getRootVehicle();
         if (entity != this.player && entity.getControllingPassenger() == this.player && entity == this.topmostRiddenEntity) {
            ServerWorld serverWorld = this.player.getWorld();
            double d = entity.getX();
            double e = entity.getY();
            double f = entity.getZ();
            double g = clampHorizontal(packet.position().getX());
            double h = clampVertical(packet.position().getY());
            double i = clampHorizontal(packet.position().getZ());
            float j = MathHelper.wrapDegrees(packet.yaw());
            float k = MathHelper.wrapDegrees(packet.pitch());
            double l = g - this.lastTickRiddenX;
            double m = h - this.lastTickRiddenY;
            double n = i - this.lastTickRiddenZ;
            double o = entity.getVelocity().lengthSquared();
            double p = l * l + m * m + n * n;
            if (p - o > 100.0 && !this.isHost()) {
               LOGGER.warn("{} (vehicle of {}) moved too quickly! {},{},{}", new Object[]{entity.getName().getString(), this.player.getName().getString(), l, m, n});
               this.sendPacket(VehicleMoveS2CPacket.fromVehicle(entity));
               return;
            }

            Box box = entity.getBoundingBox();
            l = g - this.updatedRiddenX;
            m = h - this.updatedRiddenY;
            n = i - this.updatedRiddenZ;
            boolean bl = entity.groundCollision;
            if (entity instanceof LivingEntity) {
               LivingEntity livingEntity = (LivingEntity)entity;
               if (livingEntity.isClimbing()) {
                  livingEntity.onLanding();
               }
            }

            entity.move(MovementType.PLAYER, new Vec3d(l, m, n));
            double q = m;
            l = g - entity.getX();
            m = h - entity.getY();
            if (m > -0.5 || m < 0.5) {
               m = 0.0;
            }

            n = i - entity.getZ();
            p = l * l + m * m + n * n;
            boolean bl2 = false;
            if (p > 0.0625) {
               bl2 = true;
               LOGGER.warn("{} (vehicle of {}) moved wrongly! {}", new Object[]{entity.getName().getString(), this.player.getName().getString(), Math.sqrt(p)});
            }

            if (bl2 && serverWorld.isSpaceEmpty(entity, box) || this.isEntityNotCollidingWithBlocks(serverWorld, entity, box, g, h, i)) {
               entity.updatePositionAndAngles(d, e, f, j, k);
               this.sendPacket(VehicleMoveS2CPacket.fromVehicle(entity));
               entity.popQueuedCollisionCheck();
               return;
            }

            entity.updatePositionAndAngles(g, h, i, j, k);
            this.player.getWorld().getChunkManager().updatePosition(this.player);
            Vec3d vec3d = new Vec3d(entity.getX() - d, entity.getY() - e, entity.getZ() - f);
            this.handleMovement(vec3d);
            entity.setMovement(packet.onGround(), vec3d);
            entity.handleFall(vec3d.x, vec3d.y, vec3d.z, packet.onGround());
            this.player.increaseTravelMotionStats(vec3d.x, vec3d.y, vec3d.z);
            this.vehicleFloating = q >= -0.03125 && !bl && !this.server.isFlightEnabled() && !entity.isFlyingVehicle() && !entity.hasNoGravity() && this.isEntityOnAir(entity);
            this.updatedRiddenX = entity.getX();
            this.updatedRiddenY = entity.getY();
            this.updatedRiddenZ = entity.getZ();
         }

      }
   }

   private boolean isEntityOnAir(Entity entity) {
      return entity.getWorld().getStatesInBox(entity.getBoundingBox().expand(0.0625).stretch(0.0, -0.55, 0.0)).allMatch(AbstractBlock.AbstractBlockState::isAir);
   }

   public void onTeleportConfirm(TeleportConfirmC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      if (packet.getTeleportId() == this.requestedTeleportId) {
         if (this.requestedTeleportPos == null) {
            this.disconnect(Text.translatable("multiplayer.disconnect.invalid_player_movement"));
            return;
         }

         this.player.updatePositionAndAngles(this.requestedTeleportPos.x, this.requestedTeleportPos.y, this.requestedTeleportPos.z, this.player.getYaw(), this.player.getPitch());
         this.updatedX = this.requestedTeleportPos.x;
         this.updatedY = this.requestedTeleportPos.y;
         this.updatedZ = this.requestedTeleportPos.z;
         this.player.onTeleportationDone();
         this.requestedTeleportPos = null;
      }

   }

   public void onPlayerLoaded(PlayerLoadedC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      this.player.setLoaded(true);
   }

   public void onRecipeBookData(RecipeBookDataC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      ServerRecipeManager.ServerRecipe serverRecipe = this.server.getRecipeManager().get(packet.recipeId());
      if (serverRecipe != null) {
         this.player.getRecipeBook().unmarkHighlighted(serverRecipe.parent().id());
      }

   }

   public void onBundleItemSelected(BundleItemSelectedC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      this.player.currentScreenHandler.selectBundleStack(packet.slotId(), packet.selectedItemIndex());
   }

   public void onRecipeCategoryOptions(RecipeCategoryOptionsC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      this.player.getRecipeBook().setCategoryOptions(packet.getCategory(), packet.isGuiOpen(), packet.isFilteringCraftable());
   }

   public void onAdvancementTab(AdvancementTabC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      if (packet.getAction() == AdvancementTabC2SPacket.Action.OPENED_TAB) {
         Identifier identifier = (Identifier)Objects.requireNonNull(packet.getTabToOpen());
         AdvancementEntry advancementEntry = this.server.getAdvancementLoader().get(identifier);
         if (advancementEntry != null) {
            this.player.getAdvancementTracker().setDisplayTab(advancementEntry);
         }
      }

   }

   public void onRequestCommandCompletions(RequestCommandCompletionsC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      StringReader stringReader = new StringReader(packet.getPartialCommand());
      if (stringReader.canRead() && stringReader.peek() == '/') {
         stringReader.skip();
      }

      ParseResults parseResults = this.server.getCommandManager().getDispatcher().parse(stringReader, this.player.getCommandSource());
      this.server.getCommandManager().getDispatcher().getCompletionSuggestions(parseResults).thenAccept((suggestions) -> {
         Suggestions suggestions2 = suggestions.getList().size() <= 1000 ? suggestions : new Suggestions(suggestions.getRange(), suggestions.getList().subList(0, 1000));
         this.sendPacket(new CommandSuggestionsS2CPacket(packet.getCompletionId(), suggestions2));
      });
   }

   public void onUpdateCommandBlock(UpdateCommandBlockC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      if (!this.server.areCommandBlocksEnabled()) {
         this.player.sendMessage(Text.translatable("advMode.notEnabled"));
      } else if (!this.player.isCreativeLevelTwoOp()) {
         this.player.sendMessage(Text.translatable("advMode.notAllowed"));
      } else {
         CommandBlockExecutor commandBlockExecutor = null;
         CommandBlockBlockEntity commandBlockBlockEntity = null;
         BlockPos blockPos = packet.getPos();
         BlockEntity blockEntity = this.player.getWorld().getBlockEntity(blockPos);
         if (blockEntity instanceof CommandBlockBlockEntity) {
            commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
            commandBlockExecutor = commandBlockBlockEntity.getCommandExecutor();
         }

         String string = packet.getCommand();
         boolean bl = packet.shouldTrackOutput();
         if (commandBlockExecutor != null) {
            CommandBlockBlockEntity.Type type = commandBlockBlockEntity.getCommandBlockType();
            BlockState blockState = this.player.getWorld().getBlockState(blockPos);
            Direction direction = (Direction)blockState.get(CommandBlock.FACING);
            BlockState var10000;
            switch (packet.getType()) {
               case SEQUENCE:
                  var10000 = Blocks.CHAIN_COMMAND_BLOCK.getDefaultState();
                  break;
               case AUTO:
                  var10000 = Blocks.REPEATING_COMMAND_BLOCK.getDefaultState();
                  break;
               default:
                  var10000 = Blocks.COMMAND_BLOCK.getDefaultState();
            }

            BlockState blockState2 = var10000;
            BlockState blockState3 = (BlockState)((BlockState)blockState2.with(CommandBlock.FACING, direction)).with(CommandBlock.CONDITIONAL, packet.isConditional());
            if (blockState3 != blockState) {
               this.player.getWorld().setBlockState(blockPos, blockState3, 2);
               blockEntity.setCachedState(blockState3);
               this.player.getWorld().getWorldChunk(blockPos).setBlockEntity(blockEntity);
            }

            commandBlockExecutor.setCommand(string);
            commandBlockExecutor.setTrackOutput(bl);
            if (!bl) {
               commandBlockExecutor.setLastOutput((Text)null);
            }

            commandBlockBlockEntity.setAuto(packet.isAlwaysActive());
            if (type != packet.getType()) {
               commandBlockBlockEntity.updateCommandBlock();
            }

            commandBlockExecutor.markDirty();
            if (!StringHelper.isEmpty(string)) {
               this.player.sendMessage(Text.translatable("advMode.setCommand.success", string));
            }
         }

      }
   }

   public void onUpdateCommandBlockMinecart(UpdateCommandBlockMinecartC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      if (!this.server.areCommandBlocksEnabled()) {
         this.player.sendMessage(Text.translatable("advMode.notEnabled"));
      } else if (!this.player.isCreativeLevelTwoOp()) {
         this.player.sendMessage(Text.translatable("advMode.notAllowed"));
      } else {
         CommandBlockExecutor commandBlockExecutor = packet.getMinecartCommandExecutor(this.player.getWorld());
         if (commandBlockExecutor != null) {
            commandBlockExecutor.setCommand(packet.getCommand());
            commandBlockExecutor.setTrackOutput(packet.shouldTrackOutput());
            if (!packet.shouldTrackOutput()) {
               commandBlockExecutor.setLastOutput((Text)null);
            }

            commandBlockExecutor.markDirty();
            this.player.sendMessage(Text.translatable("advMode.setCommand.success", packet.getCommand()));
         }

      }
   }

   public void onPickItemFromBlock(PickItemFromBlockC2SPacket packet) {
      ServerWorld serverWorld = this.player.getWorld();
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)serverWorld);
      BlockPos blockPos = packet.pos();
      if (this.player.canInteractWithBlockAt(blockPos, 1.0)) {
         if (serverWorld.isPosLoaded(blockPos)) {
            BlockState blockState = serverWorld.getBlockState(blockPos);
            boolean bl = this.player.isInCreativeMode() && packet.includeData();
            ItemStack itemStack = blockState.getPickStack(serverWorld, blockPos, bl);
            if (!itemStack.isEmpty()) {
               if (bl) {
                  copyBlockDataToStack(blockState, serverWorld, blockPos, itemStack);
               }

               this.onPickItem(itemStack);
            }
         }
      }
   }

   private static void copyBlockDataToStack(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack) {
      BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
      if (blockEntity != null) {
         ErrorReporter.Logging logging = new ErrorReporter.Logging(blockEntity.getReporterContext(), LOGGER);

         try {
            NbtWriteView nbtWriteView = NbtWriteView.create(logging, world.getRegistryManager());
            blockEntity.writeComponentlessData(nbtWriteView);
            blockEntity.removeFromCopiedStackData(nbtWriteView);
            BlockItem.setBlockEntityData(stack, blockEntity.getType(), nbtWriteView);
            stack.applyComponentsFrom(blockEntity.createComponentMap());
         } catch (Throwable var9) {
            try {
               logging.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }

            throw var9;
         }

         logging.close();
      }

   }

   public void onPickItemFromEntity(PickItemFromEntityC2SPacket packet) {
      ServerWorld serverWorld = this.player.getWorld();
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)serverWorld);
      Entity entity = serverWorld.getEntityOrDragonPart(packet.id());
      if (entity != null && this.player.canInteractWithEntity(entity, 3.0)) {
         ItemStack itemStack = entity.getPickBlockStack();
         if (itemStack != null && !itemStack.isEmpty()) {
            this.onPickItem(itemStack);
         }

      }
   }

   private void onPickItem(ItemStack stack) {
      if (stack.isItemEnabled(this.player.getWorld().getEnabledFeatures())) {
         PlayerInventory playerInventory = this.player.getInventory();
         int i = playerInventory.getSlotWithStack(stack);
         if (i != -1) {
            if (PlayerInventory.isValidHotbarIndex(i)) {
               playerInventory.setSelectedSlot(i);
            } else {
               playerInventory.swapSlotWithHotbar(i);
            }
         } else if (this.player.isInCreativeMode()) {
            playerInventory.swapStackWithHotbar(stack);
         }

         this.sendPacket(new UpdateSelectedSlotS2CPacket(playerInventory.getSelectedSlot()));
         this.player.playerScreenHandler.sendContentUpdates();
      }
   }

   public void onRenameItem(RenameItemC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      ScreenHandler var3 = this.player.currentScreenHandler;
      if (var3 instanceof AnvilScreenHandler anvilScreenHandler) {
         if (!anvilScreenHandler.canUse(this.player)) {
            LOGGER.debug("Player {} interacted with invalid menu {}", this.player, anvilScreenHandler);
            return;
         }

         anvilScreenHandler.setNewItemName(packet.getName());
      }

   }

   public void onUpdateBeacon(UpdateBeaconC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      ScreenHandler var3 = this.player.currentScreenHandler;
      if (var3 instanceof BeaconScreenHandler beaconScreenHandler) {
         if (!this.player.currentScreenHandler.canUse(this.player)) {
            LOGGER.debug("Player {} interacted with invalid menu {}", this.player, this.player.currentScreenHandler);
            return;
         }

         beaconScreenHandler.setEffects(packet.primary(), packet.secondary());
      }

   }

   public void onUpdateStructureBlock(UpdateStructureBlockC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      if (this.player.isCreativeLevelTwoOp()) {
         BlockPos blockPos = packet.getPos();
         BlockState blockState = this.player.getWorld().getBlockState(blockPos);
         BlockEntity blockEntity = this.player.getWorld().getBlockEntity(blockPos);
         if (blockEntity instanceof StructureBlockBlockEntity) {
            StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)blockEntity;
            structureBlockBlockEntity.setMode(packet.getMode());
            structureBlockBlockEntity.setTemplateName(packet.getTemplateName());
            structureBlockBlockEntity.setOffset(packet.getOffset());
            structureBlockBlockEntity.setSize(packet.getSize());
            structureBlockBlockEntity.setMirror(packet.getMirror());
            structureBlockBlockEntity.setRotation(packet.getRotation());
            structureBlockBlockEntity.setMetadata(packet.getMetadata());
            structureBlockBlockEntity.setIgnoreEntities(packet.shouldIgnoreEntities());
            structureBlockBlockEntity.setStrict(packet.isStrict());
            structureBlockBlockEntity.setShowAir(packet.shouldShowAir());
            structureBlockBlockEntity.setShowBoundingBox(packet.shouldShowBoundingBox());
            structureBlockBlockEntity.setIntegrity(packet.getIntegrity());
            structureBlockBlockEntity.setSeed(packet.getSeed());
            if (structureBlockBlockEntity.hasStructureName()) {
               String string = structureBlockBlockEntity.getTemplateName();
               if (packet.getAction() == StructureBlockBlockEntity.Action.SAVE_AREA) {
                  if (structureBlockBlockEntity.saveStructure()) {
                     this.player.sendMessage(Text.translatable("structure_block.save_success", string), false);
                  } else {
                     this.player.sendMessage(Text.translatable("structure_block.save_failure", string), false);
                  }
               } else if (packet.getAction() == StructureBlockBlockEntity.Action.LOAD_AREA) {
                  if (!structureBlockBlockEntity.isStructureAvailable()) {
                     this.player.sendMessage(Text.translatable("structure_block.load_not_found", string), false);
                  } else if (structureBlockBlockEntity.loadAndTryPlaceStructure(this.player.getWorld())) {
                     this.player.sendMessage(Text.translatable("structure_block.load_success", string), false);
                  } else {
                     this.player.sendMessage(Text.translatable("structure_block.load_prepare", string), false);
                  }
               } else if (packet.getAction() == StructureBlockBlockEntity.Action.SCAN_AREA) {
                  if (structureBlockBlockEntity.detectStructureSize()) {
                     this.player.sendMessage(Text.translatable("structure_block.size_success", string), false);
                  } else {
                     this.player.sendMessage(Text.translatable("structure_block.size_failure"), false);
                  }
               }
            } else {
               this.player.sendMessage(Text.translatable("structure_block.invalid_structure_name", packet.getTemplateName()), false);
            }

            structureBlockBlockEntity.markDirty();
            this.player.getWorld().updateListeners(blockPos, blockState, blockState, 3);
         }

      }
   }

   public void onSetTestBlock(SetTestBlockC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      if (this.player.isCreativeLevelTwoOp()) {
         BlockPos blockPos = packet.position();
         BlockState blockState = this.player.getWorld().getBlockState(blockPos);
         BlockEntity blockEntity = this.player.getWorld().getBlockEntity(blockPos);
         if (blockEntity instanceof TestBlockEntity) {
            TestBlockEntity testBlockEntity = (TestBlockEntity)blockEntity;
            testBlockEntity.setMode(packet.mode());
            testBlockEntity.setMessage(packet.message());
            testBlockEntity.markDirty();
            this.player.getWorld().updateListeners(blockPos, blockState, testBlockEntity.getCachedState(), 3);
         }

      }
   }

   public void onTestInstanceBlockAction(TestInstanceBlockActionC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      BlockPos blockPos = packet.pos();
      if (this.player.isCreativeLevelTwoOp()) {
         BlockEntity var4 = this.player.getWorld().getBlockEntity(blockPos);
         if (var4 instanceof TestInstanceBlockEntity) {
            TestInstanceBlockEntity testInstanceBlockEntity = (TestInstanceBlockEntity)var4;
            if (packet.action() != TestInstanceBlockActionC2SPacket.Action.QUERY && packet.action() != TestInstanceBlockActionC2SPacket.Action.INIT) {
               testInstanceBlockEntity.setData(packet.data());
               ServerPlayerEntity var10001;
               if (packet.action() == TestInstanceBlockActionC2SPacket.Action.RESET) {
                  var10001 = this.player;
                  Objects.requireNonNull(var10001);
                  testInstanceBlockEntity.reset(var10001::sendMessage);
               } else if (packet.action() == TestInstanceBlockActionC2SPacket.Action.SAVE) {
                  var10001 = this.player;
                  Objects.requireNonNull(var10001);
                  testInstanceBlockEntity.saveStructure(var10001::sendMessage);
               } else if (packet.action() == TestInstanceBlockActionC2SPacket.Action.EXPORT) {
                  var10001 = this.player;
                  Objects.requireNonNull(var10001);
                  testInstanceBlockEntity.export(var10001::sendMessage);
               } else if (packet.action() == TestInstanceBlockActionC2SPacket.Action.RUN) {
                  var10001 = this.player;
                  Objects.requireNonNull(var10001);
                  testInstanceBlockEntity.start(var10001::sendMessage);
               }

               BlockState blockState = this.player.getWorld().getBlockState(blockPos);
               this.player.getWorld().updateListeners(blockPos, Blocks.AIR.getDefaultState(), blockState, 3);
            } else {
               Registry registry = this.player.getRegistryManager().getOrThrow(RegistryKeys.TEST_INSTANCE);
               Optional var10000 = packet.data().test();
               Objects.requireNonNull(registry);
               Optional optional = var10000.flatMap(registry::getOptional);
               Object text;
               if (optional.isPresent()) {
                  text = ((TestInstance)((RegistryEntry.Reference)optional.get()).value()).getDescription();
               } else {
                  text = Text.translatable("test_instance.description.no_test").formatted(Formatting.RED);
               }

               Optional optional2;
               if (packet.action() == TestInstanceBlockActionC2SPacket.Action.QUERY) {
                  optional2 = packet.data().test().flatMap((registryKey) -> {
                     return TestInstanceBlockEntity.getStructureSize(this.player.getWorld(), registryKey);
                  });
               } else {
                  optional2 = Optional.empty();
               }

               this.connection.send(new TestInstanceBlockStatusS2CPacket((Text)text, optional2));
            }

            return;
         }
      }

   }

   public void onUpdateJigsaw(UpdateJigsawC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      if (this.player.isCreativeLevelTwoOp()) {
         BlockPos blockPos = packet.getPos();
         BlockState blockState = this.player.getWorld().getBlockState(blockPos);
         BlockEntity blockEntity = this.player.getWorld().getBlockEntity(blockPos);
         if (blockEntity instanceof JigsawBlockEntity) {
            JigsawBlockEntity jigsawBlockEntity = (JigsawBlockEntity)blockEntity;
            jigsawBlockEntity.setName(packet.getName());
            jigsawBlockEntity.setTarget(packet.getTarget());
            jigsawBlockEntity.setPool(RegistryKey.of(RegistryKeys.TEMPLATE_POOL, packet.getPool()));
            jigsawBlockEntity.setFinalState(packet.getFinalState());
            jigsawBlockEntity.setJoint(packet.getJointType());
            jigsawBlockEntity.setPlacementPriority(packet.getPlacementPriority());
            jigsawBlockEntity.setSelectionPriority(packet.getSelectionPriority());
            jigsawBlockEntity.markDirty();
            this.player.getWorld().updateListeners(blockPos, blockState, blockState, 3);
         }

      }
   }

   public void onJigsawGenerating(JigsawGeneratingC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      if (this.player.isCreativeLevelTwoOp()) {
         BlockPos blockPos = packet.getPos();
         BlockEntity blockEntity = this.player.getWorld().getBlockEntity(blockPos);
         if (blockEntity instanceof JigsawBlockEntity) {
            JigsawBlockEntity jigsawBlockEntity = (JigsawBlockEntity)blockEntity;
            jigsawBlockEntity.generate(this.player.getWorld(), packet.getMaxDepth(), packet.shouldKeepJigsaws());
         }

      }
   }

   public void onSelectMerchantTrade(SelectMerchantTradeC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      int i = packet.getTradeId();
      ScreenHandler var4 = this.player.currentScreenHandler;
      if (var4 instanceof MerchantScreenHandler merchantScreenHandler) {
         if (!merchantScreenHandler.canUse(this.player)) {
            LOGGER.debug("Player {} interacted with invalid menu {}", this.player, merchantScreenHandler);
            return;
         }

         merchantScreenHandler.setRecipeIndex(i);
         merchantScreenHandler.switchTo(i);
      }

   }

   public void onBookUpdate(BookUpdateC2SPacket packet) {
      int i = packet.slot();
      if (PlayerInventory.isValidHotbarIndex(i) || i == 40) {
         List list = Lists.newArrayList();
         Optional optional = packet.title();
         Objects.requireNonNull(list);
         optional.ifPresent(list::add);
         list.addAll(packet.pages());
         Consumer consumer = optional.isPresent() ? (texts) -> {
            this.addBook((FilteredMessage)texts.get(0), texts.subList(1, texts.size()), i);
         } : (texts) -> {
            this.updateBookContent(texts, i);
         };
         this.filterTexts(list).thenAcceptAsync(consumer, this.server);
      }
   }

   private void updateBookContent(List pages, int slotId) {
      ItemStack itemStack = this.player.getInventory().getStack(slotId);
      if (itemStack.contains(DataComponentTypes.WRITABLE_BOOK_CONTENT)) {
         List list = pages.stream().map(this::toRawFilteredPair).toList();
         itemStack.set(DataComponentTypes.WRITABLE_BOOK_CONTENT, new WritableBookContentComponent(list));
      }
   }

   private void addBook(FilteredMessage title, List pages, int slotId) {
      ItemStack itemStack = this.player.getInventory().getStack(slotId);
      if (itemStack.contains(DataComponentTypes.WRITABLE_BOOK_CONTENT)) {
         ItemStack itemStack2 = itemStack.withItem(Items.WRITTEN_BOOK);
         itemStack2.remove(DataComponentTypes.WRITABLE_BOOK_CONTENT);
         List list = pages.stream().map((page) -> {
            return this.toRawFilteredPair(page).map(Text::literal);
         }).toList();
         itemStack2.set(DataComponentTypes.WRITTEN_BOOK_CONTENT, new WrittenBookContentComponent(this.toRawFilteredPair(title), this.player.getName().getString(), 0, list, true));
         this.player.getInventory().setStack(slotId, itemStack2);
      }
   }

   private RawFilteredPair toRawFilteredPair(FilteredMessage message) {
      return this.player.shouldFilterText() ? RawFilteredPair.of((Object)message.getString()) : RawFilteredPair.of(message);
   }

   public void onQueryEntityNbt(QueryEntityNbtC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      if (this.player.hasPermissionLevel(2)) {
         Entity entity = this.player.getWorld().getEntityById(packet.getEntityId());
         if (entity != null) {
            ErrorReporter.Logging logging = new ErrorReporter.Logging(entity.getErrorReporterContext(), LOGGER);

            try {
               NbtWriteView nbtWriteView = NbtWriteView.create(logging, entity.getRegistryManager());
               entity.writeData(nbtWriteView);
               NbtCompound nbtCompound = nbtWriteView.getNbt();
               this.sendPacket(new NbtQueryResponseS2CPacket(packet.getTransactionId(), nbtCompound));
            } catch (Throwable var7) {
               try {
                  logging.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }

               throw var7;
            }

            logging.close();
         }

      }
   }

   public void onSlotChangedState(SlotChangedStateC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      if (!this.player.isSpectator() && packet.screenHandlerId() == this.player.currentScreenHandler.syncId) {
         ScreenHandler var4 = this.player.currentScreenHandler;
         if (var4 instanceof CrafterScreenHandler) {
            CrafterScreenHandler crafterScreenHandler = (CrafterScreenHandler)var4;
            Inventory var5 = crafterScreenHandler.getInputInventory();
            if (var5 instanceof CrafterBlockEntity) {
               CrafterBlockEntity crafterBlockEntity = (CrafterBlockEntity)var5;
               crafterBlockEntity.setSlotEnabled(packet.slotId(), packet.newState());
            }
         }

      }
   }

   public void onQueryBlockNbt(QueryBlockNbtC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      if (this.player.hasPermissionLevel(2)) {
         BlockEntity blockEntity = this.player.getWorld().getBlockEntity(packet.getPos());
         NbtCompound nbtCompound = blockEntity != null ? blockEntity.createNbt(this.player.getRegistryManager()) : null;
         this.sendPacket(new NbtQueryResponseS2CPacket(packet.getTransactionId(), nbtCompound));
      }
   }

   public void onPlayerMove(PlayerMoveC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      if (isMovementInvalid(packet.getX(0.0), packet.getY(0.0), packet.getZ(0.0), packet.getYaw(0.0F), packet.getPitch(0.0F))) {
         this.disconnect(Text.translatable("multiplayer.disconnect.invalid_player_movement"));
      } else {
         ServerWorld serverWorld = this.player.getWorld();
         if (!this.player.notInAnyWorld) {
            if (this.ticks == 0) {
               this.syncWithPlayerPosition();
            }

            if (this.player.isLoaded()) {
               float f = MathHelper.wrapDegrees(packet.getYaw(this.player.getYaw()));
               float g = MathHelper.wrapDegrees(packet.getPitch(this.player.getPitch()));
               if (this.handlePendingTeleport()) {
                  this.player.setAngles(f, g);
               } else {
                  double d = clampHorizontal(packet.getX(this.player.getX()));
                  double e = clampVertical(packet.getY(this.player.getY()));
                  double h = clampHorizontal(packet.getZ(this.player.getZ()));
                  if (this.player.hasVehicle()) {
                     this.player.updatePositionAndAngles(this.player.getX(), this.player.getY(), this.player.getZ(), f, g);
                     this.player.getWorld().getChunkManager().updatePosition(this.player);
                  } else {
                     double i = this.player.getX();
                     double j = this.player.getY();
                     double k = this.player.getZ();
                     double l = d - this.lastTickX;
                     double m = e - this.lastTickY;
                     double n = h - this.lastTickZ;
                     double o = this.player.getVelocity().lengthSquared();
                     double p = l * l + m * m + n * n;
                     if (this.player.isSleeping()) {
                        if (p > 1.0) {
                           this.requestTeleport(this.player.getX(), this.player.getY(), this.player.getZ(), f, g);
                        }

                     } else {
                        boolean bl = this.player.isGliding();
                        if (serverWorld.getTickManager().shouldTick()) {
                           ++this.movePacketsCount;
                           int q = this.movePacketsCount - this.lastTickMovePacketsCount;
                           if (q > 5) {
                              LOGGER.debug("{} is sending move packets too frequently ({} packets since last tick)", this.player.getName().getString(), q);
                              q = 1;
                           }

                           if (this.shouldCheckMovement(bl)) {
                              float r = bl ? 300.0F : 100.0F;
                              if (p - o > (double)(r * (float)q)) {
                                 LOGGER.warn("{} moved too quickly! {},{},{}", new Object[]{this.player.getName().getString(), l, m, n});
                                 this.requestTeleport(this.player.getX(), this.player.getY(), this.player.getZ(), this.player.getYaw(), this.player.getPitch());
                                 return;
                              }
                           }
                        }

                        Box box = this.player.getBoundingBox();
                        l = d - this.updatedX;
                        m = e - this.updatedY;
                        n = h - this.updatedZ;
                        boolean bl2 = m > 0.0;
                        if (this.player.isOnGround() && !packet.isOnGround() && bl2) {
                           this.player.jump();
                        }

                        boolean bl3 = this.player.groundCollision;
                        this.player.move(MovementType.PLAYER, new Vec3d(l, m, n));
                        double s = m;
                        l = d - this.player.getX();
                        m = e - this.player.getY();
                        if (m > -0.5 || m < 0.5) {
                           m = 0.0;
                        }

                        n = h - this.player.getZ();
                        p = l * l + m * m + n * n;
                        boolean bl4 = false;
                        if (!this.player.isInTeleportationState() && p > 0.0625 && !this.player.isSleeping() && !this.player.isCreative() && !this.player.isSpectator()) {
                           bl4 = true;
                           LOGGER.warn("{} moved wrongly!", this.player.getName().getString());
                        }

                        if (!this.player.noClip && !this.player.isSleeping() && (bl4 && serverWorld.isSpaceEmpty(this.player, box) || this.isEntityNotCollidingWithBlocks(serverWorld, this.player, box, d, e, h))) {
                           this.requestTeleport(i, j, k, f, g);
                           this.player.handleFall(this.player.getX() - i, this.player.getY() - j, this.player.getZ() - k, packet.isOnGround());
                           this.player.popQueuedCollisionCheck();
                        } else {
                           this.player.updatePositionAndAngles(d, e, h, f, g);
                           boolean bl5 = this.player.isUsingRiptide();
                           this.floating = s >= -0.03125 && !bl3 && !this.player.isSpectator() && !this.server.isFlightEnabled() && !this.player.getAbilities().allowFlying && !this.player.hasStatusEffect(StatusEffects.LEVITATION) && !bl && !bl5 && this.isEntityOnAir(this.player);
                           this.player.getWorld().getChunkManager().updatePosition(this.player);
                           Vec3d vec3d = new Vec3d(this.player.getX() - i, this.player.getY() - j, this.player.getZ() - k);
                           this.player.setMovement(packet.isOnGround(), packet.horizontalCollision(), vec3d);
                           this.player.handleFall(vec3d.x, vec3d.y, vec3d.z, packet.isOnGround());
                           this.handleMovement(vec3d);
                           if (bl2) {
                              this.player.onLanding();
                           }

                           if (packet.isOnGround() || this.player.hasLandedInFluid() || this.player.isClimbing() || this.player.isSpectator() || bl || bl5) {
                              this.player.tryClearCurrentExplosion();
                           }

                           this.player.increaseTravelMotionStats(this.player.getX() - i, this.player.getY() - j, this.player.getZ() - k);
                           this.updatedX = this.player.getX();
                           this.updatedY = this.player.getY();
                           this.updatedZ = this.player.getZ();
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean shouldCheckMovement(boolean elytra) {
      if (this.isHost()) {
         return false;
      } else if (this.player.isInTeleportationState()) {
         return false;
      } else {
         GameRules gameRules = this.player.getWorld().getGameRules();
         if (gameRules.getBoolean(GameRules.DISABLE_PLAYER_MOVEMENT_CHECK)) {
            return false;
         } else {
            return !elytra || !gameRules.getBoolean(GameRules.DISABLE_ELYTRA_MOVEMENT_CHECK);
         }
      }
   }

   private boolean handlePendingTeleport() {
      if (this.requestedTeleportPos != null) {
         if (this.ticks - this.lastTeleportCheckTicks > 20) {
            this.lastTeleportCheckTicks = this.ticks;
            this.requestTeleport(this.requestedTeleportPos.x, this.requestedTeleportPos.y, this.requestedTeleportPos.z, this.player.getYaw(), this.player.getPitch());
         }

         return true;
      } else {
         this.lastTeleportCheckTicks = this.ticks;
         return false;
      }
   }

   private boolean isEntityNotCollidingWithBlocks(WorldView world, Entity entity, Box box, double newX, double newY, double newZ) {
      Box box2 = entity.getBoundingBox().offset(newX - entity.getX(), newY - entity.getY(), newZ - entity.getZ());
      Iterable iterable = world.getCollisions(entity, box2.contract(9.999999747378752E-6), box.getHorizontalCenter());
      VoxelShape voxelShape = VoxelShapes.cuboid(box.contract(9.999999747378752E-6));
      Iterator var13 = iterable.iterator();

      VoxelShape voxelShape2;
      do {
         if (!var13.hasNext()) {
            return false;
         }

         voxelShape2 = (VoxelShape)var13.next();
      } while(VoxelShapes.matchesAnywhere(voxelShape2, voxelShape, BooleanBiFunction.AND));

      return true;
   }

   public void requestTeleport(double x, double y, double z, float yaw, float pitch) {
      this.requestTeleport(new PlayerPosition(new Vec3d(x, y, z), Vec3d.ZERO, yaw, pitch), Collections.emptySet());
   }

   public void requestTeleport(PlayerPosition pos, Set flags) {
      this.lastTeleportCheckTicks = this.ticks;
      if (++this.requestedTeleportId == Integer.MAX_VALUE) {
         this.requestedTeleportId = 0;
      }

      this.player.setPosition(pos, flags);
      this.requestedTeleportPos = this.player.getPos();
      this.sendPacket(PlayerPositionLookS2CPacket.of(this.requestedTeleportId, pos, flags));
   }

   public void onPlayerAction(PlayerActionC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      if (this.player.isLoaded()) {
         BlockPos blockPos = packet.getPos();
         this.player.updateLastActionTime();
         PlayerActionC2SPacket.Action action = packet.getAction();
         switch (action) {
            case SWAP_ITEM_WITH_OFFHAND:
               if (!this.player.isSpectator()) {
                  ItemStack itemStack = this.player.getStackInHand(Hand.OFF_HAND);
                  this.player.setStackInHand(Hand.OFF_HAND, this.player.getStackInHand(Hand.MAIN_HAND));
                  this.player.setStackInHand(Hand.MAIN_HAND, itemStack);
                  this.player.clearActiveItem();
               }

               return;
            case DROP_ITEM:
               if (!this.player.isSpectator()) {
                  this.player.dropSelectedItem(false);
               }

               return;
            case DROP_ALL_ITEMS:
               if (!this.player.isSpectator()) {
                  this.player.dropSelectedItem(true);
               }

               return;
            case RELEASE_USE_ITEM:
               this.player.stopUsingItem();
               return;
            case START_DESTROY_BLOCK:
            case ABORT_DESTROY_BLOCK:
            case STOP_DESTROY_BLOCK:
               this.player.interactionManager.processBlockBreakingAction(blockPos, action, packet.getDirection(), this.player.getWorld().getTopYInclusive(), packet.getSequence());
               this.updateSequence(packet.getSequence());
               return;
            default:
               throw new IllegalArgumentException("Invalid player action");
         }
      }
   }

   private static boolean canPlace(ServerPlayerEntity player, ItemStack stack) {
      if (stack.isEmpty()) {
         return false;
      } else {
         Item item = stack.getItem();
         return (item instanceof BlockItem || item instanceof BucketItem) && !player.getItemCooldownManager().isCoolingDown(stack);
      }
   }

   public void onPlayerInteractBlock(PlayerInteractBlockC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      if (this.player.isLoaded()) {
         this.updateSequence(packet.getSequence());
         ServerWorld serverWorld = this.player.getWorld();
         Hand hand = packet.getHand();
         ItemStack itemStack = this.player.getStackInHand(hand);
         if (itemStack.isItemEnabled(serverWorld.getEnabledFeatures())) {
            BlockHitResult blockHitResult = packet.getBlockHitResult();
            Vec3d vec3d = blockHitResult.getPos();
            BlockPos blockPos = blockHitResult.getBlockPos();
            if (this.player.canInteractWithBlockAt(blockPos, 1.0)) {
               Vec3d vec3d2 = vec3d.subtract(Vec3d.ofCenter(blockPos));
               double d = 1.0000001;
               if (Math.abs(vec3d2.getX()) < 1.0000001 && Math.abs(vec3d2.getY()) < 1.0000001 && Math.abs(vec3d2.getZ()) < 1.0000001) {
                  Direction direction = blockHitResult.getSide();
                  this.player.updateLastActionTime();
                  int i = this.player.getWorld().getTopYInclusive();
                  if (blockPos.getY() <= i) {
                     if (this.requestedTeleportPos == null && serverWorld.canEntityModifyAt(this.player, blockPos)) {
                        ActionResult actionResult = this.player.interactionManager.interactBlock(this.player, serverWorld, itemStack, hand, blockHitResult);
                        if (actionResult.isAccepted()) {
                           Criteria.ANY_BLOCK_USE.trigger(this.player, blockHitResult.getBlockPos(), itemStack.copy());
                        }

                        if (direction == Direction.UP && !actionResult.isAccepted() && blockPos.getY() >= i && canPlace(this.player, itemStack)) {
                           Text text = Text.translatable("build.tooHigh", i).formatted(Formatting.RED);
                           this.player.sendMessageToClient(text, true);
                        } else if (actionResult instanceof ActionResult.Success) {
                           ActionResult.Success success = (ActionResult.Success)actionResult;
                           if (success.swingSource() == ActionResult.SwingSource.SERVER) {
                              this.player.swingHand(hand, true);
                           }
                        }
                     }
                  } else {
                     Text text2 = Text.translatable("build.tooHigh", i).formatted(Formatting.RED);
                     this.player.sendMessageToClient(text2, true);
                  }

                  this.sendPacket(new BlockUpdateS2CPacket(serverWorld, blockPos));
                  this.sendPacket(new BlockUpdateS2CPacket(serverWorld, blockPos.offset(direction)));
               } else {
                  LOGGER.warn("Rejecting UseItemOnPacket from {}: Location {} too far away from hit block {}.", new Object[]{this.player.getGameProfile().getName(), vec3d, blockPos});
               }
            }
         }
      }
   }

   public void onPlayerInteractItem(PlayerInteractItemC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      if (this.player.isLoaded()) {
         this.updateSequence(packet.getSequence());
         ServerWorld serverWorld = this.player.getWorld();
         Hand hand = packet.getHand();
         ItemStack itemStack = this.player.getStackInHand(hand);
         this.player.updateLastActionTime();
         if (!itemStack.isEmpty() && itemStack.isItemEnabled(serverWorld.getEnabledFeatures())) {
            float f = MathHelper.wrapDegrees(packet.getYaw());
            float g = MathHelper.wrapDegrees(packet.getPitch());
            if (g != this.player.getPitch() || f != this.player.getYaw()) {
               this.player.setAngles(f, g);
            }

            ActionResult actionResult = this.player.interactionManager.interactItem(this.player, serverWorld, itemStack, hand);
            if (actionResult instanceof ActionResult.Success) {
               ActionResult.Success success = (ActionResult.Success)actionResult;
               if (success.swingSource() == ActionResult.SwingSource.SERVER) {
                  this.player.swingHand(hand, true);
               }
            }

         }
      }
   }

   public void onSpectatorTeleport(SpectatorTeleportC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      if (this.player.isSpectator()) {
         Iterator var2 = this.server.getWorlds().iterator();

         while(var2.hasNext()) {
            ServerWorld serverWorld = (ServerWorld)var2.next();
            Entity entity = packet.getTarget(serverWorld);
            if (entity != null) {
               this.player.teleport(serverWorld, entity.getX(), entity.getY(), entity.getZ(), Set.of(), entity.getYaw(), entity.getPitch(), true);
               return;
            }
         }
      }

   }

   public void onBoatPaddleState(BoatPaddleStateC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      Entity entity = this.player.getControllingVehicle();
      if (entity instanceof AbstractBoatEntity abstractBoatEntity) {
         abstractBoatEntity.setPaddlesMoving(packet.isLeftPaddling(), packet.isRightPaddling());
      }

   }

   public void onDisconnected(DisconnectionInfo info) {
      LOGGER.info("{} lost connection: {}", this.player.getName().getString(), info.reason().getString());
      this.cleanUp();
      super.onDisconnected(info);
   }

   private void cleanUp() {
      this.messageChainTaskQueue.close();
      this.server.forcePlayerSampleUpdate();
      this.server.getPlayerManager().broadcast(Text.translatable("multiplayer.player.left", this.player.getDisplayName()).formatted(Formatting.YELLOW), false);
      this.player.onDisconnect();
      this.server.getPlayerManager().remove(this.player);
      this.player.getTextStream().onDisconnect();
   }

   public void updateSequence(int sequence) {
      if (sequence < 0) {
         throw new IllegalArgumentException("Expected packet sequence nr >= 0");
      } else {
         this.sequence = Math.max(sequence, this.sequence);
      }
   }

   public void onUpdateSelectedSlot(UpdateSelectedSlotC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      if (packet.getSelectedSlot() >= 0 && packet.getSelectedSlot() < PlayerInventory.getHotbarSize()) {
         if (this.player.getInventory().getSelectedSlot() != packet.getSelectedSlot() && this.player.getActiveHand() == Hand.MAIN_HAND) {
            this.player.clearActiveItem();
         }

         this.player.getInventory().setSelectedSlot(packet.getSelectedSlot());
         this.player.updateLastActionTime();
      } else {
         LOGGER.warn("{} tried to set an invalid carried item", this.player.getName().getString());
      }
   }

   public void onChatMessage(ChatMessageC2SPacket packet) {
      Optional optional = this.validateAcknowledgment(packet.acknowledgment());
      if (!optional.isEmpty()) {
         this.validateMessage(packet.chatMessage(), () -> {
            SignedMessage signedMessage;
            try {
               signedMessage = this.getSignedMessage(packet, (LastSeenMessageList)optional.get());
            } catch (MessageChain.MessageChainException var6) {
               this.handleMessageChainException(var6);
               return;
            }

            CompletableFuture completableFuture = this.filterText(signedMessage.getSignedContent());
            Text text = this.server.getMessageDecorator().decorate(this.player, signedMessage.getContent());
            this.messageChainTaskQueue.append(completableFuture, (filtered) -> {
               SignedMessage signedMessage2 = signedMessage.withUnsignedContent(text).withFilterMask(filtered.mask());
               this.handleDecoratedMessage(signedMessage2);
            });
         });
      }
   }

   public void onCommandExecution(CommandExecutionC2SPacket packet) {
      this.validateMessage(packet.command(), () -> {
         this.executeCommand(packet.command());
         this.checkForSpam();
      });
   }

   private void executeCommand(String command) {
      ParseResults parseResults = this.parse(command);
      if (this.server.shouldEnforceSecureProfile() && SignedArgumentList.isNotEmpty(parseResults)) {
         LOGGER.error("Received unsigned command packet from {}, but the command requires signable arguments: {}", this.player.getGameProfile().getName(), command);
         this.player.sendMessage(INVALID_COMMAND_SIGNATURE_TEXT);
      } else {
         this.server.getCommandManager().execute(parseResults, command);
      }
   }

   public void onChatCommandSigned(ChatCommandSignedC2SPacket packet) {
      Optional optional = this.validateAcknowledgment(packet.lastSeenMessages());
      if (!optional.isEmpty()) {
         this.validateMessage(packet.command(), () -> {
            this.handleCommandExecution(packet, (LastSeenMessageList)optional.get());
            this.checkForSpam();
         });
      }
   }

   private void handleCommandExecution(ChatCommandSignedC2SPacket packet, LastSeenMessageList lastSeenMessages) {
      ParseResults parseResults = this.parse(packet.command());

      Map map;
      try {
         map = this.collectArgumentMessages(packet, SignedArgumentList.of(parseResults), lastSeenMessages);
      } catch (MessageChain.MessageChainException var6) {
         this.handleMessageChainException(var6);
         return;
      }

      SignedCommandArguments signedCommandArguments = new SignedCommandArguments.Impl(map);
      parseResults = CommandManager.withCommandSource(parseResults, (source) -> {
         return source.withSignedArguments(signedCommandArguments, this.messageChainTaskQueue);
      });
      this.server.getCommandManager().execute(parseResults, packet.command());
   }

   private void handleMessageChainException(MessageChain.MessageChainException exception) {
      LOGGER.warn("Failed to update secure chat state for {}: '{}'", this.player.getGameProfile().getName(), exception.getMessageText().getString());
      this.player.sendMessage(exception.getMessageText().copy().formatted(Formatting.RED));
   }

   private Map collectArgumentMessages(ChatCommandSignedC2SPacket packet, SignedArgumentList arguments, LastSeenMessageList lastSeenMessages) throws MessageChain.MessageChainException {
      List list = packet.argumentSignatures().entries();
      List list2 = arguments.arguments();
      if (list.isEmpty()) {
         return this.toUnsignedSignatures(list2);
      } else {
         Map map = new Object2ObjectOpenHashMap();
         Iterator var7 = list.iterator();

         while(var7.hasNext()) {
            ArgumentSignatureDataMap.Entry entry = (ArgumentSignatureDataMap.Entry)var7.next();
            SignedArgumentList.ParsedArgument parsedArgument = arguments.get(entry.name());
            if (parsedArgument == null) {
               this.messageUnpacker.setChainBroken();
               throw createInvalidCommandSignatureException(packet.command(), list, list2);
            }

            MessageBody messageBody = new MessageBody(parsedArgument.value(), packet.timestamp(), packet.salt(), lastSeenMessages);
            map.put(parsedArgument.getNodeName(), this.messageUnpacker.unpack(entry.signature(), messageBody));
         }

         var7 = list2.iterator();

         SignedArgumentList.ParsedArgument parsedArgument2;
         do {
            if (!var7.hasNext()) {
               return map;
            }

            parsedArgument2 = (SignedArgumentList.ParsedArgument)var7.next();
         } while(map.containsKey(parsedArgument2.getNodeName()));

         throw createInvalidCommandSignatureException(packet.command(), list, list2);
      }
   }

   private Map toUnsignedSignatures(List arguments) throws MessageChain.MessageChainException {
      Map map = new HashMap();
      Iterator var3 = arguments.iterator();

      while(var3.hasNext()) {
         SignedArgumentList.ParsedArgument parsedArgument = (SignedArgumentList.ParsedArgument)var3.next();
         MessageBody messageBody = MessageBody.ofUnsigned(parsedArgument.value());
         map.put(parsedArgument.getNodeName(), this.messageUnpacker.unpack((MessageSignatureData)null, messageBody));
      }

      return map;
   }

   private static MessageChain.MessageChainException createInvalidCommandSignatureException(String command, List actual, List expected) {
      String string = (String)actual.stream().map(ArgumentSignatureDataMap.Entry::name).collect(Collectors.joining(", "));
      String string2 = (String)expected.stream().map(SignedArgumentList.ParsedArgument::getNodeName).collect(Collectors.joining(", "));
      LOGGER.error("Signed command mismatch between server and client ('{}'): got [{}] from client, but expected [{}]", new Object[]{command, string, string2});
      return new MessageChain.MessageChainException(INVALID_COMMAND_SIGNATURE_TEXT);
   }

   private ParseResults parse(String command) {
      CommandDispatcher commandDispatcher = this.server.getCommandManager().getDispatcher();
      return commandDispatcher.parse(command, this.player.getCommandSource());
   }

   private void validateMessage(String message, Runnable callback) {
      if (hasIllegalCharacter(message)) {
         this.disconnect(Text.translatable("multiplayer.disconnect.illegal_characters"));
      } else if (this.player.getClientChatVisibility() == ChatVisibility.HIDDEN) {
         this.sendPacket(new GameMessageS2CPacket(Text.translatable("chat.disabled.options").formatted(Formatting.RED), false));
      } else {
         this.player.updateLastActionTime();
         this.server.execute(callback);
      }
   }

   private Optional validateAcknowledgment(LastSeenMessageList.Acknowledgment acknowledgment) {
      synchronized(this.acknowledgmentValidator) {
         Optional var10000;
         try {
            LastSeenMessageList lastSeenMessageList = this.acknowledgmentValidator.validate(acknowledgment);
            var10000 = Optional.of(lastSeenMessageList);
         } catch (AcknowledgmentValidator.ValidationException var5) {
            LOGGER.error("Failed to validate message acknowledgements from {}: {}", this.player.getName().getString(), var5.getMessage());
            this.disconnect(CHAT_VALIDATION_FAILED_TEXT);
            return Optional.empty();
         }

         return var10000;
      }
   }

   private static boolean hasIllegalCharacter(String message) {
      for(int i = 0; i < message.length(); ++i) {
         if (!StringHelper.isValidChar(message.charAt(i))) {
            return true;
         }
      }

      return false;
   }

   private SignedMessage getSignedMessage(ChatMessageC2SPacket packet, LastSeenMessageList lastSeenMessages) throws MessageChain.MessageChainException {
      MessageBody messageBody = new MessageBody(packet.chatMessage(), packet.timestamp(), packet.salt(), lastSeenMessages);
      return this.messageUnpacker.unpack(packet.signature(), messageBody);
   }

   private void handleDecoratedMessage(SignedMessage message) {
      this.server.getPlayerManager().broadcast(message, this.player, MessageType.params(MessageType.CHAT, (Entity)this.player));
      this.checkForSpam();
   }

   private void checkForSpam() {
      this.messageCooldown.increment();
      if (!this.messageCooldown.canUse() && !this.server.getPlayerManager().isOperator(this.player.getGameProfile()) && !this.server.isHost(this.player.getGameProfile())) {
         this.disconnect(Text.translatable("disconnect.spam"));
      }

   }

   public void onMessageAcknowledgment(MessageAcknowledgmentC2SPacket packet) {
      synchronized(this.acknowledgmentValidator) {
         try {
            this.acknowledgmentValidator.removeUntil(packet.offset());
         } catch (AcknowledgmentValidator.ValidationException var5) {
            LOGGER.error("Failed to validate message acknowledgement offset from {}: {}", this.player.getName().getString(), var5.getMessage());
            this.disconnect(CHAT_VALIDATION_FAILED_TEXT);
         }

      }
   }

   public void onHandSwing(HandSwingC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      this.player.updateLastActionTime();
      this.player.swingHand(packet.getHand());
   }

   public void onClientCommand(ClientCommandC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      if (this.player.isLoaded()) {
         this.player.updateLastActionTime();
         Entity var3;
         JumpingMount jumpingMount;
         switch (packet.getMode()) {
            case START_SPRINTING:
               this.player.setSprinting(true);
               break;
            case STOP_SPRINTING:
               this.player.setSprinting(false);
               break;
            case STOP_SLEEPING:
               if (this.player.isSleeping()) {
                  this.player.wakeUp(false, true);
                  this.requestedTeleportPos = this.player.getPos();
               }
               break;
            case START_RIDING_JUMP:
               var3 = this.player.getControllingVehicle();
               if (var3 instanceof JumpingMount) {
                  jumpingMount = (JumpingMount)var3;
                  int i = packet.getMountJumpHeight();
                  if (jumpingMount.canJump() && i > 0) {
                     jumpingMount.startJumping(i);
                  }
               }
               break;
            case STOP_RIDING_JUMP:
               var3 = this.player.getControllingVehicle();
               if (var3 instanceof JumpingMount) {
                  jumpingMount = (JumpingMount)var3;
                  jumpingMount.stopJumping();
               }
               break;
            case OPEN_INVENTORY:
               var3 = this.player.getVehicle();
               if (var3 instanceof RideableInventory) {
                  RideableInventory rideableInventory = (RideableInventory)var3;
                  rideableInventory.openInventory(this.player);
               }
               break;
            case START_FALL_FLYING:
               if (!this.player.checkGliding()) {
                  this.player.stopGliding();
               }
               break;
            default:
               throw new IllegalArgumentException("Invalid client command!");
         }

      }
   }

   public void sendChatMessage(SignedMessage message, MessageType.Parameters params) {
      this.sendPacket(new ChatMessageS2CPacket(this.globalChatMessageIndex++, message.link().sender(), message.link().index(), message.signature(), message.signedBody().toSerialized(this.signatureStorage), message.unsignedContent(), message.filterMask(), params));
      MessageSignatureData messageSignatureData = message.signature();
      if (messageSignatureData != null) {
         this.signatureStorage.add(message.signedBody(), message.signature());
         int i;
         synchronized(this.acknowledgmentValidator) {
            this.acknowledgmentValidator.addPending(messageSignatureData);
            i = this.acknowledgmentValidator.getMessageCount();
         }

         if (i > 4096) {
            this.disconnect(Text.translatable("multiplayer.disconnect.too_many_pending_chats"));
         }

      }
   }

   public void sendProfilelessChatMessage(Text message, MessageType.Parameters params) {
      this.sendPacket(new ProfilelessChatMessageS2CPacket(message, params));
   }

   public SocketAddress getConnectionAddress() {
      return this.connection.getAddress();
   }

   public void reconfigure() {
      this.requestedReconfiguration = true;
      this.cleanUp();
      this.sendPacket(EnterReconfigurationS2CPacket.INSTANCE);
      this.connection.transitionOutbound(ConfigurationStates.S2C);
   }

   public void onQueryPing(QueryPingC2SPacket packet) {
      this.connection.send(new PingResultS2CPacket(packet.getStartTime()));
   }

   public void onPlayerInteractEntity(PlayerInteractEntityC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      if (this.player.isLoaded()) {
         final ServerWorld serverWorld = this.player.getWorld();
         final Entity entity = packet.getEntity(serverWorld);
         this.player.updateLastActionTime();
         this.player.setSneaking(packet.isPlayerSneaking());
         if (entity != null) {
            if (!serverWorld.getWorldBorder().contains(entity.getBlockPos())) {
               return;
            }

            Box box = entity.getBoundingBox();
            if (this.player.canInteractWithEntityIn(box, 3.0)) {
               packet.handle(new PlayerInteractEntityC2SPacket.Handler() {
                  private void processInteract(Hand hand, Interaction action) {
                     ItemStack itemStack = ServerPlayNetworkHandler.this.player.getStackInHand(hand);
                     if (itemStack.isItemEnabled(serverWorld.getEnabledFeatures())) {
                        ItemStack itemStack2 = itemStack.copy();
                        ActionResult actionResult = action.run(ServerPlayNetworkHandler.this.player, entity, hand);
                        if (actionResult instanceof ActionResult.Success) {
                           ActionResult.Success success = (ActionResult.Success)actionResult;
                           ItemStack itemStack3 = success.shouldIncrementStat() ? itemStack2 : ItemStack.EMPTY;
                           Criteria.PLAYER_INTERACTED_WITH_ENTITY.trigger(ServerPlayNetworkHandler.this.player, itemStack3, entity);
                           if (success.swingSource() == ActionResult.SwingSource.SERVER) {
                              ServerPlayNetworkHandler.this.player.swingHand(hand, true);
                           }
                        }

                     }
                  }

                  public void interact(Hand hand) {
                     this.processInteract(hand, PlayerEntity::interact);
                  }

                  public void interactAt(Hand hand, Vec3d pos) {
                     this.processInteract(hand, (player, entityx, handx) -> {
                        return entityx.interactAt(player, pos, handx);
                     });
                  }

                  public void attack() {
                     if (!(entity instanceof ItemEntity) && !(entity instanceof ExperienceOrbEntity) && entity != ServerPlayNetworkHandler.this.player) {
                        label29: {
                           if (entity instanceof PersistentProjectileEntity) {
                              PersistentProjectileEntity persistentProjectileEntity = (PersistentProjectileEntity)entity;
                              if (!persistentProjectileEntity.isAttackable()) {
                                 break label29;
                              }
                           }

                           ItemStack itemStack = ServerPlayNetworkHandler.this.player.getStackInHand(Hand.MAIN_HAND);
                           if (!itemStack.isItemEnabled(serverWorld.getEnabledFeatures())) {
                              return;
                           }

                           ServerPlayNetworkHandler.this.player.attack(entity);
                           return;
                        }
                     }

                     ServerPlayNetworkHandler.this.disconnect(Text.translatable("multiplayer.disconnect.invalid_entity_attacked"));
                     ServerPlayNetworkHandler.LOGGER.warn("Player {} tried to attack an invalid entity", ServerPlayNetworkHandler.this.player.getName().getString());
                  }
               });
            }
         }

      }
   }

   public void onClientStatus(ClientStatusC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      this.player.updateLastActionTime();
      ClientStatusC2SPacket.Mode mode = packet.getMode();
      switch (mode) {
         case PERFORM_RESPAWN:
            if (this.player.notInAnyWorld) {
               this.player.notInAnyWorld = false;
               this.player = this.server.getPlayerManager().respawnPlayer(this.player, true, Entity.RemovalReason.CHANGED_DIMENSION);
               this.syncWithPlayerPosition();
               Criteria.CHANGED_DIMENSION.trigger(this.player, World.END, World.OVERWORLD);
            } else {
               if (this.player.getHealth() > 0.0F) {
                  return;
               }

               this.player = this.server.getPlayerManager().respawnPlayer(this.player, false, Entity.RemovalReason.KILLED);
               this.syncWithPlayerPosition();
               if (this.server.isHardcore()) {
                  this.player.changeGameMode(GameMode.SPECTATOR);
                  ((GameRules.BooleanRule)this.player.getWorld().getGameRules().get(GameRules.SPECTATORS_GENERATE_CHUNKS)).set(false, this.server);
               }
            }
            break;
         case REQUEST_STATS:
            this.player.getStatHandler().sendStats(this.player);
      }

   }

   public void onCloseHandledScreen(CloseHandledScreenC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      this.player.onHandledScreenClosed();
   }

   public void onClickSlot(ClickSlotC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      this.player.updateLastActionTime();
      if (this.player.currentScreenHandler.syncId == packet.syncId()) {
         if (this.player.isSpectator()) {
            this.player.currentScreenHandler.syncState();
         } else if (!this.player.currentScreenHandler.canUse(this.player)) {
            LOGGER.debug("Player {} interacted with invalid menu {}", this.player, this.player.currentScreenHandler);
         } else {
            int i = packet.slot();
            if (!this.player.currentScreenHandler.isValid(i)) {
               LOGGER.debug("Player {} clicked invalid slot index: {}, available slots: {}", new Object[]{this.player.getName(), Integer.valueOf(i), this.player.currentScreenHandler.slots.size()});
            } else {
               boolean bl = packet.revision() != this.player.currentScreenHandler.getRevision();
               this.player.currentScreenHandler.disableSyncing();
               this.player.currentScreenHandler.onSlotClick(i, packet.button(), packet.actionType(), this.player);
               ObjectIterator var4 = Int2ObjectMaps.fastIterable(packet.modifiedStacks()).iterator();

               while(var4.hasNext()) {
                  Int2ObjectMap.Entry entry = (Int2ObjectMap.Entry)var4.next();
                  this.player.currentScreenHandler.setReceivedHash(entry.getIntKey(), (ItemStackHash)entry.getValue());
               }

               this.player.currentScreenHandler.setReceivedCursorHash(packet.cursor());
               this.player.currentScreenHandler.enableSyncing();
               if (bl) {
                  this.player.currentScreenHandler.updateToClient();
               } else {
                  this.player.currentScreenHandler.sendContentUpdates();
               }

            }
         }
      }
   }

   public void onCraftRequest(CraftRequestC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      this.player.updateLastActionTime();
      if (!this.player.isSpectator() && this.player.currentScreenHandler.syncId == packet.syncId()) {
         if (!this.player.currentScreenHandler.canUse(this.player)) {
            LOGGER.debug("Player {} interacted with invalid menu {}", this.player, this.player.currentScreenHandler);
         } else {
            ServerRecipeManager.ServerRecipe serverRecipe = this.server.getRecipeManager().get(packet.recipeId());
            if (serverRecipe != null) {
               RecipeEntry recipeEntry = serverRecipe.parent();
               if (this.player.getRecipeBook().isUnlocked(recipeEntry.id())) {
                  ScreenHandler var5 = this.player.currentScreenHandler;
                  if (var5 instanceof AbstractRecipeScreenHandler) {
                     AbstractRecipeScreenHandler abstractRecipeScreenHandler = (AbstractRecipeScreenHandler)var5;
                     if (recipeEntry.value().getIngredientPlacement().hasNoPlacement()) {
                        LOGGER.debug("Player {} tried to place impossible recipe {}", this.player, recipeEntry.id().getValue());
                        return;
                     }

                     AbstractRecipeScreenHandler.PostFillAction postFillAction = abstractRecipeScreenHandler.fillInputSlots(packet.craftAll(), this.player.isCreative(), recipeEntry, this.player.getWorld(), this.player.getInventory());
                     if (postFillAction == AbstractRecipeScreenHandler.PostFillAction.PLACE_GHOST_RECIPE) {
                        this.sendPacket(new CraftFailedResponseS2CPacket(this.player.currentScreenHandler.syncId, serverRecipe.display().display()));
                     }
                  }

               }
            }
         }
      }
   }

   public void onButtonClick(ButtonClickC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      this.player.updateLastActionTime();
      if (this.player.currentScreenHandler.syncId == packet.syncId() && !this.player.isSpectator()) {
         if (!this.player.currentScreenHandler.canUse(this.player)) {
            LOGGER.debug("Player {} interacted with invalid menu {}", this.player, this.player.currentScreenHandler);
         } else {
            boolean bl = this.player.currentScreenHandler.onButtonClick(this.player, packet.buttonId());
            if (bl) {
               this.player.currentScreenHandler.sendContentUpdates();
            }

         }
      }
   }

   public void onCreativeInventoryAction(CreativeInventoryActionC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      if (this.player.isInCreativeMode()) {
         boolean bl = packet.slot() < 0;
         ItemStack itemStack = packet.stack();
         if (!itemStack.isItemEnabled(this.player.getWorld().getEnabledFeatures())) {
            return;
         }

         boolean bl2 = packet.slot() >= 1 && packet.slot() <= 45;
         boolean bl3 = itemStack.isEmpty() || itemStack.getCount() <= itemStack.getMaxCount();
         if (bl2 && bl3) {
            this.player.playerScreenHandler.getSlot(packet.slot()).setStack(itemStack);
            this.player.playerScreenHandler.setReceivedStack(packet.slot(), itemStack);
            this.player.playerScreenHandler.sendContentUpdates();
         } else if (bl && bl3) {
            if (this.creativeItemDropCooldown.canUse()) {
               this.creativeItemDropCooldown.increment();
               this.player.dropItem(itemStack, true);
            } else {
               LOGGER.warn("Player {} was dropping items too fast in creative mode, ignoring.", this.player.getName().getString());
            }
         }
      }

   }

   public void onUpdateSign(UpdateSignC2SPacket packet) {
      List list = (List)Stream.of(packet.getText()).map(Formatting::strip).collect(Collectors.toList());
      this.filterTexts(list).thenAcceptAsync((texts) -> {
         this.onSignUpdate(packet, texts);
      }, this.server);
   }

   private void onSignUpdate(UpdateSignC2SPacket packet, List signText) {
      this.player.updateLastActionTime();
      ServerWorld serverWorld = this.player.getWorld();
      BlockPos blockPos = packet.getPos();
      if (serverWorld.isChunkLoaded(blockPos)) {
         BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
         if (!(blockEntity instanceof SignBlockEntity)) {
            return;
         }

         SignBlockEntity signBlockEntity = (SignBlockEntity)blockEntity;
         signBlockEntity.tryChangeText(this.player, packet.isFront(), signText);
      }

   }

   public void onUpdatePlayerAbilities(UpdatePlayerAbilitiesC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      this.player.getAbilities().flying = packet.isFlying() && this.player.getAbilities().allowFlying;
   }

   public void onClientOptions(ClientOptionsC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      boolean bl = this.player.isPartVisible(PlayerModelPart.HAT);
      this.player.setClientOptions(packet.options());
      if (this.player.isPartVisible(PlayerModelPart.HAT) != bl) {
         this.server.getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_HAT, this.player));
      }

   }

   public void onUpdateDifficulty(UpdateDifficultyC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      if (!this.player.hasPermissionLevel(2) && !this.isHost()) {
         LOGGER.warn("Player {} tried to change difficulty to {} without required permissions", this.player.getGameProfile().getName(), packet.difficulty().getTranslatableName());
      } else {
         this.server.setDifficulty(packet.difficulty(), false);
      }
   }

   public void onChangeGameMode(ChangeGameModeC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      if (!this.player.hasPermissionLevel(2)) {
         LOGGER.warn("Player {} tried to change game mode to {} without required permissions", this.player.getGameProfile().getName(), packet.mode().getSimpleTranslatableName());
      } else {
         GameModeCommand.execute(this.player, packet.mode());
      }
   }

   public void onUpdateDifficultyLock(UpdateDifficultyLockC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      if (this.player.hasPermissionLevel(2) || this.isHost()) {
         this.server.setDifficultyLocked(packet.isDifficultyLocked());
      }
   }

   public void onPlayerSession(PlayerSessionC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      PublicPlayerSession.Serialized serialized = packet.chatSession();
      PlayerPublicKey.PublicKeyData publicKeyData = this.session != null ? this.session.publicKeyData().data() : null;
      PlayerPublicKey.PublicKeyData publicKeyData2 = serialized.publicKeyData();
      if (!Objects.equals(publicKeyData, publicKeyData2)) {
         if (publicKeyData != null && publicKeyData2.expiresAt().isBefore(publicKeyData.expiresAt())) {
            this.disconnect(PlayerPublicKey.EXPIRED_PUBLIC_KEY_TEXT);
         } else {
            try {
               SignatureVerifier signatureVerifier = this.server.getServicesSignatureVerifier();
               if (signatureVerifier == null) {
                  LOGGER.warn("Ignoring chat session from {} due to missing Services public key", this.player.getGameProfile().getName());
                  return;
               }

               this.setSession(serialized.toSession(this.player.getGameProfile(), signatureVerifier));
            } catch (PlayerPublicKey.PublicKeyException var6) {
               LOGGER.error("Failed to validate profile key: {}", var6.getMessage());
               this.disconnect(var6.getMessageText());
            }

         }
      }
   }

   public void onAcknowledgeReconfiguration(AcknowledgeReconfigurationC2SPacket packet) {
      if (!this.requestedReconfiguration) {
         throw new IllegalStateException("Client acknowledged config, but none was requested");
      } else {
         this.connection.transitionInbound(ConfigurationStates.C2S, new ServerConfigurationNetworkHandler(this.server, this.connection, this.createClientData(this.player.getClientOptions())));
      }
   }

   public void onAcknowledgeChunks(AcknowledgeChunksC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      this.chunkDataSender.onAcknowledgeChunks(packet.desiredChunksPerTick());
   }

   public void onDebugSampleSubscription(DebugSampleSubscriptionC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      this.server.subscribeToDebugSample(this.player, packet.sampleType());
   }

   private void setSession(PublicPlayerSession session) {
      this.session = session;
      this.messageUnpacker = session.createUnpacker(this.player.getUuid());
      this.messageChainTaskQueue.append(() -> {
         this.player.setSession(session);
         this.server.getPlayerManager().sendToAll(new PlayerListS2CPacket(EnumSet.of(PlayerListS2CPacket.Action.INITIALIZE_CHAT), List.of(this.player)));
      });
   }

   public void onCustomPayload(CustomPayloadC2SPacket packet) {
   }

   public void onClientTickEnd(ClientTickEndC2SPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ServerWorld)this.player.getWorld());
      if (!this.movedThisTick) {
         this.player.setMovement(Vec3d.ZERO);
      }

      this.movedThisTick = false;
   }

   private void handleMovement(Vec3d movement) {
      if (movement.lengthSquared() > 9.999999747378752E-6) {
         this.player.updateLastActionTime();
      }

      this.player.setMovement(movement);
      this.movedThisTick = true;
   }

   public boolean isInCreativeMode() {
      return this.player.isInCreativeMode();
   }

   public ServerPlayerEntity getPlayer() {
      return this.player;
   }

   static {
      INVALID_COMMAND_SIGNATURE_TEXT = Text.translatable("chat.disabled.invalid_command_signature").formatted(Formatting.RED);
   }

   @FunctionalInterface
   private interface Interaction {
      ActionResult run(ServerPlayerEntity player, Entity entity, Hand hand);
   }
}
