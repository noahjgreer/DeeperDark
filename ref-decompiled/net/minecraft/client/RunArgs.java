/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.RunArgs
 *  net.minecraft.client.RunArgs$Directories
 *  net.minecraft.client.RunArgs$Game
 *  net.minecraft.client.RunArgs$Network
 *  net.minecraft.client.RunArgs$QuickPlay
 *  net.minecraft.client.WindowSettings
 */
package net.minecraft.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.RunArgs;
import net.minecraft.client.WindowSettings;

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
}

