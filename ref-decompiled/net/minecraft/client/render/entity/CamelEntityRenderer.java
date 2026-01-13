/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.AgeableMobEntityRenderer
 *  net.minecraft.client.render.entity.CamelEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.equipment.EquipmentModel$LayerType
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.SaddleFeatureRenderer
 *  net.minecraft.client.render.entity.model.CamelEntityModel
 *  net.minecraft.client.render.entity.model.CamelSaddleEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.state.CamelEntityRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.CamelEntity
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
import net.minecraft.client.render.entity.model.CamelEntityModel;
import net.minecraft.client.render.entity.model.CamelSaddleEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.CamelEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CamelEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class CamelEntityRenderer
extends AgeableMobEntityRenderer<CamelEntity, CamelEntityRenderState, CamelEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/camel/camel.png");

    public CamelEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new CamelEntityModel(context.getPart(EntityModelLayers.CAMEL)), (EntityModel)new CamelEntityModel(context.getPart(EntityModelLayers.CAMEL_BABY)), 0.7f);
        this.addFeature((FeatureRenderer)this.createSaddleFeatureRenderer(context));
    }

    protected SaddleFeatureRenderer<CamelEntityRenderState, CamelEntityModel, CamelSaddleEntityModel> createSaddleFeatureRenderer(EntityRendererFactory.Context context) {
        return new SaddleFeatureRenderer((FeatureRendererContext)this, context.getEquipmentRenderer(), EquipmentModel.LayerType.CAMEL_SADDLE, state -> state.saddleStack, (EntityModel)new CamelSaddleEntityModel(context.getPart(EntityModelLayers.CAMEL_SADDLE)), (EntityModel)new CamelSaddleEntityModel(context.getPart(EntityModelLayers.CAMEL_BABY_SADDLE)));
    }

    public Identifier getTexture(CamelEntityRenderState camelEntityRenderState) {
        return TEXTURE;
    }

    public CamelEntityRenderState createRenderState() {
        return new CamelEntityRenderState();
    }

    public void updateRenderState(CamelEntity camelEntity, CamelEntityRenderState camelEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)camelEntity, (LivingEntityRenderState)camelEntityRenderState, f);
        camelEntityRenderState.saddleStack = camelEntity.getEquippedStack(EquipmentSlot.SADDLE).copy();
        camelEntityRenderState.hasPassengers = camelEntity.hasPassengers();
        camelEntityRenderState.jumpCooldown = Math.max((float)camelEntity.getJumpCooldown() - f, 0.0f);
        camelEntityRenderState.sittingTransitionAnimationState.copyFrom(camelEntity.sittingTransitionAnimationState);
        camelEntityRenderState.sittingAnimationState.copyFrom(camelEntity.sittingAnimationState);
        camelEntityRenderState.standingTransitionAnimationState.copyFrom(camelEntity.standingTransitionAnimationState);
        camelEntityRenderState.idlingAnimationState.copyFrom(camelEntity.idlingAnimationState);
        camelEntityRenderState.dashingAnimationState.copyFrom(camelEntity.dashingAnimationState);
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((CamelEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

