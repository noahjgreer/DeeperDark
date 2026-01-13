/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.BatEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.model.BatEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.state.BatEntityRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.BatEntity
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.BatEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.BatEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class BatEntityRenderer
extends MobEntityRenderer<BatEntity, BatEntityRenderState, BatEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/bat.png");

    public BatEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new BatEntityModel(context.getPart(EntityModelLayers.BAT)), 0.25f);
    }

    public Identifier getTexture(BatEntityRenderState batEntityRenderState) {
        return TEXTURE;
    }

    public BatEntityRenderState createRenderState() {
        return new BatEntityRenderState();
    }

    public void updateRenderState(BatEntity batEntity, BatEntityRenderState batEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)batEntity, (LivingEntityRenderState)batEntityRenderState, f);
        batEntityRenderState.roosting = batEntity.isRoosting();
        batEntityRenderState.flyingAnimationState.copyFrom(batEntity.flyingAnimationState);
        batEntityRenderState.roostingAnimationState.copyFrom(batEntity.roostingAnimationState);
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((BatEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

