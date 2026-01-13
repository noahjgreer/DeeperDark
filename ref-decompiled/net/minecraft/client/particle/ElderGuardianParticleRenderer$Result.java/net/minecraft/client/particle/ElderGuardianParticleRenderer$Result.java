/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.particle;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ElderGuardianParticleRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.Submittable;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.util.Unit;

@Environment(value=EnvType.CLIENT)
record ElderGuardianParticleRenderer.Result(List<ElderGuardianParticleRenderer.State> states) implements Submittable
{
    @Override
    public void submit(OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        for (ElderGuardianParticleRenderer.State state : this.states) {
            orderedRenderCommandQueue.submitModel(state.model, Unit.INSTANCE, state.matrices, state.renderLayer, 0xF000F0, OverlayTexture.DEFAULT_UV, state.color, null, 0, null);
        }
    }
}
