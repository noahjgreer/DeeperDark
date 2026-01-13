/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.WarningScreen
 *  net.minecraft.client.gui.widget.CheckboxWidget
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.LayoutWidget
 *  net.minecraft.client.gui.widget.ScrollableTextWidget
 *  net.minecraft.client.gui.widget.SimplePositioningWidget
 *  net.minecraft.client.gui.widget.TextWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.text.Text
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.ScrollableTextWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class WarningScreen
extends Screen {
    private static final int field_49538 = 100;
    private final Text messageText;
    private final @Nullable Text checkMessage;
    private final Text narratedText;
    protected @Nullable CheckboxWidget checkbox;
    private @Nullable ScrollableTextWidget textWidget;
    private final SimplePositioningWidget positioningWidget;

    protected WarningScreen(Text header, Text message, Text narratedText) {
        this(header, message, null, narratedText);
    }

    protected WarningScreen(Text header, Text messageText, @Nullable Text checkMessage, Text narratedText) {
        super(header);
        this.messageText = messageText;
        this.checkMessage = checkMessage;
        this.narratedText = narratedText;
        this.positioningWidget = new SimplePositioningWidget(0, 0, this.width, this.height);
    }

    protected abstract LayoutWidget getLayout();

    protected void init() {
        DirectionalLayoutWidget directionalLayoutWidget = (DirectionalLayoutWidget)this.positioningWidget.add((Widget)DirectionalLayoutWidget.vertical().spacing(8));
        directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
        directionalLayoutWidget.add((Widget)new TextWidget(this.getTitle(), this.textRenderer));
        this.textWidget = (ScrollableTextWidget)directionalLayoutWidget.add((Widget)new ScrollableTextWidget(0, 0, this.width - 100, this.height - 100, this.messageText, this.textRenderer), positioner -> positioner.margin(12));
        DirectionalLayoutWidget directionalLayoutWidget2 = (DirectionalLayoutWidget)directionalLayoutWidget.add((Widget)DirectionalLayoutWidget.vertical().spacing(8));
        directionalLayoutWidget2.getMainPositioner().alignHorizontalCenter();
        if (this.checkMessage != null) {
            this.checkbox = (CheckboxWidget)directionalLayoutWidget2.add((Widget)CheckboxWidget.builder((Text)this.checkMessage, (TextRenderer)this.textRenderer).build());
        }
        directionalLayoutWidget2.add((Widget)this.getLayout());
        this.positioningWidget.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
    }

    protected void refreshWidgetPositions() {
        if (this.textWidget != null) {
            this.textWidget.setWidth(this.width - 100);
            this.textWidget.setHeight(this.height - 100);
            this.textWidget.updateHeight();
        }
        this.positioningWidget.refreshPositions();
        SimplePositioningWidget.setPos((Widget)this.positioningWidget, (ScreenRect)this.getNavigationFocus());
    }

    public Text getNarratedTitle() {
        return this.narratedText;
    }
}

