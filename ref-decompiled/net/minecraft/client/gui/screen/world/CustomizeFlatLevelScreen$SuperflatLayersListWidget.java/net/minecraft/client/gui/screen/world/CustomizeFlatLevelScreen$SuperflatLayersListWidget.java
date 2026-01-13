/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.world;

import java.util.List;
import java.util.SequencedCollection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
class CustomizeFlatLevelScreen.SuperflatLayersListWidget
extends AlwaysSelectedEntryListWidget<Entry> {
    static final Text LAYER_MATERIAL_TEXT = Text.translatable("createWorld.customize.flat.tile").formatted(Formatting.UNDERLINE);
    static final Text HEIGHT_TEXT = Text.translatable("createWorld.customize.flat.height").formatted(Formatting.UNDERLINE);

    public CustomizeFlatLevelScreen.SuperflatLayersListWidget() {
        super(CustomizeFlatLevelScreen.this.client, CustomizeFlatLevelScreen.this.width, CustomizeFlatLevelScreen.this.height - 103, 43, 24);
        this.refreshLayers();
    }

    private void refreshLayers() {
        this.addEntry(new HeaderEntry(CustomizeFlatLevelScreen.this.textRenderer), (int)((double)CustomizeFlatLevelScreen.this.textRenderer.fontHeight * 1.5));
        SequencedCollection list = CustomizeFlatLevelScreen.this.config.getLayers().reversed();
        for (int i = 0; i < list.size(); ++i) {
            this.addEntry(new SuperflatLayerEntry((FlatChunkGeneratorLayer)list.get(i), i));
        }
    }

    @Override
    public void setSelected(@Nullable Entry entry) {
        super.setSelected(entry);
        CustomizeFlatLevelScreen.this.updateRemoveLayerButton();
    }

    public void updateLayers() {
        int i = this.children().indexOf(this.getSelectedOrNull());
        this.clearEntries();
        this.refreshLayers();
        List list = this.children();
        if (i >= 0 && i < list.size()) {
            this.setSelected((Entry)list.get(i));
        }
    }

    void removeLayer(SuperflatLayerEntry layer) {
        List<FlatChunkGeneratorLayer> list = CustomizeFlatLevelScreen.this.config.getLayers();
        int i = this.children().indexOf(layer);
        this.removeEntry(layer);
        list.remove(layer.layer);
        this.setSelected(list.isEmpty() ? null : (Entry)this.children().get(Math.min(i, list.size())));
        CustomizeFlatLevelScreen.this.config.updateLayerBlocks();
        this.updateLayers();
        CustomizeFlatLevelScreen.this.updateRemoveLayerButton();
    }

    @Environment(value=EnvType.CLIENT)
    static class HeaderEntry
    extends Entry {
        private final TextRenderer textRenderer;

        public HeaderEntry(TextRenderer textRenderer) {
            this.textRenderer = textRenderer;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            context.drawTextWithShadow(this.textRenderer, LAYER_MATERIAL_TEXT, this.getContentX(), this.getContentY(), -1);
            context.drawTextWithShadow(this.textRenderer, HEIGHT_TEXT, this.getContentRightEnd() - this.textRenderer.getWidth(HEIGHT_TEXT), this.getContentY(), -1);
        }

        @Override
        public Text getNarration() {
            return ScreenTexts.joinSentences(LAYER_MATERIAL_TEXT, HEIGHT_TEXT);
        }
    }

    @Environment(value=EnvType.CLIENT)
    class SuperflatLayerEntry
    extends Entry {
        final FlatChunkGeneratorLayer layer;
        private final int index;

        public SuperflatLayerEntry(FlatChunkGeneratorLayer layer, int index) {
            this.layer = layer;
            this.index = index;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            BlockState blockState = this.layer.getBlockState();
            ItemStack itemStack = this.createItemStackFor(blockState);
            this.renderIcon(context, this.getContentX(), this.getContentY(), itemStack);
            int i = this.getContentMiddleY() - CustomizeFlatLevelScreen.this.textRenderer.fontHeight / 2;
            context.drawTextWithShadow(CustomizeFlatLevelScreen.this.textRenderer, itemStack.getName(), this.getContentX() + 18 + 5, i, -1);
            MutableText text = this.index == 0 ? Text.translatable("createWorld.customize.flat.layer.top", this.layer.getThickness()) : (this.index == CustomizeFlatLevelScreen.this.config.getLayers().size() - 1 ? Text.translatable("createWorld.customize.flat.layer.bottom", this.layer.getThickness()) : Text.translatable("createWorld.customize.flat.layer", this.layer.getThickness()));
            context.drawTextWithShadow(CustomizeFlatLevelScreen.this.textRenderer, text, this.getContentRightEnd() - CustomizeFlatLevelScreen.this.textRenderer.getWidth(text), i, -1);
        }

        private ItemStack createItemStackFor(BlockState state) {
            Item item = state.getBlock().asItem();
            if (item == Items.AIR) {
                if (state.isOf(Blocks.WATER)) {
                    item = Items.WATER_BUCKET;
                } else if (state.isOf(Blocks.LAVA)) {
                    item = Items.LAVA_BUCKET;
                }
            }
            return new ItemStack(item);
        }

        @Override
        public Text getNarration() {
            ItemStack itemStack = this.createItemStackFor(this.layer.getBlockState());
            if (!itemStack.isEmpty()) {
                return ScreenTexts.joinSentences(Text.translatable("narrator.select", itemStack.getName()), HEIGHT_TEXT, Text.literal(String.valueOf(this.layer.getThickness())));
            }
            return ScreenTexts.EMPTY;
        }

        @Override
        public boolean mouseClicked(Click click, boolean doubled) {
            SuperflatLayersListWidget.this.setSelected(this);
            return super.mouseClicked(click, doubled);
        }

        private void renderIcon(DrawContext context, int x, int y, ItemStack iconItem) {
            this.renderIconBackgroundTexture(context, x + 1, y + 1);
            if (!iconItem.isEmpty()) {
                context.drawItemWithoutEntity(iconItem, x + 2, y + 2);
            }
        }

        private void renderIconBackgroundTexture(DrawContext context, int x, int y) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SLOT_TEXTURE, x, y, 18, 18);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static abstract class Entry
    extends AlwaysSelectedEntryListWidget.Entry<Entry> {
        Entry() {
        }
    }
}
