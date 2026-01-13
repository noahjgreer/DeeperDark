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
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public record SimpleOption.CategoricalSliderCallbacks<T>(List<T> values, Codec<T> codec) implements SimpleOption.SliderCallbacks<T>
{
    @Override
    public double toSliderProgress(T value) {
        if (value == this.values.getFirst()) {
            return 0.0;
        }
        if (value == this.values.getLast()) {
            return 1.0;
        }
        return MathHelper.map((double)this.values.indexOf(value), 0.0, (double)(this.values.size() - 1), 0.0, 1.0);
    }

    @Override
    public Optional<T> getNext(T value) {
        int i = this.values.indexOf(value);
        int j = MathHelper.clamp(i + 1, 0, this.values.size() - 1);
        return Optional.of(this.values.get(j));
    }

    @Override
    public Optional<T> getPrevious(T value) {
        int i = this.values.indexOf(value);
        int j = MathHelper.clamp(i - 1, 0, this.values.size() - 1);
        return Optional.of(this.values.get(j));
    }

    @Override
    public T toValue(double sliderProgress) {
        if (sliderProgress >= 1.0) {
            sliderProgress = 0.99999f;
        }
        int i = MathHelper.floor(MathHelper.map(sliderProgress, 0.0, 1.0, 0.0, (double)this.values.size()));
        return this.values.get(MathHelper.clamp(i, 0, this.values.size() - 1));
    }

    @Override
    public Optional<T> validate(T value) {
        int i = this.values.indexOf(value);
        return i > -1 ? Optional.of(value) : Optional.empty();
    }
}
