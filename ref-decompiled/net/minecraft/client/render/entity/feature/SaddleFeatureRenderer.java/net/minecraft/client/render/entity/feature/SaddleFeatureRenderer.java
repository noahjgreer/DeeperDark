/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.entity.feature;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.util.Identifier;
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

    @Override
    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, S livingEntityRenderState, float f, float g) {
        ItemStack itemStack = this.saddleStackGetter.apply(livingEntityRenderState);
        EquippableComponent equippableComponent = itemStack.get(DataComponentTypes.EQUIPPABLE);
        if (equippableComponent == null || equippableComponent.assetId().isEmpty() || ((LivingEntityRenderState)livingEntityRenderState).baby && this.babyModel == null) {
            return;
        }
        EM entityModel = ((LivingEntityRenderState)livingEntityRenderState).baby ? this.babyModel : this.adultModel;
        this.equipmentRenderer.render(this.layerType, equippableComponent.assetId().get(), entityModel, livingEntityRenderState, itemStack, matrixStack, orderedRenderCommandQueue, i, (Identifier)null, ((LivingEntityRenderState)livingEntityRenderState).outlineColor, this.initialQueueOrder);
    }
}
