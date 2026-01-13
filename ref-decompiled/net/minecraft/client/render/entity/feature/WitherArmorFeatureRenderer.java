/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.feature.EnergySwirlOverlayFeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.WitherArmorFeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.LoadedEntityModels
 *  net.minecraft.client.render.entity.model.WitherEntityModel
 *  net.minecraft.client.render.entity.state.WitherEntityRenderState
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.EnergySwirlOverlayFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.WitherEntityModel;
import net.minecraft.client.render.entity.state.WitherEntityRenderState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WitherArmorFeatureRenderer
extends EnergySwirlOverlayFeatureRenderer<WitherEntityRenderState, WitherEntityModel> {
    private static final Identifier SKIN = Identifier.ofVanilla((String)"textures/entity/wither/wither_armor.png");
    private final WitherEntityModel model;

    public WitherArmorFeatureRenderer(FeatureRendererContext<WitherEntityRenderState, WitherEntityModel> context, LoadedEntityModels loader) {
        super(context);
        this.model = new WitherEntityModel(loader.getModelPart(EntityModelLayers.WITHER_ARMOR));
    }

    protected boolean shouldRender(WitherEntityRenderState witherEntityRenderState) {
        return witherEntityRenderState.armored;
    }

    protected float getEnergySwirlX(float partialAge) {
        return MathHelper.cos((double)(partialAge * 0.02f)) * 3.0f;
    }

    protected Identifier getEnergySwirlTexture() {
        return SKIN;
    }

    protected WitherEntityModel getEnergySwirlModel() {
        return this.model;
    }

    protected /* synthetic */ EntityModel getEnergySwirlModel() {
        return this.getEnergySwirlModel();
    }
}

