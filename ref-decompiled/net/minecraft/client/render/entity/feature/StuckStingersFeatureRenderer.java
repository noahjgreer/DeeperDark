/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.LivingEntityRenderer
 *  net.minecraft.client.render.entity.feature.StuckObjectsFeatureRenderer
 *  net.minecraft.client.render.entity.feature.StuckObjectsFeatureRenderer$RenderPosition
 *  net.minecraft.client.render.entity.feature.StuckStingersFeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.PlayerEntityModel
 *  net.minecraft.client.render.entity.model.StingerModel
 *  net.minecraft.client.render.entity.state.PlayerEntityRenderState
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Unit
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.StuckObjectsFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.model.StingerModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;

@Environment(value=EnvType.CLIENT)
public class StuckStingersFeatureRenderer<M extends PlayerEntityModel>
extends StuckObjectsFeatureRenderer<M, Unit> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/bee/bee_stinger.png");

    public StuckStingersFeatureRenderer(LivingEntityRenderer<?, PlayerEntityRenderState, M> entityRenderer, EntityRendererFactory.Context context) {
        super(entityRenderer, (Model)new StingerModel(context.getPart(EntityModelLayers.BEE_STINGER)), (Object)Unit.INSTANCE, TEXTURE, StuckObjectsFeatureRenderer.RenderPosition.ON_SURFACE);
    }

    protected int getObjectCount(PlayerEntityRenderState playerRenderState) {
        return playerRenderState.stingerCount;
    }
}

