/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.AbstractHorseEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.HorseEntityRenderer
 *  net.minecraft.client.render.entity.equipment.EquipmentModel$LayerType
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.HorseMarkingFeatureRenderer
 *  net.minecraft.client.render.entity.feature.SaddleFeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.HorseEntityModel
 *  net.minecraft.client.render.entity.model.HorseSaddleEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.HorseEntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.LivingHorseEntityRenderState
 *  net.minecraft.entity.passive.AbstractHorseEntity
 *  net.minecraft.entity.passive.HorseColor
 *  net.minecraft.entity.passive.HorseEntity
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AbstractHorseEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HorseMarkingFeatureRenderer;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.render.entity.model.HorseSaddleEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.HorseEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.LivingHorseEntityRenderState;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.HorseColor;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public final class HorseEntityRenderer
extends AbstractHorseEntityRenderer<HorseEntity, HorseEntityRenderState, HorseEntityModel> {
    private static final Map<HorseColor, Identifier> TEXTURES = Maps.newEnumMap(Map.of(HorseColor.WHITE, Identifier.ofVanilla((String)"textures/entity/horse/horse_white.png"), HorseColor.CREAMY, Identifier.ofVanilla((String)"textures/entity/horse/horse_creamy.png"), HorseColor.CHESTNUT, Identifier.ofVanilla((String)"textures/entity/horse/horse_chestnut.png"), HorseColor.BROWN, Identifier.ofVanilla((String)"textures/entity/horse/horse_brown.png"), HorseColor.BLACK, Identifier.ofVanilla((String)"textures/entity/horse/horse_black.png"), HorseColor.GRAY, Identifier.ofVanilla((String)"textures/entity/horse/horse_gray.png"), HorseColor.DARK_BROWN, Identifier.ofVanilla((String)"textures/entity/horse/horse_darkbrown.png")));

    public HorseEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new HorseEntityModel(context.getPart(EntityModelLayers.HORSE)), (EntityModel)new HorseEntityModel(context.getPart(EntityModelLayers.HORSE_BABY)));
        this.addFeature((FeatureRenderer)new HorseMarkingFeatureRenderer((FeatureRendererContext)this));
        this.addFeature((FeatureRenderer)new SaddleFeatureRenderer((FeatureRendererContext)this, context.getEquipmentRenderer(), EquipmentModel.LayerType.HORSE_BODY, state -> state.armorStack, (EntityModel)new HorseEntityModel(context.getPart(EntityModelLayers.HORSE_ARMOR)), (EntityModel)new HorseEntityModel(context.getPart(EntityModelLayers.HORSE_ARMOR_BABY)), 2));
        this.addFeature((FeatureRenderer)new SaddleFeatureRenderer((FeatureRendererContext)this, context.getEquipmentRenderer(), EquipmentModel.LayerType.HORSE_SADDLE, state -> state.saddleStack, (EntityModel)new HorseSaddleEntityModel(context.getPart(EntityModelLayers.HORSE_SADDLE)), (EntityModel)new HorseSaddleEntityModel(context.getPart(EntityModelLayers.HORSE_BABY_SADDLE)), 2));
    }

    public Identifier getTexture(HorseEntityRenderState horseEntityRenderState) {
        return (Identifier)TEXTURES.get(horseEntityRenderState.color);
    }

    public HorseEntityRenderState createRenderState() {
        return new HorseEntityRenderState();
    }

    public void updateRenderState(HorseEntity horseEntity, HorseEntityRenderState horseEntityRenderState, float f) {
        super.updateRenderState((AbstractHorseEntity)horseEntity, (LivingHorseEntityRenderState)horseEntityRenderState, f);
        horseEntityRenderState.color = horseEntity.getHorseColor();
        horseEntityRenderState.marking = horseEntity.getMarking();
        horseEntityRenderState.armorStack = horseEntity.getBodyArmor().copy();
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((HorseEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

