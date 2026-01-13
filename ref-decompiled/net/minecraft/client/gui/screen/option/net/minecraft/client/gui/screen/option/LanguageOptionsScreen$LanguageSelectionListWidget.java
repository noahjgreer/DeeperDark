/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.option;

import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
class LanguageOptionsScreen.LanguageSelectionListWidget
extends AlwaysSelectedEntryListWidget<LanguageEntry> {
    public LanguageOptionsScreen.LanguageSelectionListWidget(MinecraftClient client) {
        super(client, LanguageOptionsScreen.this.width, LanguageOptionsScreen.this.height - 33 - 53, 33, 18);
        String string = LanguageOptionsScreen.this.languageManager.getLanguage();
        LanguageOptionsScreen.this.languageManager.getAllLanguages().forEach((languageCode, languageDefinition) -> {
            LanguageEntry languageEntry = new LanguageEntry((String)languageCode, (LanguageDefinition)languageDefinition);
            this.addEntry(languageEntry);
            if (string.equals(languageCode)) {
                this.setSelected(languageEntry);
            }
        });
        if (this.getSelectedOrNull() != null) {
            this.centerScrollOn((LanguageEntry)this.getSelectedOrNull());
        }
    }

    void setSearch(String search) {
        SortedMap<String, LanguageDefinition> sortedMap = LanguageOptionsScreen.this.languageManager.getAllLanguages();
        List<LanguageEntry> list = sortedMap.entrySet().stream().filter(entry -> search.isEmpty() || ((LanguageDefinition)entry.getValue()).name().toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT)) || ((LanguageDefinition)entry.getValue()).region().toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT))).map(entry -> new LanguageEntry((String)entry.getKey(), (LanguageDefinition)entry.getValue())).toList();
        this.replaceEntries(list);
        this.refreshScroll();
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 50;
    }

    @Environment(value=EnvType.CLIENT)
    public class LanguageEntry
    extends AlwaysSelectedEntryListWidget.Entry<LanguageEntry> {
        final String languageCode;
        private final Text languageDefinition;

        public LanguageEntry(String languageCode, LanguageDefinition languageDefinition) {
            this.languageCode = languageCode;
            this.languageDefinition = languageDefinition.getDisplayText();
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            context.drawCenteredTextWithShadow(LanguageOptionsScreen.this.textRenderer, this.languageDefinition, LanguageSelectionListWidget.this.width / 2, this.getContentMiddleY() - ((LanguageOptionsScreen)LanguageOptionsScreen.this).textRenderer.fontHeight / 2, -1);
        }

        @Override
        public boolean keyPressed(KeyInput input) {
            if (input.isEnterOrSpace()) {
                this.onPressed();
                LanguageOptionsScreen.this.onDone();
                return true;
            }
            return super.keyPressed(input);
        }

        @Override
        public boolean mouseClicked(Click click, boolean doubled) {
            this.onPressed();
            if (doubled) {
                LanguageOptionsScreen.this.onDone();
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
}
