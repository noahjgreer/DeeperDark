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
import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.option.SimpleOption;

@Environment(value=EnvType.CLIENT)
public record SimpleOption.AlternateValuesSupportingCyclingCallbacks<T>(List<T> values, List<T> altValues, BooleanSupplier altCondition, SimpleOption.CyclingCallbacks.ValueSetter<T> valueSetter, Codec<T> codec) implements SimpleOption.CyclingCallbacks<T>
{
    @Override
    public CyclingButtonWidget.Values<T> getValues() {
        return CyclingButtonWidget.Values.of(this.altCondition, this.values, this.altValues);
    }

    @Override
    public Optional<T> validate(T value) {
        return (this.altCondition.getAsBoolean() ? this.altValues : this.values).contains(value) ? Optional.of(value) : Optional.empty();
    }
}
