/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud.spectator;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.spectator.SpectatorMenu;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

@Environment(value=EnvType.CLIENT)
static class SpectatorMenu.CloseSpectatorMenuCommand
implements SpectatorMenuCommand {
    SpectatorMenu.CloseSpectatorMenuCommand() {
    }

    @Override
    public void use(SpectatorMenu menu) {
        menu.close();
    }

    @Override
    public Text getName() {
        return CLOSE_TEXT;
    }

    @Override
    public void renderIcon(DrawContext context, float brightness, float alpha) {
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, CLOSE_TEXTURE, 0, 0, 16, 16, ColorHelper.fromFloats(alpha, brightness, brightness, brightness));
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
