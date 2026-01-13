/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.BipedEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.VillagerEntityRenderer
 *  net.minecraft.client.render.entity.ZombieVillagerEntityRenderer
 *  net.minecraft.client.render.entity.feature.ArmorFeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.EquipmentModelData
 *  net.minecraft.client.render.entity.model.LoadedEntityModels
 *  net.minecraft.client.render.entity.model.ZombieVillagerEntityModel
 *  net.minecraft.client.render.entity.state.BipedEntityRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.ZombieVillagerRenderState
 *  net.minecraft.entity.mob.MobEntity
 *  net.minecraft.entity.mob.ZombieVillagerEntity
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.VillagerEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EquipmentModelData;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.ZombieVillagerEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.ZombieVillagerRenderState;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ZombieVillagerEntityRenderer
extends BipedEntityRenderer<ZombieVillagerEntity, ZombieVillagerRenderState, ZombieVillagerEntityModel<ZombieVillagerRenderState>> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/zombie_villager/zombie_villager.png");

    public ZombieVillagerEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (BipedEntityModel)new ZombieVillagerEntityModel(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER)), (BipedEntityModel)new ZombieVillagerEntityModel(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER_BABY)), 0.5f, VillagerEntityRenderer.HEAD_TRANSFORMATION);
        this.addFeature((FeatureRenderer)new ArmorFeatureRenderer((FeatureRendererContext)this, EquipmentModelData.mapToEntityModel((EquipmentModelData)EntityModelLayers.ZOMBIE_VILLAGER_EQUIPMENT, (LoadedEntityModels)context.getEntityModels(), ZombieVillagerEntityModel::new), EquipmentModelData.mapToEntityModel((EquipmentModelData)EntityModelLayers.ZOMBIE_VILLAGER_BABY_EQUIPMENT, (LoadedEntityModels)context.getEntityModels(), ZombieVillagerEntityModel::new), context.getEquipmentRenderer()));
        this.addFeature((FeatureRenderer)new VillagerClothingFeatureRenderer((FeatureRendererContext)this, context.getResourceManager(), "zombie_villager", (EntityModel)new ZombieVillagerEntityModel(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER_NO_HAT)), (EntityModel)new ZombieVillagerEntityModel(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER_BABY_NO_HAT))));
    }

    public Identifier getTexture(ZombieVillagerRenderState zombieVillagerRenderState) {
        return TEXTURE;
    }

    public ZombieVillagerRenderState createRenderState() {
        return new ZombieVillagerRenderState();
    }

    public void updateRenderState(ZombieVillagerEntity zombieVillagerEntity, ZombieVillagerRenderState zombieVillagerRenderState, float f) {
        super.updateRenderState((MobEntity)zombieVillagerEntity, (BipedEntityRenderState)zombieVillagerRenderState, f);
        zombieVillagerRenderState.convertingInWater = zombieVillagerEntity.isConverting();
        zombieVillagerRenderState.villagerData = zombieVillagerEntity.getVillagerData();
        zombieVillagerRenderState.attacking = zombieVillagerEntity.isAttacking();
    }

    protected boolean isShaking(ZombieVillagerRenderState zombieVillagerRenderState) {
        return super.isShaking((LivingEntityRenderState)zombieVillagerRenderState) || zombieVillagerRenderState.convertingInWater;
    }

    protected /* synthetic */ boolean isShaking(LivingEntityRenderState state) {
        return this.isShaking((ZombieVillagerRenderState)state);
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((ZombieVillagerRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

