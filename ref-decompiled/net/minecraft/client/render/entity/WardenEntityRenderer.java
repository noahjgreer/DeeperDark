/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.WardenEntityRenderer
 *  net.minecraft.client.render.entity.feature.EmissiveFeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.WardenEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.WardenEntityRenderState
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.mob.WardenEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.EmissiveFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.WardenEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.WardenEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WardenEntityRenderer
extends MobEntityRenderer<WardenEntity, WardenEntityRenderState, WardenEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/warden/warden.png");
    private static final Identifier BIOLUMINESCENT_LAYER_TEXTURE = Identifier.ofVanilla((String)"textures/entity/warden/warden_bioluminescent_layer.png");
    private static final Identifier HEART_TEXTURE = Identifier.ofVanilla((String)"textures/entity/warden/warden_heart.png");
    private static final Identifier PULSATING_SPOTS_1_TEXTURE = Identifier.ofVanilla((String)"textures/entity/warden/warden_pulsating_spots_1.png");
    private static final Identifier PULSATING_SPOTS_2_TEXTURE = Identifier.ofVanilla((String)"textures/entity/warden/warden_pulsating_spots_2.png");

    public WardenEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new WardenEntityModel(context.getPart(EntityModelLayers.WARDEN)), 0.9f);
        WardenEntityModel wardenEntityModel = new WardenEntityModel(context.getPart(EntityModelLayers.WARDEN_BIOLUMINESCENT));
        WardenEntityModel wardenEntityModel2 = new WardenEntityModel(context.getPart(EntityModelLayers.WARDEN_PULSATING_SPOTS));
        WardenEntityModel wardenEntityModel3 = new WardenEntityModel(context.getPart(EntityModelLayers.WARDEN_TENDRILS));
        WardenEntityModel wardenEntityModel4 = new WardenEntityModel(context.getPart(EntityModelLayers.WARDEN_HEART));
        this.addFeature((FeatureRenderer)new EmissiveFeatureRenderer((FeatureRendererContext)this, state -> BIOLUMINESCENT_LAYER_TEXTURE, (state, tickProgress) -> 1.0f, (EntityModel)wardenEntityModel, RenderLayers::entityTranslucentEmissive, false));
        this.addFeature((FeatureRenderer)new EmissiveFeatureRenderer((FeatureRendererContext)this, state -> PULSATING_SPOTS_1_TEXTURE, (state, tickProgress) -> Math.max(0.0f, MathHelper.cos((double)(tickProgress * 0.045f)) * 0.25f), (EntityModel)wardenEntityModel2, RenderLayers::entityTranslucentEmissive, false));
        this.addFeature((FeatureRenderer)new EmissiveFeatureRenderer((FeatureRendererContext)this, state -> PULSATING_SPOTS_2_TEXTURE, (state, tickProgress) -> Math.max(0.0f, MathHelper.cos((double)(tickProgress * 0.045f + (float)Math.PI)) * 0.25f), (EntityModel)wardenEntityModel2, RenderLayers::entityTranslucentEmissive, false));
        this.addFeature((FeatureRenderer)new EmissiveFeatureRenderer((FeatureRendererContext)this, state -> TEXTURE, (state, tickProgress) -> state.tendrilAlpha, (EntityModel)wardenEntityModel3, RenderLayers::entityTranslucentEmissive, false));
        this.addFeature((FeatureRenderer)new EmissiveFeatureRenderer((FeatureRendererContext)this, state -> HEART_TEXTURE, (state, tickProgress) -> state.heartAlpha, (EntityModel)wardenEntityModel4, RenderLayers::entityTranslucentEmissive, false));
    }

    public Identifier getTexture(WardenEntityRenderState wardenEntityRenderState) {
        return TEXTURE;
    }

    public WardenEntityRenderState createRenderState() {
        return new WardenEntityRenderState();
    }

    public void updateRenderState(WardenEntity wardenEntity, WardenEntityRenderState wardenEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)wardenEntity, (LivingEntityRenderState)wardenEntityRenderState, f);
        wardenEntityRenderState.tendrilAlpha = wardenEntity.getTendrilAlpha(f);
        wardenEntityRenderState.heartAlpha = wardenEntity.getHeartAlpha(f);
        wardenEntityRenderState.roaringAnimationState.copyFrom(wardenEntity.roaringAnimationState);
        wardenEntityRenderState.sniffingAnimationState.copyFrom(wardenEntity.sniffingAnimationState);
        wardenEntityRenderState.emergingAnimationState.copyFrom(wardenEntity.emergingAnimationState);
        wardenEntityRenderState.diggingAnimationState.copyFrom(wardenEntity.diggingAnimationState);
        wardenEntityRenderState.attackingAnimationState.copyFrom(wardenEntity.attackingAnimationState);
        wardenEntityRenderState.chargingSonicBoomAnimationState.copyFrom(wardenEntity.chargingSonicBoomAnimationState);
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((WardenEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

