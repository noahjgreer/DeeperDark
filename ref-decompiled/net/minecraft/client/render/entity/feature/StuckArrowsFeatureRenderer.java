/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.entity.ArrowEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.LivingEntityRenderer
 *  net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer
 *  net.minecraft.client.render.entity.feature.StuckObjectsFeatureRenderer
 *  net.minecraft.client.render.entity.feature.StuckObjectsFeatureRenderer$RenderPosition
 *  net.minecraft.client.render.entity.model.ArrowEntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.PlayerEntityModel
 *  net.minecraft.client.render.entity.state.PlayerEntityRenderState
 *  net.minecraft.client.render.entity.state.ProjectileEntityRenderState
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.entity.ArrowEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.StuckObjectsFeatureRenderer;
import net.minecraft.client.render.entity.model.ArrowEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class StuckArrowsFeatureRenderer<M extends PlayerEntityModel>
extends StuckObjectsFeatureRenderer<M, ProjectileEntityRenderState> {
    public StuckArrowsFeatureRenderer(LivingEntityRenderer<?, PlayerEntityRenderState, M> entityRenderer, EntityRendererFactory.Context context) {
        super(entityRenderer, (Model)new ArrowEntityModel(context.getPart(EntityModelLayers.ARROW)), (Object)new ProjectileEntityRenderState(), ArrowEntityRenderer.TEXTURE, StuckObjectsFeatureRenderer.RenderPosition.IN_CUBE);
    }

    protected int getObjectCount(PlayerEntityRenderState playerRenderState) {
        return playerRenderState.stuckArrowCount;
    }
}

