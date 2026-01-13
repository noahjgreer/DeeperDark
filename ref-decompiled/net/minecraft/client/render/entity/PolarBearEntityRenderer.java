/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.AgeableMobEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.PolarBearEntityRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.PolarBearEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.PolarBearEntityRenderState
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.PolarBearEntity
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PolarBearEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PolarBearEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class PolarBearEntityRenderer
extends AgeableMobEntityRenderer<PolarBearEntity, PolarBearEntityRenderState, PolarBearEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/bear/polarbear.png");

    public PolarBearEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new PolarBearEntityModel(context.getPart(EntityModelLayers.POLAR_BEAR)), (EntityModel)new PolarBearEntityModel(context.getPart(EntityModelLayers.POLAR_BEAR_BABY)), 0.9f);
    }

    public Identifier getTexture(PolarBearEntityRenderState polarBearEntityRenderState) {
        return TEXTURE;
    }

    public PolarBearEntityRenderState createRenderState() {
        return new PolarBearEntityRenderState();
    }

    public void updateRenderState(PolarBearEntity polarBearEntity, PolarBearEntityRenderState polarBearEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)polarBearEntity, (LivingEntityRenderState)polarBearEntityRenderState, f);
        polarBearEntityRenderState.warningAnimationProgress = polarBearEntity.getWarningAnimationProgress(f);
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((PolarBearEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

