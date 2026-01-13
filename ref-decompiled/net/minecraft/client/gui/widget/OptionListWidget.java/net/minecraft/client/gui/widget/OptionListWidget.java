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
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.Updatable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

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
        this.addEntry(WidgetEntry.create(this.client.options, option, (Screen)this.optionsScreen));
    }

    public void addAll(SimpleOption<?> ... options) {
        for (int i = 0; i < options.length; i += 2) {
            SimpleOption<?> simpleOption = i < options.length - 1 ? options[i + 1] : null;
            this.addEntry(WidgetEntry.create(this.client.options, options[i], simpleOption, this.optionsScreen));
        }
    }

    public void addAll(List<ClickableWidget> widgets) {
        for (int i = 0; i < widgets.size(); i += 2) {
            this.addWidgetEntry(widgets.get(i), i < widgets.size() - 1 ? widgets.get(i + 1) : null);
        }
    }

    public void addWidgetEntry(ClickableWidget firstWidget, @Nullable ClickableWidget secondWidget) {
        this.addEntry(WidgetEntry.create(firstWidget, secondWidget, (Screen)this.optionsScreen));
    }

    public void addWidgetEntry(ClickableWidget firstWidget, SimpleOption<?> option, @Nullable ClickableWidget secondWidget) {
        this.addEntry(WidgetEntry.create(firstWidget, option, secondWidget, (Screen)this.optionsScreen));
    }

    public void addHeader(Text title) {
        int i = this.client.textRenderer.fontHeight;
        int j = this.children().isEmpty() ? 0 : i * 2;
        this.addEntry(new Header(this.optionsScreen, title, j), j + i + 4);
    }

    @Override
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
                Updatable updatable = (Updatable)((Object)clickableWidget);
                updatable.update();
                return;
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static class WidgetEntry
    extends Component {
        final List<OptionAssociatedWidget> widgets;
        private final Screen screen;
        private static final int WIDGET_X_SPACING = 160;

        private WidgetEntry(List<OptionAssociatedWidget> widgets, Screen screen) {
            this.widgets = widgets;
            this.screen = screen;
        }

        public static WidgetEntry create(GameOptions options, SimpleOption<?> option, Screen screen) {
            return new WidgetEntry(List.of(new OptionAssociatedWidget(option.createWidget(options, 0, 0, 310), option)), screen);
        }

        public static WidgetEntry create(ClickableWidget firstWidget, @Nullable ClickableWidget secondWidget, Screen screen) {
            if (secondWidget == null) {
                return new WidgetEntry(List.of(new OptionAssociatedWidget(firstWidget)), screen);
            }
            return new WidgetEntry(List.of(new OptionAssociatedWidget(firstWidget), new OptionAssociatedWidget(secondWidget)), screen);
        }

        public static WidgetEntry create(ClickableWidget firstWidget, SimpleOption<?> option, @Nullable ClickableWidget secondWidget, Screen screen) {
            if (secondWidget == null) {
                return new WidgetEntry(List.of(new OptionAssociatedWidget(firstWidget, option)), screen);
            }
            return new WidgetEntry(List.of(new OptionAssociatedWidget(firstWidget, option), new OptionAssociatedWidget(secondWidget)), screen);
        }

        public static WidgetEntry create(GameOptions options, SimpleOption<?> firstOption, @Nullable SimpleOption<?> secondOption, GameOptionsScreen screen) {
            ClickableWidget clickableWidget = firstOption.createWidget(options);
            if (secondOption == null) {
                return new WidgetEntry(List.of(new OptionAssociatedWidget(clickableWidget, firstOption)), screen);
            }
            return new WidgetEntry(List.of(new OptionAssociatedWidget(clickableWidget, firstOption), new OptionAssociatedWidget(secondOption.createWidget(options), secondOption)), screen);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            int i = 0;
            int j = this.screen.width / 2 - 155;
            for (OptionAssociatedWidget optionAssociatedWidget : this.widgets) {
                optionAssociatedWidget.widget().setPosition(j + i, this.getContentY());
                optionAssociatedWidget.widget().render(context, mouseX, mouseY, deltaTicks);
                i += 160;
            }
        }

        @Override
        public List<? extends Element> children() {
            return Lists.transform(this.widgets, OptionAssociatedWidget::widget);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return Lists.transform(this.widgets, OptionAssociatedWidget::widget);
        }

        public @Nullable ClickableWidget getWidgetFor(SimpleOption<?> option) {
            for (OptionAssociatedWidget optionAssociatedWidget : this.widgets) {
                if (optionAssociatedWidget.optionInstance != option) continue;
                return optionAssociatedWidget.widget();
            }
            return null;
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static class Header
    extends Component {
        private final Screen parent;
        private final int yOffset;
        private final TextWidget title;

        protected Header(Screen parent, Text title, int yOffset) {
            this.parent = parent;
            this.yOffset = yOffset;
            this.title = new TextWidget(title, parent.getTextRenderer());
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of(this.title);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            this.title.setPosition(this.parent.width / 2 - 155, this.getContentY() + this.yOffset);
            this.title.render(context, mouseX, mouseY, deltaTicks);
        }

        @Override
        public List<? extends Element> children() {
            return List.of(this.title);
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static abstract class Component
    extends ElementListWidget.Entry<Component> {
        protected Component() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class OptionAssociatedWidget
    extends Record {
        private final ClickableWidget widget;
        final @Nullable SimpleOption<?> optionInstance;

        public OptionAssociatedWidget(ClickableWidget widget) {
            this(widget, null);
        }

        public OptionAssociatedWidget(ClickableWidget widget, @Nullable SimpleOption<?> optionInstance) {
            this.widget = widget;
            this.optionInstance = optionInstance;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{OptionAssociatedWidget.class, "widget;optionInstance", "widget", "optionInstance"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{OptionAssociatedWidget.class, "widget;optionInstance", "widget", "optionInstance"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{OptionAssociatedWidget.class, "widget;optionInstance", "widget", "optionInstance"}, this, object);
        }

        public ClickableWidget widget() {
            return this.widget;
        }

        public @Nullable SimpleOption<?> optionInstance() {
            return this.optionInstance;
        }
    }
}
