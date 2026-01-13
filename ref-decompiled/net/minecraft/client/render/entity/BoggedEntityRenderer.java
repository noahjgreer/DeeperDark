/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.AbstractSkeletonEntityRenderer
 *  net.minecraft.client.render.entity.BoggedEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.SkeletonOverlayFeatureRenderer
 *  net.minecraft.client.render.entity.model.BoggedEntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.SkeletonEntityModel
 *  net.minecraft.client.render.entity.state.BoggedEntityRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.SkeletonEntityRenderState
 *  net.minecraft.entity.mob.AbstractSkeletonEntity
 *  net.minecraft.entity.mob.BoggedEntity
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AbstractSkeletonEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.SkeletonOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.BoggedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.client.render.entity.state.BoggedEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.SkeletonEntityRenderState;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.BoggedEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class BoggedEntityRenderer
extends AbstractSkeletonEntityRenderer<BoggedEntity, BoggedEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/skeleton/bogged.png");
    private static final Identifier OVERLAY_TEXTURE = Identifier.ofVanilla((String)"textures/entity/skeleton/bogged_overlay.png");

    public BoggedEntityRenderer(EntityRendererFactory.Context context) {
        super(context, EntityModelLayers.BOGGED_EQUIPMENT, (SkeletonEntityModel)new BoggedEntityModel(context.getPart(EntityModelLayers.BOGGED)));
        this.addFeature((FeatureRenderer)new SkeletonOverlayFeatureRenderer((FeatureRendererContext)this, context.getEntityModels(), EntityModelLayers.BOGGED_OUTER, OVERLAY_TEXTURE));
    }

    public Identifier getTexture(BoggedEntityRenderState boggedEntityRenderState) {
        return TEXTURE;
    }

    public BoggedEntityRenderState createRenderState() {
        return new BoggedEntityRenderState();
    }

    public void updateRenderState(BoggedEntity boggedEntity, BoggedEntityRenderState boggedEntityRenderState, float f) {
        super.updateRenderState((AbstractSkeletonEntity)boggedEntity, (SkeletonEntityRenderState)boggedEntityRenderState, f);
        boggedEntityRenderState.sheared = boggedEntity.isSheared();
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((BoggedEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

