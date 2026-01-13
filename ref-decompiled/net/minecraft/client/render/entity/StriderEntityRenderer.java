/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.AgeableMobEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.StriderEntityRenderer
 *  net.minecraft.client.render.entity.equipment.EquipmentModel$LayerType
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.SaddleFeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.StriderEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.StriderEntityRenderState
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.StriderEntity
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.StriderEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.StriderEntityRenderState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class StriderEntityRenderer
extends AgeableMobEntityRenderer<StriderEntity, StriderEntityRenderState, StriderEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/strider/strider.png");
    private static final Identifier COLD_TEXTURE = Identifier.ofVanilla((String)"textures/entity/strider/strider_cold.png");
    private static final float BABY_SHADOW_RADIUS_SCALE = 0.5f;

    public StriderEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new StriderEntityModel(context.getPart(EntityModelLayers.STRIDER)), (EntityModel)new StriderEntityModel(context.getPart(EntityModelLayers.STRIDER_BABY)), 0.5f);
        this.addFeature((FeatureRenderer)new SaddleFeatureRenderer((FeatureRendererContext)this, context.getEquipmentRenderer(), EquipmentModel.LayerType.STRIDER_SADDLE, state -> state.saddleStack, (EntityModel)new StriderEntityModel(context.getPart(EntityModelLayers.STRIDER_SADDLE)), (EntityModel)new StriderEntityModel(context.getPart(EntityModelLayers.STRIDER_BABY_SADDLE))));
    }

    public Identifier getTexture(StriderEntityRenderState striderEntityRenderState) {
        return striderEntityRenderState.cold ? COLD_TEXTURE : TEXTURE;
    }

    protected float getShadowRadius(StriderEntityRenderState striderEntityRenderState) {
        float f = super.getShadowRadius((LivingEntityRenderState)striderEntityRenderState);
        if (striderEntityRenderState.baby) {
            return f * 0.5f;
        }
        return f;
    }

    public StriderEntityRenderState createRenderState() {
        return new StriderEntityRenderState();
    }

    public void updateRenderState(StriderEntity striderEntity, StriderEntityRenderState striderEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)striderEntity, (LivingEntityRenderState)striderEntityRenderState, f);
        striderEntityRenderState.saddleStack = striderEntity.getEquippedStack(EquipmentSlot.SADDLE).copy();
        striderEntityRenderState.cold = striderEntity.isCold();
        striderEntityRenderState.hasPassengers = striderEntity.hasPassengers();
    }

    protected boolean isShaking(StriderEntityRenderState striderEntityRenderState) {
        return super.isShaking((LivingEntityRenderState)striderEntityRenderState) || striderEntityRenderState.cold;
    }

    protected /* synthetic */ float getShadowRadius(LivingEntityRenderState livingEntityRenderState) {
        return this.getShadowRadius((StriderEntityRenderState)livingEntityRenderState);
    }

    protected /* synthetic */ boolean isShaking(LivingEntityRenderState state) {
        return this.isShaking((StriderEntityRenderState)state);
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((StriderEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    protected /* synthetic */ float getShadowRadius(EntityRenderState state) {
        return this.getShadowRadius((StriderEntityRenderState)state);
    }
}

