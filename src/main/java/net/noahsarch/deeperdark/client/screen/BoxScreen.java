package net.noahsarch.deeperdark.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.noahsarch.deeperdark.Deeperdark;
import net.noahsarch.deeperdark.menu.BoxMenu;

@Environment(EnvType.CLIENT)
public class BoxScreen extends AbstractContainerScreen<BoxMenu> {
    private static final Identifier GENERIC_3 = Identifier.fromNamespaceAndPath(
        Deeperdark.MOD_ID,
        "textures/gui/container/generic_3.png"
    );
    private static final Identifier GENERIC_6 = Identifier.fromNamespaceAndPath(
        Deeperdark.MOD_ID,
        "textures/gui/container/generic_6.png"
    );

    private final Identifier texture;

    public BoxScreen(BoxMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
        this.texture = menu.getRowCount() == 2 ? GENERIC_6 : GENERIC_3;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(RenderPipelines.GUI_TEXTURED, this.texture, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
    }
}
