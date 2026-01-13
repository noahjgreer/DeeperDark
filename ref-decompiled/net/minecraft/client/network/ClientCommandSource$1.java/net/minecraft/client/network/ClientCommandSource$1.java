/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.packet.s2c.play.ChatSuggestionsS2CPacket;

@Environment(value=EnvType.CLIENT)
static class ClientCommandSource.1 {
    static final /* synthetic */ int[] field_39795;

    static {
        field_39795 = new int[ChatSuggestionsS2CPacket.Action.values().length];
        try {
            ClientCommandSource.1.field_39795[ChatSuggestionsS2CPacket.Action.ADD.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ClientCommandSource.1.field_39795[ChatSuggestionsS2CPacket.Action.REMOVE.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ClientCommandSource.1.field_39795[ChatSuggestionsS2CPacket.Action.SET.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
