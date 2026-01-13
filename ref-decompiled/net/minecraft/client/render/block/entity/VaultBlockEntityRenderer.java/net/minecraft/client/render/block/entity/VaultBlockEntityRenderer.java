/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.block.vault.VaultClientData;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.block.entity.state.VaultBlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.entity.state.ItemStackEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.joml.Quaternionfc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class VaultBlockEntityRenderer
implements BlockEntityRenderer<VaultBlockEntity, VaultBlockEntityRenderState> {
    private final ItemModelManager itemModelManager;
    private final Random random = Random.create();

    public VaultBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.itemModelManager = context.itemModelManager();
    }

    @Override
    public VaultBlockEntityRenderState createRenderState() {
        return new VaultBlockEntityRenderState();
    }

    @Override
    public void updateRenderState(VaultBlockEntity vaultBlockEntity, VaultBlockEntityRenderState vaultBlockEntityRenderState, float f, Vec3d vec3d,  @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        BlockEntityRenderer.super.updateRenderState(vaultBlockEntity, vaultBlockEntityRenderState, f, vec3d, crumblingOverlayCommand);
        ItemStack itemStack = vaultBlockEntity.getSharedData().getDisplayItem();
        if (!VaultBlockEntity.Client.hasDisplayItem(vaultBlockEntity.getSharedData()) || itemStack.isEmpty() || vaultBlockEntity.getWorld() == null) {
            return;
        }
        vaultBlockEntityRenderState.displayItemStackState = new ItemStackEntityRenderState();
        this.itemModelManager.clearAndUpdate(vaultBlockEntityRenderState.displayItemStackState.itemRenderState, itemStack, ItemDisplayContext.GROUND, vaultBlockEntity.getWorld(), null, 0);
        vaultBlockEntityRenderState.displayItemStackState.renderedAmount = ItemStackEntityRenderState.getRenderedAmount(itemStack.getCount());
        vaultBlockEntityRenderState.displayItemStackState.seed = ItemStackEntityRenderState.getSeed(itemStack);
        VaultClientData vaultClientData = vaultBlockEntity.getClientData();
        vaultBlockEntityRenderState.displayRotationDegrees = MathHelper.lerpAngleDegrees(f, vaultClientData.getLastDisplayRotation(), vaultClientData.getDisplayRotation());
    }

    @Override
    public void render(VaultBlockEntityRenderState vaultBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        if (vaultBlockEntityRenderState.displayItemStackState == null) {
            return;
        }
        matrixStack.push();
        matrixStack.translate(0.5f, 0.4f, 0.5f);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(vaultBlockEntityRenderState.displayRotationDegrees));
        ItemEntityRenderer.renderStack(matrixStack, orderedRenderCommandQueue, vaultBlockEntityRenderState.lightmapCoordinates, vaultBlockEntityRenderState.displayItemStackState, this.random);
        matrixStack.pop();
    }

    @Override
    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
