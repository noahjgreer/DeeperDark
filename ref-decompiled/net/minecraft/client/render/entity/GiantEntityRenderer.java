/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.item.ItemModelManager
 *  net.minecraft.client.render.entity.BipedEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.GiantEntityRenderer
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.feature.ArmorFeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.EquipmentModelData
 *  net.minecraft.client.render.entity.model.GiantEntityModel
 *  net.minecraft.client.render.entity.model.LoadedEntityModels
 *  net.minecraft.client.render.entity.state.BipedEntityRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.ZombieEntityRenderState
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.mob.GiantEntity
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EquipmentModelData;
import net.minecraft.client.render.entity.model.GiantEntityModel;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class GiantEntityRenderer
extends MobEntityRenderer<GiantEntity, ZombieEntityRenderState, BipedEntityModel<ZombieEntityRenderState>> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/zombie/zombie.png");

    public GiantEntityRenderer(EntityRendererFactory.Context ctx, float scale) {
        super(ctx, (EntityModel)new GiantEntityModel(ctx.getPart(EntityModelLayers.GIANT)), 0.5f * scale);
        this.addFeature((FeatureRenderer)new HeldItemFeatureRenderer((FeatureRendererContext)this));
        this.addFeature((FeatureRenderer)new ArmorFeatureRenderer((FeatureRendererContext)this, EquipmentModelData.mapToEntityModel((EquipmentModelData)EntityModelLayers.GIANT_EQUIPMENT, (LoadedEntityModels)ctx.getEntityModels(), GiantEntityModel::new), ctx.getEquipmentRenderer()));
    }

    public Identifier getTexture(ZombieEntityRenderState zombieEntityRenderState) {
        return TEXTURE;
    }

    public ZombieEntityRenderState createRenderState() {
        return new ZombieEntityRenderState();
    }

    public void updateRenderState(GiantEntity giantEntity, ZombieEntityRenderState zombieEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)giantEntity, (LivingEntityRenderState)zombieEntityRenderState, f);
        BipedEntityRenderer.updateBipedRenderState((LivingEntity)giantEntity, (BipedEntityRenderState)zombieEntityRenderState, (float)f, (ItemModelManager)this.itemModelResolver);
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((ZombieEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

