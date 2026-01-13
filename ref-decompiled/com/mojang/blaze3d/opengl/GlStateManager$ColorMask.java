/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.blaze3d.opengl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
static class GlStateManager.ColorMask {
    public boolean red = true;
    public boolean green = true;
    public boolean blue = true;
    public boolean alpha = true;

    GlStateManager.ColorMask() {
    }
}
