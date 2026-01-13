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
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
final class MinecraftClient.ChatRestriction.4
extends MinecraftClient.ChatRestriction {
    MinecraftClient.ChatRestriction.4(Text text) {
    }

    @Override
    public boolean allowsChat(boolean singlePlayer) {
        return singlePlayer;
    }
}
