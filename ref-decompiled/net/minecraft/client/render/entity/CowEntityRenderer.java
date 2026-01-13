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
 *  net.minecraft.client.render.entity.CowEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.model.CowEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.state.CowEntityRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.texture.MissingSprite
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.CowEntity
 *  net.minecraft.entity.passive.CowVariant
 *  net.minecraft.entity.passive.CowVariant$Model
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
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.CowEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.CowVariant;
import net.minecraft.util.Identifier;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class CowEntityRenderer
extends MobEntityRenderer<CowEntity, CowEntityRenderState, CowEntityModel> {
    private final Map<CowVariant.Model, BabyModelPair<CowEntityModel>> babyModelPairMap;

    public CowEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new CowEntityModel(context.getPart(EntityModelLayers.COW)), 0.7f);
        this.babyModelPairMap = CowEntityRenderer.createBabyModelPairMap((EntityRendererFactory.Context)context);
    }

    private static Map<CowVariant.Model, BabyModelPair<CowEntityModel>> createBabyModelPairMap(EntityRendererFactory.Context context) {
        return Maps.newEnumMap(Map.of(CowVariant.Model.NORMAL, new BabyModelPair((Model)new CowEntityModel(context.getPart(EntityModelLayers.COW)), (Model)new CowEntityModel(context.getPart(EntityModelLayers.COW_BABY))), CowVariant.Model.WARM, new BabyModelPair((Model)new CowEntityModel(context.getPart(EntityModelLayers.WARM_COW)), (Model)new CowEntityModel(context.getPart(EntityModelLayers.WARM_COW_BABY))), CowVariant.Model.COLD, new BabyModelPair((Model)new CowEntityModel(context.getPart(EntityModelLayers.COLD_COW)), (Model)new CowEntityModel(context.getPart(EntityModelLayers.COLD_COW_BABY)))));
    }

    public Identifier getTexture(CowEntityRenderState cowEntityRenderState) {
        return cowEntityRenderState.variant == null ? MissingSprite.getMissingSpriteId() : cowEntityRenderState.variant.modelAndTexture().asset().texturePath();
    }

    public CowEntityRenderState createRenderState() {
        return new CowEntityRenderState();
    }

    public void updateRenderState(CowEntity cowEntity, CowEntityRenderState cowEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)cowEntity, (LivingEntityRenderState)cowEntityRenderState, f);
        cowEntityRenderState.variant = (CowVariant)cowEntity.getVariant().value();
    }

    public void render(CowEntityRenderState cowEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        if (cowEntityRenderState.variant == null) {
            return;
        }
        this.model = (EntityModel)((BabyModelPair)this.babyModelPairMap.get(cowEntityRenderState.variant.modelAndTexture().model())).get(cowEntityRenderState.baby);
        super.render((LivingEntityRenderState)cowEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((CowEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

