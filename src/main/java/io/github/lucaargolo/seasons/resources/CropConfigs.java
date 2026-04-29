package io.github.lucaargolo.seasons.resources;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.utils.CropConfig;
import io.github.lucaargolo.seasons.utils.Season;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static io.github.lucaargolo.seasons.FabricSeasons.MOD_NAME;

public class CropConfigs implements SimpleSynchronousResourceReloadListener {

    private static final CropConfig DEFAULT = new CropConfig(1.0f, 0.8f, 0.6f, 0f);
    private static CropConfig defaultCropConfig = DEFAULT;
    private static HashMap<Identifier, CropConfig> cropConfigMap = new HashMap<>();

    public static float getSeasonCropMultiplier(Identifier cropIdentifier, Season season) {
        return cropConfigMap.getOrDefault(cropIdentifier, defaultCropConfig).getModifier(season);
    }

    public static void receiveConfig(CropConfig defaultConfig, HashMap<Identifier, CropConfig> configMap) {
        defaultCropConfig = defaultConfig;
        cropConfigMap = configMap;
    }

    public static void clear() {
        defaultCropConfig = DEFAULT;
        cropConfigMap.clear();
    }

    public static CropConfig getDefaultCropConfig() {
        return defaultCropConfig;
    }

    public static void toBuf(RegistryFriendlyByteBuf buf) {
        buf.writeInt(cropConfigMap.size());
        cropConfigMap.forEach((id, config) -> {
            Identifier.STREAM_CODEC.encode(buf, id);
            config.toBuf(buf);
        });
    }

    public static HashMap<Identifier, CropConfig> fromBuf(RegistryFriendlyByteBuf buf) {
        HashMap<Identifier, CropConfig> map = new HashMap<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            map.put(Identifier.STREAM_CODEC.decode(buf), CropConfig.fromBuf(buf));
        }
        return map;
    }

    public static HashMap<Identifier, CropConfig> getCropConfigMap() {
        return cropConfigMap;
    }

    @Override
    public Identifier getFabricId() {
        return FabricSeasons.identifier("crop_configs");
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        try {
            Resource resource = manager.getResourceOrThrow(FabricSeasons.identifier("hardcoded/crop/default.json"));
            JsonElement input = JsonParser.parseReader(new InputStreamReader(resource.open(), StandardCharsets.UTF_8));
            defaultCropConfig = CropConfig.CODEC.parse(JsonOps.INSTANCE, input.getAsJsonObject()).getOrThrow();
        } catch (Exception e) {
            FabricSeasons.LOGGER.error("[" + MOD_NAME + "] Failed to load hardcoded crop default", e);
        }

        cropConfigMap.clear();
        manager.listResources("seasons/crop", id -> id.getPath().endsWith(".json")).forEach((id, resource) -> {
            String[] split = id.getPath().split("/");
            Identifier cropIdentifier = Identifier.fromNamespaceAndPath(id.getNamespace(), split[split.length - 1].replace(".json", ""));
            try {
                JsonElement input = JsonParser.parseReader(new InputStreamReader(resource.open(), StandardCharsets.UTF_8));
                CropConfig config = CropConfig.CODEC.parse(JsonOps.INSTANCE, input.getAsJsonObject()).getOrThrow();
                cropConfigMap.put(cropIdentifier, config);
            } catch (Exception e) {
                FabricSeasons.LOGGER.error("[" + MOD_NAME + "] Failed to load crop config for: " + cropIdentifier, e);
            }
        });

        if (!cropConfigMap.isEmpty()) {
            FabricSeasons.LOGGER.info("[" + MOD_NAME + "] Successfully loaded {} custom crop configs.", cropConfigMap.size());
        }
    }
}
