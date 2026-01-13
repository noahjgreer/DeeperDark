/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.option;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.function.DoubleFunction;
import java.util.function.ToDoubleFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.SimpleOption;

@Environment(value=EnvType.CLIENT)
class SimpleOption.DoubleSliderCallbacks.1
implements SimpleOption.SliderCallbacks<R> {
    final /* synthetic */ ToDoubleFunction field_38283;
    final /* synthetic */ DoubleFunction field_38284;

    SimpleOption.DoubleSliderCallbacks.1(ToDoubleFunction toDoubleFunction, DoubleFunction doubleFunction) {
        this.field_38283 = toDoubleFunction;
        this.field_38284 = doubleFunction;
    }

    @Override
    public Optional<R> validate(R value) {
        return DoubleSliderCallbacks.this.validate(this.field_38283.applyAsDouble(value)).map(this.field_38284::apply);
    }

    @Override
    public double toSliderProgress(R value) {
        return DoubleSliderCallbacks.this.toSliderProgress(this.field_38283.applyAsDouble(value));
    }

    @Override
    public R toValue(double sliderProgress) {
        return this.field_38284.apply(DoubleSliderCallbacks.this.toValue(sliderProgress));
    }

    @Override
    public Codec<R> codec() {
        return DoubleSliderCallbacks.this.codec().xmap(this.field_38284::apply, this.field_38283::applyAsDouble);
    }
}
