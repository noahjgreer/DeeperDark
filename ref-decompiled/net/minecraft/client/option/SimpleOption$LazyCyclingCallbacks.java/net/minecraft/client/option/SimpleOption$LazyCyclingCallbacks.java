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
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.option.SimpleOption;

@Environment(value=EnvType.CLIENT)
public record SimpleOption.LazyCyclingCallbacks<T>(Supplier<List<T>> values, Function<T, Optional<T>> validateValue, Codec<T> codec) implements SimpleOption.CyclingCallbacks<T>
{
    @Override
    public Optional<T> validate(T value) {
        return this.validateValue.apply(value);
    }

    @Override
    public CyclingButtonWidget.Values<T> getValues() {
        return CyclingButtonWidget.Values.of((Collection)this.values.get());
    }
}
