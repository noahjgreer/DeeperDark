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
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;

@Environment(value=EnvType.CLIENT)
public interface Submittable {
    public void submit(OrderedRenderCommandQueue var1, CameraRenderState var2);

    default public void onFrameEnd() {
    }
}
