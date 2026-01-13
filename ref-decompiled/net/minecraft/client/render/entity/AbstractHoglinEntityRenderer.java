/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.AbstractHoglinEntityRenderer
 *  net.minecraft.client.render.entity.AgeableMobEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayer
 *  net.minecraft.client.render.entity.model.HoglinEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.HoglinEntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.entity.mob.Hoglin
 *  net.minecraft.entity.mob.MobEntity
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.HoglinEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.HoglinEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.mob.Hoglin;
import net.minecraft.entity.mob.MobEntity;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractHoglinEntityRenderer<T extends MobEntity>
extends AgeableMobEntityRenderer<T, HoglinEntityRenderState, HoglinEntityModel> {
    public AbstractHoglinEntityRenderer(EntityRendererFactory.Context context, EntityModelLayer layer, EntityModelLayer babyLayer, float scale) {
        super(context, (EntityModel)new HoglinEntityModel(context.getPart(layer)), (EntityModel)new HoglinEntityModel(context.getPart(babyLayer)), scale);
    }

    public HoglinEntityRenderState createRenderState() {
        return new HoglinEntityRenderState();
    }

    public void updateRenderState(T mobEntity, HoglinEntityRenderState hoglinEntityRenderState, float f) {
        super.updateRenderState(mobEntity, (LivingEntityRenderState)hoglinEntityRenderState, f);
        hoglinEntityRenderState.movementCooldownTicks = ((Hoglin)mobEntity).getMovementCooldownTicks();
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

