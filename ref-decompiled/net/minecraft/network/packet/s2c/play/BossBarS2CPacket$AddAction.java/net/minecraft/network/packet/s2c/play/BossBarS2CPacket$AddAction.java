/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.play;

import java.util.UUID;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

static class BossBarS2CPacket.AddAction
implements BossBarS2CPacket.Action {
    private final Text name;
    private final float percent;
    private final BossBar.Color color;
    private final BossBar.Style style;
    private final boolean darkenSky;
    private final boolean dragonMusic;
    private final boolean thickenFog;

    BossBarS2CPacket.AddAction(BossBar bar) {
        this.name = bar.getName();
        this.percent = bar.getPercent();
        this.color = bar.getColor();
        this.style = bar.getStyle();
        this.darkenSky = bar.shouldDarkenSky();
        this.dragonMusic = bar.hasDragonMusic();
        this.thickenFog = bar.shouldThickenFog();
    }

    private BossBarS2CPacket.AddAction(RegistryByteBuf buf) {
        this.name = (Text)TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.decode(buf);
        this.percent = buf.readFloat();
        this.color = buf.readEnumConstant(BossBar.Color.class);
        this.style = buf.readEnumConstant(BossBar.Style.class);
        short i = buf.readUnsignedByte();
        this.darkenSky = (i & 1) > 0;
        this.dragonMusic = (i & 2) > 0;
        this.thickenFog = (i & 4) > 0;
    }

    @Override
    public BossBarS2CPacket.Type getType() {
        return BossBarS2CPacket.Type.ADD;
    }

    @Override
    public void accept(UUID uuid, BossBarS2CPacket.Consumer consumer) {
        consumer.add(uuid, this.name, this.percent, this.color, this.style, this.darkenSky, this.dragonMusic, this.thickenFog);
    }

    @Override
    public void toPacket(RegistryByteBuf buf) {
        TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.encode(buf, this.name);
        buf.writeFloat(this.percent);
        buf.writeEnumConstant(this.color);
        buf.writeEnumConstant(this.style);
        buf.writeByte(BossBarS2CPacket.maskProperties(this.darkenSky, this.dragonMusic, this.thickenFog));
    }
}
