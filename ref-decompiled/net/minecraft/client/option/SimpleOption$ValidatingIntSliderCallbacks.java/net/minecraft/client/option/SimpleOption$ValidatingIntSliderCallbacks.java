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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.SimpleOption;

@Environment(value=EnvType.CLIENT)
public record SimpleOption.ValidatingIntSliderCallbacks(int minInclusive, int maxInclusive, boolean applyValueImmediately) implements SimpleOption.IntSliderCallbacks
{
    public SimpleOption.ValidatingIntSliderCallbacks(int minInclusive, int maxInclusive) {
        this(minInclusive, maxInclusive, true);
    }

    @Override
    public Optional<Integer> validate(Integer integer) {
        return integer.compareTo(this.minInclusive()) >= 0 && integer.compareTo(this.maxInclusive()) <= 0 ? Optional.of(integer) : Optional.empty();
    }

    @Override
    public Codec<Integer> codec() {
        return Codec.intRange((int)this.minInclusive, (int)(this.maxInclusive + 1));
    }
}
