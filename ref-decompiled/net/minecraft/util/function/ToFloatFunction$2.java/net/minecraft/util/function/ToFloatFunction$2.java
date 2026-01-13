/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.function;

import java.util.function.Function;
import net.minecraft.util.function.ToFloatFunction;

class ToFloatFunction.2
implements ToFloatFunction<C2> {
    final /* synthetic */ ToFloatFunction field_37411;
    final /* synthetic */ Function field_37412;

    ToFloatFunction.2(ToFloatFunction toFloatFunction, ToFloatFunction toFloatFunction2, Function function) {
        this.field_37411 = toFloatFunction2;
        this.field_37412 = function;
    }

    @Override
    public float apply(C2 x) {
        return this.field_37411.apply(this.field_37412.apply(x));
    }

    @Override
    public float min() {
        return this.field_37411.min();
    }

    @Override
    public float max() {
        return this.field_37411.max();
    }
}
