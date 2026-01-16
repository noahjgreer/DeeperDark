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
import java.util.List;

public class DeeperDarkConfig {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger("DeeperDarkConfig");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE_JSON = FabricLoader.getInstance().getConfigDir().resolve("deeperdark.json").toFile();
    private static final File CONFIG_FILE_YAML = FabricLoader.getInstance().getConfigDir().resolve("deeperdark.yaml").toFile();

    private static ConfigInstance instance;

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

        // Anvil configuration
        public int anvilRepairCost = 0; // Cost in levels for item repairs (diamond + diamond pickaxe)
        public int anvilEnchantCost = 5; // Flat cost in levels for any enchanting combination

        // Fortune enchantment configuration
        public boolean customFortuneEnabled = true;
        public double fortune1DropChance = 0.1666; // 16.66% chance to drop 2 items
        public double fortune2DropChance = 0.3333; // 33.33% chance to drop 2 items
        public double fortune3DropChance = 0.50; // 50% chance to drop 2 items
        public int fortuneMaxDrops = 2; // Maximum drops with custom Fortune (never more than 2)

        // Explosion item knockback configuration
        public boolean explosionItemKnockbackEnabled = false; // If true, items get extra velocity from explosions
        public double explosionItemKnockbackMultiplier = 3.0; // Multiplier for explosion knockback on items (1.0 = normal, 3.0 = 3x farther)

        // Beacon configuration
        public int beaconIronTime = 180;
        public int beaconGoldTime = 300;
        public int beaconEmeraldTime = 600;
        public int beaconDiamondTime = 1800;
        public int beaconNetheriteTime = 21600;

        // Beacon Association Radius by pyramid level
        public int beaconLevel1Radius = 10;
        public int beaconLevel2Radius = 20;
        public int beaconLevel3Radius = 30;
        public int beaconLevel4Radius = 40;

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
                    # Anvil Configuration:
                    # anvilRepairCost: XP level cost for repairing items with materials (e.g., diamond + diamond pickaxe). Set to 0 for free repairs.
                    # anvilEnchantCost: Flat XP level cost for any enchanting combination (book + tool, book + book, etc.)
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
                    # Beacon Configuration:
                    # beaconIronTime: how long (seconds) the Beacon effect lasts when using Iron Ingots (default: 180 seconds)
                    # beaconGoldTime: how long (seconds) the Beacon effect lasts when using Gold Ingots (default: 300 seconds)
                    # beaconEmeraldTime: how long (seconds) the Beacon effect lasts when using Emeralds (default: 600 seconds)
                    # beaconDiamondTime: how long (seconds) the Beacon effect lasts when using Diamonds (default: 1800 seconds)
                    # beaconNetheriteTime: how long (seconds) the Beacon effect lasts when using Netherite Ingots (default: 21600 seconds)
                    # beaconLevel1Radius: radius (blocks) of the Beacon's effect at pyramid level 1 (default: 10 blocks)
                    # beaconLevel2Radius: radius (blocks) of the Beacon's effect at pyramid level 2 (default: 20 blocks)
                    # beaconLevel3Radius: radius (blocks) of the Beacon's effect at pyramid level 3 (default: 30 blocks)
                    # beaconLevel4Radius: radius (blocks) of the Beacon's effect at pyramid level 4 (default: 40 blocks)
                    #
                    """;

            DumperOptions options = new DumperOptions();
            options.setIndent(2);
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);

            // Use a representer to avoid global tag issues with nested classes
            org.yaml.snakeyaml.representer.Representer representer = new org.yaml.snakeyaml.representer.Representer(options);
            representer.addClassTag(ConfigInstance.class, org.yaml.snakeyaml.nodes.Tag.MAP);

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
