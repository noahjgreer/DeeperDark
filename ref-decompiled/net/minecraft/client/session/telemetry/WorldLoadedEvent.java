/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.network.ServerInfo
 *  net.minecraft.client.session.telemetry.PropertyMap$Builder
 *  net.minecraft.client.session.telemetry.TelemetryEventProperty
 *  net.minecraft.client.session.telemetry.TelemetryEventProperty$GameMode
 *  net.minecraft.client.session.telemetry.TelemetryEventProperty$ServerType
 *  net.minecraft.client.session.telemetry.TelemetryEventType
 *  net.minecraft.client.session.telemetry.TelemetrySender
 *  net.minecraft.client.session.telemetry.WorldLoadedEvent
 *  net.minecraft.client.session.telemetry.WorldLoadedEvent$1
 *  net.minecraft.world.GameMode
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.session.telemetry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.session.telemetry.PropertyMap;
import net.minecraft.client.session.telemetry.TelemetryEventProperty;
import net.minecraft.client.session.telemetry.TelemetryEventType;
import net.minecraft.client.session.telemetry.TelemetrySender;
import net.minecraft.client.session.telemetry.WorldLoadedEvent;
import net.minecraft.world.GameMode;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class WorldLoadedEvent {
    private boolean sent;
    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable TelemetryEventProperty.GameMode gameMode;
    private @Nullable String brand;
    private final @Nullable String minigameName;

    public WorldLoadedEvent(@Nullable String minigameName) {
        this.minigameName = minigameName;
    }

    public void putServerType(PropertyMap.Builder builder) {
        if (this.brand != null) {
            builder.put(TelemetryEventProperty.SERVER_MODDED, (Object)(!this.brand.equals("vanilla") ? 1 : 0));
        }
        builder.put(TelemetryEventProperty.SERVER_TYPE, (Object)this.getServerType());
    }

    private TelemetryEventProperty.ServerType getServerType() {
        ServerInfo serverInfo = MinecraftClient.getInstance().getCurrentServerEntry();
        if (serverInfo != null && serverInfo.isRealm()) {
            return TelemetryEventProperty.ServerType.REALM;
        }
        if (MinecraftClient.getInstance().isIntegratedServerRunning()) {
            return TelemetryEventProperty.ServerType.LOCAL;
        }
        return TelemetryEventProperty.ServerType.OTHER;
    }

    public boolean send(TelemetrySender sender) {
        if (this.sent || this.gameMode == null || this.brand == null) {
            return false;
        }
        this.sent = true;
        sender.send(TelemetryEventType.WORLD_LOADED, adder -> {
            adder.put(TelemetryEventProperty.GAME_MODE, (Object)this.gameMode);
            if (this.minigameName != null) {
                adder.put(TelemetryEventProperty.REALMS_MAP_CONTENT, (Object)this.minigameName);
            }
        });
        return true;
    }

    public void setGameMode(GameMode gameMode, boolean hardcore) {
        this.gameMode = switch (1.field_34955[gameMode.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> {
                if (hardcore) {
                    yield TelemetryEventProperty.GameMode.HARDCORE;
                }
                yield TelemetryEventProperty.GameMode.SURVIVAL;
            }
            case 2 -> TelemetryEventProperty.GameMode.CREATIVE;
            case 3 -> TelemetryEventProperty.GameMode.ADVENTURE;
            case 4 -> TelemetryEventProperty.GameMode.SPECTATOR;
        };
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}

