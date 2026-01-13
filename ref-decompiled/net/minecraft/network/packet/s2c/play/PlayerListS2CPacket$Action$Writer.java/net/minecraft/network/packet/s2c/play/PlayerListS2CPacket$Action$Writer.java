/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;

public static interface PlayerListS2CPacket.Action.Writer {
    public void write(RegistryByteBuf var1, PlayerListS2CPacket.Entry var2);
}
