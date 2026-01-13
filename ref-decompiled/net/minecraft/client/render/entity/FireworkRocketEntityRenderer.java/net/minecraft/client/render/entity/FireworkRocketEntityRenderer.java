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
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.FireworkRocketEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
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

    @Override
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
        super.render(fireworkRocketEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    @Override
    public FireworkRocketEntityRenderState createRenderState() {
        return new FireworkRocketEntityRenderState();
    }

    @Override
    public void updateRenderState(FireworkRocketEntity fireworkRocketEntity, FireworkRocketEntityRenderState fireworkRocketEntityRenderState, float f) {
        super.updateRenderState(fireworkRocketEntity, fireworkRocketEntityRenderState, f);
        fireworkRocketEntityRenderState.shotAtAngle = fireworkRocketEntity.wasShotAtAngle();
        this.itemModelManager.updateForNonLivingEntity(fireworkRocketEntityRenderState.stack, fireworkRocketEntity.getStack(), ItemDisplayContext.GROUND, fireworkRocketEntity);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
