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
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.SimpleOption;

@Environment(value=EnvType.CLIENT)
class SimpleOption.IntSliderCallbacks.1
implements SimpleOption.SliderCallbacks<R> {
    final /* synthetic */ ToIntFunction field_37869;
    final /* synthetic */ IntFunction field_37870;
    final /* synthetic */ boolean field_63467;

    SimpleOption.IntSliderCallbacks.1(ToIntFunction toIntFunction, IntFunction intFunction, boolean bl) {
        this.field_37869 = toIntFunction;
        this.field_37870 = intFunction;
        this.field_63467 = bl;
    }

    @Override
    public Optional<R> validate(R value) {
        return IntSliderCallbacks.this.validate(this.field_37869.applyAsInt(value)).map(this.field_37870::apply);
    }

    @Override
    public double toSliderProgress(R value) {
        return IntSliderCallbacks.this.toSliderProgress(this.field_37869.applyAsInt(value));
    }

    @Override
    public Optional<R> getNext(R value) {
        if (!this.field_63467) {
            return Optional.empty();
        }
        int i = this.field_37869.applyAsInt(value);
        return Optional.of(this.field_37870.apply(IntSliderCallbacks.this.validate(i + 1).orElse(i)));
    }

    @Override
    public Optional<R> getPrevious(R value) {
        if (!this.field_63467) {
            return Optional.empty();
        }
        int i = this.field_37869.applyAsInt(value);
        return Optional.of(this.field_37870.apply(IntSliderCallbacks.this.validate(i - 1).orElse(i)));
    }

    @Override
    public R toValue(double sliderProgress) {
        return this.field_37870.apply(IntSliderCallbacks.this.toValue(sliderProgress));
    }

    @Override
    public Codec<R> codec() {
        return IntSliderCallbacks.this.codec().xmap(this.field_37870::apply, this.field_37869::applyAsInt);
    }
}
