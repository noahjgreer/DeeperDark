/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.ZombieBaseEntityRenderer
 *  net.minecraft.client.render.entity.ZombieEntityRenderer
 *  net.minecraft.client.render.entity.model.EntityModelLayer
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.EquipmentModelData
 *  net.minecraft.client.render.entity.model.LoadedEntityModels
 *  net.minecraft.client.render.entity.model.ZombieEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.ZombieEntityRenderState
 *  net.minecraft.entity.mob.ZombieEntity
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ZombieBaseEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EquipmentModelData;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import net.minecraft.entity.mob.ZombieEntity;

@Environment(value=EnvType.CLIENT)
public class ZombieEntityRenderer
extends ZombieBaseEntityRenderer<ZombieEntity, ZombieEntityRenderState, ZombieEntityModel<ZombieEntityRenderState>> {
    public ZombieEntityRenderer(EntityRendererFactory.Context context) {
        this(context, EntityModelLayers.ZOMBIE, EntityModelLayers.ZOMBIE_BABY, EntityModelLayers.ZOMBIE_EQUIPMENT, EntityModelLayers.ZOMBIE_BABY_EQUIPMENT);
    }

    public ZombieEntityRenderState createRenderState() {
        return new ZombieEntityRenderState();
    }

    public ZombieEntityRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer, EntityModelLayer legsArmorLayer, EquipmentModelData<EntityModelLayer> equipmentModelData, EquipmentModelData<EntityModelLayer> equipmentModelData2) {
        super(ctx, new ZombieEntityModel(ctx.getPart(layer)), new ZombieEntityModel(ctx.getPart(legsArmorLayer)), EquipmentModelData.mapToEntityModel(equipmentModelData, (LoadedEntityModels)ctx.getEntityModels(), ZombieEntityModel::new), EquipmentModelData.mapToEntityModel(equipmentModelData2, (LoadedEntityModels)ctx.getEntityModels(), ZombieEntityModel::new));
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

