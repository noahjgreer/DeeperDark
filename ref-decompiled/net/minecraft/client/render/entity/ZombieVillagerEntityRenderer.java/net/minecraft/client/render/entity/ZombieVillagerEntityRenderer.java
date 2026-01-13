/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.VillagerEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EquipmentModelData;
import net.minecraft.client.render.entity.model.ZombieVillagerEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.ZombieVillagerRenderState;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ZombieVillagerEntityRenderer
extends BipedEntityRenderer<ZombieVillagerEntity, ZombieVillagerRenderState, ZombieVillagerEntityModel<ZombieVillagerRenderState>> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/zombie_villager/zombie_villager.png");

    public ZombieVillagerEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new ZombieVillagerEntityModel(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER)), new ZombieVillagerEntityModel(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER_BABY)), 0.5f, VillagerEntityRenderer.HEAD_TRANSFORMATION);
        this.addFeature(new ArmorFeatureRenderer<ZombieVillagerRenderState, ZombieVillagerEntityModel<ZombieVillagerRenderState>, ZombieVillagerEntityModel>(this, EquipmentModelData.mapToEntityModel(EntityModelLayers.ZOMBIE_VILLAGER_EQUIPMENT, context.getEntityModels(), ZombieVillagerEntityModel::new), EquipmentModelData.mapToEntityModel(EntityModelLayers.ZOMBIE_VILLAGER_BABY_EQUIPMENT, context.getEntityModels(), ZombieVillagerEntityModel::new), context.getEquipmentRenderer()));
        this.addFeature(new VillagerClothingFeatureRenderer(this, context.getResourceManager(), "zombie_villager", new ZombieVillagerEntityModel(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER_NO_HAT)), new ZombieVillagerEntityModel(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER_BABY_NO_HAT))));
    }

    @Override
    public Identifier getTexture(ZombieVillagerRenderState zombieVillagerRenderState) {
        return TEXTURE;
    }

    @Override
    public ZombieVillagerRenderState createRenderState() {
        return new ZombieVillagerRenderState();
    }

    @Override
    public void updateRenderState(ZombieVillagerEntity zombieVillagerEntity, ZombieVillagerRenderState zombieVillagerRenderState, float f) {
        super.updateRenderState(zombieVillagerEntity, zombieVillagerRenderState, f);
        zombieVillagerRenderState.convertingInWater = zombieVillagerEntity.isConverting();
        zombieVillagerRenderState.villagerData = zombieVillagerEntity.getVillagerData();
        zombieVillagerRenderState.attacking = zombieVillagerEntity.isAttacking();
    }

    @Override
    protected boolean isShaking(ZombieVillagerRenderState zombieVillagerRenderState) {
        return super.isShaking(zombieVillagerRenderState) || zombieVillagerRenderState.convertingInWater;
    }

    @Override
    protected /* synthetic */ boolean isShaking(LivingEntityRenderState state) {
        return this.isShaking((ZombieVillagerRenderState)state);
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((ZombieVillagerRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
