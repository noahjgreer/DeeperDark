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
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EvokerFangsEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.EvokerFangsEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class EvokerFangsEntityRenderer
extends EntityRenderer<EvokerFangsEntity, EvokerFangsEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/illager/evoker_fangs.png");
    private final EvokerFangsEntityModel model;

    public EvokerFangsEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new EvokerFangsEntityModel(context.getPart(EntityModelLayers.EVOKER_FANGS));
    }

    @Override
    public void render(EvokerFangsEntityRenderState evokerFangsEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        float f = evokerFangsEntityRenderState.animationProgress;
        if (f == 0.0f) {
            return;
        }
        matrixStack.push();
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(90.0f - evokerFangsEntityRenderState.yaw));
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        matrixStack.translate(0.0f, -1.501f, 0.0f);
        orderedRenderCommandQueue.submitModel(this.model, evokerFangsEntityRenderState, matrixStack, this.model.getLayer(TEXTURE), evokerFangsEntityRenderState.light, OverlayTexture.DEFAULT_UV, evokerFangsEntityRenderState.outlineColor, null);
        matrixStack.pop();
        super.render(evokerFangsEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    @Override
    public EvokerFangsEntityRenderState createRenderState() {
        return new EvokerFangsEntityRenderState();
    }

    @Override
    public void updateRenderState(EvokerFangsEntity evokerFangsEntity, EvokerFangsEntityRenderState evokerFangsEntityRenderState, float f) {
        super.updateRenderState(evokerFangsEntity, evokerFangsEntityRenderState, f);
        evokerFangsEntityRenderState.yaw = evokerFangsEntity.getYaw();
        evokerFangsEntityRenderState.animationProgress = evokerFangsEntity.getAnimationProgress(f);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
