/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.widget;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
protected static class OptionListWidget.WidgetEntry
extends OptionListWidget.Component {
    final List<OptionListWidget.OptionAssociatedWidget> widgets;
    private final Screen screen;
    private static final int WIDGET_X_SPACING = 160;

    private OptionListWidget.WidgetEntry(List<OptionListWidget.OptionAssociatedWidget> widgets, Screen screen) {
        this.widgets = widgets;
        this.screen = screen;
    }

    public static OptionListWidget.WidgetEntry create(GameOptions options, SimpleOption<?> option, Screen screen) {
        return new OptionListWidget.WidgetEntry(List.of(new OptionListWidget.OptionAssociatedWidget(option.createWidget(options, 0, 0, 310), option)), screen);
    }

    public static OptionListWidget.WidgetEntry create(ClickableWidget firstWidget, @Nullable ClickableWidget secondWidget, Screen screen) {
        if (secondWidget == null) {
            return new OptionListWidget.WidgetEntry(List.of(new OptionListWidget.OptionAssociatedWidget(firstWidget)), screen);
        }
        return new OptionListWidget.WidgetEntry(List.of(new OptionListWidget.OptionAssociatedWidget(firstWidget), new OptionListWidget.OptionAssociatedWidget(secondWidget)), screen);
    }

    public static OptionListWidget.WidgetEntry create(ClickableWidget firstWidget, SimpleOption<?> option, @Nullable ClickableWidget secondWidget, Screen screen) {
        if (secondWidget == null) {
            return new OptionListWidget.WidgetEntry(List.of(new OptionListWidget.OptionAssociatedWidget(firstWidget, option)), screen);
        }
        return new OptionListWidget.WidgetEntry(List.of(new OptionListWidget.OptionAssociatedWidget(firstWidget, option), new OptionListWidget.OptionAssociatedWidget(secondWidget)), screen);
    }

    public static OptionListWidget.WidgetEntry create(GameOptions options, SimpleOption<?> firstOption, @Nullable SimpleOption<?> secondOption, GameOptionsScreen screen) {
        ClickableWidget clickableWidget = firstOption.createWidget(options);
        if (secondOption == null) {
            return new OptionListWidget.WidgetEntry(List.of(new OptionListWidget.OptionAssociatedWidget(clickableWidget, firstOption)), screen);
        }
        return new OptionListWidget.WidgetEntry(List.of(new OptionListWidget.OptionAssociatedWidget(clickableWidget, firstOption), new OptionListWidget.OptionAssociatedWidget(secondOption.createWidget(options), secondOption)), screen);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        int i = 0;
        int j = this.screen.width / 2 - 155;
        for (OptionListWidget.OptionAssociatedWidget optionAssociatedWidget : this.widgets) {
            optionAssociatedWidget.widget().setPosition(j + i, this.getContentY());
            optionAssociatedWidget.widget().render(context, mouseX, mouseY, deltaTicks);
            i += 160;
        }
    }

    @Override
    public List<? extends Element> children() {
        return Lists.transform(this.widgets, OptionListWidget.OptionAssociatedWidget::widget);
    }

    @Override
    public List<? extends Selectable> selectableChildren() {
        return Lists.transform(this.widgets, OptionListWidget.OptionAssociatedWidget::widget);
    }

    public @Nullable ClickableWidget getWidgetFor(SimpleOption<?> option) {
        for (OptionListWidget.OptionAssociatedWidget optionAssociatedWidget : this.widgets) {
            if (optionAssociatedWidget.optionInstance != option) continue;
            return optionAssociatedWidget.widget();
        }
        return null;
    }
}
