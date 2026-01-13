/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package net.minecraft.client.render.command;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.BatchingRenderCommandQueue;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

@Environment(value=EnvType.CLIENT)
public class LeashCommandRenderer {
    private static final int LEASH_SEGMENTS = 24;
    private static final float LEASH_WIDTH = 0.05f;

    public void render(BatchingRenderCommandQueue queue, VertexConsumerProvider.Immediate vertexConsumers) {
        for (OrderedRenderCommandQueueImpl.LeashCommand leashCommand : queue.getLeashCommands()) {
            LeashCommandRenderer.render(leashCommand.matricesEntry(), vertexConsumers, leashCommand.leashState());
        }
    }

    private static void render(Matrix4f matrix, VertexConsumerProvider vertexConsumers, EntityRenderState.LeashData data) {
        int l;
        float f = (float)(data.endPos.x - data.startPos.x);
        float g = (float)(data.endPos.y - data.startPos.y);
        float h = (float)(data.endPos.z - data.startPos.z);
        float i = MathHelper.inverseSqrt(f * f + h * h) * 0.05f / 2.0f;
        float j = h * i;
        float k = f * i;
        matrix.translate((float)data.offset.x, (float)data.offset.y, (float)data.offset.z);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayers.leash());
        for (l = 0; l <= 24; ++l) {
            LeashCommandRenderer.render(vertexConsumer, matrix, f, g, h, 0.05f, j, k, l, false, data);
        }
        for (l = 24; l >= 0; --l) {
            LeashCommandRenderer.render(vertexConsumer, matrix, f, g, h, 0.0f, j, k, l, true, data);
        }
    }

    private static void render(VertexConsumer vertexConsumer, Matrix4f matrix, float offsetX, float offsetY, float offsetZ, float yOffset, float sideOffset, float perpendicularOffset, int segmentIndex, boolean backside, EntityRenderState.LeashData data) {
        float f = (float)segmentIndex / 24.0f;
        int i = (int)MathHelper.lerp(f, (float)data.leashedEntityBlockLight, (float)data.leashHolderBlockLight);
        int j = (int)MathHelper.lerp(f, (float)data.leashedEntitySkyLight, (float)data.leashHolderSkyLight);
        int k = LightmapTextureManager.pack(i, j);
        float g = segmentIndex % 2 == (backside ? 1 : 0) ? 0.7f : 1.0f;
        float h = 0.5f * g;
        float l = 0.4f * g;
        float m = 0.3f * g;
        float n = offsetX * f;
        float o = data.slack ? (offsetY > 0.0f ? offsetY * f * f : offsetY - offsetY * (1.0f - f) * (1.0f - f)) : offsetY * f;
        float p = offsetZ * f;
        vertexConsumer.vertex((Matrix4fc)matrix, n - sideOffset, o + yOffset, p + perpendicularOffset).color(h, l, m, 1.0f).light(k);
        vertexConsumer.vertex((Matrix4fc)matrix, n + sideOffset, o + 0.05f - yOffset, p - perpendicularOffset).color(h, l, m, 1.0f).light(k);
    }
}
