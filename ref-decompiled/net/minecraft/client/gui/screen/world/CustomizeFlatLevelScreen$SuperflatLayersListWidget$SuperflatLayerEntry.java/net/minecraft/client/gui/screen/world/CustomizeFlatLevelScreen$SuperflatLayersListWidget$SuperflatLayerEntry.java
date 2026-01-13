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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.world.CustomizeFlatLevelScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;

@Environment(value=EnvType.CLIENT)
class CustomizeFlatLevelScreen.SuperflatLayersListWidget.SuperflatLayerEntry
extends CustomizeFlatLevelScreen.SuperflatLayersListWidget.Entry {
    final FlatChunkGeneratorLayer layer;
    private final int index;

    public CustomizeFlatLevelScreen.SuperflatLayersListWidget.SuperflatLayerEntry(FlatChunkGeneratorLayer layer, int index) {
        this.layer = layer;
        this.index = index;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        BlockState blockState = this.layer.getBlockState();
        ItemStack itemStack = this.createItemStackFor(blockState);
        this.renderIcon(context, this.getContentX(), this.getContentY(), itemStack);
        int i = this.getContentMiddleY() - SuperflatLayersListWidget.this.field_18738.textRenderer.fontHeight / 2;
        context.drawTextWithShadow(SuperflatLayersListWidget.this.field_18738.textRenderer, itemStack.getName(), this.getContentX() + 18 + 5, i, -1);
        MutableText text = this.index == 0 ? Text.translatable("createWorld.customize.flat.layer.top", this.layer.getThickness()) : (this.index == SuperflatLayersListWidget.this.field_18738.config.getLayers().size() - 1 ? Text.translatable("createWorld.customize.flat.layer.bottom", this.layer.getThickness()) : Text.translatable("createWorld.customize.flat.layer", this.layer.getThickness()));
        context.drawTextWithShadow(SuperflatLayersListWidget.this.field_18738.textRenderer, text, this.getContentRightEnd() - SuperflatLayersListWidget.this.field_18738.textRenderer.getWidth(text), i, -1);
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
