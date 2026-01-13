/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
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
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class ExperienceOrbEntityRenderer
extends EntityRenderer<ExperienceOrbEntity, ExperienceOrbEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/experience_orb.png");
    private static final RenderLayer LAYER = RenderLayers.itemEntityTranslucentCull(TEXTURE);

    public ExperienceOrbEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.15f;
        this.shadowOpacity = 0.75f;
    }

    @Override
    protected int getBlockLight(ExperienceOrbEntity experienceOrbEntity, BlockPos blockPos) {
        return MathHelper.clamp(super.getBlockLight(experienceOrbEntity, blockPos) + 7, 0, 15);
    }

    @Override
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
        int p = (int)((MathHelper.sin(o + 0.0f) + 1.0f) * 0.5f * 255.0f);
        int q = 255;
        int r = (int)((MathHelper.sin(o + 4.1887903f) + 1.0f) * 0.1f * 255.0f);
        matrixStack.translate(0.0f, 0.1f, 0.0f);
        matrixStack.multiply((Quaternionfc)cameraRenderState.orientation);
        float s = 0.3f;
        matrixStack.scale(0.3f, 0.3f, 0.3f);
        orderedRenderCommandQueue.submitCustom(matrixStack, LAYER, (matricesEntry, vertexConsumer) -> {
            ExperienceOrbEntityRenderer.vertex(vertexConsumer, matricesEntry, -0.5f, -0.25f, p, 255, r, f, j, experienceOrbEntityRenderState.light);
            ExperienceOrbEntityRenderer.vertex(vertexConsumer, matricesEntry, 0.5f, -0.25f, p, 255, r, g, j, experienceOrbEntityRenderState.light);
            ExperienceOrbEntityRenderer.vertex(vertexConsumer, matricesEntry, 0.5f, 0.75f, p, 255, r, g, h, experienceOrbEntityRenderState.light);
            ExperienceOrbEntityRenderer.vertex(vertexConsumer, matricesEntry, -0.5f, 0.75f, p, 255, r, f, h, experienceOrbEntityRenderState.light);
        });
        matrixStack.pop();
        super.render(experienceOrbEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    private static void vertex(VertexConsumer vertexConsumer, MatrixStack.Entry matrix, float x, float y, int red, int green, int blue, float u, float v, int light) {
        vertexConsumer.vertex(matrix, x, y, 0.0f).color(red, green, blue, 128).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix, 0.0f, 1.0f, 0.0f);
    }

    @Override
    public ExperienceOrbEntityRenderState createRenderState() {
        return new ExperienceOrbEntityRenderState();
    }

    @Override
    public void updateRenderState(ExperienceOrbEntity experienceOrbEntity, ExperienceOrbEntityRenderState experienceOrbEntityRenderState, float f) {
        super.updateRenderState(experienceOrbEntity, experienceOrbEntityRenderState, f);
        experienceOrbEntityRenderState.size = experienceOrbEntity.getOrbSize();
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
