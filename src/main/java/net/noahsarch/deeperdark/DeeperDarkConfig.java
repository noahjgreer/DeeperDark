package net.noahsarch.deeperdark;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class DeeperDarkConfig {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger("DeeperDarkConfig");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE_JSON = FabricLoader.getInstance().getConfigDir().resolve("deeperdark.json").toFile();
    private static final File CONFIG_FILE_YAML = FabricLoader.getInstance().getConfigDir().resolve("deeperdark.yaml").toFile();

    private static ConfigInstance instance;

    public static class AutoUpdaterConfig {
        public String repoURL = "https://github.com/noahjgreer/DeeperDark/releases/latest";
        public boolean canDenyUpdate = false;
        public boolean canRestartLater = false;
    }

    public static class ItemMagnetVariantConfig {
        public double radius;
        public double passiveStrength;

        public ItemMagnetVariantConfig() {}

        public ItemMagnetVariantConfig(double radius, double passiveStrength) {
            this.radius = radius;
            this.passiveStrength = passiveStrength;
        }
    }

    public static class ItemMagnetConfig {
        public ItemMagnetVariantConfig copper   = new ItemMagnetVariantConfig(10,  0.25);
        public ItemMagnetVariantConfig iron     = new ItemMagnetVariantConfig(25,  0.5);
        public ItemMagnetVariantConfig gold     = new ItemMagnetVariantConfig(35,  0.625);
        public ItemMagnetVariantConfig diamond  = new ItemMagnetVariantConfig(50,  0.75);
        public ItemMagnetVariantConfig netherite = new ItemMagnetVariantConfig(85, 1.0);
    }

    public static class PlayerSoundProfile {
        public String sendMessageSound = "";
        public String deathMessageSound = "";
        public String joinMessageSound = "";
        public String hurtSound = "";
        public double pitch = 1.0;
        public double pitchDeviance = 0.0;

        public PlayerSoundProfile() {
        }
    }

    @SuppressWarnings("unchecked")
    private static void convertPlayerSoundProfiles(ConfigInstance config) {
        if (config.playerSounds == null) return;
        // SnakeYAML deserializes Map<String, PlayerSoundProfile> values as LinkedHashMap when
        // no type tag is present in the YAML (e.g. user-edited files). Convert them explicitly.
        Map<String, Object> raw = (Map<String, Object>) (Map<?, ?>) config.playerSounds;
        Map<String, PlayerSoundProfile> converted = new HashMap<>();
        for (Map.Entry<String, Object> entry : raw.entrySet()) {
            Object val = entry.getValue();
            if (val instanceof PlayerSoundProfile csp) {
                converted.put(entry.getKey(), csp);
            } else if (val instanceof Map<?, ?> m) {
                PlayerSoundProfile profile = new PlayerSoundProfile();
                Object send = m.get("sendMessageSound");
                Object death = m.get("deathMessageSound");
                Object join = m.get("joinMessageSound");
                Object hurt = m.get("hurtSound");
                Object pitch = m.get("pitch");
                Object deviance = m.get("pitchDeviance");
                if (send instanceof String s) profile.sendMessageSound = s;
                if (death instanceof String s) profile.deathMessageSound = s;
                if (join instanceof String s) profile.joinMessageSound = s;
                if (hurt instanceof String s) profile.hurtSound = s;
                if (pitch instanceof Number n) profile.pitch = n.doubleValue();
                if (deviance instanceof Number n) profile.pitchDeviance = n.doubleValue();
                converted.put(entry.getKey(), profile);
            }
        }
        config.playerSounds = converted;
    }

    private static boolean normalizeLoadedConfig(ConfigInstance config) {
        boolean changed = false;

        // Fix deserialization: SnakeYAML may load PlayerSoundProfile values as plain maps
        convertPlayerSoundProfiles(config);

        if (config.playerSounds == null) {
            config.playerSounds = new HashMap<>();
            changed = true;
        }

        if (config.playerSoundVolumes == null) {
            config.playerSoundVolumes = new HashMap<>();
            changed = true;
        }

        if (config.playerSoundExclusions == null) {
            config.playerSoundExclusions = new ArrayList<>();
            changed = true;
        } else {
            List<String> normalized = new ArrayList<>();
            for (String excluded : config.playerSoundExclusions) {
                if (excluded == null || excluded.isBlank()) {
                    changed = true;
                    continue;
                }

                String lowered = excluded.toLowerCase(Locale.ROOT);
                if (!normalized.contains(lowered)) {
                    normalized.add(lowered);
                } else {
                    changed = true;
                }

                if (!lowered.equals(excluded)) {
                    changed = true;
                }
            }
            config.playerSoundExclusions = normalized;
        }

        if (config.leavesDecayMaxTicks < config.leavesDecayMinTicks) {
            config.leavesDecayMaxTicks = config.leavesDecayMinTicks;
            changed = true;
        }


        if (config.unloadedActivityMaxOccurrencesPerBlock < 0) {
            config.unloadedActivityMaxOccurrencesPerBlock = 0;
            changed = true;
        }

        if (config.unloadedActivityMaxChunkUpdates < 0) {
            config.unloadedActivityMaxChunkUpdates = 0;
            changed = true;
        }

        if (config.unloadedActivityMaxKnownChunkUpdates < 0) {
            config.unloadedActivityMaxKnownChunkUpdates = 0;
            changed = true;
        }

        if (config.autoUpdater == null) {
            config.autoUpdater = new AutoUpdaterConfig();
            changed = true;
        }

        if (config.itemMagnet == null) {
            config.itemMagnet = new ItemMagnetConfig();
            changed = true;
        }

        return changed;
    }

    public static class ConfigInstance {
        public int originX = 0;
        public int originZ = 0;
        public double safeRadius = 1000.0;
        public double forceMultiplier = 0.5;
        public boolean allowEntitySpawning = false;
        public boolean pushSurvivalModeOnly = true; // If true, only survival mode players get pushed by border
        // creeper effect duration bounds in seconds
        public int creeperEffectMinSeconds = 15;
        public int creeperEffectMaxSeconds = 60;

        // Nether coordinate multiplier (1.0 = 1:1, 8.0 = vanilla 1:8)
        public double netherCoordinateMultiplier = 1.0;

        // Fortune enchantment configuration
        public boolean customFortuneEnabled = true;
        public double fortune1DropChance = 0.1666; // 16.66% chance to drop 2 items
        public double fortune2DropChance = 0.3333; // 33.33% chance to drop 2 items
        public double fortune3DropChance = 0.50; // 50% chance to drop 2 items
        public int fortuneMaxDrops = 2; // Maximum drops with custom Fortune (never more than 2)

        // Explosion item knockback configuration
        public boolean explosionItemKnockbackEnabled = false; // If true, items get extra velocity from explosions
        public double explosionItemKnockbackMultiplier = 3.0; // Multiplier for explosion knockback on items (1.0 = normal, 3.0 = 3x farther)

        // Piston push limit (vanilla default is 12)
        public int pistonPushLimit = 12;

        // Enderman configuration
        public boolean endermanPickUpAllBlocks = false; // If true, endermen can pick up any non-bedrock block

        // Baby mob spawning configuration
        public boolean babySkeletonsEnabled = true; // If true, baby skeletons can spawn
        public boolean babyCreepersEnabled = true; // If true, baby creepers can spawn
        public boolean babySpidersEnabled = true; // If true, baby spiders can spawn

        // Fishing configuration
        public int fishingChargedCreeperChance = 5000; // 1 in X chance to fish up a charged creeper

        // Cobblestone/stone brick mossing configuration
        public boolean mossGrowthEnabled = true; // If true, cobblestone and stone bricks slowly moss over time
        public int mossTickCheckFrequency = 3; // How many random ticks to skip between checks (higher = slower)
        public double mossBaseChance = 0.001; // Base chance per valid tick to moss (very low for 5-10 day timing)
        public double mossNearbyBonus = 0.05; // Additional chance per nearby mossy block
        public double mossUnderwaterMultiplier = 3.0; // Multiplier for mossing speed when underwater
        public double stoneBrickMossMultiplier = 0.5; // Multiplier for stone brick mossing speed (slower than cobble)

        // Zombie behavior configuration
        public double zombieFollowRange = 35.0; // How far zombies can detect and track players (vanilla default: 35.0)

        // Player sounds configuration
        public Map<String, PlayerSoundProfile> playerSounds = new HashMap<>();
        public List<String> playerSoundExclusions = new ArrayList<>();
        public Map<String, Double> playerSoundVolumes = new HashMap<>();

        // Leaves Be Gone (server-only port) configuration
        public boolean leavesBeGoneEnabled = true;
        public int leavesDecayMinTicks = 5;
        public int leavesDecayMaxTicks = 20;
        public boolean leavesBeGoneIgnoreOtherLeafTypes = false;

        // Unloaded Activity (server-only port) configuration
        public boolean unloadedActivityEnabled = true;
        public int unloadedActivityTickDifferenceThreshold = 1;
        public int unloadedActivityMaxChunkUpdates = 10;
        public int unloadedActivityMaxKnownChunkUpdates = 100;
        public boolean unloadedActivityRememberBlockPositions = true;
        public boolean unloadedActivityRandomizeBlockUpdates = false;
        public boolean unloadedActivityUpdateAllChunksWhenSleep = true;
        public int unloadedActivityMaxOccurrencesPerBlock = 100;
        public int unloadedActivityMaxNegativeBinomialAttempts = 10;

        // Creature configuration
        public net.noahsarch.deeperdark.creature.CreatureConfig creature = new net.noahsarch.deeperdark.creature.CreatureConfig();

        // Auto-updater configuration
        public AutoUpdaterConfig autoUpdater = new AutoUpdaterConfig();

        // Item magnet configuration
        public ItemMagnetConfig itemMagnet = new ItemMagnetConfig();
    }

    public static void load() {
        // prefer YAML, fallback to JSON for backwards compatibility
        if (CONFIG_FILE_YAML.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE_YAML)) {
                DumperOptions options = new DumperOptions();
                options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

                // Configure LoaderOptions to allow global tags (for backwards compatibility with old configs)
                org.yaml.snakeyaml.LoaderOptions loaderOptions = new org.yaml.snakeyaml.LoaderOptions();
                loaderOptions.setTagInspector(tag -> true); // Allow all tags

                org.yaml.snakeyaml.constructor.Constructor constructor = new org.yaml.snakeyaml.constructor.Constructor(ConfigInstance.class, loaderOptions);
                Yaml yaml = new Yaml(constructor);
                instance = yaml.load(reader);
            } catch (Exception e) {
                LOGGER.error("Failed to load config, creating new one", e);
                // If loading fails, delete corrupted file and create new one
                try {
                    java.nio.file.Files.deleteIfExists(CONFIG_FILE_YAML.toPath());
                } catch (IOException ex) {
                    LOGGER.warn("Failed to delete corrupted config", ex);
                }
                instance = null; // Will trigger creation of new config below
            }
        } else if (CONFIG_FILE_JSON.exists()) {
            try {
                // read JSON and strip comments
                List<String> lines = Files.readAllLines(CONFIG_FILE_JSON.toPath());
                StringBuilder sb = new StringBuilder();
                for (String line : lines) {
                    String trimmed = line.trim();
                    if (trimmed.startsWith("#") || trimmed.startsWith("//") || trimmed.isEmpty()) continue;
                    sb.append(line).append(System.lineSeparator());
                }
                instance = GSON.fromJson(sb.toString(), ConfigInstance.class);
            } catch (IOException e) {
                LOGGER.error("Failed to load generic JSON config", e);
            }
        }

        if (instance == null) {
            instance = new ConfigInstance();
            save();
        } else {
            // Backfill newly added config sections without overwriting existing user values
            if (normalizeLoadedConfig(instance)) {
                save();
            }
        }
    }

    public static void save() {
        try {
            if (!CONFIG_FILE_YAML.getParentFile().exists()) {
                if (!CONFIG_FILE_YAML.getParentFile().mkdirs()) {
                    LOGGER.warn("Failed to create config directory");
                }
            }

            // write a helpful commented header so server operators can understand the options
            String header = """
                    # DeeperDark configuration
                    # originX/originZ: center of the safe area (integers)
                    # safeRadius: radius (blocks) considered safe
                    # forceMultiplier: how strongly the border pushes players (multiplier)
                    # allowEntitySpawning: if false, mobs cannot spawn outside border and are pushed back
                    # pushSurvivalModeOnly: if true, only survival mode players get pushed by border (creative players ignored)
                    # netherCoordinateMultiplier: coordinate scale between overworld and nether (1.0 = 1:1, 8.0 = vanilla 1:8)
                    # creeperEffectMinSeconds / creeperEffectMaxSeconds (seconds): when a creeper with an infinite-duration effect explodes,
                    #   the effect given by the explosion will have a random duration between these values (seconds).
                    #
                    # Fortune Enchantment Configuration:
                    # customFortuneEnabled: if true, uses custom Fortune behavior; if false, uses vanilla
                    # fortune1DropChance: probability (0.0-1.0) that Fortune I drops 2 items instead of 1 (default: 0.1666 = 16.66%)
                    # fortune2DropChance: probability (0.0-1.0) that Fortune II drops 2 items instead of 1 (default: 0.3333 = 33.33%)
                    # fortune3DropChance: probability (0.0-1.0) that Fortune III drops 2 items instead of 1 (default: 0.50 = 50%)
                    # fortuneMaxDrops: maximum number of items Fortune can drop (vanilla Fortune III can drop 4+ items, this limits it)
                    # NOTE: This affects ALL fortune-based drops (ores, crops, gravel, glowstone, etc.)
                    #
                    # Explosion Item Knockback Configuration:
                    # explosionItemKnockbackEnabled: if true, items are amplified by explosions and fly farther (default: false)
                    # explosionItemKnockbackMultiplier: how much to multiply explosion knockback on items (1.0 = normal, 3.0 = 3x farther, 5.0 = extreme)
                    # NOTE: Items are never destroyed by explosions, but this makes them scatter more dramatically when enabled
                    #
                    # Piston Configuration:
                    # pistonPushLimit: maximum number of blocks a piston can push (vanilla default: 12)
                    #
                    # Enderman Configuration:
                    # endermanPickUpAllBlocks: if true, endermen can pick up any block except bedrock (default: false)
                    #
                    # Baby Mob Configuration:
                    # babySkeletonsEnabled: if true, skeletons have a 5% chance to spawn as babies (default: true)
                    # babyCreepersEnabled: if true, creepers have a 5% chance to spawn as babies (default: true)
                    # NOTE: Baby creepers have 1/5 explosion radius, pitched-up sounds, and poof particles instead of explosion
                    #
                    # Fishing Configuration:
                    # fishingChargedCreeperChance: 1 in X chance to fish up a charged creeper instead of loot (default: 5000)
                    #
                    # Moss Growth Configuration:
                    # mossGrowthEnabled: if true, cobblestone and stone bricks slowly moss over time (default: true)
                    # mossTickCheckFrequency: how many random ticks to skip between moss checks (higher = slower, default: 3)
                    # mossBaseChance: base probability per valid tick to convert to mossy (default: 0.001 for ~5-10 in-game days)
                    # mossNearbyBonus: additional chance per adjacent mossy block (default: 0.05)
                    # mossUnderwaterMultiplier: multiplier for mossing speed when underwater (default: 3.0)
                    # stoneBrickMossMultiplier: multiplier for stone brick mossing (default: 0.5, twice as slow as cobble)
                    #
                    # Zombie Behavior Configuration:
                    # zombieFollowRange: how far zombies can detect and track players (vanilla default: 35.0)
                    #
                    # Player Sounds Configuration:
                    # playerSounds: per-player sound settings used for message/death/join/hurt events.
                    #   Entry format:
                    #     PlayerName:
                    #       sendMessageSound: entity.cat.ambient
                    #       deathMessageSound: entity.cat.death
                    #       joinMessageSound: entity.cat.stray_ambient
                    #       hurtSound: entity.cat.hurt  (played nearby only, replaces vanilla hurt sound)
                    #       pitch: 1.0
                    #       pitchDeviance: 0.2
                    # playerSoundExclusions: lowercase player names who opted out via /ddclient player_sounds false.
                    # playerSoundVolumes: per-player volume (0.0-1.0) for hearing player sounds. Default is 0.8.
                    #   Set via /ddclient player_sounds volume <0.0-1.0>. Example:
                    #     FinniTheFox: 0.5
                    #
                    # Leaves Be Gone (server-only port):
                    # leavesBeGoneEnabled: if true, detached leaves are scheduled for fast random-tick decay.
                    # leavesDecayMinTicks/leavesDecayMaxTicks: random delay range (ticks) before a detached leaf decays.
                    # leavesBeGoneIgnoreOtherLeafTypes: if true, leaves of different species do not support each other.
                    #
                    # Unloaded Activity (server-only port):
                    # unloadedActivityEnabled: if true, chunks simulate catch-up random ticks when ticked after being unloaded.
                    # unloadedActivityTickDifferenceThreshold: minimum tick gap before a chunk gets catch-up simulation (default: 1).
                    # unloadedActivityMaxChunkUpdates: max unknown chunks updated per server tick (default: 10).
                    # unloadedActivityMaxKnownChunkUpdates: max known (cached block list) chunks updated per server tick (default: 100).
                    # unloadedActivityRememberBlockPositions: cache randomly-ticking block positions per chunk (faster re-simulation).
                    # unloadedActivityRandomizeBlockUpdates: shuffle block order each simulation (default: false).
                    # unloadedActivityUpdateAllChunksWhenSleep: bypass per-tick limits when players sleep (default: true).
                    # unloadedActivityMaxOccurrencesPerBlock: max random ticks simulated per block per catch-up (default: 100).
                    # unloadedActivityMaxNegativeBinomialAttempts: attempts for negative-binomial duration sampling (default: 10).
                    #
                    # Auto-Updater Configuration (client-side only):
                    # autoUpdater.repoURL: GitHub releases page URL used to check for and download updates.
                    # autoUpdater.canDenyUpdate: if true, players can click "Update Later" to skip; if false, that button is disabled.
                    # autoUpdater.canRestartLater: if true, players can delay restarting after an update installs; if false, "Restart Later" is disabled.
                    #
                    """;

            DumperOptions options = new DumperOptions();
            options.setIndent(2);
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);

            // Use a representer to avoid global tag issues with nested classes
            org.yaml.snakeyaml.representer.Representer representer = new org.yaml.snakeyaml.representer.Representer(options);
            representer.addClassTag(ConfigInstance.class, org.yaml.snakeyaml.nodes.Tag.MAP);
            representer.addClassTag(PlayerSoundProfile.class, org.yaml.snakeyaml.nodes.Tag.MAP);
            representer.addClassTag(AutoUpdaterConfig.class, org.yaml.snakeyaml.nodes.Tag.MAP);
            representer.addClassTag(ItemMagnetConfig.class, org.yaml.snakeyaml.nodes.Tag.MAP);
            representer.addClassTag(ItemMagnetVariantConfig.class, org.yaml.snakeyaml.nodes.Tag.MAP);

            Yaml yaml = new Yaml(representer, options);

            try (FileWriter writer = new FileWriter(CONFIG_FILE_YAML)) {
                writer.write(header);
                yaml.dump(instance, writer);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to save config", e);
        }
    }

    public static ConfigInstance get() {
        if (instance == null) {
            load();
        }
        return instance;
    }
}
