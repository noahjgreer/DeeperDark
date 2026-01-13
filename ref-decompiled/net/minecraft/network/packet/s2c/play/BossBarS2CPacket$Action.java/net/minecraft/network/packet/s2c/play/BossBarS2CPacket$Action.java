/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.play;

import java.util.UUID;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;

static interface BossBarS2CPacket.Action {
    public BossBarS2CPacket.Type getType();

    public void accept(UUID var1, BossBarS2CPacket.Consumer var2);

    public void toPacket(RegistryByteBuf var1);
}
