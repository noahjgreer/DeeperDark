package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.command.CommandSource;
import net.minecraft.command.PermissionLevelSource;
import net.minecraft.command.ReturnValueConsumer;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedCommandArguments;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.Tracer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.thread.FutureQueue;
import net.minecraft.world.GameRules;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

public class ServerCommandSource implements AbstractServerCommandSource, PermissionLevelSource, CommandSource {
   public static final SimpleCommandExceptionType REQUIRES_PLAYER_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("permissions.requires.player"));
   public static final SimpleCommandExceptionType REQUIRES_ENTITY_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("permissions.requires.entity"));
   private final CommandOutput output;
   private final Vec3d position;
   private final ServerWorld world;
   private final int level;
   private final String name;
   private final Text displayName;
   private final MinecraftServer server;
   private final boolean silent;
   @Nullable
   private final Entity entity;
   private final ReturnValueConsumer returnValueConsumer;
   private final EntityAnchorArgumentType.EntityAnchor entityAnchor;
   private final Vec2f rotation;
   private final SignedCommandArguments signedArguments;
   private final FutureQueue messageChainTaskQueue;

   public ServerCommandSource(CommandOutput output, Vec3d pos, Vec2f rot, ServerWorld world, int level, String name, Text displayName, MinecraftServer server, @Nullable Entity entity) {
      this(output, pos, rot, world, level, name, displayName, server, entity, false, ReturnValueConsumer.EMPTY, EntityAnchorArgumentType.EntityAnchor.FEET, SignedCommandArguments.EMPTY, FutureQueue.immediate(server));
   }

   protected ServerCommandSource(CommandOutput output, Vec3d pos, Vec2f rot, ServerWorld world, int level, String name, Text displayName, MinecraftServer server, @Nullable Entity entity, boolean silent, ReturnValueConsumer resultStorer, EntityAnchorArgumentType.EntityAnchor entityAnchor, SignedCommandArguments signedArguments, FutureQueue messageChainTaskQueue) {
      this.output = output;
      this.position = pos;
      this.world = world;
      this.silent = silent;
      this.entity = entity;
      this.level = level;
      this.name = name;
      this.displayName = displayName;
      this.server = server;
      this.returnValueConsumer = resultStorer;
      this.entityAnchor = entityAnchor;
      this.rotation = rot;
      this.signedArguments = signedArguments;
      this.messageChainTaskQueue = messageChainTaskQueue;
   }

   public ServerCommandSource withOutput(CommandOutput output) {
      return this.output == output ? this : new ServerCommandSource(output, this.position, this.rotation, this.world, this.level, this.name, this.displayName, this.server, this.entity, this.silent, this.returnValueConsumer, this.entityAnchor, this.signedArguments, this.messageChainTaskQueue);
   }

   public ServerCommandSource withEntity(Entity entity) {
      return this.entity == entity ? this : new ServerCommandSource(this.output, this.position, this.rotation, this.world, this.level, entity.getName().getString(), entity.getDisplayName(), this.server, entity, this.silent, this.returnValueConsumer, this.entityAnchor, this.signedArguments, this.messageChainTaskQueue);
   }

   public ServerCommandSource withPosition(Vec3d position) {
      return this.position.equals(position) ? this : new ServerCommandSource(this.output, position, this.rotation, this.world, this.level, this.name, this.displayName, this.server, this.entity, this.silent, this.returnValueConsumer, this.entityAnchor, this.signedArguments, this.messageChainTaskQueue);
   }

   public ServerCommandSource withRotation(Vec2f rotation) {
      return this.rotation.equals(rotation) ? this : new ServerCommandSource(this.output, this.position, rotation, this.world, this.level, this.name, this.displayName, this.server, this.entity, this.silent, this.returnValueConsumer, this.entityAnchor, this.signedArguments, this.messageChainTaskQueue);
   }

   public ServerCommandSource withReturnValueConsumer(ReturnValueConsumer returnValueConsumer) {
      return Objects.equals(this.returnValueConsumer, returnValueConsumer) ? this : new ServerCommandSource(this.output, this.position, this.rotation, this.world, this.level, this.name, this.displayName, this.server, this.entity, this.silent, returnValueConsumer, this.entityAnchor, this.signedArguments, this.messageChainTaskQueue);
   }

   public ServerCommandSource mergeReturnValueConsumers(ReturnValueConsumer returnValueConsumer, BinaryOperator merger) {
      ReturnValueConsumer returnValueConsumer2 = (ReturnValueConsumer)merger.apply(this.returnValueConsumer, returnValueConsumer);
      return this.withReturnValueConsumer(returnValueConsumer2);
   }

   public ServerCommandSource withSilent() {
      return !this.silent && !this.output.cannotBeSilenced() ? new ServerCommandSource(this.output, this.position, this.rotation, this.world, this.level, this.name, this.displayName, this.server, this.entity, true, this.returnValueConsumer, this.entityAnchor, this.signedArguments, this.messageChainTaskQueue) : this;
   }

   public ServerCommandSource withLevel(int level) {
      return level == this.level ? this : new ServerCommandSource(this.output, this.position, this.rotation, this.world, level, this.name, this.displayName, this.server, this.entity, this.silent, this.returnValueConsumer, this.entityAnchor, this.signedArguments, this.messageChainTaskQueue);
   }

   public ServerCommandSource withMaxLevel(int level) {
      return level <= this.level ? this : new ServerCommandSource(this.output, this.position, this.rotation, this.world, level, this.name, this.displayName, this.server, this.entity, this.silent, this.returnValueConsumer, this.entityAnchor, this.signedArguments, this.messageChainTaskQueue);
   }

   public ServerCommandSource withEntityAnchor(EntityAnchorArgumentType.EntityAnchor anchor) {
      return anchor == this.entityAnchor ? this : new ServerCommandSource(this.output, this.position, this.rotation, this.world, this.level, this.name, this.displayName, this.server, this.entity, this.silent, this.returnValueConsumer, anchor, this.signedArguments, this.messageChainTaskQueue);
   }

   public ServerCommandSource withWorld(ServerWorld world) {
      if (world == this.world) {
         return this;
      } else {
         double d = DimensionType.getCoordinateScaleFactor(this.world.getDimension(), world.getDimension());
         Vec3d vec3d = new Vec3d(this.position.x * d, this.position.y, this.position.z * d);
         return new ServerCommandSource(this.output, vec3d, this.rotation, world, this.level, this.name, this.displayName, this.server, this.entity, this.silent, this.returnValueConsumer, this.entityAnchor, this.signedArguments, this.messageChainTaskQueue);
      }
   }

   public ServerCommandSource withLookingAt(Entity entity, EntityAnchorArgumentType.EntityAnchor anchor) {
      return this.withLookingAt(anchor.positionAt(entity));
   }

   public ServerCommandSource withLookingAt(Vec3d position) {
      Vec3d vec3d = this.entityAnchor.positionAt(this);
      double d = position.x - vec3d.x;
      double e = position.y - vec3d.y;
      double f = position.z - vec3d.z;
      double g = Math.sqrt(d * d + f * f);
      float h = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(e, g) * 57.2957763671875)));
      float i = MathHelper.wrapDegrees((float)(MathHelper.atan2(f, d) * 57.2957763671875) - 90.0F);
      return this.withRotation(new Vec2f(h, i));
   }

   public ServerCommandSource withSignedArguments(SignedCommandArguments signedArguments, FutureQueue messageChainTaskQueue) {
      return signedArguments == this.signedArguments && messageChainTaskQueue == this.messageChainTaskQueue ? this : new ServerCommandSource(this.output, this.position, this.rotation, this.world, this.level, this.name, this.displayName, this.server, this.entity, this.silent, this.returnValueConsumer, this.entityAnchor, signedArguments, messageChainTaskQueue);
   }

   public Text getDisplayName() {
      return this.displayName;
   }

   public String getName() {
      return this.name;
   }

   public boolean hasPermissionLevel(int level) {
      return this.level >= level;
   }

   public Vec3d getPosition() {
      return this.position;
   }

   public ServerWorld getWorld() {
      return this.world;
   }

   @Nullable
   public Entity getEntity() {
      return this.entity;
   }

   public Entity getEntityOrThrow() throws CommandSyntaxException {
      if (this.entity == null) {
         throw REQUIRES_ENTITY_EXCEPTION.create();
      } else {
         return this.entity;
      }
   }

   public ServerPlayerEntity getPlayerOrThrow() throws CommandSyntaxException {
      Entity var2 = this.entity;
      if (var2 instanceof ServerPlayerEntity serverPlayerEntity) {
         return serverPlayerEntity;
      } else {
         throw REQUIRES_PLAYER_EXCEPTION.create();
      }
   }

   @Nullable
   public ServerPlayerEntity getPlayer() {
      Entity var2 = this.entity;
      ServerPlayerEntity var10000;
      if (var2 instanceof ServerPlayerEntity serverPlayerEntity) {
         var10000 = serverPlayerEntity;
      } else {
         var10000 = null;
      }

      return var10000;
   }

   public boolean isExecutedByPlayer() {
      return this.entity instanceof ServerPlayerEntity;
   }

   public Vec2f getRotation() {
      return this.rotation;
   }

   public MinecraftServer getServer() {
      return this.server;
   }

   public EntityAnchorArgumentType.EntityAnchor getEntityAnchor() {
      return this.entityAnchor;
   }

   public SignedCommandArguments getSignedArguments() {
      return this.signedArguments;
   }

   public FutureQueue getMessageChainTaskQueue() {
      return this.messageChainTaskQueue;
   }

   public boolean shouldFilterText(ServerPlayerEntity recipient) {
      ServerPlayerEntity serverPlayerEntity = this.getPlayer();
      if (recipient == serverPlayerEntity) {
         return false;
      } else {
         return serverPlayerEntity != null && serverPlayerEntity.shouldFilterText() || recipient.shouldFilterText();
      }
   }

   public void sendChatMessage(SentMessage message, boolean filterMaskEnabled, MessageType.Parameters params) {
      if (!this.silent) {
         ServerPlayerEntity serverPlayerEntity = this.getPlayer();
         if (serverPlayerEntity != null) {
            serverPlayerEntity.sendChatMessage(message, filterMaskEnabled, params);
         } else {
            this.output.sendMessage(params.applyChatDecoration(message.content()));
         }

      }
   }

   public void sendMessage(Text message) {
      if (!this.silent) {
         ServerPlayerEntity serverPlayerEntity = this.getPlayer();
         if (serverPlayerEntity != null) {
            serverPlayerEntity.sendMessage(message);
         } else {
            this.output.sendMessage(message);
         }

      }
   }

   public void sendFeedback(Supplier feedbackSupplier, boolean broadcastToOps) {
      boolean bl = this.output.shouldReceiveFeedback() && !this.silent;
      boolean bl2 = broadcastToOps && this.output.shouldBroadcastConsoleToOps() && !this.silent;
      if (bl || bl2) {
         Text text = (Text)feedbackSupplier.get();
         if (bl) {
            this.output.sendMessage(text);
         }

         if (bl2) {
            this.sendToOps(text);
         }

      }
   }

   private void sendToOps(Text message) {
      Text text = Text.translatable("chat.type.admin", this.getDisplayName(), message).formatted(Formatting.GRAY, Formatting.ITALIC);
      if (this.server.getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK)) {
         Iterator var3 = this.server.getPlayerManager().getPlayerList().iterator();

         while(var3.hasNext()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var3.next();
            if (serverPlayerEntity.getCommandOutput() != this.output && this.server.getPlayerManager().isOperator(serverPlayerEntity.getGameProfile())) {
               serverPlayerEntity.sendMessage(text);
            }
         }
      }

      if (this.output != this.server && this.server.getGameRules().getBoolean(GameRules.LOG_ADMIN_COMMANDS)) {
         this.server.sendMessage(text);
      }

   }

   public void sendError(Text message) {
      if (this.output.shouldTrackOutput() && !this.silent) {
         this.output.sendMessage(Text.empty().append(message).formatted(Formatting.RED));
      }

   }

   public ReturnValueConsumer getReturnValueConsumer() {
      return this.returnValueConsumer;
   }

   public Collection getPlayerNames() {
      return Lists.newArrayList(this.server.getPlayerNames());
   }

   public Collection getTeamNames() {
      return this.server.getScoreboard().getTeamNames();
   }

   public Stream getSoundIds() {
      return Registries.SOUND_EVENT.stream().map(SoundEvent::id);
   }

   public CompletableFuture getCompletions(CommandContext context) {
      return Suggestions.empty();
   }

   public CompletableFuture listIdSuggestions(RegistryKey registryRef, CommandSource.SuggestedIdType suggestedIdType, SuggestionsBuilder builder, CommandContext context) {
      if (registryRef == RegistryKeys.RECIPE) {
         return CommandSource.suggestIdentifiers(this.server.getRecipeManager().values().stream().map((recipe) -> {
            return recipe.id().getValue();
         }), builder);
      } else if (registryRef == RegistryKeys.ADVANCEMENT) {
         Collection collection = this.server.getAdvancementLoader().getAdvancements();
         return CommandSource.suggestIdentifiers(collection.stream().map(AdvancementEntry::id), builder);
      } else {
         return (CompletableFuture)this.getRegistry(registryRef).map((registry) -> {
            this.suggestIdentifiers(registry, suggestedIdType, builder);
            return builder.buildFuture();
         }).orElseGet(Suggestions::empty);
      }
   }

   private Optional getRegistry(RegistryKey registryRef) {
      Optional optional = this.getRegistryManager().getOptional(registryRef);
      return optional.isPresent() ? optional : this.server.getReloadableRegistries().createRegistryLookup().getOptional(registryRef);
   }

   public Set getWorldKeys() {
      return this.server.getWorldRegistryKeys();
   }

   public DynamicRegistryManager getRegistryManager() {
      return this.server.getRegistryManager();
   }

   public FeatureSet getEnabledFeatures() {
      return this.world.getEnabledFeatures();
   }

   public CommandDispatcher getDispatcher() {
      return this.getServer().getCommandFunctionManager().getDispatcher();
   }

   public void handleException(CommandExceptionType type, Message message, boolean silent, @Nullable Tracer tracer) {
      if (tracer != null) {
         tracer.traceError(message.getString());
      }

      if (!silent) {
         this.sendError(Texts.toText(message));
      }

   }

   public boolean isSilent() {
      return this.silent;
   }

   // $FF: synthetic method
   public AbstractServerCommandSource withReturnValueConsumer(final ReturnValueConsumer returnValueConsumer) {
      return this.withReturnValueConsumer(returnValueConsumer);
   }
}
