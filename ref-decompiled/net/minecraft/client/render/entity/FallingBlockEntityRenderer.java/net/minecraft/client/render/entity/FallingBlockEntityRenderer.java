/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.FallingBlockEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class FallingBlockEntityRenderer
extends EntityRenderer<FallingBlockEntity, FallingBlockEntityRenderState> {
    public FallingBlockEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.5f;
    }

    @Override
    public boolean shouldRender(FallingBlockEntity fallingBlockEntity, Frustum frustum, double d, double e, double f) {
        if (!super.shouldRender(fallingBlockEntity, frustum, d, e, f)) {
            return false;
        }
        return fallingBlockEntity.getBlockState() != fallingBlockEntity.getEntityWorld().getBlockState(fallingBlockEntity.getBlockPos());
    }

    @Override
    public void render(FallingBlockEntityRenderState fallingBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        BlockState blockState = fallingBlockEntityRenderState.movingBlockRenderState.blockState;
        if (blockState.getRenderType() != BlockRenderType.MODEL) {
            return;
        }
        matrixStack.push();
        matrixStack.translate(-0.5, 0.0, -0.5);
        orderedRenderCommandQueue.submitMovingBlock(matrixStack, fallingBlockEntityRenderState.movingBlockRenderState);
        matrixStack.pop();
        super.render(fallingBlockEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    @Override
    public FallingBlockEntityRenderState createRenderState() {
        return new FallingBlockEntityRenderState();
    }

    @Override
    public void updateRenderState(FallingBlockEntity fallingBlockEntity, FallingBlockEntityRenderState fallingBlockEntityRenderState, float f) {
        super.updateRenderState(fallingBlockEntity, fallingBlockEntityRenderState, f);
        BlockPos blockPos = BlockPos.ofFloored(fallingBlockEntity.getX(), fallingBlockEntity.getBoundingBox().maxY, fallingBlockEntity.getZ());
        fallingBlockEntityRenderState.movingBlockRenderState.fallingBlockPos = fallingBlockEntity.getFallingBlockPos();
        fallingBlockEntityRenderState.movingBlockRenderState.entityBlockPos = blockPos;
        fallingBlockEntityRenderState.movingBlockRenderState.blockState = fallingBlockEntity.getBlockState();
        fallingBlockEntityRenderState.movingBlockRenderState.biome = fallingBlockEntity.getEntityWorld().getBiome(blockPos);
        fallingBlockEntityRenderState.movingBlockRenderState.world = fallingBlockEntity.getEntityWorld();
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
