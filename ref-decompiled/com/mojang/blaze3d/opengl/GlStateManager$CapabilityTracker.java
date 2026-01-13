/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.opengl.GL11
 */
package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.opengl.GL11;

@Environment(value=EnvType.CLIENT)
static class GlStateManager.CapabilityTracker {
    private final int cap;
    private boolean state;

    public GlStateManager.CapabilityTracker(int cap) {
        this.cap = cap;
    }

    public void disable() {
        this.setState(false);
    }

    public void enable() {
        this.setState(true);
    }

    public void setState(boolean state) {
        RenderSystem.assertOnRenderThread();
        if (state != this.state) {
            this.state = state;
            if (state) {
                GL11.glEnable((int)this.cap);
            } else {
                GL11.glDisable((int)this.cap);
            }
        }
    }
}
