/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.codec;

import net.minecraft.network.codec.PacketCodec;

static class PacketCodec.13
implements PacketCodec<B, V> {
    final /* synthetic */ Object field_48586;

    PacketCodec.13(Object object) {
        this.field_48586 = object;
    }

    @Override
    public V decode(B object) {
        return this.field_48586;
    }

    @Override
    public void encode(B object, V object2) {
        if (!object2.equals(this.field_48586)) {
            throw new IllegalStateException("Can't encode '" + String.valueOf(object2) + "', expected '" + String.valueOf(this.field_48586) + "'");
        }
    }
}
