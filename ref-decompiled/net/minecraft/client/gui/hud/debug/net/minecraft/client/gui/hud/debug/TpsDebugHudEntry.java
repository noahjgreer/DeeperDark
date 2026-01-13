/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.hud.debug;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.ServerTickManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.tick.TickManager;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TpsDebugHudEntry
implements DebugHudEntry {
    @Override
    public void render(DebugHudLines lines, @Nullable World world, @Nullable WorldChunk clientChunk, @Nullable WorldChunk chunk) {
        String string3;
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        IntegratedServer integratedServer = minecraftClient.getServer();
        ClientPlayNetworkHandler clientPlayNetworkHandler = minecraftClient.getNetworkHandler();
        if (clientPlayNetworkHandler == null || world == null) {
            return;
        }
        ClientConnection clientConnection = clientPlayNetworkHandler.getConnection();
        float f = clientConnection.getAveragePacketsSent();
        float g = clientConnection.getAveragePacketsReceived();
        TickManager tickManager = world.getTickManager();
        String string = tickManager.isStepping() ? " (frozen - stepping)" : (tickManager.isFrozen() ? " (frozen)" : "");
        if (integratedServer != null) {
            ServerTickManager serverTickManager = integratedServer.getTickManager();
            boolean bl = serverTickManager.isSprinting();
            if (bl) {
                string = " (sprinting)";
            }
            String string2 = bl ? "-" : String.format(Locale.ROOT, "%.1f", Float.valueOf(tickManager.getMillisPerTick()));
            string3 = String.format(Locale.ROOT, "Integrated server @ %.1f/%s ms%s, %.0f tx, %.0f rx", Float.valueOf(integratedServer.getAverageTickTime()), string2, string, Float.valueOf(f), Float.valueOf(g));
        } else {
            string3 = String.format(Locale.ROOT, "\"%s\" server%s, %.0f tx, %.0f rx", clientPlayNetworkHandler.getBrand(), string, Float.valueOf(f), Float.valueOf(g));
        }
        lines.addLine(string3);
    }

    @Override
    public boolean canShow(boolean reducedDebugInfo) {
        return true;
    }
}
