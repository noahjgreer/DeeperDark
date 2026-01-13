/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexRendering
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.util.math.MatrixStack$Entry
 *  net.minecraft.util.shape.VoxelShape
 *  org.joml.Vector3f
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.shape.VoxelShape;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class VertexRendering {
    public static void drawOutline(MatrixStack matrices, VertexConsumer vertexConsumers, VoxelShape shape, double offsetX, double offsetY, double offsetZ, int color, float lineWidth) {
        MatrixStack.Entry entry = matrices.peek();
        shape.forEachEdge((minX, minY, minZ, maxX, maxY, maxZ) -> {
            Vector3f vector3f = new Vector3f((float)(maxX - minX), (float)(maxY - minY), (float)(maxZ - minZ)).normalize();
            vertexConsumers.vertex(entry, (float)(minX + offsetX), (float)(minY + offsetY), (float)(minZ + offsetZ)).color(color).normal(entry, vector3f).lineWidth(lineWidth);
            vertexConsumers.vertex(entry, (float)(maxX + offsetX), (float)(maxY + offsetY), (float)(maxZ + offsetZ)).color(color).normal(entry, vector3f).lineWidth(lineWidth);
        });
    }
}

