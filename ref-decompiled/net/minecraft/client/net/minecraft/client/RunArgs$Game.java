/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static class RunArgs.Game {
    public final boolean demo;
    public final String version;
    public final String versionType;
    public final boolean multiplayerDisabled;
    public final boolean onlineChatDisabled;
    public final boolean tracyEnabled;
    public final boolean renderDebugLabels;
    public final boolean offlineDeveloperMode;

    public RunArgs.Game(boolean demo, String version, String versionType, boolean multiplayerDisabled, boolean onlineChatDisabled, boolean tracyEnabled, boolean renderDebugLabels, boolean offlineDeveloperMode) {
        this.demo = demo;
        this.version = version;
        this.versionType = versionType;
        this.multiplayerDisabled = multiplayerDisabled;
        this.onlineChatDisabled = onlineChatDisabled;
        this.tracyEnabled = tracyEnabled;
        this.renderDebugLabels = renderDebugLabels;
        this.offlineDeveloperMode = offlineDeveloperMode;
    }
}
