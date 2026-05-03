package io.github.lucaargolo.seasons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.lucaargolo.seasons.commands.SeasonCommand;
import io.github.lucaargolo.seasons.mixed.BiomeMixed;
import io.github.lucaargolo.seasons.payload.ConfigSyncPacket;
import io.github.lucaargolo.seasons.payload.SeasonTimeSyncPacket;
import io.github.lucaargolo.seasons.payload.UpdateCropsPaycket;
import io.github.lucaargolo.seasons.resources.CropConfigs;
import io.github.lucaargolo.seasons.utils.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.clock.WorldClocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.server.level.ServerLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;

public class FabricSeasons implements ModInitializer {

    public static final String MOD_ID = "seasons";
    public static final String MOD_NAME = "Fabric Seasons";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static ModConfig CONFIG = new ModConfig();

    /** Maps seed/food items to their corresponding crop blocks for tooltip/multiplier lookup. */
    public static final HashMap<Item, Block> SEEDS_MAP = new HashMap<>();

    /** Position set by worldgen/precipitation mixins to mark the next block placed as NOT manually placed. */
    private static BlockPos nextMeltablePos = null;

    public static Identifier identifier(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    /**
     * Called just before {@code setBlock}/{@code setBlockAndUpdate} in worldgen/tick mixins
     * to signal that the imminent block placement is NOT player-initiated.
     * {@code AbstractBlockMixin.onPlace} reads and clears this to skip manual-placement marking.
     */
    public static void setMeltable(BlockPos pos) {
        nextMeltablePos = pos;
    }

    /** Returns {@code true} and clears the flag if {@code pos} matches the pending meltable position. */
    public static boolean isMeltable(BlockPos pos) {
        if (nextMeltablePos != null && nextMeltablePos.equals(pos)) {
            nextMeltablePos = null;
            return true;
        }
        return false;
    }

    public static PlacedMeltablesState getPlacedMeltablesState(ServerLevel world) {
        return world.getDataStorage().computeIfAbsent(PlacedMeltablesState.TYPE);
    }

    public static ReplacedMeltablesState getReplacedMeltablesState(ServerLevel world) {
        return world.getDataStorage().computeIfAbsent(ReplacedMeltablesState.TYPE);
    }

    // -------------------------------------------------------------------------
    // Season calculation
    // -------------------------------------------------------------------------

    private static Holder<WorldClock> overworldClockHolder = null;

    // Client-side: time anchor received from the server via SeasonTimeSyncPacket.
    // We advance it by the delta in vanilla gameTime so no per-tick packet is needed.
    private static long clientOverworldTimeBase = 0;
    private static long clientGameTimeBase = 0;
    private static boolean clientTimeSynced = false;

    public static void setClientOverworldTime(long overworldTime, long gameTime) {
        clientOverworldTimeBase = overworldTime;
        clientGameTimeBase = gameTime;
        clientTimeSynced = true;
    }

    public static boolean isClientTimeSynced() {
        return clientTimeSynced;
    }

    public static void resetClientTime() {
        clientTimeSynced = false;
        clientOverworldTimeBase = 0;
        clientGameTimeBase = 0;
    }

    private static Holder<WorldClock> getOverworldClockHolder(Level world) {
        if (overworldClockHolder == null) {
            overworldClockHolder = world.registryAccess()
                .lookupOrThrow(Registries.WORLD_CLOCK)
                .getOrThrow(WorldClocks.OVERWORLD);
        }
        return overworldClockHolder;
    }

    public static long getOverworldTime(Level world) {
        if (world.isClientSide() && clientTimeSynced) {
            return clientOverworldTimeBase + (world.getGameTime() - clientGameTimeBase);
        }
        try {
            return world.clockManager().getTotalTicks(getOverworldClockHolder(world));
        } catch (IllegalStateException e) {
            // Clock not yet initialized on brand-new worlds; treat as time 0 (SPRING)
            return 0L;
        }
    }

    public static Season getCurrentSeason(Level world) {
        if (!CONFIG.isValidInDimension(world.dimension())) return Season.SPRING;
        if (CONFIG.isSeasonLocked()) return CONFIG.getLockedSeason();
        if (CONFIG.isSeasonTiedWithSystemTime()) return getSystemTimeSeason();

        long time = getOverworldTime(world) % CONFIG.getYearLength();
        Season season = CONFIG.getStartingSeason();
        long accumulated = 0;
        for (int i = 0; i < 4; i++) {
            accumulated += season.getSeasonLength();
            if (time < accumulated) return season;
            season = season.getNext();
        }
        return season;
    }

    public static Season getNextSeason(Level world, Season currentSeason) {
        return currentSeason.getNext();
    }

    public static float getSeasonTransitionFraction(Level world) {
        if (CONFIG.isSeasonLocked() || CONFIG.isSeasonTiedWithSystemTime()) return 0.0f;
        long time = getOverworldTime(world) % CONFIG.getYearLength();
        Season season = CONFIG.getStartingSeason();
        long accumulated = 0;
        for (int i = 0; i < 4; i++) {
            long len = season.getSeasonLength();
            accumulated += len;
            if (time < accumulated) {
                return (float)(time - (accumulated - len)) / (float)len;
            }
            season = season.getNext();
        }
        return 0.0f;
    }

    public static long getTimeToNextSeason(Level world) {
        if (CONFIG.isSeasonLocked() || CONFIG.isSeasonTiedWithSystemTime()) return 0;
        long time = getOverworldTime(world) % CONFIG.getYearLength();
        Season season = CONFIG.getStartingSeason();
        long accumulated = 0;
        for (int i = 0; i < 4; i++) {
            accumulated += season.getSeasonLength();
            if (time < accumulated) return accumulated - time;
            season = season.getNext();
        }
        return 0;
    }

    private static Season getSystemTimeSeason() {
        int month = LocalDate.now().getMonthValue();
        if (CONFIG.isInNorthHemisphere()) {
            if (month >= 3 && month <= 5) return CONFIG.isFallAndSpringReversed() ? Season.FALL : Season.SPRING;
            if (month >= 6 && month <= 8) return Season.SUMMER;
            if (month >= 9 && month <= 11) return CONFIG.isFallAndSpringReversed() ? Season.SPRING : Season.FALL;
            return Season.WINTER;
        } else {
            if (month >= 3 && month <= 5) return CONFIG.isFallAndSpringReversed() ? Season.SPRING : Season.FALL;
            if (month >= 6 && month <= 8) return Season.WINTER;
            if (month >= 9 && month <= 11) return CONFIG.isFallAndSpringReversed() ? Season.FALL : Season.SPRING;
            return Season.SUMMER;
        }
    }

    // -------------------------------------------------------------------------
    // Biome temperature injection (called from WorldViewMixin)
    // -------------------------------------------------------------------------

    public static void injectBiomeTemperature(Holder<Biome> biomeHolder, Level world) {
        Biome biome = biomeHolder.value();
        if (!((Object)biome instanceof BiomeMixed biomeMixed)) return;

        if (!CONFIG.isValidInDimension(world.dimension())) {
            biomeMixed.seasons_clearSeasonWeather();
            return;
        }

        Identifier biomeId = biomeHolder.unwrapKey()
                .map(ResourceKey::identifier)
                .orElse(null);

        if (biomeId == null || !CONFIG.doTemperatureChanges(biomeId)) {
            biomeMixed.seasons_clearSeasonWeather();
            return;
        }

        if (!biomeMixed.seasons_hasOriginalStored()) {
            biomeMixed.seasons_storeOriginal(
                    biome.getBaseTemperature(),
                    biomeMixed.seasons_getBaseDownfall(),
                    biome.hasPrecipitation());
        }

        float originalTemp  = biomeMixed.seasons_getOriginalTemperature();
        boolean originalPrecip = biomeMixed.seasons_getOriginalHasPrecipitation();
        Season season = getCurrentSeason(world);

        float seasonTemp;
        boolean seasonPrecip;

        switch (season) {
            case SUMMER -> {
                seasonTemp  = originalTemp + 0.4f;
                seasonPrecip = originalPrecip;
            }
            case FALL -> {
                seasonTemp  = originalTemp - 0.1f;
                seasonPrecip = originalPrecip;
            }
            case WINTER -> {
                seasonTemp  = originalTemp - 0.7f;
                seasonPrecip = originalPrecip;
                if (CONFIG.isSnowForcedInBiome(biomeId)) {
                    seasonTemp  = Math.min(seasonTemp, 0.0f);
                    seasonPrecip = true;
                }
            }
            default -> { // SPRING
                seasonTemp  = originalTemp;
                seasonPrecip = originalPrecip;
            }
        }

        biomeMixed.seasons_applySeasonWeather(seasonTemp, seasonPrecip);
    }

    // -------------------------------------------------------------------------
    // ModInitializer
    // -------------------------------------------------------------------------

    @Override
    public void onInitialize() {
        loadConfig();
        registerSeeds();

        PayloadTypeRegistry.clientboundPlay().register(ConfigSyncPacket.ID,       ConfigSyncPacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(UpdateCropsPaycket.ID,     UpdateCropsPaycket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SeasonTimeSyncPacket.ID,   SeasonTimeSyncPacket.CODEC);

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new CropConfigs());

        ServerTickEvents.START_SERVER_TICK.register(GreenhouseCache::tick);

        ServerTickEvents.START_SERVER_TICK.register(server -> {
            if (server.getTickCount() % 100 == 0) {
                ServerLevel overworld = server.getLevel(Level.OVERWORLD);
                if (overworld != null) {
                    SeasonTimeSyncPacket packet = new SeasonTimeSyncPacket(
                            getOverworldTime(overworld), overworld.getGameTime());
                    server.getPlayerList().getPlayers().forEach(p -> ServerPlayNetworking.send(p, packet));
                }
            }
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registries, environment) ->
                SeasonCommand.register(dispatcher));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerLevel overworld = server.getLevel(Level.OVERWORLD);
            if (overworld != null) {
                try {
                    ServerPlayNetworking.send(handler.player, new SeasonTimeSyncPacket(
                            getOverworldTime(overworld), overworld.getGameTime()));
                } catch (IllegalStateException ignored) {
                    // Integrated server clock not yet initialized; the periodic 100-tick sender will sync shortly
                }
            }
            ServerPlayNetworking.send(handler.player, new ConfigSyncPacket(GSON.toJson(CONFIG)));
            ServerPlayNetworking.send(handler.player,
                    UpdateCropsPaycket.fromConfig(CropConfigs.getDefaultCropConfig(), CropConfigs.getCropConfigMap()));
        });
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static void loadConfig() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("seasons.json");
        try {
            if (Files.exists(configPath)) {
                String json = Files.readString(configPath);
                ModConfig loaded = GSON.fromJson(json, ModConfig.class);
                if (loaded != null && loaded.isValidStartingSeason()) {
                    CONFIG = loaded;
                } else {
                    LOGGER.warn("[{}] Invalid config, using defaults.", MOD_NAME);
                }
            } else {
                Files.writeString(configPath, GSON.toJson(CONFIG));
            }
        } catch (Exception e) {
            LOGGER.error("[{}] Failed to load config, using defaults.", MOD_NAME, e);
        }
    }

    private static void registerSeeds() {
        SEEDS_MAP.put(Items.WHEAT_SEEDS,   Blocks.WHEAT);
        SEEDS_MAP.put(Items.CARROT,        Blocks.CARROTS);
        SEEDS_MAP.put(Items.POTATO,        Blocks.POTATOES);
        SEEDS_MAP.put(Items.BEETROOT_SEEDS,Blocks.BEETROOTS);
        SEEDS_MAP.put(Items.PUMPKIN_SEEDS, Blocks.PUMPKIN_STEM);
        SEEDS_MAP.put(Items.MELON_SEEDS,   Blocks.MELON_STEM);
        SEEDS_MAP.put(Items.NETHER_WART,   Blocks.NETHER_WART);
        SEEDS_MAP.put(Items.SWEET_BERRIES, Blocks.SWEET_BERRY_BUSH);
    }
}
