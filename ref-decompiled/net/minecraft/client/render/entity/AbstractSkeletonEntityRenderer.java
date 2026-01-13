/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.AbstractSkeletonEntityRenderer
 *  net.minecraft.client.render.entity.BipedEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.feature.ArmorFeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.model.BipedEntityModel$ArmPose
 *  net.minecraft.client.render.entity.model.EntityModelLayer
 *  net.minecraft.client.render.entity.model.EquipmentModelData
 *  net.minecraft.client.render.entity.model.LoadedEntityModels
 *  net.minecraft.client.render.entity.model.SkeletonEntityModel
 *  net.minecraft.client.render.entity.state.SkeletonEntityRenderState
 *  net.minecraft.entity.mob.AbstractSkeletonEntity
 *  net.minecraft.item.Items
 *  net.minecraft.util.Arm
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EquipmentModelData;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.client.render.entity.state.SkeletonEntityRenderState;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractSkeletonEntityRenderer<T extends AbstractSkeletonEntity, S extends SkeletonEntityRenderState>
extends BipedEntityRenderer<T, S, SkeletonEntityModel<S>> {
    public AbstractSkeletonEntityRenderer(EntityRendererFactory.Context context, EntityModelLayer layer, EquipmentModelData<EntityModelLayer> model) {
        this(context, model, new SkeletonEntityModel(context.getPart(layer)));
    }

    public AbstractSkeletonEntityRenderer(EntityRendererFactory.Context context, EquipmentModelData<EntityModelLayer> equipmentModelData, SkeletonEntityModel<S> model) {
        super(context, model, 0.5f);
        this.addFeature((FeatureRenderer)new ArmorFeatureRenderer((FeatureRendererContext)this, EquipmentModelData.mapToEntityModel(equipmentModelData, (LoadedEntityModels)context.getEntityModels(), SkeletonEntityModel::new), context.getEquipmentRenderer()));
    }

    public void updateRenderState(T abstractSkeletonEntity, S skeletonEntityRenderState, float f) {
        super.updateRenderState(abstractSkeletonEntity, skeletonEntityRenderState, f);
        ((SkeletonEntityRenderState)skeletonEntityRenderState).attacking = abstractSkeletonEntity.isAttacking();
        ((SkeletonEntityRenderState)skeletonEntityRenderState).shaking = abstractSkeletonEntity.isShaking();
        ((SkeletonEntityRenderState)skeletonEntityRenderState).holdingBow = abstractSkeletonEntity.getMainHandStack().isOf(Items.BOW);
    }

    protected boolean isShaking(S skeletonEntityRenderState) {
        return ((SkeletonEntityRenderState)skeletonEntityRenderState).shaking;
    }

    protected BipedEntityModel.ArmPose getArmPose(T abstractSkeletonEntity, Arm arm) {
        if (abstractSkeletonEntity.getMainArm() == arm && abstractSkeletonEntity.isAttacking() && abstractSkeletonEntity.getMainHandStack().isOf(Items.BOW)) {
            return BipedEntityModel.ArmPose.BOW_AND_ARROW;
        }
        return super.getArmPose(abstractSkeletonEntity, arm);
    }
}

