/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.Submittable
 *  net.minecraft.client.render.SubmittableBatch
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.command.OrderedRenderCommandQueueImpl
 *  net.minecraft.client.render.state.CameraRenderState
 */
package net.minecraft.client.render;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Submittable;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import net.minecraft.client.render.state.CameraRenderState;

@Environment(value=EnvType.CLIENT)
public class SubmittableBatch {
    public final List<Submittable> batch = new ArrayList();

    public void onFrameEnd() {
        this.batch.forEach(Submittable::onFrameEnd);
        this.batch.clear();
    }

    public void add(Submittable submittable) {
        this.batch.add(submittable);
    }

    public void submit(OrderedRenderCommandQueueImpl queue, CameraRenderState cameraRenderState) {
        for (Submittable submittable : this.batch) {
            submittable.submit((OrderedRenderCommandQueue)queue, cameraRenderState);
        }
    }
}

