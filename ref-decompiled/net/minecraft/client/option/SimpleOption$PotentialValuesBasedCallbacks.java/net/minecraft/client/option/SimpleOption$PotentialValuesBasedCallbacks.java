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
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.option.SimpleOption;

@Environment(value=EnvType.CLIENT)
public record SimpleOption.PotentialValuesBasedCallbacks<T>(List<T> values, Codec<T> codec) implements SimpleOption.CyclingCallbacks<T>
{
    @Override
    public Optional<T> validate(T value) {
        return this.values.contains(value) ? Optional.of(value) : Optional.empty();
    }

    @Override
    public CyclingButtonWidget.Values<T> getValues() {
        return CyclingButtonWidget.Values.of(this.values);
    }
}
