/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.HashCommon
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import it.unimi.dsi.fastutil.HashCommon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.ShelfBlock;
import net.minecraft.block.entity.ShelfBlockEntity;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.block.entity.state.ShelfBlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionfc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ShelfBlockEntityRenderer
implements BlockEntityRenderer<ShelfBlockEntity, ShelfBlockEntityRenderState> {
    private static final float ITEM_SCALE = 0.25f;
    private static final float BOTTOM_ALIGNED_OFFSET = -0.25f;
    private final ItemModelManager itemModelManager;

    public ShelfBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.itemModelManager = context.itemModelManager();
    }

    @Override
    public ShelfBlockEntityRenderState createRenderState() {
        return new ShelfBlockEntityRenderState();
    }

    @Override
    public void updateRenderState(ShelfBlockEntity shelfBlockEntity, ShelfBlockEntityRenderState shelfBlockEntityRenderState, float f, Vec3d vec3d,  @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        BlockEntityRenderer.super.updateRenderState(shelfBlockEntity, shelfBlockEntityRenderState, f, vec3d, crumblingOverlayCommand);
        shelfBlockEntityRenderState.alignItemsToBottom = shelfBlockEntity.shouldAlignItemsToBottom();
        DefaultedList<ItemStack> defaultedList = shelfBlockEntity.getHeldStacks();
        int i = HashCommon.long2int((long)shelfBlockEntity.getPos().asLong());
        for (int j = 0; j < defaultedList.size(); ++j) {
            ItemStack itemStack = defaultedList.get(j);
            if (itemStack.isEmpty()) continue;
            ItemRenderState itemRenderState = new ItemRenderState();
            this.itemModelManager.clearAndUpdate(itemRenderState, itemStack, ItemDisplayContext.ON_SHELF, shelfBlockEntity.getEntityWorld(), shelfBlockEntity, i + j);
            shelfBlockEntityRenderState.itemRenderStates[j] = itemRenderState;
        }
    }

    @Override
    public void render(ShelfBlockEntityRenderState shelfBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        Direction direction = shelfBlockEntityRenderState.blockState.get(ShelfBlock.FACING);
        float f = direction.getAxis().isHorizontal() ? -direction.getPositiveHorizontalDegrees() : 180.0f;
        for (int i = 0; i < shelfBlockEntityRenderState.itemRenderStates.length; ++i) {
            ItemRenderState itemRenderState = shelfBlockEntityRenderState.itemRenderStates[i];
            if (itemRenderState == null) continue;
            this.renderItem(shelfBlockEntityRenderState, itemRenderState, matrixStack, orderedRenderCommandQueue, i, f);
        }
    }

    private void renderItem(ShelfBlockEntityRenderState state, ItemRenderState itemRenderState, MatrixStack matrices, OrderedRenderCommandQueue queue, int overlay, float rotationDegrees) {
        float f = (float)(overlay - 1) * 0.3125f;
        Vec3d vec3d = new Vec3d(f, state.alignItemsToBottom ? -0.25 : 0.0, -0.25);
        matrices.push();
        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(rotationDegrees));
        matrices.translate(vec3d);
        matrices.scale(0.25f, 0.25f, 0.25f);
        Box box = itemRenderState.getModelBoundingBox();
        double d = -box.minY;
        if (!state.alignItemsToBottom) {
            d += -(box.maxY - box.minY) / 2.0;
        }
        matrices.translate(0.0, d, 0.0);
        itemRenderState.render(matrices, queue, state.lightmapCoordinates, OverlayTexture.DEFAULT_UV, 0);
        matrices.pop();
    }

    @Override
    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
