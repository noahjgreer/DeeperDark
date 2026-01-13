/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.AbstractHoglinEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.HoglinEntityRenderer
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.state.HoglinEntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.entity.mob.HoglinEntity
 *  net.minecraft.entity.mob.MobEntity
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AbstractHoglinEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.HoglinEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class HoglinEntityRenderer
extends AbstractHoglinEntityRenderer<HoglinEntity> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/hoglin/hoglin.png");

    public HoglinEntityRenderer(EntityRendererFactory.Context context) {
        super(context, EntityModelLayers.HOGLIN, EntityModelLayers.HOGLIN_BABY, 0.7f);
    }

    public Identifier getTexture(HoglinEntityRenderState hoglinEntityRenderState) {
        return TEXTURE;
    }

    public void updateRenderState(HoglinEntity hoglinEntity, HoglinEntityRenderState hoglinEntityRenderState, float f) {
        super.updateRenderState((MobEntity)hoglinEntity, hoglinEntityRenderState, f);
        hoglinEntityRenderState.canConvert = hoglinEntity.canConvert();
    }

    protected boolean isShaking(HoglinEntityRenderState hoglinEntityRenderState) {
        return super.isShaking((LivingEntityRenderState)hoglinEntityRenderState) || hoglinEntityRenderState.canConvert;
    }

    protected /* synthetic */ boolean isShaking(LivingEntityRenderState state) {
        return this.isShaking((HoglinEntityRenderState)state);
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((HoglinEntityRenderState)state);
    }
}

