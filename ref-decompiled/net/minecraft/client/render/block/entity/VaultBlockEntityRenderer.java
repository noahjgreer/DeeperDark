/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.VaultBlockEntity
 *  net.minecraft.block.entity.VaultBlockEntity$Client
 *  net.minecraft.block.vault.VaultClientData
 *  net.minecraft.block.vault.VaultSharedData
 *  net.minecraft.client.item.ItemModelManager
 *  net.minecraft.client.render.block.entity.BlockEntityRenderer
 *  net.minecraft.client.render.block.entity.BlockEntityRendererFactory$Context
 *  net.minecraft.client.render.block.entity.VaultBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.state.BlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.VaultBlockEntityRenderState
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.ItemEntityRenderer
 *  net.minecraft.client.render.entity.state.ItemStackEntityRenderState
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.random.Random
 *  org.joml.Quaternionfc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.block.vault.VaultClientData;
import net.minecraft.block.vault.VaultSharedData;
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

    public VaultBlockEntityRenderState createRenderState() {
        return new VaultBlockEntityRenderState();
    }

    public void updateRenderState(VaultBlockEntity vaultBlockEntity, VaultBlockEntityRenderState vaultBlockEntityRenderState, float f, Vec3d vec3d, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        super.updateRenderState((BlockEntity)vaultBlockEntity, (BlockEntityRenderState)vaultBlockEntityRenderState, f, vec3d, crumblingOverlayCommand);
        ItemStack itemStack = vaultBlockEntity.getSharedData().getDisplayItem();
        if (!VaultBlockEntity.Client.hasDisplayItem((VaultSharedData)vaultBlockEntity.getSharedData()) || itemStack.isEmpty() || vaultBlockEntity.getWorld() == null) {
            return;
        }
        vaultBlockEntityRenderState.displayItemStackState = new ItemStackEntityRenderState();
        this.itemModelManager.clearAndUpdate(vaultBlockEntityRenderState.displayItemStackState.itemRenderState, itemStack, ItemDisplayContext.GROUND, vaultBlockEntity.getWorld(), null, 0);
        vaultBlockEntityRenderState.displayItemStackState.renderedAmount = ItemStackEntityRenderState.getRenderedAmount((int)itemStack.getCount());
        vaultBlockEntityRenderState.displayItemStackState.seed = ItemStackEntityRenderState.getSeed((ItemStack)itemStack);
        VaultClientData vaultClientData = vaultBlockEntity.getClientData();
        vaultBlockEntityRenderState.displayRotationDegrees = MathHelper.lerpAngleDegrees((float)f, (float)vaultClientData.getLastDisplayRotation(), (float)vaultClientData.getDisplayRotation());
    }

    public void render(VaultBlockEntityRenderState vaultBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        if (vaultBlockEntityRenderState.displayItemStackState == null) {
            return;
        }
        matrixStack.push();
        matrixStack.translate(0.5f, 0.4f, 0.5f);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(vaultBlockEntityRenderState.displayRotationDegrees));
        ItemEntityRenderer.renderStack((MatrixStack)matrixStack, (OrderedRenderCommandQueue)orderedRenderCommandQueue, (int)vaultBlockEntityRenderState.lightmapCoordinates, (ItemStackEntityRenderState)vaultBlockEntityRenderState.displayItemStackState, (Random)this.random);
        matrixStack.pop();
    }

    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

