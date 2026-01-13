/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.report;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.report.ChatSelectionScreen;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class ChatSelectionScreen.SelectionListWidget.TextEntry
extends ChatSelectionScreen.SelectionListWidget.Entry {
    private final Text text;

    public ChatSelectionScreen.SelectionListWidget.TextEntry(Text text) {
        this.text = text;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        int i = this.getContentMiddleY();
        int j = this.getContentRightEnd() - 8;
        int k = SelectionListWidget.this.field_39592.textRenderer.getWidth(this.text);
        int l = (this.getContentX() + j - k) / 2;
        int m = i - ((ChatSelectionScreen)SelectionListWidget.this.field_39592).textRenderer.fontHeight / 2;
        context.drawTextWithShadow(SelectionListWidget.this.field_39592.textRenderer, this.text, l, m, -6250336);
    }

    @Override
    public Text getNarration() {
        return this.text;
    }
}
