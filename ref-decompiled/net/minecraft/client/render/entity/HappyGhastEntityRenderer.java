/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.AgeableMobEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.HappyGhastEntityRenderer
 *  net.minecraft.client.render.entity.equipment.EquipmentModel$LayerType
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.HappyGhastRopesFeatureRenderer
 *  net.minecraft.client.render.entity.feature.SaddleFeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.HappyGhastEntityModel
 *  net.minecraft.client.render.entity.model.HappyGhastHarnessEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.HappyGhastEntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.HappyGhastEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.Box
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HappyGhastRopesFeatureRenderer;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.HappyGhastEntityModel;
import net.minecraft.client.render.entity.model.HappyGhastHarnessEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.HappyGhastEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.HappyGhastEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;

@Environment(value=EnvType.CLIENT)
public class HappyGhastEntityRenderer
extends AgeableMobEntityRenderer<HappyGhastEntity, HappyGhastEntityRenderState, HappyGhastEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/ghast/happy_ghast.png");
    private static final Identifier BABY_TEXTURE = Identifier.ofVanilla((String)"textures/entity/ghast/happy_ghast_baby.png");
    private static final Identifier ROPES_TEXTURE = Identifier.ofVanilla((String)"textures/entity/ghast/happy_ghast_ropes.png");

    public HappyGhastEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new HappyGhastEntityModel(context.getPart(EntityModelLayers.HAPPY_GHAST)), (EntityModel)new HappyGhastEntityModel(context.getPart(EntityModelLayers.HAPPY_GHAST_BABY)), 2.0f);
        this.addFeature((FeatureRenderer)new SaddleFeatureRenderer((FeatureRendererContext)this, context.getEquipmentRenderer(), EquipmentModel.LayerType.HAPPY_GHAST_BODY, state -> state.harnessStack, (EntityModel)new HappyGhastHarnessEntityModel(context.getPart(EntityModelLayers.HAPPY_GHAST_HARNESS)), (EntityModel)new HappyGhastHarnessEntityModel(context.getPart(EntityModelLayers.HAPPY_GHAST_BABY_HARNESS))));
        this.addFeature((FeatureRenderer)new HappyGhastRopesFeatureRenderer((FeatureRendererContext)this, context.getEntityModels(), ROPES_TEXTURE));
    }

    public Identifier getTexture(HappyGhastEntityRenderState happyGhastEntityRenderState) {
        if (happyGhastEntityRenderState.baby) {
            return BABY_TEXTURE;
        }
        return TEXTURE;
    }

    public HappyGhastEntityRenderState createRenderState() {
        return new HappyGhastEntityRenderState();
    }

    protected Box getBoundingBox(HappyGhastEntity happyGhastEntity) {
        Box box = super.getBoundingBox((LivingEntity)happyGhastEntity);
        float f = happyGhastEntity.getHeight();
        return box.withMinY(box.minY - (double)(f / 2.0f));
    }

    public void updateRenderState(HappyGhastEntity happyGhastEntity, HappyGhastEntityRenderState happyGhastEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)happyGhastEntity, (LivingEntityRenderState)happyGhastEntityRenderState, f);
        happyGhastEntityRenderState.harnessStack = happyGhastEntity.getEquippedStack(EquipmentSlot.BODY).copy();
        happyGhastEntityRenderState.hasPassengers = happyGhastEntity.hasPassengers();
        happyGhastEntityRenderState.hasRopes = happyGhastEntity.hasRopes();
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((HappyGhastEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

