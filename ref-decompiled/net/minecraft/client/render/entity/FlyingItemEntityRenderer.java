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
 *  net.minecraft.client.render.entity.FlyingItemEntityRenderer
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.FlyingItemEntityRenderState
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.FlyingItemEntity
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.util.math.BlockPos
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
import net.minecraft.client.render.entity.state.FlyingItemEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.math.BlockPos;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class FlyingItemEntityRenderer<T extends Entity>
extends EntityRenderer<T, FlyingItemEntityRenderState> {
    private final ItemModelManager itemModelManager;
    private final float scale;
    private final boolean lit;

    public FlyingItemEntityRenderer(EntityRendererFactory.Context ctx, float scale, boolean lit) {
        super(ctx);
        this.itemModelManager = ctx.getItemModelManager();
        this.scale = scale;
        this.lit = lit;
    }

    public FlyingItemEntityRenderer(EntityRendererFactory.Context context) {
        this(context, 1.0f, false);
    }

    protected int getBlockLight(T entity, BlockPos pos) {
        return this.lit ? 15 : super.getBlockLight(entity, pos);
    }

    public void render(FlyingItemEntityRenderState flyingItemEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        matrixStack.push();
        matrixStack.scale(this.scale, this.scale, this.scale);
        matrixStack.multiply((Quaternionfc)cameraRenderState.orientation);
        flyingItemEntityRenderState.itemRenderState.render(matrixStack, orderedRenderCommandQueue, flyingItemEntityRenderState.light, OverlayTexture.DEFAULT_UV, flyingItemEntityRenderState.outlineColor);
        matrixStack.pop();
        super.render((EntityRenderState)flyingItemEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    public FlyingItemEntityRenderState createRenderState() {
        return new FlyingItemEntityRenderState();
    }

    public void updateRenderState(T entity, FlyingItemEntityRenderState flyingItemEntityRenderState, float f) {
        super.updateRenderState(entity, (EntityRenderState)flyingItemEntityRenderState, f);
        this.itemModelManager.updateForNonLivingEntity(flyingItemEntityRenderState.itemRenderState, ((FlyingItemEntity)entity).getStack(), ItemDisplayContext.GROUND, entity);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

