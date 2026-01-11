package net.noahsarch.deeperdark;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DeeperDarkConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("deeperdark.json").toFile();

    private static ConfigInstance instance;

    public static class ConfigInstance {
        public int originX = 0;
        public int originZ = 0;
        public double safeRadius = 1000.0;
        public double forceMultiplier = 0.5;
    }

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                instance = GSON.fromJson(reader, ConfigInstance.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (instance == null) {
            instance = new ConfigInstance();
            save();
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(instance, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ConfigInstance get() {
        if (instance == null) {
            load();
        }
        return instance;
    }
}

