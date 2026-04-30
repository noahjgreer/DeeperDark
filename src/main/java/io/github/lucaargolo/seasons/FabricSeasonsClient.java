package io.github.lucaargolo.seasons;

import com.google.gson.JsonParseException;
import io.github.lucaargolo.seasons.payload.ConfigSyncPacket;
import io.github.lucaargolo.seasons.payload.SeasonTimeSyncPacket;
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
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
public class FabricSeasonsClient implements ClientModInitializer {

    private static boolean colorsDirtyNeeded = false;
    private static int colorsDirtyTick = 0;
    private static final int COLORS_DIRTY_INTERVAL = 60;

    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new GrassSeasonColors());
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new FoliageSeasonColors());

        // Clear biome color cache every tick so any section currently being compiled
        // picks up the latest interpolated season color.
        // Additionally, every COLORS_DIRTY_INTERVAL ticks (or immediately after a time sync),
        // mark all visible sections dirty so already-compiled sections also get updated.
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            var level = client.level;
            if (level == null) return;
            ColorsCache.clearCache();

            if (FabricSeasons.CONFIG.isValidInDimension(level.dimension())
                    && client.levelRenderer != null && client.player != null) {
                colorsDirtyTick++;
                if (colorsDirtyNeeded || colorsDirtyTick >= COLORS_DIRTY_INTERVAL) {
                    colorsDirtyNeeded = false;
                    colorsDirtyTick = 0;
                    // Clear BlockTintCache BEFORE marking sections dirty.
                    // BlockTintCache caches blended biome tint per block position and is
                    // checked before biome.getGrassColor() is ever called during section
                    // compilation — stale entries would cause sections to recompile with
                    // the old season color even after dirty. ColorsCache (seasons' own
                    // per-biome cache) is already cleared every tick above.
                    level.clearTintCaches();
                    int r = client.options.renderDistance().get();
                    int sx = Mth.floor(client.player.getX()) >> 4;
                    int sz = Mth.floor(client.player.getZ()) >> 4;
                    client.levelRenderer.setSectionRangeDirty(
                            sx - r, level.getMinSectionY(), sz - r,
                            sx + r, level.getMaxSectionY(), sz + r);
                }
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

        // On time sync: update client's season clock and schedule a section dirty pass
        // so all visible chunks update to the correct seasonal colors without a full allChanged() flash.
        ClientPlayNetworking.registerGlobalReceiver(SeasonTimeSyncPacket.ID, (payload, context) -> {
            FabricSeasons.setClientOverworldTime(payload.overworldTime(), payload.gameTime());
            colorsDirtyNeeded = true;
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            // Force a color refresh on the first tick after joining.
            colorsDirtyNeeded = true;
            colorsDirtyTick = 0;

            if (FabricSeasons.CONFIG.shouldNotifyCompat()) {
                Thread thread = new Thread(() -> CompatWarnState.join(client));
                thread.setDaemon(true);
                thread.setName("seasons-compat-check");
                thread.start();
            }
        });

        // Reset client time sync state on disconnect so a later reconnect doesn't
        // use stale overworld time from a previous session.
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            FabricSeasons.resetClientTime();
            colorsDirtyNeeded = false;
            colorsDirtyTick = 0;
        });
    }
}
