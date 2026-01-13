/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BrushableBlockEntity
 *  net.minecraft.client.item.ItemModelManager
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.WorldRenderer
 *  net.minecraft.client.render.WorldRenderer$BrightnessGetter
 *  net.minecraft.client.render.block.entity.BlockEntityRenderer
 *  net.minecraft.client.render.block.entity.BlockEntityRendererFactory$Context
 *  net.minecraft.client.render.block.entity.BrushableBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.BrushableBlockEntityRenderer$1
 *  net.minecraft.client.render.block.entity.state.BlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.BrushableBlockEntityRenderState
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.BlockRenderView
 *  org.joml.Quaternionfc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BrushableBlockEntity;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.BrushableBlockEntityRenderer;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.block.entity.state.BrushableBlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockRenderView;
import org.joml.Quaternionfc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BrushableBlockEntityRenderer
implements BlockEntityRenderer<BrushableBlockEntity, BrushableBlockEntityRenderState> {
    private final ItemModelManager itemModelManager;

    public BrushableBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.itemModelManager = context.itemModelManager();
    }

    public BrushableBlockEntityRenderState createRenderState() {
        return new BrushableBlockEntityRenderState();
    }

    public void updateRenderState(BrushableBlockEntity brushableBlockEntity, BrushableBlockEntityRenderState brushableBlockEntityRenderState, float f, Vec3d vec3d, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        super.updateRenderState((BlockEntity)brushableBlockEntity, (BlockEntityRenderState)brushableBlockEntityRenderState, f, vec3d, crumblingOverlayCommand);
        brushableBlockEntityRenderState.face = brushableBlockEntity.getHitDirection();
        brushableBlockEntityRenderState.dusted = (Integer)brushableBlockEntity.getCachedState().get((Property)Properties.DUSTED);
        if (brushableBlockEntity.getWorld() != null && brushableBlockEntity.getHitDirection() != null) {
            brushableBlockEntityRenderState.lightmapCoordinates = WorldRenderer.getLightmapCoordinates((WorldRenderer.BrightnessGetter)WorldRenderer.BrightnessGetter.DEFAULT, (BlockRenderView)brushableBlockEntity.getWorld(), (BlockState)brushableBlockEntity.getCachedState(), (BlockPos)brushableBlockEntity.getPos().offset(brushableBlockEntity.getHitDirection()));
        }
        this.itemModelManager.clearAndUpdate(brushableBlockEntityRenderState.itemRenderState, brushableBlockEntity.getItem(), ItemDisplayContext.FIXED, brushableBlockEntity.getWorld(), null, 0);
    }

    public void render(BrushableBlockEntityRenderState brushableBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        if (brushableBlockEntityRenderState.dusted <= 0 || brushableBlockEntityRenderState.face == null || brushableBlockEntityRenderState.itemRenderState.isEmpty()) {
            return;
        }
        matrixStack.push();
        matrixStack.translate(0.0f, 0.5f, 0.0f);
        float[] fs = this.getTranslation(brushableBlockEntityRenderState.face, brushableBlockEntityRenderState.dusted);
        matrixStack.translate(fs[0], fs[1], fs[2]);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(75.0f));
        boolean bl = brushableBlockEntityRenderState.face == Direction.EAST || brushableBlockEntityRenderState.face == Direction.WEST;
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees((float)((bl ? 90 : 0) + 11)));
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        brushableBlockEntityRenderState.itemRenderState.render(matrixStack, orderedRenderCommandQueue, brushableBlockEntityRenderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV, 0);
        matrixStack.pop();
    }

    private float[] getTranslation(Direction direction, int dustedLevel) {
        float[] fs = new float[]{0.5f, 0.0f, 0.5f};
        float f = (float)dustedLevel / 10.0f * 0.75f;
        switch (1.field_42929[direction.ordinal()]) {
            case 1: {
                fs[0] = 0.73f + f;
                break;
            }
            case 2: {
                fs[0] = 0.25f - f;
                break;
            }
            case 3: {
                fs[1] = 0.25f + f;
                break;
            }
            case 4: {
                fs[1] = -0.23f - f;
                break;
            }
            case 5: {
                fs[2] = 0.25f - f;
                break;
            }
            case 6: {
                fs[2] = 0.73f + f;
            }
        }
        return fs;
    }

    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

