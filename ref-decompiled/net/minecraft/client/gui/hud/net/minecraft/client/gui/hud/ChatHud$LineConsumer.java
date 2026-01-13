/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.ChatHudLine;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
static interface ChatHud.LineConsumer {
    public void accept(ChatHudLine.Visible var1, int var2, float var3);
}
