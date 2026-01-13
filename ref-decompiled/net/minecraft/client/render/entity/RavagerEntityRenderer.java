/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.RavagerEntityRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.RavagerEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.RavagerEntityRenderState
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.mob.RavagerEntity
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.RavagerEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.RavagerEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class RavagerEntityRenderer
extends MobEntityRenderer<RavagerEntity, RavagerEntityRenderState, RavagerEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/illager/ravager.png");

    public RavagerEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new RavagerEntityModel(context.getPart(EntityModelLayers.RAVAGER)), 1.1f);
    }

    public Identifier getTexture(RavagerEntityRenderState ravagerEntityRenderState) {
        return TEXTURE;
    }

    public RavagerEntityRenderState createRenderState() {
        return new RavagerEntityRenderState();
    }

    public void updateRenderState(RavagerEntity ravagerEntity, RavagerEntityRenderState ravagerEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)ravagerEntity, (LivingEntityRenderState)ravagerEntityRenderState, f);
        ravagerEntityRenderState.stunTick = (float)ravagerEntity.getStunTick() > 0.0f ? (float)ravagerEntity.getStunTick() - f : 0.0f;
        ravagerEntityRenderState.attackTick = (float)ravagerEntity.getAttackTick() > 0.0f ? (float)ravagerEntity.getAttackTick() - f : 0.0f;
        ravagerEntityRenderState.roarTick = ravagerEntity.getRoarTick() > 0 ? ((float)(20 - ravagerEntity.getRoarTick()) + f) / 20.0f : 0.0f;
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((RavagerEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

