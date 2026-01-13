/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.BabyModelPair
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.PigEntityRenderer
 *  net.minecraft.client.render.entity.equipment.EquipmentModel$LayerType
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.SaddleFeatureRenderer
 *  net.minecraft.client.render.entity.model.ColdPigEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.PigEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.PigEntityRenderState
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.texture.MissingSprite
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.PigEntity
 *  net.minecraft.entity.passive.PigVariant
 *  net.minecraft.entity.passive.PigVariant$Model
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.BabyModelPair;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.ColdPigEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PigEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.PigVariant;
import net.minecraft.util.Identifier;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class PigEntityRenderer
extends MobEntityRenderer<PigEntity, PigEntityRenderState, PigEntityModel> {
    private final Map<PigVariant.Model, BabyModelPair<PigEntityModel>> modelPairs;

    public PigEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new PigEntityModel(context.getPart(EntityModelLayers.PIG)), 0.7f);
        this.modelPairs = PigEntityRenderer.createModelPairs((EntityRendererFactory.Context)context);
        this.addFeature((FeatureRenderer)new SaddleFeatureRenderer((FeatureRendererContext)this, context.getEquipmentRenderer(), EquipmentModel.LayerType.PIG_SADDLE, state -> state.saddleStack, (EntityModel)new PigEntityModel(context.getPart(EntityModelLayers.PIG_SADDLE)), (EntityModel)new PigEntityModel(context.getPart(EntityModelLayers.PIG_BABY_SADDLE))));
    }

    private static Map<PigVariant.Model, BabyModelPair<PigEntityModel>> createModelPairs(EntityRendererFactory.Context context) {
        return Maps.newEnumMap(Map.of(PigVariant.Model.NORMAL, new BabyModelPair((Model)new PigEntityModel(context.getPart(EntityModelLayers.PIG)), (Model)new PigEntityModel(context.getPart(EntityModelLayers.PIG_BABY))), PigVariant.Model.COLD, new BabyModelPair((Model)new ColdPigEntityModel(context.getPart(EntityModelLayers.COLD_PIG)), (Model)new ColdPigEntityModel(context.getPart(EntityModelLayers.COLD_PIG_BABY)))));
    }

    public void render(PigEntityRenderState pigEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        if (pigEntityRenderState.variant == null) {
            return;
        }
        this.model = (EntityModel)((BabyModelPair)this.modelPairs.get(pigEntityRenderState.variant.modelAndTexture().model())).get(pigEntityRenderState.baby);
        super.render((LivingEntityRenderState)pigEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    public Identifier getTexture(PigEntityRenderState pigEntityRenderState) {
        return pigEntityRenderState.variant == null ? MissingSprite.getMissingSpriteId() : pigEntityRenderState.variant.modelAndTexture().asset().texturePath();
    }

    public PigEntityRenderState createRenderState() {
        return new PigEntityRenderState();
    }

    public void updateRenderState(PigEntity pigEntity, PigEntityRenderState pigEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)pigEntity, (LivingEntityRenderState)pigEntityRenderState, f);
        pigEntityRenderState.saddleStack = pigEntity.getEquippedStack(EquipmentSlot.SADDLE).copy();
        pigEntityRenderState.variant = (PigVariant)pigEntity.getVariant().value();
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((PigEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

