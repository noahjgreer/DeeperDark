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
public static final class SimpleOption.DoubleSliderCallbacks
extends Enum<SimpleOption.DoubleSliderCallbacks>
implements SimpleOption.SliderCallbacks<Double> {
    public static final /* enum */ SimpleOption.DoubleSliderCallbacks INSTANCE = new SimpleOption.DoubleSliderCallbacks();
    private static final /* synthetic */ SimpleOption.DoubleSliderCallbacks[] field_37876;

    public static SimpleOption.DoubleSliderCallbacks[] values() {
        return (SimpleOption.DoubleSliderCallbacks[])field_37876.clone();
    }

    public static SimpleOption.DoubleSliderCallbacks valueOf(String string) {
        return Enum.valueOf(SimpleOption.DoubleSliderCallbacks.class, string);
    }

    @Override
    public Optional<Double> validate(Double double_) {
        return double_ >= 0.0 && double_ <= 1.0 ? Optional.of(double_) : Optional.empty();
    }

    @Override
    public double toSliderProgress(Double double_) {
        return double_;
    }

    @Override
    public Double toValue(double d) {
        return d;
    }

    public <R> SimpleOption.SliderCallbacks<R> withModifier(final DoubleFunction<? extends R> sliderProgressValueToValue, final ToDoubleFunction<? super R> valueToSliderProgressValue) {
        return new SimpleOption.SliderCallbacks<R>(){

            @Override
            public Optional<R> validate(R value) {
                return this.validate(valueToSliderProgressValue.applyAsDouble(value)).map(sliderProgressValueToValue::apply);
            }

            @Override
            public double toSliderProgress(R value) {
                return this.toSliderProgress(valueToSliderProgressValue.applyAsDouble(value));
            }

            @Override
            public R toValue(double sliderProgress) {
                return sliderProgressValueToValue.apply(this.toValue(sliderProgress));
            }

            @Override
            public Codec<R> codec() {
                return this.codec().xmap(sliderProgressValueToValue::apply, valueToSliderProgressValue::applyAsDouble);
            }
        };
    }

    @Override
    public Codec<Double> codec() {
        return Codec.withAlternative((Codec)Codec.doubleRange((double)0.0, (double)1.0), (Codec)Codec.BOOL, value -> value != false ? 1.0 : 0.0);
    }

    @Override
    public /* synthetic */ Object toValue(double sliderProgress) {
        return this.toValue(sliderProgress);
    }

    @Override
    public /* synthetic */ double toSliderProgress(Object value) {
        return this.toSliderProgress((Double)value);
    }

    private static /* synthetic */ SimpleOption.DoubleSliderCallbacks[] method_41767() {
        return new SimpleOption.DoubleSliderCallbacks[]{INSTANCE};
    }

    static {
        field_37876 = SimpleOption.DoubleSliderCallbacks.method_41767();
    }
}
