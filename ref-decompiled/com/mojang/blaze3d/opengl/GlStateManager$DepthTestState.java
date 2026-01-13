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
static class GlStateManager.DepthTestState {
    public final GlStateManager.CapabilityTracker capState = new GlStateManager.CapabilityTracker(2929);
    public boolean mask = true;
    public int func = 513;

    GlStateManager.DepthTestState() {
    }
}
