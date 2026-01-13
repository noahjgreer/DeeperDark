/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.play;

import java.util.UUID;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;

class BossBarS2CPacket.1
implements BossBarS2CPacket.Action {
    BossBarS2CPacket.1() {
    }

    @Override
    public BossBarS2CPacket.Type getType() {
        return BossBarS2CPacket.Type.REMOVE;
    }

    @Override
    public void accept(UUID uuid, BossBarS2CPacket.Consumer consumer) {
        consumer.remove(uuid);
    }

    @Override
    public void toPacket(RegistryByteBuf buf) {
    }
}
