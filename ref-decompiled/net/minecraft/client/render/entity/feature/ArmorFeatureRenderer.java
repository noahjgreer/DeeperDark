/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.equipment.EquipmentModel$LayerType
 *  net.minecraft.client.render.entity.equipment.EquipmentRenderer
 *  net.minecraft.client.render.entity.feature.ArmorFeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 *  net.minecraft.client.render.entity.model.EquipmentModelData
 *  net.minecraft.client.render.entity.state.BipedEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.EquippableComponent
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.RegistryKey
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EquipmentModelData;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ArmorFeatureRenderer<S extends BipedEntityRenderState, M extends BipedEntityModel<S>, A extends BipedEntityModel<S>>
extends FeatureRenderer<S, M> {
    private final EquipmentModelData<A> adultModelData;
    private final EquipmentModelData<A> babyModelData;
    private final EquipmentRenderer equipmentRenderer;

    public ArmorFeatureRenderer(FeatureRendererContext<S, M> context, EquipmentModelData<A> modelData, EquipmentRenderer equipmentRenderer) {
        this(context, modelData, modelData, equipmentRenderer);
    }

    public ArmorFeatureRenderer(FeatureRendererContext<S, M> context, EquipmentModelData<A> adultModelData, EquipmentModelData<A> babyModelData, EquipmentRenderer equipmentRenderer) {
        super(context);
        this.adultModelData = adultModelData;
        this.babyModelData = babyModelData;
        this.equipmentRenderer = equipmentRenderer;
    }

    public static boolean hasModel(ItemStack stack, EquipmentSlot slot) {
        EquippableComponent equippableComponent = (EquippableComponent)stack.get(DataComponentTypes.EQUIPPABLE);
        return equippableComponent != null && ArmorFeatureRenderer.hasModel((EquippableComponent)equippableComponent, (EquipmentSlot)slot);
    }

    private static boolean hasModel(EquippableComponent component, EquipmentSlot slot) {
        return component.assetId().isPresent() && component.slot() == slot;
    }

    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, S bipedEntityRenderState, float f, float g) {
        this.renderArmor(matrixStack, orderedRenderCommandQueue, ((BipedEntityRenderState)bipedEntityRenderState).equippedChestStack, EquipmentSlot.CHEST, i, bipedEntityRenderState);
        this.renderArmor(matrixStack, orderedRenderCommandQueue, ((BipedEntityRenderState)bipedEntityRenderState).equippedLegsStack, EquipmentSlot.LEGS, i, bipedEntityRenderState);
        this.renderArmor(matrixStack, orderedRenderCommandQueue, ((BipedEntityRenderState)bipedEntityRenderState).equippedFeetStack, EquipmentSlot.FEET, i, bipedEntityRenderState);
        this.renderArmor(matrixStack, orderedRenderCommandQueue, ((BipedEntityRenderState)bipedEntityRenderState).equippedHeadStack, EquipmentSlot.HEAD, i, bipedEntityRenderState);
    }

    private void renderArmor(MatrixStack matrices, OrderedRenderCommandQueue queue, ItemStack stack, EquipmentSlot slot, int light, S state) {
        EquippableComponent equippableComponent = (EquippableComponent)stack.get(DataComponentTypes.EQUIPPABLE);
        if (equippableComponent == null || !ArmorFeatureRenderer.hasModel((EquippableComponent)equippableComponent, (EquipmentSlot)slot)) {
            return;
        }
        BipedEntityModel bipedEntityModel = this.getModel(state, slot);
        EquipmentModel.LayerType layerType = this.usesInnerModel(slot) ? EquipmentModel.LayerType.HUMANOID_LEGGINGS : EquipmentModel.LayerType.HUMANOID;
        this.equipmentRenderer.render(layerType, (RegistryKey)equippableComponent.assetId().orElseThrow(), (Model)bipedEntityModel, state, stack, matrices, queue, light, ((BipedEntityRenderState)state).outlineColor);
    }

    private A getModel(S state, EquipmentSlot slot) {
        return (A)((BipedEntityModel)(((BipedEntityRenderState)state).baby ? this.babyModelData : this.adultModelData).getModelData(slot));
    }

    private boolean usesInnerModel(EquipmentSlot slot) {
        return slot == EquipmentSlot.LEGS;
    }
}

