package net.minecraft.server.dedicated;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ServerPropertiesHandler extends AbstractPropertiesHandler {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final Pattern SHA1_PATTERN = Pattern.compile("^[a-fA-F0-9]{40}$");
   private static final Splitter COMMA_SPLITTER = Splitter.on(',').trimResults();
   public final boolean onlineMode = this.parseBoolean("online-mode", true);
   public final boolean preventProxyConnections = this.parseBoolean("prevent-proxy-connections", false);
   public final String serverIp = this.getString("server-ip", "");
   public final boolean pvp = this.parseBoolean("pvp", true);
   public final boolean allowFlight = this.parseBoolean("allow-flight", false);
   public final String motd = this.getString("motd", "A Minecraft Server");
   public final String bugReportLink = this.getString("bug-report-link", "");
   public final boolean forceGameMode = this.parseBoolean("force-gamemode", false);
   public final boolean enforceWhitelist = this.parseBoolean("enforce-whitelist", false);
   public final Difficulty difficulty;
   public final GameMode gameMode;
   public final String levelName;
   public final int serverPort;
   @Nullable
   public final Boolean announcePlayerAchievements;
   public final boolean enableQuery;
   public final int queryPort;
   public final boolean enableRcon;
   public final int rconPort;
   public final String rconPassword;
   public final boolean hardcore;
   public final boolean allowNether;
   public final boolean spawnMonsters;
   public final boolean useNativeTransport;
   public final boolean enableCommandBlock;
   public final int spawnProtection;
   public final int opPermissionLevel;
   public final int functionPermissionLevel;
   public final long maxTickTime;
   public final int maxChainedNeighborUpdates;
   public final int rateLimit;
   public final int viewDistance;
   public final int simulationDistance;
   public final int maxPlayers;
   public final int networkCompressionThreshold;
   public final boolean broadcastRconToOps;
   public final boolean broadcastConsoleToOps;
   public final int maxWorldSize;
   public final boolean syncChunkWrites;
   public final String regionFileCompression;
   public final boolean enableJmxMonitoring;
   public final boolean enableStatus;
   public final boolean hideOnlinePlayers;
   public final int entityBroadcastRangePercentage;
   public final String textFilteringConfig;
   public final int textFilteringVersion;
   public final Optional serverResourcePackProperties;
   public final DataPackSettings dataPackSettings;
   public final AbstractPropertiesHandler.PropertyAccessor playerIdleTimeout;
   public final AbstractPropertiesHandler.PropertyAccessor whiteList;
   public final boolean enforceSecureProfile;
   public final boolean logIps;
   public final int pauseWhenEmptySeconds;
   private final WorldGenProperties worldGenProperties;
   public final GeneratorOptions generatorOptions;
   public boolean acceptsTransfers;

   public ServerPropertiesHandler(Properties properties) {
      super(properties);
      this.difficulty = (Difficulty)this.get("difficulty", combineParser(Difficulty::byId, Difficulty::byName), Difficulty::getName, Difficulty.EASY);
      this.gameMode = (GameMode)this.get("gamemode", combineParser(GameMode::byIndex, GameMode::byId), GameMode::getId, GameMode.SURVIVAL);
      this.levelName = this.getString("level-name", "world");
      this.serverPort = this.getInt("server-port", 25565);
      this.announcePlayerAchievements = this.getDeprecatedBoolean("announce-player-achievements");
      this.enableQuery = this.parseBoolean("enable-query", false);
      this.queryPort = this.getInt("query.port", 25565);
      this.enableRcon = this.parseBoolean("enable-rcon", false);
      this.rconPort = this.getInt("rcon.port", 25575);
      this.rconPassword = this.getString("rcon.password", "");
      this.hardcore = this.parseBoolean("hardcore", false);
      this.allowNether = this.parseBoolean("allow-nether", true);
      this.spawnMonsters = this.parseBoolean("spawn-monsters", true);
      this.useNativeTransport = this.parseBoolean("use-native-transport", true);
      this.enableCommandBlock = this.parseBoolean("enable-command-block", false);
      this.spawnProtection = this.getInt("spawn-protection", 16);
      this.opPermissionLevel = this.getInt("op-permission-level", 4);
      this.functionPermissionLevel = this.getInt("function-permission-level", 2);
      this.maxTickTime = this.parseLong("max-tick-time", TimeUnit.MINUTES.toMillis(1L));
      this.maxChainedNeighborUpdates = this.getInt("max-chained-neighbor-updates", 1000000);
      this.rateLimit = this.getInt("rate-limit", 0);
      this.viewDistance = this.getInt("view-distance", 10);
      this.simulationDistance = this.getInt("simulation-distance", 10);
      this.maxPlayers = this.getInt("max-players", 20);
      this.networkCompressionThreshold = this.getInt("network-compression-threshold", 256);
      this.broadcastRconToOps = this.parseBoolean("broadcast-rcon-to-ops", true);
      this.broadcastConsoleToOps = this.parseBoolean("broadcast-console-to-ops", true);
      this.maxWorldSize = this.transformedParseInt("max-world-size", (maxWorldSize) -> {
         return MathHelper.clamp(maxWorldSize, 1, 29999984);
      }, 29999984);
      this.syncChunkWrites = this.parseBoolean("sync-chunk-writes", true);
      this.regionFileCompression = this.getString("region-file-compression", "deflate");
      this.enableJmxMonitoring = this.parseBoolean("enable-jmx-monitoring", false);
      this.enableStatus = this.parseBoolean("enable-status", true);
      this.hideOnlinePlayers = this.parseBoolean("hide-online-players", false);
      this.entityBroadcastRangePercentage = this.transformedParseInt("entity-broadcast-range-percentage", (percentage) -> {
         return MathHelper.clamp(percentage, 10, 1000);
      }, 100);
      this.textFilteringConfig = this.getString("text-filtering-config", "");
      this.textFilteringVersion = this.getInt("text-filtering-version", 0);
      this.playerIdleTimeout = this.intAccessor("player-idle-timeout", 0);
      this.whiteList = this.booleanAccessor("white-list", false);
      this.enforceSecureProfile = this.parseBoolean("enforce-secure-profile", true);
      this.logIps = this.parseBoolean("log-ips", true);
      this.pauseWhenEmptySeconds = this.getInt("pause-when-empty-seconds", 60);
      this.acceptsTransfers = this.parseBoolean("accepts-transfers", false);
      String string = this.getString("level-seed", "");
      boolean bl = this.parseBoolean("generate-structures", true);
      long l = GeneratorOptions.parseSeed(string).orElse(GeneratorOptions.getRandomSeed());
      this.generatorOptions = new GeneratorOptions(l, bl, false);
      this.worldGenProperties = new WorldGenProperties((JsonObject)this.get("generator-settings", (generatorSettings) -> {
         return JsonHelper.deserialize(!generatorSettings.isEmpty() ? generatorSettings : "{}");
      }, new JsonObject()), (String)this.get("level-type", (type) -> {
         return type.toLowerCase(Locale.ROOT);
      }, WorldPresets.DEFAULT.getValue().toString()));
      this.serverResourcePackProperties = getServerResourcePackProperties(this.getString("resource-pack-id", ""), this.getString("resource-pack", ""), this.getString("resource-pack-sha1", ""), this.getDeprecatedString("resource-pack-hash"), this.parseBoolean("require-resource-pack", false), this.getString("resource-pack-prompt", ""));
      this.dataPackSettings = parseDataPackSettings(this.getString("initial-enabled-packs", String.join(",", DataConfiguration.SAFE_MODE.dataPacks().getEnabled())), this.getString("initial-disabled-packs", String.join(",", DataConfiguration.SAFE_MODE.dataPacks().getDisabled())));
   }

   public static ServerPropertiesHandler load(Path path) {
      return new ServerPropertiesHandler(loadProperties(path));
   }

   protected ServerPropertiesHandler create(DynamicRegistryManager dynamicRegistryManager, Properties properties) {
      return new ServerPropertiesHandler(properties);
   }

   @Nullable
   private static Text parseResourcePackPrompt(String prompt) {
      if (!Strings.isNullOrEmpty(prompt)) {
         try {
            JsonElement jsonElement = StrictJsonParser.parse(prompt);
            return (Text)TextCodecs.CODEC.parse(DynamicRegistryManager.EMPTY.getOps(JsonOps.INSTANCE), jsonElement).resultOrPartial((string2) -> {
               LOGGER.warn("Failed to parse resource pack prompt '{}': {}", prompt, string2);
            }).orElse((Object)null);
         } catch (Exception var2) {
            LOGGER.warn("Failed to parse resource pack prompt '{}'", prompt, var2);
         }
      }

      return null;
   }

   private static Optional getServerResourcePackProperties(String id, String url, String sha1, @Nullable String hash, boolean required, String prompt) {
      if (url.isEmpty()) {
         return Optional.empty();
      } else {
         String string;
         if (!sha1.isEmpty()) {
            string = sha1;
            if (!Strings.isNullOrEmpty(hash)) {
               LOGGER.warn("resource-pack-hash is deprecated and found along side resource-pack-sha1. resource-pack-hash will be ignored.");
            }
         } else if (!Strings.isNullOrEmpty(hash)) {
            LOGGER.warn("resource-pack-hash is deprecated. Please use resource-pack-sha1 instead.");
            string = hash;
         } else {
            string = "";
         }

         if (string.isEmpty()) {
            LOGGER.warn("You specified a resource pack without providing a sha1 hash. Pack will be updated on the client only if you change the name of the pack.");
         } else if (!SHA1_PATTERN.matcher(string).matches()) {
            LOGGER.warn("Invalid sha1 for resource-pack-sha1");
         }

         Text text = parseResourcePackPrompt(prompt);
         UUID uUID;
         if (id.isEmpty()) {
            uUID = UUID.nameUUIDFromBytes(url.getBytes(StandardCharsets.UTF_8));
            LOGGER.warn("resource-pack-id missing, using default of {}", uUID);
         } else {
            try {
               uUID = UUID.fromString(id);
            } catch (IllegalArgumentException var10) {
               LOGGER.warn("Failed to parse '{}' into UUID", id);
               return Optional.empty();
            }
         }

         return Optional.of(new MinecraftServer.ServerResourcePackProperties(uUID, url, string, required, text));
      }
   }

   private static DataPackSettings parseDataPackSettings(String enabled, String disabled) {
      List list = COMMA_SPLITTER.splitToList(enabled);
      List list2 = COMMA_SPLITTER.splitToList(disabled);
      return new DataPackSettings(list, list2);
   }

   public DimensionOptionsRegistryHolder createDimensionsRegistryHolder(RegistryWrapper.WrapperLookup registries) {
      return this.worldGenProperties.createDimensionsRegistryHolder(registries);
   }

   // $FF: synthetic method
   protected AbstractPropertiesHandler create(final DynamicRegistryManager registryManager, final Properties properties) {
      return this.create(registryManager, properties);
   }

   static record WorldGenProperties(JsonObject generatorSettings, String levelType) {
      private static final Map LEVEL_TYPE_TO_PRESET_KEY;

      WorldGenProperties(JsonObject jsonObject, String string) {
         this.generatorSettings = jsonObject;
         this.levelType = string;
      }

      public DimensionOptionsRegistryHolder createDimensionsRegistryHolder(RegistryWrapper.WrapperLookup registries) {
         RegistryWrapper registryWrapper = registries.getOrThrow(RegistryKeys.WORLD_PRESET);
         RegistryEntry.Reference reference = (RegistryEntry.Reference)registryWrapper.getOptional(WorldPresets.DEFAULT).or(() -> {
            return registryWrapper.streamEntries().findAny();
         }).orElseThrow(() -> {
            return new IllegalStateException("Invalid datapack contents: can't find default preset");
         });
         Optional var10000 = Optional.ofNullable(Identifier.tryParse(this.levelType)).map((levelTypeId) -> {
            return RegistryKey.of(RegistryKeys.WORLD_PRESET, levelTypeId);
         }).or(() -> {
            return Optional.ofNullable((RegistryKey)LEVEL_TYPE_TO_PRESET_KEY.get(this.levelType));
         });
         Objects.requireNonNull(registryWrapper);
         RegistryEntry registryEntry = (RegistryEntry)var10000.flatMap(registryWrapper::getOptional).orElseGet(() -> {
            ServerPropertiesHandler.LOGGER.warn("Failed to parse level-type {}, defaulting to {}", this.levelType, reference.registryKey().getValue());
            return reference;
         });
         DimensionOptionsRegistryHolder dimensionOptionsRegistryHolder = ((WorldPreset)registryEntry.value()).createDimensionsRegistryHolder();
         if (registryEntry.matchesKey(WorldPresets.FLAT)) {
            RegistryOps registryOps = registries.getOps(JsonOps.INSTANCE);
            DataResult var8 = FlatChunkGeneratorConfig.CODEC.parse(new Dynamic(registryOps, this.generatorSettings()));
            Logger var10001 = ServerPropertiesHandler.LOGGER;
            Objects.requireNonNull(var10001);
            Optional optional = var8.resultOrPartial(var10001::error);
            if (optional.isPresent()) {
               return dimensionOptionsRegistryHolder.with(registries, new FlatChunkGenerator((FlatChunkGeneratorConfig)optional.get()));
            }
         }

         return dimensionOptionsRegistryHolder;
      }

      public JsonObject generatorSettings() {
         return this.generatorSettings;
      }

      public String levelType() {
         return this.levelType;
      }

      static {
         LEVEL_TYPE_TO_PRESET_KEY = Map.of("default", WorldPresets.DEFAULT, "largebiomes", WorldPresets.LARGE_BIOMES);
      }
   }
}
