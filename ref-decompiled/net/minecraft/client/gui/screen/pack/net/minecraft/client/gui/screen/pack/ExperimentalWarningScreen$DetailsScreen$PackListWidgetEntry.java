/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.pack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Alignment;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.pack.ExperimentalWarningScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
class ExperimentalWarningScreen.DetailsScreen.PackListWidgetEntry
extends AlwaysSelectedEntryListWidget.Entry<ExperimentalWarningScreen.DetailsScreen.PackListWidgetEntry> {
    private final Text displayName;
    private final Text details;
    private final MultilineText multilineDetails;

    ExperimentalWarningScreen.DetailsScreen.PackListWidgetEntry(Text displayName, Text details, MultilineText multilineDetails) {
        this.displayName = displayName;
        this.details = details;
        this.multilineDetails = multilineDetails;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        DrawnTextConsumer drawnTextConsumer = context.getTextConsumer();
        context.drawTextWithShadow(((ExperimentalWarningScreen.DetailsScreen)DetailsScreen.this).client.textRenderer, this.displayName, this.getContentX(), this.getContentY(), -1);
        this.multilineDetails.draw(Alignment.LEFT, this.getContentX(), this.getContentY() + 12, ((ExperimentalWarningScreen.DetailsScreen)DetailsScreen.this).textRenderer.fontHeight, drawnTextConsumer);
    }

    @Override
    public Text getNarration() {
        return Text.translatable("narrator.select", ScreenTexts.joinSentences(this.displayName, this.details));
    }
}
