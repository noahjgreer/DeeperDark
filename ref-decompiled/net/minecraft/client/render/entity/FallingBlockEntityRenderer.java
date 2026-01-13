/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.BlockRenderType
 *  net.minecraft.block.BlockState
 *  net.minecraft.client.render.Frustum
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.EntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.FallingBlockEntityRenderer
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.FallingBlockEntityRenderState
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.FallingBlockEntity
 *  net.minecraft.util.math.BlockPos
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
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class FallingBlockEntityRenderer
extends EntityRenderer<FallingBlockEntity, FallingBlockEntityRenderState> {
    public FallingBlockEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.5f;
    }

    public boolean shouldRender(FallingBlockEntity fallingBlockEntity, Frustum frustum, double d, double e, double f) {
        if (!super.shouldRender((Entity)fallingBlockEntity, frustum, d, e, f)) {
            return false;
        }
        return fallingBlockEntity.getBlockState() != fallingBlockEntity.getEntityWorld().getBlockState(fallingBlockEntity.getBlockPos());
    }

    public void render(FallingBlockEntityRenderState fallingBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        BlockState blockState = fallingBlockEntityRenderState.movingBlockRenderState.blockState;
        if (blockState.getRenderType() != BlockRenderType.MODEL) {
            return;
        }
        matrixStack.push();
        matrixStack.translate(-0.5, 0.0, -0.5);
        orderedRenderCommandQueue.submitMovingBlock(matrixStack, fallingBlockEntityRenderState.movingBlockRenderState);
        matrixStack.pop();
        super.render((EntityRenderState)fallingBlockEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    public FallingBlockEntityRenderState createRenderState() {
        return new FallingBlockEntityRenderState();
    }

    public void updateRenderState(FallingBlockEntity fallingBlockEntity, FallingBlockEntityRenderState fallingBlockEntityRenderState, float f) {
        super.updateRenderState((Entity)fallingBlockEntity, (EntityRenderState)fallingBlockEntityRenderState, f);
        BlockPos blockPos = BlockPos.ofFloored((double)fallingBlockEntity.getX(), (double)fallingBlockEntity.getBoundingBox().maxY, (double)fallingBlockEntity.getZ());
        fallingBlockEntityRenderState.movingBlockRenderState.fallingBlockPos = fallingBlockEntity.getFallingBlockPos();
        fallingBlockEntityRenderState.movingBlockRenderState.entityBlockPos = blockPos;
        fallingBlockEntityRenderState.movingBlockRenderState.blockState = fallingBlockEntity.getBlockState();
        fallingBlockEntityRenderState.movingBlockRenderState.biome = fallingBlockEntity.getEntityWorld().getBiome(blockPos);
        fallingBlockEntityRenderState.movingBlockRenderState.world = fallingBlockEntity.getEntityWorld();
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

