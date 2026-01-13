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
 *  net.minecraft.client.render.entity.EntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.ExperienceOrbEntityRenderer
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.ExperienceOrbEntityRenderState
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.util.math.MatrixStack$Entry
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ExperienceOrbEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
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
import net.minecraft.client.render.entity.state.ExperienceOrbEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionfc;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ExperienceOrbEntityRenderer
extends EntityRenderer<ExperienceOrbEntity, ExperienceOrbEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/experience_orb.png");
    private static final RenderLayer LAYER = RenderLayers.itemEntityTranslucentCull((Identifier)TEXTURE);

    public ExperienceOrbEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.15f;
        this.shadowOpacity = 0.75f;
    }

    protected int getBlockLight(ExperienceOrbEntity experienceOrbEntity, BlockPos blockPos) {
        return MathHelper.clamp((int)(super.getBlockLight((Entity)experienceOrbEntity, blockPos) + 7), (int)0, (int)15);
    }

    public void render(ExperienceOrbEntityRenderState experienceOrbEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        matrixStack.push();
        int i = experienceOrbEntityRenderState.size;
        float f = (float)(i % 4 * 16 + 0) / 64.0f;
        float g = (float)(i % 4 * 16 + 16) / 64.0f;
        float h = (float)(i / 4 * 16 + 0) / 64.0f;
        float j = (float)(i / 4 * 16 + 16) / 64.0f;
        float k = 1.0f;
        float l = 0.5f;
        float m = 0.25f;
        float n = 255.0f;
        float o = experienceOrbEntityRenderState.age / 2.0f;
        int p = (int)((MathHelper.sin((double)(o + 0.0f)) + 1.0f) * 0.5f * 255.0f);
        int q = 255;
        int r = (int)((MathHelper.sin((double)(o + 4.1887903f)) + 1.0f) * 0.1f * 255.0f);
        matrixStack.translate(0.0f, 0.1f, 0.0f);
        matrixStack.multiply((Quaternionfc)cameraRenderState.orientation);
        float s = 0.3f;
        matrixStack.scale(0.3f, 0.3f, 0.3f);
        orderedRenderCommandQueue.submitCustom(matrixStack, LAYER, (matricesEntry, vertexConsumer) -> {
            ExperienceOrbEntityRenderer.vertex((VertexConsumer)vertexConsumer, (MatrixStack.Entry)matricesEntry, (float)-0.5f, (float)-0.25f, (int)p, (int)255, (int)r, (float)f, (float)j, (int)experienceOrbEntityRenderState.light);
            ExperienceOrbEntityRenderer.vertex((VertexConsumer)vertexConsumer, (MatrixStack.Entry)matricesEntry, (float)0.5f, (float)-0.25f, (int)p, (int)255, (int)r, (float)g, (float)j, (int)experienceOrbEntityRenderState.light);
            ExperienceOrbEntityRenderer.vertex((VertexConsumer)vertexConsumer, (MatrixStack.Entry)matricesEntry, (float)0.5f, (float)0.75f, (int)p, (int)255, (int)r, (float)g, (float)h, (int)experienceOrbEntityRenderState.light);
            ExperienceOrbEntityRenderer.vertex((VertexConsumer)vertexConsumer, (MatrixStack.Entry)matricesEntry, (float)-0.5f, (float)0.75f, (int)p, (int)255, (int)r, (float)f, (float)h, (int)experienceOrbEntityRenderState.light);
        });
        matrixStack.pop();
        super.render((EntityRenderState)experienceOrbEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    private static void vertex(VertexConsumer vertexConsumer, MatrixStack.Entry matrix, float x, float y, int red, int green, int blue, float u, float v, int light) {
        vertexConsumer.vertex(matrix, x, y, 0.0f).color(red, green, blue, 128).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix, 0.0f, 1.0f, 0.0f);
    }

    public ExperienceOrbEntityRenderState createRenderState() {
        return new ExperienceOrbEntityRenderState();
    }

    public void updateRenderState(ExperienceOrbEntity experienceOrbEntity, ExperienceOrbEntityRenderState experienceOrbEntityRenderState, float f) {
        super.updateRenderState((Entity)experienceOrbEntity, (EntityRenderState)experienceOrbEntityRenderState, f);
        experienceOrbEntityRenderState.size = experienceOrbEntity.getOrbSize();
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

