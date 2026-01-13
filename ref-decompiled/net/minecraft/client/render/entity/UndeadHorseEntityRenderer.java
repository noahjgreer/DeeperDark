/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.AbstractHorseEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.UndeadHorseEntityRenderer
 *  net.minecraft.client.render.entity.UndeadHorseEntityRenderer$Type
 *  net.minecraft.client.render.entity.equipment.EquipmentModel$LayerType
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.SaddleFeatureRenderer
 *  net.minecraft.client.render.entity.model.AbstractHorseEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.HorseEntityModel
 *  net.minecraft.client.render.entity.model.HorseSaddleEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.LivingHorseEntityRenderState
 *  net.minecraft.entity.passive.AbstractHorseEntity
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AbstractHorseEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.UndeadHorseEntityRenderer;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.AbstractHorseEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.render.entity.model.HorseSaddleEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.LivingHorseEntityRenderState;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class UndeadHorseEntityRenderer
extends AbstractHorseEntityRenderer<AbstractHorseEntity, LivingHorseEntityRenderState, AbstractHorseEntityModel<LivingHorseEntityRenderState>> {
    private final Identifier texture;

    public UndeadHorseEntityRenderer(EntityRendererFactory.Context ctx, Type type) {
        super(ctx, (EntityModel)new HorseEntityModel(ctx.getPart(type.modelLayer)), (EntityModel)new HorseEntityModel(ctx.getPart(type.babyModelLayer)));
        this.texture = type.texture;
        this.addFeature((FeatureRenderer)new SaddleFeatureRenderer((FeatureRendererContext)this, ctx.getEquipmentRenderer(), EquipmentModel.LayerType.HORSE_BODY, state -> state.armorStack, (EntityModel)new HorseEntityModel(ctx.getPart(EntityModelLayers.UNDEAD_HORSE_ARMOR)), (EntityModel)new HorseEntityModel(ctx.getPart(EntityModelLayers.UNDEAD_HORSE_BABY_ARMOR))));
        this.addFeature((FeatureRenderer)new SaddleFeatureRenderer((FeatureRendererContext)this, ctx.getEquipmentRenderer(), type.saddleLayerType, state -> state.saddleStack, (EntityModel)new HorseSaddleEntityModel(ctx.getPart(type.saddleModelLayer)), (EntityModel)new HorseSaddleEntityModel(ctx.getPart(type.babySaddleModelLayer))));
    }

    public Identifier getTexture(LivingHorseEntityRenderState livingHorseEntityRenderState) {
        return this.texture;
    }

    public LivingHorseEntityRenderState createRenderState() {
        return new LivingHorseEntityRenderState();
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((LivingHorseEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

