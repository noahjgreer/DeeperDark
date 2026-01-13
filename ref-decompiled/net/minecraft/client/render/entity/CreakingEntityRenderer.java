/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.entity.CreakingEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.feature.EmissiveFeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.model.CreakingEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.state.CreakingEntityRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.entity.mob.CreakingEntity
 *  net.minecraft.util.Identifier
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
import net.minecraft.client.render.entity.model.CreakingEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.CreakingEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.mob.CreakingEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class CreakingEntityRenderer<T extends CreakingEntity>
extends MobEntityRenderer<T, CreakingEntityRenderState, CreakingEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/creaking/creaking.png");
    private static final Identifier EYES_TEXTURE = Identifier.ofVanilla((String)"textures/entity/creaking/creaking_eyes.png");

    public CreakingEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new CreakingEntityModel(context.getPart(EntityModelLayers.CREAKING)), 0.6f);
        this.addFeature((FeatureRenderer)new EmissiveFeatureRenderer((FeatureRendererContext)this, state -> EYES_TEXTURE, (state, tickProgress) -> state.glowingEyes ? 1.0f : 0.0f, (EntityModel)new CreakingEntityModel(context.getPart(EntityModelLayers.CREAKING_EYES)), RenderLayers::eyes, true));
    }

    public Identifier getTexture(CreakingEntityRenderState creakingEntityRenderState) {
        return TEXTURE;
    }

    public CreakingEntityRenderState createRenderState() {
        return new CreakingEntityRenderState();
    }

    public void updateRenderState(T creakingEntity, CreakingEntityRenderState creakingEntityRenderState, float f) {
        super.updateRenderState(creakingEntity, (LivingEntityRenderState)creakingEntityRenderState, f);
        creakingEntityRenderState.attackAnimationState.copyFrom(((CreakingEntity)creakingEntity).attackAnimationState);
        creakingEntityRenderState.invulnerableAnimationState.copyFrom(((CreakingEntity)creakingEntity).invulnerableAnimationState);
        creakingEntityRenderState.crumblingAnimationState.copyFrom(((CreakingEntity)creakingEntity).crumblingAnimationState);
        if (creakingEntity.isCrumbling()) {
            creakingEntityRenderState.deathTime = 0.0f;
            creakingEntityRenderState.hurt = false;
            creakingEntityRenderState.glowingEyes = creakingEntity.hasGlowingEyesWhileCrumbling();
        } else {
            creakingEntityRenderState.glowingEyes = creakingEntity.isActive();
        }
        creakingEntityRenderState.unrooted = creakingEntity.isUnrooted();
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((CreakingEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

