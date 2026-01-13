/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.play;

import java.util.UUID;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;

static class BossBarS2CPacket.UpdateStyleAction
implements BossBarS2CPacket.Action {
    private final BossBar.Color color;
    private final BossBar.Style style;

    BossBarS2CPacket.UpdateStyleAction(BossBar.Color color, BossBar.Style style) {
        this.color = color;
        this.style = style;
    }

    private BossBarS2CPacket.UpdateStyleAction(RegistryByteBuf buf) {
        this.color = buf.readEnumConstant(BossBar.Color.class);
        this.style = buf.readEnumConstant(BossBar.Style.class);
    }

    @Override
    public BossBarS2CPacket.Type getType() {
        return BossBarS2CPacket.Type.UPDATE_STYLE;
    }

    @Override
    public void accept(UUID uuid, BossBarS2CPacket.Consumer consumer) {
        consumer.updateStyle(uuid, this.color, this.style);
    }

    @Override
    public void toPacket(RegistryByteBuf buf) {
        buf.writeEnumConstant(this.color);
        buf.writeEnumConstant(this.style);
    }
}
