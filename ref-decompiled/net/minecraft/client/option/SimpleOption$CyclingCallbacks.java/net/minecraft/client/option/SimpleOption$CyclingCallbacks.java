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
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;

@Environment(value=EnvType.CLIENT)
static interface SimpleOption.CyclingCallbacks<T>
extends SimpleOption.Callbacks<T> {
    public CyclingButtonWidget.Values<T> getValues();

    default public ValueSetter<T> valueSetter() {
        return SimpleOption::setValue;
    }

    @Override
    default public Function<SimpleOption<T>, ClickableWidget> getWidgetCreator(SimpleOption.TooltipFactory<T> tooltipFactory, GameOptions gameOptions, int x, int y, int width, Consumer<T> changeCallback) {
        return option -> CyclingButtonWidget.builder(option.textGetter, option::getValue).values(this.getValues()).tooltip(tooltipFactory).build(x, y, width, 20, option.text, (button, value) -> {
            this.valueSetter().set((SimpleOption<Object>)option, value);
            gameOptions.write();
            changeCallback.accept(value);
        });
    }

    @Environment(value=EnvType.CLIENT)
    public static interface ValueSetter<T> {
        public void set(SimpleOption<T> var1, T var2);
    }
}
