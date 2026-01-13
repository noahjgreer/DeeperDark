/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LightningEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

@Environment(value=EnvType.CLIENT)
public class LightningEntityRenderer
extends EntityRenderer<LightningEntity, LightningEntityRenderState> {
    public LightningEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(LightningEntityRenderState lightningEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        float[] fs = new float[8];
        float[] gs = new float[8];
        float f = 0.0f;
        float g = 0.0f;
        Random random = Random.create(lightningEntityRenderState.seed);
        for (int i = 7; i >= 0; --i) {
            fs[i] = f;
            gs[i] = g;
            f += (float)(random.nextInt(11) - 5);
            g += (float)(random.nextInt(11) - 5);
        }
        float h = f;
        float j = g;
        orderedRenderCommandQueue.submitCustom(matrixStack, RenderLayers.lightning(), (matricesEntry, vertexConsumer) -> {
            Matrix4f matrix4f = matricesEntry.getPositionMatrix();
            for (int i = 0; i < 4; ++i) {
                Random random = Random.create(lightningEntityRenderState.seed);
                for (int j = 0; j < 3; ++j) {
                    int k = 7;
                    int l = 0;
                    if (j > 0) {
                        k = 7 - j;
                    }
                    if (j > 0) {
                        l = k - 2;
                    }
                    float h = fs[k] - h;
                    float m = gs[k] - j;
                    for (int n = k; n >= l; --n) {
                        float o = h;
                        float p = m;
                        if (j == 0) {
                            h += (float)(random.nextInt(11) - 5);
                            m += (float)(random.nextInt(11) - 5);
                        } else {
                            h += (float)(random.nextInt(31) - 15);
                            m += (float)(random.nextInt(31) - 15);
                        }
                        float q = 0.5f;
                        float r = 0.45f;
                        float s = 0.45f;
                        float t = 0.5f;
                        float u = 0.1f + (float)i * 0.2f;
                        if (j == 0) {
                            u *= (float)n * 0.1f + 1.0f;
                        }
                        float v = 0.1f + (float)i * 0.2f;
                        if (j == 0) {
                            v *= ((float)n - 1.0f) * 0.1f + 1.0f;
                        }
                        LightningEntityRenderer.drawBranch(matrix4f, vertexConsumer, h, m, n, o, p, 0.45f, 0.45f, 0.5f, u, v, false, false, true, false);
                        LightningEntityRenderer.drawBranch(matrix4f, vertexConsumer, h, m, n, o, p, 0.45f, 0.45f, 0.5f, u, v, true, false, true, true);
                        LightningEntityRenderer.drawBranch(matrix4f, vertexConsumer, h, m, n, o, p, 0.45f, 0.45f, 0.5f, u, v, true, true, false, true);
                        LightningEntityRenderer.drawBranch(matrix4f, vertexConsumer, h, m, n, o, p, 0.45f, 0.45f, 0.5f, u, v, false, true, false, false);
                    }
                }
            }
        });
    }

    private static void drawBranch(Matrix4f matrix, VertexConsumer buffer, float x1, float z1, int y, float x2, float z2, float red, float green, float blue, float offset2, float offset1, boolean shiftEast1, boolean shiftSouth1, boolean shiftEast2, boolean shiftSouth2) {
        buffer.vertex((Matrix4fc)matrix, x1 + (shiftEast1 ? offset1 : -offset1), (float)(y * 16), z1 + (shiftSouth1 ? offset1 : -offset1)).color(red, green, blue, 0.3f);
        buffer.vertex((Matrix4fc)matrix, x2 + (shiftEast1 ? offset2 : -offset2), (float)((y + 1) * 16), z2 + (shiftSouth1 ? offset2 : -offset2)).color(red, green, blue, 0.3f);
        buffer.vertex((Matrix4fc)matrix, x2 + (shiftEast2 ? offset2 : -offset2), (float)((y + 1) * 16), z2 + (shiftSouth2 ? offset2 : -offset2)).color(red, green, blue, 0.3f);
        buffer.vertex((Matrix4fc)matrix, x1 + (shiftEast2 ? offset1 : -offset1), (float)(y * 16), z1 + (shiftSouth2 ? offset1 : -offset1)).color(red, green, blue, 0.3f);
    }

    @Override
    public LightningEntityRenderState createRenderState() {
        return new LightningEntityRenderState();
    }

    @Override
    public void updateRenderState(LightningEntity lightningEntity, LightningEntityRenderState lightningEntityRenderState, float f) {
        super.updateRenderState(lightningEntity, lightningEntityRenderState, f);
        lightningEntityRenderState.seed = lightningEntity.seed;
    }

    @Override
    protected boolean canBeCulled(LightningEntity lightningEntity) {
        return false;
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    @Override
    protected /* synthetic */ boolean canBeCulled(Entity entity) {
        return this.canBeCulled((LightningEntity)entity);
    }
}
