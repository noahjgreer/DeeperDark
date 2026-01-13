/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.option;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Optional;
import java.util.function.IntSupplier;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public record SimpleOption.MaxSuppliableIntCallbacks(int minInclusive, IntSupplier maxSupplier, int encodableMaxInclusive) implements SimpleOption.IntSliderCallbacks,
SimpleOption.TypeChangeableCallbacks<Integer>
{
    @Override
    public Optional<Integer> validate(Integer integer) {
        return Optional.of(MathHelper.clamp(integer, this.minInclusive(), this.maxInclusive()));
    }

    @Override
    public int maxInclusive() {
        return this.maxSupplier.getAsInt();
    }

    @Override
    public Codec<Integer> codec() {
        return Codec.INT.validate(value -> {
            int i = this.encodableMaxInclusive + 1;
            if (value.compareTo(this.minInclusive) >= 0 && value.compareTo(i) <= 0) {
                return DataResult.success((Object)value);
            }
            return DataResult.error(() -> "Value " + value + " outside of range [" + this.minInclusive + ":" + i + "]", (Object)value);
        });
    }

    @Override
    public boolean isCycling() {
        return true;
    }

    @Override
    public CyclingButtonWidget.Values<Integer> getValues() {
        return CyclingButtonWidget.Values.of(IntStream.range(this.minInclusive, this.maxInclusive() + 1).boxed().toList());
    }
}
