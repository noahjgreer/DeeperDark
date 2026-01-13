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
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.SaddleFeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.EquippableComponent
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.RegistryKey
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.entity.feature;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SaddleFeatureRenderer<S extends LivingEntityRenderState, RM extends EntityModel<? super S>, EM extends EntityModel<? super S>>
extends FeatureRenderer<S, RM> {
    private final EquipmentRenderer equipmentRenderer;
    private final EquipmentModel.LayerType layerType;
    private final Function<S, ItemStack> saddleStackGetter;
    private final EM adultModel;
    private final @Nullable EM babyModel;
    private final int initialQueueOrder;

    public SaddleFeatureRenderer(FeatureRendererContext<S, RM> context, EquipmentRenderer equipmentRenderer, EquipmentModel.LayerType layerType, Function<S, ItemStack> saddleStackGetter, EM adultModel, @Nullable EM babyModel, int initialQueueOrder) {
        super(context);
        this.equipmentRenderer = equipmentRenderer;
        this.layerType = layerType;
        this.saddleStackGetter = saddleStackGetter;
        this.adultModel = adultModel;
        this.babyModel = babyModel;
        this.initialQueueOrder = initialQueueOrder;
    }

    public SaddleFeatureRenderer(FeatureRendererContext<S, RM> context, EquipmentRenderer equipmentRenderer, EquipmentModel.LayerType layerType, Function<S, ItemStack> saddleStackGetter, EM adultModel, @Nullable EM babyModel) {
        this(context, equipmentRenderer, layerType, saddleStackGetter, adultModel, babyModel, 0);
    }

    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, S livingEntityRenderState, float f, float g) {
        ItemStack itemStack = (ItemStack)this.saddleStackGetter.apply(livingEntityRenderState);
        EquippableComponent equippableComponent = (EquippableComponent)itemStack.get(DataComponentTypes.EQUIPPABLE);
        if (equippableComponent == null || equippableComponent.assetId().isEmpty() || ((LivingEntityRenderState)livingEntityRenderState).baby && this.babyModel == null) {
            return;
        }
        EntityModel entityModel = ((LivingEntityRenderState)livingEntityRenderState).baby ? this.babyModel : this.adultModel;
        this.equipmentRenderer.render(this.layerType, (RegistryKey)equippableComponent.assetId().get(), (Model)entityModel, livingEntityRenderState, itemStack, matrixStack, orderedRenderCommandQueue, i, null, ((LivingEntityRenderState)livingEntityRenderState).outlineColor, this.initialQueueOrder);
    }
}

