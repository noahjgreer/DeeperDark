/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ibm.icu.text.Collator
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.world;

import com.ibm.icu.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.world.biome.Biome;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
class CustomizeBuffetLevelScreen.BuffetBiomesListWidget
extends AlwaysSelectedEntryListWidget<BuffetBiomeItem> {
    CustomizeBuffetLevelScreen.BuffetBiomesListWidget() {
        super(CustomizeBuffetLevelScreen.this.client, CustomizeBuffetLevelScreen.this.width, CustomizeBuffetLevelScreen.this.layout.getContentHeight(), CustomizeBuffetLevelScreen.this.layout.getHeaderHeight(), 15);
        this.onSearch("");
    }

    private void onSearch(String search) {
        Collator collator = Collator.getInstance((Locale)Locale.getDefault());
        String string = search.toLowerCase(Locale.ROOT);
        List<BuffetBiomeItem> list = CustomizeBuffetLevelScreen.this.biomeRegistry.streamEntries().map(entry -> new BuffetBiomeItem((RegistryEntry.Reference<Biome>)entry)).sorted(Comparator.comparing(biome -> biome.text.getString(), collator)).filter(biome -> search.isEmpty() || biome.text.getString().toLowerCase(Locale.ROOT).contains(string)).toList();
        this.replaceEntries(list);
        this.refreshScroll();
    }

    @Override
    public void setSelected(@Nullable BuffetBiomeItem buffetBiomeItem) {
        super.setSelected(buffetBiomeItem);
        if (buffetBiomeItem != null) {
            CustomizeBuffetLevelScreen.this.biome = buffetBiomeItem.biome;
        }
        CustomizeBuffetLevelScreen.this.refreshConfirmButton();
    }

    @Environment(value=EnvType.CLIENT)
    class BuffetBiomeItem
    extends AlwaysSelectedEntryListWidget.Entry<BuffetBiomeItem> {
        final RegistryEntry.Reference<Biome> biome;
        final Text text;

        public BuffetBiomeItem(RegistryEntry.Reference<Biome> biome) {
            this.biome = biome;
            Identifier identifier = biome.registryKey().getValue();
            String string = identifier.toTranslationKey("biome");
            this.text = Language.getInstance().hasTranslation(string) ? Text.translatable(string) : Text.literal(identifier.toString());
        }

        @Override
        public Text getNarration() {
            return Text.translatable("narrator.select", this.text);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            context.drawTextWithShadow(CustomizeBuffetLevelScreen.this.textRenderer, this.text, this.getContentX() + 5, this.getContentY() + 2, -1);
        }

        @Override
        public boolean mouseClicked(Click click, boolean doubled) {
            BuffetBiomesListWidget.this.setSelected(this);
            return super.mouseClicked(click, doubled);
        }
    }
}
