/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.floats.Float2FloatFunction
 */
package net.minecraft.util.function;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.util.function.ToFloatFunction;

static class ToFloatFunction.1
implements ToFloatFunction<Float> {
    final /* synthetic */ Float2FloatFunction field_37410;

    ToFloatFunction.1(Float2FloatFunction float2FloatFunction) {
        this.field_37410 = float2FloatFunction;
    }

    @Override
    public float apply(Float float_) {
        return ((Float)this.field_37410.apply((Object)float_)).floatValue();
    }

    @Override
    public float min() {
        return Float.NEGATIVE_INFINITY;
    }

    @Override
    public float max() {
        return Float.POSITIVE_INFINITY;
    }
}
