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
 *  net.minecraft.client.render.entity.equipment.EquipmentModel$LayerType
 *  net.minecraft.client.render.entity.equipment.EquipmentRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.WolfArmorFeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.LoadedEntityModels
 *  net.minecraft.client.render.entity.model.WolfEntityModel
 *  net.minecraft.client.render.entity.state.WolfEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.EquippableComponent
 *  net.minecraft.entity.passive.Cracks
 *  net.minecraft.entity.passive.Cracks$CrackLevel
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity.feature;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.client.render.entity.state.WolfEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.passive.Cracks;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class WolfArmorFeatureRenderer
extends FeatureRenderer<WolfEntityRenderState, WolfEntityModel> {
    private final WolfEntityModel model;
    private final WolfEntityModel babyModel;
    private final EquipmentRenderer equipmentRenderer;
    private static final Map<Cracks.CrackLevel, Identifier> CRACK_TEXTURES = Map.of(Cracks.CrackLevel.LOW, Identifier.ofVanilla((String)"textures/entity/wolf/wolf_armor_crackiness_low.png"), Cracks.CrackLevel.MEDIUM, Identifier.ofVanilla((String)"textures/entity/wolf/wolf_armor_crackiness_medium.png"), Cracks.CrackLevel.HIGH, Identifier.ofVanilla((String)"textures/entity/wolf/wolf_armor_crackiness_high.png"));

    public WolfArmorFeatureRenderer(FeatureRendererContext<WolfEntityRenderState, WolfEntityModel> context, LoadedEntityModels loader, EquipmentRenderer equipmentRenderer) {
        super(context);
        this.model = new WolfEntityModel(loader.getModelPart(EntityModelLayers.WOLF_ARMOR));
        this.babyModel = new WolfEntityModel(loader.getModelPart(EntityModelLayers.WOLF_BABY_ARMOR));
        this.equipmentRenderer = equipmentRenderer;
    }

    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, WolfEntityRenderState wolfEntityRenderState, float f, float g) {
        ItemStack itemStack = wolfEntityRenderState.bodyArmor;
        EquippableComponent equippableComponent = (EquippableComponent)itemStack.get(DataComponentTypes.EQUIPPABLE);
        if (equippableComponent == null || equippableComponent.assetId().isEmpty()) {
            return;
        }
        WolfEntityModel wolfEntityModel = wolfEntityRenderState.baby ? this.babyModel : this.model;
        this.equipmentRenderer.render(EquipmentModel.LayerType.WOLF_BODY, (RegistryKey)equippableComponent.assetId().get(), (Model)wolfEntityModel, (Object)wolfEntityRenderState, itemStack, matrixStack, orderedRenderCommandQueue, i, wolfEntityRenderState.outlineColor);
        this.renderCracks(matrixStack, orderedRenderCommandQueue, i, itemStack, (Model)wolfEntityModel, wolfEntityRenderState);
    }

    private void renderCracks(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, ItemStack stack, Model<WolfEntityRenderState> model, WolfEntityRenderState state) {
        Cracks.CrackLevel crackLevel = Cracks.WOLF_ARMOR.getCrackLevel(stack);
        if (crackLevel == Cracks.CrackLevel.NONE) {
            return;
        }
        Identifier identifier = (Identifier)CRACK_TEXTURES.get(crackLevel);
        queue.submitModel(model, (Object)state, matrices, RenderLayers.armorTranslucent((Identifier)identifier), light, OverlayTexture.DEFAULT_UV, state.outlineColor, null);
    }
}

