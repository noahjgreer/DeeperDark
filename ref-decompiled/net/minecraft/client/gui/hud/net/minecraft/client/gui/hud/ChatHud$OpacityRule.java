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
import net.minecraft.util.math.MathHelper;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
static interface ChatHud.OpacityRule {
    public static final ChatHud.OpacityRule CONSTANT = line -> 1.0f;

    public static ChatHud.OpacityRule timeBased(int currentTick) {
        return line -> {
            int j = currentTick - line.addedTime();
            double d = (double)j / 200.0;
            d = 1.0 - d;
            d *= 10.0;
            d = MathHelper.clamp(d, 0.0, 1.0);
            d *= d;
            return (float)d;
        };
    }

    public float calculate(ChatHudLine.Visible var1);
}
