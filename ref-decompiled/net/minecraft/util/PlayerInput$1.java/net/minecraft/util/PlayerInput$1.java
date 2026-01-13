/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.PlayerInput;

class PlayerInput.1
implements PacketCodec<PacketByteBuf, PlayerInput> {
    PlayerInput.1() {
    }

    @Override
    public void encode(PacketByteBuf packetByteBuf, PlayerInput playerInput) {
        byte b = 0;
        b = (byte)(b | (playerInput.forward() ? 1 : 0));
        b = (byte)(b | (playerInput.backward() ? 2 : 0));
        b = (byte)(b | (playerInput.left() ? 4 : 0));
        b = (byte)(b | (playerInput.right() ? 8 : 0));
        b = (byte)(b | (playerInput.jump() ? 16 : 0));
        b = (byte)(b | (playerInput.sneak() ? 32 : 0));
        b = (byte)(b | (playerInput.sprint() ? 64 : 0));
        packetByteBuf.writeByte(b);
    }

    @Override
    public PlayerInput decode(PacketByteBuf packetByteBuf) {
        byte b = packetByteBuf.readByte();
        boolean bl = (b & 1) != 0;
        boolean bl2 = (b & 2) != 0;
        boolean bl3 = (b & 4) != 0;
        boolean bl4 = (b & 8) != 0;
        boolean bl5 = (b & 0x10) != 0;
        boolean bl6 = (b & 0x20) != 0;
        boolean bl7 = (b & 0x40) != 0;
        return new PlayerInput(bl, bl2, bl3, bl4, bl5, bl6, bl7);
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((PacketByteBuf)((Object)object), (PlayerInput)object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((PacketByteBuf)((Object)object));
    }
}
