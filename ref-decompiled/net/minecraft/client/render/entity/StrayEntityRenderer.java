/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.AbstractSkeletonEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.StrayEntityRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.SkeletonOverlayFeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.SkeletonEntityRenderState
 *  net.minecraft.entity.mob.StrayEntity
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
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.SkeletonEntityRenderState;
import net.minecraft.entity.mob.StrayEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class StrayEntityRenderer
extends AbstractSkeletonEntityRenderer<StrayEntity, SkeletonEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/skeleton/stray.png");
    private static final Identifier OVERLAY_TEXTURE = Identifier.ofVanilla((String)"textures/entity/skeleton/stray_overlay.png");

    public StrayEntityRenderer(EntityRendererFactory.Context context) {
        super(context, EntityModelLayers.STRAY, EntityModelLayers.STRAY_EQUIPMENT);
        this.addFeature((FeatureRenderer)new SkeletonOverlayFeatureRenderer((FeatureRendererContext)this, context.getEntityModels(), EntityModelLayers.STRAY_OUTER, OVERLAY_TEXTURE));
    }

    public Identifier getTexture(SkeletonEntityRenderState skeletonEntityRenderState) {
        return TEXTURE;
    }

    public SkeletonEntityRenderState createRenderState() {
        return new SkeletonEntityRenderState();
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

