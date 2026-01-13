/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.play;

import java.util.UUID;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;

static class BossBarS2CPacket.UpdatePropertiesAction
implements BossBarS2CPacket.Action {
    private final boolean darkenSky;
    private final boolean dragonMusic;
    private final boolean thickenFog;

    BossBarS2CPacket.UpdatePropertiesAction(boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
        this.darkenSky = darkenSky;
        this.dragonMusic = dragonMusic;
        this.thickenFog = thickenFog;
    }

    private BossBarS2CPacket.UpdatePropertiesAction(RegistryByteBuf buf) {
        short i = buf.readUnsignedByte();
        this.darkenSky = (i & 1) > 0;
        this.dragonMusic = (i & 2) > 0;
        this.thickenFog = (i & 4) > 0;
    }

    @Override
    public BossBarS2CPacket.Type getType() {
        return BossBarS2CPacket.Type.UPDATE_PROPERTIES;
    }

    @Override
    public void accept(UUID uuid, BossBarS2CPacket.Consumer consumer) {
        consumer.updateProperties(uuid, this.darkenSky, this.dragonMusic, this.thickenFog);
    }

    @Override
    public void toPacket(RegistryByteBuf buf) {
        buf.writeByte(BossBarS2CPacket.maskProperties(this.darkenSky, this.dragonMusic, this.thickenFog));
    }
}
