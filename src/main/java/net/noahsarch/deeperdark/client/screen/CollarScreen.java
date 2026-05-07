package net.noahsarch.deeperdark.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.noahsarch.deeperdark.menu.CollarMenu;

@Environment(EnvType.CLIENT)
public class CollarScreen extends AbstractContainerScreen<CollarMenu> {

    private static final Identifier HOPPER_TEXTURE = Identifier.withDefaultNamespace(
        "textures/gui/container/hopper.png"
    );

    public CollarScreen(CollarMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 133);
        this.titleLabelY = 6;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(RenderPipelines.GUI_TEXTURED, HOPPER_TEXTURE, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
    }
}
