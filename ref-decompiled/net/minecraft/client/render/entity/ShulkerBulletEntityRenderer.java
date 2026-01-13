/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.EntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.ShulkerBulletEntityRenderer
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.ShulkerBulletEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.ShulkerBulletEntityRenderState
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.projectile.ShulkerBulletEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RotationAxis
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ShulkerBulletEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ShulkerBulletEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class ShulkerBulletEntityRenderer
extends EntityRenderer<ShulkerBulletEntity, ShulkerBulletEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/shulker/spark.png");
    private static final RenderLayer LAYER = RenderLayers.entityTranslucent((Identifier)TEXTURE);
    private final ShulkerBulletEntityModel model;

    public ShulkerBulletEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new ShulkerBulletEntityModel(context.getPart(EntityModelLayers.SHULKER_BULLET));
    }

    protected int getBlockLight(ShulkerBulletEntity shulkerBulletEntity, BlockPos blockPos) {
        return 15;
    }

    public void render(ShulkerBulletEntityRenderState shulkerBulletEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        matrixStack.push();
        float f = shulkerBulletEntityRenderState.age;
        matrixStack.translate(0.0f, 0.15f, 0.0f);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.sin((double)(f * 0.1f)) * 180.0f));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.cos((double)(f * 0.1f)) * 180.0f));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.sin((double)(f * 0.15f)) * 360.0f));
        matrixStack.scale(-0.5f, -0.5f, 0.5f);
        orderedRenderCommandQueue.submitModel((Model)this.model, (Object)shulkerBulletEntityRenderState, matrixStack, this.model.getLayer(TEXTURE), shulkerBulletEntityRenderState.light, OverlayTexture.DEFAULT_UV, shulkerBulletEntityRenderState.outlineColor, null);
        matrixStack.scale(1.5f, 1.5f, 1.5f);
        orderedRenderCommandQueue.getBatchingQueue(1).submitModel((Model)this.model, (Object)shulkerBulletEntityRenderState, matrixStack, LAYER, shulkerBulletEntityRenderState.light, OverlayTexture.DEFAULT_UV, 0x26FFFFFF, null, shulkerBulletEntityRenderState.outlineColor, null);
        matrixStack.pop();
        super.render((EntityRenderState)shulkerBulletEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    public ShulkerBulletEntityRenderState createRenderState() {
        return new ShulkerBulletEntityRenderState();
    }

    public void updateRenderState(ShulkerBulletEntity shulkerBulletEntity, ShulkerBulletEntityRenderState shulkerBulletEntityRenderState, float f) {
        super.updateRenderState((Entity)shulkerBulletEntity, (EntityRenderState)shulkerBulletEntityRenderState, f);
        shulkerBulletEntityRenderState.yaw = shulkerBulletEntity.getLerpedYaw(f);
        shulkerBulletEntityRenderState.pitch = shulkerBulletEntity.getLerpedPitch(f);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

