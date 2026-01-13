/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.Updatable
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.option.GameOptionsScreen
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.ElementListWidget
 *  net.minecraft.client.gui.widget.EntryListWidget$Entry
 *  net.minecraft.client.gui.widget.OptionListWidget
 *  net.minecraft.client.gui.widget.OptionListWidget$Component
 *  net.minecraft.client.gui.widget.OptionListWidget$Header
 *  net.minecraft.client.gui.widget.OptionListWidget$OptionAssociatedWidget
 *  net.minecraft.client.gui.widget.OptionListWidget$WidgetEntry
 *  net.minecraft.client.option.GameOptions
 *  net.minecraft.client.option.SimpleOption
 *  net.minecraft.client.option.SimpleOption$OptionSliderWidgetImpl
 *  net.minecraft.text.Text
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.widget;

import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Updatable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class OptionListWidget
extends ElementListWidget<Component> {
    private static final int field_49481 = 310;
    private static final int field_49482 = 25;
    private final GameOptionsScreen optionsScreen;

    public OptionListWidget(MinecraftClient client, int width, GameOptionsScreen optionsScreen) {
        super(client, width, optionsScreen.layout.getContentHeight(), optionsScreen.layout.getHeaderHeight(), 25);
        this.centerListVertically = false;
        this.optionsScreen = optionsScreen;
    }

    public void addSingleOptionEntry(SimpleOption<?> option) {
        this.addEntry((EntryListWidget.Entry)WidgetEntry.create((GameOptions)this.client.options, option, (Screen)this.optionsScreen));
    }

    public void addAll(SimpleOption<?> ... options) {
        for (int i = 0; i < options.length; i += 2) {
            SimpleOption<?> simpleOption = i < options.length - 1 ? options[i + 1] : null;
            this.addEntry((EntryListWidget.Entry)WidgetEntry.create((GameOptions)this.client.options, options[i], simpleOption, (GameOptionsScreen)this.optionsScreen));
        }
    }

    public void addAll(List<ClickableWidget> widgets) {
        for (int i = 0; i < widgets.size(); i += 2) {
            this.addWidgetEntry(widgets.get(i), i < widgets.size() - 1 ? widgets.get(i + 1) : null);
        }
    }

    public void addWidgetEntry(ClickableWidget firstWidget, @Nullable ClickableWidget secondWidget) {
        this.addEntry((EntryListWidget.Entry)WidgetEntry.create((ClickableWidget)firstWidget, (ClickableWidget)secondWidget, (Screen)this.optionsScreen));
    }

    public void addWidgetEntry(ClickableWidget firstWidget, SimpleOption<?> option, @Nullable ClickableWidget secondWidget) {
        this.addEntry((EntryListWidget.Entry)WidgetEntry.create((ClickableWidget)firstWidget, option, (ClickableWidget)secondWidget, (Screen)this.optionsScreen));
    }

    public void addHeader(Text title) {
        Objects.requireNonNull(this.client.textRenderer);
        int i = 9;
        int j = this.children().isEmpty() ? 0 : i * 2;
        this.addEntry((EntryListWidget.Entry)new Header((Screen)this.optionsScreen, title, j), j + i + 4);
    }

    public int getRowWidth() {
        return 310;
    }

    public @Nullable ClickableWidget getWidgetFor(SimpleOption<?> option) {
        for (Component component : this.children()) {
            WidgetEntry widgetEntry;
            ClickableWidget clickableWidget;
            if (!(component instanceof WidgetEntry) || (clickableWidget = (widgetEntry = (WidgetEntry)component).getWidgetFor(option)) == null) continue;
            return clickableWidget;
        }
        return null;
    }

    public void applyAllPendingValues() {
        for (Component component : this.children()) {
            if (!(component instanceof WidgetEntry)) continue;
            WidgetEntry widgetEntry = (WidgetEntry)component;
            for (OptionAssociatedWidget optionAssociatedWidget : widgetEntry.widgets) {
                ClickableWidget clickableWidget;
                if (optionAssociatedWidget.optionInstance() == null || !((clickableWidget = optionAssociatedWidget.widget()) instanceof SimpleOption.OptionSliderWidgetImpl)) continue;
                SimpleOption.OptionSliderWidgetImpl optionSliderWidgetImpl = (SimpleOption.OptionSliderWidgetImpl)clickableWidget;
                optionSliderWidgetImpl.applyPendingValue();
            }
        }
    }

    public void update(SimpleOption<?> simpleOption) {
        for (Component component : this.children()) {
            if (!(component instanceof WidgetEntry)) continue;
            WidgetEntry widgetEntry = (WidgetEntry)component;
            for (OptionAssociatedWidget optionAssociatedWidget : widgetEntry.widgets) {
                ClickableWidget clickableWidget;
                if (optionAssociatedWidget.optionInstance() != simpleOption || !((clickableWidget = optionAssociatedWidget.widget()) instanceof Updatable)) continue;
                Updatable updatable = (Updatable)clickableWidget;
                updatable.update();
                return;
            }
        }
    }
}

