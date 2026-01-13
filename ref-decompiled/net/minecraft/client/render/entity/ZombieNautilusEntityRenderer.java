/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.ZombieNautilusEntityRenderer
 *  net.minecraft.client.render.entity.equipment.EquipmentModel$LayerType
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.SaddleFeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.NautilusArmorEntityModel
 *  net.minecraft.client.render.entity.model.NautilusEntityModel
 *  net.minecraft.client.render.entity.model.NautilusSaddleEntityModel
 *  net.minecraft.client.render.entity.model.ZombieNautilusCoralEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.NautilusEntityRenderState
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.texture.MissingSprite
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.mob.ZombieNautilusEntity
 *  net.minecraft.entity.mob.ZombieNautilusVariant
 *  net.minecraft.entity.mob.ZombieNautilusVariant$Model
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.NautilusArmorEntityModel;
import net.minecraft.client.render.entity.model.NautilusEntityModel;
import net.minecraft.client.render.entity.model.NautilusSaddleEntityModel;
import net.minecraft.client.render.entity.model.ZombieNautilusCoralEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.NautilusEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieNautilusEntity;
import net.minecraft.entity.mob.ZombieNautilusVariant;
import net.minecraft.util.Identifier;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ZombieNautilusEntityRenderer
extends MobEntityRenderer<ZombieNautilusEntity, NautilusEntityRenderState, NautilusEntityModel> {
    private final Map<ZombieNautilusVariant.Model, NautilusEntityModel> models;

    public ZombieNautilusEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new NautilusEntityModel(context.getPart(EntityModelLayers.ZOMBIE_NAUTILUS)), 0.7f);
        this.addFeature((FeatureRenderer)new SaddleFeatureRenderer((FeatureRendererContext)this, context.getEquipmentRenderer(), EquipmentModel.LayerType.NAUTILUS_BODY, state -> state.armorStack, (EntityModel)new NautilusArmorEntityModel(context.getPart(EntityModelLayers.NAUTILUS_ARMOR)), null));
        this.addFeature((FeatureRenderer)new SaddleFeatureRenderer((FeatureRendererContext)this, context.getEquipmentRenderer(), EquipmentModel.LayerType.NAUTILUS_SADDLE, state -> state.saddleStack, (EntityModel)new NautilusSaddleEntityModel(context.getPart(EntityModelLayers.NAUTILUS_SADDLE)), null));
        this.models = ZombieNautilusEntityRenderer.createModels((EntityRendererFactory.Context)context);
    }

    private static Map<ZombieNautilusVariant.Model, NautilusEntityModel> createModels(EntityRendererFactory.Context context) {
        return Maps.newEnumMap(Map.of(ZombieNautilusVariant.Model.NORMAL, new NautilusEntityModel(context.getPart(EntityModelLayers.ZOMBIE_NAUTILUS)), ZombieNautilusVariant.Model.WARM, new ZombieNautilusCoralEntityModel(context.getPart(EntityModelLayers.ZOMBIE_NAUTILUS_CORAL))));
    }

    public void render(NautilusEntityRenderState nautilusEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        if (nautilusEntityRenderState.variant == null) {
            return;
        }
        this.model = (EntityModel)this.models.get(nautilusEntityRenderState.variant.modelAndTexture().model());
        super.render((LivingEntityRenderState)nautilusEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    public Identifier getTexture(NautilusEntityRenderState nautilusEntityRenderState) {
        return nautilusEntityRenderState.variant == null ? MissingSprite.getMissingSpriteId() : nautilusEntityRenderState.variant.modelAndTexture().asset().texturePath();
    }

    public NautilusEntityRenderState createRenderState() {
        return new NautilusEntityRenderState();
    }

    public void updateRenderState(ZombieNautilusEntity zombieNautilusEntity, NautilusEntityRenderState nautilusEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)zombieNautilusEntity, (LivingEntityRenderState)nautilusEntityRenderState, f);
        nautilusEntityRenderState.saddleStack = zombieNautilusEntity.getEquippedStack(EquipmentSlot.SADDLE).copy();
        nautilusEntityRenderState.armorStack = zombieNautilusEntity.getBodyArmor().copy();
        nautilusEntityRenderState.variant = (ZombieNautilusVariant)zombieNautilusEntity.getVariant().value();
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((NautilusEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

