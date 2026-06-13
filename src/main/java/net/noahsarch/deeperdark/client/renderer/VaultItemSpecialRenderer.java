package net.noahsarch.deeperdark.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.AbstractEndPortalRenderer;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.noahsarch.deeperdark.block.VaultBlockEntity;
import net.noahsarch.deeperdark.component.ModComponents;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class VaultItemSpecialRenderer implements SpecialModelRenderer<ItemStack> {

    private final RandomSource random = RandomSource.create();

    @Override
    public @Nullable ItemStack extractArgument(ItemStack stack) {
        List<VaultBlockEntity.VaultEntry> entries = stack.get(ModComponents.VAULT_ENTRIES);
        if (entries != null && !entries.isEmpty()) {
            int idx = (int) ((System.currentTimeMillis() / 3000L) % entries.size());
            return entries.get(idx).representative.copyWithCount(1);
        }
        return null;
    }

    @Override
    public void submit(
            @Nullable ItemStack displayItem,
            PoseStack poseStack,
            SubmitNodeCollector nodes,
            int lightCoords,
            int overlayCoords,
            boolean hasFoil,
            int outlineColor) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.scale(-0.98f, 0.98f, 0.98f);
        poseStack.translate(-0.5, -0.5, -0.5);
        AbstractEndPortalRenderer.submitSpecial(RenderTypes.endGateway(), poseStack, nodes);
        poseStack.popPose();

        if (displayItem == null || displayItem.isEmpty()) return;

        ItemClusterRenderState cluster = new ItemClusterRenderState();
        Minecraft.getInstance().getItemModelResolver()
            .updateForTopItem(cluster.item, displayItem, ItemDisplayContext.GROUND, null, null, 0);
        cluster.count = 1;
        cluster.seed = ItemClusterRenderState.getSeedForItemStack(displayItem);

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.15f, 0.5f);
        float spin = (float) ((System.currentTimeMillis() % 10000L) / 10000.0 * 360.0);
        poseStack.mulPose(Axis.YP.rotationDegrees(spin));
        poseStack.scale(2.0f, 2.0f, 2.0f);
        ItemEntityRenderer.renderMultipleFromCount(poseStack, nodes, lightCoords, cluster, random);
        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        AbstractEndPortalRenderer.getExtents(output);
    }

    @Environment(EnvType.CLIENT)
    public static class Unbaked implements SpecialModelRenderer.Unbaked<ItemStack> {
        public static final Unbaked INSTANCE = new Unbaked();
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(INSTANCE);

        @Override
        public SpecialModelRenderer<ItemStack> bake(SpecialModelRenderer.BakingContext context) {
            return new VaultItemSpecialRenderer();
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked<ItemStack>> type() {
            return MAP_CODEC;
        }
    }
}
