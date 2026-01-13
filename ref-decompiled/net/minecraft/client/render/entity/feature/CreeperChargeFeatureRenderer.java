/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.feature.CreeperChargeFeatureRenderer
 *  net.minecraft.client.render.entity.feature.EnergySwirlOverlayFeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.model.CreeperEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.LoadedEntityModels
 *  net.minecraft.client.render.entity.state.CreeperEntityRenderState
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.EnergySwirlOverlayFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.state.CreeperEntityRenderState;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class CreeperChargeFeatureRenderer
extends EnergySwirlOverlayFeatureRenderer<CreeperEntityRenderState, CreeperEntityModel> {
    private static final Identifier SKIN = Identifier.ofVanilla((String)"textures/entity/creeper/creeper_armor.png");
    private final CreeperEntityModel model;

    public CreeperChargeFeatureRenderer(FeatureRendererContext<CreeperEntityRenderState, CreeperEntityModel> context, LoadedEntityModels loader) {
        super(context);
        this.model = new CreeperEntityModel(loader.getModelPart(EntityModelLayers.CREEPER_ARMOR));
    }

    protected boolean shouldRender(CreeperEntityRenderState creeperEntityRenderState) {
        return creeperEntityRenderState.charged;
    }

    protected float getEnergySwirlX(float partialAge) {
        return partialAge * 0.01f;
    }

    protected Identifier getEnergySwirlTexture() {
        return SKIN;
    }

    protected CreeperEntityModel getEnergySwirlModel() {
        return this.model;
    }

    protected /* synthetic */ EntityModel getEnergySwirlModel() {
        return this.getEnergySwirlModel();
    }
}

