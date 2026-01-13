/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet;

import java.util.Map;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

static class CustomPayload.1
implements PacketCodec<B, CustomPayload> {
    final /* synthetic */ Map field_48658;
    final /* synthetic */ CustomPayload.CodecFactory field_48659;

    CustomPayload.1(Map map, CustomPayload.CodecFactory codecFactory) {
        this.field_48658 = map;
        this.field_48659 = codecFactory;
    }

    private PacketCodec<? super B, ? extends CustomPayload> getCodec(Identifier id) {
        PacketCodec packetCodec = (PacketCodec)this.field_48658.get(id);
        if (packetCodec != null) {
            return packetCodec;
        }
        return this.field_48659.create(id);
    }

    private <T extends CustomPayload> void encode(B value, CustomPayload.Id<T> id, CustomPayload payload) {
        ((PacketByteBuf)((Object)value)).writeIdentifier(id.id());
        PacketCodec packetCodec = this.getCodec(id.id);
        packetCodec.encode(value, payload);
    }

    @Override
    public void encode(B packetByteBuf, CustomPayload customPayload) {
        this.encode(packetByteBuf, customPayload.getId(), customPayload);
    }

    @Override
    public CustomPayload decode(B packetByteBuf) {
        Identifier identifier = ((PacketByteBuf)((Object)packetByteBuf)).readIdentifier();
        return (CustomPayload)this.getCodec(identifier).decode(packetByteBuf);
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((Object)((Object)((PacketByteBuf)((Object)object))), (CustomPayload)object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((B)((Object)((PacketByteBuf)((Object)object))));
    }
}
