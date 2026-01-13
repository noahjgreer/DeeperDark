/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.GhastEntityRenderer
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.GhastEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.GhastEntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.mob.GhastEntity
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.GhastEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.GhastEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class GhastEntityRenderer
extends MobEntityRenderer<GhastEntity, GhastEntityRenderState, GhastEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/ghast/ghast.png");
    private static final Identifier SHOOTING_TEXTURE = Identifier.ofVanilla((String)"textures/entity/ghast/ghast_shooting.png");

    public GhastEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new GhastEntityModel(context.getPart(EntityModelLayers.GHAST)), 1.5f);
    }

    public Identifier getTexture(GhastEntityRenderState ghastEntityRenderState) {
        if (ghastEntityRenderState.shooting) {
            return SHOOTING_TEXTURE;
        }
        return TEXTURE;
    }

    public GhastEntityRenderState createRenderState() {
        return new GhastEntityRenderState();
    }

    public void updateRenderState(GhastEntity ghastEntity, GhastEntityRenderState ghastEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)ghastEntity, (LivingEntityRenderState)ghastEntityRenderState, f);
        ghastEntityRenderState.shooting = ghastEntity.isShooting();
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((GhastEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

