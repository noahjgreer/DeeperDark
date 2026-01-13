/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.ingame.GrindstoneScreen
 *  net.minecraft.client.gui.screen.ingame.HandledScreen
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.screen.GrindstoneScreenHandler
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class GrindstoneScreen
extends HandledScreen<GrindstoneScreenHandler> {
    private static final Identifier ERROR_TEXTURE = Identifier.ofVanilla((String)"container/grindstone/error");
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/gui/container/grindstone.png");

    public GrindstoneScreen(GrindstoneScreenHandler handler, PlayerInventory inventory, Text title) {
        super((ScreenHandler)handler, inventory, title);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0f, 0.0f, this.backgroundWidth, this.backgroundHeight, 256, 256);
        if ((((GrindstoneScreenHandler)this.handler).getSlot(0).hasStack() || ((GrindstoneScreenHandler)this.handler).getSlot(1).hasStack()) && !((GrindstoneScreenHandler)this.handler).getSlot(2).hasStack()) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ERROR_TEXTURE, i + 92, j + 31, 28, 21);
        }
    }
}

