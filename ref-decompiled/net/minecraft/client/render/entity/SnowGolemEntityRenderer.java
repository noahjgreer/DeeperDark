/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.SnowGolemEntityRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.SnowGolemPumpkinFeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.SnowGolemEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.SnowGolemEntityRenderState
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.SnowGolemEntity
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.SnowGolemPumpkinFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SnowGolemEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.SnowGolemEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SnowGolemEntityRenderer
extends MobEntityRenderer<SnowGolemEntity, SnowGolemEntityRenderState, SnowGolemEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/snow_golem.png");

    public SnowGolemEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new SnowGolemEntityModel(context.getPart(EntityModelLayers.SNOW_GOLEM)), 0.5f);
        this.addFeature((FeatureRenderer)new SnowGolemPumpkinFeatureRenderer((FeatureRendererContext)this, context.getBlockRenderManager()));
    }

    public Identifier getTexture(SnowGolemEntityRenderState snowGolemEntityRenderState) {
        return TEXTURE;
    }

    public SnowGolemEntityRenderState createRenderState() {
        return new SnowGolemEntityRenderState();
    }

    public void updateRenderState(SnowGolemEntity snowGolemEntity, SnowGolemEntityRenderState snowGolemEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)snowGolemEntity, (LivingEntityRenderState)snowGolemEntityRenderState, f);
        snowGolemEntityRenderState.hasPumpkin = snowGolemEntity.hasPumpkin();
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((SnowGolemEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

