/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.state;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.handler.PacketDecoderException;
import net.minecraft.network.handler.PacketEncoderException;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.state.PlayStateFactories;

static class PlayStateFactories.1
implements PacketCodec<RegistryByteBuf, CreativeInventoryActionC2SPacket> {
    final /* synthetic */ PlayStateFactories.PacketCodecModifierContext field_58069;
    final /* synthetic */ PacketCodec field_58070;

    PlayStateFactories.1(PlayStateFactories.PacketCodecModifierContext packetCodecModifierContext, PacketCodec packetCodec) {
        this.field_58069 = packetCodecModifierContext;
        this.field_58070 = packetCodec;
    }

    @Override
    public CreativeInventoryActionC2SPacket decode(RegistryByteBuf registryByteBuf) {
        if (!this.field_58069.isInCreativeMode()) {
            throw new PacketDecoderException("Not in creative mode");
        }
        return (CreativeInventoryActionC2SPacket)this.field_58070.decode(registryByteBuf);
    }

    @Override
    public void encode(RegistryByteBuf registryByteBuf, CreativeInventoryActionC2SPacket creativeInventoryActionC2SPacket) {
        if (!this.field_58069.isInCreativeMode()) {
            throw new PacketEncoderException("Not in creative mode");
        }
        this.field_58070.encode(registryByteBuf, creativeInventoryActionC2SPacket);
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((RegistryByteBuf)((Object)object), (CreativeInventoryActionC2SPacket)object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((RegistryByteBuf)((Object)object));
    }
}
