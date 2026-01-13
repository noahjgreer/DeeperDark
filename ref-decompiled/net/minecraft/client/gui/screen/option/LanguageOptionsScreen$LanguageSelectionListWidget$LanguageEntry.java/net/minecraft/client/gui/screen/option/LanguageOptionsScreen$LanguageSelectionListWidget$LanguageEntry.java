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
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry
extends AlwaysSelectedEntryListWidget.Entry<LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry> {
    final String languageCode;
    private final Text languageDefinition;

    public LanguageOptionsScreen.LanguageSelectionListWidget.LanguageEntry(String languageCode, LanguageDefinition languageDefinition) {
        this.languageCode = languageCode;
        this.languageDefinition = languageDefinition.getDisplayText();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        context.drawCenteredTextWithShadow(LanguageSelectionListWidget.this.field_18744.textRenderer, this.languageDefinition, LanguageSelectionListWidget.this.width / 2, this.getContentMiddleY() - ((LanguageOptionsScreen)LanguageSelectionListWidget.this.field_18744).textRenderer.fontHeight / 2, -1);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.isEnterOrSpace()) {
            this.onPressed();
            LanguageSelectionListWidget.this.field_18744.onDone();
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        this.onPressed();
        if (doubled) {
            LanguageSelectionListWidget.this.field_18744.onDone();
        }
        return super.mouseClicked(click, doubled);
    }

    private void onPressed() {
        LanguageSelectionListWidget.this.setSelected(this);
    }

    @Override
    public Text getNarration() {
        return Text.translatable("narrator.select", this.languageDefinition);
    }
}
