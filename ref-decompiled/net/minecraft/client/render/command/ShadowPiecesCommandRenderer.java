/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexConsumerProvider$Immediate
 *  net.minecraft.client.render.command.BatchingRenderCommandQueue
 *  net.minecraft.client.render.command.OrderedRenderCommandQueueImpl$ShadowPiecesCommand
 *  net.minecraft.client.render.command.ShadowPiecesCommandRenderer
 *  net.minecraft.client.render.entity.state.EntityRenderState$ShadowPiece
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.ColorHelper
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 */
package net.minecraft.client.render.command;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.BatchingRenderCommandQueue;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ShadowPiecesCommandRenderer {
    private static final RenderLayer renderLayer = RenderLayers.entityShadow((Identifier)Identifier.ofVanilla((String)"textures/misc/shadow.png"));

    public void render(BatchingRenderCommandQueue queue, VertexConsumerProvider.Immediate vertexConsumers) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
        for (OrderedRenderCommandQueueImpl.ShadowPiecesCommand shadowPiecesCommand : queue.getShadowPiecesCommands()) {
            for (EntityRenderState.ShadowPiece shadowPiece : shadowPiecesCommand.pieces()) {
                Box box = shadowPiece.shapeBelow().getBoundingBox();
                float f = shadowPiece.relativeX() + (float)box.minX;
                float g = shadowPiece.relativeX() + (float)box.maxX;
                float h = shadowPiece.relativeY() + (float)box.minY;
                float i = shadowPiece.relativeZ() + (float)box.minZ;
                float j = shadowPiece.relativeZ() + (float)box.maxZ;
                float k = shadowPiecesCommand.radius();
                float l = -f / 2.0f / k + 0.5f;
                float m = -g / 2.0f / k + 0.5f;
                float n = -i / 2.0f / k + 0.5f;
                float o = -j / 2.0f / k + 0.5f;
                int p = ColorHelper.getWhite((float)shadowPiece.alpha());
                ShadowPiecesCommandRenderer.vertex((Matrix4f)shadowPiecesCommand.matricesEntry(), (VertexConsumer)vertexConsumer, (int)p, (float)f, (float)h, (float)i, (float)l, (float)n);
                ShadowPiecesCommandRenderer.vertex((Matrix4f)shadowPiecesCommand.matricesEntry(), (VertexConsumer)vertexConsumer, (int)p, (float)f, (float)h, (float)j, (float)l, (float)o);
                ShadowPiecesCommandRenderer.vertex((Matrix4f)shadowPiecesCommand.matricesEntry(), (VertexConsumer)vertexConsumer, (int)p, (float)g, (float)h, (float)j, (float)m, (float)o);
                ShadowPiecesCommandRenderer.vertex((Matrix4f)shadowPiecesCommand.matricesEntry(), (VertexConsumer)vertexConsumer, (int)p, (float)g, (float)h, (float)i, (float)m, (float)n);
            }
        }
    }

    private static void vertex(Matrix4f matrix, VertexConsumer vertexConsumer, int color, float x, float y, float z, float u, float v) {
        Vector3f vector3f = matrix.transformPosition(x, y, z, new Vector3f());
        vertexConsumer.vertex(vector3f.x(), vector3f.y(), vector3f.z(), color, u, v, OverlayTexture.DEFAULT_UV, 0xF000F0, 0.0f, 1.0f, 0.0f);
    }
}

