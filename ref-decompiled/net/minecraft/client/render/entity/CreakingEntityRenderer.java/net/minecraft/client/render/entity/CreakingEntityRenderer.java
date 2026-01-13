/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.EmissiveFeatureRenderer;
import net.minecraft.client.render.entity.model.CreakingEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.CreakingEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.mob.CreakingEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class CreakingEntityRenderer<T extends CreakingEntity>
extends MobEntityRenderer<T, CreakingEntityRenderState, CreakingEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/creaking/creaking.png");
    private static final Identifier EYES_TEXTURE = Identifier.ofVanilla("textures/entity/creaking/creaking_eyes.png");

    public CreakingEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new CreakingEntityModel(context.getPart(EntityModelLayers.CREAKING)), 0.6f);
        this.addFeature(new EmissiveFeatureRenderer<CreakingEntityRenderState, CreakingEntityModel>(this, state -> EYES_TEXTURE, (state, tickProgress) -> state.glowingEyes ? 1.0f : 0.0f, new CreakingEntityModel(context.getPart(EntityModelLayers.CREAKING_EYES)), RenderLayers::eyes, true));
    }

    @Override
    public Identifier getTexture(CreakingEntityRenderState creakingEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public CreakingEntityRenderState createRenderState() {
        return new CreakingEntityRenderState();
    }

    @Override
    public void updateRenderState(T creakingEntity, CreakingEntityRenderState creakingEntityRenderState, float f) {
        super.updateRenderState(creakingEntity, creakingEntityRenderState, f);
        creakingEntityRenderState.attackAnimationState.copyFrom(((CreakingEntity)creakingEntity).attackAnimationState);
        creakingEntityRenderState.invulnerableAnimationState.copyFrom(((CreakingEntity)creakingEntity).invulnerableAnimationState);
        creakingEntityRenderState.crumblingAnimationState.copyFrom(((CreakingEntity)creakingEntity).crumblingAnimationState);
        if (((CreakingEntity)creakingEntity).isCrumbling()) {
            creakingEntityRenderState.deathTime = 0.0f;
            creakingEntityRenderState.hurt = false;
            creakingEntityRenderState.glowingEyes = ((CreakingEntity)creakingEntity).hasGlowingEyesWhileCrumbling();
        } else {
            creakingEntityRenderState.glowingEyes = ((CreakingEntity)creakingEntity).isActive();
        }
        creakingEntityRenderState.unrooted = ((CreakingEntity)creakingEntity).isUnrooted();
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((CreakingEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
