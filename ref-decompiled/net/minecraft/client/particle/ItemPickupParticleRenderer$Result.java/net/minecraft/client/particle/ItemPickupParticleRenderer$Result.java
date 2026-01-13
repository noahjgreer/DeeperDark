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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ItemPickupParticleRenderer;
import net.minecraft.client.render.Submittable;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
record ItemPickupParticleRenderer.Result(List<ItemPickupParticleRenderer.Instance> instances) implements Submittable
{
    @Override
    public void submit(OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        MatrixStack matrixStack = new MatrixStack();
        EntityRenderManager entityRenderManager = MinecraftClient.getInstance().getEntityRenderDispatcher();
        for (ItemPickupParticleRenderer.Instance instance : this.instances) {
            entityRenderManager.render(instance.itemRenderState, cameraRenderState, instance.xOffset, instance.yOffset, instance.zOffset, matrixStack, orderedRenderCommandQueue);
        }
    }
}
