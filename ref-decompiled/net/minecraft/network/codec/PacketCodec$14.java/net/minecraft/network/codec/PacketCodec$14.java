/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.codec;

import java.util.function.Function;
import net.minecraft.network.codec.PacketCodec;

class PacketCodec.14
implements PacketCodec<B, O> {
    final /* synthetic */ Function field_48587;
    final /* synthetic */ Function field_48588;

    PacketCodec.14(Function function, Function function2) {
        this.field_48587 = function;
        this.field_48588 = function2;
    }

    @Override
    public O decode(B object) {
        return this.field_48587.apply(PacketCodec.this.decode(object));
    }

    @Override
    public void encode(B object, O object2) {
        PacketCodec.this.encode(object, this.field_48588.apply(object2));
    }
}
