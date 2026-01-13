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
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
static interface SimpleOption.IntSliderCallbacks
extends SimpleOption.SliderCallbacks<Integer> {
    public int minInclusive();

    public int maxInclusive();

    @Override
    default public Optional<Integer> getNext(Integer integer) {
        return Optional.of(integer + 1);
    }

    @Override
    default public Optional<Integer> getPrevious(Integer integer) {
        return Optional.of(integer - 1);
    }

    @Override
    default public double toSliderProgress(Integer integer) {
        if (integer.intValue() == this.minInclusive()) {
            return 0.0;
        }
        if (integer.intValue() == this.maxInclusive()) {
            return 1.0;
        }
        return MathHelper.map((double)integer.intValue() + 0.5, (double)this.minInclusive(), (double)this.maxInclusive() + 1.0, 0.0, 1.0);
    }

    @Override
    default public Integer toValue(double d) {
        if (d >= 1.0) {
            d = 0.99999f;
        }
        return MathHelper.floor(MathHelper.map(d, 0.0, 1.0, (double)this.minInclusive(), (double)this.maxInclusive() + 1.0));
    }

    default public <R> SimpleOption.SliderCallbacks<R> withModifier(final IntFunction<? extends R> sliderProgressValueToValue, final ToIntFunction<? super R> valueToSliderProgressValue, final boolean bl) {
        return new SimpleOption.SliderCallbacks<R>(){

            @Override
            public Optional<R> validate(R value) {
                return this.validate(valueToSliderProgressValue.applyAsInt(value)).map(sliderProgressValueToValue::apply);
            }

            @Override
            public double toSliderProgress(R value) {
                return this.toSliderProgress(valueToSliderProgressValue.applyAsInt(value));
            }

            @Override
            public Optional<R> getNext(R value) {
                if (!bl) {
                    return Optional.empty();
                }
                int i = valueToSliderProgressValue.applyAsInt(value);
                return Optional.of(sliderProgressValueToValue.apply(this.validate(i + 1).orElse(i)));
            }

            @Override
            public Optional<R> getPrevious(R value) {
                if (!bl) {
                    return Optional.empty();
                }
                int i = valueToSliderProgressValue.applyAsInt(value);
                return Optional.of(sliderProgressValueToValue.apply(this.validate(i - 1).orElse(i)));
            }

            @Override
            public R toValue(double sliderProgress) {
                return sliderProgressValueToValue.apply(this.toValue(sliderProgress));
            }

            @Override
            public Codec<R> codec() {
                return this.codec().xmap(sliderProgressValueToValue::apply, valueToSliderProgressValue::applyAsInt);
            }
        };
    }

    @Override
    default public /* synthetic */ Object toValue(double sliderProgress) {
        return this.toValue(sliderProgress);
    }

    @Override
    default public /* synthetic */ Optional getPrevious(Object value) {
        return this.getPrevious((Integer)value);
    }

    @Override
    default public /* synthetic */ Optional getNext(Object value) {
        return this.getNext((Integer)value);
    }
}
