/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderTickCounter;

@Environment(value=EnvType.CLIENT)
public static class RenderTickCounter.Constant
implements RenderTickCounter {
    private final float value;

    RenderTickCounter.Constant(float value) {
        this.value = value;
    }

    @Override
    public float getDynamicDeltaTicks() {
        return this.value;
    }

    @Override
    public float getTickProgress(boolean ignoreFreeze) {
        return this.value;
    }

    @Override
    public float getFixedDeltaTicks() {
        return this.value;
    }
}
