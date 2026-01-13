/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms.gui.screen;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.IconWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.realms.dto.RealmsNotification;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
class RealmsMainScreen.VisitUrlNotification
extends RealmsMainScreen.Entry {
    private static final int field_43002 = 40;
    public static final int field_62554 = 7;
    public static final int field_62082 = 38;
    private final Text message;
    private final List<ClickableWidget> gridChildren;
    private final @Nullable RealmsMainScreen.CrossButton dismissButton;
    private final MultilineTextWidget textWidget;
    private final GridWidget grid;
    private final SimplePositioningWidget textGrid;
    private final ButtonWidget urlButton;
    private int width;

    public RealmsMainScreen.VisitUrlNotification(RealmsMainScreen parent, int lines, Text message, RealmsNotification.VisitUrl url) {
        super(RealmsMainScreen.this);
        this.gridChildren = new ArrayList<ClickableWidget>();
        this.width = -1;
        this.message = message;
        this.grid = new GridWidget();
        this.grid.add(IconWidget.create(20, 20, INFO_ICON_TEXTURE), 0, 0, this.grid.copyPositioner().margin(7, 7, 0, 0));
        this.grid.add(EmptyWidget.ofWidth(40), 0, 0);
        this.textGrid = this.grid.add(new SimplePositioningWidget(0, lines), 0, 1, this.grid.copyPositioner().marginTop(7));
        this.textWidget = this.textGrid.add(new MultilineTextWidget(message, RealmsMainScreen.this.textRenderer).setCentered(true), this.textGrid.copyPositioner().alignHorizontalCenter().alignTop());
        this.grid.add(EmptyWidget.ofWidth(40), 0, 2);
        this.dismissButton = url.isDismissable() ? this.grid.add(new RealmsMainScreen.CrossButton(button -> RealmsMainScreen.this.dismissNotification(url.getUuid()), Text.translatable("mco.notification.dismiss")), 0, 2, this.grid.copyPositioner().alignRight().margin(0, 7, 7, 0)) : null;
        this.urlButton = this.grid.add(url.createButton(parent), 1, 1, this.grid.copyPositioner().alignHorizontalCenter().margin(4));
        this.urlButton.setFocusOverride(() -> this.isFocused());
        this.grid.forEachChild(this.gridChildren::add);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (this.urlButton.keyPressed(input)) {
            return true;
        }
        if (this.dismissButton != null && this.dismissButton.keyPressed(input)) {
            return true;
        }
        return super.keyPressed(input);
    }

    private void setWidth() {
        int i = this.getWidth();
        if (this.width != i) {
            this.updateWidth(i);
            this.width = i;
        }
    }

    private void updateWidth(int width) {
        int i = RealmsMainScreen.VisitUrlNotification.getTextWidth(width);
        this.textGrid.setMinWidth(i);
        this.textWidget.setMaxWidth(i);
        this.grid.refreshPositions();
    }

    public static int getTextWidth(int width) {
        return width - 80;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        this.grid.setPosition(this.getContentX(), this.getContentY());
        this.setWidth();
        this.gridChildren.forEach(child -> child.render(context, mouseX, mouseY, deltaTicks));
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (this.dismissButton != null && this.dismissButton.mouseClicked(click, doubled)) {
            return true;
        }
        if (this.urlButton.mouseClicked(click, doubled)) {
            return true;
        }
        return super.mouseClicked(click, doubled);
    }

    public Text getMessage() {
        return this.message;
    }

    @Override
    public Text getNarration() {
        return this.getMessage();
    }
}
