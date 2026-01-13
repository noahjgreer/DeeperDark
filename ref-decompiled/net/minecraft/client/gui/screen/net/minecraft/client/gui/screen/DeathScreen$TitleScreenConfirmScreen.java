/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public static class DeathScreen.TitleScreenConfirmScreen
extends ConfirmScreen {
    public DeathScreen.TitleScreenConfirmScreen(BooleanConsumer booleanConsumer, Text text, Text text2, Text text3, Text text4) {
        super(booleanConsumer, text, text2, text3, text4);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        DeathScreen.fillBackgroundGradient(context, this.width, this.height);
    }
}
