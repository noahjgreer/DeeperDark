/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.c2s.play;

import java.util.function.Function;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

static final class PlayerInteractEntityC2SPacket.InteractType
extends Enum<PlayerInteractEntityC2SPacket.InteractType> {
    public static final /* enum */ PlayerInteractEntityC2SPacket.InteractType INTERACT = new PlayerInteractEntityC2SPacket.InteractType(PlayerInteractEntityC2SPacket.InteractHandler::new);
    public static final /* enum */ PlayerInteractEntityC2SPacket.InteractType ATTACK = new PlayerInteractEntityC2SPacket.InteractType(buf -> ATTACK);
    public static final /* enum */ PlayerInteractEntityC2SPacket.InteractType INTERACT_AT = new PlayerInteractEntityC2SPacket.InteractType(PlayerInteractEntityC2SPacket.InteractAtHandler::new);
    final Function<PacketByteBuf, PlayerInteractEntityC2SPacket.InteractTypeHandler> handlerGetter;
    private static final /* synthetic */ PlayerInteractEntityC2SPacket.InteractType[] field_29175;

    public static PlayerInteractEntityC2SPacket.InteractType[] values() {
        return (PlayerInteractEntityC2SPacket.InteractType[])field_29175.clone();
    }

    public static PlayerInteractEntityC2SPacket.InteractType valueOf(String string) {
        return Enum.valueOf(PlayerInteractEntityC2SPacket.InteractType.class, string);
    }

    private PlayerInteractEntityC2SPacket.InteractType(Function<PacketByteBuf, PlayerInteractEntityC2SPacket.InteractTypeHandler> handlerGetter) {
        this.handlerGetter = handlerGetter;
    }

    private static /* synthetic */ PlayerInteractEntityC2SPacket.InteractType[] method_36956() {
        return new PlayerInteractEntityC2SPacket.InteractType[]{INTERACT, ATTACK, INTERACT_AT};
    }

    static {
        field_29175 = PlayerInteractEntityC2SPacket.InteractType.method_36956();
    }
}
