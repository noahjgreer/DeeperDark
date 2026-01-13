/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.equipment.EquipmentModel
 *  net.minecraft.client.render.entity.equipment.EquipmentModel$LayerType
 *  net.minecraft.client.render.entity.equipment.EquipmentModelLoader
 *  net.minecraft.client.render.entity.feature.CapeFeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.LoadedEntityModels
 *  net.minecraft.client.render.entity.model.PlayerCapeModel
 *  net.minecraft.client.render.entity.model.PlayerEntityModel
 *  net.minecraft.client.render.entity.state.PlayerEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.EquippableComponent
 *  net.minecraft.entity.player.SkinTextures
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentModelLoader;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.PlayerCapeModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class CapeFeatureRenderer
extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
    private final BipedEntityModel<PlayerEntityRenderState> model;
    private final EquipmentModelLoader equipmentModelLoader;

    public CapeFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context, LoadedEntityModels modelLoader, EquipmentModelLoader equipmentModelLoader) {
        super(context);
        this.model = new PlayerCapeModel(modelLoader.getModelPart(EntityModelLayers.PLAYER_CAPE));
        this.equipmentModelLoader = equipmentModelLoader;
    }

    private boolean hasCustomModelForLayer(ItemStack stack, EquipmentModel.LayerType layerType) {
        EquippableComponent equippableComponent = (EquippableComponent)stack.get(DataComponentTypes.EQUIPPABLE);
        if (equippableComponent == null || equippableComponent.assetId().isEmpty()) {
            return false;
        }
        EquipmentModel equipmentModel = this.equipmentModelLoader.get((RegistryKey)equippableComponent.assetId().get());
        return !equipmentModel.getLayers(layerType).isEmpty();
    }

    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, PlayerEntityRenderState playerEntityRenderState, float f, float g) {
        if (playerEntityRenderState.invisible || !playerEntityRenderState.capeVisible) {
            return;
        }
        SkinTextures skinTextures = playerEntityRenderState.skinTextures;
        if (skinTextures.cape() == null) {
            return;
        }
        if (this.hasCustomModelForLayer(playerEntityRenderState.equippedChestStack, EquipmentModel.LayerType.WINGS)) {
            return;
        }
        matrixStack.push();
        if (this.hasCustomModelForLayer(playerEntityRenderState.equippedChestStack, EquipmentModel.LayerType.HUMANOID)) {
            matrixStack.translate(0.0f, -0.053125f, 0.06875f);
        }
        orderedRenderCommandQueue.submitModel((Model)this.model, (Object)playerEntityRenderState, matrixStack, RenderLayers.entitySolid((Identifier)skinTextures.cape().texturePath()), i, OverlayTexture.DEFAULT_UV, playerEntityRenderState.outlineColor, null);
        matrixStack.pop();
    }
}

