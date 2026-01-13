/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.option;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;

@Environment(value=EnvType.CLIENT)
static interface SimpleOption.SliderCallbacks<T>
extends SimpleOption.Callbacks<T> {
    public double toSliderProgress(T var1);

    default public Optional<T> getNext(T value) {
        return Optional.empty();
    }

    default public Optional<T> getPrevious(T value) {
        return Optional.empty();
    }

    public T toValue(double var1);

    default public boolean applyValueImmediately() {
        return true;
    }

    @Override
    default public Function<SimpleOption<T>, ClickableWidget> getWidgetCreator(SimpleOption.TooltipFactory<T> tooltipFactory, GameOptions gameOptions, int x, int y, int width, Consumer<T> changeCallback) {
        return option -> new SimpleOption.OptionSliderWidgetImpl(gameOptions, x, y, width, 20, option, this, tooltipFactory, changeCallback, this.applyValueImmediately());
    }
}
