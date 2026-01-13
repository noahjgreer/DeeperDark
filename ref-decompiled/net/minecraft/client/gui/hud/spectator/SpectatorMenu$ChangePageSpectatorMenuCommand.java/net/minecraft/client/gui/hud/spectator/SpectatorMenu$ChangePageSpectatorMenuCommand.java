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
static class SpectatorMenu.ChangePageSpectatorMenuCommand
implements SpectatorMenuCommand {
    private final int direction;
    private final boolean enabled;

    public SpectatorMenu.ChangePageSpectatorMenuCommand(int direction, boolean enabled) {
        this.direction = direction;
        this.enabled = enabled;
    }

    @Override
    public void use(SpectatorMenu menu) {
        menu.page += this.direction;
    }

    @Override
    public Text getName() {
        return this.direction < 0 ? PREVIOUS_PAGE_TEXT : NEXT_PAGE_TEXT;
    }

    @Override
    public void renderIcon(DrawContext context, float brightness, float alpha) {
        int i = ColorHelper.fromFloats(alpha, brightness, brightness, brightness);
        if (this.direction < 0) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SCROLL_LEFT_TEXTURE, 0, 0, 16, 16, i);
        } else {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SCROLL_RIGHT_TEXTURE, 0, 0, 16, 16, i);
        }
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
