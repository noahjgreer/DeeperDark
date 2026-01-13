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
static class GlStateManager.LogicOpState {
    public final GlStateManager.CapabilityTracker capState = new GlStateManager.CapabilityTracker(3058);
    public int op = 5379;

    GlStateManager.LogicOpState() {
    }
}
