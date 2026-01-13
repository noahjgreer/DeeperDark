/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.opengl.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
static class GlStateManager.BlendFuncState {
    public final GlStateManager.CapabilityTracker capState = new GlStateManager.CapabilityTracker(3042);
    public int srcFactorRgb = 1;
    public int dstFactorRgb = 0;
    public int srcFactorAlpha = 1;
    public int dstFactorAlpha = 0;

    GlStateManager.BlendFuncState() {
    }
}
