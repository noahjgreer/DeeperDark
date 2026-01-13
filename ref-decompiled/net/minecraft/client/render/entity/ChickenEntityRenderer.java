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
 *  net.minecraft.client.render.entity.ChickenEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.model.ChickenEntityModel
 *  net.minecraft.client.render.entity.model.ColdChickenEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.state.ChickenEntityRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.texture.MissingSprite
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.ChickenEntity
 *  net.minecraft.entity.passive.ChickenVariant
 *  net.minecraft.entity.passive.ChickenVariant$Model
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.MathHelper
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
import net.minecraft.client.render.entity.model.ChickenEntityModel;
import net.minecraft.client.render.entity.model.ColdChickenEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.ChickenEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.ChickenVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ChickenEntityRenderer
extends MobEntityRenderer<ChickenEntity, ChickenEntityRenderState, ChickenEntityModel> {
    private final Map<ChickenVariant.Model, BabyModelPair<ChickenEntityModel>> babyModelPairMap;

    public ChickenEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new ChickenEntityModel(context.getPart(EntityModelLayers.CHICKEN)), 0.3f);
        this.babyModelPairMap = ChickenEntityRenderer.createBabyModelPairMap((EntityRendererFactory.Context)context);
    }

    private static Map<ChickenVariant.Model, BabyModelPair<ChickenEntityModel>> createBabyModelPairMap(EntityRendererFactory.Context context) {
        return Maps.newEnumMap(Map.of(ChickenVariant.Model.NORMAL, new BabyModelPair((Model)new ChickenEntityModel(context.getPart(EntityModelLayers.CHICKEN)), (Model)new ChickenEntityModel(context.getPart(EntityModelLayers.CHICKEN_BABY))), ChickenVariant.Model.COLD, new BabyModelPair((Model)new ColdChickenEntityModel(context.getPart(EntityModelLayers.COLD_CHICKEN)), (Model)new ColdChickenEntityModel(context.getPart(EntityModelLayers.COLD_CHICKEN_BABY)))));
    }

    public void render(ChickenEntityRenderState chickenEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        if (chickenEntityRenderState.variant == null) {
            return;
        }
        this.model = (EntityModel)((BabyModelPair)this.babyModelPairMap.get(chickenEntityRenderState.variant.modelAndTexture().model())).get(chickenEntityRenderState.baby);
        super.render((LivingEntityRenderState)chickenEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    public Identifier getTexture(ChickenEntityRenderState chickenEntityRenderState) {
        return chickenEntityRenderState.variant == null ? MissingSprite.getMissingSpriteId() : chickenEntityRenderState.variant.modelAndTexture().asset().texturePath();
    }

    public ChickenEntityRenderState createRenderState() {
        return new ChickenEntityRenderState();
    }

    public void updateRenderState(ChickenEntity chickenEntity, ChickenEntityRenderState chickenEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)chickenEntity, (LivingEntityRenderState)chickenEntityRenderState, f);
        chickenEntityRenderState.flapProgress = MathHelper.lerp((float)f, (float)chickenEntity.lastFlapProgress, (float)chickenEntity.flapProgress);
        chickenEntityRenderState.maxWingDeviation = MathHelper.lerp((float)f, (float)chickenEntity.lastMaxWingDeviation, (float)chickenEntity.maxWingDeviation);
        chickenEntityRenderState.variant = (ChickenVariant)chickenEntity.getVariant().value();
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((ChickenEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

