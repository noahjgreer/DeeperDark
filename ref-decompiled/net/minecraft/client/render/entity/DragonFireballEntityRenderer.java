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
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.DragonFireballEntityRenderer
 *  net.minecraft.client.render.entity.EntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.util.math.MatrixStack$Entry
 *  net.minecraft.entity.projectile.DragonFireballEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.BlockPos
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.joml.Quaternionfc;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class DragonFireballEntityRenderer
extends EntityRenderer<DragonFireballEntity, EntityRenderState> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/enderdragon/dragon_fireball.png");
    private static final RenderLayer LAYER = RenderLayers.entityCutoutNoCull((Identifier)TEXTURE);

    public DragonFireballEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    protected int getBlockLight(DragonFireballEntity dragonFireballEntity, BlockPos blockPos) {
        return 15;
    }

    public void render(EntityRenderState renderState, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState) {
        matrices.push();
        matrices.scale(2.0f, 2.0f, 2.0f);
        matrices.multiply((Quaternionfc)cameraState.orientation);
        queue.submitCustom(matrices, LAYER, (matricesEntry, vertexConsumer) -> {
            DragonFireballEntityRenderer.produceVertex((VertexConsumer)vertexConsumer, (MatrixStack.Entry)matricesEntry, (int)entityRenderState.light, (float)0.0f, (int)0, (int)0, (int)1);
            DragonFireballEntityRenderer.produceVertex((VertexConsumer)vertexConsumer, (MatrixStack.Entry)matricesEntry, (int)entityRenderState.light, (float)1.0f, (int)0, (int)1, (int)1);
            DragonFireballEntityRenderer.produceVertex((VertexConsumer)vertexConsumer, (MatrixStack.Entry)matricesEntry, (int)entityRenderState.light, (float)1.0f, (int)1, (int)1, (int)0);
            DragonFireballEntityRenderer.produceVertex((VertexConsumer)vertexConsumer, (MatrixStack.Entry)matricesEntry, (int)entityRenderState.light, (float)0.0f, (int)1, (int)0, (int)0);
        });
        matrices.pop();
        super.render(renderState, matrices, queue, cameraState);
    }

    private static void produceVertex(VertexConsumer vertexConsumer, MatrixStack.Entry matrix, int light, float x, int z, int textureU, int textureV) {
        vertexConsumer.vertex(matrix, x - 0.5f, (float)z - 0.25f, 0.0f).color(-1).texture((float)textureU, (float)textureV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix, 0.0f, 1.0f, 0.0f);
    }

    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
}

