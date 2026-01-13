/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.BipedEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.PiglinEntityRenderer
 *  net.minecraft.client.render.entity.ZombifiedPiglinEntityRenderer
 *  net.minecraft.client.render.entity.feature.ArmorFeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayer
 *  net.minecraft.client.render.entity.model.EquipmentModelData
 *  net.minecraft.client.render.entity.model.LoadedEntityModels
 *  net.minecraft.client.render.entity.model.ZombifiedPiglinEntityModel
 *  net.minecraft.client.render.entity.state.BipedEntityRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.ZombifiedPiglinEntityRenderState
 *  net.minecraft.entity.mob.MobEntity
 *  net.minecraft.entity.mob.ZombifiedPiglinEntity
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PiglinEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EquipmentModelData;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.ZombifiedPiglinEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.ZombifiedPiglinEntityRenderState;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ZombifiedPiglinEntityRenderer
extends BipedEntityRenderer<ZombifiedPiglinEntity, ZombifiedPiglinEntityRenderState, ZombifiedPiglinEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/piglin/zombified_piglin.png");

    public ZombifiedPiglinEntityRenderer(EntityRendererFactory.Context context, EntityModelLayer mainLayer, EntityModelLayer babyMainLayer, EquipmentModelData<EntityModelLayer> adultModel, EquipmentModelData<EntityModelLayer> babyModel) {
        super(context, (BipedEntityModel)new ZombifiedPiglinEntityModel(context.getPart(mainLayer)), (BipedEntityModel)new ZombifiedPiglinEntityModel(context.getPart(babyMainLayer)), 0.5f, PiglinEntityRenderer.HEAD_TRANSFORMATION);
        this.addFeature((FeatureRenderer)new ArmorFeatureRenderer((FeatureRendererContext)this, EquipmentModelData.mapToEntityModel(adultModel, (LoadedEntityModels)context.getEntityModels(), ZombifiedPiglinEntityModel::new), EquipmentModelData.mapToEntityModel(babyModel, (LoadedEntityModels)context.getEntityModels(), ZombifiedPiglinEntityModel::new), context.getEquipmentRenderer()));
    }

    public Identifier getTexture(ZombifiedPiglinEntityRenderState zombifiedPiglinEntityRenderState) {
        return TEXTURE;
    }

    public ZombifiedPiglinEntityRenderState createRenderState() {
        return new ZombifiedPiglinEntityRenderState();
    }

    public void updateRenderState(ZombifiedPiglinEntity zombifiedPiglinEntity, ZombifiedPiglinEntityRenderState zombifiedPiglinEntityRenderState, float f) {
        super.updateRenderState((MobEntity)zombifiedPiglinEntity, (BipedEntityRenderState)zombifiedPiglinEntityRenderState, f);
        zombifiedPiglinEntityRenderState.attacking = zombifiedPiglinEntity.isAttacking();
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((ZombifiedPiglinEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

