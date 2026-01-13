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
import net.minecraft.client.RunArgs;

@Environment(value=EnvType.CLIENT)
public static sealed interface RunArgs.QuickPlayVariant
permits RunArgs.SingleplayerQuickPlay, RunArgs.MultiplayerQuickPlay, RunArgs.RealmsQuickPlay, RunArgs.DisabledQuickPlay {
    public static final RunArgs.QuickPlayVariant DEFAULT = new RunArgs.DisabledQuickPlay();

    public boolean isEnabled();
}
