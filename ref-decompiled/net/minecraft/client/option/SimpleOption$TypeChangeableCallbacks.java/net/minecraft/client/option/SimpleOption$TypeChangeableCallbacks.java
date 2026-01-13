/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.option;

import java.util.function.Consumer;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;

@Environment(value=EnvType.CLIENT)
static interface SimpleOption.TypeChangeableCallbacks<T>
extends SimpleOption.CyclingCallbacks<T>,
SimpleOption.SliderCallbacks<T> {
    public boolean isCycling();

    @Override
    default public Function<SimpleOption<T>, ClickableWidget> getWidgetCreator(SimpleOption.TooltipFactory<T> tooltipFactory, GameOptions gameOptions, int x, int y, int width, Consumer<T> changeCallback) {
        if (this.isCycling()) {
            return SimpleOption.CyclingCallbacks.super.getWidgetCreator(tooltipFactory, gameOptions, x, y, width, changeCallback);
        }
        return SimpleOption.SliderCallbacks.super.getWidgetCreator(tooltipFactory, gameOptions, x, y, width, changeCallback);
    }
}
