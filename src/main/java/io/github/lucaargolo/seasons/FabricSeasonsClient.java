package io.github.lucaargolo.seasons;

import com.google.gson.JsonParseException;
import io.github.lucaargolo.seasons.payload.ConfigSyncPacket;
import io.github.lucaargolo.seasons.payload.UpdateCropsPaycket;
import io.github.lucaargolo.seasons.resources.CropConfigs;
import io.github.lucaargolo.seasons.resources.FoliageSeasonColors;
import io.github.lucaargolo.seasons.resources.GrassSeasonColors;
import io.github.lucaargolo.seasons.utils.ColorsCache;
import io.github.lucaargolo.seasons.utils.CompatWarnState;
import io.github.lucaargolo.seasons.utils.ModConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

@Environment(EnvType.CLIENT)
public class FabricSeasonsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new GrassSeasonColors());
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new FoliageSeasonColors());

        // Clear the biome color cache every tick so chunk sections rebuilt this tick
        // use the current interpolated season color rather than a stale discrete value.
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.level != null) {
                ColorsCache.clearCache();
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPacket.ID, (payload, context) -> {
            try {
                ModConfig received = FabricSeasons.GSON.fromJson(payload.config(), ModConfig.class);
                if (received != null && received.isValidStartingSeason()) {
                    FabricSeasons.CONFIG = received;
                }
            } catch (JsonParseException e) {
                FabricSeasons.LOGGER.error("[{}] Failed to parse config sync packet.", FabricSeasons.MOD_NAME, e);
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(UpdateCropsPaycket.ID, (payload, context) ->
                CropConfigs.receiveConfig(payload.cropConfig(), payload.cropConfigMap()));

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (FabricSeasons.CONFIG.shouldNotifyCompat()) {
                Thread thread = new Thread(() -> CompatWarnState.join(client));
                thread.setDaemon(true);
                thread.setName("seasons-compat-check");
                thread.start();
            }
        });
    }
}
