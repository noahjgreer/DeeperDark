/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.CampfireBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.CampfireBlockEntity
 *  net.minecraft.client.item.ItemModelManager
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.block.entity.BlockEntityRenderer
 *  net.minecraft.client.render.block.entity.BlockEntityRendererFactory$Context
 *  net.minecraft.client.render.block.entity.CampfireBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.state.BlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.CampfireBlockEntityRenderState
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.item.ItemRenderState
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.util.math.Vec3d
 *  org.joml.Quaternionfc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.block.entity.state.CampfireBlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionfc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CampfireBlockEntityRenderer
implements BlockEntityRenderer<CampfireBlockEntity, CampfireBlockEntityRenderState> {
    private static final float SCALE = 0.375f;
    private final ItemModelManager itemModelManager;

    public CampfireBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.itemModelManager = ctx.itemModelManager();
    }

    public CampfireBlockEntityRenderState createRenderState() {
        return new CampfireBlockEntityRenderState();
    }

    public void updateRenderState(CampfireBlockEntity campfireBlockEntity, CampfireBlockEntityRenderState campfireBlockEntityRenderState, float f, Vec3d vec3d, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        super.updateRenderState((BlockEntity)campfireBlockEntity, (BlockEntityRenderState)campfireBlockEntityRenderState, f, vec3d, crumblingOverlayCommand);
        campfireBlockEntityRenderState.facing = (Direction)campfireBlockEntity.getCachedState().get((Property)CampfireBlock.FACING);
        int i = (int)campfireBlockEntity.getPos().asLong();
        campfireBlockEntityRenderState.cookedItemStates = new ArrayList();
        for (int j = 0; j < campfireBlockEntity.getItemsBeingCooked().size(); ++j) {
            ItemRenderState itemRenderState = new ItemRenderState();
            this.itemModelManager.clearAndUpdate(itemRenderState, (ItemStack)campfireBlockEntity.getItemsBeingCooked().get(j), ItemDisplayContext.FIXED, campfireBlockEntity.getWorld(), null, i + j);
            campfireBlockEntityRenderState.cookedItemStates.add(itemRenderState);
        }
    }

    public void render(CampfireBlockEntityRenderState campfireBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        Direction direction = campfireBlockEntityRenderState.facing;
        List list = campfireBlockEntityRenderState.cookedItemStates;
        for (int i = 0; i < list.size(); ++i) {
            ItemRenderState itemRenderState = (ItemRenderState)list.get(i);
            if (itemRenderState.isEmpty()) continue;
            matrixStack.push();
            matrixStack.translate(0.5f, 0.44921875f, 0.5f);
            Direction direction2 = Direction.fromHorizontalQuarterTurns((int)((i + direction.getHorizontalQuarterTurns()) % 4));
            float f = -direction2.getPositiveHorizontalDegrees();
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(f));
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
            matrixStack.translate(-0.3125f, -0.3125f, 0.0f);
            matrixStack.scale(0.375f, 0.375f, 0.375f);
            itemRenderState.render(matrixStack, orderedRenderCommandQueue, campfireBlockEntityRenderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV, 0);
            matrixStack.pop();
        }
    }

    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

