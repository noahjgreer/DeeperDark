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
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.world.PresetsScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;

@Environment(value=EnvType.CLIENT)
public class PresetsScreen.SuperflatPresetsListWidget.SuperflatPresetEntry
extends AlwaysSelectedEntryListWidget.Entry<PresetsScreen.SuperflatPresetsListWidget.SuperflatPresetEntry> {
    private static final Identifier STATS_ICONS_TEXTURE = Identifier.ofVanilla("textures/gui/container/stats_icons.png");
    private final FlatLevelGeneratorPreset preset;
    private final Text text;

    public PresetsScreen.SuperflatPresetsListWidget.SuperflatPresetEntry(RegistryEntry<FlatLevelGeneratorPreset> preset) {
        this.preset = preset.value();
        this.text = preset.getKey().map(key -> Text.translatable(key.getValue().toTranslationKey("flat_world_preset"))).orElse(UNKNOWN_PRESET_TEXT);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        this.renderIcon(context, this.getContentX(), this.getContentY(), this.preset.displayItem().value());
        context.drawTextWithShadow(SuperflatPresetsListWidget.this.field_18747.textRenderer, this.text, this.getContentX() + 18 + 5, this.getContentY() + 6, -1);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        this.setPreset();
        return super.mouseClicked(click, doubled);
    }

    void setPreset() {
        SuperflatPresetsListWidget.this.setSelected(this);
        SuperflatPresetsListWidget.this.field_18747.config = this.preset.settings();
        SuperflatPresetsListWidget.this.field_18747.customPresetField.setText(PresetsScreen.getGeneratorConfigString(SuperflatPresetsListWidget.this.field_18747.config));
        SuperflatPresetsListWidget.this.field_18747.customPresetField.setCursorToStart(false);
    }

    private void renderIcon(DrawContext context, int x, int y, Item iconItem) {
        this.drawIconBackground(context, x + 1, y + 1);
        context.drawItemWithoutEntity(new ItemStack(iconItem), x + 2, y + 2);
    }

    private void drawIconBackground(DrawContext context, int x, int y) {
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SLOT_TEXTURE, x, y, 18, 18);
    }

    @Override
    public Text getNarration() {
        return Text.translatable("narrator.select", this.text);
    }
}
