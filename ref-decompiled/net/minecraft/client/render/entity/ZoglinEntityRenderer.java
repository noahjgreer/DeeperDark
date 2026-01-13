/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.AbstractHoglinEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.ZoglinEntityRenderer
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.state.HoglinEntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.entity.mob.ZoglinEntity
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
import net.minecraft.entity.mob.ZoglinEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ZoglinEntityRenderer
extends AbstractHoglinEntityRenderer<ZoglinEntity> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/hoglin/zoglin.png");

    public ZoglinEntityRenderer(EntityRendererFactory.Context context) {
        super(context, EntityModelLayers.ZOGLIN, EntityModelLayers.ZOGLIN_BABY, 0.7f);
    }

    public Identifier getTexture(HoglinEntityRenderState hoglinEntityRenderState) {
        return TEXTURE;
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((HoglinEntityRenderState)state);
    }
}

