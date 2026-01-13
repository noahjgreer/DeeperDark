/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketDecoder;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;

static final class BossBarS2CPacket.Type
extends Enum<BossBarS2CPacket.Type> {
    public static final /* enum */ BossBarS2CPacket.Type ADD = new BossBarS2CPacket.Type(BossBarS2CPacket.AddAction::new);
    public static final /* enum */ BossBarS2CPacket.Type REMOVE = new BossBarS2CPacket.Type(buf -> REMOVE_ACTION);
    public static final /* enum */ BossBarS2CPacket.Type UPDATE_PROGRESS = new BossBarS2CPacket.Type(BossBarS2CPacket.UpdateProgressAction::new);
    public static final /* enum */ BossBarS2CPacket.Type UPDATE_NAME = new BossBarS2CPacket.Type(BossBarS2CPacket.UpdateNameAction::new);
    public static final /* enum */ BossBarS2CPacket.Type UPDATE_STYLE = new BossBarS2CPacket.Type(BossBarS2CPacket.UpdateStyleAction::new);
    public static final /* enum */ BossBarS2CPacket.Type UPDATE_PROPERTIES = new BossBarS2CPacket.Type(BossBarS2CPacket.UpdatePropertiesAction::new);
    final PacketDecoder<RegistryByteBuf, BossBarS2CPacket.Action> parser;
    private static final /* synthetic */ BossBarS2CPacket.Type[] field_29114;

    public static BossBarS2CPacket.Type[] values() {
        return (BossBarS2CPacket.Type[])field_29114.clone();
    }

    public static BossBarS2CPacket.Type valueOf(String string) {
        return Enum.valueOf(BossBarS2CPacket.Type.class, string);
    }

    private BossBarS2CPacket.Type(PacketDecoder<RegistryByteBuf, BossBarS2CPacket.Action> parser) {
        this.parser = parser;
    }

    private static /* synthetic */ BossBarS2CPacket.Type[] method_36948() {
        return new BossBarS2CPacket.Type[]{ADD, REMOVE, UPDATE_PROGRESS, UPDATE_NAME, UPDATE_STYLE, UPDATE_PROPERTIES};
    }

    static {
        field_29114 = BossBarS2CPacket.Type.method_36948();
    }
}
