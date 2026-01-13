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
static class GlStateManager.PolygonOffsetState {
    public final GlStateManager.CapabilityTracker capFill = new GlStateManager.CapabilityTracker(32823);
    public float factor;
    public float units;

    GlStateManager.PolygonOffsetState() {
    }
}
