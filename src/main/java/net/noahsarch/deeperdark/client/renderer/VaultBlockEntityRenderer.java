package net.noahsarch.deeperdark.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.noahsarch.deeperdark.block.VaultBlockEntity;

public class VaultBlockEntityRenderer implements BlockEntityRenderer<VaultBlockEntity, VaultBERState> {

    private final ItemModelResolver itemModelResolver;
    private final RandomSource random = RandomSource.create();

    public VaultBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public VaultBERState createRenderState() {
        return new VaultBERState();
    }

    @Override
    public void extractRenderState(VaultBlockEntity vault, VaultBERState state, float partialTick,
                                   Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay overlay) {
        BlockEntityRenderState.extractBase(vault, state, overlay);

        state.spin = (float) ((System.currentTimeMillis() % 10000L) / 10000.0 * 360.0);

        int entryCount = vault.getEntryCount();
        if (entryCount == 0 || vault.getLevel() == null) {
            state.displayItem = null;
            return;
        }

        int idx = (int) ((System.currentTimeMillis() / 3000L) % entryCount);
        ItemStack display = vault.getDisplayStack(idx);
        if (display.isEmpty()) {
            state.displayItem = null;
            return;
        }

        ItemClusterRenderState cluster = new ItemClusterRenderState();
        itemModelResolver.updateForTopItem(cluster.item, display, ItemDisplayContext.GROUND,
            vault.getLevel(), null, 0);
        cluster.count = 1;
        cluster.seed = ItemClusterRenderState.getSeedForItemStack(display);
        state.displayItem = cluster;
    }

    @Override
    public void submit(VaultBERState state, PoseStack poseStack, SubmitNodeCollector nodes,
                       CameraRenderState camera) {
        if (state.displayItem == null) return;

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.15f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.spin));
        poseStack.scale(2.0f, 2.0f, 2.0f);
        ItemEntityRenderer.renderMultipleFromCount(poseStack, nodes, state.lightCoords, state.displayItem, random);
        poseStack.popPose();
    }
}
