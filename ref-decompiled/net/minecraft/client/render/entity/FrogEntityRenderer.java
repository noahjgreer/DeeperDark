/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.FrogEntityRenderer
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.FrogEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.FrogEntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.FrogEntity
 *  net.minecraft.entity.passive.FrogVariant
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.FrogEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.FrogEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class FrogEntityRenderer
extends MobEntityRenderer<FrogEntity, FrogEntityRenderState, FrogEntityModel> {
    public FrogEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new FrogEntityModel(context.getPart(EntityModelLayers.FROG)), 0.3f);
    }

    public Identifier getTexture(FrogEntityRenderState frogEntityRenderState) {
        return frogEntityRenderState.texture;
    }

    public FrogEntityRenderState createRenderState() {
        return new FrogEntityRenderState();
    }

    public void updateRenderState(FrogEntity frogEntity, FrogEntityRenderState frogEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)frogEntity, (LivingEntityRenderState)frogEntityRenderState, f);
        frogEntityRenderState.insideWaterOrBubbleColumn = frogEntity.isTouchingWater();
        frogEntityRenderState.longJumpingAnimationState.copyFrom(frogEntity.longJumpingAnimationState);
        frogEntityRenderState.croakingAnimationState.copyFrom(frogEntity.croakingAnimationState);
        frogEntityRenderState.usingTongueAnimationState.copyFrom(frogEntity.usingTongueAnimationState);
        frogEntityRenderState.idlingInWaterAnimationState.copyFrom(frogEntity.idlingInWaterAnimationState);
        frogEntityRenderState.texture = ((FrogVariant)frogEntity.getVariant().value()).assetInfo().texturePath();
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((FrogEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

