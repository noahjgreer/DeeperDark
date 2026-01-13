/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
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
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
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
import net.minecraft.entity.mob.ZombieNautilusEntity;
import net.minecraft.entity.mob.ZombieNautilusVariant;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ZombieNautilusEntityRenderer
extends MobEntityRenderer<ZombieNautilusEntity, NautilusEntityRenderState, NautilusEntityModel> {
    private final Map<ZombieNautilusVariant.Model, NautilusEntityModel> models;

    public ZombieNautilusEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new NautilusEntityModel(context.getPart(EntityModelLayers.ZOMBIE_NAUTILUS)), 0.7f);
        this.addFeature(new SaddleFeatureRenderer<NautilusEntityRenderState, NautilusEntityModel, Object>(this, context.getEquipmentRenderer(), EquipmentModel.LayerType.NAUTILUS_BODY, state -> state.armorStack, new NautilusArmorEntityModel(context.getPart(EntityModelLayers.NAUTILUS_ARMOR)), null));
        this.addFeature(new SaddleFeatureRenderer<NautilusEntityRenderState, NautilusEntityModel, Object>(this, context.getEquipmentRenderer(), EquipmentModel.LayerType.NAUTILUS_SADDLE, state -> state.saddleStack, new NautilusSaddleEntityModel(context.getPart(EntityModelLayers.NAUTILUS_SADDLE)), null));
        this.models = ZombieNautilusEntityRenderer.createModels(context);
    }

    private static Map<ZombieNautilusVariant.Model, NautilusEntityModel> createModels(EntityRendererFactory.Context context) {
        return Maps.newEnumMap(Map.of(ZombieNautilusVariant.Model.NORMAL, new NautilusEntityModel(context.getPart(EntityModelLayers.ZOMBIE_NAUTILUS)), ZombieNautilusVariant.Model.WARM, new ZombieNautilusCoralEntityModel(context.getPart(EntityModelLayers.ZOMBIE_NAUTILUS_CORAL))));
    }

    @Override
    public void render(NautilusEntityRenderState nautilusEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        if (nautilusEntityRenderState.variant == null) {
            return;
        }
        this.model = this.models.get(nautilusEntityRenderState.variant.modelAndTexture().model());
        super.render(nautilusEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    @Override
    public Identifier getTexture(NautilusEntityRenderState nautilusEntityRenderState) {
        return nautilusEntityRenderState.variant == null ? MissingSprite.getMissingSpriteId() : nautilusEntityRenderState.variant.modelAndTexture().asset().texturePath();
    }

    @Override
    public NautilusEntityRenderState createRenderState() {
        return new NautilusEntityRenderState();
    }

    @Override
    public void updateRenderState(ZombieNautilusEntity zombieNautilusEntity, NautilusEntityRenderState nautilusEntityRenderState, float f) {
        super.updateRenderState(zombieNautilusEntity, nautilusEntityRenderState, f);
        nautilusEntityRenderState.saddleStack = zombieNautilusEntity.getEquippedStack(EquipmentSlot.SADDLE).copy();
        nautilusEntityRenderState.armorStack = zombieNautilusEntity.getBodyArmor().copy();
        nautilusEntityRenderState.variant = zombieNautilusEntity.getVariant().value();
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((NautilusEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
