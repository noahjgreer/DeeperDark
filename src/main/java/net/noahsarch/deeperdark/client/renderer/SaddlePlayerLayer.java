package net.noahsarch.deeperdark.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

@Environment(EnvType.CLIENT)
public class SaddlePlayerLayer<S extends HumanoidRenderState, M extends HumanoidModel<S>>
        extends RenderLayer<S, M> {

    // Equipment asset defined in assets/deeperdark/equipment/player_saddle.json
    // Texture expected at assets/deeperdark/textures/entity/equipment/humanoid/player_saddle.png
    public static final ResourceKey<EquipmentAsset> PLAYER_SADDLE_ASSET =
            ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath("deeperdark", "player_saddle"));

    private final EquipmentLayerRenderer equipmentRenderer;

    public SaddlePlayerLayer(RenderLayerParent<S, M> renderer, EquipmentLayerRenderer equipmentRenderer) {
        super(renderer);
        this.equipmentRenderer = equipmentRenderer;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, S state, float yRot, float xRot) {
        if (!state.headEquipment.is(Items.SADDLE)) return;

        poseStack.pushPose();
        poseStack.scale(1.05f, 1.05f, 1.05f);
        this.equipmentRenderer.renderLayers(
                EquipmentClientInfo.LayerType.HUMANOID,
                PLAYER_SADDLE_ASSET,
                this.getParentModel(),
                state,
                state.headEquipment,
                poseStack,
                submitNodeCollector,
                lightCoords,
                state.outlineColor
        );
        poseStack.popPose();
    }
}
