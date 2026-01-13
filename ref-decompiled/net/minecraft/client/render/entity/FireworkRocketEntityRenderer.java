/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.item.ItemModelManager
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.EntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.FireworkRocketEntityRenderer
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.FireworkRocketEntityRenderState
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.projectile.FireworkRocketEntity
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.util.math.RotationAxis
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.FireworkRocketEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class FireworkRocketEntityRenderer
extends EntityRenderer<FireworkRocketEntity, FireworkRocketEntityRenderState> {
    private final ItemModelManager itemModelManager;

    public FireworkRocketEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemModelManager = context.getItemModelManager();
    }

    public void render(FireworkRocketEntityRenderState fireworkRocketEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        matrixStack.push();
        matrixStack.multiply((Quaternionfc)cameraRenderState.orientation);
        if (fireworkRocketEntityRenderState.shotAtAngle) {
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
        }
        fireworkRocketEntityRenderState.stack.render(matrixStack, orderedRenderCommandQueue, fireworkRocketEntityRenderState.light, OverlayTexture.DEFAULT_UV, fireworkRocketEntityRenderState.outlineColor);
        matrixStack.pop();
        super.render((EntityRenderState)fireworkRocketEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    public FireworkRocketEntityRenderState createRenderState() {
        return new FireworkRocketEntityRenderState();
    }

    public void updateRenderState(FireworkRocketEntity fireworkRocketEntity, FireworkRocketEntityRenderState fireworkRocketEntityRenderState, float f) {
        super.updateRenderState((Entity)fireworkRocketEntity, (EntityRenderState)fireworkRocketEntityRenderState, f);
        fireworkRocketEntityRenderState.shotAtAngle = fireworkRocketEntity.wasShotAtAngle();
        this.itemModelManager.updateForNonLivingEntity(fireworkRocketEntityRenderState.stack, fireworkRocketEntity.getStack(), ItemDisplayContext.GROUND, (Entity)fireworkRocketEntity);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

