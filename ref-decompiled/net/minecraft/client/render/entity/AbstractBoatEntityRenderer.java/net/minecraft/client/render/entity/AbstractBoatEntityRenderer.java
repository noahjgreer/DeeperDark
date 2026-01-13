/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.BoatEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractBoatEntityRenderer
extends EntityRenderer<AbstractBoatEntity, BoatEntityRenderState> {
    public AbstractBoatEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.8f;
    }

    @Override
    public void render(BoatEntityRenderState boatEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        matrixStack.push();
        matrixStack.translate(0.0f, 0.375f, 0.0f);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - boatEntityRenderState.yaw));
        float f = boatEntityRenderState.damageWobbleTicks;
        if (f > 0.0f) {
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.sin(f) * f * boatEntityRenderState.damageWobbleStrength / 10.0f * (float)boatEntityRenderState.damageWobbleSide));
        }
        if (!boatEntityRenderState.submergedInWater && !MathHelper.approximatelyEquals(boatEntityRenderState.bubbleWobble, 0.0f)) {
            matrixStack.multiply((Quaternionfc)new Quaternionf().setAngleAxis(boatEntityRenderState.bubbleWobble * ((float)Math.PI / 180), 1.0f, 0.0f, 1.0f));
        }
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
        orderedRenderCommandQueue.submitModel(this.getModel(), boatEntityRenderState, matrixStack, this.getRenderLayer(), boatEntityRenderState.light, OverlayTexture.DEFAULT_UV, boatEntityRenderState.outlineColor, null);
        this.renderWaterMask(boatEntityRenderState, matrixStack, orderedRenderCommandQueue, boatEntityRenderState.light);
        matrixStack.pop();
        super.render(boatEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    protected void renderWaterMask(BoatEntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, int light) {
    }

    protected abstract EntityModel<BoatEntityRenderState> getModel();

    protected abstract RenderLayer getRenderLayer();

    @Override
    public BoatEntityRenderState createRenderState() {
        return new BoatEntityRenderState();
    }

    @Override
    public void updateRenderState(AbstractBoatEntity abstractBoatEntity, BoatEntityRenderState boatEntityRenderState, float f) {
        super.updateRenderState(abstractBoatEntity, boatEntityRenderState, f);
        boatEntityRenderState.yaw = abstractBoatEntity.getLerpedYaw(f);
        boatEntityRenderState.damageWobbleTicks = (float)abstractBoatEntity.getDamageWobbleTicks() - f;
        boatEntityRenderState.damageWobbleSide = abstractBoatEntity.getDamageWobbleSide();
        boatEntityRenderState.damageWobbleStrength = Math.max(abstractBoatEntity.getDamageWobbleStrength() - f, 0.0f);
        boatEntityRenderState.bubbleWobble = abstractBoatEntity.lerpBubbleWobble(f);
        boatEntityRenderState.submergedInWater = abstractBoatEntity.isSubmergedInWater();
        boatEntityRenderState.leftPaddleAngle = abstractBoatEntity.lerpPaddlePhase(0, f);
        boatEntityRenderState.rightPaddleAngle = abstractBoatEntity.lerpPaddlePhase(1, f);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
