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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.spectator.SpectatorMenu;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
class SpectatorMenu.1
implements SpectatorMenuCommand {
    SpectatorMenu.1() {
    }

    @Override
    public void use(SpectatorMenu menu) {
    }

    @Override
    public Text getName() {
        return ScreenTexts.EMPTY;
    }

    @Override
    public void renderIcon(DrawContext context, float brightness, float alpha) {
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
