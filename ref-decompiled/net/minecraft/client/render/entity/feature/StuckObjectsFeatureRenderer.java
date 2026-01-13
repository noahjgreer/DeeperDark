/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.model.ModelPart$Cuboid
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.LivingEntityRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.StuckObjectsFeatureRenderer
 *  net.minecraft.client.render.entity.feature.StuckObjectsFeatureRenderer$RenderPosition
 *  net.minecraft.client.render.entity.model.PlayerEntityModel
 *  net.minecraft.client.render.entity.state.PlayerEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.util.math.random.Random
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckObjectsFeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import org.joml.Quaternionfc;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public abstract class StuckObjectsFeatureRenderer<M extends PlayerEntityModel, S>
extends FeatureRenderer<PlayerEntityRenderState, M> {
    private final Model<S> model;
    private final S stuckObjectState;
    private final Identifier texture;
    private final RenderPosition renderPosition;

    public StuckObjectsFeatureRenderer(LivingEntityRenderer<?, PlayerEntityRenderState, M> entityRenderer, Model<S> model, S stuckObjectState, Identifier texture, RenderPosition renderPosition) {
        super(entityRenderer);
        this.model = model;
        this.stuckObjectState = stuckObjectState;
        this.texture = texture;
        this.renderPosition = renderPosition;
    }

    protected abstract int getObjectCount(PlayerEntityRenderState var1);

    private void renderObject(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, float f, float directionX, float directionY, int color) {
        float g = MathHelper.sqrt((float)(f * f + directionY * directionY));
        float h = (float)(Math.atan2(f, directionY) * 57.2957763671875);
        float i = (float)(Math.atan2(directionX, g) * 57.2957763671875);
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(h - 90.0f));
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(i));
        queue.submitModel(this.model, this.stuckObjectState, matrices, this.model.getLayer(this.texture), light, OverlayTexture.DEFAULT_UV, color, null);
    }

    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, PlayerEntityRenderState playerEntityRenderState, float f, float g) {
        int j = this.getObjectCount(playerEntityRenderState);
        if (j <= 0) {
            return;
        }
        Random random = Random.create((long)playerEntityRenderState.id);
        for (int k = 0; k < j; ++k) {
            matrixStack.push();
            ModelPart modelPart = ((PlayerEntityModel)this.getContextModel()).getRandomPart(random);
            ModelPart.Cuboid cuboid = modelPart.getRandomCuboid(random);
            modelPart.applyTransform(matrixStack);
            float h = random.nextFloat();
            float l = random.nextFloat();
            float m = random.nextFloat();
            if (this.renderPosition == RenderPosition.ON_SURFACE) {
                int n = random.nextInt(3);
                switch (n) {
                    case 0: {
                        h = StuckObjectsFeatureRenderer.method_62597((float)h);
                        break;
                    }
                    case 1: {
                        l = StuckObjectsFeatureRenderer.method_62597((float)l);
                        break;
                    }
                    default: {
                        m = StuckObjectsFeatureRenderer.method_62597((float)m);
                    }
                }
            }
            matrixStack.translate(MathHelper.lerp((float)h, (float)cuboid.minX, (float)cuboid.maxX) / 16.0f, MathHelper.lerp((float)l, (float)cuboid.minY, (float)cuboid.maxY) / 16.0f, MathHelper.lerp((float)m, (float)cuboid.minZ, (float)cuboid.maxZ) / 16.0f);
            this.renderObject(matrixStack, orderedRenderCommandQueue, i, -(h * 2.0f - 1.0f), -(l * 2.0f - 1.0f), -(m * 2.0f - 1.0f), playerEntityRenderState.outlineColor);
            matrixStack.pop();
        }
    }

    private static float method_62597(float f) {
        return f > 0.5f ? 1.0f : 0.5f;
    }
}

