/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.item.ItemModelManager
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.IllagerEntityRenderer
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.HeadFeatureRenderer
 *  net.minecraft.client.render.entity.model.IllagerEntityModel
 *  net.minecraft.client.render.entity.state.ArmedEntityRenderState
 *  net.minecraft.client.render.entity.state.IllagerEntityRenderState
 *  net.minecraft.entity.mob.IllagerEntity
 *  net.minecraft.entity.mob.IllagerEntity$State
 *  net.minecraft.item.CrossbowItem
 *  net.minecraft.item.ItemStack
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.entity.state.IllagerEntityRenderState;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;

@Environment(value=EnvType.CLIENT)
public abstract class IllagerEntityRenderer<T extends IllagerEntity, S extends IllagerEntityRenderState>
extends MobEntityRenderer<T, S, IllagerEntityModel<S>> {
    protected IllagerEntityRenderer(EntityRendererFactory.Context ctx, IllagerEntityModel<S> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
        this.addFeature((FeatureRenderer)new HeadFeatureRenderer((FeatureRendererContext)this, ctx.getEntityModels(), ctx.getPlayerSkinCache()));
    }

    public void updateRenderState(T illagerEntity, S illagerEntityRenderState, float f) {
        super.updateRenderState(illagerEntity, illagerEntityRenderState, f);
        ArmedEntityRenderState.updateRenderState(illagerEntity, illagerEntityRenderState, (ItemModelManager)this.itemModelResolver, (float)f);
        ((IllagerEntityRenderState)illagerEntityRenderState).hasVehicle = illagerEntity.hasVehicle();
        ((IllagerEntityRenderState)illagerEntityRenderState).illagerMainArm = illagerEntity.getMainArm();
        ((IllagerEntityRenderState)illagerEntityRenderState).illagerState = illagerEntity.getState();
        ((IllagerEntityRenderState)illagerEntityRenderState).crossbowPullTime = ((IllagerEntityRenderState)illagerEntityRenderState).illagerState == IllagerEntity.State.CROSSBOW_CHARGE ? CrossbowItem.getPullTime((ItemStack)illagerEntity.getActiveItem(), illagerEntity) : 0;
        ((IllagerEntityRenderState)illagerEntityRenderState).itemUseTime = illagerEntity.getItemUseTime(f);
        ((IllagerEntityRenderState)illagerEntityRenderState).handSwingProgress = illagerEntity.getHandSwingProgress(f);
        ((IllagerEntityRenderState)illagerEntityRenderState).attacking = illagerEntity.isAttacking();
    }
}

