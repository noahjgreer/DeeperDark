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
        // creeper effect duration bounds in seconds
        public int creeperEffectMinSeconds = 15;
        public int creeperEffectMaxSeconds = 60;
    }

    public static void load() {
        // prefer YAML, fallback to JSON for backwards compatibility
        if (CONFIG_FILE_YAML.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE_YAML)) {
                Yaml yaml = new Yaml();
                instance = yaml.loadAs(reader, ConfigInstance.class);
            } catch (IOException e) {
                LOGGER.error("Failed to load config", e);
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
                    # creeperEffectMinSeconds / creeperEffectMaxSeconds (seconds): when a creeper with an infinite-duration effect explodes,
                    #   the effect given by the explosion will have a random duration between these values (seconds).
                    #
                    """;

            DumperOptions options = new DumperOptions();
            options.setIndent(2);
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);
            Yaml yaml = new Yaml(options);

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
