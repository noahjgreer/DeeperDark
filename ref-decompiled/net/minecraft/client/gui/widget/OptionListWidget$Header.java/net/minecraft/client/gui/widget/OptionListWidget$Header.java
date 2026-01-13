/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
protected static class OptionListWidget.Header
extends OptionListWidget.Component {
    private final Screen parent;
    private final int yOffset;
    private final TextWidget title;

    protected OptionListWidget.Header(Screen parent, Text title, int yOffset) {
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
