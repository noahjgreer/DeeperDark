/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client;

import java.io.File;
import java.net.Proxy;
import java.nio.file.Path;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.resource.ResourceIndex;
import net.minecraft.client.session.Session;
import net.minecraft.util.StringHelper;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RunArgs {
    public final Network network;
    public final WindowSettings windowSettings;
    public final Directories directories;
    public final Game game;
    public final QuickPlay quickPlay;

    public RunArgs(Network network, WindowSettings windowSettings, Directories dirs, Game game, QuickPlay quickPlay) {
        this.network = network;
        this.windowSettings = windowSettings;
        this.directories = dirs;
        this.game = game;
        this.quickPlay = quickPlay;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Network {
        public final Session session;
        public final Proxy netProxy;

        public Network(Session session, Proxy proxy) {
            this.session = session;
            this.netProxy = proxy;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Directories {
        public final File runDir;
        public final File resourcePackDir;
        public final File assetDir;
        public final @Nullable String assetIndex;

        public Directories(File runDir, File resPackDir, File assetDir, @Nullable String assetIndex) {
            this.runDir = runDir;
            this.resourcePackDir = resPackDir;
            this.assetDir = assetDir;
            this.assetIndex = assetIndex;
        }

        public Path getAssetDir() {
            return this.assetIndex == null ? this.assetDir.toPath() : ResourceIndex.buildFileSystem(this.assetDir.toPath(), this.assetIndex);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Game {
        public final boolean demo;
        public final String version;
        public final String versionType;
        public final boolean multiplayerDisabled;
        public final boolean onlineChatDisabled;
        public final boolean tracyEnabled;
        public final boolean renderDebugLabels;
        public final boolean offlineDeveloperMode;

        public Game(boolean demo, String version, String versionType, boolean multiplayerDisabled, boolean onlineChatDisabled, boolean tracyEnabled, boolean renderDebugLabels, boolean offlineDeveloperMode) {
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

    @Environment(value=EnvType.CLIENT)
    public record QuickPlay(@Nullable String logPath, QuickPlayVariant variant) {
        public boolean isEnabled() {
            return this.variant.isEnabled();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record DisabledQuickPlay() implements QuickPlayVariant
    {
        @Override
        public boolean isEnabled() {
            return false;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record RealmsQuickPlay(String realmId) implements QuickPlayVariant
    {
        @Override
        public boolean isEnabled() {
            return !StringHelper.isBlank(this.realmId);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record MultiplayerQuickPlay(String serverAddress) implements QuickPlayVariant
    {
        @Override
        public boolean isEnabled() {
            return !StringHelper.isBlank(this.serverAddress);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record SingleplayerQuickPlay(@Nullable String worldId) implements QuickPlayVariant
    {
        @Override
        public boolean isEnabled() {
            return true;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static sealed interface QuickPlayVariant
    permits SingleplayerQuickPlay, MultiplayerQuickPlay, RealmsQuickPlay, DisabledQuickPlay {
        public static final QuickPlayVariant DEFAULT = new DisabledQuickPlay();

        public boolean isEnabled();
    }
}
