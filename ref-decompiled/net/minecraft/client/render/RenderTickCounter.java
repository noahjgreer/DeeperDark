/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.RenderTickCounter
 *  net.minecraft.client.render.RenderTickCounter$Constant
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderTickCounter;

@Environment(value=EnvType.CLIENT)
public interface RenderTickCounter {
    public static final RenderTickCounter ZERO = new Constant(0.0f);
    public static final RenderTickCounter ONE = new Constant(1.0f);

    public float getDynamicDeltaTicks();

    public float getTickProgress(boolean var1);

    public float getFixedDeltaTicks();
}

