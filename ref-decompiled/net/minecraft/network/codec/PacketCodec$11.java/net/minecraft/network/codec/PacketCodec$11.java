/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Suppliers
 */
package net.minecraft.network.codec;

import com.google.common.base.Suppliers;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.minecraft.network.codec.PacketCodec;

static class PacketCodec.11
implements PacketCodec<B, T> {
    private final Supplier<PacketCodec<B, T>> codecSupplier = Suppliers.memoize(() -> (PacketCodec)this.field_49711.apply(this));
    final /* synthetic */ UnaryOperator field_49711;

    PacketCodec.11(UnaryOperator unaryOperator) {
        this.field_49711 = unaryOperator;
    }

    @Override
    public T decode(B object) {
        return this.codecSupplier.get().decode(object);
    }

    @Override
    public void encode(B object, T object2) {
        this.codecSupplier.get().encode(object, object2);
    }
}
