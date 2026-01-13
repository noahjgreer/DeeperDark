/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.world;

import java.util.Set;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.world.PresetsScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.FlatLevelGeneratorPresetTags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
class PresetsScreen.SuperflatPresetsListWidget
extends AlwaysSelectedEntryListWidget<SuperflatPresetEntry> {
    public PresetsScreen.SuperflatPresetsListWidget(DynamicRegistryManager dynamicRegistryManager, FeatureSet featureSet) {
        super(PresetsScreen.this.client, PresetsScreen.this.width, PresetsScreen.this.height - 117, 80, 24);
        for (RegistryEntry<FlatLevelGeneratorPreset> registryEntry : dynamicRegistryManager.getOrThrow(RegistryKeys.FLAT_LEVEL_GENERATOR_PRESET).iterateEntries(FlatLevelGeneratorPresetTags.VISIBLE)) {
            Set set = registryEntry.value().settings().getLayers().stream().map(layer -> layer.getBlockState().getBlock()).filter(block -> !block.isEnabled(featureSet)).collect(Collectors.toSet());
            if (!set.isEmpty()) {
                LOGGER.info("Discarding flat world preset {} since it contains experimental blocks {}", (Object)registryEntry.getKey().map(key -> key.getValue().toString()).orElse("<unknown>"), set);
                continue;
            }
            this.addEntry(new SuperflatPresetEntry(registryEntry));
        }
    }

    @Override
    public void setSelected(@Nullable SuperflatPresetEntry superflatPresetEntry) {
        super.setSelected(superflatPresetEntry);
        PresetsScreen.this.updateSelectButton(superflatPresetEntry != null);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (super.keyPressed(input)) {
            return true;
        }
        if (input.isEnterOrSpace() && this.getSelectedOrNull() != null) {
            ((SuperflatPresetEntry)this.getSelectedOrNull()).setPreset();
        }
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    public class SuperflatPresetEntry
    extends AlwaysSelectedEntryListWidget.Entry<SuperflatPresetEntry> {
        private static final Identifier STATS_ICONS_TEXTURE = Identifier.ofVanilla("textures/gui/container/stats_icons.png");
        private final FlatLevelGeneratorPreset preset;
        private final Text text;

        public SuperflatPresetEntry(RegistryEntry<FlatLevelGeneratorPreset> preset) {
            this.preset = preset.value();
            this.text = preset.getKey().map(key -> Text.translatable(key.getValue().toTranslationKey("flat_world_preset"))).orElse(UNKNOWN_PRESET_TEXT);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            this.renderIcon(context, this.getContentX(), this.getContentY(), this.preset.displayItem().value());
            context.drawTextWithShadow(PresetsScreen.this.textRenderer, this.text, this.getContentX() + 18 + 5, this.getContentY() + 6, -1);
        }

        @Override
        public boolean mouseClicked(Click click, boolean doubled) {
            this.setPreset();
            return super.mouseClicked(click, doubled);
        }

        void setPreset() {
            SuperflatPresetsListWidget.this.setSelected(this);
            PresetsScreen.this.config = this.preset.settings();
            PresetsScreen.this.customPresetField.setText(PresetsScreen.getGeneratorConfigString(PresetsScreen.this.config));
            PresetsScreen.this.customPresetField.setCursorToStart(false);
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
}
