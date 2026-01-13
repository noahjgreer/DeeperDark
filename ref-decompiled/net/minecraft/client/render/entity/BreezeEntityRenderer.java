/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.BreezeEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.feature.BreezeEyesFeatureRenderer
 *  net.minecraft.client.render.entity.feature.BreezeWindFeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.model.BreezeEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.state.BreezeEntityRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.mob.BreezeEntity
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.BreezeEyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.BreezeWindFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BreezeEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.BreezeEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.BreezeEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class BreezeEntityRenderer
extends MobEntityRenderer<BreezeEntity, BreezeEntityRenderState, BreezeEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/breeze/breeze.png");

    public BreezeEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new BreezeEntityModel(context.getPart(EntityModelLayers.BREEZE)), 0.5f);
        this.addFeature((FeatureRenderer)new BreezeWindFeatureRenderer((FeatureRendererContext)this, context.getEntityModels()));
        this.addFeature((FeatureRenderer)new BreezeEyesFeatureRenderer((FeatureRendererContext)this, context.getEntityModels()));
    }

    public Identifier getTexture(BreezeEntityRenderState breezeEntityRenderState) {
        return TEXTURE;
    }

    public BreezeEntityRenderState createRenderState() {
        return new BreezeEntityRenderState();
    }

    public void updateRenderState(BreezeEntity breezeEntity, BreezeEntityRenderState breezeEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)breezeEntity, (LivingEntityRenderState)breezeEntityRenderState, f);
        breezeEntityRenderState.idleAnimationState.copyFrom(breezeEntity.idleAnimationState);
        breezeEntityRenderState.shootingAnimationState.copyFrom(breezeEntity.shootingAnimationState);
        breezeEntityRenderState.slidingAnimationState.copyFrom(breezeEntity.slidingAnimationState);
        breezeEntityRenderState.slidingBackAnimationState.copyFrom(breezeEntity.slidingBackAnimationState);
        breezeEntityRenderState.inhalingAnimationState.copyFrom(breezeEntity.inhalingAnimationState);
        breezeEntityRenderState.longJumpingAnimationState.copyFrom(breezeEntity.longJumpingAnimationState);
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((BreezeEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

