/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.AbstractHorseEntityRenderer
 *  net.minecraft.client.render.entity.AgeableMobEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.state.LivingHorseEntityRenderState
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.passive.AbstractHorseEntity
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingHorseEntityRenderState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.AbstractHorseEntity;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractHorseEntityRenderer<T extends AbstractHorseEntity, S extends LivingHorseEntityRenderState, M extends EntityModel<? super S>>
extends AgeableMobEntityRenderer<T, S, M> {
    public AbstractHorseEntityRenderer(EntityRendererFactory.Context context, M model, M babyModel) {
        super(context, model, babyModel, 0.75f);
    }

    public void updateRenderState(T abstractHorseEntity, S livingHorseEntityRenderState, float f) {
        super.updateRenderState(abstractHorseEntity, livingHorseEntityRenderState, f);
        ((LivingHorseEntityRenderState)livingHorseEntityRenderState).saddleStack = abstractHorseEntity.getEquippedStack(EquipmentSlot.SADDLE).copy();
        ((LivingHorseEntityRenderState)livingHorseEntityRenderState).armorStack = abstractHorseEntity.getBodyArmor().copy();
        ((LivingHorseEntityRenderState)livingHorseEntityRenderState).hasPassengers = abstractHorseEntity.hasPassengers();
        ((LivingHorseEntityRenderState)livingHorseEntityRenderState).eatingGrassAnimationProgress = abstractHorseEntity.getEatingGrassAnimationProgress(f);
        ((LivingHorseEntityRenderState)livingHorseEntityRenderState).angryAnimationProgress = abstractHorseEntity.getAngryAnimationProgress(f);
        ((LivingHorseEntityRenderState)livingHorseEntityRenderState).eatingAnimationProgress = abstractHorseEntity.getEatingAnimationProgress(f);
        ((LivingHorseEntityRenderState)livingHorseEntityRenderState).waggingTail = ((AbstractHorseEntity)abstractHorseEntity).tailWagTicks > 0;
    }
}

