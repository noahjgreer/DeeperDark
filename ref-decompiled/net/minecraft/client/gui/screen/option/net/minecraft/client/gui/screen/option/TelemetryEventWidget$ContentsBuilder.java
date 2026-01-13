/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.option.TelemetryEventWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
static class TelemetryEventWidget.ContentsBuilder {
    private final int gridWidth;
    private final DirectionalLayoutWidget layout;
    private final MutableText narration = Text.empty();

    public TelemetryEventWidget.ContentsBuilder(int gridWidth) {
        this.gridWidth = gridWidth;
        this.layout = DirectionalLayoutWidget.vertical();
        this.layout.getMainPositioner().alignLeft();
        this.layout.add(EmptyWidget.ofWidth(gridWidth));
    }

    public void appendTitle(TextRenderer textRenderer, Text title) {
        this.appendTitle(textRenderer, title, 0);
    }

    public void appendTitle(TextRenderer textRenderer, Text title, int marginBottom) {
        this.layout.add(new MultilineTextWidget(title, textRenderer).setMaxWidth(this.gridWidth), positioner -> positioner.marginBottom(marginBottom));
        this.narration.append(title).append("\n");
    }

    public void appendText(TextRenderer textRenderer, Text text) {
        this.layout.add(new MultilineTextWidget(text, textRenderer).setMaxWidth(this.gridWidth - 64).setCentered(true), positioner -> positioner.alignHorizontalCenter().marginX(32));
        this.narration.append(text).append("\n");
    }

    public void appendSpace(int height) {
        this.layout.add(EmptyWidget.ofHeight(height));
    }

    public TelemetryEventWidget.Contents build() {
        this.layout.refreshPositions();
        return new TelemetryEventWidget.Contents(this.layout, this.narration);
    }
}
