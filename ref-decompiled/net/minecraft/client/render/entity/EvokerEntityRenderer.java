/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.EvokerEntityRenderer
 *  net.minecraft.client.render.entity.IllagerEntityRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.IllagerEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.EvokerEntityRenderState
 *  net.minecraft.client.render.entity.state.IllagerEntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.entity.mob.SpellcastingIllagerEntity
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IllagerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.EvokerEntityRenderState;
import net.minecraft.client.render.entity.state.IllagerEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.mob.SpellcastingIllagerEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class EvokerEntityRenderer<T extends SpellcastingIllagerEntity>
extends IllagerEntityRenderer<T, EvokerEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/illager/evoker.png");

    public EvokerEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new IllagerEntityModel(context.getPart(EntityModelLayers.EVOKER)), 0.5f);
        this.addFeature((FeatureRenderer)new /* Unavailable Anonymous Inner Class!! */);
    }

    public Identifier getTexture(EvokerEntityRenderState evokerEntityRenderState) {
        return TEXTURE;
    }

    public EvokerEntityRenderState createRenderState() {
        return new EvokerEntityRenderState();
    }

    public void updateRenderState(T spellcastingIllagerEntity, EvokerEntityRenderState evokerEntityRenderState, float f) {
        super.updateRenderState(spellcastingIllagerEntity, (IllagerEntityRenderState)evokerEntityRenderState, f);
        evokerEntityRenderState.spellcasting = spellcastingIllagerEntity.isSpellcasting();
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((EvokerEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

