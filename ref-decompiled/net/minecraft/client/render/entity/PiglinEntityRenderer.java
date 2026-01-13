/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.BipedEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.PiglinEntityRenderer
 *  net.minecraft.client.render.entity.feature.ArmorFeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.HeadFeatureRenderer$HeadTransformation
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayer
 *  net.minecraft.client.render.entity.model.EquipmentModelData
 *  net.minecraft.client.render.entity.model.LoadedEntityModels
 *  net.minecraft.client.render.entity.model.PiglinEntityModel
 *  net.minecraft.client.render.entity.state.BipedEntityRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.PiglinEntityRenderState
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.mob.AbstractPiglinEntity
 *  net.minecraft.entity.mob.MobEntity
 *  net.minecraft.item.CrossbowItem
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EquipmentModelData;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.PiglinEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PiglinEntityRenderState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class PiglinEntityRenderer
extends BipedEntityRenderer<AbstractPiglinEntity, PiglinEntityRenderState, PiglinEntityModel> {
    private static final Identifier PIGLIN_TEXTURE = Identifier.ofVanilla((String)"textures/entity/piglin/piglin.png");
    private static final Identifier PIGLIN_BRUTE_TEXTURE = Identifier.ofVanilla((String)"textures/entity/piglin/piglin_brute.png");
    public static final HeadFeatureRenderer.HeadTransformation HEAD_TRANSFORMATION = new HeadFeatureRenderer.HeadTransformation(0.0f, 0.0f, 1.0019531f);

    public PiglinEntityRenderer(EntityRendererFactory.Context ctx, EntityModelLayer mainLayer, EntityModelLayer babyMainLayer, EquipmentModelData<EntityModelLayer> adultModel, EquipmentModelData<EntityModelLayer> babyModel) {
        super(ctx, (BipedEntityModel)new PiglinEntityModel(ctx.getPart(mainLayer)), (BipedEntityModel)new PiglinEntityModel(ctx.getPart(babyMainLayer)), 0.5f, HEAD_TRANSFORMATION);
        this.addFeature((FeatureRenderer)new ArmorFeatureRenderer((FeatureRendererContext)this, EquipmentModelData.mapToEntityModel(adultModel, (LoadedEntityModels)ctx.getEntityModels(), PiglinEntityModel::new), EquipmentModelData.mapToEntityModel(babyModel, (LoadedEntityModels)ctx.getEntityModels(), PiglinEntityModel::new), ctx.getEquipmentRenderer()));
    }

    public Identifier getTexture(PiglinEntityRenderState piglinEntityRenderState) {
        return piglinEntityRenderState.brute ? PIGLIN_BRUTE_TEXTURE : PIGLIN_TEXTURE;
    }

    public PiglinEntityRenderState createRenderState() {
        return new PiglinEntityRenderState();
    }

    public void updateRenderState(AbstractPiglinEntity abstractPiglinEntity, PiglinEntityRenderState piglinEntityRenderState, float f) {
        super.updateRenderState((MobEntity)abstractPiglinEntity, (BipedEntityRenderState)piglinEntityRenderState, f);
        piglinEntityRenderState.brute = abstractPiglinEntity.getType() == EntityType.PIGLIN_BRUTE;
        piglinEntityRenderState.activity = abstractPiglinEntity.getActivity();
        piglinEntityRenderState.piglinCrossbowPullTime = CrossbowItem.getPullTime((ItemStack)abstractPiglinEntity.getActiveItem(), (LivingEntity)abstractPiglinEntity);
        piglinEntityRenderState.shouldZombify = abstractPiglinEntity.shouldZombify();
    }

    protected boolean isShaking(PiglinEntityRenderState piglinEntityRenderState) {
        return super.isShaking((LivingEntityRenderState)piglinEntityRenderState) || piglinEntityRenderState.shouldZombify;
    }

    protected /* synthetic */ boolean isShaking(LivingEntityRenderState state) {
        return this.isShaking((PiglinEntityRenderState)state);
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((PiglinEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

