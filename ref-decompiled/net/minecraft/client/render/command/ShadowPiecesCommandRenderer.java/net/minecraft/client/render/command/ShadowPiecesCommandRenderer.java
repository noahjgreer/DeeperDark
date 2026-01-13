/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
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

@Environment(value=EnvType.CLIENT)
public class ShadowPiecesCommandRenderer {
    private static final RenderLayer renderLayer = RenderLayers.entityShadow(Identifier.ofVanilla("textures/misc/shadow.png"));

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
                int p = ColorHelper.getWhite(shadowPiece.alpha());
                ShadowPiecesCommandRenderer.vertex(shadowPiecesCommand.matricesEntry(), vertexConsumer, p, f, h, i, l, n);
                ShadowPiecesCommandRenderer.vertex(shadowPiecesCommand.matricesEntry(), vertexConsumer, p, f, h, j, l, o);
                ShadowPiecesCommandRenderer.vertex(shadowPiecesCommand.matricesEntry(), vertexConsumer, p, g, h, j, m, o);
                ShadowPiecesCommandRenderer.vertex(shadowPiecesCommand.matricesEntry(), vertexConsumer, p, g, h, i, m, n);
            }
        }
    }

    private static void vertex(Matrix4f matrix, VertexConsumer vertexConsumer, int color, float x, float y, float z, float u, float v) {
        Vector3f vector3f = matrix.transformPosition(x, y, z, new Vector3f());
        vertexConsumer.vertex(vector3f.x(), vector3f.y(), vector3f.z(), color, u, v, OverlayTexture.DEFAULT_UV, 0xF000F0, 0.0f, 1.0f, 0.0f);
    }
}
