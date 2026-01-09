package net.minecraft.world;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicLike;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerWaypointHandler;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class GameRules {
   public static final int DEFAULT_RANDOM_TICK_SPEED = 3;
   static final Logger LOGGER = LogUtils.getLogger();
   private static final Map RULE_TYPES = Maps.newTreeMap(Comparator.comparing((key) -> {
      return key.name;
   }));
   public static final Key DO_FIRE_TICK;
   public static final Key ALLOW_FIRE_TICKS_AWAY_FROM_PLAYER;
   public static final Key DO_MOB_GRIEFING;
   public static final Key KEEP_INVENTORY;
   public static final Key DO_MOB_SPAWNING;
   public static final Key DO_MOB_LOOT;
   public static final Key PROJECTILES_CAN_BREAK_BLOCKS;
   public static final Key DO_TILE_DROPS;
   public static final Key DO_ENTITY_DROPS;
   public static final Key COMMAND_BLOCK_OUTPUT;
   public static final Key NATURAL_REGENERATION;
   public static final Key DO_DAYLIGHT_CYCLE;
   public static final Key LOG_ADMIN_COMMANDS;
   public static final Key SHOW_DEATH_MESSAGES;
   public static final Key RANDOM_TICK_SPEED;
   public static final Key SEND_COMMAND_FEEDBACK;
   public static final Key REDUCED_DEBUG_INFO;
   public static final Key SPECTATORS_GENERATE_CHUNKS;
   public static final Key SPAWN_RADIUS;
   public static final Key DISABLE_PLAYER_MOVEMENT_CHECK;
   public static final Key DISABLE_ELYTRA_MOVEMENT_CHECK;
   public static final Key MAX_ENTITY_CRAMMING;
   public static final Key DO_WEATHER_CYCLE;
   public static final Key DO_LIMITED_CRAFTING;
   public static final Key MAX_COMMAND_CHAIN_LENGTH;
   public static final Key MAX_COMMAND_FORK_COUNT;
   public static final Key COMMAND_MODIFICATION_BLOCK_LIMIT;
   public static final Key ANNOUNCE_ADVANCEMENTS;
   public static final Key DISABLE_RAIDS;
   public static final Key DO_INSOMNIA;
   public static final Key DO_IMMEDIATE_RESPAWN;
   public static final Key PLAYERS_NETHER_PORTAL_DEFAULT_DELAY;
   public static final Key PLAYERS_NETHER_PORTAL_CREATIVE_DELAY;
   public static final Key DROWNING_DAMAGE;
   public static final Key FALL_DAMAGE;
   public static final Key FIRE_DAMAGE;
   public static final Key FREEZE_DAMAGE;
   public static final Key DO_PATROL_SPAWNING;
   public static final Key DO_TRADER_SPAWNING;
   public static final Key DO_WARDEN_SPAWNING;
   public static final Key FORGIVE_DEAD_PLAYERS;
   public static final Key UNIVERSAL_ANGER;
   public static final Key PLAYERS_SLEEPING_PERCENTAGE;
   public static final Key BLOCK_EXPLOSION_DROP_DECAY;
   public static final Key MOB_EXPLOSION_DROP_DECAY;
   public static final Key TNT_EXPLOSION_DROP_DECAY;
   public static final Key SNOW_ACCUMULATION_HEIGHT;
   public static final Key WATER_SOURCE_CONVERSION;
   public static final Key LAVA_SOURCE_CONVERSION;
   public static final Key GLOBAL_SOUND_EVENTS;
   public static final Key DO_VINES_SPREAD;
   public static final Key ENDER_PEARLS_VANISH_ON_DEATH;
   public static final Key MINECART_MAX_SPEED;
   public static final Key SPAWN_CHUNK_RADIUS;
   public static final Key TNT_EXPLODES;
   public static final Key LOCATOR_BAR;
   private final Map rules;
   private final FeatureSet enabledFeatures;

   public static Type getRuleType(Key key) {
      return (Type)RULE_TYPES.get(key);
   }

   public static Codec createKeyCodec(Class ruleClass) {
      return Codec.STRING.comapFlatMap((string) -> {
         return (DataResult)RULE_TYPES.entrySet().stream().filter((ruleType) -> {
            return ((Type)ruleType.getValue()).ruleClass == ruleClass;
         }).map(Map.Entry::getKey).filter((key) -> {
            return key.getName().equals(string);
         }).map((key) -> {
            return key;
         }).findFirst().map(DataResult::success).orElseGet(() -> {
            return DataResult.error(() -> {
               return "Invalid game rule ID for type: " + string;
            });
         });
      }, Key::getName);
   }

   private static Key register(String name, Category category, Type type) {
      Key key = new Key(name, category);
      Type type2 = (Type)RULE_TYPES.put(key, type);
      if (type2 != null) {
         throw new IllegalStateException("Duplicate game rule registration for " + name);
      } else {
         return key;
      }
   }

   public GameRules(FeatureSet enabledFeatures, DynamicLike values) {
      this(enabledFeatures);
      this.load(values);
   }

   public GameRules(FeatureSet enabledFeatures) {
      this((Map)streamAllRules(enabledFeatures).collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, (entry) -> {
         return ((Type)entry.getValue()).createRule();
      })), enabledFeatures);
   }

   private static Stream streamAllRules(FeatureSet enabledFeatures) {
      return RULE_TYPES.entrySet().stream().filter((entry) -> {
         return ((Type)entry.getValue()).requiredFeatures.isSubsetOf(enabledFeatures);
      });
   }

   private GameRules(Map rules, FeatureSet enabledFeatures) {
      this.rules = rules;
      this.enabledFeatures = enabledFeatures;
   }

   public Rule get(Key key) {
      Rule rule = (Rule)this.rules.get(key);
      if (rule == null) {
         throw new IllegalArgumentException("Tried to access invalid game rule");
      } else {
         return rule;
      }
   }

   public NbtCompound toNbt() {
      NbtCompound nbtCompound = new NbtCompound();
      this.rules.forEach((key, rule) -> {
         nbtCompound.putString(key.name, rule.serialize());
      });
      return nbtCompound;
   }

   private void load(DynamicLike values) {
      this.rules.forEach((key, rule) -> {
         DataResult var10000 = values.get(key.name).asString();
         Objects.requireNonNull(rule);
         var10000.ifSuccess(rule::deserialize);
      });
   }

   public GameRules copy(FeatureSet enabledFeatures) {
      return new GameRules((Map)streamAllRules(enabledFeatures).collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, (entry) -> {
         return this.rules.containsKey(entry.getKey()) ? ((Rule)this.rules.get(entry.getKey())).copy() : ((Type)entry.getValue()).createRule();
      })), enabledFeatures);
   }

   public void accept(Visitor visitor) {
      RULE_TYPES.forEach((key, type) -> {
         this.accept(visitor, key, type);
      });
   }

   private void accept(Visitor visitor, Key key, Type type) {
      if (type.requiredFeatures.isSubsetOf(this.enabledFeatures)) {
         visitor.visit(key, type);
         type.accept(visitor, key);
      }

   }

   public void setAllValues(GameRules rules, @Nullable MinecraftServer server) {
      rules.rules.keySet().forEach((key) -> {
         this.setValue(key, rules, server);
      });
   }

   private void setValue(Key key, GameRules rules, @Nullable MinecraftServer server) {
      Rule rule = rules.get(key);
      this.get(key).setValue(rule, server);
   }

   public boolean getBoolean(Key rule) {
      return ((BooleanRule)this.get(rule)).get();
   }

   public int getInt(Key rule) {
      return ((IntRule)this.get(rule)).get();
   }

   static {
      DO_FIRE_TICK = register("doFireTick", GameRules.Category.UPDATES, GameRules.BooleanRule.create(true));
      ALLOW_FIRE_TICKS_AWAY_FROM_PLAYER = register("allowFireTicksAwayFromPlayer", GameRules.Category.UPDATES, GameRules.BooleanRule.create(false));
      DO_MOB_GRIEFING = register("mobGriefing", GameRules.Category.MOBS, GameRules.BooleanRule.create(true));
      KEEP_INVENTORY = register("keepInventory", GameRules.Category.PLAYER, GameRules.BooleanRule.create(false));
      DO_MOB_SPAWNING = register("doMobSpawning", GameRules.Category.SPAWNING, GameRules.BooleanRule.create(true));
      DO_MOB_LOOT = register("doMobLoot", GameRules.Category.DROPS, GameRules.BooleanRule.create(true));
      PROJECTILES_CAN_BREAK_BLOCKS = register("projectilesCanBreakBlocks", GameRules.Category.DROPS, GameRules.BooleanRule.create(true));
      DO_TILE_DROPS = register("doTileDrops", GameRules.Category.DROPS, GameRules.BooleanRule.create(true));
      DO_ENTITY_DROPS = register("doEntityDrops", GameRules.Category.DROPS, GameRules.BooleanRule.create(true));
      COMMAND_BLOCK_OUTPUT = register("commandBlockOutput", GameRules.Category.CHAT, GameRules.BooleanRule.create(true));
      NATURAL_REGENERATION = register("naturalRegeneration", GameRules.Category.PLAYER, GameRules.BooleanRule.create(true));
      DO_DAYLIGHT_CYCLE = register("doDaylightCycle", GameRules.Category.UPDATES, GameRules.BooleanRule.create(true));
      LOG_ADMIN_COMMANDS = register("logAdminCommands", GameRules.Category.CHAT, GameRules.BooleanRule.create(true));
      SHOW_DEATH_MESSAGES = register("showDeathMessages", GameRules.Category.CHAT, GameRules.BooleanRule.create(true));
      RANDOM_TICK_SPEED = register("randomTickSpeed", GameRules.Category.UPDATES, GameRules.IntRule.create(3));
      SEND_COMMAND_FEEDBACK = register("sendCommandFeedback", GameRules.Category.CHAT, GameRules.BooleanRule.create(true));
      REDUCED_DEBUG_INFO = register("reducedDebugInfo", GameRules.Category.MISC, GameRules.BooleanRule.create(false, (server, rule) -> {
         byte b = rule.get() ? 22 : 23;
         Iterator var3 = server.getPlayerManager().getPlayerList().iterator();

         while(var3.hasNext()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var3.next();
            serverPlayerEntity.networkHandler.sendPacket(new EntityStatusS2CPacket(serverPlayerEntity, (byte)b));
         }

      }));
      SPECTATORS_GENERATE_CHUNKS = register("spectatorsGenerateChunks", GameRules.Category.PLAYER, GameRules.BooleanRule.create(true));
      SPAWN_RADIUS = register("spawnRadius", GameRules.Category.PLAYER, GameRules.IntRule.create(10));
      DISABLE_PLAYER_MOVEMENT_CHECK = register("disablePlayerMovementCheck", GameRules.Category.PLAYER, GameRules.BooleanRule.create(false));
      DISABLE_ELYTRA_MOVEMENT_CHECK = register("disableElytraMovementCheck", GameRules.Category.PLAYER, GameRules.BooleanRule.create(false));
      MAX_ENTITY_CRAMMING = register("maxEntityCramming", GameRules.Category.MOBS, GameRules.IntRule.create(24));
      DO_WEATHER_CYCLE = register("doWeatherCycle", GameRules.Category.UPDATES, GameRules.BooleanRule.create(true));
      DO_LIMITED_CRAFTING = register("doLimitedCrafting", GameRules.Category.PLAYER, GameRules.BooleanRule.create(false, (server, rule) -> {
         Iterator var2 = server.getPlayerManager().getPlayerList().iterator();

         while(var2.hasNext()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var2.next();
            serverPlayerEntity.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.LIMITED_CRAFTING_TOGGLED, rule.get() ? 1.0F : 0.0F));
         }

      }));
      MAX_COMMAND_CHAIN_LENGTH = register("maxCommandChainLength", GameRules.Category.MISC, GameRules.IntRule.create(65536));
      MAX_COMMAND_FORK_COUNT = register("maxCommandForkCount", GameRules.Category.MISC, GameRules.IntRule.create(65536));
      COMMAND_MODIFICATION_BLOCK_LIMIT = register("commandModificationBlockLimit", GameRules.Category.MISC, GameRules.IntRule.create(32768));
      ANNOUNCE_ADVANCEMENTS = register("announceAdvancements", GameRules.Category.CHAT, GameRules.BooleanRule.create(true));
      DISABLE_RAIDS = register("disableRaids", GameRules.Category.MOBS, GameRules.BooleanRule.create(false));
      DO_INSOMNIA = register("doInsomnia", GameRules.Category.SPAWNING, GameRules.BooleanRule.create(true));
      DO_IMMEDIATE_RESPAWN = register("doImmediateRespawn", GameRules.Category.PLAYER, GameRules.BooleanRule.create(false, (server, rule) -> {
         Iterator var2 = server.getPlayerManager().getPlayerList().iterator();

         while(var2.hasNext()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var2.next();
            serverPlayerEntity.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.IMMEDIATE_RESPAWN, rule.get() ? 1.0F : 0.0F));
         }

      }));
      PLAYERS_NETHER_PORTAL_DEFAULT_DELAY = register("playersNetherPortalDefaultDelay", GameRules.Category.PLAYER, GameRules.IntRule.create(80));
      PLAYERS_NETHER_PORTAL_CREATIVE_DELAY = register("playersNetherPortalCreativeDelay", GameRules.Category.PLAYER, GameRules.IntRule.create(0));
      DROWNING_DAMAGE = register("drowningDamage", GameRules.Category.PLAYER, GameRules.BooleanRule.create(true));
      FALL_DAMAGE = register("fallDamage", GameRules.Category.PLAYER, GameRules.BooleanRule.create(true));
      FIRE_DAMAGE = register("fireDamage", GameRules.Category.PLAYER, GameRules.BooleanRule.create(true));
      FREEZE_DAMAGE = register("freezeDamage", GameRules.Category.PLAYER, GameRules.BooleanRule.create(true));
      DO_PATROL_SPAWNING = register("doPatrolSpawning", GameRules.Category.SPAWNING, GameRules.BooleanRule.create(true));
      DO_TRADER_SPAWNING = register("doTraderSpawning", GameRules.Category.SPAWNING, GameRules.BooleanRule.create(true));
      DO_WARDEN_SPAWNING = register("doWardenSpawning", GameRules.Category.SPAWNING, GameRules.BooleanRule.create(true));
      FORGIVE_DEAD_PLAYERS = register("forgiveDeadPlayers", GameRules.Category.MOBS, GameRules.BooleanRule.create(true));
      UNIVERSAL_ANGER = register("universalAnger", GameRules.Category.MOBS, GameRules.BooleanRule.create(false));
      PLAYERS_SLEEPING_PERCENTAGE = register("playersSleepingPercentage", GameRules.Category.PLAYER, GameRules.IntRule.create(100));
      BLOCK_EXPLOSION_DROP_DECAY = register("blockExplosionDropDecay", GameRules.Category.DROPS, GameRules.BooleanRule.create(true));
      MOB_EXPLOSION_DROP_DECAY = register("mobExplosionDropDecay", GameRules.Category.DROPS, GameRules.BooleanRule.create(true));
      TNT_EXPLOSION_DROP_DECAY = register("tntExplosionDropDecay", GameRules.Category.DROPS, GameRules.BooleanRule.create(false));
      SNOW_ACCUMULATION_HEIGHT = register("snowAccumulationHeight", GameRules.Category.UPDATES, GameRules.IntRule.create(1));
      WATER_SOURCE_CONVERSION = register("waterSourceConversion", GameRules.Category.UPDATES, GameRules.BooleanRule.create(true));
      LAVA_SOURCE_CONVERSION = register("lavaSourceConversion", GameRules.Category.UPDATES, GameRules.BooleanRule.create(false));
      GLOBAL_SOUND_EVENTS = register("globalSoundEvents", GameRules.Category.MISC, GameRules.BooleanRule.create(true));
      DO_VINES_SPREAD = register("doVinesSpread", GameRules.Category.UPDATES, GameRules.BooleanRule.create(true));
      ENDER_PEARLS_VANISH_ON_DEATH = register("enderPearlsVanishOnDeath", GameRules.Category.PLAYER, GameRules.BooleanRule.create(true));
      MINECART_MAX_SPEED = register("minecartMaxSpeed", GameRules.Category.MISC, GameRules.IntRule.create(8, 1, 1000, FeatureSet.of(FeatureFlags.MINECART_IMPROVEMENTS), (server, value) -> {
      }));
      SPAWN_CHUNK_RADIUS = register("spawnChunkRadius", GameRules.Category.MISC, GameRules.IntRule.create(2, 0, 32, FeatureSet.empty(), (server, value) -> {
         ServerWorld serverWorld = server.getOverworld();
         serverWorld.setSpawnPos(serverWorld.getSpawnPos(), serverWorld.getSpawnAngle());
      }));
      TNT_EXPLODES = register("tntExplodes", GameRules.Category.MISC, GameRules.BooleanRule.create(true));
      LOCATOR_BAR = register("locatorBar", GameRules.Category.PLAYER, GameRules.BooleanRule.create(true, (server, rule) -> {
         server.getWorlds().forEach((world) -> {
            ServerWaypointHandler serverWaypointHandler = world.getWaypointHandler();
            if (rule.get()) {
               List var10000 = world.getPlayers();
               Objects.requireNonNull(serverWaypointHandler);
               var10000.forEach(serverWaypointHandler::updatePlayerPos);
            } else {
               serverWaypointHandler.clear();
            }

         });
      }));
   }

   public static class Type {
      final Supplier argumentType;
      private final Function ruleFactory;
      final BiConsumer changeCallback;
      private final Acceptor ruleAcceptor;
      final Class ruleClass;
      final FeatureSet requiredFeatures;

      Type(Supplier argumentType, Function ruleFactory, BiConsumer changeCallback, Acceptor ruleAcceptor, Class ruleClass, FeatureSet requiredFeatures) {
         this.argumentType = argumentType;
         this.ruleFactory = ruleFactory;
         this.changeCallback = changeCallback;
         this.ruleAcceptor = ruleAcceptor;
         this.ruleClass = ruleClass;
         this.requiredFeatures = requiredFeatures;
      }

      public RequiredArgumentBuilder argument(String name) {
         return CommandManager.argument(name, (ArgumentType)this.argumentType.get());
      }

      public Rule createRule() {
         return (Rule)this.ruleFactory.apply(this);
      }

      public void accept(Visitor consumer, Key key) {
         this.ruleAcceptor.call(consumer, key, this);
      }

      public FeatureSet getRequiredFeatures() {
         return this.requiredFeatures;
      }
   }

   public static final class Key {
      final String name;
      private final Category category;

      public Key(String name, Category category) {
         this.name = name;
         this.category = category;
      }

      public String toString() {
         return this.name;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else {
            return o instanceof Key && ((Key)o).name.equals(this.name);
         }
      }

      public int hashCode() {
         return this.name.hashCode();
      }

      public String getName() {
         return this.name;
      }

      public String getTranslationKey() {
         return "gamerule." + this.name;
      }

      public Category getCategory() {
         return this.category;
      }
   }

   public static enum Category {
      PLAYER("gamerule.category.player"),
      MOBS("gamerule.category.mobs"),
      SPAWNING("gamerule.category.spawning"),
      DROPS("gamerule.category.drops"),
      UPDATES("gamerule.category.updates"),
      CHAT("gamerule.category.chat"),
      MISC("gamerule.category.misc");

      private final String category;

      private Category(final String category) {
         this.category = category;
      }

      public String getCategory() {
         return this.category;
      }

      // $FF: synthetic method
      private static Category[] method_36694() {
         return new Category[]{PLAYER, MOBS, SPAWNING, DROPS, UPDATES, CHAT, MISC};
      }
   }

   public abstract static class Rule {
      protected final Type type;

      public Rule(Type type) {
         this.type = type;
      }

      protected abstract void setFromArgument(CommandContext context, String name);

      public void set(CommandContext context, String name) {
         this.setFromArgument(context, name);
         this.changed(((ServerCommandSource)context.getSource()).getServer());
      }

      protected void changed(@Nullable MinecraftServer server) {
         if (server != null) {
            this.type.changeCallback.accept(server, this.getThis());
         }

      }

      protected abstract void deserialize(String value);

      public abstract String serialize();

      public String toString() {
         return this.serialize();
      }

      public abstract int getCommandResult();

      protected abstract Rule getThis();

      protected abstract Rule copy();

      public abstract void setValue(Rule rule, @Nullable MinecraftServer server);
   }

   public interface Visitor {
      default void visit(Key key, Type type) {
      }

      default void visitBoolean(Key key, Type type) {
      }

      default void visitInt(Key key, Type type) {
      }
   }

   public static class BooleanRule extends Rule {
      private boolean value;

      private static Type create(boolean initialValue, BiConsumer changeCallback, FeatureSet requiredFeatures) {
         return new Type(BoolArgumentType::bool, (type) -> {
            return new BooleanRule(type, initialValue);
         }, changeCallback, Visitor::visitBoolean, BooleanRule.class, requiredFeatures);
      }

      static Type create(boolean initialValue, BiConsumer changeCallback) {
         return new Type(BoolArgumentType::bool, (type) -> {
            return new BooleanRule(type, initialValue);
         }, changeCallback, Visitor::visitBoolean, BooleanRule.class, FeatureSet.empty());
      }

      static Type create(boolean initialValue) {
         return create(initialValue, (server, rule) -> {
         });
      }

      public BooleanRule(Type type, boolean initialValue) {
         super(type);
         this.value = initialValue;
      }

      protected void setFromArgument(CommandContext context, String name) {
         this.value = BoolArgumentType.getBool(context, name);
      }

      public boolean get() {
         return this.value;
      }

      public void set(boolean value, @Nullable MinecraftServer server) {
         this.value = value;
         this.changed(server);
      }

      public String serialize() {
         return Boolean.toString(this.value);
      }

      protected void deserialize(String value) {
         this.value = Boolean.parseBoolean(value);
      }

      public int getCommandResult() {
         return this.value ? 1 : 0;
      }

      protected BooleanRule getThis() {
         return this;
      }

      protected BooleanRule copy() {
         return new BooleanRule(this.type, this.value);
      }

      public void setValue(BooleanRule booleanRule, @Nullable MinecraftServer minecraftServer) {
         this.value = booleanRule.value;
         this.changed(minecraftServer);
      }

      // $FF: synthetic method
      protected Rule copy() {
         return this.copy();
      }

      // $FF: synthetic method
      protected Rule getThis() {
         return this.getThis();
      }
   }

   public static class IntRule extends Rule {
      private int value;

      private static Type create(int initialValue, BiConsumer changeCallback) {
         return new Type(IntegerArgumentType::integer, (type) -> {
            return new IntRule(type, initialValue);
         }, changeCallback, Visitor::visitInt, IntRule.class, FeatureSet.empty());
      }

      static Type create(int initialValue, int min, int max, FeatureSet requiredFeatures, BiConsumer changeCallback) {
         return new Type(() -> {
            return IntegerArgumentType.integer(min, max);
         }, (type) -> {
            return new IntRule(type, initialValue);
         }, changeCallback, Visitor::visitInt, IntRule.class, requiredFeatures);
      }

      static Type create(int initialValue) {
         return create(initialValue, (server, rule) -> {
         });
      }

      public IntRule(Type rule, int initialValue) {
         super(rule);
         this.value = initialValue;
      }

      protected void setFromArgument(CommandContext context, String name) {
         this.value = IntegerArgumentType.getInteger(context, name);
      }

      public int get() {
         return this.value;
      }

      public void set(int value, @Nullable MinecraftServer server) {
         this.value = value;
         this.changed(server);
      }

      public String serialize() {
         return Integer.toString(this.value);
      }

      protected void deserialize(String value) {
         this.value = parseInt(value);
      }

      public boolean validateAndSet(String input) {
         try {
            StringReader stringReader = new StringReader(input);
            this.value = (Integer)((ArgumentType)this.type.argumentType.get()).parse(stringReader);
            return !stringReader.canRead();
         } catch (CommandSyntaxException var3) {
            return false;
         }
      }

      private static int parseInt(String input) {
         if (!input.isEmpty()) {
            try {
               return Integer.parseInt(input);
            } catch (NumberFormatException var2) {
               GameRules.LOGGER.warn("Failed to parse integer {}", input);
            }
         }

         return 0;
      }

      public int getCommandResult() {
         return this.value;
      }

      protected IntRule getThis() {
         return this;
      }

      protected IntRule copy() {
         return new IntRule(this.type, this.value);
      }

      public void setValue(IntRule intRule, @Nullable MinecraftServer minecraftServer) {
         this.value = intRule.value;
         this.changed(minecraftServer);
      }

      // $FF: synthetic method
      protected Rule copy() {
         return this.copy();
      }

      // $FF: synthetic method
      protected Rule getThis() {
         return this.getThis();
      }
   }

   private interface Acceptor {
      void call(Visitor consumer, Key key, Type type);
   }
}
