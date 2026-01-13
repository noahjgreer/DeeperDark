/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.play;

import java.util.UUID;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;

record BossBarS2CPacket.UpdateProgressAction(float progress) implements BossBarS2CPacket.Action
{
    private BossBarS2CPacket.UpdateProgressAction(RegistryByteBuf buf) {
        this(buf.readFloat());
    }

    @Override
    public BossBarS2CPacket.Type getType() {
        return BossBarS2CPacket.Type.UPDATE_PROGRESS;
    }

    @Override
    public void accept(UUID uuid, BossBarS2CPacket.Consumer consumer) {
        consumer.updateProgress(uuid, this.progress);
    }

    @Override
    public void toPacket(RegistryByteBuf buf) {
        buf.writeFloat(this.progress);
    }
}
