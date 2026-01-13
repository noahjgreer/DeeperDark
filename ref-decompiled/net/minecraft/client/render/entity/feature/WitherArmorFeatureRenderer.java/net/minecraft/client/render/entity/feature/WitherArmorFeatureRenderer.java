/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
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
    private static final Identifier SKIN = Identifier.ofVanilla("textures/entity/wither/wither_armor.png");
    private final WitherEntityModel model;

    public WitherArmorFeatureRenderer(FeatureRendererContext<WitherEntityRenderState, WitherEntityModel> context, LoadedEntityModels loader) {
        super(context);
        this.model = new WitherEntityModel(loader.getModelPart(EntityModelLayers.WITHER_ARMOR));
    }

    @Override
    protected boolean shouldRender(WitherEntityRenderState witherEntityRenderState) {
        return witherEntityRenderState.armored;
    }

    @Override
    protected float getEnergySwirlX(float partialAge) {
        return MathHelper.cos(partialAge * 0.02f) * 3.0f;
    }

    @Override
    protected Identifier getEnergySwirlTexture() {
        return SKIN;
    }

    @Override
    protected WitherEntityModel getEnergySwirlModel() {
        return this.model;
    }

    @Override
    protected /* synthetic */ EntityModel getEnergySwirlModel() {
        return this.getEnergySwirlModel();
    }
}
