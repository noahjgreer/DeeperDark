/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.option.GameOptions
 *  net.minecraft.client.option.InactivityFpsLimit
 *  net.minecraft.client.option.InactivityFpsLimiter
 *  net.minecraft.client.option.InactivityFpsLimiter$LimitReason
 *  net.minecraft.util.Util
 */
package net.minecraft.client.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.InactivityFpsLimit;
import net.minecraft.client.option.InactivityFpsLimiter;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class InactivityFpsLimiter {
    private static final int IN_GUI_FPS = 60;
    private static final int MINIMIZED_FPS = 10;
    private static final int AFK_STAGE_1_FPS = 30;
    private static final int AFK_STAGE_2_FPS = 10;
    private static final long AFK_STAGE_1_THRESHOLD = 60000L;
    private static final long AFK_STAGE_2_THRESHOLD = 600000L;
    private final GameOptions options;
    private final MinecraftClient client;
    private int maxFps;
    private long lastInputTime;

    public InactivityFpsLimiter(GameOptions options, MinecraftClient client) {
        this.options = options;
        this.client = client;
        this.maxFps = (Integer)options.getMaxFps().getValue();
    }

    public int update() {
        return switch (this.getLimitReason().ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> this.maxFps;
            case 1 -> 10;
            case 2 -> 10;
            case 3 -> Math.min(this.maxFps, 30);
            case 4 -> 60;
        };
    }

    public LimitReason getLimitReason() {
        InactivityFpsLimit inactivityFpsLimit = (InactivityFpsLimit)this.options.getInactivityFpsLimit().getValue();
        if (this.client.getWindow().isMinimized()) {
            return LimitReason.WINDOW_ICONIFIED;
        }
        if (inactivityFpsLimit == InactivityFpsLimit.AFK) {
            long l = Util.getMeasuringTimeMs() - this.lastInputTime;
            if (l > 600000L) {
                return LimitReason.LONG_AFK;
            }
            if (l > 60000L) {
                return LimitReason.SHORT_AFK;
            }
        }
        if (this.client.world == null && (this.client.currentScreen != null || this.client.getOverlay() != null)) {
            return LimitReason.OUT_OF_LEVEL_MENU;
        }
        return LimitReason.NONE;
    }

    public boolean shouldDisableProfilerTimeout() {
        LimitReason limitReason = this.getLimitReason();
        return limitReason == LimitReason.WINDOW_ICONIFIED || limitReason == LimitReason.LONG_AFK;
    }

    public void setMaxFps(int maxFps) {
        this.maxFps = maxFps;
    }

    public void onInput() {
        this.lastInputTime = Util.getMeasuringTimeMs();
    }
}

