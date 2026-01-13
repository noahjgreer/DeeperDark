/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.TexturedRenderLayers
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.VertexConsumerProvider$Immediate
 *  net.minecraft.client.render.command.BatchingRenderCommandQueue
 *  net.minecraft.client.render.command.FireCommandRenderer
 *  net.minecraft.client.render.command.OrderedRenderCommandQueueImpl$FireCommand
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.model.ModelBaker
 *  net.minecraft.client.texture.AtlasManager
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.client.util.math.MatrixStack$Entry
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.command;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.BatchingRenderCommandQueue;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.texture.AtlasManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class FireCommandRenderer {
    public void render(BatchingRenderCommandQueue queue, VertexConsumerProvider.Immediate vertexConsumers, AtlasManager atlasManager) {
        for (OrderedRenderCommandQueueImpl.FireCommand fireCommand : queue.getFireCommands()) {
            this.render(fireCommand.matricesEntry(), (VertexConsumerProvider)vertexConsumers, fireCommand.renderState(), fireCommand.rotation(), atlasManager);
        }
    }

    private void render(MatrixStack.Entry matricesEntry, VertexConsumerProvider vertexConsumers, EntityRenderState renderState, Quaternionf rotation, AtlasManager atlasManager) {
        Sprite sprite = atlasManager.getSprite(ModelBaker.FIRE_0);
        Sprite sprite2 = atlasManager.getSprite(ModelBaker.FIRE_1);
        float f = renderState.width * 1.4f;
        matricesEntry.scale(f, f, f);
        float g = 0.5f;
        float h = 0.0f;
        float i = renderState.height / f;
        float j = 0.0f;
        matricesEntry.rotate((Quaternionfc)rotation);
        matricesEntry.translate(0.0f, 0.0f, 0.3f - (float)((int)i) * 0.02f);
        float k = 0.0f;
        int l = 0;
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(TexturedRenderLayers.getEntityCutout());
        while (i > 0.0f) {
            Sprite sprite3 = l % 2 == 0 ? sprite : sprite2;
            float m = sprite3.getMinU();
            float n = sprite3.getMinV();
            float o = sprite3.getMaxU();
            float p = sprite3.getMaxV();
            if (l / 2 % 2 == 0) {
                float q = o;
                o = m;
                m = q;
            }
            FireCommandRenderer.vertex((MatrixStack.Entry)matricesEntry, (VertexConsumer)vertexConsumer, (float)(-g - 0.0f), (float)(0.0f - j), (float)k, (float)o, (float)p);
            FireCommandRenderer.vertex((MatrixStack.Entry)matricesEntry, (VertexConsumer)vertexConsumer, (float)(g - 0.0f), (float)(0.0f - j), (float)k, (float)m, (float)p);
            FireCommandRenderer.vertex((MatrixStack.Entry)matricesEntry, (VertexConsumer)vertexConsumer, (float)(g - 0.0f), (float)(1.4f - j), (float)k, (float)m, (float)n);
            FireCommandRenderer.vertex((MatrixStack.Entry)matricesEntry, (VertexConsumer)vertexConsumer, (float)(-g - 0.0f), (float)(1.4f - j), (float)k, (float)o, (float)n);
            i -= 0.45f;
            j -= 0.45f;
            g *= 0.9f;
            k -= 0.03f;
            ++l;
        }
    }

    private static void vertex(MatrixStack.Entry matricesEntry, VertexConsumer vertexConsumer, float x, float y, float z, float u, float v) {
        vertexConsumer.vertex(matricesEntry, x, y, z).color(-1).texture(u, v).overlay(0, 10).light(240).normal(matricesEntry, 0.0f, 1.0f, 0.0f);
    }
}

