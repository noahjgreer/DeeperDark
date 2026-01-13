/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.world;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.world.level.storage.LevelSummary;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static class WorldListWidget.Builder {
    private final MinecraftClient client;
    private final Screen parent;
    private int width;
    private int height;
    private String search = "";
    private WorldListWidget.WorldListType worldListType = WorldListWidget.WorldListType.SINGLEPLAYER;
    private @Nullable WorldListWidget predecessor = null;
    private @Nullable Consumer<LevelSummary> selectionCallback = null;
    private @Nullable Consumer<WorldListWidget.WorldEntry> confirmationCallback = null;

    public WorldListWidget.Builder(MinecraftClient client, Screen parent) {
        this.client = client;
        this.parent = parent;
    }

    public WorldListWidget.Builder width(int width) {
        this.width = width;
        return this;
    }

    public WorldListWidget.Builder height(int height) {
        this.height = height;
        return this;
    }

    public WorldListWidget.Builder search(String search) {
        this.search = search;
        return this;
    }

    public WorldListWidget.Builder predecessor(@Nullable WorldListWidget predecessor) {
        this.predecessor = predecessor;
        return this;
    }

    public WorldListWidget.Builder selectionCallback(Consumer<LevelSummary> selectionCallback) {
        this.selectionCallback = selectionCallback;
        return this;
    }

    public WorldListWidget.Builder confirmationCallback(Consumer<WorldListWidget.WorldEntry> confirmationCallback) {
        this.confirmationCallback = confirmationCallback;
        return this;
    }

    public WorldListWidget.Builder uploadWorld() {
        this.worldListType = WorldListWidget.WorldListType.UPLOAD_WORLD;
        return this;
    }

    public WorldListWidget toWidget() {
        return new WorldListWidget(this.parent, this.client, this.width, this.height, this.search, this.predecessor, this.selectionCallback, this.confirmationCallback, this.worldListType);
    }
}
