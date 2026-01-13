/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.hud.spectator.SpectatorMenu
 *  net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand
 *  net.minecraft.text.Text
 */
package net.minecraft.client.gui.hud.spectator;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.spectator.SpectatorMenu;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public interface SpectatorMenuCommand {
    public void use(SpectatorMenu var1);

    public Text getName();

    public void renderIcon(DrawContext var1, float var2, float var3);

    public boolean isEnabled();
}

