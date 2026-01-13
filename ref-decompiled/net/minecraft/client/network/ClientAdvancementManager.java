/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.advancement.AdvancementDisplay
 *  net.minecraft.advancement.AdvancementEntry
 *  net.minecraft.advancement.AdvancementManager
 *  net.minecraft.advancement.AdvancementManager$Listener
 *  net.minecraft.advancement.AdvancementProgress
 *  net.minecraft.advancement.PlacedAdvancement
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.network.ClientAdvancementManager
 *  net.minecraft.client.network.ClientPlayNetworkHandler
 *  net.minecraft.client.session.telemetry.WorldSession
 *  net.minecraft.client.toast.AdvancementToast
 *  net.minecraft.client.toast.Toast
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.AdvancementTabC2SPacket
 *  net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket
 *  net.minecraft.util.Identifier
 *  net.minecraft.world.World
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.network;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.session.telemetry.WorldSession;
import net.minecraft.client.toast.AdvancementToast;
import net.minecraft.client.toast.Toast;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.AdvancementTabC2SPacket;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ClientAdvancementManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final MinecraftClient client;
    private final WorldSession worldSession;
    private final AdvancementManager manager = new AdvancementManager();
    private final Map<AdvancementEntry, AdvancementProgress> advancementProgresses = new Object2ObjectOpenHashMap();
    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable ClientAdvancementManager.Listener listener;
    private @Nullable AdvancementEntry selectedTab;

    public ClientAdvancementManager(MinecraftClient client, WorldSession worldSession) {
        this.client = client;
        this.worldSession = worldSession;
    }

    public void onAdvancements(AdvancementUpdateS2CPacket packet) {
        if (packet.shouldClearCurrent()) {
            this.manager.clear();
            this.advancementProgresses.clear();
        }
        this.manager.removeAll(packet.getAdvancementIdsToRemove());
        this.manager.addAll((Collection)packet.getAdvancementsToEarn());
        for (Map.Entry entry : packet.getAdvancementsToProgress().entrySet()) {
            PlacedAdvancement placedAdvancement = this.manager.get((Identifier)entry.getKey());
            if (placedAdvancement != null) {
                AdvancementProgress advancementProgress = (AdvancementProgress)entry.getValue();
                advancementProgress.init(placedAdvancement.getAdvancement().requirements());
                this.advancementProgresses.put(placedAdvancement.getAdvancementEntry(), advancementProgress);
                if (this.listener != null) {
                    this.listener.setProgress(placedAdvancement, advancementProgress);
                }
                if (packet.shouldClearCurrent() || !advancementProgress.isDone()) continue;
                if (this.client.world != null) {
                    this.worldSession.onAdvancementMade((World)this.client.world, placedAdvancement.getAdvancementEntry());
                }
                Optional optional = placedAdvancement.getAdvancement().display();
                if (!packet.shouldShowToast() || !optional.isPresent() || !((AdvancementDisplay)optional.get()).shouldShowToast()) continue;
                this.client.getToastManager().add((Toast)new AdvancementToast(placedAdvancement.getAdvancementEntry()));
                continue;
            }
            LOGGER.warn("Server informed client about progress for unknown advancement {}", entry.getKey());
        }
    }

    public AdvancementManager getManager() {
        return this.manager;
    }

    public void selectTab(@Nullable AdvancementEntry tab, boolean local) {
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
        if (clientPlayNetworkHandler != null && tab != null && local) {
            clientPlayNetworkHandler.sendPacket((Packet)AdvancementTabC2SPacket.open((AdvancementEntry)tab));
        }
        if (this.selectedTab != tab) {
            this.selectedTab = tab;
            if (this.listener != null) {
                this.listener.selectTab(tab);
            }
        }
    }

    public void setListener(// Could not load outer class - annotation placement on inner may be incorrect
    @Nullable ClientAdvancementManager.Listener listener) {
        this.listener = listener;
        this.manager.setListener((AdvancementManager.Listener)listener);
        if (listener != null) {
            this.advancementProgresses.forEach((advancement, progress) -> {
                PlacedAdvancement placedAdvancement = this.manager.get(advancement);
                if (placedAdvancement != null) {
                    listener.setProgress(placedAdvancement, progress);
                }
            });
            listener.selectTab(this.selectedTab);
        }
    }

    public @Nullable AdvancementEntry get(Identifier id) {
        PlacedAdvancement placedAdvancement = this.manager.get(id);
        return placedAdvancement != null ? placedAdvancement.getAdvancementEntry() : null;
    }
}

