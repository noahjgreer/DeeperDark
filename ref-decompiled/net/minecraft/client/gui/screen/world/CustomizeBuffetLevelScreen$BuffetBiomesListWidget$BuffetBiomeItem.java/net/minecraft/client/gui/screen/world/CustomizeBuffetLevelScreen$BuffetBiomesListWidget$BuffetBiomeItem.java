/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.world;

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

@Environment(value=EnvType.CLIENT)
class CustomizeBuffetLevelScreen.BuffetBiomesListWidget.BuffetBiomeItem
extends AlwaysSelectedEntryListWidget.Entry<CustomizeBuffetLevelScreen.BuffetBiomesListWidget.BuffetBiomeItem> {
    final RegistryEntry.Reference<Biome> biome;
    final Text text;

    public CustomizeBuffetLevelScreen.BuffetBiomesListWidget.BuffetBiomeItem(RegistryEntry.Reference<Biome> biome) {
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
        context.drawTextWithShadow(BuffetBiomesListWidget.this.screen.textRenderer, this.text, this.getContentX() + 5, this.getContentY() + 2, -1);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        BuffetBiomesListWidget.this.setSelected(this);
        return super.mouseClicked(click, doubled);
    }
}
