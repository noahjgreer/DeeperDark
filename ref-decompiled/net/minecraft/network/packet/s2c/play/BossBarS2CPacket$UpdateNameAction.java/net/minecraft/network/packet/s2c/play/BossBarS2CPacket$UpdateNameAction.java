/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.play;

import java.util.UUID;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

record BossBarS2CPacket.UpdateNameAction(Text name) implements BossBarS2CPacket.Action
{
    private BossBarS2CPacket.UpdateNameAction(RegistryByteBuf buf) {
        this((Text)TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.decode(buf));
    }

    @Override
    public BossBarS2CPacket.Type getType() {
        return BossBarS2CPacket.Type.UPDATE_NAME;
    }

    @Override
    public void accept(UUID uuid, BossBarS2CPacket.Consumer consumer) {
        consumer.updateName(uuid, this.name);
    }

    @Override
    public void toPacket(RegistryByteBuf buf) {
        TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.encode(buf, this.name);
    }
}
