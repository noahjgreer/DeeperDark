/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
static final class GuiRenderer.RenderedItem {
    final int x;
    final int y;
    final float u;
    final float v;
    int frame;

    GuiRenderer.RenderedItem(int x, int y, float u, float v, int frame) {
        this.x = x;
        this.y = y;
        this.u = u;
        this.v = v;
        this.frame = frame;
    }
}
