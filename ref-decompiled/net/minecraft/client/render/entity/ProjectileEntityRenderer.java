/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.EntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.ProjectileEntityRenderer
 *  net.minecraft.client.render.entity.model.ArrowEntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.state.ProjectileEntityRenderState
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.projectile.PersistentProjectileEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.RotationAxis
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.ArrowEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public abstract class ProjectileEntityRenderer<T extends PersistentProjectileEntity, S extends ProjectileEntityRenderState>
extends EntityRenderer<T, S> {
    private final ArrowEntityModel model;

    public ProjectileEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new ArrowEntityModel(context.getPart(EntityModelLayers.ARROW));
    }

    public void render(S projectileEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        matrixStack.push();
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(((ProjectileEntityRenderState)projectileEntityRenderState).yaw - 90.0f));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(((ProjectileEntityRenderState)projectileEntityRenderState).pitch));
        orderedRenderCommandQueue.submitModel((Model)this.model, projectileEntityRenderState, matrixStack, RenderLayers.entityCutout((Identifier)this.getTexture(projectileEntityRenderState)), ((ProjectileEntityRenderState)projectileEntityRenderState).light, OverlayTexture.DEFAULT_UV, ((ProjectileEntityRenderState)projectileEntityRenderState).outlineColor, null);
        matrixStack.pop();
        super.render(projectileEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    protected abstract Identifier getTexture(S var1);

    public void updateRenderState(T persistentProjectileEntity, S projectileEntityRenderState, float f) {
        super.updateRenderState(persistentProjectileEntity, projectileEntityRenderState, f);
        ((ProjectileEntityRenderState)projectileEntityRenderState).pitch = persistentProjectileEntity.getLerpedPitch(f);
        ((ProjectileEntityRenderState)projectileEntityRenderState).yaw = persistentProjectileEntity.getLerpedYaw(f);
        ((ProjectileEntityRenderState)projectileEntityRenderState).shake = (float)((PersistentProjectileEntity)persistentProjectileEntity).shake - f;
    }
}

